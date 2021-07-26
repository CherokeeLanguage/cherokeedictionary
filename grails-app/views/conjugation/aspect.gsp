<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 7/26/21
  Time: 10:18 AM
--%>

<%@ page import="net.cherokeedictionary.util.Tense; net.cherokeedictionary.stemmer.DefinitionLine; net.cherokeedictionary.stemmer.Stemmer; cherokee.dictionary.Likespreadsheets" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Aspect Pattern Finder</title>
</head>
<body>
<%
def entries = Likespreadsheets.findAll("from Likespreadsheets where (partofspeechc=?0 or partofspeechc=?1) and source=?2", ["vi", "vt", "CED"])

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
entries.each {entry ->
    def habitual = entry.vthirdpresk.trim()
    def imperative = entry.vsecondimperm.trim()
    def infinitive = entry.vthirdinfo.trim()
    def present1st = entry.vfirstpresg.trim()
    def present3rd = entry.entrya.trim()
    def remotepast =  entry.vthirdpasti.trim()
    if (entry.partofspeechc == 'vi' || entry.partofspeechc == "vt") { %>
<% habset << parse(habitual, "oi") + "oi" %>
<% pres1set << parse(present1st, "a") + "a" %>
<% pres3set << parse(present3rd, "a") + "a" %>
<% pastset << parse(remotepast, "vi") + "vi"%>
<% imperset << parse(imperative, ["i", "a"]) %>
<% infset << parse(infinitive, "di") + "di" %>
    <pre><%= entry.definitiond %> -- <%= entry.partofspeechc %> -- <%= entry.entrya %>
    <%= parse(habitual, "oi") %>          <%= habitual %> -- hab -- oi
    <%= parse(present1st, "a") %>          <%= present1st %> -- pres1 -- a
    <%= parse(present3rd, "a") %>          <%= present3rd %> -- pres3 -- a
    <%= parse(remotepast, "vi") %>          <%= remotepast %> -- past -- vi
    <%= parse(imperative, "i") %>          <%= imperative %> -- imp -- i
    <%= parse(infinitive, "di") %>          <%= infinitive %> -- inf -- di
    </pre>
    <% } %>
<% } %>

hab <%= habset.sort() %><br/>
pres1 <%= pres1set.sort() %><br/>
pres3 <%= pres3set.sort() %><br/>
past <%= pastset.sort() %><br/>
imp <%= imperset.sort() %><br/>
inf <%= infset.sort() %><br/>

</body>
</html>