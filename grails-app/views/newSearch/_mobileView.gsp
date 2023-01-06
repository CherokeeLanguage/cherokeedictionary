<%@ page import="net.cherokeedictionary.admin.Settings; net.cherokeedictionary.admin.Settings; net.cherokeedictionary.admin.SourceManagement; net.cherokeedictionary.admin.SourceManagement; net.cherokeedictionary.audio.AudioLink; net.cherokeedictionary.audio.AudioFile; net.cherokeedictionary.corpus.Verse;   net.cherokeedictionary.admin.SourceManagement; java.util.regex.Pattern; java.util.regex.Matcher" %>
<% def showBible = net.cherokeedictionary.admin.Settings.findAll("from Settings where setting_name=?0", ['showBible'])[0].value%>

<g:each in="${entries}" var="entry" status="i">
<%
        def bible = false
        def tdstyle = ""
        def divstyle = ""
        def anchorstyle = ""

        def hasAudio = false
        def settings = Settings.findAll("from Settings where setting_name=?0", ['showAudio'])
        def audioFile = null
        String definition = ""
        String first = ""
        String second = ""
        String third = ""

        if (showBible == 'true' && entry instanceof Verse) {
            bible = true

        }
%>
    <% if (!bible) {
        definition = entry.definitiond
        Pattern p = Pattern.compile("([0-9]?.?\\s?[a-zA-Z'(),\\s.]*)?([0-9].\\s?[a-zA-Z'(),\\s.]*)?([0-9].\\s?[a-zA-Z'(),\\s.]*)?");

        Matcher m = p.matcher(definition)

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

        if (settings) {
            if (settings[0].value == 'true') {
                def audioLink = AudioLink.findByLikespreadsheets(entry)
                if (audioLink) {
                    audioFile = AudioFile.findAllById(audioLink.audioFileId)
                    if (audioFile) {
                        hasAudio = true
                    }
                }
            }
        }
    }
