package net.cherokeedictionary.newRelational;

class AudioLinkNew {
    String filePath
    String description

    static belongsTo = [entry: DictionaryEntry]

    static constraints = {
        filePath nullable: true
        description nullable: true
    }
}