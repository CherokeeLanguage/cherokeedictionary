package cherokee.corpus

class NLPCorpus {

    static constraints = {
        english type: 'text'
        syllabary type: 'text'
    }

    String english
    String syllabary
    CorpusSource corpusSource
}
