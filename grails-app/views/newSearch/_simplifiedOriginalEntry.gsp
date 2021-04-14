<%@ page import="java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
<%
    //def lks = entry
//    def entry = params.entry;
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
<table>
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
    <tr>
<% if (session.getAttribute("loggedin")) { %><td><a href="/likespreadsheets/show/${entry.id}" target="_blank">edit</a></td><%}%>
<td style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;<%if (entry.source == 'cwl') { print "background-color:grey;color:white;"} else if (entry.source == 'msct') { print "background-color:navy;color:white;"}  else if (entry.source == 'ncmed') { print "background-color:#8B0000;color:white;"} else if (entry.source == 'cnomed') { print "background-color:#DC143C;color:white;"} else if (entry.source == 'noq') { print "background-color:yellow;color:black;"} else { print "background-color:white;color:black;"} %>">
    <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; text-transform:lowercase;width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
        <a href="#" class="popper" data-popbox="tsalagidigoweli2" style="<%if (entry.source != 'ced' && entry.source != 'noq') { println "color:white;"}%>">
            ${entry.source}
        </a>
    </div>
</td>
<td>${entry.syllabaryb}<br/>
    <% if (entry.entrya.endsWith("?")) { %>
    ${entry.entrya}
    <% } else { %>
    <g:pronunciation value="${entry.entrya}"/>
    <% } %>
    <g:intonation tone="${entry.entrytone}"/>
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
<td>${definition}</td>
<% } %>

<td>${entry.nounadjpluralsyllf}<br/>
    <g:pronunciation value="${entry.nounadjplurale}"/>
    <g:intonation tone="${entry.nounadjpluraltone}"/>
</td>
<td>${entry.vfirstpresh}<br/>
    <g:pronunciation value="${entry.vfirstpresg}"/>
    <g:intonation tone="${entry.vfirstprestone}"/>
    %{--${entry.vfirstpresh}<br/>--}%
    %{--<g:readingDisplay value="${entry.vfirstpresg}" tone="${entry.vfirstprestone}"/>--}%
</td>
<td>${entry.vthirdpastsyllj}<br/>
    <g:pronunciation value="${entry.vthirdpasti}"/>
    <g:intonation tone="${entry.vthirdpasttone}"/>
</td>
<td>${entry.vthirdpressylll}<br/>
    <g:pronunciation value="${entry.vthirdpresk}"/>
    <g:intonation tone="${entry.vthirdprestone}"/>
</td>
<td>${entry.vsecondimpersylln}<br/>
    <g:pronunciation value="${entry.vsecondimperm}"/>
    <g:intonation tone="${entry.vsecondimpertone}"/>
</td>
<td>${entry.vthirdinfsyllp}<br/>
    <g:pronunciation value="${entry.vthirdinfo}"/>
    <g:intonation tone="${entry.vthirdinftone}"/>
</td>
<td>${raw(entry.sentencesyllr)}<br/>
    ${raw(entry.sentenceq)}<br/>
    <i>${raw(entry.sentenceenglishs)}</i>
</td>
</tr>
</table>