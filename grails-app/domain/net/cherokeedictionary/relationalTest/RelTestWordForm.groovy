package net.cherokeedictionary.relationalTest

class RelTestWordForm {
    String syllabary
    String transliteration
    String tone
    
    static belongsTo = [sense: RelTestSense, type: RelTestWordType]
    
    static constraints = {
        syllabary nullable: false
        transliteration nullable: true
        tone nullable: true
    }
    static mapping = {
        entityName 'RelTestWordForm'
    }
}
