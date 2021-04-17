package newsite

import cherokee.dictionary.EnglishNumberToWords
import cherokee.dictionary.Likespreadsheets

class TodayIsTagLib {
    static defaultEncodeAs = [taglib: 'html']

    def popupString = "<div id=\"%s\" class=\"popbox\">%s</div>"
    def anchorString = "<a href=\"#\" class=\"popper\" id=\"%s\" data-popbox=\"%s\" style=\"color:blue;text-decoration:none;cursor-style:hand\">%s</a>"

    def todayIs = {params ->
        def cal = Calendar.getInstance()
        cal.setTimeZone(TimeZone.getTimeZone("America/Chicago"))
        def date = cal.get(Calendar.DATE)
        def month = cal.get(Calendar.MONTH)
        def year = cal.get(Calendar.YEAR)
        def dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        def sb = new StringBuilder()
        sb << "ᎦᏙ ᎤᏍᏗ ᎠᏎᏍᏗ ᎪᎯ? - What date is it today?<br/>"
        sb << "ᎪᎯ "
        def weekdaySyll = getWeekday(dayOfWeek)
        def dateSyll = getDate(date)
        def monthSyll = getMonth(month)
        def yearSyll = getYear(year)

        def weekdayEng = getDayOfWeek(dayOfWeek)
        def monthEng = getMonthStr(month)

        sb << formatPopupAnchor("weekdaySyll", weekdayEng, weekdaySyll)
        sb << "&nbsp;&nbsp;"
        sb << formatPopupAnchor("dateSyll", date, dateSyll)
        sb << "&nbsp;&nbsp;"
        sb << formatPopupAnchor("monthSyll", monthEng, monthSyll)
        sb << "&nbsp;&nbsp;"
        sb << formatPopupAnchor("yearSyll", year, yearSyll)

//        sb << "${getWeekday(dayOfWeek)} "
//        sb << "${getDate(date)} "
//        sb << "${getMonth(month)} "
//        sb << "${getYear(year)} "
        sb << "<br/>"

        sb << "Today is "

        sb << formatPopupAnchor("weekdayEng", weekdaySyll, weekdayEng)
        sb << "&nbsp;"
        sb << formatPopupAnchor("dateEng", dateSyll, date)
        sb << "&nbsp;"
        sb << formatPopupAnchor("monthEng", monthSyll, monthEng)
        sb << "&nbsp;"
        sb << formatPopupAnchor("yearEng", yearSyll, year)


        out << raw(sb)
    }

    private String formatPopupAnchor(title, value1, value2) {
        StringBuilder sb = new StringBuilder()
        sb << String.format(popupString, title, value1)
        sb << String.format(anchorString, title, title, value2)

        return sb.toString()
    }

    private String getMonthStr(mon) {
        switch(mon) {
            case 0:
//                return 'ᎤᏃᎸᏔᎾ'
                return 'January'
                break;
            case 1:
//                return 'ᎧᎦᎵ'
                return 'February'
                break;
            case 2:
//                return 'ᎠᏅᏱ'
                return 'March'
                break;
            case 3:
//                return 'ᎧᏬᏂ'
                return 'April'
                break;
            case 4:
//                return 'ᎠᏂᏍᎬᏘ'
                return 'May'
                break;
            case 5:
//                return 'ᏕᎭᎷᏱ'
                return 'June'
                break;
            case 6:
//                return 'ᎫᏰᏉᎾ'
                return 'July'
                break;
            case 7:
//                return 'ᎦᎶᏂ'
                return 'August'
                break;
            case 8:
//                return 'ᏚᎵᏍᏗ'
                return 'September'
                break;
            case 9:
//                return 'ᏚᏂᏂᏗ'
                return 'October'
                break;
            case 10:
//                return 'ᏅᏓᏕᏆ'
                return 'November'
                break;
            case 11:
//                return 'ᎥᏍᎩᏱ'
                return 'December'
                break;
        }
    }

    private String getMonth(mon) {
//        def monthStr = getMonthStr(mon)
//        def m2 = Likespreadsheets.all.find {
//            it.definitiond == monthStr && it.source == 'ced'
//        }.syllabaryb
        switch(mon) {
            case 0:
                return 'ᎤᏃᎸᏔᎾ'
//                return 'January'
                break;
            case 1:
                return 'ᎧᎦᎵ'
//                return 'February'
                break;
            case 2:
                return 'ᎠᏅᏱ'
//                return 'March'
                break;
            case 3:
                return 'ᎧᏬᏂ'
//                return 'April'
                break;
            case 4:
                return 'ᎠᏂᏍᎬᏘ'
//                return 'May'
                break;
            case 5:
                return 'ᏕᎭᎷᏱ'
//                return 'June'
                break;
            case 6:
                return 'ᎫᏰᏉᎾ'
//                return 'July'
                break;
            case 7:
                return 'ᎦᎶᏂ'
//                return 'August'
                break;
            case 8:
                return 'ᏚᎵᏍᏗ'
//                return 'September'
                break;
            case 9:
                return 'ᏚᏂᏂᏗ'
//                return 'October'
                break;
            case 10:
                return 'ᏅᏓᏕᏆ'
//                return 'November'
                break;
            case 11:
                return 'ᎥᏍᎩᏱ'
//                return 'December'
                break;
        }

//        return monthStr
    }

