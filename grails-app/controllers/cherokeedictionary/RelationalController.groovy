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

/**
 *
 //create tables needed
 //pull from likespreadsheets
 //parse data as needed
 //insert into new tables where applicable

 word
 syllabary
 tone
 tsalagi
 autotranslit

 category

 partofspeech

 verbcategory
 subject
 object

 sentence
 syllabary
 tone
 tsalagi
 autotranslit

 crossreference
 link
 id

 dialect
 east
 west

 definition

 etymology

 notes



 wordLink
 word_id
 likespreadsheets_id

 catLink
 category_id
 likespreadsheets_id

 posLink
 pos_id
 likespreadsheets_id

 verbCatLink

 sentenceLink

 definitionLink

 etymologyLink

 notesLink
 */
/**
 * select * words from likespreadsheets
 * determine pos
 * iterate through each field
 *      if field contains /,/ or /(w)/ or /(e)/ then split - unless definitiond... where entry could be /1./ 
 *      - boil down definition to simplest term like 'to bury' or 'bury'
 *      - have complex definition like 'he buries it' 
 */
class RelationalController {
    def headings = ["entrya",
                    "syllabaryb",
                    "partofspeechc",
                    "definitiond",
                    "nounadjplurale",
                    "nounadjpluralsyllf",
                    "vfirstpresg",
                    "vfirstpresh",
                    "vthirdpasti",
                    "vthirdpastsyllj",
                    "vthirdpresk",
                    "vthirdpressylll",
                    "vsecondimperm",
                    "vsecondimpersylln",
                    "vthirdinfo",
                    "vthirdinfsyllp",
//                    "sentenceq",
//                    "sentencesyllr",
//                    "sentenceenglishs",
                    "crossreferencet",
                    "nounadjpluraltranslit",
                    "entrytranslit",
                    "vfirstprestranslit",
                    "vthirdpasttranslit",
                    "vthirdprestranslit",
                    "vsecondimpertranslit",
                    "vthirdinftranslit",
//                    "sentencetranslit",
                    "entrytone",
                    "nounadjpluraltone",
                    "vfirstprestone",
                    "vthirdpasttone",
                    "vthirdprestone",
                    "vsecondimpertone",
                    "vthirdinftone",
                    "source",
                    "definitionlarge",
                    "etymology",
                    "notes",
                    "category"]
    def inclusionExclusion = [",", "(e)", "(w)", "(", "2", "1", ";", "<", "-"]

    def index() {
//        printQuery(true)


//        boolean loggedin = session.getAttribute("loggedin")
//
//        if (!loggedin) {
//            redirect url: "/"
//        } else {
            def lst = insertIntoDB()
//        def lst = []
//
//        fixNulls()
            render(view: "index", model: [list: lst])
//        }
    }

    def query = "from Likespreadsheets where entrya not like ? and entrya not like ? and entrya not like ? and entrya not like ? and entrya not like ? and entrya not like ? and entrya not like ? and entrya not like ? and entrya not like ? and syllabaryb not like ? and syllabaryb not like ? and syllabaryb not like ? and syllabaryb not like ? and syllabaryb not like ? and syllabaryb not like ? and syllabaryb not like ? and syllabaryb not like ? and syllabaryb not like ? and partofspeechc not like ? and partofspeechc not like ? and partofspeechc not like ? and partofspeechc not like ? and partofspeechc not like ? and partofspeechc not like ? and partofspeechc not like ? and partofspeechc not like ? and partofspeechc not like ? and definitiond not like ? and definitiond not like ? and definitiond not like ? and definitiond not like ? and definitiond not like ? and definitiond not like ? and definitiond not like ? and definitiond not like ? and definitiond not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjplurale not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and nounadjpluralsyllf not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresg not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vfirstpresh not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpasti not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpastsyllj not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpresk not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vthirdpressylll not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimperm not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vsecondimpersylln not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfo not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and vthirdinfsyllp not like ? and crossreferencet not like ? and crossreferencet not like ? and crossreferencet not like ? and crossreferencet not like ? and crossreferencet not like ? and crossreferencet not like ? and crossreferencet not like ? and crossreferencet not like ? and crossreferencet not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and nounadjpluraltranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and entrytranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vfirstprestranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdpasttranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vthirdprestranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vsecondimpertranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and vthirdinftranslit not like ? and entrytone not like ? and entrytone not like ? and entrytone not like ? and entrytone not like ? and entrytone not like ? and entrytone not like ? and entrytone not like ? and entrytone not like ? and entrytone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and nounadjpluraltone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vfirstprestone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdpasttone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vthirdprestone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vsecondimpertone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and vthirdinftone not like ? and source not like ? and source not like ? and source not like ? and source not like ? and source not like ? and source not like ? and source not like ? and source not like ? and source not like ? and definitionlarge not like ? and definitionlarge not like ? and definitionlarge not like ? and definitionlarge not like ? and definitionlarge not like ? and definitionlarge not like ? and definitionlarge not like ? and definitionlarge not like ? and definitionlarge not like ? and etymology not like ? and etymology not like ? and etymology not like ? and etymology not like ? and etymology not like ? and etymology not like ? and etymology not like ? and etymology not like ? and etymology not like ? and notes not like ? and notes not like ? and notes not like ? and notes not like ? and notes not like ? and notes not like ? and notes not like ? and notes not like ? and notes not like ? and category not like ? and category not like ? and category not like ? and category not like ? and category not like ? and category not like ? and category not like ? and category not like ? and category not like ?"
    def exclusions = ['%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%','%,%','%(e)%','%(w)%','%(%','%2%','%1%','%;%','%<%','%-%']

