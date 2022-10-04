package net.cherokeedictionary.relational

import net.cherokeedictionary.dictionary.Likespreadsheets

class RelationalController {


    def index() {
        processEntries()
        processSentences()

        def lks = Likespreadsheets.findById(73250)
        def wordLikespreadsheetsLink = WordLikespreadsheetsLink.findAllByLikespreadsheets(lks)

        render(view:'index', model:[lst: wordLikespreadsheetsLink])
    }

    /**
     * Takes a Word object and likespreadsheets and saves the link between them as well as verify if we can save
     *     and if not then show errors
     */
    def saveIt(element, Likespreadsheets lks) {
        if (element.canSave()) {
            if (!element.save()) {
                element.errors.each {
                    println it
                }
            } else {
                def wordLikespreadsheetsLink = new WordLikespreadsheetsLink()
                wordLikespreadsheetsLink.likespreadsheets = lks
                wordLikespreadsheetsLink.word = element
                wordLikespreadsheetsLink.save()
            }
        }
    }

    def processSentences() {
        List<Likespreadsheets> list = Likespreadsheets.findAll()
        list.each {
            def sentence = new Sentence()
            sentence.translation = it.sentenceenglishs
            sentence.phonetic = it.sentenceq
            sentence.syllabary = it.sentencesyllr
            sentence.autotransliteration = it.sentencetranslit

            if (sentence.canSave()) {
                if (!sentence.save()) {
                    sentence.errors.each {
                        println it
                    }
                } else {
                    def sentenceLikespreadsheetsLink = new SentenceLikespreadsheetsLink()
                    sentenceLikespreadsheetsLink.likespreadsheets = it
                    sentenceLikespreadsheetsLink.sentence = sentence
                }
            }
        }
    }

    /**
     * iterate over likespreadsheets
     * split the entries logically into words
     * save those words as entries in the Word table
     *
     * @return
     */
    private processEntries() {
        List<Likespreadsheets> list = Likespreadsheets.findAll()//ById(73250)
        list.each {
            //select source and then determine what orthography type to put in
            def entryWord = new Word()
            entryWord.syllabary = it.syllabaryb
            entryWord.phonetic = it.entrya
            entryWord.originalOrthography = new Orthography(orthography:it.entrytone, orthographyType:OrthographyType.findById(1))
            entryWord.autotransliteration = it.entrytranslit
            entryWord.wordType = WordType.findById(1)

            def nounAdjWord = new Word()
            nounAdjWord.syllabary = it.nounadjpluralsyllf
            nounAdjWord.phonetic = it.nounadjplurale
            nounAdjWord.originalOrthography = new Orthography(orthography:it.nounadjpluraltone, orthographyType:OrthographyType.findById(1))
            nounAdjWord.autotransliteration = it.nounadjpluraltranslit
            nounAdjWord.wordType = WordType.findById(2)

            def vFirstPresWord = new Word()
            vFirstPresWord.syllabary = it.vfirstpresh
            vFirstPresWord.phonetic = it.vfirstpresg
            vFirstPresWord.originalOrthography = new Orthography(orthography:it.vfirstprestone, orthographyType:OrthographyType.findById(1))
            vFirstPresWord.autotransliteration = it.vfirstprestranslit
            vFirstPresWord.wordType = WordType.findById(3)

            def vThirdPastWord = new Word()
            vThirdPastWord.syllabary = it.vthirdpastsyllj
            vThirdPastWord.phonetic = it.vthirdpasti
            vThirdPastWord.originalOrthography = new Orthography(orthography:it.vthirdpasttone, orthographyType:OrthographyType.findById(1))
            vThirdPastWord.autotransliteration = it.vthirdpasttranslit
            vThirdPastWord.wordType = WordType.findById(4)

            def vThirdPresWord = new Word()
            vThirdPresWord.syllabary = it.vthirdpressylll
            vThirdPresWord.phonetic = it.vthirdpresk
            vThirdPresWord.originalOrthography = new Orthography(orthography:it.vthirdprestone, orthographyType:OrthographyType.findById(1))
            vThirdPresWord.autotransliteration = it.vthirdprestranslit
            vThirdPresWord.wordType = WordType.findById(5)

            def vSecondImperWord = new Word()
            vSecondImperWord.syllabary = it.vsecondimpersylln
            vSecondImperWord.phonetic = it.vsecondimperm
            vSecondImperWord.originalOrthography = new Orthography(orthography:it.vsecondimpertone, orthographyType:OrthographyType.findById(1))
            vSecondImperWord.autotransliteration = it.vsecondimpertranslit
            vSecondImperWord.wordType = WordType.findById(6)

            def vThirdInfWord = new Word()
            vThirdInfWord.syllabary = it.vthirdinfsyllp
            vThirdInfWord.phonetic = it.vthirdinfo
            vThirdInfWord.originalOrthography = new Orthography(orthography:it.vthirdinftone, orthographyType:OrthographyType.findById(1))
            vThirdInfWord.autotransliteration = it.vthirdinftranslit
            vThirdInfWord.wordType = WordType.findById(7)

            saveIt(entryWord, it)
            saveIt(nounAdjWord, it)
            saveIt(vFirstPresWord, it)
            saveIt(vThirdPastWord, it)
            saveIt(vThirdPresWord, it)
            saveIt(vSecondImperWord, it)
            saveIt(vThirdInfWord, it)
            saveIt(vThirdPresWord, it)
            saveIt(vThirdPresWord, it)
        }
    }
}
