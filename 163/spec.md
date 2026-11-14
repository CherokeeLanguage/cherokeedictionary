<!-- SPDX-FileCopyrightText: 2026 Michael Conrad -->
<!-- SPDX-License-Identifier: MIT -->

# Spec — re-point readingAndListening's hardcoded data.cherokee.org audio links to the self-hosted paths

Repository: CherokeeLanguage/cherokeedictionary (spec artifact AND deliverable
home — one view file's 9 URL literals). Remote filing:
CherokeeLanguage/cherokeedictionary#163 (remote-first reservation
2026-11-14). Sibling specs: cherokeelanguage-devops#71 (the mirror — this
repo's referenced files are in its manifest) and cherokeelanguage-devops#72
(the serving placement + DB rewrite this spec's target paths depend on).

## Problem

The Reading & Listening view (`grails-app/views/readingAndListening/index.gsp:52–84`)
hardcodes 9 absolute `https://data.cherokee.org/phoenix/audio/2019/*.mp3`
anchor links (view↔controller URL sets identical, 9=9; the controller's rows
are commented — dead code). The self-hosting program (devops#71 mirror;
devops#72 serving + DB rewrite) moves every referenced audio file onto
cherokeedictionary.net — but those 9 view-literal URLs are not DB data, so
devops#72's database rewrite cannot reach them, and devops#72's scope excludes
application-code changes. Without this spec the page keeps 9 external links
whose siblings become self-hosted: a half-moved surface, and the only
remaining live references to the upstream host. This spec is the per-repo
splitting of devops#72's dependency inventory per the spec card's single-repo
scope rule; the developer's governing goal (2026-11-14): "the goal is to have
cherokeedictionary.net self-host the audio files for linking."

## Developer-statement traceability

- Developer statement (2026-11-14): "the goal is to have cherokeedictionary.net
  self-host the audio files for linking" — the 9 links are part of "the audio
  files" referenced set (devops#71's manifest carries them as source (b)) and
  their linking must end up self-hosted like the rest.
- Structural input: devops#72's validation round-1 (cycle 1, 2026-11-14)
  surfaced the gap — a requirement set covering only DB values while the
  inventory declared the view links "the requirement's object" — and this
  spec is the split-resolution filing (the sibling pointer lands in
  devops#72's revision in the same workflow).

## Analysis

- **The 9 links are anchor hrefs** (`<a href="https://data.cherokee.org/...">audio</a>`),
  not `<audio><source>` elements — plain URL-string swaps, no markup change.
- **The target paths come from devops#72's R-1 serving placement** (a
  named subtree under the `/media/audio` family; path value at plan time).
  File names are preserved from upstream (manifest mapping is mechanical —
  `…/phoenix/audio/2019/StickballbyAnnaSixkiller.mp3` mirrors
  byte-identically, per devops#71's recorded probe).
- **Ordering constraint** (the mirror → serve → re-point chain): devops#71's
  corpus complete → devops#72's serving placement verified (its SC-1) → this
  spec's change lands. Before devops#72's serving exists there is nothing
  site-relative to point at — the change is last in the chain.
- **Licensing posture** (identical to devops#72's recorded posture; companion
  research card): the published content-reuse policy covers use on the web
  site with attribution; no license title; the recordings remain CN's © —
  this spec makes no new rights claim, it re-points within the program the
  developer stated.

## Requirements

- **R-1.** The view's 9 absolute `data.cherokee.org` links SHALL be replaced
  with site-relative values resolving at the serving path from
  cherokeelanguage-devops#72's R-1 (the serving path value lands at plan
  time).
- **R-2.** No other change SHALL touch the view; the controller's commented
  seed rows stay as-is (dead code).
- **R-3.** The work SHALL ride this repo's own flow (branch, commit
  convention, PR gates) — no cross-repo file changes.

## Success criteria

| ID | Criterion | Class | Instrument |
|---|---|---|---|
| SC-1 | Zero absolute `data.cherokee.org` links remain in the view; the 9 items are site-relative | structural | grep the source file and the rendered page → zero absolute upstream URLs; 9 site-relative hrefs present |
| SC-2 | The rendered Reading & Listening page's audio links resolve self-hosted | behavioral | live probe: the 9 items → `200 audio/mpeg` on cherokeedictionary.net |

## Approach

String-for-literal replacement of 9 URLs (the site-relative prefix from
devops#72's serving placement), gated on devops#72's SC-1 (serving verified)
so SC-2's probe runs against already-serving paths. No data, schema, or
route change; no dependencies touched (the anchor class is plain hyperlinks).

## Edge cases

- **A link whose file went missing upstream** (devops#71 records fetch
  results; the mirror manifest is completeness-checked): a missing file
  surfaces as that item's 404 at SC-2 — the honest state, recorded as a gap
  in devops#71's audit line, not silently dropped from the view.
- **Case sensitivity**: site-relative hrefs are case-sensitive to the
  browser; the values must byte-match the serving tree's names (the
  manifest's exact filenames).

## Out of scope

The controller's commented seed rows (dead code); the DB-driven audio
elsewhere in the app (devops#72's rewrite); the TTS corpus; any other view;
the serving-stack and mirror work (the siblings').

## Open items

- **Serving path value** — devops#72 plan output; the R-1 rewrite takes it
  verbatim (dependency, non-blocking for this spec's authoring).

## Sources

- devops#71 and devops#72 specs (the dependency inventory, the mirror
  manifest shape, the serving placement, the licensing posture).
- CherokeeLanguage/cherokeedictionary @ current main:
  grails-app/views/readingAndListening/index.gsp (9 links, lines 52–84; the
  13 same-origin relative `/audio/walc1/berlitz/*.mp3` map entries at lines
  4–16 — not data.cherokee.org references; untouched),
  ReadingAndListeningController.groovy:7–15 (commented duplicates).
- Companion research card (parent store, issues-data):
  dict-app-data-cherokee-org-audio-dependency.md.
