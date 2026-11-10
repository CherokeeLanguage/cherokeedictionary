# Spec — SOP: document the logging paradigm (vhost-58 mirror tree, devops#48)

Filed as CherokeeLanguage/cherokeedictionary#159 (2026-11-10). Origin:
the developer's directive "add follow up specs in the appropriate container
repos in repos/ to update their docs to reflect the new logging paradigm" —
the logging paradigm cherokeelanguage-devops#48 delivered on vhost-58
(2026-11-10: the `vector` stream shipper, the grouped mirror tree
`/var/log/containers/`, the daily/30-day `copytruncate` logrotate drop-in,
and the Caddy access log). This repo's deploy/ops record
(`SOP.md`) predates that delivery and states nothing about where the
`dict-app` container's logs now live.

## Problem

1. `SOP.md` (the container's operating index) predates the logging stack: an
   operator following it has no statement of the `dict-app` log-mirror path,
   no statement that console lines persist across container recreation, and
   no pointer to the contract that governs future changes.
2. The #48 record (its deployed state verified 2026-11-10: all SCs green;
   execution record in the devops store) carries the facts; nothing in this
   repo references them — the docs' pointer-resolution guarantee (the SOP is
   an index into executing artifacts) has no entry for logging.

## Requirements

- **R-1 (SHALL):** `SOP.md` gains a **Logging** section stating, in the
  SOP's index voice (procedures live in the executing artifacts; this
  section points, never duplicates):
  1. the container's log mirror: `/var/log/containers/dict-app/current.log`
     on vhost-58 (prod host per this SOP's host-roles section) — the
     `vector` stream shipper (cherokeelanguage-devops#48) captures the
     container's stdout/stderr **verbatim, one event per line**, and the
     mirror persists across container recreation and daily rotation (30-day
     history, dated copies);
  2. the logging contract in one line: console-first — stdout/stderr stream
     capture requires no per-repo configuration; a file-based log instrument
     this app enables in the future must mount under the
     `/var/log/containers/` tree to inherit the drop-in's rotation (the R-5
     contract's file branch);
  3. the org runbook of record:
     cherokeelanguage-devops `docs/runbooks/logging-vhost-58.md`
     (cherokeelanguage-devops#48) — deploy, probe, and rollback live there.
- The console class is the execution record's fact (the Grails startup
  lines + ERROR-class request events; successful requests console-silent —
  the #158 attribution-gap record); the SOP currently states no console
  class and this spec adds none — the section adds only the
  mirror/contract facts.

## Success criteria

| ID | Class | Criterion | Verification instrument |
|---|---|---|---|
| SC-1 | structural | The committed `SOP.md` carries a Logging section stating all three R-1 elements (mirror path, one-line contract, runbook pointer). | a direct content read of the committed `SOP.md` (grep + read) after the change |

## Approach

One targeted section in the repo's existing SOP.md — no other file touched;
the SOP's index discipline (no procedure duplication) governs its shape.

## Out of scope

- Any change to the logging stack itself (cherokeelanguage-devops#48's
  scope, delivered).
- Deployment scripts, Dockerfile, and application code.
- The sibling repos' SOPs (their own specs: CherokeeLanguage/AudioQualityVote#34,
  CherokeeLanguage/CNT#21; siblings of devops#48's delivery).

## Impact

- Documentation only; runtime unaffected. The SOP's readers gain the factual
  logging surface; no credential or auth surface touched.
