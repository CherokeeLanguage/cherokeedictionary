package net.cherokeedictionary.relationalTest

class RelTestPartOfSpeech {
    String partOfSpeech
    static constraints = {
        partOfSpeech unique: true
    }
    static mapping = {
        entityName 'RelTestPartOfSpeech'
    }
}
