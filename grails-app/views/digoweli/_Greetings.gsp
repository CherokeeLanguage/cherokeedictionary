<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil"%>
<%
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
        greetingsVocabulary.And = ['ale', '<e>or', 'nole']
        greetingsVocabulary.Good = 'osda'
        greetingsVocabulary."Well/Fine" = 'osigwu'
%>

<g:bookChapter anchor="${title}" title="Greetings" titletranslit="unalihelijeti">
%{--    <g:whatYouWillLearn objectives="${greetOthersTopics}"/>--}%
    <g:dialogVocab vocab="${greetingsVocabulary}">
        <g:dialogLine name="Danili" dialog="Osiyo.  Danili dawadoa.  Gado dejadoa?" phonetic="true"/>
        <g:dialogLine name="Susani" dialog="Osiyo.  Susani dawadoa.  Dohiju?" phonetic="true"/>
        <g:dialogLine name="Danili" dialog="Osigwu.  Nihinahv?" phonetic="true"/>
        <g:dialogLine name="Susani" dialog="Osda.  Donadagohvi." phonetic="true"/>
        <g:dialogLine name="Danili" dialog="Donadagohvi." phonetic="true"/>
    </g:dialogVocab>
%{--    <g:clearCitations/>--}%

%{--    <g:bookSection title="Hello" phonetic="osiyo">--}%
%{--        In Cherokee there is only one way to say 'Good Morning,' 'Good Afternoon,' 'Good Evening,' and Hello; that is by saying <g:transl src="osiyo"/> <g:redSpan>(o)siyo</g:redSpan> or the shortened version <g:transl src="siyo"/> <g:redSpan>siyo</g:redSpan>.--}%
%{--        <br/>--}%
%{--        <g:footnote src="We will follow the convention of placing optional syllables in parenthesis.  You will see this written as (O)siyo.  The parentheses around the 'O' mean that the voicing of the 'O' is optional."/><br/>--}%
%{--        <g:footnote src="Osi will be discussed more in the section " linkTitle="Word Breakdown - Tohi and Osi" link="wordBreakdownTohiOsi"/>--}%
%{--        <br/>--}%
%{--        <g:exercise text="1. Hello, Mary 2. Hello, Mark 3. Hello, Daniel"/>--}%
%{--    </g:bookSection>--}%

%{--    <br/>--}%
%{--    <g:bookSection title="Goodbye" phonetic="donadagohvi">--}%
%{--        There is no word for 'goodbye' only 'to meet again'. The way to say 'goodbye' to one person is <g:transl src="donadagohvi"/>--}%
%{--        <g:redSpan>donadagohvi</g:redSpan>. If you would like to say 'goodbye' to more than one person you would say--}%
%{--        <g:transl src="dodadagohvi"/> <g:redSpan>dodadagohvi</g:redSpan>.  Lit: Let's meet again.<br/>--}%
%{--        <g:footnote src="We will discuss the plurality prefixes (d-) in the section " linkTitle="Word Breakdown - Plurality Prefixes" link="wordBreakdownPluralityPrefixes"/>--}%
%{--        <br/>--}%
%{--        <g:exercise text="1. Goodbye, Mary and John 2. Goodbye, Titus 3. Goodbye, Daniel 4. Goodbye, Mary, John, Susan, and Mark"/>--}%
%{--    </g:bookSection>--}%
</g:bookChapter>