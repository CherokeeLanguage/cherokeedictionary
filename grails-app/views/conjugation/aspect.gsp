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
    if (it.startsWith("u") || it.startsWith("a")) {
        startNumber = 1
    } else if (it.startsWith("du") || it.startsWith("ga") || it.startsWith("da")) {
        startNumber = 2
    }

    def tmp = it != null && it.size() > 0 ? it.substring(startNumber, it.size() - ending.size()) : ""//a

    def lastVindex = lastVowelIndex(tmp)

    if (lastVindex > -1) {
        def tma = tmp.substring(0, lastVindex)
        def lastRemove = 0
        if (tma.endsWith("in") || tma.endsWith("en")) {
            lastRemove = 2
        } else if (tma.endsWith("ehn")) {
            lastRemove = 3
        }

        return tmp.substring(lastVindex-lastRemove)
    }

    return tmp
}


entries.each {entry ->
    def habitual = entry.vthirdpresk.trim()
    def imperative = entry.vsecondimperm
    def infinitive = entry.vthirdinfo
    def present1st = entry.vfirstpresg
    def present3rd = entry.entrya
    def remotepast =  entry.vthirdpasti
    if (entry.partofspeechc == 'vi' || entry.partofspeechc == "vt") { %>
    <pre><%= entry.definitiond %> -- <%= entry.partofspeechc %>
    <%= parse(habitual, "oi") %>    <%= habitual %>
    <%= parse(present1st, "a") %>   <%= present1st %>
    <%= parse(present3rd, "a") %>   <%= present3rd %>
    <%= parse(remotepast, "vi") %>  <%= remotepast %>
    <%= parse(imperative, "a") %>   <%= imperative %>
    <%= parse(infinitive, "di") %>  <%= infinitive %>
    </pre>

    <% } %>
<% } %>
%{--<%= imperative.substring(0, imperative.lastIndexOf("")) %><br/>--}%
%{--<%= infinitive.substring(0, imperative.lastIndexOf("")) %>--}%
</body>
</html>