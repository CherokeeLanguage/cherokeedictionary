package net.cherokeedictionary.relational

class LemmaSearchController {

    def index = {
        String query = params.q ?: ""
        if (!query) {
            render "<h2>English Lemma Search & Tester</h2>" +
                    "<form action='/lemmaSearch' method='GET'>" +
                    "<input type='text' name='q' placeholder='Enter English word (e.g. circling, turning)...' style='width: 350px; padding: 8px; font-size: 14px;' />" +
                    "<input type='submit' value='Search Lemma' style='padding: 8px 15px; font-size: 14px; cursor: pointer;' />" +
                    "</form>"
            return
        }

        // 1. Compute lemma using the exact same normalization logic as LexiconEtlService
        String computedLemma = extractLemma(query)

        // 2. Query relational database tables for matching lemmas
        List<Definition> matchingDefinitions = Definition.findAllByLemmaIlike(computedLemma)

        // Fallback diagnostic: if none found, collect all definitions to inspect what's stored
        List<Definition> allDefinitions = []
        if (!matchingDefinitions) {
            allDefinitions = Definition.list()
        }

        Set<DictionaryEntry> entries = matchingDefinitions.collect { it.dictionaryEntry }.toSet()

        // 3. Render results interface
        StringBuilder sb = new StringBuilder()
        sb.append("<h2>Lemma Search Results for: '${query}'</h2>")
        sb.append("<p><b>Computed Lemma:</b> <code style='background: #eee; padding: 3px 6px; border-radius: 3px;'>${computedLemma}</code></p>")
        sb.append("<p><b>Matching Definitions Found:</b> ${matchingDefinitions.size()}</p>")
        sb.append("<hr/>")

        entries.each { entry ->
            sb.append("<div style='background: #f9f9f9; border: 1px solid #ddd; padding: 15px; margin-bottom: 15px; border-radius: 5px; font-family: sans-serif;'>")
            sb.append("<h4>Dictionary Entry (Legacy ID: ${entry.legacyId})</h4>")

            String primaryPhonetic = "[Missing]"
            try { primaryPhonetic = entry.word?.phonetic } catch(e) {}
            String primarySyllabary = ""
            try { primarySyllabary = entry.word?.syllabary } catch(e) {}

            sb.append("<p><b>Primary Headword:</b> ${primaryPhonetic} (${primarySyllabary})</p>")

            sb.append("<b>Definitions:</b><ul>")
            entry.definitions.each { d ->
                boolean isMatch = d.lemma?.equalsIgnoreCase(computedLemma)
                String highlight = isMatch ? "style='background: #e2f0d9; padding: 2px 4px; border-radius: 3px;'" : ""
                sb.append("<li ${isMatch ? highlight : ''}>${d.rawDefinition} (<i>Lemma: <b>${d.lemma}</b></i>)</li>")
            }
            sb.append("</ul>")

            sb.append("<b>Example Sentences:</b><ul>")
            def sentences = ExampleSentence.findAllByDictionaryEntry(entry)
            if (sentences) {
                sentences.each { s ->
                    sb.append("<li><b>Cherokee:</b> ${s.sentenceQuery} (${s.syllabary})<br/><b>English:</b> ${s.english}</li>")
                }
            } else {
                sb.append("<li><i>None recorded.</i></li>")
            }
            sb.append("</ul>")
            sb.append("</div>")
        }

        if (!entries) {
            sb.append("<p style='color: red;'>No relational dictionary entries found matching lemma '${computedLemma}'.</p>")
            sb.append("<h4>Diagnostic - All Definitions Currently in Database:</h4><ul>")
            allDefinitions.each { d ->
                sb.append("<li>Raw Def: '${d.rawDefinition}' | Stored Lemma: '<b>${d.lemma}</b>'</li>")
            }
            sb.append("</ul>")
        }

        sb.append("<br/><a href='/lemmaSearch'>&larr; Back to Search</a>")
        render sb.toString()
    }

    // Shared lemma extraction and normalization logic
    private String extractLemma(String text) {
        if (!text) return ""

        List<String> tokens = text.toLowerCase()
                .replaceAll(/[^a-zA-Z\s]/, '')
                .tokenize()

        if (!tokens) return text

        // Kept in sync with LexiconEtlService.extractLemma — intensity adverbs skip
        // so "very young" lemmas to "young", not "very"
        Set<String> skipWords = ['hes', 'shes', 'its', 'theyre', 'im', 'youre', 'were',
                                 'to', 'a', 'an', 'the', 'is', 'are',
                                 'very', 'quite', 'extremely', 'really', 'too', 'so', 'most']
        String targetWord = tokens.find { !skipWords.contains(it) } ?: tokens.first()

        // Clean rule-based normalization matching ETL pipeline
        if (targetWord.endsWith("ing") && targetWord.length() > 4) {
            String stem = targetWord.substring(0, targetWord.length() - 3)
            // If stem ends in a consonant cluster like 'cl', 'gl', 'bl', etc., add 'e'
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
}