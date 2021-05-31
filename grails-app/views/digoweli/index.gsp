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
    def greetOthersChapterTitle = 'Greet Others'
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
%>

<pre>
    <g:each var="title" in="${tableOfContents}">
        <a href="#${title.replaceAll(" ", "")}">${title}</a>
    </g:each>
</pre>
<g:render template="Greetings" model="['title': greetOthersChapterTitle]"/>

<g:wordBreakdown title="${g.translit(src:"dohi")} and ${g.translit(src:"osi")} Tohi and Osi" anchor="wordBreakdownTohiOsi">
    Altman and Belt (pp91-92) have this to say about Tohi and Osi:
    Tohi is a Cherokee morpheme that indicates the state in which nature is flowing at its appropriate pace and everything
    is as it should be. This fundamental concept is used in greetings and responses (<g:redSpan>Tohigwatsv?</g:redSpan> and <g:redSpan>Tohigwu.</g:redSpan>), and in a
    variety of other instances and constructions that indicate an underlying concern with the notion that things be flowing
    well in the Cherokee world. Tohi can be glossed variously as "well," "peaceful," "unhurried," and "health." In the Cherokee
    speakers' view, if the state of tohi becomes disrupted there can be disastrous consequences, and communities that are disrupted
    in this way can be dangerous or unhealthy places to live.
    <br/><br/>
    In addition to and as an adjunct to tohi, the concept of osi describes the proper state of the individual person.
    Visualized as upright, facing forward, and resting on a single point of balance, osi is also used in greetings and
    replies (<g:redSpan>osigwatsv?</g:redSpan> and <g:redSpan>osigwu.</g:redSpan>), and in other contexts that indicate that the notion of an individual’s state of
    being is crucial in ensuring that all is flowing well in the larger Cherokee world. Osi is properly understood as
    referring to the state of neutrality and balance, but it is most often glossed as "good." If individuals are out of
    balance, they can cause problems in the larger system.
    <g:citation src="Altman, H.M., & Belt, T.N. (2008). Reading History: Cherokee History through a Cherokee Lens. Native South 1, 90-98. http://doi.org/10.1353/nso.0.0003"/>
</g:wordBreakdown>
<br/>
<br/>
<br/>
<br/>
<div>
    <span><b>Bibliography</b></span>
</div>
<br/>
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