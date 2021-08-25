<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 8/16/2015
  Time: 3:47 PM
--%>

<%@ page import="net.cherokeedictionary.dictionary.Likespreadsheets;  net.cherokeedictionary.dictionary.Likespreadsheets; java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.util.Tense; net.cherokeedictionary.stemmer.Stemmer; net.cherokeedictionary.stemmer.DefinitionLine" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.verb.conjugation.Conjugate;net.cherokeedictionary.factory.VerbFactory;net.cherokeedictionary.core.Verb" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.util.PrefixTableSubject; net.cherokeedictionary.util.PrefixTableObject;" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.util.CompoundPrefixes;net.cherokeedictionary.verb.conjugation.VerbConjugationProcessor" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Conjugation Table</title>
    <script type="text/javascript">
        $(function() {
            $('#verbTense').change(function() {
              // var selectedValue = $('#verbTense').find(":selected").val();
              document.forms['verbTenses'].submit();
            });

            $('#generate').click(function() {
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

    String verbTense = params.verbTense ?: "PRESENT"
    Tense vtense = Tense.valueOf(verbTense)
    boolean intransitive = false

    if (lks.partofspeechc == 'vi') {
        intransitive = true
    }

    def entry = lks

    def conjugate = {subject, object ->
        def paramMap = [:]
        paramMap.habitual = lks.vthirdpressylll
        paramMap.imperative = lks.vsecondimpersylln
        paramMap.infinitive = lks.vthirdinfsyllp
        paramMap.present1st = lks.vfirstpresh
        paramMap.present3rd = lks.syllabaryb
        paramMap.remotepast = lks.vthirdpastsyllj
        paramMap.partofspeechc = lks.partofspeechc
        paramMap.verbTense = verbTense
        paramMap.subject = subject
        paramMap.object = object
        paramMap.yi = request.getParameter("yi") == 'on'
        paramMap.ji = request.getParameter("ji") == 'on'
        paramMap.wi = request.getParameter("wi") == 'on'
        paramMap.ni = request.getParameter("ni") == 'on'
        paramMap.de = request.getParameter("de") == 'on'
        paramMap.da = request.getParameter("da") == 'on'
        paramMap.di = request.getParameter("di") == 'on'
        paramMap.i = request.getParameter("i") == 'on'
        paramMap.ga = request.getParameter("ga") == 'on'
        paramMap.e = request.getParameter("e") == 'on'

//        out << paramMap
        //todo: make these sticky for the selection every page
        Verb verb = VerbFactory.createVerbFromParameters(paramMap)
        verb = Conjugate.conjugate(verb)

        return verb
    }
%>
<body>
<g:render template="stemmerDebug" model="[stemmer: stemmer]"/>
<g:render template="simplifiedOriginalEntry" model="[entry:entry]"/>
<br/>
<form action="/newSearch/conjtabledisplay" name="verbTenses">
    <input type="hidden" name="id" value="${entry.id}"/>
    <g:render template="initialPrefixes" mode="[vtense: vtense]"/>
    <g:render template="verbTenseSelector" model="[vtense: vtense]"/>
    <br/>
    <input type="submit" id="generate" value="Generate">
</form>
<table>
    <tr>
        <td>
            <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
                SUBJECT
            </div>
        </td>
        <td>
            <% if (intransitive) { %>
                <g:render template="intransitive" model="[conjugate:conjugate]"/>
            <% } else { %>
                <g:render template="transitive" model="[conjugate:conjugate]"/>
            <%}%>
        </td>
    </tr>
</table><br/>
* disclaimer - conjugations may be incorrect - they are for potential reference only<br/>
<br/>
<a href="#" id="showtranslit">Show/Hide Transliteration</a><br/><br/>
<g:render template="tenseDisplay" model="[vtense:vtense]"/>

</body>
</html>
