<!DOCTYPE html>
<html lang="en">
    <head>
        <meta name="layout" content="manager"/>
        <meta charset="UTF-8">
        <title>Barnes Form Test</title>
        <script>

        </script>
    </head>
    <body>
        <g:form controller="WT" action="barnes" id="barnes" name="barnes">
            Definition: <input type="text" id="definition" name="definition"/>
            <table id="formentries">
                <tr><td>Syllabary</td><td>Phonetic</td><td>Tone</td><td>Classifier</td><td>Audio File Path</td></tr>
            </table>
            <input type="hidden" value="0" name="numberOfEntries" id="numberOfEntries"/>
            <g:submitButton id="submitEntryButton" name="submitEntryButton" value="Submit Entry"/>
        </g:form><br/>
        <a onclick="addNewRow()">Add New Row</a><br/><br/>
        ${definition}<br/>
        <g:each in="${wordList}" var="word">
            ${word.syllabary} - ${word.tsalagi} - ${word.tone} <br/>
        </g:each>
        <script>
            var count = 0;
            function addNewRow() {
                $('#formentries').append("<tr><td><input type=\"text\" id=\"syllabary" + count + "\" name=\"syllabary" + count + "\"/></td><td><input type=\"text\" id=\"phonetic" + count + "\" name=\"phonetic" + count + "\"/></td><td><input type=\"text\" id=\"tone" + count + "\" name=\"tone" + count + "\"/></td><td><input type=\"text\" id=\"classifier" + count + "\" name=\"classifier" + count + "\"/></td><td><input type=\"text\" id=\"audioFilePath" + count + "\" name=\"audioFilePath" + count + "\"/></td></tr>");

                $('#classifier'+ count).autocomplete({
                    source: '<g:createLink controller='WT' action='wordType'/>'
                });

                $('#numberOfEntries').attr('value', '' + (count + 1));
                count++;
            }

            addNewRow();
        </script>
    </body>
</html>
