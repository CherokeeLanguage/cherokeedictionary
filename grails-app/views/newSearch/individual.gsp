<%@ page import="cherokee.Settings; cherokee.audio.AudioFile; cherokee.audio.AudioLink; java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
%{--<html>--}%
%{--<head>--}%
    %{--<meta name="layout" content="manager"/>--}%
    %{--<title>Individual Page</title>--}%

%{--</head>--}%
%{--<body>--}%
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script>
    $(function() {
        $('#close').click(function() {
            window.top.document.getElementById('cboxOverlay').click();
        });
    });
</script>
<%
    Pattern p = Pattern.compile("([0-9]?.?\\s?[a-zA-Z'(),\\s.]*)?([0-9].\\s?[a-zA-Z'(),\\s.]*)?([0-9].\\s?[a-zA-Z'(),\\s.]*)?");

    String definition = entry.definitiond;
    Matcher m = p.matcher(definition)
    String first = "";
    String second = "";
    String third = "";

    if (m.matches()) {
        switch (m.groupCount()) {
            case 3:
                third = m.group(3);
            case 2:
                second = m.group(2);
            case 1:
                first = m.group(1);
        }
    }

    def tdstyle = "";
    def divstyle = "";
    def anchorstyle = "";
%>


    <table border="1" width="100%">

        <tr>
            <td <%if (!entry.partofspeechc) {%> colspan="2"<%}%>>
                <b>Source:</b><br/>
                <%switch (entry.source) {
                    case "ced":
                        out << "Cherokee English Dictionary"
                        break;
                    case "cwl":
                        out << "Consortium Word List"
                        break;
                    case "cnomed":
                        out << "Cherokee Nation Online Medical Terminology"
                        break;
                    case "ncmed":
                        out << "Cherokee North Carolina Medical Terminology"
                        break;
                    case "noq":
                        out << "Noquisis Word List"
                        break;
                    case "rrd":
                        out << "Raven Rock Dictionary"
                        break;
                    case "msct":
                        out << "Microsoft Computer Terminology"
                        break;
                } %>

                <br/>
            </td>

        <%if (entry.partofspeechc) {%>

            <td><b>Part of Speech</b><br/>
                ${entry.partofspeechc}</td>

        <%}%>
        </tr>
        <% if (first.trim() != "") { %>
        <tr>
            <td>
                <b>Definitions:</b><br/>
                ${first}<br/>
                ${second}
                <% if (third) { %>
                <br/>${third}
            <% } %>
            </td>
        </tr><tr>
        <% } else { %>
        <td><b>Definition:</b><br/><span id="definitiondmain${i}">${raw(definition)}</span></td>
        <% } %>
    </tr>
    <tr>
        <td colspan="2">
            <b>Entry:</b><br/>
            <span id="syllabarybmain${i}">${entry.syllabaryb}</span><br/>
            <span id="entryamain${i}">
                <% if (entry.entrya.endsWith("?")) { %>
                ${entry.entrya}
                <% } else { %>
                <g:pronunciation value="${entry.entrya}"/>
                <% } %>
            </span>
            <span id="entryatonemain${i}"><g:intonation tone="${entry.entrytone}"/></span>
            <%
                def isVerb = entry.partofspeechc == 'vt' || entry.partofspeechc == 'vi';
                if (isVerb) {// && session.getAttribute("loggedin")
            %>

            <span><br/><a href="/newSearch/conjdisplay?set=${entry.partofspeechc}&id=${entry.id}">conjugate</a></span>
            <span><br/><a href="/newSearch/conjtabledisplay?set=${entry.partofspeechc}&id=${entry.id}">verb table</a><br/></span>
            <% } %>
        </td>
    </tr>
<%
    def settings = cherokee.Settings.findAll("from Settings where setting_name=?0", ['showAudio']);
    if (settings) {
        if (settings[0].value == 'true') {

        def audioLink = cherokee.audio.AudioLink.findByLikespreadsheets(entry)
        if (audioLink) {
            def audioFile = cherokee.audio.AudioFile.findAllById(audioLink.audioFileId)
            if (audioFile) {
%>
        <tr>
            <td colspan="2">
                <b>Pronunciation:</b><br/>
                <audio controls style="width: 100px;height:20px">
                    <source src="${audioFile.audioFilePath[0]}" type="audio/mpeg">
                </audio>
            </td>
        </tr>
<%
                }
            }
        }
    }
