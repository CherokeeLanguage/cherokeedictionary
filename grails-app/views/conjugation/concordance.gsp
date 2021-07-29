<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 7/21/2021
  Time: 12:49 PM
--%>

<%@ page import="cherokee.corpus.Verse" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Concordance</title>
</head>

<body>
<%
    def verses = Verse.findAll("from Verse where source=?0", ["chr"])
    def words = [:]
    verses.each {
        def split = it.verseContext.split(" ")
        split.each {spl ->
            spl = spl.replaceAll("\\?", "").replaceAll(";", "").replaceAll("\\.", "").replaceAll(",", "").replaceAll("\"", "")

            if (words.containsKey(spl)) {
                def val = words.get(spl)
                val << "${it.bookName} ${it.chapterNumber}:${it.verseNumber}"
            } else {
                def tmp = []
                tmp << "${it.bookName} ${it.chapterNumber}:${it.verseNumber}"
                words.put(spl, tmp)
            }
        }
    }

    words.each {
        out << raw("${it.key} -- ${it.value}<br/>")
    }
%>
%{--    <%@ include file="concordanceData.gsp" %>--}%

</body>
</html>