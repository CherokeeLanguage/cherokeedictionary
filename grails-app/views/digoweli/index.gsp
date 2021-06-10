<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 5/29/21
  Time: 5:52 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="cherokee.dictionary.taglibUtil.NumbersSection; cherokee.dictionary.taglibUtil.GreetingsSection;cherokee.dictionary.taglibUtil.ColorsSection; cherokee.dictionary.taglibUtil.WhatIsYourNameSection" %>
<%@ page import="cherokee.dictionary.taglibUtil.DatesSection;" %>
<html>
<head>
    <title>Cherokee Language Book - <g:translit src="tsalagi gawonihisd digoweli"/></title>
</head>
<body>
%{--<g:render template="hold/old/1Greetings/Chapter"/>--}%
%{--<g:render template="hold/old/2WhatDoYouWantToDo/Chapter"/>--}%
<%
def isPrintVersion = request.getParameter("print") && request.getParameter("print") == "true"
def greetingsSection = new GreetingsSection()
def whatIsYourNameSection = new WhatIsYourNameSection()
def numbersSection = new NumbersSection()
def colorsSection = new ColorsSection()
def datesSection = new DatesSection()

def tableOfContents = ['Dedication'
                       , 'From The Author'
                       , 'Greetings'
                       , 'Do you speak Cherokee?'
                       , '\t' + greetingsSection.linkTitle
                       ,  whatIsYourNameSection.linkTitle
                       , 'Where are you from?'
                       , 'I\'d like you to meet'
                       , 'Do you understand?'
                       , ''
                       , 'Numbers and Money'
                       , 'Cardinal Numbers'
                       , 'Ordinal Numbers'
                       , 'Money'
                       , ''
                       , 'On the Telephone'
                       , ''
                       , 'Address and Email'
                       , ''
                       , 'Dates, Months, and Days of Week'
                       , 'Time, Counting, Hours, Minutes, Seconds, Fractions'
                       , ''
                       , 'Special Occasions'
                       , ''
                       , 'Describing Things'
                       , 'Colors'
                       , 'Clothes and Shopping'
                       , 'Describing Others'
                       , 'Shapes'
                       , ''
                       , 'Food, eating, drinking'
                       , 'Breakfast, Coffee, Lunch, Dinner, Dating'
                       , 'are you hungry'
                       , ''
                       , 'Directions, where is something, directions around town'
                       , 'Weather, Seasons'
                       , ''
                       , 'Profession'
                       , 'Body Parts & Functions'
                       , '5 senses'
                       , 'Emotions'
                       , ''
                       , 'Seasons'
                       , 'This and That'
                       , 'Family'
                       , ''
                       , 'This & That'
                       , 'Posessive'
                       , 'Animals'
                       , ''
                       , 'On the farm'
                       , 'Visiting Friends'
                       , 'Verb Conjugation'
                       , 'Yours, Mine, Ours'
                       , 'At the doctor'
                       , 'Around the House'
                       , 'At the beach'
                       , 'Transporation'
                       , 'Festivals and Celebrations'
                       , 'At the airport'
                       , ''
                       , 'I want.  I see'
                       , 'Questions'
                       , 'Pronouns'
                       , 'Singular/Plural'
                       , 'Derived Nouns'
                       , 'Articles and Conversion'
                       , 'prepositions'
                       , 'conjunctions'
                       , 'interjections'
                       , 'negation'
                       , 'To Have and Have Not'
                       , 'Advanced Verb'
                       , 'Clitics'
                       , 'Grammar Summary'
                       , 'Charts'
                       , 'Publication bibliography']

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

%{--<g:render template="gsf/sections/titleTOC" model="[tableOfContents: tableOfContents]"/>--}%
<pre>
Dedication
From The Author
</pre>
<pre>
Greetings
Do you speak Cherokee?
    ${greetingsSection.linkTitle}
${whatIsYourNameSection.linkTitle}
Where are you from?
I\d like you to meet
Do you understand?
</pre>
<g:render template="gsf/chapters/Greetings" model="[baseSection: greetingsSection, isPrintVersion:isPrintVersion]"/>
<g:render template="gsf/chapters/WhatIsYourName" model="[baseSection: whatIsYourNameSection, isPrintVersion:isPrintVersion]"/>
<pre>
Numbers and Money
Cardinal Numbers
Ordinal Numbers
Money
</pre>
<g:render template="gsf/chapters/Numbers" model="[baseSection: numbersSection, isPrintVersion:isPrintVersion]"/>
<pre>
On the Telephone

Address and Email

Dates, Months, and Days of Week
</pre>
<g:render template="gsf/chapters/Dates" model="[baseSection: datesSection, isPrintVersion:isPrintVersion]"/>
<pre>
Time, Counting, Hours, Minutes, Seconds, Fractions

Special Occasions

Describing Things</pre>
<g:render template="gsf/chapters/Colors" model="[baseSection: colorsSection, isPrintVersion:isPrintVersion]"/>
<pre>Clothes and Shopping
Describing Others
Shapes

Food, eating, drinking
Breakfast, Coffee, Lunch, Dinner, Dating
are you hungry

Directions, where is something, directions around town
Weather, Seasons

Profession
Body Parts \& Functions
5 senses
Emotions

Seasons
This and That
Family

This \& That
Possessive
Animals

On the farm
Visiting Friends
Verb Conjugation
Yours, Mine, Ours
At the doctor
Around the House
At the beach
Transportation
Festivals and Celebrations
At the airport

I want.  I see
Questions
Pronouns
Singular/Plural
Derived Nouns
Articles and Conversion
prepositions
conjunctions
interjections
negation
To Have and Have Not
Advanced Verb
Clitics
</pre>

Grammar Summary<br/>
<g:render template="gsf/sections/dialect"/>
<g:render template="gsf/sections/tohiDohiWordBreakdownQuote"/>
<g:render template="gsf/sections/daysOfWeekMeaning"/>
<br/><br/>
<br/>Charts<br/>

<g:reader/>
<g:answerKeyPrint/>

<g:if test="${!request.getParameter("print") || request.getParameter("print") == "false"}">
    <a name="bibliography"></a>
    <h2>Bibliography</h2>
</g:if>
<g:printCitations/>

%{--<g:vocabulary src="${yesNoVocabulary}"/>--}%
%{--<g:vocabulary src="${greetings2}"/>--}%
%{--<g:vocabulary src="${greetingsVocabulary << yesNoVocabulary << greetings2}"/>--}%
%{--<g:render template="gsf/items"/>--}%
</body>
</html>

%{--<g:render template="gsf/layout/footnote" model="[isPrintVersion:isPrintVersion, src:'Osi will be discussed more in the section ', linkTitle:'Word Breakdown - Tohi and Osi', link:'wordBreakdownTohiOsi']"/>--}%
%{--<g:footnote src="Osi will be discussed more in the section " linkTitle="Word Breakdown - Tohi and Osi" link="wordBreakdownTohiOsi"/>--}%
%{--<g:render template="gsf/layout/footnote" model='[isPrintVersion: false, src:'Osi will be discussed more in the section ', linkTitle:Word Breakdown - Tohi and Osi", link:"wordBreakdownTohiOsi"]"/>--}%