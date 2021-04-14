package newsite

import cherokee.dictionary.Likespreadsheets
import net.cherokeedictionary.transliteration.SyllabaryUtil

class LookupTagLib {
    static defaultEncodeAs = [taglib: 'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static final def su = new SyllabaryUtil();

    def lookup = {params ->
        def source = params.source
        def lst
        if (source) {
            lst = Likespreadsheets.findAll('from Likespreadsheets l where l.definitiond = ? and (source = ?)', [params.definition, source])
        } else {
            lst = Likespreadsheets.findAll('from Likespreadsheets l where l.definitiond = ? and (source = ? or source = ? or source = ? or source = ?)', [params.definition, 'ced', 'rrd', 'noq', 'cwl'])
        }

        if (lst) {
            displayToPage(lst.get(0).syllabaryb, params.definition)
        }
    }

    def lkdisplay = {params ->
        displayToPage(params.syllabary, params.definition)
    }

    def sylldisplayHover = {params ->
        StringBuilder sb = new StringBuilder()

        def trans = su.parseSyllabary(params.syllabary)
        def transLabel = trans.replaceAll(/\s/, "")
        sb << " -- "
        sb << "<div id=\"${transLabel}a\" class=\"popbox\" style=\"display: none; top: 129px; left: 231px;\">${trans}</div>"
        sb << "<a href=\"#\" class=\"popper\" id=\"${transLabel}a\" data-popbox=\"${transLabel}a\" style=\"color:blue;text-decoration:none;cursor-style:hand\">${params.syllabary}</a>"

        out << raw(sb.toString())
    }

    def displayToPage = {ls, definition ->
        StringBuilder sb = new StringBuilder()
        sb << definition
        if (ls) {
            def trans = su.parseSyllabary(ls)
            def transLabel = trans.replaceAll(/\s/, "")
            sb << " -- "
            sb << "<div id=\"${transLabel}a\" class=\"popbox\" style=\"display: none; top: 129px; left: 231px;\">${trans}</div>"
            sb << "<a href=\"#\" class=\"popper\" id=\"${transLabel}a\" data-popbox=\"${transLabel}a\" style=\"color:blue;text-decoration:none;cursor-style:hand\">${ls}</a>"
        } else {
            sb << " -- no result"
        }

        out << raw(sb.toString())
    }
}