%>
    <%if (entry.nounadjpluralsyllf) {%>
    <tr>
        <td colspan="2"><span id="nounadjpluralsyllfmain${i}">${entry.nounadjpluralsyllf}</span><br/>
            <span id="nounadjpluralemain${i}"><g:pronunciation value="${entry.nounadjplurale}"/></span>
            <span id="nounadjpluraltonemain${i}"><g:intonation tone="${entry.nounadjpluraltone}"/></span>
        </td>
    </tr>
    <%}%>
    <%if (entry.partofspeechc == 'vi' || entry.partofspeechc == 'vt' || entry.partofspeechc == 'vi' || entry.partofspeechc == 'vt') {%>
    <tr>
        <td>
            <b>Verb First Present</b><br/>
            <span id="vfirstpreshmain${i}">${entry.vfirstpresh}</span><br/>
            <span id="vfirstpresgmain${i}"><g:pronunciation value="${entry.vfirstpresg}"/></span>
            <span id="vfirstprestonemain${i}"><g:intonation tone="${entry.vfirstprestone}"/></span><br/>
        </td>
        <td>
            <b>Verb Second Imperative</b><br/>
            <span id="vsecondimpersyllnmain${i}">${entry.vsecondimpersylln}</span><br/>
            <span id="vsecondimpermmain${i}"><g:pronunciation value="${entry.vsecondimperm}"/></span>
            <span id="vsecondimpertonemain${i}"><g:intonation tone="${entry.vsecondimpertone}"/></span>
        </td>
    </tr>
    <tr>
        <td>
            <b>Verb Third Past</b><br/>
            <span id="vthirdpastsylljmain${i}">${entry.vthirdpastsyllj}</span><br/>
            <span id="vthirdpastimain${i}"><g:pronunciation value="${entry.vthirdpasti}"/></span>
            <span id="vthirdpasttonemain${i}"><g:intonation tone="${entry.vthirdpasttone}"/></span>
        </td>

        <td><b>Verb Third Present Habitual</b><br/>
            <span id="vthirdpressylllmain${i}">${entry.vthirdpressylll}</span><br/>
            <span id="vthirdpreskmain${i}"><g:pronunciation value="${entry.vthirdpresk}"/></span>
            <span id="vthirdprestonemain${i}"><g:intonation tone="${entry.vthirdprestone}"/></span>
        </td>
    </tr>
    </tr><tr>
        <td colspan="2"><b>Verb Third Infinitive</b><br/>
            <span id="vthirdinfsyllpmain${i}">${entry.vthirdinfsyllp}</span><br/>
            <span id="vthirdinfomain${i}"><g:pronunciation value="${entry.vthirdinfo}"/></span>
            <span id="vthirdinftonemain${i}"><g:intonation tone="${entry.vthirdinftone}"/></span>
        </td>
    </tr>
    <%}%>
    <%if (entry.sentencesyllr) {%>
    <tr>
        <td colspan="2">
            <b>Sentences:</b><br/>
            <span id="sentencesyllrmain${i}">${raw(entry.sentencesyllr)}</span><br/>
            <span id="sentenceqmain${i}">${raw(entry.sentenceq)}</span><br/>
            <i><span id="sentenceenglishsmain${i}">${raw(entry.sentenceenglishs)}</span></i>
        </td>
    </tr>
    <%}%>
    <%if (entry.definitionlarge) {%>
    <tr>
        <td>
            <b>Large Definition:</b><br/>
            ${raw(entry.definitionlarge)}<br/>
        </td>
    </tr>
    <%}%>
    <%if (entry.category) {%>
    <tr>
        <td>
            <b>Category:</b><br/>
            ${raw(entry.category)}<br/>
        </td>
    </tr>
    <%}%>
    <%if (entry.notes) {%>
    <tr>
        <td>
            <b>Notes:</b><br/>
            ${raw(entry.notes)}<br/>
        </td>
    </tr>
    <%}%>
    <%if (entry.etymology) {%>
    <tr>
        <td>
            <b>Etymology:</b><br/>
            ${raw(entry.etymology)}<br/>
        </td>
    </tr>
    <%}%>

    <%if (entry.crossreferencet) {%>
    <tr>

        <%
                String crossReference = entry.crossreferencet;
        %>
        <td colspan="2">
            <b>See Also:</b><br/>
            <% if (entry.crossreferencet) {
                String[] splits = crossReference.split(", ");
            %>
            <g:each var="cf" in="${splits}">
                <%
                        String cleanedValue = cf.trim();
                        if (cleanedValue.contains("(")) {
                            cleanedValue = cleanedValue.substring(0, cleanedValue.indexOf("(")).trim();
                        }
                %>

                <a href="xrefdisplay?old=${cleanedValue}&current=${entry.id}">${cf.trim()}</a><br/>
            </g:each><br/>
            <% } %>
        </td>
    </tr>
    <%}%>
</table>

