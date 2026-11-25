package net.cherokeedictionary.sql

import groovy.sql.Sql
import grails.converters.JSON

class WordLookupController {
    def DRIVER_NAME = "com.mysql.cj.jdbc.Driver"
    def dbUrl = 'jdbc:mysql://127.0.0.1:3306/cherokeedictionary?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=America/Chicago&useSSL=false&allowPublicKeyRetrieval=true'
    def dbUser = 'root'
    def dbPassword = 'Tk02030#'

    def lookup = {
        String q = params.q?.trim()
        if (!q) {
            render([matches: [], dictionaryEntry: null] as JSON)
            return
        }

        def SQL = Sql.newInstance(dbUrl, dbUser, dbPassword, DRIVER_NAME)
        
        def dictMatch = SQL.firstRow("select * from Likespreadsheets where syllabaryb like ? or vthirdpressylll like ? or english like ?", ["%${q}%", "%${q}%", "%${q}%"])
        def matches = []

        SQL.eachRow("select id, corpus_source_id, syllabary, english_translation from NLPCorpus where syllabary like ?", ["%${q}%"]) { c ->
            matches << [source: 'NLPCorpus', id: c.id, citation: "NLPCorpus #${c.id}", text: c.syllabary, translation: c.english_translation]
        }

        SQL.eachRow("select id, book_name, chapter_number, verse_number, verse_context, english_translation from verse where source='chr' and verse_context like ?", ["%${q}%"]) { v ->
            matches << [source: 'Verse', id: v.id, citation: "${v.book_name} ${v.chapter_number}:${v.verse_number}", text: v.verse_context, translation: v.english_translation]
        }

        render([query: q, dictionaryEntry: dictMatch, matches: matches] as JSON)
    }
}
