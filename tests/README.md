# Regression test suite (cherokeedictionary#133)

The pre/post deploy gate: a committed regression suite in two tiers, both
targets driven by one variable (`--target` / `CED_TARGET` / an explicit
`--base-url`), with per-check latency records gated by fixed bands (R-7).

## Layout / implementation choice (recorded, SC-1)

Suite home: `tests/` (sibling of `poc/`, not inside it). `poc/` stays the
PoC validation artifacts; the gate is a deliverable, not a PoC. The
deploy gate rides `deploy-stack.sh`.

- `regression_config.py` — the versioned constants: `PERF_MULT` (2.0x
  baseline), the per-latency-class absolute bands (`PERF_ABS`: the spec's
  original 2.0 s curl / 10.0 s playwright figures are the LOCAL class —
  the loopback-published PoC stack they were derived against; the remote
  classes carry wider constants because the public edge's measured
  single-request jitter (0.17 s–26.8 s, dev tier, back-to-back runs,
  2026-11-04) exceeds any tight band), `PERF_ABS_OVERRIDES` (per-check
  bands for checks whose latency belongs to a surface other than their
  target — the audio-asset check fetches the EXTERNAL CDN even on a
  local target, so it rides an 8.0 s band, not the local 2.0 s),
  `BASELINE_WINDOW` (the first 10 runs of a check are baseline-collection
  only), and `DEAD_REF_MAX` (the dead-reference sweep's per-run ref cap).
  Overrides ride this file, versioned — never ad-hoc.
- `suite_lib.py` — the latency record: the trailing-baseline (median of
  the check's previous good behavior-run latencies ON THE SAME TARGET,
  windowed to `BASELINE_WINDOW` — a loopback latency is not a baseline
  for a remote-edge run), the `fail:perf` verdict (reported as its own
  result entry — a slow AND broken check reports both verdicts, never
  silently reclassified), the one-line-per-check + summary output.
- `target.py` — THE TARGET DRIVER, single point of truth: the named
  targets table, the local name→loopback mapping (both `socket` for the
  parent process and `--host-resolver-rules` for browser-process tiers),
  and the Cloudflare-leak guardrail (abort before check 1 when a local
  target's hostname resolves off loopback).
- `curl_tier.py` — the enforced gate: pure HTTP status + payload-shape
  assertions, single requests, runs in seconds.
- `playwright_tier.py` — the explicit deeper check (browser tier).
  NOT a deploy blocker; invoked from the runbook.
- `runlog.jsonl` — the append-only run log, **local and untracked
  (gitignored — never committed)**: one behavior record per check per
  run, plus one perf-kind record per check per post-window run, so
  latency drift stays visible within a working session. Records carry
  `ts`, `head`, `tier`, `base_url`, `check`, verdict, `kind`,
  `latency_s`, `baseline_s`, and the `baseline_collection` mark.
  **Policy: run records are ephemeral working-session evidence — the
  suite's gate result is binary (the run passes or exits FAILED); no
  run-record file is ever committed or re-added to this repository.**
  Housekeeping: the log is append-only; a prune must keep at least the
  last `BASELINE_WINDOW` good behavior records per (tier, check,
  base_url) or the baselines reset into collection mode.

## Running

Targets are the driver's job (`tests/target.py`): `--target poc|dev|apex`
(or `CED_TARGET`), or an explicit `--base-url`. LOCAL targets get the
name→loopback mapping installed process-wide AND a GUARDRAIL: if the
local target's hostname resolves to anything but loopback, the driver
aborts loudly BEFORE any check runs — a Cloudflare 521 from the wrong
destination is a driver failure, not a check failure. The leak class is
ALSO enforced at the wire (`assert_not_edge_leaked`): a local target's
response carrying Cloudflare's `cf-ray`/`server: cloudflare` markers
aborts the run regardless of which component produced it — the
2026-11-04 incident (the SC-7 rehearsal proxy resolved the serving name
itself and 521'd off the public edge, caught only by human inspection)
is the class this tripwire remembers; suite tooling takes its connect
address from `connect_target()` and never resolves serving names. This
is the permanent fix for the recurring "tests leak to the public edge
when meant to be local-only" issue; no tier or tool carries its own
mapping hack anymore.

```sh
# pre/post deploy against the dev tier (default):
python3 tests/curl_tier.py
python3 tests/playwright_tier.py

# local PoC stack:
python3 tests/curl_tier.py --target poc
python3 tests/playwright_tier.py --target poc

# explicit override (quirk demanding URLs get the same driver treatment):
python3 tests/curl_tier.py --base-url https://cherokeedictionary.net:8443
python3 tests/playwright_tier.py --base-url https://cherokeedictionary.net:8443
```

## Fixture (tracked seed subset, #133)

The suite runs from the REPOSITORY alone: `poc/seeds/make-fixture.py`
generates `poc/seeds-fixture/01-fixture-dictionary-sample.sql` (a small,
secrets-free subset of the operator's prod dumps) plus copies of the
shared 98/99 seed entries. It is commited and deterministic.

- every SourceManagement row ships with ~12 sample `likespreadsheets`
  entries (sources with zero prod entries get one "sample" placeholder
  row — NO fabricated Cherokee words), so the UI's source filters are
  all testable;
- the FK chains (CherokeeIndex, word_link/word/definition chain where
  consistent, audio_file/audio_link) follow the sampled entries; the
  relational word/definition tables are FRAGMENTED in the prod dumps and
  ship created-empty;
- `admin`/`user`/`role`/`history`/`user_search` tables ship CREATED
  EMPTY — the prod dump's admin table carries REAL plaintext passwords
  and must never ride a fixture;
- the spring-session JDBC DDL (#130): the canonical committed
  `sql/spring-session-schema-mysql.sql` is mounted by the PoC compose
  for BOTH seed documents, and `97-spring-session-schema.sh` (present in
  both seed documents) applies it idempotently at init. The prod dumps
  predate #130 and carry no SPRING_SESSION DDL — a dump-mode stack
  without it 500s on EVERY request (the session store's response-commit
  INSERT fails; the whole site is down). The fixture generator also
  inlines the same DDL at generation time; the script no-ops there.
- verse rows: a small even sample;
- the generator records one sampled audio-linked entry in
  `tests/fixture_audio_entry.txt` — the deterministic entry id shared by
  every target derived from the operator dumps (the fixture is a subset
  of the same dump, ids intact). The browser tier's `audio:fixture-entry`
  and the curl tier's `entry:schema-joins-render` / `audio:asset-200`
  checks open THAT entry.

Fixture-mode run (no operator dumps required):

```sh
cd poc && CED_SEEDS_DIR=seeds-fixture docker compose -p ced-poc up -d
```

(one of the seed documents, never both: ids collide; rebuilds need
`down -v` so the MariaDB init scripts re-run).

Regenerate after the schema changes (operator-required):

```sh
python3 poc/seeds/make-fixture.py   # needs the operator's dumps locally
```

Both tiers: R-3 — nonzero exit on any failed check (behavior `fail` or
latency `fail:perf`), one line per check (`pass|fail: name`), and a
summary.

**The gate's one remaining red is `/cnt` by design** (`assets:referenced-resolve`):
the nav item is a dead endpoint until the reconciled static deployment lands —
**cherokeelanguage-devops#29** tracks it. The font and colorbox classes from
**#136** are FIXED (the vendored FreeSerif family + the `loading.gif` typo
correction ride this same branch); the browser-tier sweep already runs green.

## The sanctioned credential surface (the suite's ONLY credential)

The admin session-survival item (devops#14 smoke item 15's executable
form) rides a dedicated seeded test account — not operator credentials,
not a token in the repo:

- the PoC MariaDB test container seeds it at init:
  `poc/seeds/98-poc-test-admin.sh`, with the username/password from the
  operator-held `.env` via the `CED_TEST_ADMIN_USER` /
  `CED_TEST_ADMIN_PASSWORD` compose environment (never literals in the
  tree; serving-stack containers do NOT seed it).
- `AdminController.login` matches the password in PLAINTEXT
  (`Admin.findAll("from Admin where username=?0 and password=?1")`,
  `grails-app/controllers/.../AdminController.groovy:120`), so the seeded
  row works with no hashing step.

Two-step survival flow (login → container restart → still authenticated):

```sh
# 1. pre-restart: log in with the test account, save the session state
#    (credentials from the operator .env's CED_TEST_ADMIN_* keys:
#    set -a; . <(grep '^CED_TEST_ADMIN_' poc/.env); set +a)
python3 tests/playwright_tier.py --dump-session /tmp/ced-state.json

# 2. restart the stack (the survival condition)
#    docker compose -f poc/compose.yaml restart dict-app

# 3. post-restart: the carried session must still reach /admin
python3 tests/playwright_tier.py --storage-state /tmp/ced-state.json
```

`--storage-state` omitted: the survival check reports as a skip in its
line's detail — never a failure.

## Negative control rehearsal (SC-3, SC-7)

1. The forced failure (behavior): stop a surface deliberately (point
   `--base-url` at a closed port, or stop `dict-app`), run the curl tier,
   confirm it prints `fail: <name>`, the non-zero exit, and a visible
   summary. Recorded instance: the 2026-11-04 16:44Z run (3 fails, exit 1)
   and the closed-port run in the audit transcript.
2. The forced slow (`fail:perf`, SC-7): inject a delay in front of one
   endpoint and confirm the run log shows the perf record (`kind: "perf"`,
   `verdict: "fail:perf"`) and the run exits nonzero. The concrete
   procedure, using the committed rehearsal tool
   (`tests/perf_delay_proxy.py` — which takes its connect address from
   `tests/target.py` and carries the edge-leak tripwire; rehearsed
   2026-11-04 against the PoC stack at `https://localhost:8444`):
   1. run a TLS-terminating delay proxy in front of the PoC stack:
      `openssl req -x509 -newkey rsa:2048 -nodes -days 1 -subj
      "/CN=cherokeedictionary.net" -keyout /tmp/proxy.key -out
      /tmp/proxy.crt`, then `python3 tests/perf_delay_proxy.py
      /tmp/proxy.crt /tmp/proxy.key 127.0.0.1 8444 poc
      /newSearch/ajaxEnglishFinder 0`;
   2. warm the target's baseline: `python3 tests/curl_tier.py --base-url
      https://localhost:8444` until every check has BASELINE_WINDOW (10)
      good runs — until then the run is baseline-collection only, NO perf
      verdict (the run log marks those records `baseline_collection`);
   3. re-run with the injected delay (restart the proxy with `35` in
      place of `0` — it must exceed the REMOTE-class absolute band, since
      explicit `--base-url` overrides are remote-class): the delayed check
      exceeds BOTH bands → `fail:perf` line, `PERF (fail:perf): …` in the
      summary, exit 1.

## Staged-failure rehearsal of the deploy gate (SC-4)

The gate's FAILED path is rehearsed by running `deploy-stack.sh` with the
gate pointed at a deliberately broken edge:
`GATE_BASE_URL=http://127.0.0.1:9 ./deploy-stack.sh` — the deploy
completes, the boot-wait sees the real marker, then the curl tier fails
every check against the closed port and the script exits
`REGRESSION GATE: FAILED`. Recorded instance: 2026-11-04 (the deploy
transcript). The passing path (default `GATE_BASE_URL`, the dev tier)
is every real deploy's post-step.

## Provenance catalog (R-5)

| Check | Regression it remembers |
|---|---|
| home:200-form-syllabary / home:200, home:search-form, home:syllabary-glyphs | core surface; the first curl spot-check per fix (lived in transcripts, never enforced) |
| autocomplete:db-backed-json | DB-backed JSON through the full edge chain (datasource/conn class) — **also the timezone-value rejection class** (#123: connector 8.0.13 rejected `serverTimezone=CST` at connection setup — the class's failure mode is total datasource refusal, indistinguishable over HTTP from any DB-backed check; no public datetime surface exists to probe finer, and app changes are out of scope) |
| search:form-flow-results | **#122**: the search 500'd silently in production while every GET-only spot check passed — server-side session state (`session["results"]`, `NewSearchController.groovy:123`) are observable only on the POST flow |
| download:csv-with-rows | **#122**: download streams from `session["results"]` (`NewSearchController.groovy:359-360`) — same session contract |
| notfound:404-view | the catch-all routing class: the Caddyfile catch-all must route unknown paths INTO the app (the app's 404 view), not swallow them |
| entry:schema-joins-render | the **FK/schema failure** class (#118 replacement DDL, #124's `default_catalog` finding): the entry page joins likespreadsheets→SourceManagement (the source-attribution block) and renders the headword — a broken schema/FK chain 500s or blanks it |
| audio:asset-200 (curl) / audio:fixture-entry (playwright) | the audio-asset class: the DB-backed audio path must yield a SERVING asset (200, audio/*), not just a rendered `<audio>` tag |
| the edge-leak tripwire (target.py `assert_not_edge_leaked`, wired into the curl Session, the playwright responses, and the delay proxy) | the **Cloudflare-leak class**: suite traffic meant for a local stack reaching the public edge — recurring across component kinds (fixed in the tiers, reintroduced 2026-11-04 by the SC-7 rehearsal proxy doing its own name resolution); enforced at BOTH layers: the cause (tooling takes connect addresses from `connect_target()`, never resolves serving names) and the outcome (cf-ray markers on a local target's response abort the run loudly) |
| admin:session-survived | the session/cookie-contract class (devops#14 smoke item 15) — on its first live run (2026-11-04) it caught the spring-session parallel-commit duplicate-key 500s (#135, filed to #130's domain); the survival flow settles its redirect chain so the rehearsal doesn't trip that store race as a side effect |
| assets:referenced-resolve (curl) / assets:no-dead-references (playwright) | the **dead-reference class** (R-8, #136): generated/static URLs that reference non-existent files or endpoints — a dead asset reference never changes a page's status code, so it is invisible to every status-based check. First catch, the day the check landed: the FreeSerif `@font-face` family (12 dead URLs in the public layout), the `/cnt` nav link, the colorbox loading skins → **#136**. Complementary instruments: the curl sweep fetches collected refs directly (catches link-shaped dead endpoints like `/cnt`); the playwright wire guard records every same-host ≥400 response the browser itself issues (catches font/CSS/image fetches, including conditional loads) |
| (playwright) entry:individual-200, entry:audio-present | the entry page and the DB-backed audio path classes |
| (playwright) grammar:toc-loaded | the grammar guide (hold1.txt fetch) surface |

New regressions found in the wild get a check the SAME DAY, with its
provenance line here and in-code.

## Adoption/rollback posture (SC-6)

- the gate rides `deploy-stack.sh` post-`up -d` (R-4): boot-marker wait
  (real state check, timeout — no fixed sleep), then the curl tier.
  Nonzero → the script prints the failing checks and exits FAILED — the
  deployment is NOT silently committed (the FAILED-path rehearsal is the
  "Staged-failure rehearsal" section above).
- rollback on a gate failure: redeploy the prior image tag (or roll the
  DNS flip back per the devops#14 cutover plan) — the gate's failing
  check names are the diagnostic front end, the fix is a redeploy of a
  prior build, never a manual server surgery.
- the apex cutover (devops#14) re-points `GATE_BASE_URL` at the apex for
  pre/post verification — no suite code change.
- docs/index registration for this runbook: DONE — registered in the
  devops repository's docs/index under Runbooks (cross-repo link, per
  that index's scope note: per-repo documentation is linked, never
  copied; the suite itself lives here).
