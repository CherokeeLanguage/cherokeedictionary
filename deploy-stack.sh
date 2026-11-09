#!/bin/bash

# Deploy the cherokeedictionary application to the serving stack's Docker host
# (vhost-58.cherokeedictionary.net) — cherokeedictionary#125's deliverable.
#
# Ships the build context as the COMMITTED tree state (git archive HEAD — no
# untracked or ignored material rides along: .env, poc/, build outputs stay
# local), then rebuilds the dict-app image and cuts it over ZERO-DOWNTIME
# via blue-green:
#
#   1. docker compose build — safe while the old container serves: the
#      image tag moves, the running container keeps its pinned image ID
#   2. boot a dict-app-next slot container mirroring the compose service
#      config (same env file, networks, memory cap, log rotation)
#   3. gate the slot on its boot marker + a direct HTTP probe
#   4. atomically flip the caddy catch-all upstream to the slot (graceful
#      reload — in-flight requests and listeners survive)
#   5. recreate the canonical dict-app container through compose — users
#      are on the slot during the restart window
#   6. gate the canonical container, flip caddy back to it
#   7. run the curl-tier regression gate, then drain and remove the slot
#
# At every instant at least one boot-verified upstream sits behind caddy.
# If anything fails after the first flip, the verified slot keeps serving
# and the script exits FAILED without disturbing traffic further.
#
# The compose service itself (tree, env delivery, routing) is devops#14's
# deliverable, staged by the devops repository's
# docs/runbooks/dict-app-vhost-58-stage.sh — this script's variable block is
# the single place the host path, service name, and edge-proxy coordinates
# live. The slot container mirrors services.dict-app in the host compose
# file — keep the two blocks in sync.
#
# Gate note: the gate targets the APEX (the live edge). dev.* still rides
# the old pre-cutover host behind cloudflare — gating against it would
# exercise the wrong build.

set -e
set -o pipefail

cd "$(dirname "$(readlink -f "$0")")"

trap "echo ERROR" ERR

STACK_HOST="mconrad@vhost-58.cherokeedictionary.net"
STACK_DIR="/srv/cherokeedictionary"
SERVICE="dict-app"

# edge proxy (devops#14): caddy sits on the proxy network; its Caddyfile is
# bind-mounted from this host path; the catch-all upstream line is what the
# blue-green flip rewrites (dict-app <-> dict-app-next)
CADDY_CONTAINER="caddy"
CADDY_CONF="/srv/caddy/caddy/Caddyfile"

# the slot mirrors services.dict-app in $STACK_DIR/compose.yaml
# (env delivery is the operator-held env file by path) — keep in sync
SLOT_NAME="dict-app-next"
SLOT_IMAGE="dict-app:stage"
SLOT_ENV_FILE="/srv/mariadb/secrets/dict-app.env"
SLOT_JAVA_OPTS="-Xms2048m -Xmx2048m -XX:MaxMetaspaceSize=1024m"
SLOT_MEM="3500m"

# the regression gate target (cherokeedictionary#133 R-4): the edge the
# delivered site serves on — the apex (see note above)
GATE_BASE_URL="${GATE_BASE_URL:-https://cherokeedictionary.net}"
GATE_BOOT_TIMEOUT="${GATE_BOOT_TIMEOUT:-180}"
GATE_POLL="${GATE_POLL:-5}"

HEAD=$(git rev-parse HEAD)
DIRTY=$(git status --porcelain | wc -l)
echo "deploying committed tree ${HEAD:0:12} (${DIRTY} uncommitted changes present — recorded, not enforced)"

# ship the committed build context
git archive HEAD | ssh "$STACK_HOST" "mkdir -p $STACK_DIR/build && tar -x -C $STACK_DIR/build"

# ---------------------------------------------------------------- helpers

# wait_boot CONTAINER — poll the container log for the boot marker (Grails:
# Tomcat started on the port / Grails application running) with a timeout —
# not a fixed sleep. One SHORT ssh per poll: a single long-lived connection
# held open for the whole wait has proven flaky (connection resets). ssh
# failures (rc >= 2, e.g. reset) are "unknown, keep waiting" — only a
# successful poll returning no marker counts toward nothing, and only a
# successful poll finding the marker succeeds. Returns nonzero on timeout.
wait_boot() { # $1 = container name (docker logs target)
  local CNT="$1"
  local SINCE=$(date +%s)
  while :; do
    sleep "$GATE_POLL"
    local NOW=$(date +%s)
    if [ $((NOW - SINCE)) -ge "$GATE_BOOT_TIMEOUT" ]; then
      echo "REGRESSION: no boot marker within ${GATE_BOOT_TIMEOUT}s (the deployment is not silently committed)"
      return 1
    fi
    local RC
    RC=$(ssh "$STACK_HOST" "docker logs '$CNT' 2>&1 | grep -m1 -qE 'Tomcat.*started|Grails application running|Started .*Application'; echo \$?") || RC=255
    if [ "$RC" = 0 ]; then
      echo "boot marker seen"
      return 0
    fi
  done
}