    private String getDayOfWeek(dayofweek) {
        switch (dayofweek) {
            case 1:
                return 'Sunday'
//                return 'ᏙᏓᏆᏍᎬᎢ'
                break;
            case 2:
                return 'Monday'
//                return 'ᏙᏓᏉᏅᎢ'
                break;
            case 3:
                return 'Tuesday'
//                return 'ᏔᎵᏁ ᎢᎦ'
                break;
            case 4:
                return 'Wednesday'
//                return 'ᏦᎢᏁ ᎢᎦ'
                break;
            case 5:
                return 'Thursday'
//                return 'ᏅᎩᏁ ᎢᎦ'
                break;
            case 6:
                return 'Friday'
//                return 'ᏧᏅᎩᎶᏍᏗ'
                break;
            case 7:
                return 'Saturday'
//                return 'ᏙᏓᏈᏕᎾ'
                break;
        }
    }

    private String getWeekday(dayofweek) {
//        def dayOfWeek = getDayOfWeek(dayofweek)
//        def d2 = Likespreadsheets.all.find {
//            it.definitiond == dayOfWeek && it.source == 'ced'
//        }.syllabaryb

//        return dayOfWeek
        switch (dayofweek) {
            case 1:
//                return 'Sunday'
                return 'ᏙᏓᏆᏍᎬᎢ'
                break;
            case 2:
//                return 'Monday'
                return 'ᏙᏓᏉᏅᎢ'
                break;
            case 3:
//                return 'Tuesday'
                return 'ᏔᎵᏁ ᎢᎦ'
                break;
            case 4:
//                return 'Wednesday'
                return 'ᏦᎢᏁ ᎢᎦ'
                break;
            case 5:
//                return 'Thursday'
                return 'ᏅᎩᏁ ᎢᎦ'
                break;
            case 6:
//                return 'Friday'
                return 'ᏧᏅᎩᎶᏍᏗ'
                break;
            case 7:
//                return 'Saturday'
                return 'ᏙᏓᏈᏕᎾ'
                break;
        }
    }

    private String getDate(datum) {
        def date = EnglishNumberToWords.convert(datum)
        def month = ""
        def monthSize = 0
        def	day = ""

        if (date != 'twenty' && date != 'twenty one' && date != 'thirty one' && date != 'thirty') {
            if (date.startsWith("twenty")) {
                month = "ᏔᎵᏍᎪ "
                monthSize = 7
            } else if (date.startsWith("thirty")) {
                month = "ᏦᎣᏍᎪ "
                monthSize = 7
            }
        }

//        def dateTrim = date != 'twenty' && date != 'twenty one' && date != 'thirty one' && date != 'thirty' ? date.substring(monthSize).trim() : date.trim()
        def dateTrim = date.substring(monthSize).trim()

        switch(dateTrim) { 
            case "one":
				day = "ᎢᎬᏱᎢ"
            	break;
			case "two":
				day = "ᏔᎵᏁᎢ"
            	break;
			case "three":
				day = "ᏦᎢᏁᎢ"
            	break;
			case "four":
				day = "ᏅᎩᏁᎢ"
            	break;
			case "five":
				day = "ᎯᏍᎩᏁᎢ"
            	break;
			case "six":
				day = "ᏑᏓᎵᏁᎢ"
            	break;
			case "seven":
				day = "ᎦᎵᏉᎩᏁᎢ"
            	break;
			case "eight":
				day = "ᏧᏁᎵᏁᎢ"
            	break;
			case "nine":
				day = "ᏐᏁᎵᏁᎢ"
            	break;
			case "ten":
				day = "ᏍᎪᎯᏁᎢ"
            	break;
			case "eleven":
				day = "ᏌᏚᏏᏁᎢ"
            	break;
			case "twelve":
				day = "ᏔᎵᏚᏏᏁᎢ"
            	break;
			case "thirteen":
				day = "ᏦᎦᏚᏏᏁᎢ"
            	break;
			case "fourteen":
				day = "ᏂᎦᏚᏏᏁᎢ"
            	break;
			case "fifteen":
				day = "ᏍᎩᎦᏚᏏᏁᎢ"
            	break;
			case "sixteen":
				day = "ᏓᎳᏚᏏᏁᎢ"
            	break;
			case "seventeen":
				day = "ᎦᎵᏆᏚᏏᏁᎢ"
            	break;
			case "eighteen":
				day = "ᏁᎳᏚᏏᏁᎢ"
            	break;
			case "nineteen":
				day = "ᏐᏁᎳᏚᏏᏁᎢ"
            	break;
			case "twenty":
				day = "ᏔᎵᏍᎪᎯᏁᎢ"
            	break;
			case "twenty one":
				day = "ᏔᎵᏍᎪ ᏌᏊᎯᏁᎢ"
            	break;
            case "thirty":
                day = "ᏦᏍᎪᎯᏁᎢ"
                break;
			case "thirty one":
				day = "ᏦᏍᎪ ᏌᏊᎯᏁᎢ"
            break;
        }

        return String.format("%s %s", month, day).trim()
    }

