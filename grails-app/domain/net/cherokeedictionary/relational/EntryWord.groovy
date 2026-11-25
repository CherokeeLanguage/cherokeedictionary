package net.cherokeedictionary.relational

class EntryWord {
    static belongsTo = [
        dictionaryEntry: DictionaryEntry,
        word: Word,
        formType: FormType // Identifies if this word is a plural, past tense, etc.
    ]

    static constraints = {
        dictionaryEntry nullable: false
        word nullable: false
        formType nullable: false
    }
}