package net.cherokeedictionary.relational

import net.cherokeedictionary.admin.SourceManagement

class DictionaryEntry {
    String legacyId            // Stores the original monolithic table ID / spreadsheet ID
    
    // Core Relationships
    Word word                  // The primary headword
    PartOfSpeech partOfSpeech  
    Category category
    SourceManagement source    

    // Metadata & Notes
    String etymology
    String notes
    String crossReference      

    // Collections
    static hasMany = [
        definitions: Definition,
        entryWords: EntryWord,
        sentences: ExampleSentence
    ]

    static constraints = {
        legacyId nullable: true, unique: true // Ensures fast, error-free lookups
        word nullable: false
        partOfSpeech nullable: false
        category nullable: true
        source nullable: false
        etymology nullable: true
        notes nullable: true
        crossReference nullable: true
    }
}