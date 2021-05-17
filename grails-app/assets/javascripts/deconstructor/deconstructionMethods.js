var verbTense = Tense.PRESENT;

function getFinalSuffixes(wholeWord) {
    //iterate over final endings to remove each one from top to bottom right to left
    var finalSuffixesList = [];
    var word = wholeWord.phonetic;

    var foundAResult = true;
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

    wholeWord.finalSuffixes = finalSuffixesList.reverse();

    wholeWord.tmpParse = word;

    return wholeWord;
}

//TODO: what if there are multiples?  return both?
function getVerbTenseSuffixes(wholeWord) {
    var word = wholeWord.tmpParse;
    for (const verbTense of VerbTense.keys()) {
        if (word.endsWith(verbTense)) {
            var verbTenseItem = VerbTense.get(verbTense);
            wholeWord.verbTense = {tense: verbTenseItem, ending:verbTense};
            word = word.substr(0, word.length - verbTense.length);
        }
    }

    // wholeWord.verbTenseSuffixes = verbTenseSuffixesList;
    wholeWord.tmpParse = word;

    return wholeWord;
}

function getNonFinalSuffixes(wholeWord) {
    var word = wholeWord.tmpParse;

    var nonFinalSuffixesList = [];

    var foundAResult = true;
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
    var initialPrefixList = [];
    var tmp = wholeWord.tmpParse;
    var foundAResult = true;
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
    var pronominalPrefixList = [];

    var pronSize = 0;
    var tmp = wholeWord.tmpParse;
    for (const consonantPrefix of ConsonantPrefixes.keys()) {
        if (tmp.startsWith(consonantPrefix)) {
            if (consonantPrefix.length > pronSize) {
                pronSize = consonantPrefix.length;
            }

            pronominalPrefixList.push(ConsonantPrefixes.get(consonantPrefix));
        }
    }

    if (pronSize > 0) {
        tmp = tmp.substring(pronSize);
        pronSize = 0;
    } else {
        for (const vowelPrefix of VowelPrefixes.keys()) {
            if (tmp.startsWith(vowelPrefix)) {
                if (vowelPrefix.length > pronSize) {
                    pronSize = vowelPrefix.length;
                }

                pronominalPrefixList.push(VowelPrefixes.get(vowelPrefix));
            }
        }
    }

    if (pronSize > 0) {
        tmp = tmp.substring(pronSize);
    }

    //TODO: There's another to look through
    // if (pronSize === 0 ) {
    //     for (const vowelPrefix of VowelPrefixes.keys()) {
    //         if (tmp.startsWith(vowelPrefix)) {
    //             if (vowelPrefix.length > pronSize) {
    //                 pronSize = vowelPrefix.length;
    //             }
    //
    //             pronominalPrefixList.push(VowelPrefixes.get(vowelPrefix));
    //         }
    //     }
    // }


    wholeWord.tmpParse = tmp;

    wholeWord.pronounPrefixes = pronominalPrefixList;

    wholeWord.root_phonetic = tmp;
    wholeWord.root_syllabary = newTsalagiToSyllabary(tmp, false, true);

    return wholeWord;
}

function getReflexivePrefix(wholeWord) {
    return wholeWord;
}
