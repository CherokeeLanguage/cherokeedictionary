package cherokeedictionary

import cherokee.dictionary.Likespreadsheets
import net.cherokeedictionary.core.Verb

//for V2
import net.cherokeedictionary.factory.VerbFactory
import net.cherokeedictionary.stemmer.DefinitionLine
import net.cherokeedictionary.stemmer.Stemmer
import net.cherokeedictionary.util.PartOfSpeech
import net.cherokeedictionary.verb.conjugation.Conjugate

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

    def verbPrefixTable() {}

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
        println paramMap

        def displayValue
        try {
            def habitual = paramMap.habitual
            def imperative = paramMap.imperative
            def infinitive = paramMap.infinitive
            def present1st = paramMap.present1st
            def present3rd = paramMap.present3rd
            def remotepast = paramMap.remotepast
            def partofspeechc = paramMap.partofspeechc

//        println paramMap

            def stemmer = new Stemmer()
            stemmer.habitual = new DefinitionLine(syllabary: habitual)//"ᎦᏬᏂᏍᎪᎢ")
            stemmer.imperative = new DefinitionLine(syllabary: imperative)//"ᎯᏬᏂᎯ")
            stemmer.infinitive = new DefinitionLine(syllabary: infinitive)//"ᎤᏬᏂᎯᏍᏗ")
            stemmer.present1st = new DefinitionLine(syllabary:  present1st)//"ᏥᏬᏂᎭ")
            stemmer.present3rd = new DefinitionLine(syllabary: present3rd)//"ᎦᏬᏂᎭ")
            stemmer.remotepast = new DefinitionLine(syllabary: remotepast)//"ᎤᏬᏂᏒᎢ")

            if (partofspeechc == 'v.i.' || partofspeechc == 'vi') {
                partofspeechc = PartOfSpeech.VERB_INTRANSITIVE
            } else {
                partofspeechc = PartOfSpeech.VERB_TRANSITIVE
            }

            Verb verb = Conjugate.createVerbToConjugate(paramMap.subject, paramMap.object, stemmer, paramMap.verbTense, partofspeechc, false)
            verb.initialPrefixHolder.yi = paramMap.yi
            verb.initialPrefixHolder.ji = paramMap.ji
            verb.initialPrefixHolder.wi = paramMap.wi
            verb.initialPrefixHolder.ni = paramMap.ni
            verb.initialPrefixHolder.de = paramMap.de
            verb.initialPrefixHolder.da = paramMap.da
            verb.initialPrefixHolder.di = paramMap.dis
            verb.initialPrefixHolder.i = paramMap.i
            verb.initialPrefixHolder.ga = paramMap.ga
            verb.initialPrefixHolder.e = paramMap.e
            verb = Conjugate.conjugate(verb);
            displayValue = verb.wholeWord


//            return displayValuedisplayValue = VerbFactory.createVerbFromParameters(paramMap)
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
