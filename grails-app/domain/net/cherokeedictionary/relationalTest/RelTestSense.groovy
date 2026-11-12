package net.cherokeedictionary.relationalTest

class RelTestSense {
    String definition
    String definitionLarge
    String dialect
    
    static belongsTo = [entry: RelTestDictionaryEntry, pos: RelTestPartOfSpeech, category: RelTestCategory]
    static hasMany = [forms: RelTestWordForm, examples: RelTestExampleSentence]
    
    static constraints = {
        definition nullable: true
        definitionLarge nullable: true
        pos nullable: true
        category nullable: true
        dialect nullable: true
    }
    static mapping = {
        entityName 'RelTestSense'
    }
}