    def query2 = "from Likespreadsheets where entrya not like '%,%' and entrya not like '%(e)%' and entrya not like '%(w)%' and entrya not like '%(%' and entrya not like '%2%' and entrya not like '%1%' and entrya not like '%;%' and entrya not like '%<%' and entrya not like '%-%' and syllabaryb not like '%,%' and syllabaryb not like '%(e)%' and syllabaryb not like '%(w)%' and syllabaryb not like '%(%' and syllabaryb not like '%2%' and syllabaryb not like '%1%' and syllabaryb not like '%;%' and syllabaryb not like '%<%' and syllabaryb not like '%-%' and partofspeechc not like '%,%' and partofspeechc not like '%(e)%' and partofspeechc not like '%(w)%' and partofspeechc not like '%(%' and partofspeechc not like '%2%' and partofspeechc not like '%1%' and partofspeechc not like '%;%' and partofspeechc not like '%<%' and partofspeechc not like '%-%' and definitiond not like '%,%' and definitiond not like '%(e)%' and definitiond not like '%(w)%' and definitiond not like '%(%' and definitiond not like '%2%' and definitiond not like '%1%' and definitiond not like '%;%' and definitiond not like '%<%' and definitiond not like '%-%' and nounadjplurale not like '%,%' and nounadjplurale not like '%(e)%' and nounadjplurale not like '%(w)%' and nounadjplurale not like '%(%' and nounadjplurale not like '%2%' and nounadjplurale not like '%1%' and nounadjplurale not like '%;%' and nounadjplurale not like '%<%' and nounadjplurale not like '%-%' and nounadjpluralsyllf not like '%,%' and nounadjpluralsyllf not like '%(e)%' and nounadjpluralsyllf not like '%(w)%' and nounadjpluralsyllf not like '%(%' and nounadjpluralsyllf not like '%2%' and nounadjpluralsyllf not like '%1%' and nounadjpluralsyllf not like '%;%' and nounadjpluralsyllf not like '%<%' and nounadjpluralsyllf not like '%-%' and vfirstpresg not like '%,%' and vfirstpresg not like '%(e)%' and vfirstpresg not like '%(w)%' and vfirstpresg not like '%(%' and vfirstpresg not like '%2%' and vfirstpresg not like '%1%' and vfirstpresg not like '%;%' and vfirstpresg not like '%<%' and vfirstpresg not like '%-%' and vfirstpresh not like '%,%' and vfirstpresh not like '%(e)%' and vfirstpresh not like '%(w)%' and vfirstpresh not like '%(%' and vfirstpresh not like '%2%' and vfirstpresh not like '%1%' and vfirstpresh not like '%;%' and vfirstpresh not like '%<%' and vfirstpresh not like '%-%' and vthirdpasti not like '%,%' and vthirdpasti not like '%(e)%' and vthirdpasti not like '%(w)%' and vthirdpasti not like '%(%' and vthirdpasti not like '%2%' and vthirdpasti not like '%1%' and vthirdpasti not like '%;%' and vthirdpasti not like '%<%' and vthirdpasti not like '%-%' and vthirdpastsyllj not like '%,%' and vthirdpastsyllj not like '%(e)%' and vthirdpastsyllj not like '%(w)%' and vthirdpastsyllj not like '%(%' and vthirdpastsyllj not like '%2%' and vthirdpastsyllj not like '%1%' and vthirdpastsyllj not like '%;%' and vthirdpastsyllj not like '%<%' and vthirdpastsyllj not like '%-%' and vthirdpresk not like '%,%' and vthirdpresk not like '%(e)%' and vthirdpresk not like '%(w)%' and vthirdpresk not like '%(%' and vthirdpresk not like '%2%' and vthirdpresk not like '%1%' and vthirdpresk not like '%;%' and vthirdpresk not like '%<%' and vthirdpresk not like '%-%' and vthirdpressylll not like '%,%' and vthirdpressylll not like '%(e)%' and vthirdpressylll not like '%(w)%' and vthirdpressylll not like '%(%' and vthirdpressylll not like '%2%' and vthirdpressylll not like '%1%' and vthirdpressylll not like '%;%' and vthirdpressylll not like '%<%' and vthirdpressylll not like '%-%' and vsecondimperm not like '%,%' and vsecondimperm not like '%(e)%' and vsecondimperm not like '%(w)%' and vsecondimperm not like '%(%' and vsecondimperm not like '%2%' and vsecondimperm not like '%1%' and vsecondimperm not like '%;%' and vsecondimperm not like '%<%' and vsecondimperm not like '%-%' and vsecondimpersylln not like '%,%' and vsecondimpersylln not like '%(e)%' and vsecondimpersylln not like '%(w)%' and vsecondimpersylln not like '%(%' and vsecondimpersylln not like '%2%' and vsecondimpersylln not like '%1%' and vsecondimpersylln not like '%;%' and vsecondimpersylln not like '%<%' and vsecondimpersylln not like '%-%' and vthirdinfo not like '%,%' and vthirdinfo not like '%(e)%' and vthirdinfo not like '%(w)%' and vthirdinfo not like '%(%' and vthirdinfo not like '%2%' and vthirdinfo not like '%1%' and vthirdinfo not like '%;%' and vthirdinfo not like '%<%' and vthirdinfo not like '%-%' and vthirdinfsyllp not like '%,%' and vthirdinfsyllp not like '%(e)%' and vthirdinfsyllp not like '%(w)%' and vthirdinfsyllp not like '%(%' and vthirdinfsyllp not like '%2%' and vthirdinfsyllp not like '%1%' and vthirdinfsyllp not like '%;%' and vthirdinfsyllp not like '%<%' and vthirdinfsyllp not like '%-%' and crossreferencet not like '%,%' and crossreferencet not like '%(e)%' and crossreferencet not like '%(w)%' and crossreferencet not like '%(%' and crossreferencet not like '%2%' and crossreferencet not like '%1%' and crossreferencet not like '%;%' and crossreferencet not like '%<%' and crossreferencet not like '%-%' and nounadjpluraltranslit not like '%,%' and nounadjpluraltranslit not like '%(e)%' and nounadjpluraltranslit not like '%(w)%' and nounadjpluraltranslit not like '%(%' and nounadjpluraltranslit not like '%2%' and nounadjpluraltranslit not like '%1%' and nounadjpluraltranslit not like '%;%' and nounadjpluraltranslit not like '%<%' and nounadjpluraltranslit not like '%-%' and entrytranslit not like '%,%' and entrytranslit not like '%(e)%' and entrytranslit not like '%(w)%' and entrytranslit not like '%(%' and entrytranslit not like '%2%' and entrytranslit not like '%1%' and entrytranslit not like '%;%' and entrytranslit not like '%<%' and entrytranslit not like '%-%' and vfirstprestranslit not like '%,%' and vfirstprestranslit not like '%(e)%' and vfirstprestranslit not like '%(w)%' and vfirstprestranslit not like '%(%' and vfirstprestranslit not like '%2%' and vfirstprestranslit not like '%1%' and vfirstprestranslit not like '%;%' and vfirstprestranslit not like '%<%' and vfirstprestranslit not like '%-%' and vthirdpasttranslit not like '%,%' and vthirdpasttranslit not like '%(e)%' and vthirdpasttranslit not like '%(w)%' and vthirdpasttranslit not like '%(%' and vthirdpasttranslit not like '%2%' and vthirdpasttranslit not like '%1%' and vthirdpasttranslit not like '%;%' and vthirdpasttranslit not like '%<%' and vthirdpasttranslit not like '%-%' and vthirdprestranslit not like '%,%' and vthirdprestranslit not like '%(e)%' and vthirdprestranslit not like '%(w)%' and vthirdprestranslit not like '%(%' and vthirdprestranslit not like '%2%' and vthirdprestranslit not like '%1%' and vthirdprestranslit not like '%;%' and vthirdprestranslit not like '%<%' and vthirdprestranslit not like '%-%' and vsecondimpertranslit not like '%,%' and vsecondimpertranslit not like '%(e)%' and vsecondimpertranslit not like '%(w)%' and vsecondimpertranslit not like '%(%' and vsecondimpertranslit not like '%2%' and vsecondimpertranslit not like '%1%' and vsecondimpertranslit not like '%;%' and vsecondimpertranslit not like '%<%' and vsecondimpertranslit not like '%-%' and vthirdinftranslit not like '%,%' and vthirdinftranslit not like '%(e)%' and vthirdinftranslit not like '%(w)%' and vthirdinftranslit not like '%(%' and vthirdinftranslit not like '%2%' and vthirdinftranslit not like '%1%' and vthirdinftranslit not like '%;%' and vthirdinftranslit not like '%<%' and vthirdinftranslit not like '%-%' and entrytone not like '%,%' and entrytone not like '%(e)%' and entrytone not like '%(w)%' and entrytone not like '%(%' and entrytone not like '%2%' and entrytone not like '%1%' and entrytone not like '%;%' and entrytone not like '%<%' and entrytone not like '%-%' and nounadjpluraltone not like '%,%' and nounadjpluraltone not like '%(e)%' and nounadjpluraltone not like '%(w)%' and nounadjpluraltone not like '%(%' and nounadjpluraltone not like '%2%' and nounadjpluraltone not like '%1%' and nounadjpluraltone not like '%;%' and nounadjpluraltone not like '%<%' and nounadjpluraltone not like '%-%' and vfirstprestone not like '%,%' and vfirstprestone not like '%(e)%' and vfirstprestone not like '%(w)%' and vfirstprestone not like '%(%' and vfirstprestone not like '%2%' and vfirstprestone not like '%1%' and vfirstprestone not like '%;%' and vfirstprestone not like '%<%' and vfirstprestone not like '%-%' and vthirdpasttone not like '%,%' and vthirdpasttone not like '%(e)%' and vthirdpasttone not like '%(w)%' and vthirdpasttone not like '%(%' and vthirdpasttone not like '%2%' and vthirdpasttone not like '%1%' and vthirdpasttone not like '%;%' and vthirdpasttone not like '%<%' and vthirdpasttone not like '%-%' and vthirdprestone not like '%,%' and vthirdprestone not like '%(e)%' and vthirdprestone not like '%(w)%' and vthirdprestone not like '%(%' and vthirdprestone not like '%2%' and vthirdprestone not like '%1%' and vthirdprestone not like '%;%' and vthirdprestone not like '%<%' and vthirdprestone not like '%-%' and vsecondimpertone not like '%,%' and vsecondimpertone not like '%(e)%' and vsecondimpertone not like '%(w)%' and vsecondimpertone not like '%(%' and vsecondimpertone not like '%2%' and vsecondimpertone not like '%1%' and vsecondimpertone not like '%;%' and vsecondimpertone not like '%<%' and vsecondimpertone not like '%-%' and vthirdinftone not like '%,%' and vthirdinftone not like '%(e)%' and vthirdinftone not like '%(w)%' and vthirdinftone not like '%(%' and vthirdinftone not like '%2%' and vthirdinftone not like '%1%' and vthirdinftone not like '%;%' and vthirdinftone not like '%<%' and vthirdinftone not like '%-%' and source not like '%,%' and source not like '%(e)%' and source not like '%(w)%' and source not like '%(%' and source not like '%2%' and source not like '%1%' and source not like '%;%' and source not like '%<%' and source not like '%-%' and definitionlarge not like '%,%' and definitionlarge not like '%(e)%' and definitionlarge not like '%(w)%' and definitionlarge not like '%(%' and definitionlarge not like '%2%' and definitionlarge not like '%1%' and definitionlarge not like '%;%' and definitionlarge not like '%<%' and definitionlarge not like '%-%' and etymology not like '%,%' and etymology not like '%(e)%' and etymology not like '%(w)%' and etymology not like '%(%' and etymology not like '%2%' and etymology not like '%1%' and etymology not like '%;%' and etymology not like '%<%' and etymology not like '%-%' and notes not like '%,%' and notes not like '%(e)%' and notes not like '%(w)%' and notes not like '%(%' and notes not like '%2%' and notes not like '%1%' and notes not like '%;%' and notes not like '%<%' and notes not like '%-%' and category not like '%,%' and category not like '%(e)%' and category not like '%(w)%' and category not like '%(%' and category not like '%2%' and category not like '%1%' and category not like '%;%' and category not like '%<%' and category not like '%-%'"

