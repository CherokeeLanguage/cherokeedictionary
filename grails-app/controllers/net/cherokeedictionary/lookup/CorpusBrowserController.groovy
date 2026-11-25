package net.cherokeedictionary.lookup

import groovy.sql.Sql
import grails.converters.JSON

class CorpusBrowserController {
    def DRIVER_NAME = "com.mysql.cj.jdbc.Driver"
    def dbUrl = 'jdbc:mysql://127.0.0.1:3306/cherokeedictionary?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=America/Chicago&useSSL=false&allowPublicKeyRetrieval=true'
    def dbUser = 'root'
    def dbPassword = 'Tk02030#'

    def entries = {
        String source = params.source ?: 'verse'
        String book = params.book ?: 'Genesis'
        int chapter = params.chapter ? params.chapter as int : 1
        Integer verse = params.verse ? params.verse as int : null

        def SQL = Sql.newInstance(dbUrl, dbUser, dbPassword, DRIVER_NAME)
        def result = []

        if (source == 'nlp') {
            SQL.eachRow("select id, corpus_source_id, syllabary, english_translation from NLPCorpus") { c ->
                result << [id: c.id, citation: "NLPCorpus #${c.id}", text: c.syllabary, translation: c.english_translation, source_type: 'nlp']
            }
        } else {
            String sql = "select id, book_name, chapter_number, verse_number, verse_context, english_translation from verse where source='chr' and book_name = ? and chapter_number = ?"
            def queryArgs = [book, chapter]
            if (verse) {
                sql += " and verse_number = ?"
                queryArgs << verse
            }
            SQL.eachRow(sql, queryArgs) { v ->
                result << [id: v.id, citation: "${v.book_name} ${v.chapter_number}:${v.verse_number}", text: v.verse_context, translation: v.english_translation, source_type: 'verse']
            }
        }

        render(result as JSON)
    }
}
