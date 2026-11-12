# Project Backlog & Todo

## Migration Work
- [ ] Complete full dataset dry-run migration (iteration phase).
- [ ] Refine split/massage rules for all edge cases (commas, parentheses, semicolons in definitions).
- [ ] Implement Migration Report (tracking rows processed/skipped/failed).
- [ ] Production load script (syncing `Likespreadsheets` to normalized schema).
- [ ] Establish ongoing `Likespreadsheets` → relational sync (GORM service-based).

## Future Improvements
- [ ] **Dialect Tagging:** Revisit the `dialect` field in `RelTestSense`. Currently, we extract `(e)` and `(w)` tags; we need to decide if we want a `Dialect` lookup table, support for more regions (Snowbird, etc.), and UI/search features for dialect filtering.

## Current State
- Local prototype with `RelTest*` domain classes is functional.
- `MigrationTestService` / `Controller` / View exists at `/migrationTest/entry` and `/migrationTest/index`.
- Positional splitting for comma-delimited form columns is implemented.
