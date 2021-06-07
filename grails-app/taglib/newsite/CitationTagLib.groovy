package newsite

import net.cherokeedictionary.transliteration.SyllabaryUtil

import java.util.regex.Matcher
import java.util.regex.Pattern

class CitationTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    def citationIndex = 0
    def citationMap = [:]
    def isPrintVersion = false

    def printVersion = {
        isPrintVersion = it.trueFalse && it.trueFalse=="true"
    }

    def clearCitations = {
        citationIndex = 0
        citationMap = [:]
    }

    def footnote = {
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\footnote{${it.src}"
            if (it.link) {
                sb << "\\hyperref[sec:${it.link}]{${it.linkTitle}}"
            }
            sb << "}"
        } else {
            sb << "<sup>${it.src}"
            if (it.link) {
                sb << "<a href=\"#${it.link}\">${it.linkTitle}</a>"
            }
            sb << "</sup>"
        }

        out << raw(sb.toString())
    }

    def citation = {params, body ->
        def sb = new StringBuilder()
        citationIndex++

        if (isPrintVersion) {
            sb << "\\cite{${params.title}}"
        } else {
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
            sb << "<a name=\"#cite${params.title}\"></a><sup><a id=\"#cite${params.title}\" href=\"#ref${params.title}\">${citationIndex}</a></sup>"
//        }
        }

        citationMap[params.title] = params.src

        out << raw(sb.toString())
    }

    def printCitations = {
        def sb = new StringBuilder()

        if (isPrintVersion) {
            sb << "%+Bibliography<br/>\\begin{thebibliography}{99}<br/>"
            println citationMap.size()
            citationMap.eachWithIndex { item, idx ->
                def value = item.value.replaceAll("&", "\\\\&")
                sb << "\\bibitem{${item.key}} ${value}<br/>"
//                sb << "<sup id=\"ref${item.key}\">[${item.key}]</sup> ${item.value} <br/>"//<a href=\"#cite${item.key}\">go back</a>
            }
            sb << "<br/>\\end{thebibliography}<br/>%-Bibliography<br/>"
        } else {
            citationMap.eachWithIndex { item, idx ->
                sb << "<sup id=\"ref${item.key}\">[${idx + 1}]</sup> ${item.value} <br/>"//<a href=\"#cite${item.key}\">go back</a>
            }
        }

        out << raw(sb.toString())
    }

    def wordBreakdown = {params, body ->
        def sb = new StringBuilder()
        if (isPrintVersion) {
            if (params.anchor) {
                sb << "\\label{sec:${params.anchor.replaceAll(" ", "")}}"
            }
            sb << "\\section{Word Breakdown - ${params.title}}"
            sb << body()

        } else {
            if (params.anchor) {
                sb << "<a name=\"${params.anchor.replaceAll(" ", "")}\"></a>"
            }
            sb << "<h4>Word Breakdown - ${params.title}</h4>"
            sb << body()
        }
        out << raw(sb.toString())
    }

    def bookChapter = { params, body ->
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\chapter{${params.title} - \\Cherokee ${SyllabaryUtil.tsalagiToSyllabary(params.titletranslit)} \\selectfont}<br/>"
            sb << body()
        } else {
            sb << "<a name=\"${params.anchor.replaceAll(" ", "")}\"></a>"
            sb << "<h2>${params.title} - ${SyllabaryUtil.tsalagiToSyllabary(params.titletranslit)}</h2>"
            sb << body()
        }

        out << raw(sb.toString())
    }

    def redSpan = {params, body ->
        def sb = new StringBuilder()

        if (isPrintVersion) {
            sb << "\\textcolor{red}{${body()}}"
        } else {
            sb << "<span style=\"color:red\">${body()}</span>"
        }

        out << raw(sb.toString())
    }

    def exercise = {params, body ->
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\section{Exercise - ${g.transl(src: "alisinahisdisgv digvdodi")}}<br/>"
            sb << "Translate to Cherokee syllabary and the phonetic equivalent\\\\<br/>"
            sb << "${params.text}\\\\<br/>"
        } else {
            sb << "<h4>Exercise - ${g.transl(src: "alisinahisdisgv digvdodi")}</h4>"
            sb << "Translate to Cherokee syllabary and the phonetic equivalent<br/>"
            sb << params.text
        }

        out << raw(sb.toString())
    }

    def bookSection = { params, body ->
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\subsection{${params.title} - \\Cherokee ${g.translit(src: params.phonetic)}\\selectfont}<br/>"
            sb << body()
        } else {
            sb << "<h4>${params.title} - ${g.translit(src:params.phonetic)}</h4>"
            sb << body()
        }

        out << raw(sb.toString())
    }

    def transl = {params ->
        def src = params.src
        if (isPrintVersion) {
            out << "\\Cherokee ${SyllabaryUtil.tsalagiToSyllabary(src)} \\selectfont "
        } else {
            if (src) {
                out << SyllabaryUtil.tsalagiToSyllabary(src)
            }
        }
    }

    def whatYouWillLearn = {params, body ->
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\subsection{What You Will Learn}<br/>"
            sb << "In this unit you will learn:<br/>"
            sb << "\\begin{itemize}<br/>"
            def objectives = params.objectives

            objectives.each {
                sb << "\\item ${it}<br/>"
            }

            sb << "\\end{itemize}\\newpage<br/><br/>"
        } else {
            sb << "<b>What You Will Learn In This Chapter</b><br/>"
            sb << "In this unit you will learn:<br/>"
            sb << "<ul>"
            def objectives = params.objectives

            objectives.each {
                sb << raw("<li>${it}</li>")
            }

            sb << raw("</ul>")
        }
        
        out << raw(sb.toString())
    }

    def dialogVocab = {params, body ->
        def sb = new StringBuilder()
        def vocabulary = params.vocab

        if (isPrintVersion) {
            sb << "\\section{Dialog - }<br/>"
            sb << "\\begin{tabular}{p{2cm} p{11cm}}<br/>"
            sb << body()
            sb << "\\end{tabular}\\\\<br/>"
            sb << g.vocabulary(src: vocabulary)
        } else {
            sb << "<div style=\"display:table-row\">"
            sb << "<div style=\"display:table-cell;padding-right:20px\">"
            sb << "<h4>Dialog - </h4>"
            sb << body()
            sb << "</div>"
            sb << "<div style=\"display:table-cell\">"
            sb << g.vocabulary(src: vocabulary)
            sb << "</div>"
            sb << "</div>"
        }

        out << raw(sb.toString())
    }
    final Pattern eTag = Pattern.compile("<e\\b[^>]*>(.*?)</e>")
    def vocabulary = {params ->
        def sb = new StringBuilder()

        if (isPrintVersion) {
            sb << "\\subsection{Vocabulary - \\Cherokee ${SyllabaryUtil.tsalagiToSyllabary("dikaneisdi")} \\selectfont}<br/>"
            sb << "\\begin{tabular}{p{3cm} p{11cm}}<br/>"
            params.src.each { key, value ->
                def translit = value

//                if (translit.contains("<e>")) {
//                    final Matcher m = eTag.matcher(translit)
//
//                    final StringBuffer sb2 = new StringBuffer(translit.length());
//                    while (m.find()) {
//                        final String text = m.group();
//                        def tmpText = text.substring("<e>".length(), text.length() - "</e>".length())
//                        //some way to separate out the syllabary?
//                        m.appendReplacement(sb2, "\\selectfont ${SyllabaryUtil.tsalagiToSyllabary(tmpText))}")
//                    }
//
//                    m.appendTail(sb2);
//                    sb << sb2
////                    translit = translit.replaceAll("<e>", "\\selectfont")
//                }
                sb << "${key} & \\Cherokee ${SyllabaryUtil.mixedTransliteration(translit)} \\selectfont\\newline \\textcolor{red}{${translit}}\\\\<br/>"
//                sb << "<div style=\"display:table-row\"><div style=\"display:table-cell;padding-right:10px\">${key}</div><div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(translit)}"
//                sb << "<br/><span style=\"color:red\">${translit}</span>"
//                sb << "</div></div>"
            }
            sb << "\\end{tabular}<br/><br/>"
        } else {
            sb << "<h4>Vocabulary - ${SyllabaryUtil.tsalagiToSyllabary("dikaneisdi")}</h4>"

            params.src.each { key, value ->
                def translit = value
                sb << "<div style=\"display:table-row\"><div style=\"display:table-cell;padding-right:10px\">${key}</div><div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(translit)}"
                sb << "<br/><span style=\"color:red\">${translit}</span>"
                sb << "</div></div>"
            }
        }

        out << raw(sb.toString())
    }

    def dialogLine = {params ->
        def sb = new StringBuilder()
        def name = params.name
        def dialog = params.dialog
        def showRedHelper = params.phonetic ? true : false

        if (isPrintVersion) {
            sb << "\\Cherokee ${SyllabaryUtil.mixedTransliteration(name)}\\selectfont:"
            sb << " & \\Cherokee ${SyllabaryUtil.mixedTransliteration(dialog)}\\selectfont\\newline \\textcolor{red}{${dialog}}\\\\<br/>"
        } else {
            sb << "<div style=\"display:table-row\">\n" +
                    "    <div style=\"display:table-cell;padding-right:10px\">\n" +
                    "        <div style=\"display:table-row\">\n" +
                    "            <div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(name)}:</div>\n" +
                    "        </div>\n"
            if (showRedHelper) {
                sb << "        <div style=\"display:table-row\">\n" +
                        "            <div style=\"display:table-cell\"><span style=\"color:red\">${name}:</span></div>\n" +
                        "        </div>\n"
            }
            sb << "    </div>\n" +
                    "    <div style=\"display:table-cell\">\n" +
                    "        <div style=\"display:table-row\">\n" +
                    "            <div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(dialog)}</div>\n" +
                    "        </div>\n"
            if (showRedHelper) {
                sb << "        <div style=\"display:table-row\">\n" +
                        "            <div style=\"display:table-cell\"><span style=\"color:red\">${dialog}</span></div>\n" +
                        "        </div>\n"
            }

            sb << "    </div>\n" +
                    "</div>"
        }

        out << raw(sb.toString())
    }


    def dialogItem = {params, body ->
        def key = params.key
        def value = params.value
        out << g.dialogLine(name: key, dialog: value, phonetic: 'true')
    }
}
