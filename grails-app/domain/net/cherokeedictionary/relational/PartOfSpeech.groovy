package net.cherokeedictionary.relational

class PartOfSpeech {

    static constraints = {
    }

    String partofspeech
    String longName

    public String toString() {
        return longName
    }
}
