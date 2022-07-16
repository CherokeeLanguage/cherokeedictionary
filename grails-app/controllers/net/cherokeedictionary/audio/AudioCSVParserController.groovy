package net.cherokeedictionary.audio

import groovy.sql.Sql

/**
 * Parses a CSV and replace into the database for the MCO pronunciation lookup
 */
class AudioCSVParserController {

    def dataSource

    def index() {

    }

    def upload() {
        def file = request.getFile('file')
        def startTime = System.currentTimeMillis()

        def samesList = []
        def count = 0
        //https://www.danvega.dev/blog/2013/08/21/writing-sql-in-a-grails-application/
        def sql = new Sql(dataSource)
        file.getInputStream().splitEachLine(",") {fields ->
            def query = "REPLACE INTO audio_lookup (id, version, mco_Pronunciation, mco_Translit, gender, voice, audio_File, duration) VALUES (0, 0, '${fields[0]}', '${fields[1]}', '${fields[2]}', '${fields[3]}', '${fields[4]}', '${fields[5]}');"
            sql.execute(query)
            count++
        }

        def endTime = System.currentTimeMillis()
        long minutes = (long) (((endTime - startTime) / 1000) / 60);
        long seconds = ((long) ((endTime - startTime) / 1000)) % 60;
        println "the import took ${minutes} minutes and ${seconds} seconds to complate ${count} records"
        return [minutes: minutes, seconds: seconds, count: count]
    }
}
