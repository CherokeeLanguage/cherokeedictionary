package net.cherokeedictionary.relational

class ExampleSentence {
    String sentenceQuery
    String syllabary
    String english
    String transliteration
    DictionaryEntry dictionaryEntry
    // Sense sense // if mapped as an association

    static constraints = {
        sentenceQuery nullable: true, blank: true
        syllabary nullable: true, blank: true
        english nullable: true, blank: true
        transliteration nullable: true, blank: true
        dictionaryEntry nullable: false
        // sense nullable: true // Allows saving sentences without a direct sense link during ETL
    }
}