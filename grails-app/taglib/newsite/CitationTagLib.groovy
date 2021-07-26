package newsite

import cherokee.dictionary.taglibUtil.CitationUtil

class CitationTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    CitationUtil citationUtil = new CitationUtil()

    def printVersion = {
        citationUtil.printVersion(it)
    }

    def clearCitations = {
        citationUtil.clearCitations(it)
    }

    def footnote = {
        out << raw(citationUtil.footnote(it))
    }

    def br = {
        def sb = new StringBuilder()
        if (citationUtil.isPrintVersion) {
            sb << "\\\\"
        }

        sb << raw("<br/>")
        out << raw(sb.toString())
    }

    def citation = {params, body ->
        out << raw(citationUtil.citation(params, body))
    }

    def printCitations = {
        out << raw(citationUtil.printCitations(it))
    }

    def wordBreakdown = {params, body ->
        out << raw(citationUtil.wordBreakdown(params, body))
    }

    def bookChapter = { params, body ->
        out << raw(citationUtil.bookChapter(params, body))
    }

    def redSpan = {params, body ->
        out << raw(citationUtil.redSpan(params, body))
    }

    def exercise = {params, body ->
        out << raw(citationUtil.exercise(params, body))
    }

    def bookSection = { params, body ->
        out << raw(citationUtil.bookSection(params, body))
    }

    def transl = {params ->
        out << raw(citationUtil.transl(params))
    }

    def whatYouWillLearn = {params, body ->
        out << raw(citationUtil.whatYouWillLearn(params, body))
    }

//    def dialogVocab = {params, body ->
//        out << raw(citationUtil.dialogVocab(params, body))
//    }
//
//    def dialogLine = {params ->
//        out << raw(citationUtil.dialogLine(params))
//    }

    def vocabulary = {params ->
        out << raw(citationUtil.vocabulary(params))
    }

    def dialogItem = {params, body ->
        out << citationUtil.dialogItem(params, body)
    }

    def answerKeyPrint = {
        out << raw(citationUtil.answerKeyPrint(it))
    }

    def reader = {
        out << raw(citationUtil.reader(it))
    }
}
