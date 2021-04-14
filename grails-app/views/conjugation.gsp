<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 8/16/2015
  Time: 3:47 PM
--%>

%{--<%@ page import="cherokee.dictionary.Likespreadsheets;cherokee.dictionary.verb.conjugation.originalConjugation.Tense;cherokee.dictionary.hold.stem.DefinitionLine;cherokee.dictionary.verb.conjugation.originalConjugation.Stemmer;cherokee.dictionary.verb.conjugation.originalConjugation.Conjugate" contentType="text/html;charset=UTF-8" %>--}%
<%@ page import="cherokee.dictionary.Likespreadsheets; java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.util.Tense; net.cherokeedictionary.stemmer.Stemmer; net.cherokeedictionary.stemmer.DefinitionLine" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.verb.conjugation.Conjugate" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <script src="https://code.jquery.com/jquery-latest.min.js"></script>
    <style>
    table, th, td {
        border: 1px solid black;
    }
    </style>
    <script type="text/javascript">
        $(function() {
            $('#showtranslit').click(function() {
                alert("here");
            });
        });
    </script>
</head>
<%
    def lks = Likespreadsheets.findById(72348);

    def habitual = lks.vthirdpressylll
    def imperative = lks.vsecondimpersylln
    def infinitive = lks.vthirdinfsyllp
    def present1st = lks.vfirstpresh
    def present3rd = lks.syllabaryb
    def remotepast = lks.vthirdpastsyllj
    def partofspeechc = lks.partofspeechc

    def stemmer = new Stemmer()
    stemmer.habitual = new DefinitionLine(syllabary: habitual)
    stemmer.imperative = new DefinitionLine(syllabary: imperative)
    stemmer.infinitive = new DefinitionLine(syllabary: infinitive)
    stemmer.present1st = new DefinitionLine(syllabary:  present1st)
    stemmer.present3rd = new DefinitionLine(syllabary: present3rd)
    stemmer.remotepast = new DefinitionLine(syllabary: remotepast)

    String verbTense = "PRESENT";
    Conjugate conjugate = new Conjugate()
%>
<body>
<a href="#" id="showtranslit">Show/Hide Transliteration</a>
<table>
    <tr>
        <td></td>
        <td>SG1</td>
        <td>SG2</td>
        <td>SG3AN</td>
        <td>SG3IN</td>
        <td>DL1INCL</td>
        <td>DL1EXCL</td>
        <td>PL1INCL</td>
        <td>PL1EXCL</td>
        <td>DL2</td>
        <td>PL2</td>
        <td>PL3AN</td>
        <td>PL3IN</td> 
    </tr>
    <tr>
        <td>SG1</td>
        <td>${conjugate.conjugate("SG1", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG1", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>SG2</td>
        <td>${conjugate.conjugate("SG2", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG2", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>SG3</td>
        <td>${conjugate.conjugate("SG3", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("SG3", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>DL1INCL</td>
        <td>${conjugate.conjugate("DL1INCL", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1INCL", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>DL1EXCL</td>
        <td>${conjugate.conjugate("DL1EXCL", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL1EXCL", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>PL1INCL</td>
        <td>${conjugate.conjugate("PL1INCL", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1INCL", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>PL1EXCL</td>
        <td>${conjugate.conjugate("PL1EXCL", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL1EXCL", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>DL2</td>
        <td>${conjugate.conjugate("DL2", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("DL2", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>PL2</td>
        <td>${conjugate.conjugate("PL2", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL2", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
    <tr>
        <td>PL3</td>
        <td>${conjugate.conjugate("PL3", "SG1", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "SG2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "SG3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "SG3IN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "DL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "DL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "PL1INCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "PL1EXCL", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "DL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "PL2", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "PL3AN", stemmer, verbTense, partofspeechc)}</td>
        <td>${conjugate.conjugate("PL3", "PL3IN", stemmer, verbTense, partofspeechc)}</td>
    </tr>
</table>

</body>
</html>