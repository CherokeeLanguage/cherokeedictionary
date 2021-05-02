<%@ page import="net.cherokeedictionary.util.Tense" contentType="text/html;charset=UTF-8" %>

<html>
    <head>
        <title></title>
    </head>
    <body>
        <%
            def initialPrefixes = ["y", "j", "w", "n", "de", "da", "di", "i", "ga"]

            def verbRoot = "woni"
            def verbEnding = "h"
            def Tense verbTense = Tense.PRESENT

            def makeDisplayVerb = {
                return "ga${verbRoot}${verbEnding}a"
            }
        %>

        <select>
            <option>Present</option>
        </select>

        <div style="display:table">
            <div style="display:table-row">
                <div style="display:table-cell"><%= makeDisplayVerb() %></div>
            </div>
        </div>
    </body>
</html>
