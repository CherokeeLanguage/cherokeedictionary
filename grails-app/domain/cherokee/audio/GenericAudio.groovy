package cherokee.audio

class GenericAudio {

    static constraints = {
        english()
        phonetic()
        syllabary()
        audioLink()
        sourceInformation()
        source()
    }

    String syllabary
    String phonetic
    String english
    String sourceInformation
    String source
    AudioFile audioLink

    String toString() {
        return english
    }
}
