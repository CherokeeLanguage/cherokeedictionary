// phonetic = value of item in explanation
// syllabary = syllabic representation if available
// type = suffix, prefix, root, non-final suffix, final suffix, initial prefix, proniminal prefix, reflexive prefix
// function = pronoun, like focus,
// meaning = focus etc
function createExplanation(phonetic, syllabary, type, purpose, meaning, name) {
    var explanation = {
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
        // verbTenseSuffixes: [], // what are the tense options of the current word
        initialPrefixes: [], // what are the initial prefixes found
        pronounPrefixes: [], // what are the pronoun prefixes found
        reflexive: false, // was a reflexive prefix found
        nonFinalSuffixes: [], // what are the non final suffixes found
        finalSuffixes: [], // what are the final suffixes found
        lookupOptions: [] // a list of potential values to lookup in the database
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

function getPronPrefix(wholeWord) {
    var pronPrefixes = wholeWord && wholeWord.pronounPrefixes ? wholeWord.pronounPrefixes[0] : "";
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

        console.log("pron prefix " + pronPrefix);

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

