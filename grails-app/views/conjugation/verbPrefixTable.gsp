<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 5/1/21
  Time: 11:17 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Verb Prefix Table</title>
</head>

<body>
This table shows the prefixes put before different prefixes according to the rules in the DF Grammar Guide<br/>
<%
    def initialPrefixes = ['y', 'j', 'w', 'n', 'de', 'da', 'di', 'i', 'ga', 'e']
    def reflexivePrefix = ['adad']
    def pronominalPrefixes = ['j', "h", 'g'] //i, i, a
    def paddingSize = 50

    def yMorpheme = {row, column ->
        if (row == 'y' && column == 'w') {
            return 'yuw'
        }
    }

    def testVerb = "gawoniha"
    def testVerbRoot = "woni"
    def testVerbRootSuffix = "h"
    def testVerbPresentEnding = "a"
%>
preVowel, preConsonant, other rules

<div style="display:table;border: 1px solid #ccc!important;">
    <%
        out << raw("<div style=\"display:table-row;border: 1px solid #ccc!important;\">")
        out << raw("<div style=\"display:table-cell;padding-right: ${paddingSize}px;border: 1px solid #ccc!important;\">")
        out << raw("</div>")
        initialPrefixes.each {
            out << raw("<div style=\"display:table-cell;padding-right: ${paddingSize}px;border: 1px solid #ccc!important;\">")
            out << it
            out << raw("</div>")
        }

        pronominalPrefixes.each {
            out << raw("<div style=\"display:table-cell;padding-right: ${paddingSize}px;border: 1px solid #ccc!important;\">")
            out << it
            out << raw("</div>")
        }

        out << raw("</div>")
        initialPrefixes.each {row ->
            out << raw("<div style=\"display:table-row;border: 1px solid #ccc!important;\">")

            out << raw("<div style=\"display:table-cell;padding-right: ${paddingSize}px;border: 1px solid #ccc!important;\">")
            out << row
            out << raw("</div>")

            initialPrefixes.each {column ->
                out << raw("<div style=\"display:table-cell;padding-right: ${paddingSize}px;border: 1px solid #ccc!important;\">")
                out << yMorpheme(row, column)
                out << raw("</div>")
            }

            pronominalPrefixes.each {
                out << raw("<div style=\"display:table-cell;padding-right: ${paddingSize}px;border: 1px solid #ccc!important;\">")
//                out << it
                out << raw("</div>")
            }

            out << raw("</div>")

        }

        pronominalPrefixes.each {
            out << raw("<div style=\"display:table-row;border: 1px solid #ccc!important;\">")
            out << raw("<div style=\"display:table-cell;padding-right: ${paddingSize}px;border: 1px solid #ccc!important;\">")
                out << it
            out << raw("</div>")
            out << raw("</div>")
        }
    %>
</div>

</body>
</html>