
<%--
/**
 *
 * @author torr
 * @since 7/11/13 2:16 PM
 */
--%>

<%@ page import="net.cherokeedictionary.admin.SourceManagement; net.cherokeedictionary.admin.SourceManagement;  net.cherokeedictionary.admin.SourceManagement; java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <script src="https://code.jquery.com/jquery-latest.min.js"></script>
    <script src="https://code.jquery.com/ui/1.11.2/jquery-ui.min.js"></script>
%{--<g:set var="showLinks" value="false" scope="request"/>--}%
<meta name="layout" content="manager"/>
<title>Dictionary</title>
<style>
/*sup { vertical-align: top; position: relative; top: -0.2em; }*/
.odd {
    background: #f7f7f7;
}

.even {
    background: #ffffff;
}
td, th {
    line-height: 1.5em;
    padding: 0.5em 0.6em;
    text-align: left;
    vertical-align: top;
}
table {
    border-top: 1px solid #DFDFDF;
    border-collapse: collapse;
    width: 100%;
    margin-bottom: 1em;
}

tr {
    border: 0;
}

tr>td:first-child, tr>th:first-child {
    padding-left: 1.25em;
}

tr>td:last-child, tr>th:last-child {
    padding-right: 1.25em;
}

th:hover, tr:hover {
    background: #E1F2B6;
}
</style>

</head>
<body>
<%
    if (session.getAttribute("loggedin")) {
        out << raw("<div style=\"color : red\">you are logged in</div>")
    }



    //multiple entries
    xref.add(current)
    def entries = xref//[current, xref];
%>

<div id="message"></div>
<table>
<tr>
    <th style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0"></th>
    <th>Entry</th>
    <th>Part of<br/>speech</th>
    <th>Definition</th>
    <th>Plural</th>
    <th>Verb<br/>1st present</th>
    <th>Verb<br/>3rd past</th>
    <th>Verb<br/>3rd present habitual</th>
    <th>Verb<br/>2nd imperative</th>
    <th>Verb<br/>3rd infinitive</th>
    <th>Sentence</th>
</tr>

<g:each in="${entries}" var="entry" status="i">
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
<tr class="${(i % 2) == 0 ? 'even' : 'odd'}" id="edit${i}">
    <td style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;${net.cherokeedictionary.admin.SourceManagement.findByCode(entry.source).color};text-color${net.cherokeedictionary.admin.SourceManagement.findByCode(entry.source).textColor}">
        <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; text-transform:lowercase;width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
            <a href="#" class="popper" data-popbox="tsalagidigoweli2" style="<%if (entry.source != 'ced') { println "color:white;"}%>">
                ${entry.source}
            </a>
        </div>
        <div id="tsalagidigoweli2" class="popbox">
            CED = Cherokee English Dictionary<br/>
            CWL = Consortium Word List<br/>
            MSCT = Microsoft Computer Terminology<br/>
            CNOMED = Cherokee Nation Online Medical Terminology<br/>
            NCMED = Cherokee North Carolina Medical Terminology<br/>
        </div>
    </td>
    <td><span id="syllabarybmain${i}">${entry.syllabaryb}</span><br/>
        <span id="entryamain${i}"><g:pronunciation value="${entry.entrya}"/></span>
        <span id="entryatonemain${i}"><g:intonation tone="${entry.entrytone}"/></span>
    </td>
    <td>${entry.partofspeechc}</td>
    <% if (first.trim() != "") { %>
    <td>${first}<br/>
        ${second}
        <% if (third) { %>
        <br/>${third}
    <% } %>
    </td>
    <% } else { %>
    <td><span id="definitiondmain${i}">${definition}</span></td>
    <% } %>

    <td><span id="nounadjpluralsyllfmain${i}">${entry.nounadjpluralsyllf}</span><br/>
        <span id="nounadjpluralemain${i}"><g:pronunciation value="${entry.nounadjplurale}"/></span>
        <span id="nounadjpluraltonemain${i}"><g:intonation tone="${entry.nounadjpluraltone}"/></span>
    </td>
    <td><span id="vfirstpreshmain${i}">${entry.vfirstpresh}</span><br/>
        <span id="vfirstpresgmain${i}"><g:pronunciation value="${entry.vfirstpresg}"/></span>
        <span id="vfirstprestonemain${i}"><g:intonation tone="${entry.vfirstprestone}"/></span>
        %{--${entry.vfirstpresh}<br/>--}%
        %{--<g:readingDisplay value="${entry.vfirstpresg}" tone="${entry.vfirstprestone}"/>--}%
    </td>
    <td><span id="vthirdpastsylljmain${i}">${entry.vthirdpastsyllj}</span><br/>
        <span id="vthirdpastimain${i}"><g:pronunciation value="${entry.vthirdpasti}"/></span>
        <span id="vthirdpasttonemain${i}"><g:intonation tone="${entry.vthirdpasttone}"/></span>
    </td>
    <td><span id="vthirdpressylllmain${i}">${entry.vthirdpressylll}</span><br/>
        <span id="vthirdpreskmain${i}"><g:pronunciation value="${entry.vthirdpresk}"/></span>
        <span id="vthirdprestonemain${i}"><g:intonation tone="${entry.vthirdprestone}"/></span>
    </td>
    <td><span id="vsecondimpersyllnmain${i}">${entry.vsecondimpersylln}</span><br/>
        <span id="vsecondimpermmain${i}"><g:pronunciation value="${entry.vsecondimperm}"/></span>
        <span id="vsecondimpertonemain${i}"><g:intonation tone="${entry.vsecondimpertone}"/></span>
    </td>
    <td><span id="vthirdinfsyllpmain${i}">${entry.vthirdinfsyllp}</span><br/>
        <span id="vthirdinfomain${i}"><g:pronunciation value="${entry.vthirdinfo}"/></span>
        <span id="vthirdinftonemain${i}"><g:intonation tone="${entry.vthirdinftone}"/></span>
    </td>
    <td><span id="sentencesyllrmain${i}">${raw(entry.sentencesyllr)}</span><br/>
        <span id="sentenceqmain${i}">${raw(entry.sentenceq)}</span><br/>
        <i><span id="sentenceenglishsmain${i}">${raw(entry.sentenceenglishs)}</span></i>
    </td>
</tr>
</g:each>
</table>

    <%
        def old = request.getParameter("old").split(",")
    %>
    <g:each in="${old}" var="cf">
        <%
            String cleanedValue = cf.trim();
            if (cleanedValue.contains("(")) {
                cleanedValue = cleanedValue.substring(0, cleanedValue.indexOf("(")).trim();
            }
        %>
        <script>
            $(function() {
                $('${cf}Button').click(function() {
                    $('${cleanedValue}').submit();
                });
            });
        </script>

        <form action="dictionary" method="POST" id="${cleanedValue}" target="_parent">
            <g:hiddenField id="tsalagiSearch" name="tsalagiSearch" value="${cleanedValue}"/>
            Search whole dictionary for: <font color="blue">${cleanedValue}</font>
            <g:submitButton name="submit"/>
            %{--<a href="#" onclick="document.getElementById('${cleanedValue}').submit();">${cf.trim()}</a><br/>--}%
            %{--<a href="#" id="${cf}Button">${cf.trim()}</a><br/>--}%
            %{--<a class='iframe' href="xrefdisplay.gsp?old=${cleanedValue}&current=${entry.id}">${cf.trim()}</a><br/>--}%
        </form>
    </g:each>
    </body>
</html>