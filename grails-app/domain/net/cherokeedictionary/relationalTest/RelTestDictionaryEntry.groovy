package net.cherokeedictionary.relationalTest

import net.cherokeedictionary.admin.SourceManagement

class RelTestDictionaryEntry {
    SourceManagement source
    String etymology
    String notes
    
    static hasMany = [senses: RelTestSense]
    
    static constraints = {
        source nullable: true
        etymology nullable: true
        notes nullable: true
    }
    static mapping = {
        entityName 'RelTestDictionaryEntry'
    }
}
