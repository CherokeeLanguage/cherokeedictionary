<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 8/18/2021
  Time: 6:58 PM
--%>

%{--<%@ page import="net.cherokeedictionary.views.SyllabarySorted; net.cherokeedictionary.views.DefinitionSorted" contentType="text/html;charset=UTF-8" %>--}%
<html>
<head>
    <title></title>
</head>
<%
def cedEntry = Likespreadsheets.findById()

%>
<body>
<table>
    <tr></tr>
</table>


%{--<%--}%
%{--    def ds = SyllabarySorted.list()--}%
%{--    %>--}%
%{--<g:each in="${ds}" var="definition">--}%
%{--    ${definition}--}%
%{--</g:each>--}%

%{--<%
    def headings = ["entrya",
                    "syllabaryb",
                    "partofspeechc",
                    "definitiond",
                    "nounadjplurale",
                    "nounadjpluralsyllf",
                    "vfirstpresg",
                    "vfirstpresh",
                    "vthirdpasti",
                    "vthirdpastsyllj",
                    "vthirdpresk",
                    "vthirdpressylll",
                    "vsecondimperm",
                    "vsecondimpersylln",
                    "vthirdinfo",
                    "vthirdinfsyllp",
                    "sentenceq",
                    "sentencesyllr",
                    "sentenceenglishs",
                    "crossreferencet",
                    /*"nounadjpluraltranslit",
                    "entrytranslit",
                    "vfirstprestranslit",
                    "vthirdpasttranslit",
                    "vthirdprestranslit",
                    "vsecondimpertranslit",
                    "vthirdinftranslit",
                    "sentencetranslit",*/
                    "entrytone",
                    "nounadjpluraltone",
                    "vfirstprestone",
                    "vthirdpasttone",
                    "vthirdprestone",
                    "vsecondimpertone",
                    "vthirdinftone",
                    "source",
                    "definitionlarge",
                    "etymology",
                    "notes",
                    "category"]

    headings.each {
        out << "group_concat(distinct ${it} separator '; ') as ${it}, "
    }
%>--}%
</body>
</html>