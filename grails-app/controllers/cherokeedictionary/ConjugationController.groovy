package cherokeedictionary

import cherokee.dictionary.Likespreadsheets
import net.cherokeedictionary.factory.VerbFactory
import net.cherokeedictionary.stemmer.DefinitionLine
import net.cherokeedictionary.stemmer.Stemmer

class ConjugationController {

    def index() {
        def displayValue = getDisplayValue(params)

        if (params.showHide) {
            render(display([item: displayValue, verbTense: params.verbTense, subject: params.subject, object: params.object], ""))
        } else {
            render(displayValue)
        }
    }

    def getDisplayValue(params) {
        def lks = Likespreadsheets.findById(params.id)
        def paramMap = [:]
        paramMap.habitual = lks.vthirdpressylll
        paramMap.imperative = lks.vsecondimpersylln
        paramMap.infinitive = lks.vthirdinfsyllp
        paramMap.present1st = lks.vfirstpresh
        paramMap.present3rd = lks.syllabaryb
        paramMap.remotepast = lks.vthirdpastsyllj
        paramMap.partofspeechc = lks.partofspeechc
        paramMap.verbTense = params.verbTense
        paramMap.subject = params.subject
        paramMap.object = params.object

        def displayValue
        try {
            displayValue = VerbFactory.createVerbFromParameters(paramMap)
        } catch (Exception e) {
            displayValue = "there was an error with your request"
        }

        if (!displayValue || displayValue == "null") {
            displayValue = "------"
        }

        return displayValue;
    }
}
