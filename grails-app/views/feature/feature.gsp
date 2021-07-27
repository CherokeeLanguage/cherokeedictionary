<%@ page import="cherokee.dictionary.Likespreadsheets" %>
<html>
<body>
<%
    def max = request.getParameter("max") ? Integer.parseInt(request.getParameter("max")) : 20
    def offset = request.getParameter("offset") ? Integer.parseInt(request.getParameter("offset")) : 20
    def results = Likespreadsheets.findAll("from Likespreadsheets where source=?0", ['ced'], [max: max, sort: "entrya", order: "desc", offset: offset])
%>
<g:each in="${results}" var="entry">
    ${entry.entrya}<br/>
</g:each>
<% if (offset > 20) {%>
<a href="?max=${max}&offset=${offset-20}">back page</a>
<% } %>
<a href="?max=${max}&offset=${offset+20}">next page</a>
</body>
</html>