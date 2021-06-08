<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 5/29/21
  Time: 5:52 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil; groovy.xml.MarkupBuilder"%>
<html>
<head>
    <title>Cherokee Language Book - <g:translit src="tsalagi gawonihisd digoweli"/></title>
</head>
<body>
%{--<g:render template="hold/old/1Greetings/Chapter"/>--}%
%{--<g:render template="hold/old/2WhatDoYouWantToDo/Chapter"/>--}%
<% def greetOthersChapterTitle = 'Greet Others'
def tableOfContents = ['Dedication'
                       , 'From The Author'
                       , 'Greetings'
                       , '\tWhat You Will Learn'
                       , '\t' + greetOthersChapterTitle
                       , 'What is your name?'
                       , 'I\'d like you to meet'
                       , 'I want.  I see'
                       , 'Where are you from?'
                       , 'Do you speak Cherokee?'
                       , 'Cardinal Numbers'
                       , 'Ordinal Numbers'
                       , 'On the Telephone'
                       , 'Address and Email'
                       , 'Profession'
                       , 'Dates, Months, and Days of Week'
                       , 'Time'
                       , 'Body Parts'
                       , 'Emotions'
                       , '5 senses'
                       , 'prepositions'
                       , 'conjunctions'
                       , 'interjections'
                       , 'negation'
                       , 'are you hungry'
                       , 'Seasons'
                       , 'This and That'
                       , 'Family'
                       , 'To Have and Have Not'
                       , 'Describing Others'
                       , 'Directions'
                       , 'Weather'
                       , 'Money'
                       , 'Clothes and Shopping'
                       , 'Animals'
                       , 'Colors'
                       , 'Food'
                       , 'On the farm'
                       , 'Visiting Friends'
                       , 'Verb Conjugation'
                       , 'Yours, Mine, Ours'
                       , 'Questions'
                       , 'Pronouns'
                       , 'At the doctor'
                       , 'Around the House'
                       , 'At the beach'
                       , 'Transporation'
                       , 'Festivals and Celebrations'
                       , 'At the airport'
                       , 'Articles and Conversion'
                       , 'advanced Verb'
                       , 'Clitics'
                       , 'Grammar Summary'
                       , 'Charts'
                       , 'Shapes'
                       , 'Publication bibliography']

//todo: what if these have audio files?
def greetingsVocabulary = [:]
greetingsVocabulary.Titus = 'Dadasi'
greetingsVocabulary.Timothy = 'Dimadi'
greetingsVocabulary.Mary = 'Meli'
greetingsVocabulary.Mark = 'Maga'
greetingsVocabulary.Daniel = 'Danili'
greetingsVocabulary.Susan = 'Susani'
greetingsVocabulary.John = 'Jani'
greetingsVocabulary.Hello = '(o)siyo'
greetingsVocabulary.And = ['ale', '<e>or', 'nole']
greetingsVocabulary.Good = 'osda'
greetingsVocabulary."Well/Fine" = 'osigwu'

def yesNoVocabulary = [:]
yesNoVocabulary.yes = "vv"
yesNoVocabulary.no = "tla, vtla, tlahv"
yesNoVocabulary.you_too = "nihisgwu"
yesNoVocabulary.good = "osda"
yesNoVocabulary.very_good = "osdadv"

def greetings2 = [:]
greetings2."How are you?" = "dohi(gwu)ju"
greetings2."I am fine." = "osigwu"
greetings2."And you?" = "nihina"
greetings2."Thank you." = "wado"
greetings2."Thank you very much." = "wadodv"
greetings2."Ok." = "howa"
%>
<g:clearCitations/>
<g:printVersion trueFalse="${request.getParameter("print")}"/>
<g:render template="gsf/titleTOC" model="[tableOfContents: tableOfContents]"/>

<g:render template="gsf/Greetings" model="['title': greetOthersChapterTitle, vocabulary:greetingsVocabulary]"/>

<g:render template="gsf/dialect"/>

%{--<g:vocabulary src="${yesNoVocabulary}"/>--}%
%{--<g:vocabulary src="${greetings2}"/>--}%
%{--<g:vocabulary src="${greetingsVocabulary << yesNoVocabulary << greetings2}"/>--}%

<g:render template="gsf/tohiDohiWordBreakdownQuote"/>

<g:reader/>
<g:answerKeyPrint/>
<g:if test="${!request.getParameter("print") || request.getParameter("print") == "false"}">
    <h2>Bibliography</h2>
</g:if>
<g:printCitations/>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>

</body>
</html>