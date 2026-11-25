package net.cherokeedictionary.relational

import net.cherokeedictionary.dictionary.Likespreadsheets

class MigrationController {

    def lexiconEtlService

    // Endpoint to run full migration: http://localhost:8080/migration/run
    def run = {
        def result = lexiconEtlService.runMigration()
        render "Migration complete. Success: ${result.success}, Errors: ${result.errors}, Skipped (grammar refs): ${result.skipped}"
    }

    // Endpoint to test single/multiple IDs: http://localhost:8080/migration/test?ids=72205,74066
    def test = {
        String idsParam = params.ids ?: ""
        List<Long> ids = []
        if (idsParam) {
            ids = idsParam.tokenize(',').collect { it.toLong() }
        }

        def result = lexiconEtlService.runMigration(ids)
        render "Test migration complete for IDs ${ids}. Success: ${result.success}, Errors: ${result.errors}, Skipped (grammar refs): ${result.skipped}"
    }

    // Endpoint to reset relational tables: http://localhost:8080/migration/reset
    // FK-safe order (children first); lookup tables (PartOfSpeech, Category, Dialect,
    // FormType) are kept so BootStrap seeds and resolved lookups survive a reset.
    def reset = {
        ExampleSentence.executeUpdate("delete from ExampleSentence")
        Definition.executeUpdate("delete from Definition")
        EntryWord.executeUpdate("delete from EntryWord")
        DictionaryEntry.executeUpdate("delete from DictionaryEntry")
        Word.executeUpdate("delete from Word")

        render "Relational tables cleared successfully (lookups retained)."
    }

