function startsWithVowel(word) {
    return word.substring(0, 1).startsWith('a') || word.substring(0, 1).startsWith('e') || word.substring(0, 1).startsWith('i')  || word.substring(0, 1).startsWith('o') || word.substring(0, 1).startsWith('u') || word.substring(0, 1).startsWith('v');
}

function endsWithVowel(word) {
    return word.endsWith('a') || word.endsWith('e') || word.endsWith('i')  || word.endsWith('o') || word.endsWith('u') || word.endsWith('v');
}

function newWholeWord(word, isSyllabary) {
    var wholeWord = {
        syllabary: "",
        phonetic: "",
        tmpParse: "",
        initialPrefixes: [],
        pronounPrefixes: [],
        reflexivePrefix: false,
        nonFinalSuffixes: [],
        verbTenseSuffix: '',
        verbTenseType: Tense.PRESENT,
        finalSuffixes: []
    };

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
    let displayObject = createDisplayObect();

    // if there's a result then we are going to put that information into the display object
    if (values.length > 0) {
        console.log("second value thing found");
        wholeWord.syllabary = tmpValSyllabary;
        // TODO: we found an item in the database - we need to create a new display object that we're going to send to the user or the page
        displayObject = valueFound(values, wholeWord);
        console.log("displayObject 2nd " + JSON.stringify(displayObject));
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

    return [wholeWord, displayObject];
}

function valueFound(values, displayObject) {
    // TODO: what do we need from the dictionary entry?  definition,
    // TODO: could also be an array of values
    for (const value of values) {
        var jsonParsedValue = JSON.parse(value)[0];
        if (values.length > 1 && jsonParsedValue.source === 'ced') {
            console.log(value[0]);
        }

        console.log(jsonParsedValue);

        displayObject.definition = jsonParsedValue.definitiond;
    }

    return displayObject;
}

function createDisplayObect() {
    var displayObject = {
        syllabary:'',
        phonetic: '',
        definition: '',
        word_type: '',//verb, noun, prefix, etc
    };

    return displayObject;
}

function populateDisplayObject(displayObject, wholeWord) {
    displayObject.syllabary = wholeWord.syllabary;
    // console.log("wholeWord " + JSON.stringify(wholeWord));
    console.log(displayObject);

    return displayObject;
}

//if processing phonetic then pass in isSyllabary as false
async function process(word, isSyllabary=true) {
    var wholeWord = newWholeWord(word, isSyllabary);

    // look up in database
    let values = await lookupWordInCED(word);
    let displayObject = createDisplayObect();

    // if there's a result then we are going to put that information into the display object
    if (values.length > 0) {
        console.log("first value thing found");
        // TODO: we found an item in the database - we need to create a new display object that we're going to send to the user or the page
        displayObject = valueFound(values, wholeWord);
    } else { // if there's no result then we need to parse the word - it could be an adj, noun, or verb
        // breakdown the word
        var tmpList = await deconstruct(wholeWord, word);
        wholeWord = tmpList[0];
        displayObject = tmpList[1];
        console.log("displayObject " + JSON.stringify(displayObject));

        if (displayObject.definition === "") {
            displayObject = await hold(displayObject, wholeWord);
        }
    }

    displayObject = populateDisplayObject(displayObject, wholeWord);

    return displayObject;
}

async function display(word, isSyllabary=true) {
    var displayObject = await process(word, isSyllabary);
    console.log(JSON.stringify(displayObject));
    // document.getElementById("display").innerText = JSON.stringify(wholeWord);
    // document.getElementById("syllabary").innerText = JSON.stringify(wholeWord.syllabary);
    // document.getElementById("phonetic").innerText = JSON.stringify(wholeWord.phonetic);
    // document.getElementById("initialPrefixes").innerText = JSON.stringify(wholeWord.initialPrefixes);
    // document.getElementById("pronominalPrefixes").innerText = JSON.stringify(wholeWord.pronounPrefixes);
    // document.getElementById("reflexivePrefixes").innerText = JSON.stringify(wholeWord.reflexivePrefix);
    // document.getElementById("root").innerText = JSON.stringify(wholeWord.tmpParse);
    // document.getElementById("nonFinalSuffixes").innerText = JSON.stringify(wholeWord.nonFinalSuffixes);
    // document.getElementById("tenseSuffixes").innerText = JSON.stringify(wholeWord.verbTenseSuffix);
    // document.getElementById("finalSuffixes").innerText = JSON.stringify(wholeWord.finalSuffixes);
}

function getBreakdown(word) {
    var displayObject = process(word, true);
    console.log(JSON.stringify(displayObject));
}

async function hold(displayObject, wholeWord) {
    // right here put together a verb and then look it up
    if (wholeWord?.pronounPrefixes?.length > 0) {
        var pronPrefix = PronominalPrefixes.get(wholeWord.pronounPrefixes[0]);
        if (pronPrefix !== undefined) {
            if (startsWithVowel(wholeWord.tmpParse)) {
                pronPrefix = pronPrefix.preVowel
            } else {
                pronPrefix = pronPrefix.preConsonant
            }
        } else {
            console.log("wholeWord prefixes " + JSON.stringify(wholeWord));
        }


    //TODO: at this point uwoniha becomes uw + onih + a -- it should be u + wonih + a
    if (pronPrefix !== undefined) {
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

    if (wholeWord.tmpParse.endsWith("s")) {
        wholeWord.tmpParse = wholeWord.tmpParse.substring(0, wholeWord.tmpParse.length -1) + "h";
    } else /*if (!endsWithVowel(wholeWord.tmpParse))*/{
        // wholeWord.tmpParse += 'a';
    }

    // once parsed then try another lookup in the database
    var tmpWord = pronPrefix + wholeWord.tmpParse + VerbTenseLookup.get("PRESENT");
    // console.log("tmpWord " + tmpWord);
    tmpWord = tsalagiToSyllabary(tmpWord);
    values = await lookupWordInCED(tmpWord);

    // if there's a result in the database put the information into the display object
    if (values.length > 0) {
        // TODO:  also take broken down word and put that data into a display object
        displayObject = valueFound(values, displayObject);
    } else {// TODO: if there's no result then DO SOMETHING ELSE
        console.log("still could not find a result");
    }
    }

    return displayObject;
}