    private String getYear(yahr) {
        def sb = new StringBuilder()
        sb << "ᏔᎵ ᎢᏯᎦᏴᎵ "

        if (yahr == 2018) {
            sb << "ᏁᎳᏚᎯ"
        } else if (yahr == 2019) {
            sb << "ᏐᎣᏁᎳᏚᎯ"
        } else if (yahr == 2020) {
            sb << "ᏔᎵᏍᎪᎯ"
        }
//        def yearConverted = EnglishNumberToWords.convert(yahr)
//        def thousands = yearConverted.substring(0, yearConverted.indexOf("thousand") + 8)
//        def year = yearConverted.substring(thousands.size())
//        year = year.trim()
//
//        switch (year == "eighteen") {
//            case "eighteen":
//                return "ᏁᎳᏚᎯ"
//                break;
//            case "nineteen":
//                return "ᏐᎣᏁᎳᏚᎯ"
//                break;
//            case "twenty":
//                return ""
//                break;
//            case "twenty one":
//                return ""
//                break;
//        }

//        def lst = Likespreadsheets.findAllByDefinitiond(year)
//        if (lst?.size()) {
//            sb << lst.get(0).syllabaryb
//        }

        return sb.toString()
    }
}

//        sb << "<br/><br/><br/>"
//        sb << "${getDate(1)}<br/>"
//        sb << "${getDate(2)}<br/>"
//        sb << "${getDate(3)}<br/>"
//        sb << "${getDate(4)}<br/>"
//        sb << "${getDate(5)}<br/>"
//        sb << "${getDate(6)}<br/>"
//        sb << "${getDate(7)}<br/>"
//        sb << "${getDate(8)}<br/>"
//        sb << "${getDate(9)}<br/>"
//        sb << "${getDate(10)}<br/>"
//
//        sb << "${getDate(11)}<br/>"
//        sb << "${getDate(12)}<br/>"
//        sb << "${getDate(13)}<br/>"
//        sb << "${getDate(14)}<br/>"
//        sb << "${getDate(15)}<br/>"
//        sb << "${getDate(16)}<br/>"
//        sb << "${getDate(17)}<br/>"
//        sb << "${getDate(18)}<br/>"
//        sb << "${getDate(19)}<br/>"
//
//        sb << "${getDate(20)}<br/>"
//        sb << "${getDate(21)}<br/>"
//        sb << "${getDate(22)}<br/>"
//        sb << "${getDate(23)}<br/>"
//        sb << "${getDate(24)}<br/>"
//        sb << "${getDate(25)}<br/>"
//        sb << "${getDate(26)}<br/>"
//        sb << "${getDate(27)}<br/>"
//        sb << "${getDate(28)}<br/>"
//        sb << "${getDate(29)}<br/>"
//        sb << "${getDate(30)}<br/>"
//        sb << "${getDate(31)}<br/>"

//        def f = new File("./daysAndMonths.txt")
//        f.write("", "UTF-8")
//        f.append(getMonth(0) + "\n", "UTF-8")
//        f.append(getMonth(1) + "\n", "UTF-8")
//        f.append(getMonth(2) + "\n", "UTF-8")
//        f.append(getMonth(3) + "\n", "UTF-8")
//        f.append(getMonth(4) + "\n", "UTF-8")
//        f.append(getMonth(5) + "\n", "UTF-8")
//        f.append(getMonth(6) + "\n", "UTF-8")
//        f.append(getMonth(7) + "\n", "UTF-8")
//        f.append(getMonth(8) + "\n", "UTF-8")
//        f.append(getMonth(9) + "\n", "UTF-8")
//        f.append(getMonth(10) + "\n", "UTF-8")
//        f.append(getMonth(11) + "\n", "UTF-8")
//
//        f.append(getWeekday(1) + "\n", "UTF-8")
//        f.append(getWeekday(2) + "\n", "UTF-8")
//        f.append(getWeekday(3) + "\n", "UTF-8")
//        f.append(getWeekday(4) + "\n", "UTF-8")
//        f.append(getWeekday(5) + "\n", "UTF-8")
//        f.append(getWeekday(6) + "\n", "UTF-8")
//        f.append(getWeekday(7) + "\n", "UTF-8")