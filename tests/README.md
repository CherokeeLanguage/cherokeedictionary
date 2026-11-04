# Regression test suite (cherokeedictionary#133)

The pre/post deploy gate: a committed regression suite in two tiers, both
targets driven by one variable (`--base-url` / `CED_BASE_URL`), with
per-check latency records gated by fixed bands (R-7).

## Layout / implementation choice (recorded, SC-1)

Suite home: `tests/` (sibling of `poc/`, not inside it). `poc/` stays the
PoC validation artifacts; the gate is a deliverable, not a PoC. The
deploy gate rides `deploy-stack.sh`.

- `regression_config.py` — the versioned constants: `PERF_MULT` (2.0x
  baseline), the tier absolute bands (curl 2.0 s, playwright 10.0 s),
  `BASELINE_WINDOW` (first 10 runs are baseline-collection only).
  Overrides ride this file, versioned — never ad-hoc.
- `suite_lib.py` — the latency record: the trailing-baseline (median of
  the check's previous good runs, windowed to `BASELINE_WINDOW`), the
  fail:perf verdict, the one-line-per-check + summary output.
- `target.py` — THE TARGET DRIVER, single point of truth: the named
  targets table, the local name→loopback mapping (both `socket` for the
  parent process and `--host-resolver-rules` for browser-process tiers),
  and the Cloudflare-leak guardrail (abort before check 1 when a local
  target's hostname resolves off loopback).
- `curl_tier.py` — the enforced gate: pure HTTP status + payload-shape
  assertions, single requests, runs in seconds.
- `playwright_tier.py` — the explicit deeper check (browser tier).
  NOT a deploy blocker; invoked from the runbook.
- `runlog.jsonl` — the append-only run log (committed): one record per
  check per run (behavior + its perf record), so latency drift stays
  visible over time.

## Running

Targets are the driver's job (`tests/target.py`): `--target poc|dev|apex`
(or `CED_TARGET`), or an explicit `--base-url`. LOCAL targets get the
name→loopback mapping installed process-wide AND a GUARDRAIL: if the
local target's hostname resolves to anything but loopback, the driver
aborts loudly BEFORE any check runs — a Cloudflare 521 from the wrong
destination is a driver failure, not a check failure. This is the
permanent fix for the recurring "tests leak to the public edge when
meant to be local-only" issue; no tier carries its own mapping hack
anymore.

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
- the spring-session JDBC DDL (#130) is emitted from the committed
  `sql/spring-session-schema-mysql.sql` into the fixture
- verse rows: a small even sample;
- the generator records one sampled audio-linked entry in
  `tests/fixture_audio_entry.txt` — the browser tier's
  `audio:fixture-entry` check opens THAT entry and demands a rendered
  `<audio>` (the deterministic positive audio check).

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
python3 tests/playwright_tier.py --dump-session /tmp/ced-state.json

# 2. restart the stack (the survival condition)
#    docker compose -f poc/compose.yaml restart dict-app

# 3. post-restart: the carried session must still reach /admin
python3 tests/playwright_tier.py --storage-state /tmp/ced-state.json
```

`--storage-state` omitted: the survival check reports as a skip in its
line's detail — never a failure.

## Negative control rehearsal (SC-3)

1. The forced failure: stop a surface deliberately (point `--base-url`
   at a closed port, or stop `dict-app`), run the curl tier, confirm it
   prints `fail: <name>`, the non-zero exit, and a visible summary.
2. The forced slow (`fail:perf`, SC-7): inject a delay in front of one
   endpoint (e.g. an nginx/Caddy proxy with a sleep in front of the dev
   tier), confirm the run log shows the perf record and the run exits
   nonzero. Note: until the check has `BASELINE_WINDOW` (10) recorded
   good runs, it's baseline-collection only — NO perf verdict; the run
   log marks those records so the window is visible in the record.

## Provenance catalog (R-5)

| Check | Regression it remembers |
|---|---|
| home:200-form-syllabary / home:200, home:search-form, home:syllabary-glyphs | core surface; the first curl spot-check per fix (lived in transcripts, never enforced) |
| autocomplete:db-backed-json | DB-backed JSON through the full edge chain (datasource/conn class) |
| search:form-flow-results | **#122**: the search 500'd silently in production while every GET-only spot check passed — server-side session state (`session["results"]`, `NewSearchController.groovy:123`) are observable only on the POST flow |
| download:csv-with-rows | **#122**: download streams from `session["results"]` (`NewSearchController.groovy:359-360`) — same session contract |
| notfound:404-view | the catch-all routing class: the Caddyfile catch-all must route unknown paths INTO the app (the app's 404 view), not swallow them |
| admin:session-survived | the session/cookie-contract class (devops#14 smoke item 15) |
| (playwright) entry:individual-200, entry:audio-present | the entry page and the DB-backed audio path classes |
| (playwright) grammar:toc-loaded | the grammar guide (hold1.txt fetch) surface |

New regressions found in the wild get a check the SAME DAY, with its
provenance line here and in-code.

## Adoption/rollback posture (SC-6)

- the gate rides `deploy-stack.sh` post-`up -d` (R-4): boot-marker wait
  (real state check, timeout — no fixed sleep), then the curl tier.
  Nonzero → the script prints the failing checks and exits FAILED — the
  deployment is NOT silently committed (rehearsal: break a surface, run
  deploy-stack.sh, confirm the FAILED path; staged-failure rehearsal
  belongs in the runbook).
- rollback on a gate failure: redeploy the prior image tag (or roll the
  DNS flip back per the devops#14 cutover plan) — the gate's failing
  check names are the diagnostic front end, the fix is a redeploy of a
  prior build, never a manual server surgery.
- the apex cutover (devops#14) re-points `GATE_BASE_URL` at the apex for
  pre/post verification — no suite code change.
- docs/index registration for this runbook: the devops repository's
  docs/index (cross-repo deliverable; the suite itself lives here).
