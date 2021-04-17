<html>
<head>
    <meta name="layout" content="manager"/>
    <title>NLP Corpus</title>
    <script>
        $(function () {
            $('#changeSource').click(function () {
                var cSource = $( "#cSource option:selected" ).val();
                $.ajax({
                    method: "POST",
                    url: "/NLPUpload/changeCorpusName",
                    data: {corpusSource: $('#corpusSource').val(), cSource: cSource, from: 'true'},
                    success: function (data) {
                        $("#sourceChanged").html(data);
                    }
                })
            });
        });
    </script>
</head>
<body>
Processing is Complete<br/>
<br/>
<div id="sourceChanged"></div>
<g:if test="${source}">
    The source file you submitted was too long for standard verification and was submitted directly to the database.<br/>
    This is the temporary name given to the source you've submitted. You may change the source name below and submit it.<br/>
    Edits to this particular data set will have to be done in chunks of 100 records each<br/>
    <br/>
    Since there is a temp source give the person a way to change the name of their temp source<br/>
    <g:select name="cSource"
              from="${cherokee.corpus.CorpusSource.list()}"
              value="cSource"
              optionKey="id"
              noSelection="['':'-Select a Corpus -']"/>
    <input type="text" id="corpusSource" name="corpusSource" value="${source}"/><input type="button" id="changeSource" name="changeSource" value="Change Source"/><br/>
    LINK HERE TO ALLOW THEM TO START EDITING<br/>
    <g:each var="value" in="${lst}">
        <div style="display:table-row">
            <div style="width:auto;display:table-cell">${value.value[0]}</div><div style="width:auto;display:table-cell">${value.value[1]}</div>
        </div>
    </g:each>
</g:if>
<g:if test="${!source}">
    <g:each var="value" in="${lst}">
        <div style="display:table-row">
            <div style="width:auto;display:table-cell">${value.syllabary}</div><div style="width:auto;display:table-cell">${value.english}</div>
        </div>
    </g:each>
</g:if>
</body>
</html>