package cherokee.dictionary.taglibUtil

import net.cherokeedictionary.transliteration.SyllabaryUtil

//https://www.mscharhag.com/grails/rendering-views-using-templates-instead-of-taglibs
//https://docs.groovy-lang.org/latest/html/gapi/groovy/text/markup/TagLibAdapter.html
class CitationUtil {
    def citationIndex = 0
    def citationMap = [:]
    def isPrintVersion = false

    def answerKey = [:]

    def printVersion = {
        isPrintVersion = it.trueFalse && it.trueFalse=="true"
    }

    def clearCitations = {
        citationIndex = 0
        citationMap = [:]
    }

    def footnote = {Map it ->
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

        return sb.toString()
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

        return sb.toString()
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

        return sb.toString()
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

        return sb.toString()
    }

    def bookChapter = { params, body ->
        def sb = new StringBuilder()
        def titleName = params.title
        def titleTranslit = SyllabaryUtil.tsalagiToSyllabary(params.titletranslit)
        def title = "${titleName} - ${titleTranslit}"
        if (isPrintVersion) {
            sb << "\\index{$titleName}"
            sb << "\\index{$titleTranslit}"
            sb << "\\chapter{${title}}<br/>"
            sb << body()
        } else {
            sb << "<a name=\"${params.anchor.replaceAll(" ", "")}\"></a>"
            sb << "<h2>${title}</h2>"
            sb << body()
        }

        return sb.toString()
    }

    def redSpan = {params, body ->
        def sb = new StringBuilder()

        if (isPrintVersion) {
            sb << "\\textcolor{red}{${body()}}"
        } else {
            sb << "<span style=\"color:red\">${body()}</span>"
        }

        return sb.toString()
    }

    def exercise = {params, body ->
        def sb = new StringBuilder()
        def exerciseTitle = "Exercise - ${transl(src: "alisinahisdisgv digvdodi")}}"
        if (isPrintVersion) {
            sb << "<br/>\\section{${exerciseTitle}<br/>"
            sb << "Translate to Cherokee syllabary and the phonetic equivalent\\\\<br/>"
            sb << "${params.text}\\\\"
            if (params.answers) {
                sb << "<br/>${params.answers}\\\\"
                sb << "<br/>${SyllabaryUtil.mixedTransliteration(params.answers)}"
            }
        } else {
            sb << "<h4>${exerciseTitle}</h4>"
            sb << "Translate to Cherokee syllabary and the phonetic equivalent<br/>"
            sb << params.text
            if (params.answers) {
                sb << "<br/>${params.answers}"
                sb << "<br/>${SyllabaryUtil.mixedTransliteration(params.answers)}"
            }
        }

        if (params.answers) {
            answerKey."${params.text}" = params.answers
        }

        return sb.toString()
    }

    def bookSection = { params, body ->
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\index{${params.title}}\\subsection{${params.title}"
            if (params.phonetic) {
                sb << " - ${transl(src: params.phonetic)}"
            }
            sb << "}<br/>"
            sb << body()
            sb << "<br/>"
        } else {
            sb << "<h4>${params.title}"
            if (params.phonetic) {
                sb << " - ${transl(src: params.phonetic)}"
            }
            sb << "</h4>"
            sb << body()
        }

        return sb.toString()
    }

    def transl = {params ->
        def sb = new StringBuilder()
        def src = params.src
        if (isPrintVersion) {
            sb << "${SyllabaryUtil.tsalagiToSyllabary(src)}  "
        } else {
            if (src) {
                sb << SyllabaryUtil.tsalagiToSyllabary(src)
            }
        }

        return sb.toString()
    }

    def whatYouWillLearn = {params, body ->
        def sb = new StringBuilder()
        if (isPrintVersion) {
            def whatYouWillLearn = "What You Will Learn"
            sb << "\\index{${whatYouWillLearn}}\\subsection{${whatYouWillLearn}}<br/>"
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
                sb << "<li>${it}</li>"
            }

            sb << "</ul>"
        }

