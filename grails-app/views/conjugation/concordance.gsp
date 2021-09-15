<%--
  Created by IntelliJ IDEA.
  User: winkimac
  Date: 4/20/21
  Time: 12:04 PM
--%>

<%@ page import="net.cherokeedictionary.dictionary.Likespreadsheets; net.cherokeedictionary.ConjugatedVerb; net.cherokeedictionary.corpus.Concordance; net.cherokeedictionary.corpus.Verse" contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    %{--        <meta name="layout" content="manager"/>--}%
    <meta charset="UTF-8">
    <title>Concordance</title>

</head>
<body>
<%
def lst = Concordance.list([max: 200])
lst.each {
    out << raw("<b>" + it.word + "</b><br/>")
    def lista = Likespreadsheets.findAll("from Likespreadsheets where syllabaryb like ?0 or vfirstpresh like ?1 or vthirdpastsyllj like ?2 or vthirdpressylll like ?3 or vsecondimpersylln like ?4 or vthirdinfsyllp like ?5", ["%${it}%", "%${it}%", "%${it}%", "%${it}%", "%${it}%", "%${it}%"])
    if (lista.isEmpty()) {
        def lst2 = ConjugatedVerb.findAll("from ConjugatedVerb where conjugation=?0", [it.word])
        if (lst2.isEmpty()) {
            def lst3 = ConjugatedVerb.findAll("from ConjugatedVerb where conjugation like ?0", ["%${it.word}%"])
            if (lst3.isEmpty()) {
                def tmp = it.word.substring(0, it.word.size() - 1)
                def lst4 = ConjugatedVerb.findAll("from ConjugatedVerb where conjugation like ?0", ["%${tmp}%"])
                if (lst4.isEmpty()) {
                    out << raw("couldn't find <br/>")
                } else {
                    lst4.each {
                        out << raw(it.conjugation + " " + it.compoundPrefix + " " + it.likespreadsheets.syllabaryb + " " + it.likespreadsheets.definitiond + "<br/>")
                    }
                }
            } else {
                lst3.each {
                    out << raw(it.conjugation + " " + it.compoundPrefix + " " + it.likespreadsheets.syllabaryb + " " + it.likespreadsheets.definitiond + "<br/>")
                }
            }
        } else {
            lst2.each {
                out << raw(it.conjugation + " " + it.compoundPrefix + " " + it.likespreadsheets.syllabaryb + " " + it.likespreadsheets.definitiond + "<br/>")
            }
        }
    } else {
        lista.each {
            out << raw(it.syllabaryb + " " + it.definitiond + "<br/>")
        }
    }

    out <<  raw("<br/>")
}
%>

%{--<g:each in="${lst}" var="item">--}%
%{--    ${lst}--}%
%{--    ${item.likespreadsheets.entrya} - ${item.likespreadsheets.definitiond} -- ${item.compoundPrefix}<br/>--}%
%{--    ${item.likespreadsheets.id} - ${item.likespreadsheets.syllabaryb} - ${item.conjugation}<br/>--}%
%{--</g:each>--}%

<%--
    def verses = Verse.findAll("from Verse where source=?0", ["chr"])
    def words = new HashSet()
    def count = 0
    verses.each {
        def split = it.verseContext.split(" ")
        split.each {spl ->
            count++
            spl = spl.replaceAll("\\?", "").replaceAll(";", "").replaceAll("\\.", "").replaceAll(",", "").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("]", "")//.replaceAll("")
            words.add(spl)
//            if (words.containsKey(spl)) {
//                def val = words.get(spl)
//                val << "${it.bookName} ${it.chapterNumber}:${it.verseNumber}"
//            } else {
//                def tmp = []
//                tmp << "${it.bookName} ${it.chapterNumber}:${it.verseNumber}"
//                words.put(spl, tmp)
//            }
        }
    }

//    out << words.size()
//    out << "count is ${count}"
//    out << raw("<br/>")
%>
<g:each in="${words}" var="word">
    <%
    def concor = new Concordance(word: word)
    if (!concor.save()) {
        concor.errors.each {
            out << it
        }
    }
%>
</g:each>
--%>
%{--
<asset:javascript src="deconstructor/HOLD/verbdeconstruction.js"/>
--}%%{--<asset:javascript src="tests.js"/>--}%%{--
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
    };--}%

%{--    // appendAnchorTag('ᎦᏬᏂᏍᎬᎢ');--}%
%{--    // appendAnchorTag('ᏓᏆᏚᎵᎭ');--}%
%{--    // appendAnchorTag('ᏳᏫᎦᏬᏂᏏᎶᏍᎬᎢᏛ');--}%
%{--    // appendAnchorTag('ᎠᎦᏍᎦᏛ'); // it's raining'--}%
%{--    // appendAnchorTag('ᎠᏆᏚᎵᎭ');--}%
%{--    // appendAnchorTag('ᎦᏬᏂᎭ');--}%
%{--    // appendAnchorTag('ᎤᏬᏂᏏᎶᎡᎢ');--}%
%{--    // appendAnchorTag('ᎦᏬᏂᎭᏊᏧ');--}%
%{--    // appendAnchorTag('ᎦᏬᏂᏍᎬᎢ');--}%
%{--    // appendAnchorTag('ᎤᏬᏂᏎᎢ');--}%
%{--    // appendAnchorTag('ᎦᏬᏂᏏᎶᏍᎬᎢ');--}%
%{--    // appendAnchorTag('ᎯᏬᏂᎯ');--}%
%{--    // appendAnchorTag('ᎯᏬᏂᏏᎶᏣ');--}%
%{--    // appendAnchorTag('ᏱᎦᏬᏂᏏᎶᏍᎬᎢᏛ');--}%

    <%--
    def verses = Verse.findAll("from Verse where source=?0", ["chr"])
    def words = [:]
    def count = 0
    verses.each {
        def split = it.verseContext.split(" ")
        split.each {spl ->
            count++
            spl = spl.replaceAll("\\?", "").replaceAll(";", "").replaceAll("\\.", "").replaceAll(",", "").replaceAll("\"", "")

            if (words.containsKey(spl)) {
                def val = words.get(spl)
                val << "${it.bookName} ${it.chapterNumber}:${it.verseNumber}"
            } else {
                def tmp = []
                tmp << "${it.bookName} ${it.chapterNumber}:${it.verseNumber}"
                words.put(spl, tmp)
            }
        }
    }

//    out << words.size()
//    out << "count is ${count}"
//    out << raw("<br/>")
--%>
    <%--<g:each in="${words}" var="word">
        const list = document.getElementById('testLinks');
        list.appendChild(${word.});
        list.appendChild(document.createElement("br"));
    </g:each>--%>

</script>

</body>
</html>
