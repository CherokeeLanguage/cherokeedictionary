package net.cherokeedictionary.dictionary

class KingCompareController {

    def index() {
        render(view: "/kingCompare", model: [filter: params.filter])
    }

    /**
     * Saves the checkbox state from kingCompare.gsp into KingRRDLookup.
     * Every King row on the page posts its id via "kingids" (even when nothing
     * is checked, so a fully-unchecked row clears its old matches), and each
     * checked box posts "kingid:cedid" via "match".
     */
    def saveMatches() {
        List kingIds = params.list('kingids')
        Map<Integer, List<Integer>> checkedByKing = [:]

        params.list('match').each { String match ->
            def parts = match.split(':')
            int kingId = parts[0] as Integer
            int cedId = parts[1] as Integer
            checkedByKing.get(kingId, []) << cedId
        }

        int savedCount = 0

        kingIds.each { String kingIdParam ->
            int kingId = kingIdParam as Integer
            List<Integer> cedIds = checkedByKing.get(kingId, []) ?: []

            // replace whatever was saved before for this King entry
            KingRRDLookup.findAllByKingid(kingId).each { it.delete() }

            cedIds.each { int cedId ->
                new KingRRDLookup(kingid: kingId, cedid: cedId).save(failOnError: true)
                savedCount++
            }
        }

        flash.message = "Saved ${savedCount} match(es) across ${kingIds.size()} King entries."
        // go back to the start of the unchecked queue: the first pageSize entries
        // that still have no checked matches (entries seen but left unchecked stay visible)
        redirect(controller: "kingCompare", action: "index",
                 params: [filter: params.filter, offset: 0, max: params.max])
    }

    /**
     * AJAX: looks up a single Likespreadsheets entry by its (pasted) id.
     * Renders the _manualResults fragment for the given King row.
     */
    def lookupById() {
        int kingId = params.int('kingId')
        def ls = Likespreadsheets.get(params.int('id'))

        render(template: "/kingCompare/manualResults",
               model: [kingId: kingId, results: ls ? [ls] : [], collapsed: false])
    }

    /**
     * AJAX: searches Likespreadsheets English definitions (source='rrd') so the
     * King entry can be cross-referenced by its English meaning. Renders the
     * _manualResults fragment for the given King row.
     */
    def lookupByTerm() {
        int kingId = params.int('kingId')
        String term = params.term?.trim()
        def results = []

        if (term) {
            def exact = Likespreadsheets.find(
                    "from Likespreadsheets l where l.source = 'rrd' and l.definitiond = ?0", [term])
            if (exact) {
                results = [exact]
            } else {
                results = Likespreadsheets.createCriteria().list(max: 20) {
                    // only RRD-sourced entries, matched on the English definition text
                    eq('source', 'rrd')
                    or {
                        rlike('definitiond', term)
                        rlike('definitionlarge', term)
                    }
                }
            }
        }

        render(template: "/kingCompare/manualResults",
               model: [kingId: kingId, results: results, collapsed: true])
    }
}
