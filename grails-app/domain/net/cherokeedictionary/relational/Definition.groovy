package net.cherokeedictionary.relational

class Definition {
    String rawDefinition
    String definitionText
    String lemma
    Dialect dialect
    DictionaryEntry dictionaryEntry

    static mapping = {
        table 'definition'
        definitionText column: 'definition_text'
        rawDefinition column: 'raw_definition'
        lemma column: 'lemma', index: 'definition_lemma_idx'
    }

    static constraints = {
        rawDefinition nullable: true, blank: true
        definitionText nullable: true, blank: true
        lemma nullable: true, blank: true
        dialect nullable: true
        dictionaryEntry nullable: false
    }
}