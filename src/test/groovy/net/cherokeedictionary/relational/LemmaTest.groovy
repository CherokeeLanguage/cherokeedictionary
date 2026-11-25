package net.cherokeedictionary.relational

import net.didion.jwnl.data.IndexWord

private String extractLemma(String text) {
    if (!text) return ""

    try {
        Dictionary jwnlDict = Dictionary.getInstance()
        List<String> tokens = text.toLowerCase().replaceAll(/[^a-zA-Z\s]/, '').tokenize()
        if (!tokens) return text

        Set<String> skipWords = ['hes', 'shes', 'its', 'theyre', 'im', 'youre', 'were', 'to', 'a', 'an', 'the', 'is', 'are']
        String targetWord = tokens.find { !skipWords.contains(it) } ?: tokens.first()

        if (jwnlDict) {
            // 1. Try direct index lookup
            IndexWord indexWord = jwnlDict.lookupIndexWord(POS.VERB, targetWord) ?:
                    jwnlDict.lookupIndexWord(POS.NOUN, targetWord)
            if (indexWord) return indexWord.lemma

            // 2. Fallback to JWNL Morphy suffix stripping/stemming
            Morphy morphy = jwnlDict.getMorphy()
            if (morphy) {
                IndexWord baseWord = morphy.lookupBaseForm(POS.VERB, targetWord) ?:
                        morphy.lookupBaseForm(POS.NOUN, targetWord)
                if (baseWord) return baseWord.lemma
            }
        }

        return targetWord
    } catch (Exception e) {
        return text.toLowerCase().tokenize().first()
    }
}