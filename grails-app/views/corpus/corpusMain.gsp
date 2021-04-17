<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Corpus Main</title>
    <script>
        $(function () {
            $('#view').click(function () {
                var corpusSource = $('#cSource').val();
                if (!corpusSource) {
                    //message for the user to make a selection from the dropdown
                }
                $.ajax({
                    method: "POST",
                    url: "/corpus/viewByCorpusSource",
                    data: {corpusSource: corpusSource},
                    success: function (data) {
                        window.location = data;
                        // $("#sourceChanged").html(data);
                    }
                })
            });
        });
    </script>
</head>
<body>
Welcome to the Cherokee Corpus Main Section. This section is not a part of the CED project.  The Cherokee Corpus is a collection of Cherokee sentences with matching translations that have been entered into a database.
These sentence pairings can then be exported into a file that is then used for training neural networks for many tasks including machine translation.
<br/>

%{--
Todo:<br/>
View by CorpusSource - paginated by user selection (10, 25, 50, 100)<br/>
Add ability for user to select delimiter and character to surround text with (currently double quotes for comma delimited and nothing for tab delimited) there are issues with double quotes since double quotes are in some sentences<br/>
Add some sort of processing gif instead of relying on the tab<br/>
Deal with cancelled downloads more gracefully<br/>
DONE -- Add edit to view page for logged in users (this is also annotated on the view page<br/>
Add a way to enter a single entry<br/>
Add more instructions<br/>
Add support for tab delimited file uploads<br/>
Add support for structured csv file uploads where the user tells what delimiters they used<br/>
Download only a select Corpus section in CSV, Tab Delimited, or Excel<br/>
on view only page for longer lined xls add dropdown to select a corpus instead of only typing in an entry<br/>
Add searches however we need or want for just the corpus (regex, etc)<br/>
we can even hook any of these to the CED searches - say someone searches for a word we would now have an additional amount of example sentences to display.<br/>
--}%

<br/>
<g:select name="cSource"
          from="${cherokee.corpus.CorpusSource.list()}"
          value="cSource"
          optionKey="id"
          noSelection="['':'-Select a Corpus -']"/>
<button id="view" name="view">View by Corpus Source</button>

<br/>
<br/>
<% if (session.getAttribute("loggedin")) { %>
<a href="/corpus/corpusUpload">Upload Excel Spreadsheet</a><br/>
<br/><br/>
<a href="/corpus/adminDownload/csv">Download latest CSV</a><br/>
<a href="/corpus/adminDownload/tab">Download latest Tab Delimited</a><br/>
<a href="/corpus/adminDownload/excel">Download latest Excel Spreadsheet</a><br/>
<% } else {%>
<a href="/corpus/download/csv">Download latest CSV</a><br/>
<a href="/corpus/download/tab">Download latest Tab Delimited</a><br/>
<a href="/corpus/download/excel">Download latest Excel Spreadsheet</a><br/>
<% } %>

</body>
</html>