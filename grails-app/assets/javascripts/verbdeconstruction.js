async function deconstruct(wholeWord, word) {
    wholeWord = getFinalSuffixes(wholeWord);

    //check word against dictionary
    // look up in database
    let tmpValSyllabary = tsalagiToSyllabary(wholeWord.tmpParse);
    let values = await lookupWordInCED(tmpValSyllabary);

    // if there's a result then we are going to put that information into the display object
    if (values.length > 0) {
        console.log("second value thing found");
        // wholeWord.syllabary = tmpValSyllabary;
        // TODO: we found an item in the database - we need to create a new display object that we're going to send to the user or the page
        wholeWord = await valueFound(values, wholeWord);
        // console.log("displayObject 2nd " + JSON.stringify(wholeWord));
    } else {
        //TODO: Need to make sure if we have a definition and it's a verb then we can continue OR if the definition is empty or undefined
        //      Repeat through the findings
        wholeWord = getVerbTenseSuffixes(wholeWord);

        for (const tmpElementElement of wholeWord.verbTenseSuffixes) {
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

function getPronPrefix(wholeWord) {
    let pronPrefixes = wholeWord && wholeWord.pronounPrefixes ? wholeWord.pronounPrefixes[0] : "";
    let pronPrefix = undefined;

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
        if (pronPrefixes && pronPrefix.startsWith("uw")) {
            if (wholeWord.pronounPrefixes[0].endsWith("B")) {
                pronPrefix = "ga";
            }

            wholeWord.tmpParse = "w" + wholeWord.tmpParse;
        } else if (pronPrefixes && pronPrefix.startsWith("u")) {
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
    let wholeWord = newWholeWord(word, isSyllabary);

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
                let pronPrefix = getPronPrefix();

                //other endings need to be added h, l, s, d, and others
                if (wholeWord.tmpParse.endsWith("s")) {
                    wholeWord.tmpParse = wholeWord.tmpParse.substring(0, wholeWord.tmpParse.length -1) + "h";
                }

                //once parsed then try another lookup in the database
                let tmpWord = pronPrefix + wholeWord.tmpParse + VerbTenseLookup.get("PRESENT");
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

async function valueFound(values, wholeWord) {
    // TODO: what do we need from the dictionary entry?  definition,
    let vals = JSON.parse(values[0]);

    for (const value of vals) {
        wholeWord.definitions.push(value);
    }

    return wholeWord;
}

// do lookup and if there's a definition look for the part of speech then if it's a verb deconstruct the rest of the way
// if it's a noun or adjective pull the final suffix and then check for pronominal prefixes
// if it has a definition do something like remove the syllabary value from the lookup value to figure out what might be the actual value
//  like agasgadv looked up comes back with agasga then remove agasga from agasgadv providing -dv as the last part