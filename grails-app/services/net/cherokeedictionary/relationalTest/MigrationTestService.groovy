package net.cherokeedictionary.relationalTest

import net.cherokeedictionary.dictionary.Likespreadsheets

class MigrationTestService {

    /** Treat literal "NULL"/blank strings as empty for display + parsing decisions. */
    String clean(String value) {
        if (value == null) return ''
        String trimmed = value.trim()
        return (trimmed.equalsIgnoreCase('NULL') || trimmed == '~') ? '' : trimmed
    }

    /**
     * Split a comma-delimited form column into its individual values.
     * E.g. "jiyadayiha, gadayiha" -> ["jiyadayiha", "gadayiha"]
     */
    List splitFormValues(String value) {
        value ? value.split(/\s*,\s*/).collect { it.trim() }.findAll { it } : []
    }

    /**
     * Expand one form-triplet column group into one WordForm per comma-delimited entry,
     * aligning latin / syllabary / translit / tone by position (shorter lists pad with '').
     */
    void addForms(List target, String type, String latin, String syllabary,
                          String translit, String tone) {
        List latinParts    = splitFormValues(latin)
        List syllabaryParts = splitFormValues(syllabary)
        List translitParts  = splitFormValues(translit)
        List toneParts      = splitFormValues(tone)

        int count = [latinParts.size(), syllabaryParts.size(), translitParts.size(), toneParts.size()].max()
        count.times { i ->
            target << [
                type    : type,
                latin   : (i < latinParts.size())    ? latinParts[i]       : '',
                syllabary: (i < syllabaryParts.size()) ? syllabaryParts[i] : '',
                translit: (i < translitParts.size())  ? translitParts[i]   : '',
                tone    : (i < toneParts.size())      ? toneParts[i]       : ''
            ]
        }
    }

    def parseRow(Likespreadsheets row) {
        String rawDefinition = clean(row.definitiond)
        String rawDefinitionLarge = clean(row.definitionlarge)

        // ---- Split definitiond into Sense records ("1. X 2. Y" -> 2 senses) ----
        List senses = []
        if (rawDefinition) {
            String[] parts = rawDefinition.split(/\d+\./)
            parts.each { part ->
                if (part.trim()) {
                    String cleanPart = part.trim()
                    String dialect = ''
                    senses << [
                        definition     : cleanPart,
                        definitionLarge: rawDefinitionLarge,
                        pos            : clean(row.partofspeechc),
                        category       : clean(row.category)
                    ]
                }
            }
        }
        if (!senses) {
            senses << [definition: '(no definition source)', definitionLarge: rawDefinitionLarge,
                       pos: clean(row.partofspeechc), category: clean(row.category)]
        }

        // ---- Candidate WordForm records: every form-triplet column group ----
        // Comma-delimited values (e.g. vfirstpresg: "jiyadayiha, gadayiha") mean
        // multiple distinct forms of the same type; split them positionally so
        // latin[i] / syllabary[i] / translit[i] / tone[i] stay aligned as one record.
        List wordForms = []
        addForms(wordForms, 'Entry (headword)',
            clean(row.entrya), clean(row.syllabaryb), clean(row.entrytranslit), clean(row.entrytone))
        addForms(wordForms, 'noun/adj plural',
            clean(row.nounadjplurale), clean(row.nounadjpluralsyllf),
            clean(row.nounadjpluraltranslit), clean(row.nounadjpluraltone))
        addForms(wordForms, 'verb 1st person present',
            clean(row.vfirstpresg), clean(row.vfirstpresh),
            clean(row.vfirstprestranslit), clean(row.vfirstprestone))
        addForms(wordForms, 'verb 3rd person past',
            clean(row.vthirdpasti), clean(row.vthirdpastsyllj),
            clean(row.vthirdpasttranslit), clean(row.vthirdpasttone))
        addForms(wordForms, 'verb 3rd person present',
            clean(row.vthirdpresk), clean(row.vthirdpressylll),
            clean(row.vthirdprestranslit), clean(row.vthirdprestone))
        addForms(wordForms, 'verb 2nd person imperative',
            clean(row.vsecondimperm), clean(row.vsecondimpersylln),
            clean(row.vsecondimpertranslit), clean(row.vsecondimpertone))
        addForms(wordForms, 'verb 3rd person infinitive',
            clean(row.vthirdinfo), clean(row.vthirdinfsyllp),
            clean(row.vthirdinftranslit), clean(row.vthirdinftone))

        return [
            // ---- legacy row header, for the approval banner ----
            legacy: [
                entrya   : clean(row.entrya),
                syllabary: clean(row.syllabaryb),
                pos      : clean(row.partofspeechc),
                source   : clean(row.source)
            ],

            // ---- RelTestDictionaryEntry fields ----
            dictionaryEntry: [
                source        : clean(row.source),
                etymology     : clean(row.etymology),
                notes         : clean(row.notes),
                crossReference: clean(row.crossreferencet)
            ],

            // ---- Lookup values this row would create/use ----
            partOfSpeech: clean(row.partofspeechc),
            category    : clean(row.category),

            // ---- RelTestSense records (one per split) ----
            senses: senses,

            // ---- RelTestWordForm records ----
            wordForms: wordForms,

            // ---- RelTestExampleSentence fields ----
            exampleSentence: [
                syllabary      : clean(row.sentencesyllr),
                transliteration: clean(row.sentencetranslit),
                english        : clean(row.sentenceenglishs),
                legacyNote     : clean(row.sentenceq)
            ],

            // ---- raw unparsed source, for side-by-side check ----
            rawDefinition: rawDefinition
        ]
    }
}
