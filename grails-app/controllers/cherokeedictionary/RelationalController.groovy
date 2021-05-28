package cherokeedictionary

import cherokee.dictionary.Likespreadsheets
import cherokee.relational.CatLink
import cherokee.relational.CrossReference
import cherokee.relational.CrossReferenceLink
import cherokee.relational.Definition
import cherokee.relational.DefinitionLarge
import cherokee.relational.DefinitionLargeLink
import cherokee.relational.DefinitionLink
import cherokee.relational.Dialect
import cherokee.relational.DialectLink
import cherokee.relational.Etymology
import cherokee.relational.EtymologyLink
import cherokee.relational.Notes
import cherokee.relational.NotesLink
import cherokee.relational.PartOfSpeech
import cherokee.relational.PosLink
import cherokee.relational.Sentence
import cherokee.relational.SentenceLink
import cherokee.relational.VerbCategory
import cherokee.relational.VerbCategoryLink
import cherokee.relational.Word
import cherokee.relational.WordLink
import net.cherokeedictionary.transliteration.SyllabaryUtil

class RelationalController {
    def index() {
        def lst = insertIntoDB()
        println lst.size()

        render(view: "index", model: [lst: lst])
    }

    private def insertIntoDB() {
//        processCWL()
        def lst = Likespreadsheets.findAll("from Likespreadsheets where source=?0", ['ced'])//"from Likespreadsheets where source=?0 and (entrya like ?1 or nounadjplurale like ?2 or vfirstpresg like ?3 or vthirdpasti like ?4 or vthirdpresk like ?5 or vsecondimperm like ?6 or vthirdinfo like ?7)", ['ced', '%,%','%,%','%,%','%,%','%,%','%,%','%,%'])
        processCED()
        println lst.size()
        return lst
    }

//    || it.nounadjplurale.contains(",") || it.vfirstpresg.contains(",") || it.vthirdpasti.contains(",") || it.vthirdpresk.contains(",") || it.vsecondimperm.contains(",") || it.vthirdinfo.contains(",")

    def lstWithCommas = ["entrya":"syllabaryb", "nounadjplurale":"nounadjpluralsyllf", "vfirstpresg":"vfirstpresh", "vthirdpasti":"vthirdpastsyllj", "vthirdpresk":"vthirdpressylll", "vsecondimperm":"vsecondimpersylln", "vthirdinfo":"vthirdinfsyllp"]

    def processCED() {
        def lst = Likespreadsheets.findAll("from Likespreadsheets where source=?0", ['ced'])

        lst.eachWithIndex { Likespreadsheets it, idx ->
            def hasComma = false
            lstWithCommas.each {comma, value->
                if (it."$comma" && it."$comma".contains(",")) {
                    def spl = (it."$comma").split(",")
                    def syllspl = (it."$value").split(",")
                    hasComma = true
                    if (spl.size() != syllspl.size()) {
//                        println spl[0] + " " + syllspl[0]
//                        println spl[1] + " " + syllspl[0]
                        def word = createAndSaveWord(syllspl[0], spl[0], it)
                        processDefinition(it, word)
                        def word2 = createAndSaveWord(syllspl[1], spl[0], it)
                        processDefinition(it, word2)
                    } else {
//                        println spl[0] + " " + syllspl[0]
                        def word = createAndSaveWord(syllspl[0], spl[0], it)
                        processDefinition(it, word)
//                        println spl[1] + " " + syllspl[1]
                        def word2 = createAndSaveWord(syllspl[1], spl[1], it)
                        processDefinition(it, word2)
                    }
                } else {
                    def entry = it."$comma"
                    def syllb = it."$value"
                    if (entry && !entry.contains("--") && syllb && !syllb.contains("--")) {
                        def word = createAndSaveWord(syllb, entry, it)
                        processDefinition(it, word)
                    }
                }
            }

//            if (!hasComma) {
//                def word = createAndSaveWord(it.syllabaryb, it.entrya, it)
//                processDefinition(it, word)
//            }
        }
//    def noun = createInsert(it.nounadjplurale, it.nounadjpluralsyllf, it.nounadjpluraltranslit)
//    def firstpres = createInsert(it.vfirstpresg, it.vfirstpresh, it.vfirstprestranslit)
//    def thirdpast = createInsert(it.vthirdpasti, it.vthirdpastsyllj, it.vthirdpasttranslit)
//    def thirdpres = createInsert(it.vthirdpresk, it.vthirdpressylll, it.vthirdprestranslit)
//    def secondimp = createInsert(it.vsecondimperm, it.vsecondimpersylln, it.vsecondimpertranslit)
//    def thirdinf = createInsert(it.vthirdinfo, it.vthirdinfsyllp, it.vthirdinftranslit)
    }

