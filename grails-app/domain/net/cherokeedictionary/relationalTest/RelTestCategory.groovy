package net.cherokeedictionary.relationalTest

class RelTestCategory {
    String category
    static constraints = {
        category unique: true
    }
    static mapping = {
        entityName 'RelTestCategory'
    }
}
