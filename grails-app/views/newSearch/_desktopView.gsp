<%@ page import="cherokee.Settings; cherokee.corpus.Verse; cherokee.dictionary.Likespreadsheets; cherokee.dictionary.SourceManagement; java.util.regex.Pattern; java.util.regex.Matcher" %>
<table>
<% if (session.getAttribute("loggedin")) { %><th>Edit</th><% } %>
<th style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0"></th>
<th>Cherokee Syllabary/<br/>Phonetic</th>
<th>Part of<br/>speech</th>
<th>English</th>
<th>Plural</th>
<th>Verb<br/>1st present</th>
<th>Verb<br/>3rd past</th>
<th>Verb<br/>3rd present habitual</th>
<th>Verb<br/>2nd imperative</th>
<th>Verb<br/>3rd infinitive</th>
<th>Sentence</th>
<th>See Also</th>
</tr>
<% def showBible = Settings.findAll("from Settings where setting_name=?0", ['showBible'])[0].value%>
<g:each in="${entries}" var="entry" status="i">
<% if (showBible == 'true' && entry instanceof Verse) {
        def listv = Verse.findAll("from Verse where chapterNumber=?0 and bookName=?1 and verseNumber=?2", [entry.chapterNumber, entry.bookName, entry.verseNumber])
%>
    <tr>
        <% if (session.getAttribute("loggedin")) { %><td></td><% } %>
        <td style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0"></td>
        <td>${entry.bookName} ${entry.chapterNumber}: ${entry.verseNumber}</td>
        <td colspan="9">
<%
        listv.each {
            def color = ""
            def fontColor = ""

            if (it.source == 'chr') {
                color = "orange"
                fontColor = "white"
            } else if (it.source == 'asv') {
                color = "blue"
                fontColor = "white"
            } else if (it.source == 'bbe') {
                color = "yellow"
                fontColor = "black"
            } else if (it.source == 'dar') {
                color = "white"
                fontColor = "black"
            } else if (it.source == 'wbt') {
                color = "black"
                fontColor = "white"
            }
%>
            <div style="display:table-row;height:50px">
                <div style="display:table-cell;background-color: ${color}; color: ${fontColor}; padding: 5px">
                    <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; text-transform:lowercase;width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
                        <a href="#" class="popper" style="color: ${fontColor}" data-popbox="thb">
                            ${it.source}
                        </a>
                    </div>
                    <div id="thb" class="popbox">
                        <b>chr</b> - Cherokee New Testament<br/>
                        <b>asv</b> - American Standard Version<br/>
                        <b>bbe</b> - Bible in Basic English<br/>
                        <b>dar</b> - Darby Version<br/>
                        <b>wbt</b> - Webster Bible Translation<br/>
                        <b>web</b> - World English Bible<br/>
                    </div>
                </div>
                <div style="display:table-cell;padding-left: 5px">${it.verseContext}</div>
            </div>
    <% } %>
        </td>
        <td></td>
    </tr>
%{--    <tr>
        <td style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0"></td>
        <td colspan="11">${entry.verseContext}</td>
    </tr>--}%

    <% } else  if (entry instanceof cherokee.dictionary.Likespreadsheets) { %>
    <%
            Pattern p = Pattern.compile("([0-9]?.?\\s?[a-zA-Z'(),\\s.]*)?([0-9].\\s?[a-zA-Z'(),\\s.]*)?([0-9].\\s?[a-zA-Z'(),\\s.]*)?");

            String definition = entry.definitiond;
            Matcher m = p.matcher(definition)
            String first = "";
            String second = "";
            String third = "";

            if (m.matches()) {
                switch (m.groupCount()) {
                    case 3:
                        third = m.group(3);
                    case 2:
                        second = m.group(2);
                    case 1:
                        first = m.group(1);
                }
            }

            def tdstyle = "";
            def divstyle = "";
            def anchorstyle = "";
            def sourceMgmt = SourceManagement.findByCode(entry.source)

    %>
    <tr class="${(i % 2) == 0 ? 'even' : 'odd'}" id="edit${i}">
        <% if (session.getAttribute("loggedin")) { %><td><a href="/likespreadsheets/show/${entry.id}" target="_blank">edit</a></td><%}%>
        <td style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;background-color:${sourceMgmt?.color};text-color${sourceMgmt?.textColor}">
            <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; text-transform:lowercase;width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
                <a href="#" class="popper" data-popbox="tsalagidigoweli2" style="<%if (entry.source != 'ced' && entry.source != 'noq') { println "color:white;"}%>">
                    ${sourceMgmt.code}
                </a>
            </div>
            <div id="tsalagidigoweli2" class="popbox">
            <%-- not sure why the colors work above but here the code is always the same - i'm missing something - timo 4may21--%>
            %{--                    <% out << sourceMgmt.code--}%
            %{--                    out << sourceMgmt.bibliographyFullAPA %>--}%
                <g:each var="source" in="${cherokee.dictionary.SourceManagement.findAll([sort: 'code', order: 'asc'])}">
                    <b>${source.code.toUpperCase()}</b> - ${source.bibliographyFullAPA}<br/>
                </g:each>
            </div>
        </td>
        <td>
            <span id="syllabarybmain${i}"><a class="iframe" href="/newSearch/individual?id=${entry.id}" alt="click here to find out more about ${entry.syllabaryb}">${entry.syllabaryb}</a><%if (entry.definitionlarge || entry.etymology || entry.category) {%>&nbsp;&nbsp;<i class="fas fa-plus-square"></i><%}%></span><br/>
            <span id="entryamain${i}">
                <% if (entry.entrya.endsWith("?")) { %>
                ${entry.entrya}
                <% } else { %>
                <g:pronunciation value="${entry.entrya}"/>
                <% } %>
            </span>
            <span id="entryatonemain${i}"><g:intonation tone="${entry.entrytone}"/></span>
            <%
                    def isVerb = entry.partofspeechc == 'vt' || entry.partofspeechc == 'vi';
                    if (isVerb) {// && session.getAttribute("loggedin")
            %>

            %{-- //floats over the page -- doesn't work for these two links but might work for something else class='iframe'--}%
            <span><br/><a href="/newSearch/conjdisplay?set=${entry.partofspeechc}&id=${entry.id}">conjugate</a></span>
            <span><br/><a href="/newSearch/conjtabledisplay?set=${entry.partofspeechc}&id=${entry.id}">verb table</a></span>
            <%if (session.getAttribute("loggedin")) {%><span><br/><a href="/newSearch/verbgenerator?set=${entry.partofspeechc}&id=${entry.id}">verb generator</a><br/></span><%}%>
        %{--<span><a href="#" id="show${i}">show</a></span>--}%
        <% } %>
        <% if (!expand) { %>
            <br/>
            <a href="/share/${entry.id}">share</a>
            <% } %>
        </td>
        <td>${entry.partofspeechc}</td>
        <% if (first.trim() != "") { %>
        <td>${first}<br/>
            ${second}
            <% if (third) { %>
            <br/>${third}
        <% } %>
        </td>
        <% } else { %>
        <td><span id="definitiondmain${i}">${raw(definition)}</span></td>
        <% } %>

        <td><span id="nounadjpluralsyllfmain${i}">${entry.nounadjpluralsyllf}</span><br/>
            <span id="nounadjpluralemain${i}"><g:pronunciation value="${entry.nounadjplurale}"/></span>
            <span id="nounadjpluraltonemain${i}"><g:intonation tone="${entry.nounadjpluraltone}"/></span>
        </td>
        <td><span id="vfirstpreshmain${i}">${entry.vfirstpresh}</span><br/>
            <span id="vfirstpresgmain${i}"><g:pronunciation value="${entry.vfirstpresg}"/></span>
            <span id="vfirstprestonemain${i}"><g:intonation tone="${entry.vfirstprestone}"/></span>
            %{--${entry.vfirstpresh}<br/>--}%
            %{--<g:readingDisplay value="${entry.vfirstpresg}" tone="${entry.vfirstprestone}"/>--}%
        </td>
        <td><span id="vthirdpastsylljmain${i}">${entry.vthirdpastsyllj}</span><br/>
            <span id="vthirdpastimain${i}"><g:pronunciation value="${entry.vthirdpasti}"/></span>
            <span id="vthirdpasttonemain${i}"><g:intonation tone="${entry.vthirdpasttone}"/></span>
        </td>
        <td><span id="vthirdpressylllmain${i}">${entry.vthirdpressylll}</span><br/>
            <span id="vthirdpreskmain${i}"><g:pronunciation value="${entry.vthirdpresk}"/></span>
            <span id="vthirdprestonemain${i}"><g:intonation tone="${entry.vthirdprestone}"/></span>
        </td>
        <td><span id="vsecondimpersyllnmain${i}">${entry.vsecondimpersylln}</span><br/>
            <span id="vsecondimpermmain${i}"><g:pronunciation value="${entry.vsecondimperm}"/></span>
            <span id="vsecondimpertonemain${i}"><g:intonation tone="${entry.vsecondimpertone}"/></span>
        </td>
        <td><span id="vthirdinfsyllpmain${i}">${entry.vthirdinfsyllp}</span><br/>
            <span id="vthirdinfomain${i}"><g:pronunciation value="${entry.vthirdinfo}"/></span>
            <span id="vthirdinftonemain${i}"><g:intonation tone="${entry.vthirdinftone}"/></span>
        </td>
        <td><span id="sentencesyllrmain${i}">${raw(entry.sentencesyllr)}</span><br/>
            <span id="sentenceqmain${i}">${raw(entry.sentenceq)}</span><br/>
            <i><span id="sentenceenglishsmain${i}">${raw(entry.sentenceenglishs)}</span></i>
        </td>
        <%
                String crossReference = entry.crossreferencet;
        %>
        <td>
            <% if (entry.crossreferencet) {
                String[] splits = crossReference.split(", ");
            %>
            <g:each var="cf" in="${splits}">
                <%
                        String cleanedValue = cf.trim();
                        if (cleanedValue.contains("(")) {
                            cleanedValue = cleanedValue.substring(0, cleanedValue.indexOf("(")).trim();
                        }
                %>

                <a href="/newSearch/xrefdisplay?old=${cleanedValue}&current=${entry.id}">${cf.trim()}</a><br/>
            </g:each><br/>
            <% } %>
        </td>
    </tr>
    <% } %>
</g:each>
</table>
