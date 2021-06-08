package newsite

import net.cherokeedictionary.transliteration.SyllabaryUtil

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
            sb << "<a name=\"#cite${params.title}\"></a><sup><a id=\"#cite${params.title}\" href=\"#ref${params.title}\">${citationIndex}</a></sup>"
        }

        citationMap[params.title] = params.src

        out << raw(sb.toString())
    }

    def printCitations = {
        def sb = new StringBuilder()

        if (isPrintVersion) {
            sb << "%+Bibliography<br/>\\begin{thebibliography}{99}<br/>"

            citationMap.eachWithIndex { item, idx ->
                def value = item.value.replaceAll("&", "\\\\&")
                sb << "\\bibitem{${item.key}} ${value}<br/>"
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
            sb << "\\chapter{${params.title} - ${SyllabaryUtil.tsalagiToSyllabary(params.titletranslit)} }<br/>"
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
            sb << "<br/>\\section{Exercise - ${g.transl(src: "alisinahisdisgv digvdodi")}}<br/>"
            sb << "Translate to Cherokee syllabary and the phonetic equivalent\\\\<br/>"
            sb << "${params.text}<br/>"
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
            sb << "\\subsection{${params.title} - ${g.translit(src: params.phonetic)}}<br/>"
            sb << body()
            sb << "<br/>"
        } else {
            sb << "<h4>${params.title} - ${g.translit(src:params.phonetic)}</h4>"
            sb << body()
        }

        out << raw(sb.toString())
    }

    def transl = {params ->
        def src = params.src
        if (isPrintVersion) {
            out << "${SyllabaryUtil.tsalagiToSyllabary(src)}  "
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
            sb << "\\subsection{Dialog - }<br/>"
            sb << "\\begin{tabular}{p{2cm} p{11cm}}<br/>"
            sb << body()
            sb << "\\end{tabular}<br/><br/>"
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

    def vocabulary = {params ->
        def sb = new StringBuilder()

        if (isPrintVersion) {
            sb << "\\subsection{Vocabulary - ${SyllabaryUtil.mixedTransliteration("dikaneisdi")} }<br/>"
            sb << "\\begin{tabular}{p{3cm} p{11cm}}<br/>"
            params.src.each { key, value ->
                def sb2 = new StringBuilder()
                def translit = ""
                if (value instanceof List) {
                    translit = value.join(" ")
                    value.each {val ->
                        if (val.contains("<e>")) {
                            sb2 << " ${val.substring("<e>".size())} "
                        } else {
                            sb2 << "${SyllabaryUtil.mixedTransliteration(val)} "
                        }
                    }
                } else {
                    translit = value
                    sb2 << "${SyllabaryUtil.mixedTransliteration(value)} "
                }

                sb << "${key} & ${sb2}\\newline \\textcolor{red}{${translit}}\\\\<br/>"
//                sb << "${key} & ${SyllabaryUtil.mixedTransliteration(translit)} \\newline \\textcolor{red}{${translit}}\\\\<br/>"
//                sb << "<div style=\"display:table-row\"><div style=\"display:table-cell;padding-right:10px\">${key}</div><div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(translit)}"
//                sb << "<br/><span style=\"color:red\">${translit}</span>"
//                sb << "</div></div>"
            }
            sb << "\\end{tabular}<br/><br/>"
        } else {
            sb << "<h4>Vocabulary - ${SyllabaryUtil.tsalagiToSyllabary("dikaneisdi")}</h4>"

            params.src.each { key, value ->
                def sb2 = new StringBuilder()
                def translit = ""
                if (value instanceof List) {
                    translit = value.join(" ")
                    value.each {val ->
                        if (val.contains("<e>")) {
                            sb2 << "${val.substring("<e>".size())} "
                        } else {
                            sb2 << "${SyllabaryUtil.mixedTransliteration(val)} "
                        }
                    }
                } else {
                    translit = value
                    sb2 << "${SyllabaryUtil.mixedTransliteration(value)}"
                }

//                sb << "${key} & ${sb2}\\newline \\textcolor{red}{${translit}}\\\\<br/>"

                sb << "<div style=\"display:table-row\">"
                sb << "<div style=\"display:table-cell;padding-right:10px\">${key}</div>"
                sb << "<div style=\"display:table-cell\">${sb2}"
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
            sb << "${SyllabaryUtil.mixedTransliteration(name)}:"
            sb << " & ${SyllabaryUtil.mixedTransliteration(dialog)}\\newline \\textcolor{red}{${dialog}}\\\\<br/>"
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
