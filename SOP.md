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

## Verify

`tests/README.md` — the two-tier regression suite (the curl gate + the
playwright browser tier), the named-target driver with the edge-leak
guardrail, the provenance catalog, and the rehearsals. `poc/compose.yaml`
is the local validation stack (fixture mode runs from the repository
alone).

## Superseded

The legacy flow (war via scp to `vhost.`'s Tomcat, `cedproject`'s
buildScripts/deployWar.sh) is superseded by this container posture —
recorded in the devops runbook
`site-publishing-cherokeedictionary-net.md`.

🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
