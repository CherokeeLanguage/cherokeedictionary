# SOP — the dict-app container (cherokeedictionary)

The Grails dictionary application's containerized serving form: the
`dict-app` compose service on the delivered stack (image `dict-app:stage`,
built by `deploy-stack.sh` from the committed tree). This file is the
container's operating index — the procedures live in the artifacts that
execute them; nothing here duplicates them.

## Host roles (2026-11-09, operator directive — cherokeedictionary#150)

- **vhost-58.cherokeedictionary.net (104.251.216.58)** — the **prod
  host**: the delivered Docker serving stack this container deploys to
  and serves from (the apex and the dev tier both ride it as of this
  date; verified by the 2026-11-09 deploy — gate PASSED, 10/10 — and the
  apex re-run, 10/10 exit 0).
- **vhost-175.cherokeedictionary.net (63.142.255.175, = ced1)** — the
  **dev host**: where dev-stack work lands going forward.
- Org-side fact record: cherokeelanguage-devops
  `docs/infrastructure/host-roles-vhost-58-vhost-175.md`.

## Deploy

`./deploy-stack.sh` — ships the committed tree (`git archive HEAD`; no
untracked or ignored material rides along) to the stack host, rebuilds and
restarts the `dict-app` compose service, waits on the real boot marker,
then runs the regression gate (`tests/curl_tier.py` against
`GATE_BASE_URL`, default the dev tier; the apex cutover re-points it).
Gate FAILED → the deployment is not committed: redeploy the prior image
tag — never manual server surgery (the adoption/rollback posture in
`tests/README.md`).

## Two-phase container check (pre-flight / post-flight — devops#56)

Every deploy or container-work step on the serving stack runs the
standing check **before** and **after** (the org rule of record:
cherokeelanguage-devops `docs/runbooks/container-sop-checks.md` — check
definitions live once there; this SoP links, never copies):

- **Pre-flight** — the check runs first and must **exit 0**: it is a
  gate, and nothing starts on a failing result.
- **Post-flight** — the check re-runs after the step; a failing
  post-flight means the work is **not done** (the deploy's own
  "the deployment is not silently committed" posture, above).
- **Capture-with-work** — the executed output is the work's artifact:
  both runs (pre and post) are captured and recorded with the work
  (the issue's `research/`).

This repo's container/route inventory on the shared vhost-58 stack
(what the sweep covers here): the **`dict-app`** compose service, behind
the apex **`/`** on cherokeedictionary.net plus the
**`/font/*`**, **`/static/*`**, **`/media/*`**, and **`/audio/*`** routes,
and the **`reverse_proxy` catch-all** (the switchover section's flip
token, `reverse_proxy dict-app:8080`). Non-routed role checks apply to
the stack's backend containers (MariaDB reachable from the
backend-attached app container).

## Switchover (zero-connection-loss image replacement — cherokeedictionary#153)

Replacing the `dict-app` image without a connection gap. The plain
`docker compose build dict-app && docker compose up -d dict-app` stops the
old container before the new one is healthy, and the boot is minutes-class
(the deploy script's `GATE_BOOT_TIMEOUT` budget) — the apex 502s for the
whole recreate+boot window. So the swap runs blue-green, conforming to the
org runbook (cherokeelanguage-devops#42). At no point is there a moment
without a healthy upstream behind Caddy.

The sequence, with this container's specifics:

1. **Stage `dict-app-next` alongside** the serving container — same env
   delivery by path (`/srv/mariadb/secrets/dict-app.env` referenced, never
   read), same `proxy`+`backend` networks, same memory-limit and
   `JAVA_TOOL_OPTIONS` posture (the devops#40 4608m values when that ticket
   lands), **no host ports** — zero conflict with the canonical service.
2. **Wait for the boot marker** on the next container's logs — the deploy
   script's marker regex:
   `Tomcat.*started|Grails application running|Started .*Application`.
3. **Verify it serves directly** (a request to `dict-app-next:8080` from
   inside the `proxy` network) before any traffic moves.
4. **Atomic Caddy flip**: in `/srv/caddy/caddy/Caddyfile`, change the
   catch-all's `reverse_proxy dict-app:8080` token to
   `dict-app-next:8080` (the `/font/*` block is a static `file_server` —
   untouched by the flip), `caddy validate` must exit 0, then the graceful
   reload — in-flight requests are preserved.
5. **Recreate the canonical `dict-app`** container with the new image.
6. **Flip Caddy back** to canonical `dict-app:8080`, verify.
7. **Drain `dict-app-next`** (remove the container).
8. **Full gate**: `tests/curl_tier.py` against the apex for a prod swap —
   the deploy script's gate, re-pointed per its `GATE_BASE_URL` convention.

**Continuity bar and rollback rule**: a request loop through the edge
across the flip window shows **zero failed requests**. The prior image tag
is retained until the full gate passes; a failed gate flips traffic back —
the existing "the deployment is not silently committed" posture (gate
FAILED → redeploy the prior image tag; never manual server surgery)
extends to the switchover.

**Mapping onto `deploy-stack.sh`**: the script already carries the context
ship (`git archive HEAD`), the build, the canonical recreate
(`docker compose up -d`), the boot-marker wait, and the curl gate. The
SoP's flip/drain steps **wrap** those: stage+verify before the recreate,
the Caddy flip around it, the drain and the re-pointed gate after. An
operator reading either artifact can execute the whole sequence. (Whether
the script gains a first-class blue-green mode is #153's open item — the
script change is a deliberate follow-up decision.)

**Rehearsal**: the local PoC validation stack (`poc/compose.yaml` — fixture
mode, runs from the repository alone; it carries its own `ced-poc-caddy`)
executes the sequence end-to-end with a continuity loop reporting zero
failed requests across the flip; the executed capture is recorded on
#153.

## Verify

`tests/README.md` — the two-tier regression suite (the curl gate + the
playwright browser tier), the named-target driver with the edge-leak
guardrail, the provenance catalog, and the rehearsals. `poc/compose.yaml`
is the local validation stack (fixture mode runs from the repository
alone).

## Logging

- Mirrored console log: `/var/log/containers/<container>/current.log` on
  vhost-58 — the devops#48 `vector` shipper captures the container's
  stdout/stderr verbatim, one event per line, surviving both container
  recreation and the daily/30-day dated-copy `copytruncate` rotation.
- Console-first contract: stdout/stderr capture needs no per-repo
  config; a future file-based log instrument must mount under the
  `/var/log/containers/` tree to inherit the rotation.
- Governing contract (org runbook of record): cherokeelanguage-devops
  `docs/runbooks/logging-vhost-58.md` (#48).

## Superseded

The legacy flow (war via scp to `vhost.`'s Tomcat, `cedproject`'s
buildScripts/deployWar.sh) is superseded by this container posture —
recorded in the devops runbook
`site-publishing-cherokeedictionary-net.md`.

🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
