package cherokee.dictionary

import grails.converters.JSON
import net.cherokeedictionary.dateTime.CherokeeDateTimeProcessor
import org.grails.web.json.JSONObject
import org.springframework.boot.jackson.JsonObjectDeserializer

class ApiManagementController {
    def sourceManagement() {
        def lst = SourceManagement.findAll()
        renderJson(lst)
    }

    def announcements() {
        def lst = Announcements.findAll()
        renderJson(lst)
    }

    def renderJson(searchResults) {
        if (params.callback) {
            render (text: "${params.callback}($searchResults)",
                    contentType: "text/javascript",
                    encoding: "UTF-8")
        } else {
            render (searchResults as JSON)
        }
    }

    def popupString = "<div id=\"%s\" class=\"popbox\">%s</div>"
//    style="color:blue;text-decoration:none;cursor:hand"
    // style={{"color":"blue","textDecoration":"none", "cursor":"hand"}}
    def anchorString = "<a href=\"#\" class=\"popper\" id=\"%s\" data-popbox=\"%s\" >%s</a>"

    def dateTime() {
        def cherokeeDateTime = CherokeeDateTimeProcessor.getDateTime()
        def lst = new StringBuilder()
        JSONObject jsonObject = new JSONObject()

        jsonObject.put("first", "ᎦᏙ ᎤᏍᏗ ᎠᏎᏍᏗ ᎪᎯ? - What date is it today?<br/>ᎪᎯ ")
        jsonObject.put("weekdayEng", cherokeeDateTime.weekdayEng)
        jsonObject.put("weekdaySyll", cherokeeDateTime.weekdaySyll)
        jsonObject.put("dateSyll", cherokeeDateTime.dateSyll)
        jsonObject.put("dateEng", cherokeeDateTime.date)
        jsonObject.put("monthSyll", cherokeeDateTime.monthSyll)
        jsonObject.put("monthEng", cherokeeDateTime.monthEng)
        jsonObject.put("yearSyll", cherokeeDateTime.yearSyll)
        jsonObject.put("yearEng", cherokeeDateTime.year)

//        lst << formatPopupAnchor("weekdaySyll", cherokeeDateTime.weekdayEng, cherokeeDateTime.weekdaySyll)
//        lst << "&nbsp;&nbsp;"
//        lst << formatPopupAnchor("dateSyll", cherokeeDateTime.date, cherokeeDateTime.dateSyll)
//        lst << "&nbsp;&nbsp;"
//        lst << formatPopupAnchor("monthSyll", cherokeeDateTime.monthEng, cherokeeDateTime.monthSyll)
//        lst << "&nbsp;&nbsp;"
//        lst << formatPopupAnchor("yearSyll", cherokeeDateTime.year, cherokeeDateTime.yearSyll)
//
//        lst << "<br/>"
//
//        lst << "Today is "
//
//        lst << formatPopupAnchor("weekdayEng", cherokeeDateTime.weekdaySyll, cherokeeDateTime.weekdayEng)
//        lst << "&nbsp;"
//        lst << formatPopupAnchor("dateEng", cherokeeDateTime.dateSyll, cherokeeDateTime.date)
//        lst << "&nbsp;"
//        lst << formatPopupAnchor("monthEng", cherokeeDateTime.monthSyll, cherokeeDateTime.monthEng)
//        lst << "&nbsp;"
//        lst << formatPopupAnchor("yearEng", cherokeeDateTime.yearSyll, cherokeeDateTime.year)
        render jsonObject as JSON
//        renderJson([lst.toString()])
    }

    private String formatPopupAnchor(title, value1, value2) {
        StringBuilder sb = new StringBuilder()
        sb << String.format(popupString, title, value1)
        sb << String.format(anchorString, title, title, value2)

        return sb.toString()
    }
}