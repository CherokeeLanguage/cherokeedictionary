package net.cherokeedictionary.relationalTest

import net.cherokeedictionary.dictionary.Likespreadsheets

class MigrationTestController {
    def migrationTestService

    def index() {
        // Grab a few rows to test
        def testRows = Likespreadsheets.list(max: 10)
        def parsedResults = testRows.collect { migrationTestService.parseRow(it) }

        [results: parsedResults]
    }

    /**
     * Full-entry comparison view for a single legacy row.
     * Shows the assembled relational entry ("he's limping") on top,
     * and the raw legacy Likespreadsheets fields underneath for comparison.
     */
    def entry(String search) {
        String needle = (search ?: "limp").trim()

        Likespreadsheets row = findRow(needle)
        if (!row) {
            return [error: "No Likespreadsheets row matched '${needle}'",
                    search: needle, entry: null, legacyRow: null]
        }

        [search: needle,
         entry: migrationTestService.parseRow(row),
         legacyRow: row]
    }

    private Likespreadsheets findRow(String needle) {
        String like = "%${needle}%".replaceAll("'", "")
        Likespreadsheets.createCriteria().get {
            or {
                ilike('definitiond', like)
                ilike('definitionlarge', like)
                ilike('vthirdpresk', like)
                ilike('vthirdprestranslit', like)
                ilike('vfirstpresg', like)
                ilike('vfirstprestranslit', like)
                ilike('entrya', like)
            }
            maxResults 1
        }
    }
}
