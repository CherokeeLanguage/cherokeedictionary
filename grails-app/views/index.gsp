<%@ page import="cherokee.dictionary.Announcements" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="manager"/>
    <script>
        $(function () {
            $('#englishSearch').autocomplete({
                source: 'newSearch/ajaxEnglishFinder'%{--<g:createLink controller='newSearch' action='ajaxEnglishFinder'/>'--}%
            });

            $('#tsalagiSearch').autocomplete({
                source: '<g:createLink controller='newSearch' action='ajaxTsalagiFinder'/>'
            });

            $('#syllabarySearch').autocomplete({
                source: '<g:createLink controller='newSearch' action='ajaxSyllabaryFinder'/>'
            });

            //keyboard with syllabary
            // type in latin and it transliterates

            $('#tsalagiKeyboard').autocomplete({
                source: '<g:createLink controller='newSearch' action='ajaxTsalagiKeyboard'/>'
            });

            $('#tsalagiKeyboardCopy').click(function () {
                $('#syllabarySearch').val($('#tsalagiKeyboard').val());
            });

            $('#buyCED').click(function () {
                var win = window.open('http://cherokeegiftshop.com/cherokee-english-dictionary', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitConsortium').click(function () {
                var win = window.open('https://language.cherokee.org/language-programs/cherokee-language-consortium/?fbclid=IwAR3pYc7whWUD8h1Nh4KHbmgwBL-CS2Ls5eoiq4nBmBQ4ylQFW6DRB8OhBVk', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitCNOMed').click(function () {
                var win = window.open('https://language.cherokee.org/posters/science-and-health/', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitCNOHumanBody').click(function () {
                var win = window.open('https://language.cherokee.org/media/ve0jksuw/humanbody.pdf', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitCNOSkeletonPage').click(function () {
                var win = window.open('https://language.cherokee.org/media/rpmooxvr/humanskeleton.pdf', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitNCMed').click(function () {
                var win = window.open('', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitMSCT').click(function () {
                var win = window.open('https://www.microsoft.com/en-us/language/Terminology?rtc=1', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitMSCTTerms').click(function () {
                var win = window.open('https://www.microsoft.com/en-us/language/LicenseAgreement', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitNOQ').click(function () {
                var win = window.open('http://www.yvwiiusdinvnohii.net/Cherokee/CherokeeWordLists/', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#visitRRD').click(function () {
                var win = window.open('http://www.culturev.com/cherokee/Raven-Rock-Cherokee-Dictionary.pdf', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

            $('#win8screenshots').click(function () {
                var win = window.open('https://www.cherokee.org/media/3ukaiatf/windows8screenshots.pdf', '_blank');
                if (win) {
                    win.focus();
                } else {
                    alert('Please allow popups for this site');
                }
            });

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

            $('#tsalagiSearch').click(function () {
                $('#syllabarySearch').val("");
                $('#englishSearch').val("");
            });

            $('#tsalagiSearch').focus(function () {
                $('#syllabarySearch').val("");
                $('#englishSearch').val("");
            });

            $('#syllabarySearch').click(function () {
                $('#tsalagiSearch').val("");
                $('#englishSearch').val("");
            });

            $('#syllabarySearch').focus(function () {
                $('#tsalagiSearch').val("");
                $('#englishSearch').val("");
            });

            $('#englishSearch').click(function () {
                $('#tsalagiSearch').val("");
                $('#syllabarySearch').val("");
            });

            $('#englishSearch').focus(function () {
                $('#tsalagiSearch').val("");
                $('#syllabarySearch').val("");
            });
        });
    </script>
</head>

<body>
<h3>Cherokee-English Dictionary Online Database</h3>
<%
    if (request.getParameter("showSup").equals("false")) {
        session.setAttribute("showSup", false);
    } else if (!request.getParameter("showSup") || request.getParameter("showSup").equals("true")) {
        session.setAttribute("showSup", true);
    }

    if (session.getAttribute("loggedin")) {
        out << raw("<div style=\"color : red\">you are logged in</div>")
//        out << ("setting showSup to " + session.getAttribute("showSup") + "<br/>");
    }

    if (params.noresults == "true") {
        out << raw("<div style=\"color : red\">NO RESULTS WERE FOUND</div>")
    }
%>

<g:todayIs/>

<div>
    <%
        def query = "from Announcements a where a.enabled=true"
        //'from Announcements a where a.enabled = ?', [true]
    %>
    <g:each in="${cherokee.dictionary.Announcements.findAll(query)}">
        ${raw(it.announcement)}<br/>
    </g:each>
</div>
<br/>
<div>
    <div id="tsalagidigoweli" class="popbox">
        <h2>Cherokee (phonetic):</h2>
        Enter your search terms using the latin alphabet without spaces (or dashes) except between words.<br/>
        variations of syllables are acceptable ie. achucha, atsutsa, ajuja, achuja etc - not "a-tsu-tsa" or "a tsu tsa"<br/>
    </div>

    <div id="tsalagidigoweli2" class="popbox">
        <g:message code="cherokee.description"/>
    </div>

    <div id="english" class="popbox">
        <h2>English</h2>
        Enter your search terms using English<br/>
    </div>

    <g:form controller="newSearch" action="dictionary" id="newSearchForm" method="POST">
        <div style="float:left">
            <b>Basic Search:</b><br/>
            <table>
                <tr>
                    <td><g:textField id="tsalagiSearch" name="tsalagiSearch"/></td>
                    <td><g:submitButton id="tsalagiSearchButton" name="tsalagiSearchButton" value="Cherokee (phonetic)"/><span id="tsalagiLabel" style="display:none">Cherokee (phonetic)</span></span></td>
                    <td><a href="#" class="popper" id="tsalagiPopup" data-popbox="tsalagidigoweli" style="color:blue;"><u>Instructions</u></a>
                    </td>
                </tr>
                <% def msg = g.message(code: "cherokee.submit.button"); %>
                <tr>
                    <td><g:textField id="syllabarySearch" name="syllabarySearch"/></td>
                    <td><g:submitButton id="syllabarySearchButton" name="syllabarySearchButton" value="${msg}"/><span id="syllabaryLabel" style="display:none">Syllabary</span></td>
                    <td><a href="#" class="popper" id="syllabaryPopup" data-popbox="tsalagidigoweli2" style="color:blue;"><u>Instructions</u></a>
                    </td>
                </tr>
                <tr>
                    <td><g:textField id="englishSearch" name="englishSearch"/></td>
                    <td><g:submitButton id="englishSearchButton" name="englishSearchButton" value="English"/><span id="englishLabel" style="display:none">English</span></td>
                    <td><a href="#" class="popper" id="englishPopup" data-popbox="english" style="color:blue;"><u>Instructions</u></a></td>
                </tr>
                <tr>
                    <td><g:select name="categorySearch"
                                  from="${cherokee.relational.Category.list()}"
                                  value="category"
                                  optionKey="id"
                                  noSelection="['':'-Select a Category-']"/></td>
                    <td><g:submitButton id="categorySearchButton" name="categorySearchButton" value="Category"/></td>
                    <td></td>
                </tr>
                <tr>
                    <td colspan="2"><a href="/advancedSearch" id="advancedSearch">Advanced Search</a></td>
                </tr>
            </table>
        </div>

        <div style="float:left">
            <table>
                <tr>
                    <td style="vertical-align:top">
                        <b>Search Options:</b><br/>
                        <table>
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
                                <td><a href="#" id="buyCED">Buy CED from Cherokee Gift Shop</a>
                                    %{--<input type="button" name="buyCED" id="buyCED" value="Buy Cherokee English Dictionary">--}%
                                </td>
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
                                <td><a href="#" id="visitConsortium">Visit Consortium Word List Page</a>
                                    %{--<input type="button" name="visitConsortium" id="visitConsortium" value="Visit Consortium Word List Page">--}%
                                </td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeCNOMedWordList" id="includeCNOMedWordList" checked="true"/></td>
                                <td>Include Cherokee Nation Medical Terms</td>
                                <td><a href="#" id="visitCNOMed">Visit CNO Body Parts Page</a><br/>
                                    <a href="#" id="visitCNOHumanBody">Visit CNO Medical Human Body Page</a><br/>
                                    <a href="#" id="visitCNOSkeletonPage">Visit CNO Medical Skeleton Page</a>
                                    %{--<input type="button" name="visitCNOMed" id="visitCNOMed" value="Visit CNO Medical Terms Page">--}%
                                </td>
                            </tr>
                            <tr>
                                <td><g:checkBox name="includeNCMedWordList" id="includeNCMedWordList" checked="true"/></td>
                                <td>Include North Carolina Medical Terms</td>
                                <td>%{--<a href="#" id="visitNCMed">Visit NC Medical Terms Page</a>--}%
                                %{--<input type="button" name="visitNCMed" id="visitNCMed" value="Visit NC Medical Terms Page">--}%</td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeMicrosoftWordList" id="includeMicrosoftWordList"
                                                checked="true"/></td>
                                <td>Include Microsoft Computer Terms</td>
                                <td><a href="#" id="visitMSCT">Visit Microsoft Cherokee Translations Page</a><br/>
                                    <a href="#" id="visitMSCTTerms">Visit Microsoft Cherokee Terms and Conditions Page</a><br/>
                                    <a href="#" id="win8screenshots">Windows 8 Screenshots</a>
                                    %{--<input type="button" name="visitMSCT" id="visitMSCT" value="Visit Microsoft Cherokee Translations Page"><br/>
                                <input type="button" name="visitMSCTTerms" id="visitMSCTTerms" value="Visit Microsoft Cherokee Terms and ConditionsPage">--}%
                                </td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeNOQWordList" id="includeNOQWordList" checked="true"/></td>
                                <td>Include Noquisis Word List</td>
                                <td><a href="#" id="visitNOQ">Visit Noquisis Word List Page</a></td>
                            </tr>
                            <tr style="vertical-align: top">
                                <td><g:checkBox name="includeRRD" id="includeRRD" checked="true"/></td>
                                <td>Include Raven Rock Dictionary</td>
                                <td><a href="#" id="visitRRD">Visit Raven Rock Dictionary Page</a></td>
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

<div style="float:left">
    <% if (params.noresults != "true") { %>
    <br/><b>For All Entries</b><br/>
    Searches will search all fields for your entry.  If you wish to narrow those results uncheck the "Include Sentences in searches" checkbox which will search all fields except sentences.<br/>
    Searches for common words like "and" or "of" may take a little longer than more narrow searches as the results will return more searches<br/>
    Also acceptable are spellings from the Durbin Feeling dictionary such as: nidulasuhlvna, Daligwa, Jalagi<br/><br/>
    You may also use a wildcard (*) in your search.<br/>
    Searching for /ani*ja/ or /a*i*ja/ will find /anichuja/ in both instances as well as the second search returning /achuja/ and /adajagaliha/<br/><br/>

    Basic Search only allows you to search by one field at a time.<br/><br/>
    If you would like to search by more than one field, check out the <a href="/advancedSearch">Advanced Search</a><br/><br/>

    Check out the <a href="https://www.facebook.com/cedprojectpage">Cherokee Dictionary Project</a> Facebook page<br/>
    Check out the Cherokee Language page on <a href="https://www.facebook.com/groups/Jalagi.Gawonihisdi/">Facebook</a><br/>
    %{--Check us out on <a href="https://www.facebook.com/groups/656137241113977/" target="TOP">Facebook</a><br/>--}%
    <br/>
    <% } %>

    DISCLAIMER: By using this site you agree not to hold any person(s) associated with this site responsible for anything.  You also abide by all of the terms of Microsofts usage (<a
        href="http://www.microsoft.com/Language/en-US/LicenseAgreement.aspx">Microsoft Agreement</a>)
    <br/><br/>
</div>

</body>
</html>
