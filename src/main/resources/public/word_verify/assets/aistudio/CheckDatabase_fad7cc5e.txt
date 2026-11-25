package net.cherokeedictionary.sql

import groovy.sql.Sql
import net.cherokeedictionary.core.Verb
import net.cherokeedictionary.stemmer.DefinitionLine
import net.cherokeedictionary.stemmer.Stemmer
import net.cherokeedictionary.util.CompoundPrefixes
import net.cherokeedictionary.util.PrefixTableSubject
import net.cherokeedictionary.util.PrefixTableObject
import net.cherokeedictionary.verb.conjugation.Conjugate

class CheckDatabase extends GroovyTestCase {
    static def start = System.currentTimeSeconds()
    static Set prefices = CompoundPrefixes.prefixes.keySet()

    static {
        println "start ${start}"
    }

    def DRIVER_NAME = "com.mysql.cj.jdbc.Driver"
    def dbUrl = 'jdbc:mysql://127.0.0.1:3306/cherokeedictionary?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=America/Chicago&useSSL=false&allowPublicKeyRetrieval=true'
    def dbUser = 'root'
    def dbPassword = 'Tk02030#'

    def findAllQuery = "select * from Likespreadsheets where partofspeechc = 'vi' or partofspeechc = 'vt' or partofspeechc like 'v.i%' or partofspeechc like 'v.t%'"
    def tryOneQuery = "select * from Likespreadsheets where id=72329"

    def insertQuery = "INSERT INTO `verb_verify` (`id`, `version`, `lks_id`, `conjugation`, `tense`, `citation`) VALUES(<{id: }>, <{version: 1}>, <{lks_id: }>, <{conjugation: }>, <{tense: }>, <{citation: }>);"

    def SQL = Sql.newInstance(
            dbUrl,
            dbUser,
            dbPassword,
            DRIVER_NAME
    )

    def createInsertWordVerify(List found) {
        if (!found.isEmpty()) {
//            println "${verb.compoundPrefix} - ${verb.wholeWord}"
            found.each {
                println "\t${it}"
            }
        }
    }

    def createConjuateQuery(Verb verb, likespreadsheets_id) {
        if (verb.wholeWord) {
            def found = []

            SQL.eachRow("select id, corpus_source_id, syllabary from NLPCorpus where syllabary like ?1", ["%${verb.wholeWord}%" as String]) {
                found << "${it.id} ${it.corpus_source_id} ${it.syllabary}" as String
            }

            SQL.eachRow("select id, book_name, verse_number, chapter_number, verse_context from verse where source=?1 and verse_context like ?2", ['chr', "%${verb.wholeWord}%" as String]) {
                found << "${it.id} ${it.book_name} ${it.chapter_number}:${it.verse_number}  -- ${it.verse_context}" as String
            }

            createInsertWordVerify(found)
        }
    }

    def conjugate(habitual, imperative, infinitive, present1st, present3rd, remotepast, String partOfSpeech, likespreadsheets_id) {
        def verbName = new Stemmer()
        verbName.habitual = new DefinitionLine(syllabary: habitual)
        verbName.imperative = new DefinitionLine(syllabary: imperative)
        verbName.infinitive = new DefinitionLine(syllabary: infinitive)
        verbName.present1st = new DefinitionLine(syllabary: present1st)
        verbName.present3rd = new DefinitionLine(syllabary: present3rd)
        verbName.remotepast = new DefinitionLine(syllabary: remotepast)

        PrefixTableSubject.values().each {subj ->
            PrefixTableObject.values().each {obj ->
//                println "${subj.toString()}${obj.toString()}"
                if (prefices.contains("${subj.toString()}${obj.toString()}" as String)) {
                    def verb = Conjugate.createVerbToConjugate(subj.toString(), obj.toString(), verbName, "PRESENT", partOfSpeech, false)
                    verb = Conjugate.conjugate(verb)
                    createConjuateQuery(verb, likespreadsheets_id)
                }
            }
        }
    }

    void testConnection() {
        SQL.eachRow(tryOneQuery) { row ->
            def habitual = row.vthirdpressylll
            def imperative = row.vsecondimpersylln
            def infinitive = row.vthirdinfsyllp
            def present1st = row.vfirstpresh
            def present3rd = row.syllabaryb
            def remotepast = row.vthirdpastsyllj

            conjugate(habitual, imperative, infinitive, present1st, present3rd, remotepast, row.partofspeechc, row.id)
        }

        println "total run time: ${System.currentTimeSeconds() - start}"
    }
}
