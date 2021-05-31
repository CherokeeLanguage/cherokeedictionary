async function display(word, isSyllabary=true) {
    var wholeWord = await process(word, isSyllabary);
    // console.log(JSON.stringify(wholeWord.lookupOptions));
    //
    // console.log("dictionary entries " + wholeWord.definitions.length);
    // for (const dictionaryEntry of wholeWord.definitions) {
    //     console.log("dictionary Entry " + dictionaryEntry.definitions);
    // }
    //
    // // console.log(JSON.stringify(wholeWord));
    // document.getElementById("display").innerText = JSON.stringify(wholeWord);
    // document.getElementById("syllabary").innerText = JSON.stringify(wholeWord.syllabary);
    // document.getElementById("phonetic").innerText = JSON.stringify(wholeWord.phonetic);
    // document.getElementById("initialPrefixes").innerText = JSON.stringify(wholeWord.initialPrefixes);
    // document.getElementById("pronominalPrefixes").innerText = JSON.stringify(wholeWord.pronounPrefixes);
    // document.getElementById("reflexivePrefixes").innerText = JSON.stringify(wholeWord.reflexivePrefix);
    // document.getElementById("root").innerText = JSON.stringify(wholeWord.tmpParse);
    // document.getElementById("nonFinalSuffixes").innerText = JSON.stringify(wholeWord.nonFinalSuffixes);
    // document.getElementById("tenseSuffixes").innerText = JSON.stringify(wholeWord.verbTenseSuffixes);
    // document.getElementById("finalSuffixes").innerText = JSON.stringify(wholeWord.finalSuffixes);
    //
    // document.getElementById("displayWord")
    createDisplayWord(wholeWord);
}

function createDisplayWord(ww) {
    let html = "<div class=\"interlinear\">";
        html += `<div class=\"orig\">${ww.syllabary}</div>`;

        html += `<div class="intlin">`;
        html += `    <div class="orig">${ww.phonetic}</div>`;

        let pronPrefix = PronominalPrefixes.get(ww.pronounPrefixes[0]);

        let definitionHTML = "";
        let dictDefinition = "";
        definitionHTML += "<div class=\"freetrans\">";
            for (const definition of ww.definitions) {
                definitionHTML += definition.definitiond;

                if (definition.partofspeechc === 'n' || definition.partofspeechc === 'pt' || definition.partofspeechc === 'adj') {
                    dictDefinition = definition.definitiond;
                }
                definitionHTML += "<br/>";
            }
        definitionHTML += "</div>";

        if (pronPrefix !== undefined) {
            if (startsWithVowel(ww.tmpParse)) {
                pronPrefix = pronPrefix.preVowel
            } else {
                pronPrefix = pronPrefix.preConsonant
            }
        } else {
            // console.log("wholeWord prefixes " + JSON.stringify(wholeWord));
        }

        for (const initialPrefix of ww.initialPrefixes) {
            html += `    <div class="intlin" style="float:left">`;
            html += `        <div class="orig">${newTsalagiToSyllabary(initialPrefix.affix, false, true)}</div>`;
            html += `        <div class="morph">${initialPrefix.affix}</div>`;
            html += `        <div class="trans">${initialPrefix.meaning}</div>`;
            html += `    </div>`;
        }

        if (pronPrefix) {
            if (pronPrefix.endsWith("qu")) {
                pronPrefix = pronPrefix.substring(0, pronPrefix.indexOf("qu")) + "gw";
            }

            html += `    <div class="intlin" style="float:left">`;
            html += `        <div class="orig">${newTsalagiToSyllabary(pronPrefix, false, true)}</div>`;
            html += `        <div class="morph">${pronPrefix}</div>`;
            html += `        <div class="trans">${ww.pronounPrefixes[0]}</div>`;
            html += `    </div>`;
        }

        if (ww.root_syllabary) {
            html += `    <div class="intlin" style="float:left">`;
            html += `        <div class="orig">${ww.root_syllabary}</div>`;
            html += `        <div class="morph">${ww.root_phonetic}</div>`;
            html += `        <div class="trans">Root</div>`;
            html += `    </div>`;
        }

        if (ww.root_ending) {
            html += `    <div class="intlin" style="float:left">`;
            html += `        <div class="orig">${newTsalagiToSyllabary(ww.root_ending, false, true)}</div>`;
            html += `        <div class="morph">${ww.root_ending}</div>`;
            html += `        <div class="trans">Root Ending</div>`;
            html += `    </div>`;
        }

        for (const nonFinalSuffix of ww.nonFinalSuffixes) {
            html += `    <div class="intlin" style="float:left">`;
            html += `        <div class="orig">${newTsalagiToSyllabary(nonFinalSuffix.affix, false, true)}</div>`;
            html += `        <div class="morph">${nonFinalSuffix.affix}</div>`;
            html += `        <div class="trans">${nonFinalSuffix.meaning}</div>`;
            html += `    </div>`;
        }

        html += `    <div class="intlin" style="float:left">`;
        html += `        <div class="orig">${newTsalagiToSyllabary(ww.verbTense.ending, false, true)}</div>`;
        html += `        <div class="morph">${ww.verbTense.ending}</div>`;
        html += `        <div class="trans">${ww.verbTense.tense}</div>`;
        html += `    </div>`;

        for (const finalSuffix of ww.finalSuffixes) {
            html += `    <div class="intlin" style="float:left">`;
            html += `        <div class="orig">${newTsalagiToSyllabary(finalSuffix.phonetic, false, true)}</div>`;//${newTsalagiToSyllabary(finalSuffix, false, true)}
            html += `        <div class="morph">${finalSuffix.phonetic}</div>`;//${ww.nonFinalSuffixes}
            html += `        <div class="trans">${finalSuffix.meaning}</div>`;
            html += `    </div>`;
        }

        html += `</div>`;

        html += definitionHTML;

    html += "</div>"

    document.getElementById("wordDisplay").innerHTML = html;
}

function getBreakdown(word) {
    var wholeWord = process(word, true);
    console.log(JSON.stringify(wholeWord));
}

/*if (ww.definitions.length > 0) {
    if (ww.definitions[0].partofspeechc === 'vi' || ww.definitions[0].partofspeechc === 'vt') {
        // if (ww.initialPrefixes.length > 0) {
        //     html += formInitialPrefixes(ww.initialPrefixes);
        // }
        //
        // html += formPronominalPrefixes(ww.pronounPrefixes);
        //
        // if (ww.reflexive) {
        //     html += formReflexivePrefixes(ww.reflexive);
        // }
        //
        // html += formBase(ww.root_syllabary, ww.definitions)
        //
        // html += formRootEnding(ww.root_ending);
        //
        // // todo: verb tense should be in syllabary too
        // html += formVerbTense(ww.verbTenseSuffixes);
        //
        // // todo: non final suffixes should be in syllabary too
        // html += formNonFinalSuffixes(ww.nonFinalSuffixes);
    }
}*/

// // todo: final suffixes should have syllabary
// html += formFinalSuffixes(ww.finalSuffixes);