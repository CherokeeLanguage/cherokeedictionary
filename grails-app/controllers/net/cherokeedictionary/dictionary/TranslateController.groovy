package net.cherokeedictionary.dictionary

import net.cherokeedictionary.dictionary.Cherokeewordsearch
import net.cherokeedictionary.dictionary.Likespreadsheets

class TranslateController {

    def index() {
        def toTranslate = params.text.trim()
        def line = toTranslate.split(" ")
        def sb = new StringBuilder()
        line.each {String it ->
            it = it.replaceAll(",", "").replaceAll("\\.", "").trim()
            sb << "<b>${it}</b><br/>"
            def lst = Cherokeewordsearch.findAll("from Cherokeewordsearch c where c.syllabaryb = ?0", ["${it}" as String])
//            def lst = Cherokeewordsearch.findAll("from Cherokeewordsearch c where c.syllabaryb like ?", ["%ᏗᎦᏍᎩᎶᎩ%" as String])

            if (lst.size() == 0) {
                sb << "EXACT MATCH NOT FOUND<br/>"
                lst = Cherokeewordsearch.findAll("from Cherokeewordsearch c where c.syllabaryb like ?0", ["%${it}%" as String])
            }

            if (lst.size() == 0) {
                sb << "LIKE SEARCH NOT FOUND<br/>"
                lst = Cherokeewordsearch.findAll("from Cherokeewordsearch c where c.syllabaryb = ?0", ["${it.substring(0, it.size() - 1)}" as String])
                if (lst.size() > 0) {
                    sb << "SEARCH OF ${it.substring(0, it.size() - 1)} RETURNED RESULTS<br/>"
                }
            }

            if (lst.size() == 0) {
                sb << "NO DEFINITION FOUND<br/><br/>"
            }

            lst.each {
                Likespreadsheets l = Likespreadsheets.findById(it.likespreadsheets_id)
                if (l != null) {
                    sb << "id: ${it.likespreadsheets_id}, entry: ${it.entrya}, syllabary: ${it.syllabaryb}, definition: ${l.definitiond}<br/>"
                } else {
                    sb << "id: ${it.likespreadsheets_id}, entry: ${it.entrya}, syllabary: ${it.syllabaryb}, definition: no definition found<br/>"
                }
            }
        }

        render([returnedValue: sb.toString()], "${sb.toString()}" as String)
    }
}