    private def retrieveFromDB() {
        def lst = Word.findAll("from Word w where w.syllabary = ?0", ['ᎠᏧᏣ'])

        println WordLink.findByWord(lst.get(0)).likespreadsheets.id
    }

    private def insertIntoDB() {
//        def lst = Likespreadsheets.findAll(query, exclusions)//, [max:10]
        def lst = Likespreadsheets.findAll(query2)//, [max:10]

        println lst.size()

        def didNotProcess = []

        lst.each {
            def oot = it
            didNotProcess.addAll(processItem(oot, it))
        }

        return didNotProcess
    }

    private List processItem(Likespreadsheets oot, Likespreadsheets it) {
        def syllabaryb = oot.syllabaryb ?: " "

        def wordList = []
        def didNotProcess = []
        Word word
        boolean halt = false;
        //TODO right here all of the work needs to be checked for commas then process through
        word = new Word(syllabary: syllabaryb, tsalagi: oot?.entrya ?: " ", autotranslit: oot.entrytranslit ?: oot.entrya, tone: oot.entrytone ?: oot.entrya)
        if (!word.save(flush: true)) {
            word.errors.allErrors.each {
                println it
                halt = true;
            }
        }

        if (halt) {
            return
        }

        WordLink wl = new WordLink(likespreadsheets:oot, word: word)
        if (!wl.save(flush: true)) {
            wl.errors.allErrors.each {
                println it
            }
        }


//        if (oot.sentencesyllr) {
//            def translit = oot.sentencetranslit
//
//            translit = translit ?: ""
//            def sentenceSyllrsplit = oot.sentencesyllr.split(/(?<=[\.\?\!]\s)/)
//            def sentenceEnglishsplit = oot.sentenceenglishs.split(/(?<=[\.\?\!]\s)/)
//            def sentenceQsplit = oot.sentenceq.split(/(?<=[\.\?\!]\s)/)
//
//            sentenceSyllrsplit.eachWithIndex { str, idx ->
//                def syllr = str ? str : " "
//                def sentq = sentenceQsplit ? sentenceQsplit[idx] : " "
//                def eng = sentenceEnglishsplit ? sentenceEnglishsplit[idx] : " "
//
////                println "${syllr} -- ${sentq} -- ${eng}"
//
//                if (syllr) {
//                    Sentence sentence = new Sentence(syllabary: syllr, tsalagi: sentq, autotranslit: translit, english: eng, tone: "tone")
//
//                    if (!sentence.save(flush: true)) {
//                        sentence.errors.allErrors.each {
//                            println it
//                        }
//                    }
//
//                    SentenceLink sl = new SentenceLink(sentence: sentence, word: word)
//                    if (!sl.save(flush: true)) {
//                        sl.errors.allErrors.each {
//                            println it
//                        }
//                    }
//                }
//            }
//        }
//
//        if (oot.source == 'rrd') {
//            Dialect dialect = Dialect.find('from Dialect d where d.dialect = ?0', ['giduwah'])
//            DialectLink dialectLink = new DialectLink(dialect: dialect, word: word)
//            if (!dialectLink.save(flush: true)) {
//                dialectLink.errors.allErrors.each {
//                    println it
//                }
//            }
//        } else {
//            Dialect dialect = Dialect.find('from Dialect d where d.dialect = ?0', ['otali'])
//            DialectLink dialectLink = new DialectLink(dialect: dialect, word: word)
//            if (!dialectLink.save(flush: true)) {
//                dialectLink.errors.allErrors.each {
//                    println it
//                }
//            }
//        }
//
//        if (oot.etymology) {
//            Etymology etymology = new Etymology(etymology: oot.etymology)
//            if (!etymology.save(flush: true)) {
//                etymology.errors.allErrors.each {
//                    println it
//                }
//            }
//            EtymologyLink el = new EtymologyLink(etymology: etymology, word: word)
//            if (!el.save(flush: true)) {
//                el.errors.allErrors.each {
//                    println it
//                }
//            }
//        }
//
//        if (oot.notes) {
//            Notes notes = new Notes(notes: it.notes)
//            if (!notes.save(flush: true)) {
//                notes.errors.allErrors.each {
//                    println it
//                }
//            }
//            NotesLink nl = new NotesLink(notes: notes, word: word)
//            if (!nl.save(flush: true)) {
//                nl.errors.allErrors.each {
//                    println it
//                }
//            }
//        }

        if (oot.definitiond) {
            Definition definition = new Definition(definition: oot.definitiond)
            if (!definition.save(flush: true)) {
                definition.errors.allErrors.each {
                    println it
                }
            }

            DefinitionLink dl = new DefinitionLink(definition: definition, word: word)
            if (!dl.save(flush: true)) {
                dl.errors.allErrors.each {
                    println it
                }
            }
        }

//        if (oot.definitionlarge) {
//            DefinitionLarge definitionlarge = new DefinitionLarge(definitionlarge: oot.definitionlarge)
//            if (!definitionlarge.save(flush: true)) {
//                println oot.definitionlarge
//                definitionlarge.errors.allErrors.each {
//                    println it
//                }
//            }
//
//            DefinitionLargeLink dl = new DefinitionLargeLink(definitionLarge: definitionlarge, word: word)
//            if (!dl.save(flush: true)) {
//                dl.errors.allErrors.each {
//                    println it
//                }
//            }
//        }

//        if (oot.category) {
//            cherokee.relational.Category category = new cherokee.relational.Category(category: oot.category)
//            if (!category.save(flush: true)) {
//                category.errors.allErrors.each {
//                    println it
//                }
//            }
//            CatLink cl = new CatLink(category: category, word: word)
//            if (!cl.save(flush: true)) {
//                cl.errors.allErrors.each {
//                    println it
//                }
//            }
//        }
//
//        PartOfSpeech pos = PartOfSpeech.find('from PartOfSpeech p where p.partofspeech = ?0', [oot.partofspeechc])
//        if (pos) {
//            if (pos?.partofspeech && (pos.partofspeech == 'vi' || pos.partofspeech == 'vt')) {
////                verbs(oot, pos)
////                    //loop through each part of the verb sections
////                    // if they have a comma process each one and attach to the same first section
////
////                    def tmp = oot.vfirstpresh?.split(",")
////                    def tone = oot.vfirstprestone?.split(",")
////                    def tsalagi = oot.vfirstpresg?.split(",")
////                    def autotranslit = oot.vfirstprestranslit?.split(",")
////
////                    if (autotranslit && autotranslit.size() == 1 || !autotranslit) {
////                        autotranslit = ['', '']
////                    }
////
////                    def tmp0
////
////                    if (tmp.size() > 1) {
////                        tmp0 = tmp[0]
////                    } else {
////                        tmp0 = tmp
////                    }
////
////
////
//////                    tmp.eachWithIndex { str, idx ->
//////                        saveIndividualWord(str, tone[idx], autotranslit[idx], tsalagi[idx], pos, VerbCategory.findAll("from VerbCategory v where v.object=? and subject=?", ['SG2', 'FUTURE_COMMAND']))
//////                    }
//
//            }
//
//            if (pos?.partofspeech && (pos.partofspeech == 'n' || pos.partofspeech == 'adj')) {
//                if (oot.nounadjpluralsyllf.contains(",")) {
//                    def tmp = oot.nounadjpluralsyllf.split(",")
//                    def tone = oot.nounadjpluraltone?.split(",")
//                    def tsalagi = oot.nounadjplurale?.split(",")
//                    def autotranslit = oot.nounadjpluraltranslit?.split(",")
//
//                    if (autotranslit && autotranslit.size() == 1 || !autotranslit) {
//                        autotranslit = ['', '']
//                    }
//
//                    tmp.eachWithIndex { str, idx ->
////                        println str
//                        Word tmpWord = new Word(syllabary: str, tone: tone[idx], tsalagi: tsalagi[idx], autotranslit: autotranslit[idx])
//
//                        PosLink pl = new PosLink(word: tmpWord, partOfSpeech: pos)
//                        if (!pl.save(flush: true)) {
//                            pl.errors.allErrors.each {
//                                println it
//                            }
//                        }
//                    }
//                }
//            }
//
//            PosLink pl = new PosLink(word: word, partOfSpeech: pos)
//            if (!pl.save(flush: true)) {
//                pl.errors.allErrors.each {
//                    println it
//                }
//            }
//
//            if (pos.partofspeech != 'vi' && pos.partofspeech != 'vt' && pos.partofspeech != 'n' && pos.partofspeech != 'adj') {
//                println oot.partofspeechc
//                didNotProcess << oot
//            }
//        }
//
//        if (oot.crossreferencet) {
//            String[] splits = oot.crossreferencet.split(", ");
//            for (String cf : splits) {
//                String cleanedValue = cf.trim();
//                if (cleanedValue.contains("(")) {
//                    cleanedValue = cleanedValue.substring(0, cleanedValue.indexOf("(")).trim();
//                }
//
//                if (cleanedValue) {
//                    CrossReference cr = new CrossReference(crossreference: cleanedValue)
//
//                    if (!cr.save(flush: true)) {
//                        cr.errors.allErrors.each {
//                            println it
//                        }
//                    } else {
//                        CrossReferenceLink cl = new CrossReferenceLink(crossReference: cr, word: word)
//                        if (!cl.save(flush: true)) {
//                            cl.errors.allErrors.each {
//                                println it
//                            }
//                        }
//                    }
//                }
//
////                    <a href="xrefdisplay?old=${cleanedValue}&current=${entry.id}">${cf.trim()}</a><br/>
//            }
//        }


        return didNotProcess
    }

