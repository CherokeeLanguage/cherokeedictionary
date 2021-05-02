package cherokeedictionary

import cherokee.dictionary.Likespreadsheets

//for V2
import net.cherokeedictionary.factory.VerbFactory
import net.cherokeedictionary.stemmer.DefinitionLine
import net.cherokeedictionary.stemmer.Stemmer

//for V1
//import cherokee.dictionary.conjugation.Conjugate
//import cherokee.dictionary.conjugation.cdpbook.Stemmer
//import cherokee.dictionary.conjugation.conjugate.PartOfSpeech
//import cherokee.dictionary.conjugation.conjugate.PrefixTableObject
//import cherokee.dictionary.conjugation.conjugate.PrefixTableSubject
//import cherokee.dictionary.conjugation.conjugate.Tense
//import cherokee.dictionary.conjugation.processors.VerbPrefixFactory
//import cherokee.dictionary.conjugation.stem.DefinitionLine
//import cherokee.dictionary.conjugation.wordFormation.Word
//import com.cobradoc.cherokee.SyllabaryUtil

class ConjugationController {
    boolean useV2 = true;

    def index() {
        def displayValue = getDisplayValue(params)

        if (params.showHide) {
            render(display([item: displayValue, verbTense: params.verbTense, subject: params.subject, object: params.object], ""))
        } else {
            render(displayValue)
        }
    }

    def parser() {}

    def verbTable() {}

    def getDisplayValue(params) {
        if (useV2) {
            return getDisplayValueConjugationEngineV2(params)
        } else {
            return getDisplayValueConjugationEngineV1(params)
        }
    }

    def getDisplayValueConjugationEngineV1(params) {
        def lks = Likespreadsheets.findById(params.id)
        def habitual = lks.vthirdpressylll
        def imperative = lks.vsecondimpersylln
        def infinitive = lks.vthirdinfsyllp
        def present1st = lks.vfirstpresh
        def present3rd = lks.syllabaryb
        def remotepast = lks.vthirdpastsyllj
        def partofspeechc = lks.partofspeechc

        println "get display value"

//        def stemmer = new Stemmer()
//        stemmer.habitual = new DefinitionLine(syllabary: habitual)//"ᎦᏬᏂᏍᎪᎢ")
//        stemmer.imperative = new DefinitionLine(syllabary: imperative)//"ᎯᏬᏂᎯ")
//        stemmer.infinitive = new DefinitionLine(syllabary: infinitive)//"ᎤᏬᏂᎯᏍᏗ")
//        stemmer.present1st = new DefinitionLine(syllabary:  present1st)//"ᏥᏬᏂᎭ")
//        stemmer.present3rd = new DefinitionLine(syllabary: present3rd)//"ᎦᏬᏂᎭ")
//        stemmer.remotepast = new DefinitionLine(syllabary: remotepast)//"ᎤᏬᏂᏒᎢ")

        println "stemmer complete"

        def displayValue

//        Word verb = new Word()
//        verb.partOfSpeech = partofspeechc == 'vi' ? PartOfSpeech.VERB_INTRANSITIVE : PartOfSpeech.VERB_TRANSITIVE
//        verb.subject = PrefixTableSubject.valueOf(params.subject)
//        verb.object = PrefixTableObject.valueOf(params.object)
//        verb.stemmer = stemmer
//        verb.tense = Tense.valueOf(params.verbTense)
//
//        verb.tense = Tense.valueOf(params.verbTense ?: "PRESENT")

        //rewrite so this works with the latest version
//        word.prefixHolderObject.with {
//            yi = params.yi == 'on'
//            ji = params.ji == 'on'
//            wi = params.wi == 'on'
//            ni = params.ni == 'on'
//            de = params.de == 'on'
//            da = params.da == 'on'
//            di = params.di == 'on'
//            i = params.i == 'on'
//            ga = params.ga == 'on'
//            e = params.e == 'on'
//        }

//        Conjugate conjugate = new Conjugate()
//        try {
//            displayValue = conjugate.conjugate(params.subject, params.object, stemmer, params.verbTense, partofspeechc)
//        } catch (Exception e) {
//            displayValue = "there was an error with your request"
//        }
//
//        if (!displayValue || displayValue == "null") {
//            displayValue = "------"
//        }

        return displayValue;
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

        def displayValue
        try {
            displayValue = VerbFactory.createVerbFromParameters(paramMap)
        } catch (Exception e) {
            displayValue = "there was an error with your request"
        }

        if (!displayValue || displayValue == "null") {
            displayValue = "      "
        }

        return displayValue;
    }
}
