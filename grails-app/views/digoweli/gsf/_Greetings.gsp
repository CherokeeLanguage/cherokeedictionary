<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil"%>
<%
    def greetOthersTopics = ['How to greet people',
                             'How to make short descriptive phrases',
                             'About definites and indefinites',
                             'Say "hello" and "goodbye"',
                             'Ask how someone is doing']
%>

<g:bookChapter anchor="${title}" title="Greetings" titletranslit="unalihelijeti">
    <g:whatYouWillLearn objectives="${greetOthersTopics}"/>
    <g:dialogVocab vocab="${vocabulary}">
        <g:dialogLine name="Danili" dialog="Osiyo.  Danili dawadoa.  Gado dejadoa?" phonetic="true"/>
        <g:dialogLine name="Susani" dialog="Osiyo.  Susani dawadoa.  Dohiju?" phonetic="true"/>
        <g:dialogLine name="Danili" dialog="Osigwu.  Nihinahv?" phonetic="true"/>
        <g:dialogLine name="Susani" dialog="Osda.  Donadagohvi." phonetic="true"/>
        <g:dialogLine name="Danili" dialog="Donadagohvi." phonetic="true"/>
    </g:dialogVocab>

    <g:render template="gsf/hello" model="[showExercise:true]"/>
    <br/>
    <g:render template="gsf/goodbye" model="[showExercise:true]"/>
</g:bookChapter>