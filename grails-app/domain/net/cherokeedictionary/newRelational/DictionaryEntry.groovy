package net.cherokeedictionary.newRelational;

class DictionaryEntry {
    String definition = ""
    String definitionLarge = ""
    String partOfSpeech = ""
    String source = ""
    String etymology = ""

    // Optional: Only if you still want a Verb associated here,
    // but usually, verbs belong to the Word or are separate.
    Verb verbDetails

    static belongsTo = [word: Word]
    static hasMany = [senses: Sense, notes: Note, audioLinks: AudioLinkNew]

    static constraints = {
        definition nullable: true
        definitionLarge nullable: true
        partOfSpeech nullable: true
        source nullable: true
        etymology nullable: true
        verbDetails nullable: true // Added this based on your error
    }
}