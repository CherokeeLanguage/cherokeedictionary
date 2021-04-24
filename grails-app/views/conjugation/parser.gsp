<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 4/20/21
  Time: 12:04 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
    <html lang="en">
    <head>
%{--        <meta name="layout" content="manager"/>--}%
        <meta charset="UTF-8">
        <title>Verb Deconstructor</title>
        <script src="https://code.jquery.com/jquery-latest.min.js"></script>

        <asset:javascript src="cherokeeParser.js"/>
        <asset:javascript src="ajaxCall.js"/>
        <asset:javascript src="verbTables.js"/>
        <asset:javascript src="deconstructionMethods.js"/>
    </head>
<body>
<div style="display:table-row">
    <div style="display:table-cell;width:300px">
        <div style="display:table-row">%{--ᎦᏬᏂᏍᎨᏍᏗ--}%
            <div style="display:table-cell"><input type="text" id="texttoprocess" value="ᎠᎦᏍᎦᏛ"/><br/><button onclick='javascript:processText();'>Process</button><br/><br/></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell"><textarea cols="30" rows="6" type="text" id="textareatoprocess">ᎤᏍᏗ ᎠᏓᏪᎳᎩᏍᎬ ᎤᏃᏛ ᏧᎾᏦᎯᏍᏗ, ᏌᎪᏂᎨ ᏃᎴ ᏓᎶᏂᎨ ᏓᏓᏪᎳᎩᏍᎬ ᏩᏁ ᏃᎴ, ᏘᎵ ᏚᏅᏛ. ᎧᎵ ᎠᏂᏅ ᏗᎦᏅᎯᏓ ᏗᎦᏍᎩᎶᎩ, ᏗᏂᏲᏟ ᎡᏓᏍᏘ ᎠᏂᏅ ᎠᎴ ᎠᏂᏅᎬ.</textarea><br/><button onclick='javascript:processTextArea();'>Process</button><br/><br/></div>
        </div>
        <div id="testLinks"></div>
    </div>
    <div style="display:table-cell">
        <div style="display:table-row">
            <div style="display:table-cell">Syllabary</div>
            <div style="display:table-cell"><div id="syllabary"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell">Phonetic</div>
            <div style="display:table-cell"><div id="phonetic"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell">Initial Prefixes</div>
            <div style="display:table-cell"><div id="initialPrefixes"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell;width:130px">Pronominal Prefixes</div>
            <div style="display:table-cell"><div id="pronominalPrefixes"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell">Reflexive Prefixes</div>
            <div style="display:table-cell"><div id="reflexivePrefixes"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell">Root</div>
            <div style="display:table-cell"><div id="root"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell">Non-Final Suffixes</div>
            <div style="display:table-cell"><div id="nonFinalSuffixes"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell">Tense Suffixes</div>
            <div style="display:table-cell"><div id="tenseSuffixes"></div></div>
        </div>
        <div style="display:table-row">
            <div style="display:table-cell">Final Suffixes</div>
            <div style="display:table-cell"><div id="finalSuffixes"></div></div>
        </div>

        <br/>JSON Representation<br/>
        <div id="display"></div>
        <div id="resultData"></div> style="display:block; visibility:hidden"
    </div>
</div>
<asset:javascript src="verbdeconstruction.js"/>
<asset:javascript src="tests.js"/>
<script>
    function processText() {
        display(document.getElementById('texttoprocess').value);
    }

    function processTextArea() {
        var processedList = [];

        var textToParseFromTextArea = document.getElementById('textareatoprocess').value;

        textToParseFromTextArea = textToParseFromTextArea.trim()

        var splitSentence = textToParseFromTextArea.split(" ")

        for (let i = 0; i < splitSentence.length; i++) {
            var splitSentenceElement = splitSentence[i];
            //remove punctuation
            //process each item as a translation
            //see what happens
            splitSentenceElement = splitSentenceElement.trim();
            splitSentenceElement = splitSentenceElement.replaceAll(/[\,\.\?]/g, "");

            if (splitSentenceElement !== "") {
                processedList.push(JSON.stringify(process(splitSentenceElement)));
            }
        }

        display(processedList.join(","));
    }

    // runTests();
</script>

<script>
    var testLinks = [];

    testLinks.push('ᎠᎦᏍᎦᏛ');

    //https://stackoverflow.com/questions/18500759/createelement-a-href-variable1variable2-a/18500892
    //https://stackoverflow.com/questions/11017509/add-onclick-event-to-document-createelementth
    const appendAnchorTag = (text) => {
        const anchor = document.createElement('a');
        const list = document.getElementById('testLinks');
        anchor.href = '#';
        anchor.innerText = text + " - " + parseSyllabary(text);
        anchor.setAttribute('onclick', "display('" + text + "');");

        list.appendChild(anchor);
        list.appendChild(document.createElement("br"));
    };

    appendAnchorTag('ᎤᏬᏂᏏᎶᎡᎢ');
    appendAnchorTag('ᎦᏬᏂᎭᏊᏧ');
    appendAnchorTag('ᎦᏬᏂᏍᎬᎢ');
    appendAnchorTag('ᎤᏬᏂᏎᎢ');
    appendAnchorTag('ᎦᏬᏂᏍᎬᎢ');
    appendAnchorTag('ᎦᏬᏂᏏᎶᏍᎬᎢ');
    appendAnchorTag('ᎯᏬᏂᎯ');
    appendAnchorTag('ᎯᏬᏂᏏᎶᏣ');
    appendAnchorTag('ᎠᎦᏍᎦᏛ'); // it's raining'
    appendAnchorTag('ᏱᎦᏬᏂᏏᎶᏍᎬᎢᏛ');
    appendAnchorTag('ᎠᏆᏚᎵᎭ');
    appendAnchorTag('ᏓᏆᏚᎵᎭ');
</script>

</body>
</html>