    private def printQuery(boolean printTogether = false) {
        print "\"from Likespreadsheets where "
        if (!printTogether) {
            headings.each { heading ->
                inclusionExclusion.each { includeExclude ->
                    print "$heading not like ? and "
                }
            }

            println "\", ["
            headings.each { heading ->
                inclusionExclusion.each { ie ->
                    print "'%$ie%',"
                }
            }

            print "]"
        } else {
            headings.each { heading ->
                inclusionExclusion.each { includeExclude ->
                    print "$heading not like '%$includeExclude%' and "
                }
            }
        }
    }

    private void fixNulls() {
        def lst = Likespreadsheets.findAll()
        lst.each {
            println it

//            if (it.vfirstpresh == null) {
                adjustAndSave(it)
//            }
        }

    }

    private void adjustAndSave(Likespreadsheets ls) {
        ls.entrya = ls.entrya == null ? "" : ls.entrya
        ls.syllabaryb = ls.syllabaryb == null ? "" : ls.syllabaryb
        ls.partofspeechc = ls.partofspeechc == null ? "" : ls.partofspeechc
        ls.definitiond = ls.definitiond == null ? "" : ls.definitiond
        ls.nounadjplurale = ls.nounadjplurale == null ? "" : ls.nounadjplurale
        ls.nounadjpluralsyllf = ls.nounadjpluralsyllf == null ? "" : ls.nounadjpluralsyllf
        ls.vfirstpresg = ls.vfirstpresg == null ? "" : ls.vfirstpresg
        ls.vfirstpresh = ls.vfirstpresh == null ? "" : ls.vfirstpresh
        ls.vthirdpasti = ls.vthirdpasti == null ? "" : ls.vthirdpasti
        ls.vthirdpastsyllj = ls.vthirdpastsyllj == null ? "" : ls.vthirdpastsyllj
        ls.vthirdpresk = ls.vthirdpresk == null ? "" : ls.vthirdpresk
        ls.vthirdpressylll = ls.vthirdpressylll == null ? "" : ls.vthirdpressylll
        ls.vsecondimperm = ls.vsecondimperm == null ? "" : ls.vsecondimperm
        ls.vsecondimpersylln = ls.vsecondimpersylln == null ? "" : ls.vsecondimpersylln
        ls.vthirdinfo = ls.vthirdinfo == null ? "" : ls.vthirdinfo
        ls.vthirdinfsyllp = ls.vthirdinfsyllp == null ? "" : ls.vthirdinfsyllp
        ls.sentenceq = ls.sentenceq == null ? "" : ls.sentenceq
        ls.sentencesyllr = ls.sentencesyllr == null ? "" : ls.sentencesyllr
        ls.sentenceenglishs = ls.sentenceenglishs == null ? "" : ls.sentenceenglishs
        ls.crossreferencet = ls.crossreferencet == null ? "" : ls.crossreferencet
        ls.nounadjpluraltranslit = ls.nounadjpluraltranslit == null ? "" : ls.nounadjpluraltranslit
        ls.entrytranslit = ls.entrytranslit == null ? "" : ls.entrytranslit
        ls.vfirstprestranslit = ls.vfirstprestranslit == null ? "" : ls.vfirstprestranslit
        ls.vthirdpasttranslit = ls.vthirdpasttranslit == null ? "" : ls.vthirdpasttranslit
        ls.vthirdprestranslit = ls.vthirdprestranslit == null ? "" : ls.vthirdprestranslit
        ls.vsecondimpertranslit = ls.vsecondimpertranslit == null ? "" : ls.vsecondimpertranslit
        ls.vthirdinftranslit = ls.vthirdinftranslit == null ? "" : ls.vthirdinftranslit
        ls.sentencetranslit = ls.sentencetranslit == null ? "" : ls.sentencetranslit
        ls.entrytone = ls.entrytone == null ? "" : ls.entrytone
        ls.nounadjpluraltone = ls.nounadjpluraltone == null ? "" : ls.nounadjpluraltone
        ls.vfirstprestone = ls.vfirstprestone == null ? "" : ls.vfirstprestone
        ls.vthirdpasttone = ls.vthirdpasttone == null ? "" : ls.vthirdpasttone
        ls.vthirdprestone = ls.vthirdprestone == null ? "" : ls.vthirdprestone
        ls.vsecondimpertone = ls.vsecondimpertone == null ? "" : ls.vsecondimpertone
        ls.vthirdinftone = ls.vthirdinftone == null ? "" : ls.vthirdinftone
        ls.source = ls.source == null ? "" : ls.source
        ls.definitionlarge = ls.definitionlarge == null ? "" : ls.definitionlarge
        ls.etymology = ls.etymology == null ? "" : ls.etymology
        ls.notes = ls.notes == null ? "" : ls.notes
        ls.category = ls.category == null ? "" : ls.category

        if (!ls.save(flush: true)) {
            ls.errors.allErrors.each {
                println it
            }
        }
    }