    // Browse all migrated entries: /migration/list (paged: ?page=2, filtered: ?q=syllabary-or-phonetic)
    def list = {
        int page = (params.page ? params.page.toInteger() : 1)
        if (page < 1) page = 1
        int pageSize = 50
        String q = params.q ? params.q.trim() : ""

        def criteria = DictionaryEntry.createCriteria()
        def entries = criteria.list(max: pageSize, offset: (page - 1) * pageSize) {
            if (q) {
                word {
                    or {
                        ilike("phonetic", "%${q}%")
                        ilike("syllabary", "%${q}%")
                        ilike("transliteration", "%${q}%")
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder()
        sb.append("<div style='font-family: sans-serif; font-size: 14px;'>")
        sb.append("<h2>Migrated Dictionary Entries (${entries.totalCount} total)</h2>")
        sb.append("<form action='/migration/list' method='GET'>")
        sb.append("<input type='text' name='q' value='${q}' placeholder='Filter by phonetic / syllabary / translit...' style='width: 350px; padding: 8px;' />")
        sb.append("<input type='submit' value='Filter' style='padding: 8px 15px; cursor: pointer;' />")
        sb.append("</form><br/>")

        if (!entries) {
            sb.append("<p><i>No entries found. Run /migration/test first.</i></p>")
        } else {
            sb.append("<table border='1' cellpadding='6' cellspacing='0'>")
            sb.append("<tr style='background: #ddd;'><th>Legacy ID</th><th>Phonetic</th><th>Syllabary</th><th>POS</th><th>Forms</th><th>Defs</th><th>Sentences</th><th></th></tr>")
            entries.each { entry ->
                int formCount = EntryWord.countByDictionaryEntry(entry)
                int defCount = Definition.countByDictionaryEntry(entry)
                int sentCount = ExampleSentence.countByDictionaryEntry(entry)
                sb.append("<tr>")
                sb.append("<td>${entry.legacyId}</td>")
                sb.append("<td>${entry.word?.phonetic}</td>")
                sb.append("<td>${entry.word?.syllabary}</td>")
                sb.append("<td>${entry.partOfSpeech?.partofspeech}</td>")
                sb.append("<td>${formCount}</td>")
                sb.append("<td>${defCount}</td>")
                sb.append("<td>${sentCount}</td>")
                sb.append("<td><a href='/migration/inspect?id=${entry.legacyId}'>inspect</a></td>")
                sb.append("</tr>")
            }
            sb.append("</table>")

            // Pager
            int totalPages = Math.max(1, (int) Math.ceil(entries.totalCount / (double) pageSize))
            sb.append("<p>Page ${page} of ${totalPages} — ")
            if (page > 1) sb.append("<a href='/migration/list?page=${page - 1}${q ? "&q=${q}" : ""}'>&larr; Prev</a> ")
            if (page < totalPages) sb.append("<a href='/migration/list?page=${page + 1}${q ? "&q=${q}" : ""}'>Next &rarr;</a>")
            sb.append("</p>")
        }
        sb.append("</div>")
        render sb.toString()
    }

    // Side-by-side comparison view: /migration/inspect?id=72205
    def inspect = {
        String legacyId = params.id
        if (!legacyId) {
            render "Please provide a legacy ID, e.g., ?id=72205"
            return
        }

        Likespreadsheets legacyRow = null
        try {
            legacyRow = Likespreadsheets.findById(legacyId.toLong())
        } catch (Exception ignored) {}

        DictionaryEntry entry = null
        try {
            entry = DictionaryEntry.findByLegacyId(legacyId)
        } catch (Exception e) {
            log.error("Error loading DictionaryEntry for legacyId ${legacyId}: ${e.message}")
        }

        StringBuilder sb = new StringBuilder()
        sb.append("<h2>Migration Comparison for Legacy ID: ${legacyId}</h2>")
        sb.append("<div style='display: flex; gap: 20px; font-family: sans-serif; font-size: 14px;'>")

        // Helper: a labeled field, hiding "(null)" noise as '(empty)'
        def field = { String label, value ->
            sb.append("<p><b>${label}:</b> ${(value == null || value.toString().trim().isEmpty()) ? '<i>(empty)</i>' : value}</p>")
        }

        // --- Column 1: Raw Legacy Monolithic Data ---
        sb.append("<div style='flex: 1; background: #f4f4f4; padding: 15px; border-radius: 5px; max-height: 900px; overflow-y: auto;'>")
        sb.append("<h3>Legacy Monolithic Data</h3>")
        if (legacyRow) {
            field("Source", legacyRow.source)
            field("Part of Speech (c)", legacyRow.partofspeechc)
            field("Category", legacyRow.category)
            sb.append("<hr/>")
            sb.append("<b>Headword:</b>")
            field("Entry (a)", legacyRow.entrya)
            field("Syllabary (b)", legacyRow.syllabaryb)
            field("Tone", legacyRow.entrytone)
            field("Translit", legacyRow.entrytranslit)
            sb.append("<hr/>")
            sb.append("<b>Definitions:</b>")
            field("Def (d)", legacyRow.definitiond)
            field("Def Large", legacyRow.definitionlarge)
            sb.append("<hr/>")
            sb.append("<b>Inflection Fields:</b>")
            field("Noun Plural (e)", legacyRow.nounadjplurale)
            field("Noun Plural Syll (f)", legacyRow.nounadjpluralsyllf)
            field("Noun Plural Tone", legacyRow.nounadjpluraltone)
            field("Noun Plural Translit", legacyRow.nounadjpluraltranslit)
            field("1st Pres (g)", legacyRow.vfirstpresg)
            field("1st Pres Syll (h)", legacyRow.vfirstpresh)
            field("1st Pres Tone", legacyRow.vfirstprestone)
            field("1st Pres Translit", legacyRow.vfirstprestranslit)
            field("3rd Past (i)", legacyRow.vthirdpasti)
            field("3rd Past Syll (j)", legacyRow.vthirdpastsyllj)
            field("3rd Past Tone", legacyRow.vthirdpasttone)
            field("3rd Past Translit", legacyRow.vthirdpasttranslit)
            field("3rd Pres (k)", legacyRow.vthirdpresk)
            field("3rd Pres Syll (l)", legacyRow.vthirdpressylll)
            field("3rd Pres Tone", legacyRow.vthirdprestone)
            field("3rd Pres Translit", legacyRow.vthirdprestranslit)
            field("2nd Imperative (m)", legacyRow.vsecondimperm)
            field("2nd Imperative Syll (n)", legacyRow.vsecondimpersylln)
            field("2nd Imperative Tone", legacyRow.vsecondimpertone)
            field("2nd Imperative Translit", legacyRow.vsecondimpertranslit)
            field("3rd Infinitive (o)", legacyRow.vthirdinfo)
            field("3rd Infinitive Syll (p)", legacyRow.vthirdinfsyllp)
            field("3rd Infinitive Tone", legacyRow.vthirdinftone)
            field("3rd Infinitive Translit", legacyRow.vthirdinftranslit)
            sb.append("<hr/>")
            sb.append("<b>Example Sentences:</b>")
            field("Sentence Query (q)", legacyRow.sentenceq)
            field("Sentence Syllabary (r)", legacyRow.sentencesyllr)
            field("Sentence English (s)", legacyRow.sentenceenglishs)
            field("Sentence Translit", legacyRow.sentencetranslit)
            sb.append("<hr/>")
            field("Etymology", legacyRow.etymology)
            field("Notes", legacyRow.notes)
            field("Cross Reference (t)", legacyRow.crossreferencet)
        } else {
            sb.append("<p style='color: red;'>Legacy row not found in Likespreadsheets table.</p>")
        }
        sb.append("</div>")

        // --- Column 2: New Relational Structure ---
        sb.append("<div style='flex: 1; background: #e9f5ff; padding: 15px; border-radius: 5px; max-height: 900px; overflow-y: auto;'>")
        sb.append("<h3>New Relational Structure</h3>")
        if (entry) {
            field("Legacy ID", entry.legacyId)
            field("Source", "${entry.source?.code} (${entry.source?.fullName})")
            field("Part of Speech", "${entry.partOfSpeech?.partofspeech} — ${entry.partOfSpeech?.longName}")
            field("Category", entry.category?.category)
            sb.append("<hr/>")

            sb.append("<b>Primary Word:</b>")
            field("Phonetic", entry.word?.phonetic)
            field("Syllabary", entry.word?.syllabary)
            field("Tone", entry.word?.tone)
            field("Translit", entry.word?.transliteration)
            field("Dialect", entry.word?.dialect ? "${entry.word.dialect.dialect} (${entry.word.dialect.name})" : "Universal")
            field("Word ID", entry.word?.id)

            sb.append("<hr/><b>Definitions:</b><ul>")
            try {
                def definitions = Definition.findAllByDictionaryEntry(entry)
                // Dialect variants hold no definitions of their own — they are shared
                // with (stored on) the primary entry. Resolve and label them.
                String sharedNote = ""
                if (!definitions && entry.legacyId ==~ /\d+-v\d+/) {
                    String primaryLegacyId = entry.legacyId.replaceAll(/-v\d+$/, "")
                    def primary = DictionaryEntry.findByLegacyId(primaryLegacyId)
                    if (primary) {
                        definitions = Definition.findAllByDictionaryEntry(primary)
                        sharedNote = " <i style='color: #666;'>(shared with primary entry ${primaryLegacyId})</i>"
                    }
                }
                sb.append("<li>")
                if (definitions) {
                    sb.append("${sharedNote}")
                    definitions.each { d ->
                        sb.append("<p><b>Raw:</b> ${d.rawDefinition}</p>")
                        sb.append("<p><b>Text:</b> ${d.definitionText}</p>")
                        sb.append("<p><b>Lemma:</b> ${d.lemma} | <b>Dialect:</b> ${d.dialect ? "${d.dialect.dialect} (${d.dialect.name})" : "Universal"} | <b>Def ID:</b> ${d.id}</p>")
                    }
                } else {
                    sb.append("<i>No definitions recorded.</i>")
                }
                sb.append("</li>")
            } catch (Exception e) {
                sb.append("<li style='color:red;'>Error loading definitions: ${e.message}</li>")
            }
            sb.append("</ul>")

            // Split entries: dialect variants (-vN sharing the primary's definition)
            // and POS sense splits (-vN each with its own definition clause + POS)
            if (entry.legacyId ==~ /\d+/) {
                def variantEntries = DictionaryEntry.findAllByLegacyIdLike("${entry.legacyId}-%").sort { it.legacyId }
                if (variantEntries) {
                    sb.append("<hr/><b>Split Entries (sense / dialect variants):</b>")
                    variantEntries.each { v ->
                        boolean isSense = (v.legacyId ==~ /\d+-v\d+/) && v.partOfSpeech?.partofspeech != entry.partOfSpeech?.partofspeech
                        String kind = v.legacyId ==~ /\d+-v\d+/ ? (isSense ? "sense" : "dialect") : "variant"
                        sb.append("<div style='margin-left: 10px; border-left: 3px solid #9cf; padding-left: 8px; margin-top: 6px;'>")
                        sb.append("<b>${v.legacyId}</b> <i>(${kind})</i> — POS: ${v.partOfSpeech?.partofspeech}")
                        field("Phonetic", v.word?.phonetic)
                        field("Syllabary", v.word?.syllabary)
                        field("Dialect", v.word?.dialect ? "${v.word.dialect.dialect} (${v.word.dialect.name})" : "Universal")
                        def vDefs = Definition.findAllByDictionaryEntry(v)
                        if (vDefs) {
                            field("Definition", vDefs.collect { it.definitionText }.join(" | "))
                        } else {
                            field("Definition", "(shared with primary)")
                        }
                        sb.append("</div>")
                    }
                }
            }

            sb.append("<b>Inflections & Forms (Grouped):</b>")
            try {
                def entryWords = EntryWord.findAllByDictionaryEntry(entry)
                def groupedForms = entryWords.groupBy { it.formType?.name }

                if (groupedForms) {
                    groupedForms.each { formTypeName, ewList ->
                        sb.append("<div style='margin-left: 10px;'><b>${formTypeName}:</b><ul>")
                        ewList.each { ew ->
                            sb.append("<li>")
                            field("Phonetic", ew.word?.phonetic)
                            field("Syllabary", ew.word?.syllabary)
                            field("Tone", ew.word?.tone)
                            field("Translit", ew.word?.transliteration)
                            field("Dialect", ew.word?.dialect ? "${ew.word.dialect.dialect} (${ew.word.dialect.name})" : "Universal")
                            field("Word ID", ew.word?.id)
                            field("EntryWord ID", ew.id)
                            sb.append("</li>")
                        }
                        sb.append("</ul></div>")
                    }
                } else {
                    sb.append("<p><i>No inflections recorded.</i></p>")
                }
            } catch (Exception e) {
                sb.append("<p style='color:red;'>Error loading inflections: ${e.message}</p>")
            }

            sb.append("<b>Example Sentences:</b><ul>")
            try {
                def sentences = ExampleSentence.findAllByDictionaryEntry(entry)
                if (sentences) {
                    sentences.each { s ->
                        sb.append("<li>")
                        field("Cherokee Query", s.sentenceQuery)
                        field("Syllabary", s.syllabary)
                        field("English", s.english)
                        field("Translit", s.transliteration)
                        field("Sentence ID", s.id)
                        sb.append("</li>")
                    }
                } else {
                    sb.append("<li><i>No example sentences found.</i></li>")
                }
            } catch (Exception e) {
                sb.append("<li style='color:red;'>Error loading sentences: ${e.message}</li>")
            }
            sb.append("</ul>")

            sb.append("<hr/>")
            field("Etymology", entry.etymology)
            field("Notes", entry.notes)
            field("Cross Reference", entry.crossReference)
        } else {
            sb.append("<p style='color: red;'>No migrated DictionaryEntry found for legacy ID ${legacyId}. Run migration test first.</p>")
        }
        sb.append("</div>")

        sb.append("</div>")
        render sb.toString()
    }
}