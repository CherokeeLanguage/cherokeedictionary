package net.cherokeedictionary.newRelational;
class Word {
    String syllabary
    String transliteration

    static hasMany = [entries: DictionaryEntry, crossReferences: Word]
    static belongsToMany = [Word]

    static constraints = {
        syllabary nullable: false
        transliteration nullable: true


    }
}