    private void saveIndividualWord(SG1PRESENT, oot, pos, List ls) {
        //check to see that it's not blank and doesn't have a -
        if (SG1PRESENT && !SG1PRESENT.contains("-")) {
            def SG1PRESENT_TONE = oot.vfirstprestone
            def SG1PRESENT_TRANSLIT = oot.vfirstprestranslit
            def SG1PRESENT_TSALAGI = oot.vfirstpresg

            Word tmpWord = new Word(syllabary: SG1PRESENT, tone: SG1PRESENT_TONE, tsalagi: SG1PRESENT_TSALAGI, autotranslit: SG1PRESENT_TRANSLIT)

            PosLink pl = new PosLink(word: tmpWord, partOfSpeech: pos)
            if (!pl.save(flush: true)) {
                pl.errors.allErrors.each {
                    println it
                }
            }

            ls.each {
                VerbCategoryLink vcl = new VerbCategoryLink(verbCategory: it, word: tmpWord)
                if (!vcl.save(flush: true)) {
                    vcl.errors.allErrors.each {
                        println it
                    }
                }
            }
        }
    }

    private void saveIndividualWord(String syllabary, String tone, String translit, String tsalagi, PartOfSpeech pos, List ls) {
        //check to see that it's not blank and doesn't have a -
        if (syllabary && !syllabary.contains("-")) {
            Word tmpWord = new Word(syllabary: syllabary, tone: tone, tsalagi: tsalagi, autotranslit: translit)

            PosLink pl = new PosLink(word: tmpWord, partOfSpeech: pos)
            if (!pl.save(flush: true)) {
                pl.errors.allErrors.each {
                    println it
                }
            }

//            println "LS SIZE is: ${ls.size()}"
            ls.each {
//                println "vcl is here for ${syllabary}"
                VerbCategoryLink vcl = new VerbCategoryLink(verbCategory: it, word: tmpWord)
                if (!vcl.save(flush: true)) {
                    vcl.errors.allErrors.each {
                        println it
                    }
                }
            }
        }
    }