%>
    <% if (!expand) { %>
        <div class="accordion ${(i % 2) == 0 ? 'even' : 'odd'}" style="margin: 0 0 0 0;padding: 0 0 0 0; border-spacing: 0" id="accordionClick${i}">
    <% } else { %>
        <div class="accordion ${(i % 2) == 0 ? 'even' : 'odd'}" style="margin: 0 0 0 0;padding: 0 0 0 0; border-spacing: 0">
    <% } %>
        <div style="width:500px;display:table-cell">
            <% if (!bible) { %>
            <div style="height:60px;width:25px;display:table-cell;margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;background-color:${net.cherokeedictionary.admin.SourceManagement.findByCode(entry.source)?.color};text-color${SourceManagement.findByCode(entry.source)?.textColor}">
                <div style="display:table-cell;transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; text-transform:lowercase;width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
                    <a href="#" class="popper" data-popbox="tsalagidigoweli2" style="text-align: center; <%if (entry.source != 'ced' && entry.source != 'noq') { println "color:white;"}%>">
                        ${entry.source}
                    </a>
                </div>
            </div>
            <% } else { %>
            <div style="display:table-cell">
                ${entry.verseContext}
            </div>
            <% } %>
            <div style="display:table-cell;vertical-align: middle;padding-left:10px;padding-top:5px; padding-bottom:5px">
                <% if (!bible) { %>
                    ${entry.syllabaryb} - <% if (!first.trim().equals("")) { %>${first}<%if (entry.definitionlarge || entry.etymology || entry.category) {%>&nbsp;&nbsp;<i class="fas fa-plus-square"></i><%}%><%if (hasAudio) {%>&nbsp;&nbsp;<i class="fa-solid fa-play"></i><%}%><br/>
                    <%if (second && !second.trim().equals("")) { %>${second}<br/><%}%>
                    <% } else { %>${raw(definition)}<br/>
                    <% } %>
                    <%if (entry.sentencesyllr) {%>${raw(entry.sentencesyllr)}<br/>${raw(entry.sentenceenglishs)}<%}%>
                <% } else { %>
                <% } %>
            </div>
        </div>
        <% if (expand) { %>
            <div style="display:table-cell;width:50px;padding-bottom:5px;vertical-align:middle;text-align:right;"></div>
        <% } else { %>
            <div style="display:table-cell;width:50px;padding-bottom:5px;vertical-align:middle;text-align:right;" id="arrowClick${i}"><i class="fas fa-sort-down fa-2x"></i></div>
        <% } %>
        <script>
            $(function() {
                $('#accordionClick${i}').click(function() {
                    var downarrow = $('#arrowClick${i}').children('.fa-sort-down');
                    var uparrow = $('#arrowClick${i}').children('.fa-sort-up');
                    if (downarrow != null) {
                        downarrow.attr('class', 'fas fa-sort-up fa-2x');
                    }

                    if (uparrow != null) {
                        uparrow.attr('class', 'fas fa-sort-down fa-2x');
                    }
                });
            });
        </script>
    </div>
    <div class="panel">
        <% if (!bible) { %>
        <table>
            <% if (!expand) { %>
            <tr><td><span><a href="/share/${entry.id}">share</a><br/></span></td></tr>
            <% } %>
            <tr>
                <td>
                    <b>Source:</b><br/>
                    <%switch (entry.source) {
                        case "ced":
                            out << "Cherokee English Dictionary"
                            break;
                        case "cwl":
                            out << "Consortium Word List"
                            break;
                        case "cnomed":
                            out << "Cherokee Nation Online Medical Terminology"
                            break;
                        case "ncmed":
                            out << "Cherokee North Carolina Medical Terminology"
                            break;
                        case "noq":
                            out << "Noquisis Word List"
                            break;
                        case "rrd":
                            out << "Raven Rock Dictionary"
                            break;
                        case "msct":
                            out << "Microsoft Computer Terminology"
                            break;
                    } %>

                    <br/>
                </td>
            </tr>
            <%if (entry.partofspeechc) {%>
            <tr>
                <td><b>Part of Speech</b><br/>
                    ${entry.partofspeechc}</td>
            </tr>
            <%}%>
            <% if (first.trim() != "") { %>
            <tr>
                <td>
                    <b>English:</b><br/>
                    ${first}<br/>
                    ${second}
                    <% if (third) { %>
                    <br/>${third}
                <% } %>
                </td>
            </tr><tr>
            <% } else { %>
            <td><b>English:</b><br/><span id="definitiondmain${i}">${raw(definition)}</span></td>
            <% } %>
        </tr>
            <tr>
                <td>

                    <b>Cherokee Syllabary/<br/>Phonetic:</b><br/>
                    <span id="syllabarybmain${i}">${entry.syllabaryb}</span><br/>
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

                    <span><br/><a href="/newSearch/conjdisplay?set=${entry.partofspeechc}&id=${entry.id}">conjugate</a></span>
                    <span><br/><a href="/newSearch/conjtabledisplay?set=${entry.partofspeechc}&id=${entry.id}">verb table</a><br/></span>
                    <% } %>


                </td>
            </tr>

            <%if (entry.nounadjpluralsyllf) {%>
            <tr>
                <td><b>Plural Forms</b><br/>
                    <span id="nounadjpluralsyllfmain${i}">${entry.nounadjpluralsyllf}</span><br/>
                    <span id="nounadjpluralemain${i}"><g:pronunciation value="${entry.nounadjplurale}"/></span>
                    <span id="nounadjpluraltonemain${i}"><g:intonation tone="${entry.nounadjpluraltone}"/></span>
                </td>
            </tr>
            <%}%>
            <%if (entry.partofspeechc == 'vi' || entry.partofspeechc == 'vt' || entry.partofspeechc == 'vi' || entry.partofspeechc == 'vt') {%>
            <tr>
                <td>
                    <b>Verb First Present</b><br/>
                    <span id="vfirstpreshmain${i}">${entry.vfirstpresh}</span><br/>
                    <span id="vfirstpresgmain${i}"><g:pronunciation value="${entry.vfirstpresg}"/></span>
                    <span id="vfirstprestonemain${i}"><g:intonation tone="${entry.vfirstprestone}"/></span><br/>
                </td>
            </tr>
            <tr>
                <td>
                    <b>Verb Third Past</b><br/>
                    <span id="vthirdpastsylljmain${i}">${entry.vthirdpastsyllj}</span><br/>
                    <span id="vthirdpastimain${i}"><g:pronunciation value="${entry.vthirdpasti}"/></span>
                    <span id="vthirdpasttonemain${i}"><g:intonation tone="${entry.vthirdpasttone}"/></span>
                </td>
            </tr><tr>
            <td><b>Verb Third Present Habitual</b><br/>
                <span id="vthirdpressylllmain${i}">${entry.vthirdpressylll}</span><br/>
                <span id="vthirdpreskmain${i}"><g:pronunciation value="${entry.vthirdpresk}"/></span>
                <span id="vthirdprestonemain${i}"><g:intonation tone="${entry.vthirdprestone}"/></span>
            </td>
        </tr><tr>
            <td>
                <b>Verb Second Imperative</b><br/>
                <span id="vsecondimpersyllnmain${i}">${entry.vsecondimpersylln}</span><br/>
                <span id="vsecondimpermmain${i}"><g:pronunciation value="${entry.vsecondimperm}"/></span>
                <span id="vsecondimpertonemain${i}"><g:intonation tone="${entry.vsecondimpertone}"/></span>
            </td>
        </tr><tr>
            <td><b>Verb Third Infinitive</b><br/>
                <span id="vthirdinfsyllpmain${i}">${entry.vthirdinfsyllp}</span><br/>
                <span id="vthirdinfomain${i}"><g:pronunciation value="${entry.vthirdinfo}"/></span>
                <span id="vthirdinftonemain${i}"><g:intonation tone="${entry.vthirdinftone}"/></span>
            </td>
        </tr>
            <%}%>
            <%if (entry.sentencesyllr) {%>
            <tr>
                <td>
                    <b>Sentences:</b><br/>
                    <span id="sentencesyllrmain${i}">${raw(entry.sentencesyllr)}</span><br/>
                    <span id="sentenceqmain${i}">${raw(entry.sentenceq)}</span><br/>
                    <i><span id="sentenceenglishsmain${i}">${raw(entry.sentenceenglishs)}</span></i>
                </td>
            </tr>
            <%}%>
            <%if (entry.definitionlarge) {%>
            <tr>
                <td>
                    <b>Large Definition:</b><br/>
                    ${raw(entry.definitionlarge)}<br/>
                </td>
            </tr>
            <%}%>
            <%if (entry.category) {%>
            <tr>
                <td>
                    <b>Category:</b><br/>
                    ${raw(entry.category)}<br/>
                </td>
            </tr>
            <%}%>
            <%if (entry.notes) {%>
            <tr>
                <td>
                    <b>Notes:</b><br/>
                    ${raw(entry.notes)}<br/>
                </td>
            </tr>
            <%}%>
            <%if (entry.etymology) {%>
            <tr>
                <td>
                    <b>Etymology:</b><br/>
                    ${raw(entry.etymology)}<br/>
                </td>
            </tr>
            <%}%>

            <%if (entry.crossreferencet) {%>
            <tr>
                <%
                        String crossReference = entry.crossreferencet;
                %>
                <td>
                    <b>See Also:</b><br/>
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
            <%}%>

