package net.cherokeedictionary.audio

import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * MCO Audio Lookup - named this way because MCOAudioLookup wouldn't work in the database
 * This class has a pk of pronunciation and voice
 *
 */
class AudioLookup implements Serializable {
    static constraints = {
    }

    //https://docs.grails.org/latest/ref/Database%20Mapping/id.html
    static mapping = {
        id composite:['mcoPronunciation', 'voice']
        version false
    }

    String mcoPronunciation
    String mcoTranslit
    String gender
    String voice
    String audioFile
    String duration

    def getPK() {
        ["mcoPronunciation":mcoPronunciation, "voice":voice]
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append mcoPronunciation
        builder.append mcoTranslit
        builder.append gender
        builder.append voice
        builder.append audioFile
        builder.append duration
        builder.toHashCode()
    }

    boolean equals(other) {
        if (!(other instanceof AudioLookup)) {
            return false
        }

        (other.mcoPronunciation == mcoPronunciation
            && other.mcoTranslit == mcoTranslit
            && other.gender == gender
            && other.voice == voice
            && other.audioFile == audioFile
            && other.duration == duration)
    }

    @Override
    String toString() {
        "${mcoPronunciation} - ${voice}"
    }
}
