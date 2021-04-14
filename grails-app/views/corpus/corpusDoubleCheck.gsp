﻿
<html>
<head>
    <meta name="layout" content="manager"/>
    <title>NLP Corpus</title>
</head>
<body>
%{--<%=  cherokee.corpus.CorpusSource.list().each {
    println it + "<br/> "
}%><br/>--}%
<g:form action="correct" controller="NLPUpload" method="POST">
    <g:select name="cSource"
              from="${cherokee.corpus.CorpusSource.list()}"
              value="cSource"
              optionKey="id"
              noSelection="['':'-Select a Corpus -']"/>
    <g:textField name="corpusS"/><br/>
    <g:each status="i" var="value" in="${lst}">
        <textarea id="textAreaChr${i}" name="textAreaChr${i}" cols="25" rows="5">${value.value[0]}</textarea><textarea id="textAreaEng${i}" name="textAreaEng${i}" cols="25" rows="5">${value.value[1]}</textarea><br/>
    </g:each>
    <g:submitButton name="upload" class="save" value="Upload" />
</g:form>
</body>
</html>