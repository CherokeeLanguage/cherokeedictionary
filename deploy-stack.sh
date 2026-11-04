#!/bin/bash

# Deploy the cherokeedictionary application to the serving stack's Docker host
# (vhost-58.cherokeedictionary.net) — cherokeedictionary#125's deliverable.
#
# Ships the build context as the COMMITTED tree state (git archive HEAD — no
# untracked or ignored material rides along: .env, poc/, build outputs stay
# local), then rebuilds/restarts the stack's dict-app compose service. The
# compose service itself (tree, env delivery, routing) is devops#14's
# deliverable, staged by the devops repository's
# docs/runbooks/dict-app-vhost-58-stage.sh — the script's variable block is
# the single place the host path and service name live.

set -e
set -o pipefail

cd "$(dirname "$(readlink -f "$0")")"

trap "echo ERROR" ERR

STACK_HOST="mconrad@vhost-58.cherokeedictionary.net"
STACK_DIR="/srv/cherokeedictionary"
SERVICE="dict-app"

# the regression gate target (cherokeedictionary#133 R-4): the edge the
# delivered site serves on; overridden by the cutover/deploy wrapper when
# the apex takes over
GATE_BASE_URL="${GATE_BASE_URL:-https://dev.cherokeedictionary.net}"
GATE_BOOT_TIMEOUT="${GATE_BOOT_TIMEOUT:-180}"
GATE_POLL="${GATE_POLL:-5}"

HEAD=$(git rev-parse HEAD)
DIRTY=$(git status --porcelain | wc -l)
echo "deploying committed tree ${HEAD:0:12} (${DIRTY} uncommitted changes present — recorded, not enforced)"

# ship the committed build context
git archive HEAD | ssh "$STACK_HOST" "mkdir -p $STACK_DIR/build && tar -x -C $STACK_DIR/build"

# (re)build and (re)start the application container on the serving stack;
# the compose service carries the env delivery (operator-held env file by path)
ssh "$STACK_HOST" "cd $STACK_DIR && docker compose build $SERVICE && docker compose up -d $SERVICE"

ssh "$STACK_HOST" "docker compose -f $STACK_DIR/compose.yaml ps $SERVICE"

# ----- post-deploy regression gate (cherokeedictionary#133 R-4, R-6) -----
# A REAL boot-wait: poll the container log for the boot marker (Grails:
# Tomcat started on the port / Grails application running) with a timeout —
# not a fixed sleep.
echo "regression gate: waiting for boot marker (timeout ${GATE_BOOT_TIMEOUT}s)"
BOOT_SINCE=$(date +%s)
BOOT_OK=0
while true; do
  NOW=$(date +%s)
  if [ $((NOW - BOOT_SINCE)) -ge "$GATE_BOOT_TIMEOUT" ]; then
    break
  fi
  # the --since window OVERLAPS the poll interval (4x) — ssh latency
  # between polls must not gap-skip the one-shot boot marker
  if ssh "$STACK_HOST" "docker compose -f $STACK_DIR/compose.yaml logs --since $((GATE_POLL * 4))s $SERVICE" | grep -m1 -qE "Tomcat.*started|Grails application running|Started .*Application"; then
    BOOT_OK=1
    break
  fi
  sleep "$GATE_POLL"
done
if [ "$BOOT_OK" != "1" ]; then
  echo "REGRESSION GATE: FAILED — no boot marker within ${GATE_BOOT_TIMEOUT}s (the deployment is not silently committed)"
  exit 1
fi
echo "regression gate: boot marker seen; running the curl tier against $GATE_BASE_URL"

# the curl tier is the enforced gate (the playwright tier stays the
# explicit deeper check, invoked from the runbook — browser dependency
# weight, never a deploy blocker); nonzero → the script exits FAILED
# with the failing checks printed by the suite itself
if ! python3 tests/curl_tier.py --base-url "$GATE_BASE_URL"; then
  echo "REGRESSION GATE: FAILED (see the failing checks above — the deployment is not silently committed)"
  exit 1
fi
echo "regression gate: PASSED"
