<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 7/26/21
  Time: 10:18 AM
--%>

<%@ page import="net.cherokeedictionary.util.Tense; net.cherokeedictionary.stemmer.DefinitionLine; net.cherokeedictionary.stemmer.Stemmer; net.cherokeedictionary.dictionary.Likespreadsheets" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Aspect Pattern Finder</title>
</head>
<body>
<%
def entries = Likespreadsheets.findAll("from Likespreadsheets where (partofspeechc=?0 or partofspeechc=?1) and (source=?2 or source=?3)", ["vi", "vt", "CED", "RRD"])

def lastVowelIndex = {String yourString ->
    for(int i = yourString.length() - 1; i > 0; i--) {
        if(yourString.charAt(i) == 'a' || yourString.charAt(i) == 'e' || yourString.charAt(i) == 'i' || yourString.charAt(i) == 'o' || yourString.charAt(i) == 'u' || yourString.charAt(i) == 'v') {
            return i
//            break;
        }
    }
}

def parse = {it, ending ->
    def startNumber = 0
//    if (it.startsWith("u") || it.startsWith("a")) {
//        startNumber = 1
//    } else if (it.startsWith("du") || it.startsWith("ga") || it.startsWith("da")) {
//        startNumber = 2
//    }

    def tmp = ""

    if (ending instanceof List) {
        ending.each {end->
            if (it.endsWith(end)) {
                tmp = it.substring(startNumber, it.size() - 1)
            }
        }
    }

    if (!tmp) {
        tmp = it != null && it.size() > 0 ? it.substring(startNumber, it.size() - ending.size()) : ""//a
    }

    def lastVindex = lastVowelIndex(tmp)

    if (lastVindex > -1) {
        def tma = tmp.substring(0, lastVindex)
        def lastRemove = 0
//        if (tma.endsWith("in") || tma.endsWith("en")) {
//            lastRemove = 2
//        } else if (tma.endsWith("ehn")) {
//            lastRemove = 3
//        }

        return tmp.substring(lastVindex-lastRemove)
    }

    return tmp
}

Set<String> habset = new HashSet<String>()
Set<String> pres1set = new HashSet<String>()
Set<String> pastset = new HashSet<String>()
Set<String> imperset = new HashSet<String>()
Set<String> infset = new HashSet<String>()
Set<String> pres3set = new HashSet<String>()

def hablst = []
def pres1lst = []
def pastlst = []
def imperlst = []
def inflst = []
def pres3lst = []

def count = 0;
entries.each {entry ->
    def habitual = entry.vthirdpresk.trim()
    def imperative = entry.vsecondimperm.trim()
    def infinitive = entry.vthirdinfo.trim()
    def present1st = entry.vfirstpresg.trim()
    def present3rd = entry.entrya.trim()
    def remotepast =  entry.vthirdpasti.trim()

    hablst << habitual
    pres1lst << present1st
    pastlst << remotepast
    imperlst << imperative
    inflst << infinitive
    pres3lst << present3rd
%>
       <% if (count % 2 == 0) { %>
            </div><div style="float:left">
     <% } %>
<%
    if (entry.partofspeechc == 'vi' || entry.partofspeechc == "vt") {
%>

<% habset << parse(habitual, "oi") + "oi" %>
<% pres1set << parse(present1st, "a") + "a" %>
<% pres3set << parse(present3rd, "a") + "a" %>
<% pastset << parse(remotepast, "vi") + "vi"%>
<% imperset << parse(imperative, ["i", "a"]) %>
<% infset << parse(infinitive, "di") + "di" %>
    <pre><%= entry.definitiond %> -- <%= entry.partofspeechc %> -- <%= entry.entrya %> -- <%= entry.source %>
    <%= parse(habitual, "oi") %>          <%= habitual %> -- hab -- oi
    <%= parse(present1st, "a") %>          <%= present1st %> -- pres1 -- a
    <%= parse(present3rd, "a") %>          <%= present3rd %> -- pres3 -- a
    <%= parse(remotepast, "vi") %>          <%= remotepast %> -- past -- vi
    <%= parse(imperative, "i") %>          <%= imperative %> -- imp -- i
    <%= parse(infinitive, "di") %>          <%= infinitive %> -- inf -- di</pre>
<%
        count++
    } %>
<% } %>
<br/><br/>
<div>
hab <%= habset.sort() %><br/>
pres1 <%= pres1set.sort() %><br/>
pres3 <%= pres3set.sort() %><br/>
past <%= pastset.sort() %><br/>
imp <%= imperset.sort() %><br/>
inf <%= infset.sort() %><br/>
<table>
    <tr>
        <td>Present 3rd</td>
        <td>Present 1st</td>
        <td>Past</td>
        <td>Infinitive</td>
        <td>Imperative</td>
        <td>Habitual</td>
    </tr>
    <% hablst.eachWithIndex {it, idx -> %>
    <tr>
        <td><%= pres3lst.get(idx) %></td>
        <td><%= pres1lst.get(idx) %></td>
        <td><%= pastlst.get(idx) %></td>
        <td><%= inflst.get(idx) %></td>
        <td><%= imperlst.get(idx) %></td>
        <td><%= it %></td>
    </tr>
    <% } %>
</table>

</div>
</body>
</html>