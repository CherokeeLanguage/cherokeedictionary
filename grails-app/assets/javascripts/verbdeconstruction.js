function startsWithVowel(word) {
    return word.substring(0, 1).startsWith('a') || word.substring(0, 1).startsWith('e') || word.substring(0, 1).startsWith('i')  || word.substring(0, 1).startsWith('o') || word.substring(0, 1).startsWith('u') || word.substring(0, 1).startsWith('v');
}

function endsWithVowel(word) {
    return word.endsWith('a') || word.endsWith('e') || word.endsWith('i')  || word.endsWith('o') || word.endsWith('u') || word.endsWith('v');
}

function newWholeWord(word, isSyllabary) {
    var wholeWord = {
        phonetic: "", // phonetic transliteration of original syllabary lookup
        syllabary: "", // original word to look up
        root_phonetic: "", // phonetic root of the word as broken down
        root_syllabary: "", // syllabary root of the word as broken down - if the root ending contains a phonetic "letter" then it will appear as syllabary+phonetic letter
        definitions: [], // all definitions found in the database
        root_ending: "", // what the root ending is - phonetic only
        constructedVerbToLookup: "", // if this word is a verb then this takes the third person prefix type (A or B), root, root ending, present tense to give a lookup so uwonisvi (third past) would become gawoniha (third present) and is then
        verbTense: {tense: "", ending: ""}, // what is the tense of the current word
        initialPrefixes: [], // what are the initial prefixes found
        pronounPrefixes: [], // what are the pronoun prefixes found
        reflexive: false, // was a reflexive prefix found
        nonFinalSuffixes: [], // what are the non final suffixes found
        finalSuffixes: [] // what are the final suffixes found
    }

    if (isSyllabary) {
        wholeWord.syllabary = word;
        wholeWord.phonetic = parseSyllabary(word)
    } else {
        wholeWord.phonetic = word;
    }

    wholeWord.phonetic = wholeWord.phonetic.replaceAll(/qu/g, "gw");

    return wholeWord;
}

async function deconstruct(wholeWord, word) {
    wholeWord = getFinalSuffixes(wholeWord);

    //check word against dictionary
    // look up in database
    let tmpValSyllabary = tsalagiToSyllabary(wholeWord.tmpParse);
    let values = await lookupWordInCED(tmpValSyllabary);

    // if there's a result then we are going to put that information into the display object
    if (values.length > 0) {
        console.log("second value thing found");
        wholeWord.syllabary = tmpValSyllabary;
        // TODO: we found an item in the database - we need to create a new display object that we're going to send to the user or the page
        wholeWord = await valueFound(values, wholeWord);
        // console.log("displayObject 2nd " + JSON.stringify(wholeWord));
    } else {
        wholeWord = getVerbTenseSuffixes(wholeWord);

        for (const tmpElementElement of wholeWord.verbTenseSuffix) {
            wholeWord.verbTenseType = tmpElementElement.meaning;
            //TODO: also need to get verb tense ending here -- will need another map
        }

        wholeWord = getNonFinalSuffixes(wholeWord);
        wholeWord = getInitialPrefixes(wholeWord);
        wholeWord = getPronominalPrefixes(wholeWord);
        // wholeWord = getReflexivePrefix(wholeWord);
    }

    return wholeWord;
}

async function valueFound(values, wholeWord) {
    // TODO: what do we need from the dictionary entry?  definition,
    var vals = JSON.parse(values[0]);

    for (const value of vals) {
        wholeWord.dictionaryEntries.push(value);
    }

    return wholeWord;
}

function getPronPrefix(wholeWord) {
    var pronPrefixes = wholeWord?.pronounPrefixes[0];
    var pronPrefix = undefined;

    if (pronPrefixes !== undefined) {
        pronPrefix = PronominalPrefixes.get(pronPrefixes);
        if (pronPrefix !== undefined) {
            if (startsWithVowel(wholeWord.tmpParse)) {
                pronPrefix = pronPrefix.preVowel
            } else {
                pronPrefix = pronPrefix.preConsonant
            }
        } else {
            // console.log("wholeWord prefixes " + JSON.stringify(wholeWord));
        }

        //TODO: at this point uwoniha becomes uw + onih + a -- it should be u + wonih + a
        if (pronPrefix.startsWith("uw")) {
            if (wholeWord.pronounPrefixes[0].endsWith("B")) {
                pronPrefix = "ga";
            }

            wholeWord.tmpParse = "w" + wholeWord.tmpParse;
        } else if (pronPrefix.startsWith("u")) {
            if (wholeWord.pronounPrefixes[0].endsWith("B")) {
                pronPrefix = "ga";
            }
        }
    } else {
        pronPrefix = "";
    }

    return pronPrefix;
}

//if processing phonetic then pass in isSyllabary as false
async function process(word, isSyllabary=true) {
    // process
    var wholeWord = newWholeWord(word, isSyllabary);

    //     lookup
    // look up in database
    let values = await lookupWordInCED(word);

    //     if exists
    //         setup wholeWord and return
    if (values.length > 0) {
        console.log("first value thing found");
        wholeWord = await valueFound(values, wholeWord);
    } else {
        //     else
        //         deconstruct
        //         strip final suffixes
        //         lookup
        //         if exists
        //             setup wholeWord and return
        //     else
        //         remove affixes
        wholeWord = await deconstruct(wholeWord, word);

        //         put together a simple word that might match the database
        if (wholeWord.definition === "" || wholeWord.definition === undefined) {
            // right here put together a generic verb entry and then look it up
            if (wholeWord.pronounPrefixes && wholeWord.pronounPrefixes.length > 0) {
                var pronPrefix = getPronPrefix();

                //other endings need to be added h, l, s, d, and others
                if (wholeWord.tmpParse.endsWith("s")) {
                    wholeWord.tmpParse = wholeWord.tmpParse.substring(0, wholeWord.tmpParse.length -1) + "h";
                }

                //once parsed then try another lookup in the database
                var tmpWord = pronPrefix + wholeWord.tmpParse + VerbTenseLookup.get("PRESENT");
                // console.log("tmpWord " + tmpWord);
                tmpWord = tsalagiToSyllabary(tmpWord);

                //         lookup
                values = await lookupWordInCED(tmpWord);

                //         if exists
                //             setup wholeWord and return
                if (values.length > 0) {
                    // TODO:  also take broken down word and put that data into a display object
                    wholeWord = await valueFound(values, wholeWord);
                } else {
                    //     else
                    //         we didn't find a match so no definition
                    //         return wholeWord
                    // TODO: if there's no result then DO SOMETHING ELSE
                    console.log("still could not find a result");
                }
            }
        }
    }

    return wholeWord;
}

async function display(word, isSyllabary=true) {
    var wholeWord = await process(word, isSyllabary);
    console.log("dictionary entries " + wholeWord.dictionaryEntries.length);
    for (const dictionaryEntry of wholeWord.dictionaryEntries) {
        console.log("dictionary Entry " + dictionaryEntry.definitiond);
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
    document.getElementById("tenseSuffixes").innerText = JSON.stringify(wholeWord.verbTenseSuffix);
    document.getElementById("finalSuffixes").innerText = JSON.stringify(wholeWord.finalSuffixes);
}

function getBreakdown(word) {
    var wholeWord = process(word, true);
    console.log(JSON.stringify(wholeWord));
}
