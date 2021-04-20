<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 8/16/2015
  Time: 3:47 PM
--%>

%{--<%@ page import="cherokee.dictionary.verb.conjugation.originalConjugation.Stemmer; cherokee.dictionary.hold.stem.DefinitionLine; cherokee.dictionary.verb.conjugation.originalConjugation.Conjugate; cherokee.dictionary.verb.conjugation.originalConjugation.Tense; com.cobradoc.cherokee.SyllabaryUtil; cherokee.dictionary.Likespreadsheets;" contentType="text/html;charset=UTF-8" %>--}%
<%@ page import="cherokee.dictionary.Likespreadsheets; java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
%{--V2--}%
<%@ page import="net.cherokeedictionary.util.Tense; net.cherokeedictionary.stemmer.Stemmer; net.cherokeedictionary.stemmer.DefinitionLine" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.verb.conjugation.Conjugate" contentType="text/html;charset=UTF-8" %>
%{--V1--}%
%{--<%@ page import="cherokee.dictionary.conjugation.cdpbook.Stemmer; cherokee.dictionary.conjugation.stem.DefinitionLine;cherokee.dictionary.conjugation.wordFormation.Word;cherokee.dictionary.conjugation.Conjugate" contentType="text/html;charset=UTF-8"%>--}%
%{--<%@ page import="cherokee.dictionary.conjugation.conjugate.Tense; cherokee.dictionary.conjugation.conjugate.PartOfSpeech;cherokee.dictionary.conjugation.Conjugate" contentType="text/html;charset=UTF-8"%>--}%
<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Conjugation Table</title>
    <script src="https://code.jquery.com/jquery-latest.min.js"></script>
    <script type="text/javascript">
        $(function() {
            $('#verbTense').change(function() {
                var selectedValue = $('#verbTense').find(":selected").val();
                document.forms['verbTenses'].submit();
            });

            $('#showtranslit').click(function() {
                $('span').toggle();
            });
        });
    </script>
    <style>
    table, th, td {
        /*border: 1px solid black;*/
    }
    </style>
</head>
<%
    def lks = Likespreadsheets.findById(params.id);

    def habitual = lks.vthirdpressylll
    def imperative = lks.vsecondimpersylln
    def infinitive = lks.vthirdinfsyllp
    def present1st = lks.vfirstpresh
    def present3rd = lks.syllabaryb
    def remotepast = lks.vthirdpastsyllj
    def partofspeechc = lks.partofspeechc

    Stemmer stemmer = new Stemmer()
    stemmer.habitual = new DefinitionLine(syllabary: habitual)
    stemmer.imperative = new DefinitionLine(syllabary: imperative)
    stemmer.infinitive = new DefinitionLine(syllabary: infinitive)
    stemmer.present1st = new DefinitionLine(syllabary:  present1st)
    stemmer.present3rd = new DefinitionLine(syllabary: present3rd)
    stemmer.remotepast = new DefinitionLine(syllabary: remotepast)

    String verbTense = params.verbTense ?: "PRESENT";
    Tense vtense = Tense.valueOf(verbTense)
    boolean intransitive = false;
    if (lks.partofspeechc == 'vi') {
        intransitive = true
    }

    Conjugate conjugate = new Conjugate();

    def entry = lks;
%>
<body>
<%if (session.getAttribute("loggedin")){%>
This is to generate conjugation tests easier for boilerplate script<br/>

static def verbName = new Stemmer()<br/>
static {<br/>
verbName.habitual = new DefinitionLine(syllabary: "${stemmer.habitual.syllabary}")<br/>
verbName.imperative = new DefinitionLine(syllabary: "${stemmer.imperative.syllabary}")<br/>
verbName.infinitive = new DefinitionLine(syllabary: "${stemmer.infinitive.syllabary}")<br/>
verbName.present1st = new DefinitionLine(syllabary: "${stemmer.present1st.syllabary}")<br/>
verbName.present3rd = new DefinitionLine(syllabary: "${stemmer.present3rd.syllabary}")<br/>
verbName.remotepast = new DefinitionLine(syllabary: "${stemmer.remotepast.syllabary}")<br/>
}
<% } %>

<form action="/newSearch/conjtabledisplay" name="verbTenses">
    <input type="hidden" name="id" value="${entry.id}"/>
    <select name="verbTense" id="verbTense">
        <option value="${Tense.PRESENT}" <g:if test="${Tense.valueOf(verbTense) == Tense.PRESENT}">selected</g:if>>Present</option>
