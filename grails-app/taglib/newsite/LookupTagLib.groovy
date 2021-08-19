package newsite

import net.cherokeedictionary.dictionary.Likespreadsheets
import net.cherokeedictionary.transliteration.SyllabaryUtil

class LookupTagLib {
    static defaultEncodeAs = [taglib: 'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static def popbox = {String transLabel, String trans ->"<div id=\"${transLabel}a\" class=\"popbox\" style=\"display: none; top: 129px; left: 231px;\">${trans}</div>"}

    static def popLink = {String transLabel, String trans, ls -> "<a href=\"#\" class=\"popper\" id=\"${transLabel}a\" data-popbox=\"${transLabel}a\" style=\"color:blue;text-decoration:none;cursor-style:hand\">${ls}</a>"}

    def lookup = {params ->
        def source = params.source
        def lst
        if (source) {
            lst = Likespreadsheets.findAll('from Likespreadsheets l where l.definitiond = ?0 and (source = ?1)', [params.definition, source])
        } else {
            lst = Likespreadsheets.findAll('from Likespreadsheets l where l.definitiond = ?0 and (source = ?1 or source = ?2 or source = ?3 or source = ?4)', [params.definition, 'ced', 'rrd', 'noq', 'cwl'])
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

        def trans = SyllabaryUtil.parseSyllabary(params.syllabary)
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
            String trans = SyllabaryUtil.parseSyllabary(ls)
            def transLabel = trans.replaceAll(/\s/, "")
            sb << " -- "
            sb << popbox(transLabel, trans)
            sb << popLink(transLabel, trans, ls)
        } else {
            sb << " -- no result"
        }

        out << raw(sb)
    }
}
