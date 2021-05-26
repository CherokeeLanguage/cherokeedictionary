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

function formInitialPrefixes(initialPrefixes) {
    let html = "";

    html += "<div class=\"tableCell\" style=\"display:table-cell\">";
    html += "initial prefixes";
    html += "</div>";

    return html;
}

function formPronominalPrefixes(pronomialPrefix) {
    let html = "";

    html += "<div class=\"tableCell\" style=\"display:table-cell\">";
    html += createRowOneCell("Pronominal Prefix");
    html += createRowOneCell(pronomialPrefix);

    html += "</div>";

    return html;
}

function formReflexivePrefixes(reflexivePrefix) {
    let html = "";

    html += "<div class=\"tableCell\" style=\"display:table-cell\">";
    html += "reflexive";
    html += "</div>";

    return html;
}

function createRowOneCell(value) {
    var html = "";
    html += "<div class=\"tableRowz\" style=\"display:table-row\"><div class=\"tableCell\" style=\"display:table-cell\">" + value + "</div></div>";

    return html;
}
