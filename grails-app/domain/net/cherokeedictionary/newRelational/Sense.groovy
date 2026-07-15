package net.cherokeedictionary.newRelational;

class Sense {
    String definition
    String definitionLarge
    String partOfSpeech
    
    static belongsTo = [entry: DictionaryEntry]
    static hasMany = [categories: CategoryNew, sentences: ExampleSentence]
    static constraints = {
        definition nullable: true
        definitionLarge nullable: true
        partOfSpeech nullable: true


    }
}