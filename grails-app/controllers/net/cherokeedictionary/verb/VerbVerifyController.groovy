package net.cherokeedictionary.verb

import net.cherokeedictionary.dictionary.Likespreadsheets
import net.cherokeedictionary.dictionary.VerbVerify
import net.cherokeedictionary.util.CompoundPrefix
import net.cherokeedictionary.util.CompoundPrefixes
import net.cherokeedictionary.util.PartOfSpeech
import net.cherokeedictionary.verb.conjugation.Conjugate

class VerbVerifyController {

    def index() {
        def lks = Likespreadsheets.findAll("from Likespreadsheets where partofspeechc = ?1 or partofspeechc = ?2 or partofspeechc like ?3 or partofspeechc like ?4", ['vi', 'vt', 'v.i%', 'v.t%'])

        lks.each {likes ->
            VerbVerify vv = new VerbVerify()
            vv.lks = likes
            CompoundPrefixes.prefixes.each { CompoundPrefix it ->
                def verb = Conjugate.createVerbToConjugate(it.prefix1, it.prefix2, verbName, "PRESENT", likes.partOfSpeechc, false)
            }

            println Conjugate.conjugate(verb).wholeWord


        }
    }
}