        return sb.toString()
    }

    def dialogVocab = {params, body ->
        def sb = new StringBuilder()
        def vocab = params.vocab

        if (isPrintVersion) {
            sb << "\\subsection{Dialog - }<br/>"
            sb << "\\begin{tabular}{p{2cm} p{11cm}}<br/>"
            sb << body()
            sb << "\\end{tabular}<br/><br/>"
            sb << vocabulary(src: vocab)
        } else {
            sb << "<div style=\"display:table-row\">"
//            sb << "<div style=\"display:table-cell;padding-right:20px\">"
            sb << "<h4>Dialog - </h4>"
            sb << body()
//            sb << "</div>"
//            sb << "<div style=\"display:table-cell\">"

//            sb << "</div>"
            sb << "</div>"
            sb << vocabulary(src: vocab)
        }

        return sb.toString()
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

                sb << "${key.replaceAll("_", " ")} & ${sb2}\\newline \\textcolor{red}{${translit}}\\\\<br/>"
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
                sb << "<div style=\"display:table-cell;padding-right:10px\">${key.replaceAll("_", " ")}</div>"
                sb << "<div style=\"display:table-cell\">${sb2}"
                sb << "<br/><span style=\"color:red\">${translit}</span>"
                sb << "</div></div>"
            }
        }

        return sb.toString()
    }

    def dialogLine = {params ->
        def sb = new StringBuilder()
        def name = params.name.trim()
        def engName = params?.engName?.trim() ?: ""
        def engDialog = params?.engDialog ?: ""
        def dialog = params.dialog
        def showRedHelper = params.phonetic ? true : false

        if (isPrintVersion) {
            sb << "${SyllabaryUtil.mixedTransliteration(name).trim()}:"
            sb << " & ${SyllabaryUtil.mixedTransliteration(dialog)}\\newline \\textcolor{red}{${dialog}}"
            sb << " & ${engName}: & ${engDialog}\\\\<br/>"
        } else {
            sb << "<div style=\"display:table-row;padding-right:20px\">"
            sb << "<div style=\"display:table-cell\">"
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

                sb << "</div></div>"
            sb << "</div>"
            sb << "<div style=\"display:table-cell\">"
                sb << "<div style=\"display:table-row\">\n" +
                        "    <div style=\"display:table-cell;padding-left:20px;padding-right:20px\">\n" +
                        "        <div style=\"display:table-row\">\n" +
                        "            <div style=\"display:table-cell\">${engName}:</div>\n" +
                        "        </div>\n"
                sb << "    </div>\n" +
                        "    <div style=\"display:table-cell\">\n" +
                        "        <div style=\"display:table-row\">\n" +
                        "            <div style=\"display:table-cell\">${engDialog}</div>\n" +
                        "        </div>\n"

                sb << "</div></div>"
            sb << "</div></div>"
        }

        return sb.toString()
    }

    def dialogItem = {params, body ->
        def key = params.key
        def value = params.value
        return dialogLine(name: key, dialog: value, phonetic: 'true')
    }

    def answerKeyPrint = {
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\section{Answer Key -}<br/>"
            answerKey.each { key, value ->
                sb << "\\noindent${key}"
                sb << "\\\\<br/>"
                sb << value
                sb << "\\\\<br/>"
                sb << SyllabaryUtil.mixedTransliteration(value)
                sb << "<br/><br/>"
            }
        } else {
            sb << "<h3>Answer Key - </h3>"
            answerKey.each { key, value ->
                sb << key
                sb << "<br/>"
                sb << value
                sb << "<br/>"
                sb << SyllabaryUtil.mixedTransliteration(value)
                sb << "<br/><br/>"
            }
        }

        return sb.toString()
    }

    def reader = {
        def sb = new StringBuilder()
        if (isPrintVersion) {
            sb << "\\section{Reader -}<br/>"
            answerKey.each { key, value ->
                sb << SyllabaryUtil.mixedTransliteration(value)
                sb << "\\\\<br/>"
            }
        } else {
            sb << "<h3>Reader - </h3>"
            answerKey.each { key, value ->
                sb << SyllabaryUtil.mixedTransliteration(value)
                sb << "<br/>"
            }
        }

        return sb.toString()
    }
}
