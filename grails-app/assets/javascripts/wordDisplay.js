function formFinalSuffixes(finalSuffixes) {
    var html = "";
    html += "<div class=\"tableCell\" style=\"display:table-cell\">";

    for (const finalSuffix of finalSuffixes) {
        html += createRowOneCell(finalSuffix.syllabary);
        html += createRowOneCell(finalSuffix.phonetic);
        html += createRowOneCell(finalSuffix.meaning);
    }

    html += "</div>";

    return html;
}

function formNonFinalSuffixes(nonFinalSuffixes) {
    var html = "";
    html += "<div class=\"tableCell\" style=\"display:table-cell\">";

    for (const nonFinalSuffix of nonFinalSuffixes) {
        html += createRowOneCell(nonFinalSuffix.syllabary);
        html += createRowOneCell(nonFinalSuffix.phonetic);
        html += createRowOneCell(nonFinalSuffix.meaning);
    }

    html += "</div>";

    return html;
}

function formVerbTense(verbTenses) {
    var html = "";
    html += "<div class=\"tableCell\" style=\"display:table-cell\">";

    for (const verbTense of verbTenses) {
        html += createRowOneCell(verbTense.phonetic);
        html += createRowOneCell(verbTense.meaning);
    }

    html += "</div>";

    return html;
}

function formRootEnding(rootEnding) {
    let html = "";
    html += "<div class=\"tableCell\" style=\"display:table-cell\">";
    html += createRowOneCell(rootEnding);
    html += createRowOneCell("root ending");
    html += "</div>";

    return html;
}

function formDefinition(definitions) {
    let html = "";

    for (const definition of definitions) {
        html += createRowOneCell(definition.definitiond);
    }

    return html;
}

function formBase(root, definitions) {
    let html = "";

    html += "<div class=\"tableCell\" style=\"display:table-cell\">";
    html += createRowOneCell(root);
    html += formDefinition(definitions);

    html += "</div>";

    return html;
}

function createRowOneCell(value) {
    var html = "";
    html += "<div class=\"tableRowz\" style=\"display:table-row\"><div class=\"tableCell\" style=\"display:table-cell\">" + value + "</div></div>";

    return html;
}

async function display(word, isSyllabary=true) {
    let wholeWord = await process(word, isSyllabary);
    console.log("dictionary entries " + wholeWord.definitions.length);
    for (const dictionaryEntry of wholeWord.definitions) {
        console.log("dictionary Entry " + dictionaryEntry.definitions);
    }

    // console.log(JSON.stringify(wholeWord));
    document.getElementById("display").innerText = JSON.stringify(wholeWord);
    document.getElementById("syllabary").innerText = JSON.stringify(wholeWord.syllabary);
    document.getElementById("phonetic").innerText = JSON.stringify(wholeWord.phonetic);
    document.getElementById("initialPrefixes").innerText = JSON.stringify(wholeWord.initialPrefixes);
    document.getElementById("pronominalPrefixes").innerText = JSON.stringify(wholeWord.pronounPrefixes);
    document.getElementById("reflexivePrefixes").innerText = JSON.stringify(wholeWord.reflexivePrefix);
    document.getElementById("root").innerText = JSON.stringify(wholeWord.tmpParse);
    document.getElementById("nonFinalSuffixes").innerText = JSON.stringify(wholeWord.nonFinalSuffixes);
    document.getElementById("tenseSuffixes").innerText = JSON.stringify(wholeWord.verbTenseSuffixes);
    document.getElementById("finalSuffixes").innerText = JSON.stringify(wholeWord.finalSuffixes);

    // document.getElementById("displayWord")
    createDisplayWord(wholeWord);
}

function createDisplayWord(ww) {
    let html = "";

    html += formBase(ww.root_syllabary, ww.definitions)

    html += formRootEnding(ww.root_ending);

    // todo: verb tense should be in syllabary too
    html += formVerbTense(ww.verbTenseSuffixes);

    // todo: non final suffixes should be in syllabary too
    html += formNonFinalSuffixes(ww.nonFinalSuffixes);

    // todo: final suffixes should have syllabary
    html += formFinalSuffixes(ww.finalSuffixes);

    document.getElementById("wordDisplay").innerHTML = html;
}

function getBreakdown(word) {
    var wholeWord = process(word, true);
    console.log(JSON.stringify(wholeWord));
}