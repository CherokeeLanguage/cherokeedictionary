package net.cherokeedictionary.relational

class Word {
    String syllabary
    String phonetic
    String tone
    String transliteration
    Dialect dialect   // plain lookup reference; nullable = universal across dialects

    // Unidirectional belongsTo: the DictionaryEntry owns the Word (cascade delete),
    // linked via DictionaryEntry.word — Word has no back-reference property.
    static belongsTo = DictionaryEntry

    static constraints = {
        syllabary nullable: true
        // nullable: some legacy rows record a form only as syllabary (no phonetic spelling)
        phonetic nullable: true, blank: false
        tone nullable: true
        transliteration nullable: true
        dialect nullable: true
    }
}