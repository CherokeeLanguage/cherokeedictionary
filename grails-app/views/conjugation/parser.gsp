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
%{--        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>--}%
%{--        <asset:javascript src="grooscript.min.js"/>--}%
%{--        <asset:javascript src="grooscript-html-builder.js"/>--}%

        <asset:javascript src="deconstructor/cherokeeParser.js"/>
        <asset:javascript src="deconstructor/ajaxCall.js"/>
        <asset:javascript src="deconstructor/rework.js"/>
        <asset:javascript src="deconstructor/verbTables.js"/>
        <asset:javascript src="deconstructor/wordDisplay.js"/>
        <asset:javascript src="deconstructor/deconstructionMethods.js"/>
        <asset:javascript src="deconstructor/display.js"/>
%{--            <asset:javascript src="parser.js"/>--}%
%{--        <script>--}%
%{--            function callAll(jsfiles) {--}%
%{--                $.getScript(jsfiles, function() {--}%
%{--                    // alert("Script loaded but not necessarily executed.");--}%
%{--                });--}%
%{--            }--}%

%{--            callAll("/assets/cherokeeParser.js");--}%
%{--            callAll("/assets/ajaxCall.js");--}%
%{--            callAll("/assets/rework.js");--}%
%{--            callAll("/assets/verbTables.js");--}%
%{--            callAll("/assets/wordDisplay.js");--}%
%{--            callAll("/assets/deconstructionMethods.js");--}%
%{--            callAll("/assets/display.js");--}%
%{--            // callAll("/assets/tests.js");--}%
%{--        </script>--}%
        <style>
        .tableRowz {
                padding-right:10px;
                text-align: center;
            }

        .tableCell {
                padding-right:10px;
                text-align: center;
            }
        </style>
        <style>
            .interlinear {clear: both; text-align: left; border: 0 black solid}
            .intlin {margin: 0.25em; text-align: left;border: 0 black solid}
            .intlin .morph, .intlin .trans, .intlin .orig, .interlinear {display: block; margin: 2px 0;}
            .intlin .orig {font-weight: bold;height: 22px}
            .interlinear .freetrans { font-style: italic;}
            .interlinear div.orig { font-weight: bold; text-align: left;}
            .interlinear .morph { color: #10a }
        </style>
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
        <div id="todolist">
            TODO LIST<br/>
            <ul>
                <li>verbs that start with 'a' are currently merged with the root</li>
                <li>test different prefixes like di, de, etc</li>
                <li>parse a whole sentence and display interlinear format</li>
                <li>parse nonverbs</li>
            </ul>
        </div>
        <div id="testLinks"></div>
    </div>
    <div style="display:table-cell">
        <div style="display:table-row">
            <div style="display:table-cell"><div id="wordDisplay"></div></div>
        </div>
        %{--<div style="display:table-row">
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
        <div id="resultData"></div> style="display:block; visibility:hidden"--}%
    </div>
</div>


<asset:javascript src="deconstructor/verbdeconstruction.js"/>
%{--<asset:javascript src="tests.js"/>--}%
<script>
    function createDisplayWordTest() {
        var html = "";
        var ww = newWholeWord("ᏱᎦᏬᏂᏏᎶᏍᎬᎢᏛ", true);
        //initialprefixes + pronominalprefixes + reflexive + root + root ending + nonfinal suffixes + final suffixes
        ww.initialPrefixes = ["negative"];// need to add all of the prefix
        ww.pronounPrefixes = ["SG3SG3IN"];// need to add the pronoun portion
        ww.finalSuffixes = [{"phonetic":"dv","syllabary":"","meaning":"focus","purpose":"","name":""}];
        ww.definitions = [{"id":73206,"vsecondimpertone":"hi.2wo1ni23hi","definitionlarge":"","definitiond":"he's speaking","entrytone":"ga.2wo3ni.2ha","vthirdprestone":"ga.2wo3ni2sgo3?i","vthirdprestranslit":"gawonisgoi","notes":"","nounadjpluraltone":"","crossreferencet":"gawonida","sentenceq":"Tsgwisdi <u>gawoniha</u>, asehnv hla gohusdi yadiha.","vsecondimperm":"hiwonihi","partofspeechc":"vi","vfirstprestone":"ji.2wo3ni.2ha","vthirdinfsyllp":"ᎤᏬᏂᎯᏍᏗ","vthirdinfo":"uwonihisdi","source":"ced","sentenceenglishs":"<u>He's</u> <u>talking</u> a lot, but he's not saying anything.","entrytranslit":"gawoniha","vthirdinftranslit":"uwonihisdi","vthirdpasti":"uwonisvi","nounadjpluraltranslit":"","vthirdpressylll":"ᎦᏬᏂᏍᎪᎢ","vthirdpasttone":"u1wo3ni2sv23?i","vsecondimpertranslit":"hiwonihi","vfirstpresg":"jiwoniha","vfirstpresh":"ᏥᏬᏂᎭ","vthirdpastsyllj":"ᎤᏬᏂᏒᎢ","sentencesyllr":"ᏥᏈᏍᏗ <u>ᎦᏬᏂᎭ</u>, ᎠᏎᏅ Ꮭ ᎪᎱᏍᏗ ᏯᏗᎭ.","category":"","vthirdpasttranslit":"uwonisvi","sentencetranslit":"tsiquisdi <u>gawoniha</u>, asenv tla gohusdi yadiha.","nounadjpluralsyllf":"","nounadjplurale":"","entrya":"gawoniha","vthirdinftone":"u2wo1ni23hi.3sdi","vsecondimpersylln":"ᎯᏬᏂᎯ","syllabaryb":"ᎦᏬᏂᎭ","etymology":"","vfirstprestranslit":"tsiwoniha","vthirdpresk":"gawonisgoi"},{"id":100923,"vsecondimpertone":"hịwonhi","definitionlarge":"","definitiond":"He is talking.; He is speaking","entrytone":"gạwoniha","vthirdprestone":"gạwonisgo³ɂi","vthirdprestranslit":"gawonisgoi","notes":"","nounadjpluraltone":"","crossreferencet":"","sentenceq":"[King p40]","vsecondimperm":"hiwonhi","partofspeechc":"vi","vfirstprestone":"tsịwonịɂa","vthirdinfsyllp":"ᎤᏬᏂᎯᏍᏗ","vthirdinfo":"uwonhisdi","source":"rrd","sentenceenglishs":"“<u>I will speak</u> in Cherokee.”","entrytranslit":"gawoniha","vthirdinftranslit":"uwonihisdi","vthirdpasti":"uwonsvi","nounadjpluraltranslit":"","vthirdpressylll":"ᎦᏬᏂᏍᎪᎢ","vthirdpasttone":"uwonsv²³ɂi","vsecondimpertranslit":"hiwonihi","vfirstpresg":"tsiwonia","vfirstpresh":"ᏥᏬᏂᎠ","vthirdpastsyllj":"ᎤᏬᏂᏒᎢ","sentencesyllr":"ᏴᏫᏯ ᎬᏗ <u>ᏓᏥᏬᏂᏏ</u>.","category":"","vthirdpasttranslit":"uwonisvi","sentencetranslit":"yvwiya gvdi <u>datsiwonisi</u>.","nounadjpluralsyllf":"","nounadjplurale":"","entrya":"gawoniha","vthirdinftone":"uwonhisdi","vsecondimpersylln":"ᎯᏬᏂᎯ","syllabaryb":"ᎦᏬᏂᎭ","etymology":"","vfirstprestranslit":"tsiwonia","vthirdpresk":"gawonisgoi"}];
        ww.verbTenses = [{"phonetic":"vi","syllabary":"","meaning":"PAST","purpose":"","name":""}];
        ww.root_syllabary = "ᏬᏂ";
        ww.root_ending = "h";
        ww.root_phonetic = "woni";
        console.log("ww " + ww.syllabary);
        console.log("ww " + ww.phonetic);
        console.log("root " + ww.root_syllabary);

        html += formBase(ww.root_syllabary, ww.definitions)

        html += formRootEnding(ww.root_ending);

        // todo: verb tense should be in syllabary too
        html += formVerbTense(ww.verbTenses);

        // todo: non final suffixes should be in syllabary too
        html += formNonFinalSuffixes(ww.nonFinalSuffixes);

        // todo: final suffixes should have syllabary
        html += formFinalSuffixes(ww.finalSuffixes);

        document.getElementById("wordDisplay").innerHTML = html;
    }

    // createDisplayWordTest();

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

    appendAnchorTag('ᎦᏬᏂᏍᎬᎢ');
    appendAnchorTag('ᏓᏆᏚᎵᎭ');
    appendAnchorTag('ᏳᏫᎦᏬᏂᏏᎶᏍᎬᎢᏛ');
    appendAnchorTag('ᎠᎦᏍᎦᏛ'); // it's raining'
    appendAnchorTag('ᎠᏆᏚᎵᎭ');
    appendAnchorTag('ᎦᏬᏂᎭ');
    appendAnchorTag('ᎤᏬᏂᏏᎶᎡᎢ');
    appendAnchorTag('ᎦᏬᏂᎭᏊᏧ');
    appendAnchorTag('ᎦᏬᏂᏍᎬᎢ');
    appendAnchorTag('ᎤᏬᏂᏎᎢ');
    appendAnchorTag('ᎦᏬᏂᏏᎶᏍᎬᎢ');
    appendAnchorTag('ᎯᏬᏂᎯ');
    appendAnchorTag('ᎯᏬᏂᏏᎶᏣ');
    appendAnchorTag('ᏱᎦᏬᏂᏏᎶᏍᎬᎢᏛ');
</script>

</body>
</html>
