package newsite

class CitationTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    def citationIndex = 0;
    def citationMap = [:]

    def clearCitations = {
        citationIndex = 0
        citationMap = [:]
    }

    def footnote = {
        def sb = new StringBuilder()

        sb << "<sup>${it.src}"
        if (it.link) {
            sb << "<a href=\"#${it.link}\">${it.linkTitle}</a>"
        }
        sb << "</sup>"

        out << raw(sb.toString())
    }

    def citation = {params, body ->
        def sb = new StringBuilder()
        //todo: maybe some other day figure out how I want citations
//        if (citationMap.containsValue(params.src)) {
//            def tmpCitationIndex = ""
//            citationMap.each {key, value ->
//                if (value == params.src) {
//                    tmpCitationIndex = key
//                }
//            }
//            sb << "<a name=\"#cite${citationIndex}\"></a><sup><a id=\"#cite${citationIndex}\" href=\"#ref${citationIndex}\">${citationIndex}</a></sup>"
//        } else {
            citationIndex++


            sb << "<a name=\"#cite${citationIndex}\"></a><sup><a id=\"#cite${citationIndex}\" href=\"#ref${citationIndex}\">${citationIndex}</a></sup>"
            citationMap[citationIndex] = params.src
//        }
        out << raw(sb.toString())
//        out << raw(body())
    }

    def printCitations = {
        def sb = new StringBuilder()

        citationMap.eachWithIndex {item, idx ->
            sb << "<sup id=\"ref${item.key}\">[${item.key}]</sup> ${item.value} <br/>"//<a href=\"#cite${item.key}\">go back</a>
        }

        out << raw(sb.toString())
    }

    def wordBreakdown = {params, body ->
        if (params.anchor) {
            out << raw("<a name=\"${params.anchor.replaceAll(" ", "")}\"></a>")
        }
        out << raw("<h4>Word Breakdown - ${params.title}</h4>")
        out << raw(body())
    }

    def bookChapter = { params, body ->
        out << raw("<a name=\"${params.anchor.replaceAll(" ", "")}\"></a>")
        out << raw("<h2>${params.title}</h2>")
        out << raw(body())
    }

    def redSpan = {params, body ->
        out << raw("<span style=\"color:red\">${body()}</span>")
    }

    def exercise = {params, body ->
        out << raw("<h4>Exercise - ${g.translit(src:"alisinahisdisgv digvdodi")}</h4>")
        out << raw("Translate to Cherokee syllabary and the phonetic equivalent<br/>")
        out << params.text
    }

    def bookSection = { params, body ->
        out << raw("<h4>${params.title} - ${g.translit(src:params.phonetic)}</h4>")
        out << raw(body())
    }

    def whatYouWillLearn = {params, body ->
        out << raw("<b>What You Will Learn In This Chapter</b><br/><ul>")
        def objectives = params.objectives

        objectives.each {
            out << raw("<li>${it}</li>")
        }

        out << raw("</ul>")
    }

    def dialogVocab = {params, body ->
        def sb = new StringBuilder()
        def vocabulary = params.vocab
        sb << "<div style=\"display:table-row\">"
        sb << "<div style=\"display:table-cell;padding-right:20px\">"
        sb << "<h4>Dialog - </h4>"
        sb << raw(body())
        sb << "</div>"
        sb << "<div style=\"display:table-cell\">"
        sb << g.vocabulary(src:vocabulary)
        sb << "</div>"
        sb << "</div>"

        out << raw(sb.toString())
    }

    def dialogItem = {params, body ->
        def key = params.key
        def value = params.value
        out << g.dialogLine(name: key, dialog: value, phonetic: 'true')
    }
}