<%
    if (hasAudio) {
%>
            <tr>
                <td colspan="2">
                    <b>Pronunciation:</b><br/>
                    <audio controls style="width: 100px;height:20px">
                        <source src="${audioFile.audioFilePath[0]}" type="audio/mpeg">
                    </audio>
                </td>
            </tr>
<%
    }
%>
        </table>
        <% } else {
            def listv = Verse.findAll("from Verse where chapterNumber=?0 and bookName=?1 and verseNumber=?2", [entry.chapterNumber, entry.bookName, entry.verseNumber])
        %>
        <table>
            <tr>
%{--                <td></td>--}%
                <td>${entry.bookName} ${entry.chapterNumber}: ${entry.verseNumber}</td>
                <td>
                    <% listv.each {
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
            </tr>
        </table>
        <% } %>
    </div>
</g:each>
    <script>
        var acc = document.getElementsByClassName("accordion");
        var i;

        for (i = 0; i < acc.length; i++) {
            acc[i].addEventListener("click", function() {
                /* Toggle between adding and removing the "active" class,
                to highlight the button that controls the panel */
                this.classList.toggle("active");

                /* Toggle between hiding and showing the active panel */
                var panel = this.nextElementSibling;
                if (panel.style.display === "block") {
                    panel.style.display = "none";
                } else {
                    panel.style.display = "block";
                }
            });
        }
        <% if (expand) { %>
            console.log("right here right now");
            acc[0].click();
        <% } %>
    </script>