    def processCWL() {
        def lst = Likespreadsheets.findAll("from Likespreadsheets where source=?0", ['cwl'])//, [max:10]

        lst.eachWithIndex { Likespreadsheets it, idx ->
            def otali = ""
            def otaliSyll = ""
            def giduwah = ""
            def giduwahSyll = ""

            if (it.entrya.contains("(w)") || it.entrya.contains("(e)")) {
                if (it.entrya.contains("(w)")) {
                    otali = it.entrya.substring(0, it.entrya.indexOf("(w)"))
                    otaliSyll = it.syllabaryb.substring(0, it.syllabaryb.indexOf("(w)"))
                }

                if (it.entrya.contains("(e)")) {
                    def startpoint = otali ? otali.length() + 3 : 0
                    giduwah = it.entrya.substring(startpoint, it.entrya.indexOf("(e)"))
                    def startpoint2 = otaliSyll ? otaliSyll.length() + 3 : 0
                    giduwahSyll = it.syllabaryb.substring(startpoint2, it.syllabaryb.indexOf("(e)"))
                }
            }

            if (otali) {
                def otaliEntry = createAndSaveWord(otaliSyll.trim(), otali, it)
                processDefinition(it, otaliEntry)

                def giduwahEntry = createAndSaveWord(giduwahSyll.trim(), giduwah, it)
                processDefinition(it, giduwahEntry)
            } else {
                def entry = createAndSaveWord(it.syllabaryb, it.entrya, it)
                processDefinition(it, entry)
            }
        }
    }

    def createAndSaveWord(syllabaryb, entrya, likespreadsheets) {
        def entry = new Word(syllabary: syllabaryb.trim(), tsalagi: entrya.trim(), autotranslit: SyllabaryUtil.parseSyllabary(syllabaryb))

        if (!entry.save(flush: true)) {
            entry.errors.allErrors.each {
                println it
            }
        }

        WordLink wl2 = new WordLink(likespreadsheets:likespreadsheets, word: entry)
        if (!wl2.save(flush: true)) {
            wl2.errors.allErrors.each {
                println it
            }
        }

        return entry;
    }

    def processDefinition(it, word) {
        if (it.definitiond) {
            if (it.definitiond.contains("(")) {
                it.definitiond = it.definitiond.replaceAll("\\(1\\)", "").replaceAll("\\(2\\)", "").replaceAll("\\(3\\)", "").replaceAll("\\(4\\)", "")
                if (it.definitiond.contains("(")) {
                    def defd = it.definitiond.substring(0, it.definitiond.indexOf("("))
                    createAndSaveDefinition(defd, word)
                } else {
                    createAndSaveDefinition(it.definitiond, word)
                }
            } else {
                createAndSaveDefinition(it.definitiond, word)
            }
        }
    }

    def createAndSaveDefinition(definitiond, word) {
        def definition = Definition.findAll("from Definition where definition=?0", [definitiond.trim()])
        if (!definition) {
            definition = new Definition(definition: definitiond.trim());

            if (!definition.save(flush: true)) {
                definition.errors.allErrors.each {
                    println it
                }
            } else {
                DefinitionLink wl2 = new DefinitionLink(word:word, definition: definition)
                if (!wl2.save(flush: true)) {
                    wl2.errors.allErrors.each {
                        println it
                    }
                }
            }
        } else {
            DefinitionLink wl2 = new DefinitionLink(word:word, definition: definition.get(0))
            if (!wl2.save(flush: true)) {
                wl2.errors.allErrors.each {
                    println it
                }
            }
        }
    }
}
