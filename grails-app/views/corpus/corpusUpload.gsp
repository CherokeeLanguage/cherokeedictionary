<html>
<head>
    <meta name="layout" content="manager"/>
    <title>NLP Corpus</title>
</head>
<body>
Once you click upload do not stop the upload until you see the next page. If your Excel spreadsheet contains a lot of rows and a slow upload connection this import process may take a while (more than 5 minutes) to complete.<br/>
<g:uploadForm action="upload" controller="NLPUpload">
    <fieldset class="form">
        <input type="file" name="file" />
    </fieldset>
    <fieldset class="buttons">
        <g:submitButton name="upload" class="save" value="Upload" />
    </fieldset>
</g:uploadForm>
</body>
</html>