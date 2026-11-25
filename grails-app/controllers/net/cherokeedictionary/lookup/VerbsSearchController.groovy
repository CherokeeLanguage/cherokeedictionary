package net.cherokeedictionary.lookup

import groovy.sql.Sql
import net.cherokeedictionary.core.Verb
import net.cherokeedictionary.stemmer.DefinitionLine
import net.cherokeedictionary.stemmer.Stemmer
import net.cherokeedictionary.util.CompoundPrefixes
import net.cherokeedictionary.util.PrefixTableSubject
import net.cherokeedictionary.util.PrefixTableObject

class VerbsSearchController {
    static def start = System.currentTimeSeconds()
    static Set prefices = CompoundPrefixes.prefixes.keySet()

    def DRIVER_NAME = "com.mysql.cj.jdbc.Driver"
    def dbUrl = 'jdbc:mysql://127.0.0.1:3306/cherokeedictionary?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=America/Chicago&useSSL=false&allowPublicKeyRetrieval=true'
    def dbUser = 'root'
    def dbPassword = 'Tk02030#'

    def index = {
        def SQL = Sql.newInstance(dbUrl, dbUser, dbPassword, DRIVER_NAME)
        def rows = SQL.rows("select * from Likespreadsheets where partofspeechc = 'vi' or partofspeechc = 'vt' or partofspeechc like 'v.i%' or partofspeechc like 'v.t%'")
        
        int foundCount = 0
        rows.each { row ->
            def habitual = row.vthirdpressylll
            def present3rd = row.syllabaryb

            [habitual, present3rd].each { word ->
                if (word) {
                    SQL.eachRow("select id, corpus_source_id, syllabary from NLPCorpus where syllabary like ?1", ["%${word}%"]) { corpus ->
                        def exists = SQL.firstRow("select id from verb_verify where lks_id = ? and citation = ?", [row.id, "NLPCorpus #" + corpus.id])
                        if (!exists) {
                            SQL.execute("INSERT INTO verb_verify (version, lks_id, conjugation, tense, citation) VALUES (1, ?, ?, 'PRESENT', ?)", [row.id, word, "NLPCorpus #" + corpus.id])
                            foundCount++
                        }
                    }

                    SQL.eachRow("select id, book_name, verse_number, chapter_number, verse_context from verse where source=?1 and verse_context like ?2", ['chr', "%${word}%"]) { verse ->
                        def citation = "${verse.book_name} ${verse.chapter_number}:${verse.verse_number}"
                        def exists = SQL.firstRow("select id from verb_verify where lks_id = ? and citation = ?", [row.id, citation])
                        if (!exists) {
                            SQL.execute("INSERT INTO verb_verify (version, lks_id, conjugation, tense, citation) VALUES (1, ?, ?, 'PRESENT', ?)", [row.id, word, citation])
                            foundCount++
                        }
                    }
                }
            }
        }
        render(text: "VerbsSearchController executed successfully. Inserted ${foundCount} verified matches.")
    }
}
