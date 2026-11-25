package net.cherokeedictionary.relational

import net.cherokeedictionary.dictionary.Likespreadsheets

class LexiconEtlService {

    // The run itself must NOT be one big transaction: a failed row would poison
    // the Hibernate session and break the commit of everything after it
    // (org.hibernate.AssertionFailure: null id ... don't flush after an exception).
    static transactional = false

    def runMigration(List<Long> targetIds = []) {
        log.info("Starting Lexicon ETL Migration from Likespreadsheets...")
        int successCount = 0
        int errorCount = 0
        int skippedCount = 0

        def legacyRows = targetIds ? Likespreadsheets.findAllByIdInList(targetIds) : Likespreadsheets.list()

        legacyRows.each { legacy ->
            // Grammatical-reference rows ("(see Gram. I, D)") are not words — skip
            if ((legacy.definitiond ?: "") ==~ /(?i).*\(see\s+gram/) {
                skippedCount++
                log.info("Skipped legacy row ID ${legacy.id}: grammatical reference entry")
                return
            }

            // One transaction per row: failures roll back just that row and the
            // session starts clean for the next one
            Word.withTransaction { status ->
                try {
                    migrateSingleRow(legacy)
                    successCount++
                } catch (Exception e) {
                    status.setRollbackOnly()
                    errorCount++
                    log.error("Failed to migrate legacy row ID ${legacy.id}: ${e.message}", e)
                }
            }
        }

        log.info("Migration completed. Success: ${successCount}, Errors: ${errorCount}, Skipped (grammar refs): ${skippedCount}")
        return [success: successCount, errors: errorCount, skipped: skippedCount]
    }

    private void migrateSingleRow(Likespreadsheets legacy) {
        // 1. Resolve PartOfSpeech
        String rawPos = sanitizeString(legacy.partofspeechc)
        PartOfSpeech posObj = resolvePartOfSpeech(rawPos)

        // 2. Resolve Category
        Category categoryObj = resolveCategory(sanitizeString(legacy.category))

        // 3. Resolve Default Source
        net.cherokeedictionary.admin.SourceManagement defaultSource =
                net.cherokeedictionary.admin.SourceManagement.first() ?:
                        new net.cherokeedictionary.admin.SourceManagement(code: "legacy", name: "Legacy Import").save(failOnError: true)

        // 4. Get raw fields
        String rawEntry = sanitizeString(legacy.entrya) ?: sanitizeString(legacy.entrytranslit)
        String rawSyllabary = sanitizeString(legacy.syllabaryb)
        String rawDef = sanitizeString(legacy.definitiond)

        // 5. Parse out multi-dialect variants if they exist (e.g. containing both (w) and (e))
        List<VariantData> variants = parseVariants(rawEntry, rawSyllabary, rawDef)

        if (!variants) {
            // No dialect split — but numbered definitions whose clauses each carry a
            // POS tag ("1. v. t. he's shooting at him  2. v.i. he's shooting") become
            // separate entries, each with its own PartOfSpeech and definition clause
            List<Map> senses = parseSenseDefinitions(cleanText(rawDef))
            if (senses) {
                variants = senses.collect { Map s ->
                    new VariantData(entry: rawEntry, syllabary: rawSyllabary, definition: s.text, pos: s.pos, dialect: null, ownDefinition: true)
                }
            } else {
                variants << new VariantData(entry: rawEntry, syllabary: rawSyllabary, definition: rawDef, dialect: null)
            }
        }

        variants.eachWithIndex { variant, index ->
            // Variants beyond the first get a suffixed legacyId ("-v1", "-v2", ...) so a
            // re-run of the migration updates the same rows instead of colliding on the
            // unique legacyId constraint.
            String legacyRef = (index == 0) ? "${legacy.id}" : "${legacy.id}-v${index}"
            DictionaryEntry entry = DictionaryEntry.findByLegacyId(legacyRef) ?: new DictionaryEntry(legacyId: legacyRef)

            // Save Word first (linked via DictionaryEntry.word; Word itself has no entry
            // back-reference) — entry.word is nullable:false so the entry can only be
            // saved once it points at a persisted Word. A headword cell may hold several
            // comma-separated variants ("ᎠᎭᏂ, ᎠᏂ"): the first is the primary Word, the
            // rest become synonym EntryWords.
            List<String> syllabaryVariants = cellVariants(variant.syllabary)
            Word word = entry.word ?: new Word()
            word.phonetic = cleanText(variant.entry) ?: "unknown"
            word.syllabary = syllabaryVariants ? syllabaryVariants[0] : null
            word.tone = cell(legacy.entrytone)
            word.transliteration = cell(legacy.entrytranslit)
            word.dialect = variant.dialect
            word.save(failOnError: true)

            entry.word = word
            entry.partOfSpeech = variant.pos ? resolvePartOfSpeech(variant.pos) : posObj
            entry.category = categoryObj
            entry.source = defaultSource
            entry.etymology = sanitizeString(legacy.etymology)
            entry.notes = sanitizeString(legacy.notes)
            entry.crossReference = sanitizeString(legacy.crossreferencet)
            entry.save(failOnError: true)

            // Multi-variant rows: connect the sibling dialect entries. Each variant
            // keeps its own dialect-tagged Definition ("connected by the definition"),
            // and the crossReference records the sibling entry links.
            if (variants.size() > 1) {
                def siblingRefs = (0..<variants.size()).findAll { it != index }.collect { "${legacy.id}-v${it}" }
                String kind = variants.any { it.pos } ? "Sense variant" : "Dialect variant"
                String siblingLink = "${kind} of: ${legacy.id} / ${siblingRefs.join(', ')}"
                if (index == 0) siblingLink += " (primary)"
                entry.crossReference = [entry.crossReference, siblingLink].findAll { it }.join(" | ")
                entry.save(failOnError: true)
            }

            // Record the primary headword as the base form
            FormType baseForm = resolveFormType("base_entry")
            EntryWord baseEntryWord = EntryWord.findByDictionaryEntryAndFormType(entry, baseForm) ?: new EntryWord(dictionaryEntry: entry, formType: baseForm)
            baseEntryWord.word = word
            baseEntryWord.save(failOnError: true)

            // Extra comma-separated headword variants become synonym forms
            if (syllabaryVariants.size() > 1) {
                FormType synonymForm = resolveFormType("synonym")
                EntryWord.findAllByDictionaryEntryAndFormType(entry, synonymForm).each {
                    if (it.word && it.word != word) it.word.delete()
                    it.delete()
                }
                syllabaryVariants.tail().each { String syll ->
                    Word synonymWord = new Word(
                            phonetic: word.phonetic,
                            syllabary: syll,
                            tone: word.tone,
                            transliteration: word.transliteration,
                            dialect: variant.dialect
                    ).save(failOnError: true)
                    new EntryWord(dictionaryEntry: entry, formType: synonymForm, word: synonymWord).save(failOnError: true)
                }
            }

            // Migrate the six legacy inflection forms into EntryWord rows
            migrateInflections(legacy, entry)

            // Definitions are singular per legacy row: dialect variants reference the
            // primary's definition via their crossReference link — no duplicate rows.
            // Sense-split entries each carry their own (distinct) definition clause.
            if (index == 0 || variant.ownDefinition) {
                Definition.executeUpdate("delete from Definition where dictionaryEntry = :entry", [entry: entry])
                // Store the FULL definition once on the primary (rawDef includes all
                // dialect clauses like "def (w) def (e)"), so nothing is lost by not
                // duplicating definitions across variants. Sense entries store their
                // own clause instead.
                String cleanDefText = cleanText(variant.ownDefinition ? variant.definition : rawDef) ?: "No definition"

                // Numbered definitions ("1. tender 2. very young, new") are separate
                // senses — one Definition row per numbered clause
                List<String> defClauses = parseNumberedDefinitions(cleanDefText)
                defClauses.each { clause ->
                    new Definition(
                            dictionaryEntry: entry,
                            rawDefinition: cleanDefText,           // full text kept on every row for fidelity
                            definitionText: clause,
                            lemma: extractLemma(clause),
                            dialect: variant.dialect
                    ).save(failOnError: true)
                }

                // definitionlarge is the long-form/expanded definition — keep it as a
                // second row, but only on the primary (singular per legacy row)
                String rawDefLarge = (index == 0) ? sanitizeString(legacy.definitionlarge) : null
                if (rawDefLarge) {
                    String cleanDefLarge = cleanText(rawDefLarge)
                    if (cleanDefLarge && cleanDefLarge != cleanDefText && !defClauses.contains(cleanDefLarge)) {
                        new Definition(
                                dictionaryEntry: entry,
                                rawDefinition: cleanDefLarge,
                                definitionText: cleanDefLarge,
                                lemma: extractLemma(cleanDefLarge),
                                dialect: variant.dialect
                        ).save(failOnError: true)
                    }
                }
            }

            // Save Example Sentences (only on the first variant — the sentence belongs
            // to the legacy row, not each dialect variant)
            if (index == 0) {
                ExampleSentence.executeUpdate("delete from ExampleSentence where dictionaryEntry = :entry", [entry: entry])
                String rawExample = sanitizeString(legacy.sentenceq)
                if (rawExample) {
                    new ExampleSentence(
                            dictionaryEntry: entry,
                            sentenceQuery: rawExample,
                            syllabary: sanitizeString(legacy.sentencesyllr) ?: rawExample,
                            english: sanitizeString(legacy.sentenceenglishs) ?: "",
                            transliteration: sanitizeString(legacy.sentencetranslit)
                    ).save(failOnError: true)
                }
            }
        }
    }

    // Splits "1. tender 2. very young, new" into ["tender", "very young, new"].
    // Text without numbering comes back as a single-clause list. Any text before
    // the first number (rare) is kept as its own clause so nothing is dropped.
    private List<String> parseNumberedDefinitions(String definitionText) {
        if (!definitionText) return ["No definition"]

        if (!(definitionText =~ /(?:^|[\s;])\d+\s*\.\s/)) return [definitionText]

        List<String> clauses = definitionText.split(/(?:^|[\s;])\d+\s*\.\s+/)
                .collect { cleanText(it) }
                .findAll { it }
        return clauses ?: [definitionText]
    }

    // Returns one [pos, text] map per numbered sense clause ONLY when the definition
    // is numbered AND every clause starts with a POS token, e.g.
    // "1. v. t. he's shooting at him  2. v.i. he's shooting"
    //   -> [[pos:'vt', text:"he's shooting at him"], [pos:'vi', text:"he's shooting"]]
    // Otherwise returns [] (definition stays whole on one entry).
    private List<Map> parseSenseDefinitions(String cleanDef) {
        if (!cleanDef) return []

        List<String> clauses = parseNumberedDefinitions(cleanDef)
        if (clauses.size() < 2) return []

        List<Map> senses = []
        for (String clause in clauses) {
            Map parsed = parsePosToken(clause)
            if (!parsed) return [] // any untagged clause -> keep the definition whole
            senses << parsed
        }
        return senses
    }

    // Detects a leading POS token ("n.", "adj.", "adv.", "v.", "v.t.", "v. t.", "v.i.")
    // and returns [pos: normalizedCode, text: clauseWithoutToken], or null
    private Map parsePosToken(String clause) {
        def m = clause =~ /^\s*(v\s*\.\s*t|v\s*\.\s*i|adj|adv|n|v)\s*\.?\s+(.*)$/
        if (!m.matches()) return null

        String code = m.group(1).replaceAll(/[.\s]/, "").toLowerCase()   // "v. t." -> "vt"
        String text = m.group(2).trim()
        if (!text) return null
        return [pos: code, text: text]
    }

    // One EntryWord (+ Word) per distinct legacy inflection form. Cells may hold
    // several comma-separated variants ("ᎯᏯᏓᏱᎦ, ᎭᏓᏴᎦ"); "-" / "-----" mean no form.
    private void migrateInflections(Likespreadsheets legacy, DictionaryEntry entry) {
        def inflectionMap = [
                noun_plural    : [legacy.nounadjplurale, legacy.nounadjpluralsyllf, legacy.nounadjpluraltone, legacy.nounadjpluraltranslit],
                v_first_pres   : [legacy.vfirstpresg, legacy.vfirstpresh, legacy.vfirstprestone, legacy.vfirstprestranslit],
                v_third_past   : [legacy.vthirdpasti, legacy.vthirdpastsyllj, legacy.vthirdpasttone, legacy.vthirdpasttranslit],
                v_third_pres   : [legacy.vthirdpresk, legacy.vthirdpressylll, legacy.vthirdprestone, legacy.vthirdprestranslit],
                v_second_imper : [legacy.vsecondimperm, legacy.vsecondimpersylln, legacy.vsecondimpertone, legacy.vsecondimpertranslit],
                v_third_inf    : [legacy.vthirdinfo, legacy.vthirdinfsyllp, legacy.vthirdinftone, legacy.vthirdinftranslit],
        ]

        inflectionMap.each { formTypeName, values ->
            List<String> phonetics = cellVariants(values[0])
            List<String> syllabaries = cellVariants(values[1])
            if (!phonetics && !syllabaries) return // empty column for this row

            String tone = cell(values[2])
            String translit = cell(values[3])

            FormType formType = resolveFormType(formTypeName)
            // Refresh on re-migration: drop this entry's existing forms of this type
            EntryWord.findAllByDictionaryEntryAndFormType(entry, formType).each {
                if (it.word) it.word.delete()
                it.delete()
            }

            int variantCount = Math.max(phonetics.size(), syllabaries.size())
            (0..<variantCount).each { int i ->
                new Word(
                        phonetic: cleanText(phonetics[i] ?: phonetics[0]),
                        syllabary: cleanText(syllabaries[i] ?: syllabaries[0]),
                        tone: tone,
                        transliteration: translit
                ).save(failOnError: true).with { newWord ->
                    new EntryWord(dictionaryEntry: entry, formType: formType, word: newWord).save(failOnError: true)
                }
            }
        }
    }

    private Category resolveCategory(String categoryString) {
        if (!categoryString) return null

        Category category = Category.findByCategoryIlike(categoryString) ?: Category.findByNameIlike(categoryString)
        if (category) return category

        return new Category(category: categoryString, name: categoryString).save(failOnError: true)
    }

    private List<VariantData> parseVariants(String entryText, String syllabaryText, String definitionText) {
        List<VariantData> list = []

        // The headword text is authoritative for the split, e.g. "ugodida (w) agadida (e)"
        // splits into two variants: ugodida = Otali (w/O), agadida = Giduwah (e/G).
        def entryChunks = splitByTag(entryText)
        if (!entryChunks) return list

        def syllChunks = splitByTag(syllabaryText)
        def defChunks = splitByTag(definitionText)

        entryChunks.each { chunk ->
            String dialectCode = (chunk.tag == 'w') ? 'O' : 'G'
            // Prefer the syllabary/definition chunk carrying the same dialect tag;
            // fall back to the shared text when it isn't tagged
            String syllabary = syllChunks.find { it.tag == chunk.tag }?.text ?: syllabaryText
            String definition = defChunks.find { it.tag == chunk.tag }?.text ?: definitionText

            list << new VariantData(
                    entry: chunk.text,
                    syllabary: syllabary,
                    definition: definition,
                    dialect: resolveDialect(dialectCode)
            )
        }

        return list
    }

    // Splits "text1 (w) text2 (e)" into [[text:'text1', tag:'w'], [text:'text2', tag:'e']].
    // Returns [] when the text carries no dialect tags at all.
    private List<Map> splitByTag(String text) {
        if (!text) return []
        def matcher = text =~ /(.+?)\s*\((w|e)\)/
        List<Map> chunks = []
        while (matcher.find()) {
            String part = cleanText(matcher.group(1))
            if (part) chunks << [text: part, tag: matcher.group(2).toLowerCase()]
        }
        return chunks
    }

    private String cleanText(String input) {
        if (!input) return null
        return input.replaceAll(/\((w|e)\)/, "").replaceAll(/\s+/, " ").trim()
    }

    private PartOfSpeech resolvePartOfSpeech(String posString) {
        String cleaned = posString ? posString.trim() : "unknown"
        if (!cleaned) cleaned = "unknown"

        PartOfSpeech pos = PartOfSpeech.findByPartofspeechIlike(cleaned) ?: PartOfSpeech.findByLongNameIlike(cleaned)
        if (pos) return pos

        PartOfSpeech newPos = new PartOfSpeech(
                partofspeech: cleaned,
                longName: cleaned
        )
        newPos.save(failOnError: true)
        return newPos
    }

    // Dialect codes ('O', 'G', 'S', ...) live in Dialect.dialect; seeds come from BootStrap
    private Dialect resolveDialect(String dialectCode) {
        Dialect dialect = Dialect.findByDialect(dialectCode)
        if (dialect) return dialect

        Dialect newDialect = new Dialect(dialect: dialectCode, name: dialectCode)
        newDialect.save(failOnError: true)
        return newDialect
    }

    private FormType resolveFormType(String name) {
        FormType formType = FormType.findByName(name)
        if (formType) return formType

        return new FormType(name: name, description: "Auto-seeded by ETL").save(failOnError: true)
    }

    private String extractLemma(String text) {
        if (!text) return ""

        List<String> tokens = text.toLowerCase()
                .replaceAll(/[^a-zA-Z\s]/, '')
                .tokenize()

        if (!tokens) return text

        // Pronouns/auxiliaries plus intensity adverbs ("very young" should lemma to
        // "young", not "very")
        Set<String> skipWords = ['hes', 'shes', 'its', 'theyre', 'im', 'youre', 'were',
                                 'to', 'a', 'an', 'the', 'is', 'are',
                                 'very', 'quite', 'extremely', 'really', 'too', 'so', 'most']
        String targetWord = tokens.find { !skipWords.contains(it) } ?: tokens.first()

        if (targetWord.endsWith("ing") && targetWord.length() > 4) {
            String stem = targetWord.substring(0, targetWord.length() - 3)
            if (stem.endsWith("cl") || stem.endsWith("gl") || stem.endsWith("bl") || stem.endsWith("fl") || stem.endsWith("pl") || stem.endsWith("sl")) {
                return stem + "e"
            }
            return stem.endsWith("c") ? stem + "le" : stem
        }
        if (targetWord.endsWith("ed") && targetWord.length() > 3) {
            String stem = targetWord.substring(0, targetWord.length() - 2)
            if (stem.endsWith("cl") || stem.endsWith("gl") || stem.endsWith("bl") || stem.endsWith("fl") || stem.endsWith("pl") || stem.endsWith("sl")) {
                return stem + "e"
            }
            return stem.endsWith("c") ? stem + "le" : stem
        }
        if (targetWord.endsWith("s") && targetWord.length() > 3 && !targetWord.endsWith("ss")) {
            return targetWord.substring(0, targetWord.length() - 1)
        }
        return targetWord
    }

    private String sanitizeString(String input) {
        if (!input) return null
        String trimmed = input.trim()
        return trimmed.isEmpty() ? null : trimmed
    }

    // Legacy cells use "-" and "-----" as "no form" placeholders; those must
    // never be stored as literal values
    private String cell(String input) {
        String s = sanitizeString(input)
        if (!s) return null
        return (s ==~ /-+/) ? null : s
    }

    // Splits a comma-separated cell ("ᎯᏯᏓᏱᎦ, ᎭᏓᏴᎦ") into distinct form variants,
    // dropping placeholders and duplicates (rows sometimes repeat the same form)
    private List<String> cellVariants(String input) {
        String s = cell(input)
        if (!s) return []
        return s.split(/\s*,\s*/).collect { cleanText(it) }.findAll { it }.unique()
    }

    private static class VariantData {
        String entry
        String syllabary
        String definition
        Dialect dialect
        String pos          // POS token parsed from a numbered sense clause ("vt", "n", ...)
        boolean ownDefinition = false  // sense entries carry their own definition clause;
                                       // dialect variants share the primary's definition
    }
}