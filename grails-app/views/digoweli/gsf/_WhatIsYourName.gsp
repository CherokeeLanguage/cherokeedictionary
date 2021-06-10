<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil"%>
<%
    def whatIsYourNameTopics = ['Tell time']
%>

<g:bookChapter anchor="${title}" title="${title}" titletranslit="Gado Dejadoa?">
    <g:whatYouWillLearn objectives="${whatIsYourNameTopics}"/>
    <g:dialogVocab vocab="${vocabulary}">
        <g:dialogLine name="" dialog="" engName="Mary" engDialog="Hello.  How are you?  My name is Mary.  What is your name?" phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Daniel" engDialog="I am fine.  My name is Daniel.  This is my friend.  His name is John." phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Mary" engDialog="Hello.  My name is Mary.  What is your name?" phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Daniel" engDialog="Hi.  My name is daniel.  How are you?" phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Mary" engDialog="I am fine.  And you?" phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Daniel" engDialog="I am fine too.  Is everything ok with you? (Is everything fine?)" phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Mary" engDialog="Yes everything is fine.  I am happy that I have seen you." phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Daniel" engDialog="I am happy to have seen you too." phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Mary" engDialog="Who is that?" phonetic="true"/>
        <g:dialogLine name="" dialog="" engName="Daniel" engDialog="That is John.  His name is John." phonetic="true"/>
%{--        <g:dialogLine name="" dialog="" engName="" engDialog="" phonetic="true"/>--}%
    </g:dialogVocab>

<pre>
Meeting people pp 2-3 (name, to want)

Simple questions pp4 (tsu, sgo, sg, s) and pp 74 Smith

turn these statements into questions
turh these questions into statements
ask for xyz
do you want xyz
tell your friend you would like an apple
Identify other people you see that you are not currently talking to.

More on this in Chapter 4

Is your name bob?
Is his name Barry?
</pre>
%{--    <g:render template="gsf/hello" model="[showExercise:true]"/>--}%
%{--    <br/>--}%
%{--    <g:render template="gsf/goodbye" model="[showExercise:true]"/>--}%
</g:bookChapter>