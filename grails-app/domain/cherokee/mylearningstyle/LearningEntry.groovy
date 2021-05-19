package cherokee.mylearningstyle

class LearningEntry {
    static constraints = {
    }

    String syllabary
    String phonetic
    String english
    String sourceInformation
    String audioLink

    String toString() {
        return english
    }
}
