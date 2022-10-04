<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 8/10/2022
  Time: 11:03 AM
--%>

<%@ page import="net.cherokeedictionary.dictionary.Likespreadsheets" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>
<%--
    def lst = []
    lst << "entrya"
    lst << "nounadjplurale"
    lst << "vfirstpresg"
    lst << "vthirdinfo"
    lst << "vsecondimperm"
    lst << "vthirdpasti"
    lst << "vthirdpresk"
    def count = 0

    lst.each {
        count += Likespreadsheets.findAll("from Likespreadsheets where ${it} != '' and ${it} not like '%---%'" as String).size()
    }

    count += Likespreadsheets.findAll("from Likespreadsheets where entrya like '%(e)%'").size()
//    select count(entrya) from likespreadsheets where entrya != '' and entrya not like '%---%';
//    select count(nounadjplurale) from likespreadsheets where nounadjplurale != '' and nounadjplurale not like '%---%';
//    select vfirstpresg from likespreadsheets where vfirstpresg != '' and vfirstpresg not like '%---%';
//    select count(vthirdinfo) from likespreadsheets where vthirdinfo != '' and vthirdinfo not like '%---%';
//    select count(vsecondimperm) from likespreadsheets where vsecondimperm != '' and vsecondimperm not like '%---%';
//    select count(vthirdpasti) from likespreadsheets where vthirdpasti != '' and vthirdpasti not like '%---%';
//    select count(vthirdpresk) from likespreadsheets where vthirdpresk != '' and vthirdpresk not like '%---%';
--%>
%{--${count}--}%
An attempt to combine multiple records into one single record
<%
    def entries = new HashSet()
    def partofspeech = new HashSet()
    def plurals = new HashSet()

    def sentences = []

    Likespreadsheets.findAll("from Likespreadsheets where (entrya = 'achuja' or entrya = 'achutsa' or entrya = 'atsutsa') or sentencetranslit like '% atsutsa %'").each {

        if (it.entrya == 'achuja' || it.entrya == 'atsutsa' || it.entrya == 'achutsa') {
            entries << "${it.entrya}<sup>${it.source}</sup>"

            if (it.partofspeechc) {
                partofspeech << "${it.partofspeechc}<sup>${it.source}</sup>"
            }
            if (it.nounadjplurale) {
                plurals << "${it.nounadjplurale}<sup>${it.source}</sup>"
            }
        }
        if (it.sentenceenglishs) {
            sentences << "${it.sentencesyllr}<sup>${it.source}</sup><br/>${it.sentenceq}<br/>${it.sentenceenglishs}<br/>"
        }

//        out << it
//        out << raw("</br>")
    }
%>

<div style="display:table-row">
    <div style="display:table-cell">${raw(entries.join(", "))}</div>
    <div style="display:table-cell">${raw(partofspeech.join(", "))}</div>
    <div style="display:table-cell">${raw(plurals.join(", "))}</div>
    <div style="display:table-cell">${raw(sentences.join("<br/>"))}</div>
</div>

</body>
</html>