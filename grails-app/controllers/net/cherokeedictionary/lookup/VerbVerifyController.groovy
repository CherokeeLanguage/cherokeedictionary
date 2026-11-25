package net.cherokeedictionary.lookup

import groovy.sql.Sql

class VerbVerifyController {
    def DRIVER_NAME = "com.mysql.cj.jdbc.Driver"
    def dbUrl = 'jdbc:mysql://127.0.0.1:3306/cherokeedictionary?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=America/Chicago&useSSL=false&allowPublicKeyRetrieval=true'
    def dbUser = 'root'
    def dbPassword = 'Tk02030#'

    def index = {
        def SQL = Sql.newInstance(dbUrl, dbUser, dbPassword, DRIVER_NAME)
        def query = '''
            SELECT vv.id, vv.lks_id, vv.conjugation, vv.tense, vv.citation,
                   lks.english, lks.partofspeechc, lks.transliteration
            FROM verb_verify vv
            JOIN Likespreadsheets lks ON vv.lks_id = lks.id
            ORDER BY vv.id DESC
        '''
        def verifiedMatches = SQL.rows(query)
        
        [verifiedMatches: verifiedMatches, totalCount: verifiedMatches.size()]
    }
}
