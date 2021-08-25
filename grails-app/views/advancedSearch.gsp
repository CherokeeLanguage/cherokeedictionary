<%@ page import="net.cherokeedictionary.admin.Announcements" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="manager"/>
    <script>
        $(function () {
            %{--$('#englishSearch').autocomplete({--}%
                %{--source: '<g:createLink controller='newSearch' action='ajaxEnglishFinder'/>'--}%
            %{--});--}%

            %{--$('#tsalagiSearch').autocomplete({--}%
                %{--source: '<g:createLink controller='newSearch' action='ajaxTsalagiFinder'/>'--}%
            %{--});--}%

            %{--$('#syllabarySearch').autocomplete({--}%
                %{--source: '<g:createLink controller='newSearch' action='ajaxSyllabaryFinder'/>'--}%
            %{--});--}%

            $('#includeCED').click(function () {
                if (!$("#includeCED").is(":checked")) {
                    $("#includeSentences").prop("checked", false);
                }
            });

            $('#includeSentences').click(function () {
                if ($("#includeSentences").is(":checked")) {
                    $("#includeCED").prop("checked", true);
                }
                /* else {
                 $("#includeCED").prop("checked", true);
                 }*/
            });

            $('#searchForExactMatch').click(function() {
               if ($('#searchForExactMatch').is(":checked")) {
                   $('#regex').attr("disabled", true);
               } else {
                   $('#regex').attr("disabled", false);
               }
            });

            $('#regex').click(function() {
                if ($('#regex').is(":checked")) {
                    $('#searchForExactMatch').attr("disabled", true);
                } else {
                    $('#searchForExactMatch').attr("disabled", false);
                }
            });
        });
    </script>
</head>

<body>
<%
    if (session.getAttribute("loggedin")) {
        out << raw("<div style=\"color : red\">you are logged in</div>")
    }

    if (params.noresults == "true") {
        out << raw("<div style=\"color : red\">NO RESULTS WERE FOUND</div>")
    }
%>
<div>
    <g:form controller="newSearch" action="advancedSearch" method="POST">
        <div style="float:left">
            <b>Advanced Search:</b><br/>
            <table>
                <tr>
                    <td><g:textField id="tsalagiSearch" name="tsalagiSearch"/></td>
                    <td><span id="tsalagiLabel">Cherokee (phonetic)</span></span></td>
                    <td></td>
                </tr>
                <% def msg = g.message(code: "cherokee.submit.button"); %>
                <tr>
                    <td><g:textField id="syllabarySearch" name="syllabarySearch"/></td>
                    <td><span id="syllabaryLabel">${msg}</span></td>
                    <td></td>
                </tr>
                <tr>
                    <td><g:textField id="englishSearch" name="englishSearch"/></td>
                    <td><span id="englishLabel">English</span></td>
                    <td></td>
                </tr>
                <tr>
                    <td><g:select name="posSearch"
                                  from="${net.cherokeedictionary.relational.PartOfSpeech.list()}"
                                  value="partOfSpeech"
                                  optionKey="id"
                                  noSelection="['':'-Select a Part of Speech-']"/></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <td><g:select name="categorySearch"
                                  from="${net.cherokeedictionary.relational.Category.list()}"
                                  value="category"
                                  optionKey="id"
                                  noSelection="['':'-Select a Category-']"/></td>
                    <td><g:submitButton id="searchButton" name="searchButton" value="Search"/></td>
                    <td></td>
                </tr>
            </table>
        </div>

        <div style="float:left">
            <table>
                <tr>
                    <td style="vertical-align:top">
                        <b>Search Options:</b><br/>
                        <table>
                            <tr valign="top">
                                <td><g:checkBox name="regex" id="regex"/></td>
                                <td>Search With Regular Expression</td>
                                <td valign="top"></td>
                            </tr>
                            <tr>
                                <td><g:checkBox name="searchForExactMatch" id="searchForExactMatch"/></td>
                                <td>Search For Exact Match</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td colspan=3></td>
                            </tr>
                            <tr>
                                <td><g:checkBox name="includeCED" id="includeCED" checked="true"/></td>
                                <td>Include Cherokee English Dictionary</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td>&nbsp;&nbsp;<g:checkBox id="includeSentences" name="includeSentences" checked="true"/></td>
                                <td>Include sentences in searches</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><g:checkBox name="includeConsortiumWordList" id="includeConsortiumWordList"
                                                checked="true"/></td>
                                <td>Include Consortium Word List</td>
                                <td></td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeCNOMedWordList" id="includeCNOMedWordList" checked="true"/></td>
                                <td>Include Cherokee Nation Medical Terms</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><g:checkBox name="includeNCMedWordList" id="includeNCMedWordList" checked="true"/></td>
                                <td>Include North Carolina Medical Terms</td>
                                <td></td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeMicrosoftWordList" id="includeMicrosoftWordList"
                                                checked="true"/></td>
                                <td>Include Microsoft Computer Terms</td>
                                <td></td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeNOQWordList" id="includeNOQWordList" checked="true"/></td>
                                <td>Include Noquisis Word List</td>
                                <td></td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeRRD" id="includeRRD" checked="true"/></td>
                                <td>Include Raven Rock Dictionary</td>
                                <td></td>
                            </tr>
                            %{--<tr style="vertical-align: top">--}%
                                %{--<td><g:checkBox name="includeMST" id="includeMST" checked="true"/></td>--}%
                                %{--<td>Include MST</td>--}%
                                %{--<td><a href="#" id="vistMST">Visit MST</a></td>--}%
                            %{--</tr>--}%
                        </table>
                    </td>
                </tr>
            </table>
        </div>
    </g:form>
</div>

<div style="float:left;width:100%">
    <% if (params.noresults != "true") { %>
    Searches will search all fields for your entry.  If you wish to narrow those results uncheck the "Include Sentences in searches" checkbox which will search all fields except sentences.<br/>
    Searches for common words like "and" or "of" may take a little longer than more narrow searches as the results will return more searches<br/>
    Also acceptable are spellings from the Durbin Feeling dictionary such as: nidulasuhlvna, Daligwa, Jalagi<br/><br/>
    You may also use a wildcard (*) in your search.<br/>
    Searching for /ani*ja/ or /a*i*ja/ will find /anichuja/ in both instances as well as the second search returning /achuja/ and /adajagaliha/<br/><br/>


    Advanced Search allows you to search multiple fields at the same time.<br/>
    An example is searching for /ani/ in the Cherokee (phonetic) field and /oys/ in the English field. This will return search results that include /ani/ or search results that include /oys/.<br/>
    Searching for /fi/ in the English field and Category of /numbers, cardinal/ will return
    If you are looking to refine your search further look to the regex search.
    <br/><br/>
    Regular Expression (Regex) Instructions:<br/>
    By checking the "Search With Regular Expressions" checkbox<br/>
    Regex is valid in any of the search fields is accepted.<br/>
    An example is searching for /ani.+/ in the Cherokee (phonetic) field and /.+oys/ in the English field. This will return search results that include /ani/ or search results that include /oys/.<br/>
    <br/>
    <a href="https://www.regexpal.com/" target="_blank">Regex tester</a><br/><br/>
    %{--Query Builder Instructions:--}%
    %{--Allows you to select the column and term or regular expression you want to search by<br/><br/>--}%

    <% } %>

</div>

</body>
</html>
