package net.cherokeedictionary.newRelational;

class Verb {
    String root
    String aspectSuffix
    String setType

    static belongsTo = [entry: DictionaryEntry, tense: VerbTense]
    static constraints = {
        root nullable: true
        aspectSuffix nullable: true
        setType nullable: true
    }
}