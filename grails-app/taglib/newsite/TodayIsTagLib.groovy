package newsite

import net.cherokeedictionary.dateTime.CherokeeDateTimeProcessor

class TodayIsTagLib {
    static defaultEncodeAs = [taglib: 'html']

    def popupString = "<div id=\"%s\" class=\"popbox\">%s</div>"
    def anchorString = "<a href=\"#\" class=\"popper\" id=\"%s\" data-popbox=\"%s\" style=\"color:blue;text-decoration:none;cursor-style:hand\">%s</a>"

    def todayIs = { params ->
        def cherokeeDateTime = CherokeeDateTimeProcessor.getDateTime()

        def sb = new StringBuilder()
        sb << "ᎦᏙ ᎤᏍᏗ ᎠᏎᏍᏗ ᎪᎯ? - What date is it today?<br/>"
        sb << "ᎪᎯ "
        sb << formatPopupAnchor("weekdaySyll", cherokeeDateTime.weekdayEng, cherokeeDateTime.weekdaySyll)
        sb << "&nbsp;&nbsp;"
        sb << formatPopupAnchor("dateSyll", cherokeeDateTime.date, cherokeeDateTime.dateSyll)
        sb << "&nbsp;&nbsp;"
        sb << formatPopupAnchor("monthSyll", cherokeeDateTime.monthEng, cherokeeDateTime.monthSyll)
        sb << "&nbsp;&nbsp;"
        sb << formatPopupAnchor("yearSyll", cherokeeDateTime.year, cherokeeDateTime.yearSyll)

        sb << "<br/>"

        sb << "Today is "

        sb << formatPopupAnchor("weekdayEng", cherokeeDateTime.weekdaySyll, cherokeeDateTime.weekdayEng)
        sb << "&nbsp;"
        sb << formatPopupAnchor("dateEng", cherokeeDateTime.dateSyll, cherokeeDateTime.date)
        sb << "&nbsp;"
        sb << formatPopupAnchor("monthEng", cherokeeDateTime.monthSyll, cherokeeDateTime.monthEng)
        sb << "&nbsp;"
        sb << formatPopupAnchor("yearEng", cherokeeDateTime.yearSyll, cherokeeDateTime.year)

        out << raw(sb)
    }

    private String formatPopupAnchor(title, value1, value2) {
        StringBuilder sb = new StringBuilder()
        sb << String.format(popupString, title, value1)
        sb << String.format(anchorString, title, title, value2)

        return sb.toString()
    }
}