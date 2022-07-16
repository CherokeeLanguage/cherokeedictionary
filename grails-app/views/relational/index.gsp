<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 5/25/21
  Time: 4:17 PM
--%>

<%@ page import="net.cherokeedictionary.relational.Word" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>
<g:each var="lks" in="${lst}">
    ${net.cherokeedictionary.dictionary.Likespreadsheets.findById(lks.likespreadsheets.id)}<br/><br/>
    ${net.cherokeedictionary.relational.Word.findById(lks.word.id).syllabary}<br/><br/>
</g:each>
</body>
</html>