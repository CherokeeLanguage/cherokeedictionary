<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 8/23/21
  Time: 1:31 PM
--%>

<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil; net.cherokeedictionary.verb.conjugation.Conjugate;net.cherokeedictionary.factory.VerbFactory; net.cherokeedictionary.util.PrefixTableObject; net.cherokeedictionary.util.PrefixTableSubject;net.cherokeedictionary.dictionary.Likespreadsheets; net.cherokeedictionary.util.Tense; net.cherokeedictionary.ConjugatedVerb; net.cherokeedictionary.core.Verb" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>
<%
    def lst = ConjugatedVerb.findAll("from ConjugatedVerb where conjugation=?0", [request.getParameter("vrb")])
%>
<g:each in="${lst}" var="item">
    ${item.likespreadsheets.entrya} - ${item.likespreadsheets.definitiond} -- ${item.compoundPrefix}<br/>
%{--    ${item.likespreadsheets.id} - ${item.likespreadsheets.syllabaryb} - ${item.conjugation}<br/>--}%
</g:each>
<%--

    def tenseList = [Tense.PRESENT, Tense.REMOTE_PAST, Tense.HABITUAL, Tense.FUTURE_COMMAND, Tense.INFINITIVE]//[Tense.HABITUAL]//
// and definitiond = 'He is bleeding'
def lst2 = Likespreadsheets.findAll("from Likespreadsheets where (partofspeechc like 'v.i%' or partofspeechc like 'v.t%' or partofspeechc = 'vi' or partofspeechc = 'vt') and id=72329")
lst2.each {
    def paramMap = [:]
    paramMap.habitual = it.vthirdpresk
    paramMap.imperative = it.vsecondimperm
    paramMap.infinitive = it.vthirdinfo
    paramMap.present1st = it.vfirstpresh
    paramMap.present3rd = it.syllabaryb
    paramMap.remotepast = it.vthirdpastsyllj
    paramMap.partofspeechc = it.partofspeechc

    tenseList.each {tense ->
        PrefixTableSubject.values().each { pts ->
            PrefixTableObject.values().each { pto ->
                paramMap.verbTense = tense.toString()
                paramMap.subject = pts.toString()
                if (it.partofspeechc != 'vi' && it.partofspeechc != 'v.i.') {
                    paramMap.object = pto.toString()
                }
                Verb display = VerbFactory.createVerbFromParameters(paramMap)
                display = Conjugate.conjugate(display)
                if (display.wholeWord.trim() != "") {
                    out << "${tense.toString()} - ${pts.toString()}:${pto.toString()} {${SyllabaryUtil.newTsalagiToSyllabary(display.wholeWord)}}"
                    /*def conjugatedV = new ConjugatedVerb(likespreadsheets: it, conjugation: SyllabaryUtil.newTsalagiToSyllabary(display.wholeWord), compoundPrefix: "${pts.toString()}:${pto.toString()}")
                    if (!conjugatedV.save()) {
                        conjugatedV.errors.each {error ->
                            println error
                        }
                    }*/
                }
            }
        }
    }
}

--%>
</body>
</html>