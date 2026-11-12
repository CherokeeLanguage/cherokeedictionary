package net.cherokeedictionary.relationalTest

class RelTestWordType {
    String wordType
    static constraints = {
        wordType unique: true
    }
    static mapping = {
        entityName 'RelTestWordType'
    }
}
