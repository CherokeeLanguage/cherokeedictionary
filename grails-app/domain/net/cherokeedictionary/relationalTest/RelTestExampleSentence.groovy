package net.cherokeedictionary.relationalTest

class RelTestExampleSentence {
    String syllabary
    String transliteration
    String english
    
    static belongsTo = [sense: RelTestSense]
    
    static constraints = {
        syllabary nullable: true
        transliteration nullable: true
        english nullable: true
    }
    static mapping = {
        entityName 'RelTestExampleSentence'
    }
}