# probe UPSTREAM — direct GET / against an upstream name resolvable from the
# caddy container (both colors ride the proxy network)
probe() { # $1 = upstream container name
  ssh "$STACK_HOST" "TGT='$1' CC='$CADDY_CONTAINER' bash -s" <<'REMOTE'
docker exec "$CC" wget -q -T 15 -O /dev/null "http://${TGT}:8080/"
REMOTE
}

# flip UPSTREAM — rewrite the catch-all upstream and reload caddy
# gracefully (validate first; in-flight connections are preserved)
flip() { # $1 = target upstream container name
  echo "caddy upstream -> $1"
  ssh "$STACK_HOST" "TGT='$1' CONF='$CADDY_CONF' CC='$CADDY_CONTAINER' bash -s" <<'REMOTE'
set -e
cp -f "$CONF" "$CONF.bak-deploy"
sed -i "s|reverse_proxy dict-app[a-z-]*:8080|reverse_proxy ${TGT}:8080|" "$CONF"
grep -q "reverse_proxy ${TGT}:8080" "$CONF"   # assert the swap took
docker exec "$CC" caddy validate --config /etc/caddy/Caddyfile >/dev/null
docker exec "$CC" caddy reload --config /etc/caddy/Caddyfile
REMOTE
}

slot_cleanup() {
  ssh "$STACK_HOST" "docker rm -f $SLOT_NAME >/dev/null 2>&1 || true"
}

# ------------------------------------------------------------------- flow

# (re)build the image — detached on the host and polled with short
# connections; a single ssh held open for the whole build has proven
# flaky (connection resets mid-build). ssh rc discipline: 0 = alive,
# 1 = process gone, >= 2 (reset/unknown) = NEVER treated as done. The
# loop only accepts completion after two consecutive clean "gone" polls,
# and success = the stage image's Created (host clock) is fresher than
# the deploy start (host clock — skew-free). The tag moves only on
# success and the serving container is untouched throughout.
echo "building image on the host (detached, polled)"
BUILD_SINCE=$(ssh "$STACK_HOST" "date +%s") || exit 1
LOCAL_SINCE=$(date +%s)
# offset between the local and host clocks, for the local timeout check only
CLOCK_OFFSET=$((LOCAL_SINCE - BUILD_SINCE))
BUILD_TIMEOUT="${BUILD_TIMEOUT:-2700}"
ssh "$STACK_HOST" "cd $STACK_DIR && nohup docker compose build $SERVICE > /tmp/dict-app-build.log 2>&1 < /dev/null & echo started" || exit 1
DEAD_STREAK=0
while :; do
  sleep 20
  if [ $(($(date +%s) - CLOCK_OFFSET - BUILD_SINCE)) -ge "$BUILD_TIMEOUT" ]; then
    echo "REGRESSION: image build exceeded ${BUILD_TIMEOUT}s — aborting (canonical untouched)"
    exit 1
  fi
  RC=$(ssh "$STACK_HOST" "pgrep -f 'docker [c]ompose build $SERVICE' >/dev/null; echo \$?") || RC=255
  if [ "$RC" = 0 ]; then
    DEAD_STREAK=0
  elif [ "$RC" = 1 ]; then
    DEAD_STREAK=$((DEAD_STREAK + 1))
  else
    DEAD_STREAK=0
  fi
  if [ "$DEAD_STREAK" -ge 2 ]; then
    if ssh "$STACK_HOST" "CREATED=\$(docker inspect -f '{{.Created}}' $SLOT_IMAGE 2>/dev/null); [ -n \"\$CREATED\" ] && [ \$(date -d \"\$CREATED\" +%s) -ge $BUILD_SINCE ]"; then
      echo "build-ok"
      break
    fi
    # one retry before declaring failure — the inspect itself could race
    # a reset; a genuinely failed build stays failed on re-check
    sleep 10
    if ssh "$STACK_HOST" "CREATED=\$(docker inspect -f '{{.Created}}' $SLOT_IMAGE 2>/dev/null); [ -n \"\$CREATED\" ] && [ \$(date -d \"\$CREATED\" +%s) -ge $BUILD_SINCE ]"; then
      echo "build-ok"
      break
    fi
    echo "REGRESSION: build failed:"
    ssh "$STACK_HOST" "tail -20 /tmp/dict-app-build.log" || true
    exit 1
  fi
