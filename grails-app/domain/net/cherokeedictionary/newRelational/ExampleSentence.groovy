package net.cherokeedictionary.newRelational;

class ExampleSentence {
    String syllabary
    String transliteration
    String english

    // Change this to link to Sense
    static belongsTo = [sense: Sense]

    static constraints = {
        syllabary nullable: true
        transliteration nullable: true
        english nullable: true
        sense nullable: false // Ensures every sentence has a sense
    }
}