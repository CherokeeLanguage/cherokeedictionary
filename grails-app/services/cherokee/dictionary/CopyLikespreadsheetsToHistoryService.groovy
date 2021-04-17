package cherokee.dictionary

import cherokee.Admin
import grails.gorm.transactions.Transactional

//@Transactional
class CopyLikespreadsheetsToHistoryService {

    def copyLikeToHistory(Likespreadsheets ls, admin) {
        History history = new History()

        history.admin = admin
        history.entrya = ls.entrya
        history.syllabaryb = ls.syllabaryb
        history.partofspeechc = ls.partofspeechc
        history.definitiond = ls.definitiond
        history.nounadjplurale = ls.nounadjplurale
        history.nounadjpluralsyllf = ls.nounadjpluralsyllf
        history.vfirstpresg = ls.vfirstpresg
        history.vfirstpresh = ls.vfirstpresh
        history.vthirdpasti = ls.vthirdpasti
        history.vthirdpastsyllj = ls.vthirdpastsyllj
        history.vthirdpresk = ls.vthirdpresk
        history.vthirdpressylll = ls.vthirdpressylll
        history.vsecondimperm = ls.vsecondimperm
        history.vsecondimpersylln = ls.vsecondimpersylln
        history.vthirdinfo = ls.vthirdinfo
        history.vthirdinfsyllp = ls.vthirdinfsyllp
        history.sentenceq = ls.sentenceq
        history.sentencesyllr = ls.sentencesyllr
        history.sentenceenglishs = ls.sentenceenglishs
        history.crossreferencet = ls.crossreferencet

        history.nounadjpluraltranslit = ls.nounadjpluraltranslit
        history.entrytranslit = ls.entrytranslit
        history.vfirstprestranslit = ls.vfirstprestranslit
        history.vthirdpasttranslit = ls.vthirdpasttranslit
        history.vthirdprestranslit = ls.vthirdprestranslit
        history.vsecondimpertranslit = ls.vsecondimpertranslit
        history.vthirdinftranslit = ls.vthirdinftranslit
        history.sentencetranslit = ls.sentencetranslit

        history.entrytone = ls.entrytone
        history.nounadjpluraltone = ls.nounadjpluraltone
        history.vfirstprestone = ls.vfirstprestone
        history.vthirdpasttone = ls.vthirdpasttone
        history.vthirdprestone = ls.vthirdprestone
        history.vsecondimpertone = ls.vsecondimpertone
        history.vthirdinftone = ls.vthirdinftone

        history.source = ls.source
        history.definitionlarge = ls.definitionlarge
        history.etymology = ls.etymology
        history.notes = ls.notes
        history.category = ls.category
        history.modified = Date.newInstance()

        if (history.save(flush:true)) {
            history.errors.allErrors.each {
                println it
            }
        }
    }
}
