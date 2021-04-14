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

    def citation = {params ->
        citationIndex++
        def sb = new StringBuilder()

        sb << "<a name=\"#cite${citationIndex}\"></a><sup><a id=\"#cite${citationIndex}\" href=\"#ref${citationIndex}\">${citationIndex}</a></sup>"
        citationMap[citationIndex] = params.src

        out << raw(sb.toString())
    }

    def printCitations = {
        def sb = new StringBuilder()

        citationMap.eachWithIndex {item, idx ->
            sb << "<sup id=\"ref${item.key}\">[${item.key}]</sup> ${item.value} <br/>"//<a href=\"#cite${item.key}\">go back</a>
        }

        out << raw(sb.toString())
    }
}
