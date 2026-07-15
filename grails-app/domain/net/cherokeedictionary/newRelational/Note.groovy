package net.cherokeedictionary.newRelational;

class Note {
    String content
    Date dateAdded

    static belongsTo = [entry: DictionaryEntry]
    static constraints = {
        content nullable: false
        dateAdded nullable: true

    }
}