<%@ page import="net.cherokeedictionary.dictionary.Likespreadsheets" %>

<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Announcements</title>
</head>
<body>

<g:each in="${net.cherokeedictionary.admin.Announcements.list(order: 'desc')}">
    %{-- dont' show the alert message but show all of the rest --}%
    <%if (it.id != 2) {%>
        ${raw(it.announcement)}<br/>
    <%} %>
</g:each>

</body>
</html>