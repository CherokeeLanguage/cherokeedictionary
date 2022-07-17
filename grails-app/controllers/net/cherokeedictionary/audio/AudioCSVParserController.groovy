package net.cherokeedictionary.audio

import groovy.sql.Sql

//import au.com.bytecode.opencsv.CSVReader
//import com.xlson.groovycsv.CsvParser
//import com.xlson.groovycsv.CsvIterator

/**
 * Parses a CSV and replace into the database for the MCO pronunciation lookup
 */
class AudioCSVParserController {

    def dataSource

    def index() {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {

        }
    }

    def upload() {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            def file = request.getFile('file')
            def startTime = System.currentTimeMillis()

            def samesList = []
            def count = 0
            //https://www.danvega.dev/blog/2013/08/21/writing-sql-in-a-grails-application/
            def sql = new Sql(dataSource)

//        def fis = file.getInputStream()
//        def is = new InputStreamReader(fis)
//        def br = new BufferedReader(is)
//        def csvData = CsvParser.parseCsv( br )

//        csvData.each {
////            sql.call("{call AudioLookup('${it.MCO_PRONUNCIATION}', '${it.VOICE}', '${it.MCO_TRANSLIT}', '${it.GENDER}', '${it.AUDIO_FILE}', '${it.DURATION.replaceAll("\"", "")}')}")
//            def query = "REPLACE INTO audio_lookup (id, version, mco_Pronunciation, mco_Translit, gender, voice, audio_File, duration) VALUES (0, 0, '${it.MCO_PRONUNCIATION}', '${it.VOICE}', '${it.MCO_TRANSLIT}', '${it.GENDER}', '${it.AUDIO_FILE}', '${it.DURATION.replaceAll("\"", "")}');"
//            sql.execute(query)
//            count++
//        }

            file.getInputStream().eachLine {
                def durIndex = it.indexOf("\"")

                if (durIndex != -1) {
                    def duration = it.substring(durIndex)
                    duration = duration.replaceAll("\"", "")

                    it = it.substring(0, durIndex)

                    def split = it.split(",")
                    def mco_Pronunciation = split[0]
                    def mco_Translit = split[1]
                    def gender = split[2]
                    def voice = split[3]
                    def audio_File = split[4]

//                def query = "REPLACE INTO audio_lookup (id, version, mco_Pronunciation, mco_Translit, gender, voice, audio_File, duration) VALUES (0, 0, '${mco_Pronunciation}', '${mco_Translit}', '${gender}', '${voice}', '${audio_File}', '${duration}');"
//                sql.execute(query)

                    def query = "REPLACE INTO audio_lookup (id, version, mco_Pronunciation, mco_Translit, gender, voice, audio_File, duration) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8);"
                    sql.execute(query, [0, 0, mco_Pronunciation, mco_Translit, gender, voice, audio_File, duration])
//                    sql.call("{call AudioLookup('${mco_Pronunciation}', '${voice}', '${mco_Translit}', '${gender}', '${audio_File}', '${duration}')}")
                }

                count++
            }

            def endTime = System.currentTimeMillis()
            long minutes = (long) (((endTime - startTime) / 1000) / 60);
            long seconds = ((long) ((endTime - startTime) / 1000)) % 60;

            return [minutes: minutes, seconds: seconds, count: count]
        }
    }
}