done

BLUEGREEN=0
if ssh "$STACK_HOST" "docker inspect $CADDY_CONTAINER >/dev/null 2>&1 && test -f $CADDY_CONF"; then
  BLUEGREEN=1

  # boot the slot from the fresh image, mirroring the compose service config
  echo "blue-green: booting slot $SLOT_NAME"
  slot_cleanup
  ssh "$STACK_HOST" "NAME='$SLOT_NAME' IMG='$SLOT_IMAGE' ENVF='$SLOT_ENV_FILE' JV='$SLOT_JAVA_OPTS' MEM='$SLOT_MEM' bash -s" <<'REMOTE'
set -e
docker run -d --name "$NAME" \
  --network backend \
  --env-file "$ENVF" \
  -e SERVER_PORT=8080 \
  -e SERVER_USE_FORWARD_HEADERS=true \
  -e SPRING_SESSION_STORE_TYPE=jdbc \
  -e "JAVA_TOOL_OPTIONS=$JV" \
  --memory "$MEM" --memory-swap "$MEM" \
  --log-driver json-file --log-opt max-size=10m --log-opt max-file=3 \
  "$IMG" >/dev/null
docker network connect proxy "$NAME"
REMOTE

  # gate the slot BEFORE any traffic moves
  wait_boot "$SLOT_NAME" || { echo "slot failed to boot — canonical untouched, nothing deployed"; slot_cleanup; exit 1; }
  probe "$SLOT_NAME" || { echo "slot probe failed — canonical untouched, nothing deployed"; slot_cleanup; exit 1; }
  echo "slot verified: boot marker + direct probe OK"

  # atomic cutover: traffic moves to the slot on the next connection
  flip "$SLOT_NAME"

  # recreate the canonical container under the slot's traffic umbrella
  echo "recreating canonical $SERVICE (traffic on $SLOT_NAME)"
  ssh "$STACK_HOST" "cd $STACK_DIR && docker compose up -d $SERVICE"
  wait_boot "$SERVICE" || { echo "REGRESSION: canonical failed to boot — caddy left on the verified slot; MANUAL FOLLOW-UP REQUIRED"; exit 1; }
  probe "$SERVICE" || { echo "REGRESSION: canonical probe failed — caddy left on the verified slot; MANUAL FOLLOW-UP REQUIRED"; exit 1; }

  # canonical verified: move traffic back to the canonical (compose-owned) name
  flip "$SERVICE"
else
  echo "no edge proxy detected — legacy in-place restart (brief downtime)"
  ssh "$STACK_HOST" "cd $STACK_DIR && docker compose up -d $SERVICE"
  wait_boot "$SERVICE" || exit 1
fi

ssh "$STACK_HOST" "docker compose -f $STACK_DIR/compose.yaml ps $SERVICE"

# ----- post-deploy regression gate (cherokeedictionary#133 R-4, R-6) -----
# the boot-wait above already gated the serving container; the enforced
# check here is the curl tier against the live edge (the playwright tier
# stays the explicit deeper check, invoked from the runbook — browser
# dependency weight, never a deploy blocker)
echo "regression gate: running the curl tier against $GATE_BASE_URL"
if ! python3 tests/curl_tier.py --base-url "$GATE_BASE_URL"; then
  echo "REGRESSION GATE: FAILED (see the failing checks above — the deployment is not silently committed)"
  if [ "$BLUEGREEN" = 1 ]; then
    echo "rolling caddy back to the verified slot (kept alive for exactly this) — MANUAL FOLLOW-UP REQUIRED"
    flip "$SLOT_NAME"
  fi
  exit 1
fi
echo "regression gate: PASSED"

# success: drain the slot only after the tier passed
if [ "$BLUEGREEN" = 1 ]; then
  echo "draining $SLOT_NAME"
  ssh "$STACK_HOST" "docker stop -t 30 $SLOT_NAME && docker rm $SLOT_NAME" >/dev/null
  echo "slot drained"
fi