%{--        <option value="${Tense.RECENT_PAST_IMPERATIVE}">Present/Recent Past Imperative</option>--}%
        <option value="${Tense.REMOTE_PAST}" <g:if test="${Tense.valueOf(verbTense) == Tense.REMOTE_PAST}">selected</g:if>>Remote Past</option>
        <option value="${Tense.HABITUAL}" <g:if test="${Tense.valueOf(verbTense) == Tense.HABITUAL}">selected</g:if>>Habitual</option>
        <option value="${Tense.FUTURE_COMMAND}" <g:if test="${Tense.valueOf(verbTense) == Tense.FUTURE_COMMAND}">selected</g:if>>Immediate</option>
%{--        <option value="${Tense.FUTURE_COMMAND}">Future Imperative</option>--}%
        <option value="${Tense.INFINITIVE}" <g:if test="${Tense.valueOf(verbTense) == Tense.INFINITIVE}">selected</g:if>>Infinitive</option>
%{--        <option value="reportative">Reportative</option>--}%
%{--        <option value="${Tense.PROGRESSIVE_FUTURE}">Future Progressive</option>--}%
%{--        <option value="pluperfect">Pluperfect</option>--}%
    </select>
</form>

<g:render template="/newSearch/simplifiedOriginalEntry" model="[entry:entry]"/>
<table>
    <tr>
        <td>
            <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
                SUBJECT
            </div>
        </td>
        <td>
<% if (intransitive) { %>
<table>
    <tr>
        <td>1SG</td>
        <td><g:display item="${conjugate.conjugate("SG1", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>2SG</td>
        <td><g:display item="${conjugate.conjugate("SG2", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>3SG</td>
        <td><g:display item="${conjugate.conjugate("SG3", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1DLINCL</td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1DLEXCL</td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1PLINCL</td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1PLEXCL</td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>2DL</td>
        <td><g:display item="${conjugate.conjugate("DL2", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>2PL</td>
        <td><g:display item="${conjugate.conjugate("PL2", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>3PL</td>
        <td><g:display item="${conjugate.conjugate("PL3", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
</table>
<% } else { %>
        <center>OBJECT</center>
<table>
    <tr>
        <td></td>
        <td>1SG</td>
        <td>2SG</td>
        <td>3SGAN</td>
        <td>3SGIN</td>
        <td>1DLINCL</td>
        <td>1DLEXCL</td>
        <td>1PLINCL</td>
        <td>1PLEXCL</td>
        <td>2DL</td>
        <td>2PL</td>
        <td>3PLAN</td>
        <td>3PLIN</td>
    </tr>
    <tr>
        <td>1SG</td>
        <td><g:display item="${conjugate.conjugate("SG1", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG1", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>2SG</td>
        <td><g:display item="${conjugate.conjugate("SG2", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG2", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>3SG</td>
        <td><g:display item="${conjugate.conjugate("SG3", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("SG3", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1DLINCL</td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1INCL", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1DLEXCL</td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL1EXCL", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1PLINCL</td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1INCL", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>1PLEXCL</td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL1EXCL", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>2DL</td>
        <td><g:display item="${conjugate.conjugate("DL2", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("DL2", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>2PL</td>
        <td><g:display item="${conjugate.conjugate("PL2", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL2", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
    <tr>
        <td>3PL</td>
        <td><g:display item="${conjugate.conjugate("PL3", "SG1", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "SG2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "SG3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "SG3IN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "DL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "DL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "PL1INCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "PL1EXCL", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "DL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "PL2", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "PL3AN", stemmer, verbTense, partofspeechc)}"/></td>
        <td><g:display item="${conjugate.conjugate("PL3", "PL3IN", stemmer, verbTense, partofspeechc)}"/></td>
    </tr>
</table>
<%}%>
        </td>
    </tr>
</table><br/>
* disclaimer - conjugations may be incorrect - they are for potential reference only<br/>
<br/>
<a href="#" id="showtranslit">Show/Hide Transliteration</a><br/><br/>
<g:if test="${vtense == Tense.PRESENT}">
    <div id="presentNote"><g:render template="/newSearch/presentNote"/></div>
</g:if>

<g:if test="${vtense == Tense.REMOTE_PAST}">
<div id="remotePastNote"><g:render template="/newSearch/remotePastNote"/></div>
</g:if>

<g:if test="${vtense == Tense.HABITUAL}">
    <div id="habitualNote"><g:render template="/newSearch/habitualNote"/></div>
</g:if>

<g:if test="${vtense == Tense.FUTURE_COMMAND}">
<div id="futureCommandNote"><g:render template="/newSearch/futureCommandNote"/></div>
</g:if>

<g:if test="${vtense == Tense.INFINITIVE}">
<div id="infinitiveNote"><g:render template="/newSearch/infinitiveNote"/></div>
</g:if>

</body>
</html>
