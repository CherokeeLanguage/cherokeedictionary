package net.cherokeedictionary.dictionary

import net.cherokeedictionary.dictionary.Likespreadsheets
import net.cherokeedictionary.factory.VerbFactory

import net.cherokeedictionary.verb.conjugation.Conjugate

class ConjugationController {
    def index() {
        def displayValue = getDisplayValue(params)

        if (params.showHide) {
            render(display([item: displayValue, verbTense: params.verbTense, subject: params.subject, object: params.object], ""))
        } else {
            render(displayValue)
        }
    }

    def parser() {}

    def verbPrefixTable() {}

    def aspect() {}
    def concordance() {}

    def printFormat() {}

    def getDisplayValue(params) {
        return getDisplayValueConjugationEngineV2(params)
    }

    def getDisplayValueConjugationEngineV2(params) {
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
        paramMap.yi = params.yi == 'on'
        paramMap.ji = params.ji == 'on'
        paramMap.wi = params.wi == 'on'
        paramMap.ni = params.ni == 'on'
        paramMap.de = params.de == 'on'
        paramMap.da = params.da == 'on'
        paramMap.di = params.di == 'on'
        paramMap.i = params.i == 'on'
        paramMap.ga = params.ga == 'on'
        paramMap.e = params.e == 'on'

        def displayValue
        try {
            def verb = VerbFactory.createVerbFromParameters(paramMap)
            verb = Conjugate.conjugate(verb);

            displayValue = verb.wholeWord
        } catch (Exception e) {
            e.printStackTrace()
            displayValue = "there was an error with your request"
        }

        if (!displayValue || displayValue == "null") {
            displayValue = "      "
        }

        return displayValue;
    }
}
