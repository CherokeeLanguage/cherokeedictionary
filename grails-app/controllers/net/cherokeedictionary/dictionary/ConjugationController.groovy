package net.cherokeedictionary.dictionary

import net.cherokeedictionary.ConjugatedVerb
import net.cherokeedictionary.core.Verb
import net.cherokeedictionary.dictionary.Likespreadsheets
import net.cherokeedictionary.factory.VerbFactory
import net.cherokeedictionary.transliteration.SyllabaryUtil
import net.cherokeedictionary.util.PrefixTableObject
import net.cherokeedictionary.util.PrefixTableSubject
import net.cherokeedictionary.verb.conjugation.Conjugate
import net.cherokeedictionary.util.Tense

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
    def audioDisplay() {}

    def printFormat() {}

    def populate() {}

    def multilang() {}

    def conjugateTable() {
        /*def tenseList = [Tense.PRESENT, Tense.REMOTE_PAST, Tense.HABITUAL, Tense.FUTURE_COMMAND, Tense.INFINITIVE]
        // and definitiond = 'He is bleeding'
        def lst = Likespreadsheets.findAll("from Likespreadsheets where (partofspeechc like 'v.i%' or partofspeechc like 'v.t%' or partofspeechc = 'vi' or partofspeechc = 'vt')")
        lst.each {
            def paramMap = [:]
            paramMap.habitual = it.vthirdpresk
            paramMap.imperative = it.vsecondimperm
            paramMap.infinitive = it.vthirdinfo
            paramMap.present1st = it.vfirstpresh
            paramMap.present3rd = it.syllabaryb
            paramMap.remotepast = it.vthirdpastsyllj
            paramMap.partofspeechc = it.partofspeechc

            tenseList.each {tense ->
                PrefixTableSubject.values().each {pts ->
                    PrefixTableObject.values().each {pto ->
                        paramMap.verbTense = tense.toString()
                        paramMap.subject = pts.toString()
                        if (it.partofspeechc != 'vi' && it.partofspeechc != 'v.i.') {
                            paramMap.object = pto.toString()
                        }
                        Verb display = VerbFactory.createVerbFromParameters(paramMap)
                        display = Conjugate.conjugate(display)
                        if (display.wholeWord.trim() != "") {
//                            println "${tense.toString()} - ${pts.toString()}:${pto.toString()} {${SyllabaryUtil.newTsalagiToSyllabary(display.wholeWord)}}"
                            def conjugatedV = new ConjugatedVerb(likespreadsheets: it, conjugation: SyllabaryUtil.newTsalagiToSyllabary(display.wholeWord), compoundPrefix: "${pts.toString()}:${pto.toString()}")
                            if (!conjugatedV.save()) {
                                conjugatedV.errors.each {error ->
                                    println error
                                }
                            }
                        }
                    }
                }
            }
        }*/

    }

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
