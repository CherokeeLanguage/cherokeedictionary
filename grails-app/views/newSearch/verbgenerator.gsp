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
<%@ page import="net.cherokeedictionary.verb.conjugation.Conjugate;net.cherokeedictionary.factory.VerbFactory;net.cherokeedictionary.core.Verb" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.util.PrefixTableSubject; net.cherokeedictionary.util.PrefixTableObject;" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.util.CompoundPrefixes;net.cherokeedictionary.verb.conjugation.VerbConjugationProcessor" contentType="text/html;charset=UTF-8" %>

%{--V1--}%
%{--<%@ page import="cherokee.dictionary.conjugation.cdpbook.Stemmer; cherokee.dictionary.conjugation.stem.DefinitionLine;cherokee.dictionary.conjugation.wordFormation.Word;cherokee.dictionary.conjugation.Conjugate" contentType="text/html;charset=UTF-8"%>--}%
%{--<%@ page import="cherokee.dictionary.conjugation.Conjugate.Tense; cherokee.dictionary.conjugation.Conjugate.PartOfSpeech;cherokee.dictionary.conjugation.Conjugate" contentType="text/html;charset=UTF-8"%>--}%
<html>
<head>
  <meta name="layout" content="manager"/>
  <title>Conjugation Table</title>
  <script src="https://code.jquery.com/jquery-latest.min.js"></script>
  <script type="text/javascript">
    $(function() {
      // $('#verbTense').change(function() {
      //   var selectedValue = $('#verbTense').find(":selected").val();
      //   document.forms['verbTenses'].submit();
      // });

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

<g:render template="/newSearch/simplifiedOriginalEntry" model="[entry:entry]"/>
<br/>
<form action="/newSearch/verbgenerator" name="verbTenses">
  <input type="hidden" name="id" value="${entry.id}"/>
    <g:include view="newSearch/_initialPrefixes.gsp"/>
    <g:include view="newSearch/_verbTenseSelector.gsp"/>
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
        <table>
            <tr>
                <td>1SG</td>
                <td><g:displayGenerated item="${conjugate("SG1", "")}"/></td>
            </tr>
            <tr>
                <td>2SG</td>
                <td><g:displayGenerated item="${conjugate("SG2", "")}"/></td>
            </tr>
            <tr>
                <td>3SG</td>
                <td><g:displayGenerated item="${conjugate("SG3", "")}"/></td>
            </tr>
            <tr>
                <td>1DLINCL</td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "")}"/></td>
            </tr>
            <tr>
                <td>1DLEXCL</td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "")}"/></td>
            </tr>
            <tr>
                <td>1PLINCL</td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "")}"/></td>
            </tr>
            <tr>
                <td>1PLEXCL</td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "")}"/></td>
            </tr>
            <tr>
                <td>2DL</td>
                <td><g:displayGenerated item="${conjugate("DL2", "")}"/></td>
            </tr>
            <tr>
                <td>2PL</td>
                <td><g:displayGenerated item="${conjugate("PL2", "")}"/></td>
            </tr>
            <tr>
                <td>3PL</td>
                <td><g:displayGenerated item="${conjugate("PL3", "")}"/></td>
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
                <td><g:displayGenerated item="${conjugate("SG1", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG1", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>2SG</td>
                <td><g:displayGenerated item="${conjugate("SG2", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG2", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>3SG</td>
                <td><g:displayGenerated item="${conjugate("SG3", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("SG3", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>1DLINCL</td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1INCL", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>1DLEXCL</td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL1EXCL", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>1PLINCL</td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1INCL", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>1PLEXCL</td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL1EXCL", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>2DL</td>
                <td><g:displayGenerated item="${conjugate("DL2", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("DL2", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>2PL</td>
                <td><g:displayGenerated item="${conjugate("PL2", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL2", "PL3IN")}"/></td>
            </tr>
            <tr>
                <td>3PL</td>
                <td><g:displayGenerated item="${conjugate("PL3", "SG1")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "SG2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "SG3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "SG3IN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "DL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "DL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "PL1INCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "PL1EXCL")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "DL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "PL2")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "PL3AN")}"/></td>
                <td><g:displayGenerated item="${conjugate("PL3", "PL3IN")}"/></td>
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