    private void verbs(oot, pos) {
        def SG1PRESENT = oot.vfirstpresh
        def SG3ANREMOTEPAST = oot.vthirdpastsyllj
        def SG3ANHABITUAL = oot.vthirdpressylll
        def SG2FUTURECOMMAND = oot.vsecondimpersylln
        def SG3ANINFINITIVE = oot.vthirdinfsyllp

        if (SG1PRESENT?.contains(",")) {
            println "SG1 has comma"
            def tmp = oot.vfirstpresh?.split(",")
            def tone = oot.vfirstprestone?.split(",")
            def tsalagi = oot.vfirstpresg?.split(",")
            def autotranslit = oot.vfirstprestranslit?.split(",")

            if (autotranslit && autotranslit.size() == 1 || !autotranslit) {
                autotranslit = ['', '']
            }

            tmp.eachWithIndex { str, idx ->
                println "inside tmp ${str} -- ${idx}"
                saveIndividualWord(str, tone[idx], autotranslit[idx], tsalagi[idx], pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG1', 'PRESENT']))
            }
        } else {
            saveIndividualWord(SG1PRESENT, oot, pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG1', 'PRESENT']))
        }

        if (SG3ANREMOTEPAST?.contains(",")) {

            def tmp = oot.vthirdpastsyllj?.split(",")
            def tone = oot.vthirdpasttone?.split(",")
            def tsalagi = oot.vthirdpasti?.split(",")
            def autotranslit = oot.vthirdpasttranslit?.split(",")

            if (autotranslit && autotranslit.size() == 1 || !autotranslit) {
                autotranslit = ['', '']
            }

            println "${tmp} -- ${tone} -- ${autotranslit} -- ${tsalagi}"

            tmp.eachWithIndex { str, idx ->
                println "${str} -- ${tone[idx]} -- ${autotranslit[idx]} -- ${tsalagi[idx]}"
                saveIndividualWord(str, tone[idx], autotranslit[idx], tsalagi[idx], pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG3AN', 'REMOTE_PAST']))
            }
        } else {
            saveIndividualWord(SG3ANREMOTEPAST, oot, pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG3AN', 'REMOTE_PAST']))
        }

        if (SG3ANHABITUAL?.contains(",")) {

            def tmp = oot.vthirdpressylll?.split(",")
            def tone = oot.vthirdprestone?.split(",")
            def tsalagi = oot.vthirdpresk?.split(",")
            def autotranslit = oot.vthirdprestranslit?.split(",")

            if (autotranslit && autotranslit.size() == 1 || !autotranslit) {
                autotranslit = ['', '']
            }

            tmp.eachWithIndex { str, idx ->
                saveIndividualWord(str, tone[idx], autotranslit[idx], tsalagi[idx], pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG3', 'HABITUAL']))
            }
        } else {
            saveIndividualWord(SG3ANHABITUAL, oot, pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG3', 'HABITUAL']))
        }

        if (SG2FUTURECOMMAND?.contains(",")) {

            def tmp = oot.vsecondimpersylln?.split(",")
            def tone = oot.vsecondimpertone?.split(",")
            def tsalagi = oot.vsecondimperm?.split(",")
            def autotranslit = oot.vsecondimpertranslit?.split(",")

            if (autotranslit && autotranslit.size() == 1 || !autotranslit) {
                autotranslit = ['', '']
            }

            tmp.eachWithIndex { str, idx ->
                saveIndividualWord(str, tone[idx], autotranslit[idx], tsalagi[idx], pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG2', 'FUTURE_COMMAND']))
            }
        } else {
            saveIndividualWord(SG2FUTURECOMMAND, oot, pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG2', 'FUTURE_COMMAND']))
        }

        if (SG3ANINFINITIVE?.contains(",")) {

            def tmp = oot.vthirdinfsyllp?.split(",")
            def tone = oot.vthirdinftone?.split(",")
            def tsalagi = oot.vthirdinfo?.split(",")
            def autotranslit = oot.vthirdinftranslit?.split(",")

            if (autotranslit && autotranslit.size() == 1 || !autotranslit) {
                autotranslit = ['', '']
            }

            tmp.eachWithIndex { str, idx ->
                saveIndividualWord(str, tone[idx], autotranslit[idx], tsalagi[idx], pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG3AN', 'INFINITIVE']))
            }
        } else {
            saveIndividualWord(SG3ANINFINITIVE, oot, pos, VerbCategory.findAll("from VerbCategory v where v.object=?0 and subject=?1", ['SG3AN', 'INFINITIVE']))
        }
    }
}
