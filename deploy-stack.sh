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

HEAD=$(git rev-parse HEAD)
DIRTY=$(git status --porcelain | wc -l)
echo "deploying committed tree ${HEAD:0:12} (${DIRTY} uncommitted changes present — recorded, not enforced)"

# ship the committed build context
git archive HEAD | ssh "$STACK_HOST" "mkdir -p $STACK_DIR/build && tar -x -C $STACK_DIR/build"

# (re)build and (re)start the application container on the serving stack;
# the compose service carries the env delivery (operator-held env file by path)
ssh "$STACK_HOST" "cd $STACK_DIR && docker compose build $SERVICE && docker compose up -d $SERVICE"

ssh "$STACK_HOST" "docker compose -f $STACK_DIR/compose.yaml ps $SERVICE"
