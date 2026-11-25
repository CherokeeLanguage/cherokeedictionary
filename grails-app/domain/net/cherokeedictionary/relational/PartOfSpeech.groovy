package net.cherokeedictionary.relational

class PartOfSpeech {
    String partofspeech
    String longName

    static mapping = {
        table 'part_of_speech'
        partofspeech column: 'partofspeech'
        longName column: 'long_name'
    }

    static constraints = {
        partofspeech blank: false, unique: true
        longName nullable: true, blank: true
    }
}