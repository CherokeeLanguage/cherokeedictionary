async function valueFound(values, wholeWord) {
    // TODO: what do we need from the dictionary entry?  definition,
    var vals = JSON.parse(values[0]);

    for (const value of vals) {
        wholeWord.definitions.push(value);
    }

    return wholeWord;
}

async function deconstruct(wholeWord, word) {
    wholeWord = getFinalSuffixes(wholeWord);

    wholeWord.lookupOptions.push(wholeWord.tmpParse);

    if (wholeWord.definitions.length === 0) {
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
        }
    }

    // let foundMatch = false;
    // if (wholeWord.definitions.length > 0) {
    //     wholeWord.definitions = [];
    //     console.log(JSON.stringify(wholeWord));
    //     for (const definition of wholeWord.definitions) {
    //         if (wholeWord.tmpParse === definition.entrya) {
    //             wholeWord.root_phonetic = wholeWord.tmpParse;
    //             wholeWord.root_syllabary = newTsalagiToSyllabary(wholeWord.tmpParse, false, true);
    //             foundMatch = true;
    //             break;
    //         }
    //     }
    // }
    //
    // if (!foundMatch) {
        //TODO: Need to make sure if we have a definition and it's a verb then we can continue OR if the definition is empty or undefined
        //      Repeat through the findings
        wholeWord = getVerbTenseSuffixes(wholeWord);

        // for (const tmpElementElement of wholeWord.verbTenseSuffixes) {
        //     wholeWord.verbTenseType = tmpElementElement.meaning;
        //     //TODO: also need to get verb tense ending here -- will need another map
        // }

        wholeWord = getNonFinalSuffixes(wholeWord);
        wholeWord = getInitialPrefixes(wholeWord);
    // }
        wholeWord = getPronominalPrefixes(wholeWord);

        //TODO: START HERE TO GET THE CONSONANT PREFIXES
        // wholeWord = getReflexivePrefix(wholeWord);
    // }

    wholeWord.lookupOptions.push(wholeWord.tmpParse);

    return wholeWord;
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
    }

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

            if (wholeWord.tmpParse.endsWith("h")) {
                wholeWord.root_ending = "h"
            } else if (wholeWord.tmpParse.endsWith("s")) {
                wholeWord.root_ending = "s"
            }

            if (wholeWord.root_ending) {
                wholeWord.tmpParse = wholeWord.tmpParse.substring(0, wholeWord.tmpParse.length - 1);
            }

            wholeWord.root_phonetic = wholeWord.tmpParse;
            wholeWord.root_syllabary = tsalagiToSyllabary(wholeWord.root_phonetic);

            console.log(wholeWord);

            //once parsed then try another lookup in the database
            var tmpWord = pronPrefix + wholeWord.tmpParse + VerbTenseLookup.get("PRESENT");
            wholeWord.lookupOptions.push(tmpWord);
            // console.log("tmpWord " + tmpWord);
            tmpWord = tsalagiToSyllabary(tmpWord);

            if (wholeWord.definitions.length === 0) {
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
