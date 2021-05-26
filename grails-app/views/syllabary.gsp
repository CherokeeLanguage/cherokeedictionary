<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 5/24/21
  Time: 4:22 PM
--%>

<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Syllabary</title>
</head>
<body>
<%
def vowels = ['a', 'e', 'i', 'o', 'u', 'v']
def consonants = ['g', 'h', 'l', 'm', 'n', 'qu', 's', 'd', 'tl', 'ts', 'w', 'y']//'t',

//out << raw("<div style=\"display:table-row\">")
//vowels.each {
//    out << raw("<div style=\"display:table-cell\">")
//    out << "$it"
//    out << raw("<br/>")
//    out << net.cherokeedictionary.transliteration.SyllabaryUtil.tsalagiToSyllabary("$it")
//    out << raw("</div>")
//}
//out<< raw("</div>")
//
//consonants.each {cons ->
//    out << raw("<div style=\"display:table-row\">")
//    vowels.each { vowl ->
//        if (cons == "m" && vowl == 'v') {
//        } else {
//            if (cons+vowl == "to" || cons+vowl == "tu" || cons+vowl == "tv") {
//            } else {
//                out << raw("<div style=\"display:table-cell\">")
//
//                out << "$cons$vowl"
//                out << raw("<br/>")
//                out << net.cherokeedictionary.transliteration.SyllabaryUtil.tsalagiToSyllabary("$cons$vowl")
//                  if (cons+vowl == "ga") {
//                out << raw("<br/>ka")
//                    out << raw("<br/>")
//                    out << net.cherokeedictionary.transliteration.SyllabaryUtil.tsalagiToSyllabary("ka")
//                }
//
//                  if (cons+vowl == "na") {
//                out << raw("<br/>hna")
//                    out << raw("<br/>")
//                    out << net.cherokeedictionary.transliteration.SyllabaryUtil.tsalagiToSyllabary("hna")
//                    out << raw("<br/>nah")
//                    out << raw("<br/>")
//                    out << net.cherokeedictionary.transliteration.SyllabaryUtil.tsalagiToSyllabary("nah")
//                }
//
//                  if (cons+vowl == "sa") {
//                out << raw("<br/>s")
//                    out << raw("<br/>")
//                    out << net.cherokeedictionary.transliteration.SyllabaryUtil.tsalagiToSyllabary("s")
//                }
//
//                if (cons+vowl == "tla") {
//                out << raw("<br/>dla")
//                out << raw("<br/>")
//                out << net.cherokeedictionary.transliteration.SyllabaryUtil.tsalagiToSyllabary("dla")
//                }
//            }
//            out << raw("</div>")
//        }
//    }
//    out<< raw("</div>")
//}
out << raw("<div style=\"display:table-row;border: 1px solid black;padding-left:5px;\">")
vowels.each {
    out << raw("<div style=\"display:table-cell;padding-right:10px;border: 1px solid black;padding-left:5px;\">")
    out << SyllabaryUtil.tsalagiToSyllabary("$it")
    out << "  $it"
    out << raw("</div>")
}
out<< raw("</div>")

consonants.each {cons ->
    out << raw("<div style=\"display:table-row;border: 1px solid black;padding-left:5px;\">")
    vowels.each { vowl ->
        if (cons == "m" && vowl == 'v') {
        } else {
            if (cons+vowl == "to" || cons+vowl == "tu" || cons+vowl == "tv") {
            } else {
                out << raw("<div style=\"display:table-cell;padding-right:10px;border: 1px solid black;padding-left:5px;\">")
                out << SyllabaryUtil.tsalagiToSyllabary("$cons$vowl")
                out << "  $cons$vowl  "
//                out << raw("<br/>")

                if (cons+vowl == "da") {
                    out << SyllabaryUtil.tsalagiToSyllabary("ta")
                    out << raw("  ta")
                }

                if (cons+vowl == "de") {
                    out << SyllabaryUtil.tsalagiToSyllabary("te")
                    out << raw("  te")
                }

                if (cons+vowl == "di") {
                    out << SyllabaryUtil.tsalagiToSyllabary("ti")
                    out << raw("  ti")
                }

                if (cons+vowl == "ga") {
                    out << SyllabaryUtil.tsalagiToSyllabary("ka")
                    out << raw("  ka")
                }

                if (cons+vowl == "na") {
                    out << SyllabaryUtil.tsalagiToSyllabary("hna")
                    out << raw("  hna  ")
                    out << SyllabaryUtil.tsalagiToSyllabary("nah")
                    out << raw("  nah  ")
                }

                if (cons+vowl == "sa") {
                    out << SyllabaryUtil.tsalagiToSyllabary("s")
                    out << raw("  s  ")
                }

                if (cons+vowl == "tla") {
                    out << SyllabaryUtil.tsalagiToSyllabary("dla")
                    out << raw("  dla  ")
                }
            }
            out << raw("</div>")
        }
    }
    out<< raw("</div>")
}
%>
</body>
</html>