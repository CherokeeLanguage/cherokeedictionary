<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil; cherokee.dictionary.taglibUtil.DialogLine"%>
<%
    def greetOthersTopics = ['How to greet people',
                             'How to make short descriptive phrases',
                             'About definites and indefinites',
                             'Say "hello" and "goodbye"',
                             'Ask how someone is doing']

    def dialogs = []
    dialogs << new DialogLine(name:"Danili",dialog:"Osiyo.  Danili dawadoa.  Gado dejadoa?", engName:"Daniel", engDialog:"Hello.  My name is Daniel.  What is your name?")
    dialogs << new DialogLine(name:"Susani", dialog:"Osiyo.  Susani dawadoa.  Dohiju?", engName:"Susan", engDialog:"Hello.  My name is Susan.  How are you?")
    dialogs << new DialogLine(name:"Danili", dialog:"Osigwu.  Nihinahv?", engName:"Daniel", engDialog:"I'm fine.  And you?")
    dialogs << new DialogLine(name:"Susani", dialog:"Osda.  Donadagohvi.", engName:"Susan", engDialog:"Good.  Goodbye.")
    dialogs << new DialogLine(name:"Danili", dialog:"Donadagohvi.", engName:"Daniel", engDialog:"Goodbye.")
%>

<g:bookChapter anchor="${title}" title="${title}" titletranslit="unalihelijeti">
    <g:whatYouWillLearn objectives="${greetOthersTopics}"/>
    <%
        dialogs.each {
            out << it
        }
    %>
%{--    <g:dialogVocab vocab="${vocabulary}">--}%
%{--        <g:dialogLine name="Danili" dialog="Osiyo.  Danili dawadoa.  Gado dejadoa?" engName="Daniel" engDialog="Hello.  My name is Daniel.  What is your name?" phonetic="true"/>--}%
%{--        <g:dialogLine name="Susani" dialog="Osiyo.  Susani dawadoa.  Dohiju?" engName="Susan" engDialog="Hello.  My name is Susan.  How are you?" phonetic="true"/>--}%
%{--        <g:dialogLine name="Danili" dialog="Osigwu.  Nihinahv?" engName="Daniel" engDialog="I'm fine.  And you?" phonetic="true"/>--}%
%{--        <g:dialogLine name="Susani" dialog="Osda.  Donadagohvi." engName="Susan" engDialog="Good.  Goodbye." phonetic="true"/>--}%
%{--        <g:dialogLine name="Danili" dialog="Donadagohvi." engName="Daniel" engDialog="Goodbye." phonetic="true"/>--}%
%{--    </g:dialogVocab>--}%

    <g:render template="gsf/hello" model="[showExercise:true]"/>
    <br/>
    <g:render template="gsf/goodbye" model="[showExercise:true]"/>
</g:bookChapter>