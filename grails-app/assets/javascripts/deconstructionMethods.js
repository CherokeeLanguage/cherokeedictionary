function startsWithVowel(word) {
    return word.substring(0, 1).startsWith('a') || word.substring(0, 1).startsWith('e') || word.substring(0, 1).startsWith('i')  || word.substring(0, 1).startsWith('o') || word.substring(0, 1).startsWith('u') || word.substring(0, 1).startsWith('v');
}

function endsWithVowel(word) {
    return word.endsWith('a') || word.endsWith('e') || word.endsWith('i')  || word.endsWith('o') || word.endsWith('u') || word.endsWith('v');
}

function newWholeWord(word, isSyllabary) {
    let wholeWord = {
        phonetic: "", // phonetic transliteration of original syllabary lookup
        syllabary: "", // original word to look up
        root_phonetic: "", // phonetic root of the word as broken down
        root_syllabary: "", // syllabary root of the word as broken down - if the root ending contains a phonetic "letter" then it will appear as syllabary+phonetic letter
        definitions: [], // all definitions found in the database
        root_ending: "", // what the root ending is - phonetic only
        constructedVerbToLookup: "", // if this word is a verb then this takes the third person prefix type (A or B), root, root ending, present tense to give a lookup so uwonisvi (third past) would become gawoniha (third present) and is then
        verbTense: {tense: "", ending: ""}, // what is the tense of the current word
        verbTenseSuffixes: [], // what are the tense options of the current word
        initialPrefixes: [], // what are the initial prefixes found
        pronounPrefixes: [], // what are the pronoun prefixes found
        reflexive: false, // was a reflexive prefix found
        nonFinalSuffixes: [], // what are the non final suffixes found
        finalSuffixes: [] // what are the final suffixes found
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


let verbTense = Tense.PRESENT;

// phonetic = value of item in explanation
// syllabary = syllabic representation if available
// type = suffix, prefix, root, non-final suffix, final suffix, initial prefix, proniminal prefix, reflexive prefix
// function = pronoun, like focus,
// meaning = focus etc
function createExplanation(phonetic, syllabary, type, purpose, meaning, name) {
    let explanation = {
        phonetic: phonetic,
        syllabary: syllabary,
        meaning: meaning,
        purpose: purpose,
        name: name
        // morpheme_romanized: "d",
        // morpheme_syllabary: "XX",
        // morpheme_name: "prefix",
        // morpheme_function: "plurality",
        // morpheme_meaning: "many",
    };

    return explanation;
}

function getFinalSuffixes(wholeWord) {
    //iterate over final endings to remove each one from top to bottom right to left
    let finalSuffixesList = [];
    let word = wholeWord.phonetic;

    let foundAResult = true;
    while(foundAResult) {
        foundAResult = false;

        for (const finalEnding of FinalEndings.keys()) {
            var tmpEnding = FinalEndings.get(finalEnding);
            if (word.endsWith(finalEnding)) {
                foundAResult = true;
                finalSuffixesList.push(createExplanation(finalEnding, '', 'suffix', '', tmpEnding, ''));
                word = word.substr(0, word.length - finalEnding.length);
            }
        }
    }

    wholeWord.finalSuffixes = finalSuffixesList;

    wholeWord.tmpParse = word;

    return wholeWord;
}

//TODO: what if there are multiples?  return both?
function getVerbTenseSuffixes(wholeWord) {
    let verbTenseSuffixesList = []
    let word = wholeWord.tmpParse;
    for (const verbTense of VerbTense.keys()) {
        if (word.endsWith(verbTense)) {
            let verbTenseItem = VerbTense.get(verbTense);
            let explanation = createExplanation(verbTense, '', 'suffix', '', verbTenseItem, '');
            verbTenseSuffixesList.push(explanation);
            word = word.substr(0, word.length - verbTense.length);
        }
    }

    wholeWord.verbTenseSuffixes = verbTenseSuffixesList;
    wholeWord.tmpParse = word;

    return wholeWord;
}

function getNonFinalSuffixes(wholeWord) {
    let word = wholeWord.tmpParse;

    let nonFinalSuffixesList = [];

    let foundAResult = true;
    while(foundAResult) {
        foundAResult = false;
        for (const nonFinalEnding of NonFinalEndings.keys()) {
            if (word.endsWith(nonFinalEnding)) {
                //wonis is ok but doesn't have a nonfinal ending - wonisis does have a nonfinal ending
                //todo: find examples of this in the wild where it's possible that there are other conflicts - add to test implementations
                if (nonFinalEnding === "is") {
                    if (word.split(nonFinalEnding).length -1 > 1) {
                        foundAResult = true;
                        word = word.substring(0, word.length - nonFinalEnding.length);
                        nonFinalSuffixesList.push(NonFinalEndings.get(nonFinalEnding));
                    }
                } else {
                    foundAResult = true;
                    word = word.substring(0, word.length - nonFinalEnding.length);
                    nonFinalSuffixesList.push(NonFinalEndings.get(nonFinalEnding));
                }
            }
        }
    }

    wholeWord.tmpParse = word;

    wholeWord.nonFinalSuffixes = nonFinalSuffixesList;

    return wholeWord;
}

function getInitialPrefixes(wholeWord) {
    let initialPrefixList = [];
    let tmp = wholeWord.tmpParse;
    let foundAResult = true;
    while(foundAResult) {
        foundAResult = false;

        for (const initialPrefix of InitialPrefixes.keys()) {
            if (tmp.startsWith(initialPrefix)) {
                if (initialPrefix === "tsi") {
                    if (tmp.split(initialPrefix).length - 1 > 1) {
                        foundAResult = true;
                        tmp = tmp.substring(initialPrefix.length);
                        initialPrefixList.push(InitialPrefixes.get(initialPrefix));
                    }
                } else if (initialPrefix === "ga") {
                    if (tmp.split(initialPrefix).length - 1 > 1) {
                        foundAResult = true;
                        tmp = tmp.substring(initialPrefix.length);
                        initialPrefixList.push(InitialPrefixes.get(initialPrefix));
                    }
                } else {
                    foundAResult = true;
                    tmp = tmp.substring(initialPrefix.length);
                    initialPrefixList.push(InitialPrefixes.get(initialPrefix));
                }
            }
        }
    }

    wholeWord.tmpParse = tmp;

    wholeWord.initialPrefixes = initialPrefixList;

    return wholeWord;
}

function getPronominalPrefixes(wholeWord) {
    let pronominalPrefixList = [];

    let pronSize = 0;
    let tmp = wholeWord.tmpParse;

    for (const consonantPrefix of ConsonantPrefixes.keys()) {
        if (tmp.startsWith(consonantPrefix)) {
            if (consonantPrefix.length > pronSize) {
                pronSize = consonantPrefix.length;
            }

            pronominalPrefixList.push(ConsonantPrefixes.get(consonantPrefix));
        }
    }

    tmp = tmp.substring(pronSize);
    if (pronSize === 0 ) {
        for (const vowelPrefix of VowelPrefixes.keys()) {
            if (tmp.startsWith(vowelPrefix)) {
                if (vowelPrefix.length > pronSize) {
                    pronSize = vowelPrefix.length;
                }

                pronominalPrefixList.push(VowelPrefixes.get(vowelPrefix));
            }
        }
    }

    wholeWord.tmpParse = tmp;

    wholeWord.pronounPrefixes = pronominalPrefixList;

    return wholeWord;
}

function getReflexivePrefix(wholeWord) {
    return wholeWord;
}
