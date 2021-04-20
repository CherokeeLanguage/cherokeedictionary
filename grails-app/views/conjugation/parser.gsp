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
    <meta charset="UTF-8">
    <title>Title</title>
    <script src="cherokeeParser.js"></script>
    <script src="verbTables.js"></script>
    <asset:javascript src="cherokeeParser.js"/>
    <asset:javascript src="verbTables.js"/>

</head>
<body>
<a href="#" onclick="javascript:process('ᎤᏬᏂᏏᎶᎡᎢ');">ᎤᏬᏂᏏᎶᎡᎢ - uwonisiloei</a><br/>
<a href="#" onclick="javascript:process('ᎦᏬᏂᎭᏊᏧ');">ᎦᏬᏂᎭᏊᏧ - gawonihaquutsu</a><br/>
<a href="#" onclick="javascript:process('ᎦᏬᏂᏍᎬᎢ');">ᎦᏬᏂᏍᎬᎢ - gawonisgvi</a><br/>
<a href="#" onclick="javascript:process('ᎤᏬᏂᏎᎢ');">ᎤᏬᏂᏎᎢ- uwonisei</a><br/>
<a href="#" onclick="javascript:process('ᎦᏬᏂᏍᎬᎢ');">ᎦᏬᏂᏍᎬᎢ - gawonisgvi</a><br/>
<a href="#" onclick="javascript:process('ᎦᏬᏂᏏᎶᏍᎬᎢ');">ᎦᏬᏂᏏᎶᏍᎬᎢ - gawonisilosgvi</a><br/>
<a href="#" onclick="javascript:process('ᎯᏬᏂᎯ');">ᎯᏬᏂᎯ - hiwonihi</a><br/>
<a href="#" onclick="javascript:process('ᎯᏬᏂᏏᎶᏣ');">ᎯᏬᏂᏏᎶᏣ - hiwonisiloja</a><br/>
<a href="#" onclick="javascript:process('ᎯᏬᏂᎯ');">ᎯᏬᏂᎯ</a><br/>
<a href="#" onclick="javascript:process('ᎠᎦᏍᎦᏛ');">ᎠᎦᏍᎦᏛ</a>
<br/><br/>
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
    <div style="display:table-cell">Pronominal Prefixes</div>
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
<br/><br/>
<div style="display:table-row">
    <div style="display:table-cell"><textarea id="textareatoprocess">ᎦᏬᏂᏍᎨᏍᏗ</textarea></div>
    <div style="display:table-cell"><button onclick='javascript:processTextArea();'>Process</button></div>
</div>

<div id="display"></div>

<asset:javascript src="verbdeconstruction.js"/>
<script>
    function processTextArea() {
        process(document.getElementById('textareatoprocess').value);
    }
</script>
<!--transliteration_complex: "[da¹gwạdu³lịha]",
transliteration_simple: "[dagwaduliha]",
mophemes: [{
morpheme_romanized: "d",
morpheme_syllabary: "XX",
morpheme_name: "prefix",
morpheme_function: "plurality",
morpheme_meaning: "many",
}, {
morpheme_romanized: "agw",
morpheme_syllabary: "ᎠᏆ",
morpheme_name: "prefix",
morpheme_fucntion: "pronoun",
morpheme_meaning: "1st person pronoun (I)",
}, {
morpheme_romanized: "aduli",
morpheme_syllabary: "ᏚᎵ",
morpheme_name: "root",
morpheme_fucntion: "root",
morpheme_meaning: "see dicts",
}, {
morpheme_romanized: "h",
morpheme_syllabary: "_not_typable_in_syllabary",
morpheme_name: "suffix",
morpheme_fucntion: "speech_softening",
morpheme_meaning: "none",
}, {
morpheme_romanized: "a",
morpheme_syllabary: "Ꭰ",
morpheme_name: "suffix",
morpheme_fucntion: "tense",
morpheme_meaning: "present tense",
}]-->
</body>
</html>
