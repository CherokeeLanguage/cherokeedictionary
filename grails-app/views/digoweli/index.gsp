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
<h3><g:translit src="tsalagi gawonihisd digoweli"/><br/>Cherokee Language Book </h3>
%{--<g:render template="old/2WhatDoYouWantToDo/Chapter"/>--}%
<%
    def toc = {tableOfContents ->%>
        <pre>
        <g:each var="title" in="${tableOfContents}">
<a href="#${title.replaceAll(" ", "")}">${title}</a>
        </g:each>
        </pre>
    <%}

    def whatYou = {objectives ->%>
        <b>What You Will Learn In This Chapter</b><br/>
        <ul>
            <g:each var="objective" in="${objectives}">
                <li>${objective}</li>
            </g:each>
        </ul>
    <%}

    def bibliography = { %>
        <br/>
        <br/>
        <br/>
        <div>
            <span><b>Bibliography</b></span>
        </div>
        <br/>
        <g:printCitations/>
    <%}

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
    def greetOthersChapterTitle = 'Greet Others'

    def greetOthersTopics = ['How to greet people',
                             'How to make short descriptive phrases',
                             'About definites and indefinites',
                             'Say "hello" and "goodbye"',
                             'Ask how someone is doing']

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
    greetingsVocabulary.And = 'ale <e>or nole'
    greetingsVocabulary.Good = 'osda'
    greetingsVocabulary."Well/Fine" = 'osigwu'
%>
<%= toc(tableOfContents) %>
<%= whatYou(greetOthersTopics) %>
<%= bibliography() %>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>
<br/><br/>

</body>
</html>