const Tense = {
    PRESENT: "PRESENT",
    FUTURE_IMPERATIVE: "FUTURE_IMPERATIVE",
    INFINITIVE: "INFINITIVE",
    HABITUAL: "HABITUAL",
    PLUPERFECT: "PLUPERFECT",
    FUTURE_PROGRESSIVE: "FUTURE_PROGRESSIVE",
    REPORTATIVE: "REPORTATIVE",
    PAST: "PAST",
    FUTURE: "FUTURE",
    PRE_INCIPIENT: "PRE_INCIPIENT"
};

const VerbTense = new Map();
VerbTense.set("esdi" , Tense.FUTURE_PROGRESSIVE);
VerbTense.set("idi" , Tense.PRE_INCIPIENT);
VerbTense.set("vi" , Tense.FUTURE_IMPERATIVE);
// VerbTense.set("a" , Tense.PRESENT);
VerbTense.set("di" , Tense.INFINITIVE);
VerbTense.set("oi" , Tense.HABITUAL);
VerbTense.set("oi" , Tense.PLUPERFECT);
// VerbTense.set("esdi" , Tense.FUTURE_PROGRESSIVE);
VerbTense.set("ei" , Tense.REPORTATIVE);
VerbTense.set("vi" , Tense.PAST);
VerbTense.set("i" , Tense.FUTURE);
VerbTense.set("a" , Tense.PRESENT);
// VerbTense.set("idi" , Tense.PRE_INCIPIENT);

const FinalEndings = new Map();
FinalEndings.set("dv", "focus");
FinalEndings.set("dina", "concessiveImperative");
FinalEndings.set("isi", "orelse");
FinalEndings.set("hv", "but");
FinalEndings.set("na", "andWhatIf");
FinalEndings.set("ke", "orInterrogative");
FinalEndings.set("tsu", "interrogative");
FinalEndings.set("sgo", "interrogative2");
FinalEndings.set("quu", "onlyJustStill");

const NonFinalEndings = new Map();
NonFinalEndings.set("dohdan", "unintentional");
NonFinalEndings.set("idol", "around");
NonFinalEndings.set("ilo", "repetitive");
NonFinalEndings.set("ilos", "repetitive");
NonFinalEndings.set("ilots", "repetitive");
NonFinalEndings.set("ihl", "comeTo");
NonFinalEndings.set("dan", "instrumentalCausative");
NonFinalEndings.set("ohn", "completive");
NonFinalEndings.set("is", "duplicative/become");
NonFinalEndings.set("el", "dativeBenefactive");
NonFinalEndings.set("gi", "reversive");
NonFinalEndings.set("g", "progressive");
NonFinalEndings.set("e", "atIntervals");
NonFinalEndings.set("y", "reversive");
NonFinalEndings.set("e", "reversive");
// NonFinalEndings.set("g", "progressive");
// NonFinalEndings.set("ihl", "comeTo");
// NonFinalEndings.set("e", "atIntervals");
// NonFinalEndings.set("idol", "around");
// NonFinalEndings.set("el", "dativeBenefactive");
// NonFinalEndings.set("ohn", "completive");
// NonFinalEndings.set("dohdan", "unintentional");
// NonFinalEndings.set("dan", "instrumentalCausative");
// NonFinalEndings.set("ilo", "repetitive");
// NonFinalEndings.set("is", "duplicative/become");
// NonFinalEndings.set("y", "reversive");
// NonFinalEndings.set("e", "reversive");
// NonFinalEndings.set("gi", "reversive");

verbTense = Tense.PRESENT;

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

var VowelPrefixes = new Map();
VowelPrefixes.set("gatsiy", "SG1PL3AN");
VowelPrefixes.set("dogin", "DL1EXCLPL3INB");
VowelPrefixes.set("dugin", "DL1INCLPL3INB");
VowelPrefixes.set("gahiy", "SG2PL3AN");
VowelPrefixes.set("gegin", "PL3DL1INCL");
VowelPrefixes.set("gogin", "PL3DL1EXCL");
VowelPrefixes.set("isgiy", "PL2DL1EXCL");
VowelPrefixes.set("itsvy", "PL1EXCLSG2");
VowelPrefixes.set("daqu", "SG1PL3INB");
VowelPrefixes.set("desd", "DL2PL3IN");
VowelPrefixes.set("dosd", "DL1EXCLPL3IN");
VowelPrefixes.set("gesd", "PL3DL2");
VowelPrefixes.set("gosd", "DL1EXCLPL3AN");
VowelPrefixes.set("gvqu", "PL3SG1");
VowelPrefixes.set("ogin", "DL1EXCLSG3INB");
VowelPrefixes.set("sdvy", "DL1EXCLDL2");
VowelPrefixes.set("sgin", "DL2DL1EXCL");
VowelPrefixes.set("tsiy", "SG1SG3AN");
VowelPrefixes.set("aqu", "SG1SG3INB");
VowelPrefixes.set("dan", "PL3PL3IN");
VowelPrefixes.set("ded", "PL1INCLPL3IN");
VowelPrefixes.set("deg", "SG3PL3IN");
VowelPrefixes.set("deh", "SG2PL3IN");
VowelPrefixes.set("dets", "PL2PL3IN");
VowelPrefixes.set("den", "DL1INCLPL3IN");
VowelPrefixes.set("dog", "PL1EXCLPL3INB");
VowelPrefixes.set("dots", "PL1EXCLPL3IN");
VowelPrefixes.set("dug", "PL1INCLPL3INB");
VowelPrefixes.set("duts", "SG2PL3INB");
VowelPrefixes.set("dun", "PL3PL3INB");
VowelPrefixes.set("duw", "SG3PL3INB");
VowelPrefixes.set("esd", "DL2SG3AN");
VowelPrefixes.set("ged", "PL1INCLPL3AN");
VowelPrefixes.set("geg", "PL3PL1INCL");
VowelPrefixes.set("gets", "PL3PL2");
VowelPrefixes.set("gen", "DL1INCLPL3AN");
VowelPrefixes.set("gin", "DL1INCLSG3INB");
VowelPrefixes.set("gog", "PL3PL1EXCL");
VowelPrefixes.set("gots", "PL1EXCLPL3AN");
VowelPrefixes.set("gvy", "SG1SG2");
VowelPrefixes.set("hiy", "SG2SG3AN");
VowelPrefixes.set("osd", "DL1EXCLSG3IN");
VowelPrefixes.set("squ", "SG2SG1");
VowelPrefixes.set("an", "PL3SG3IN");
VowelPrefixes.set("ed", "PL1INCLSG3AN");
VowelPrefixes.set("ets", "PL2SG3AN");
VowelPrefixes.set("en", "DL1INCLSG3AN");
VowelPrefixes.set("id", "PL1INCLSG3IN");
VowelPrefixes.set("ig", "PL1INCLSG3INB");
VowelPrefixes.set("its", "PL2SG3IN");
VowelPrefixes.set("in", "DL1INCLSG3IN");
VowelPrefixes.set("og", "PL1EXCLSG3INB");
VowelPrefixes.set("ots", "PL1EXCLSG3IN");
VowelPrefixes.set("sd", "DL2SG3IN");
VowelPrefixes.set("un", "PL3SG3INB");
VowelPrefixes.set("uw", "SG3SG3INB");
VowelPrefixes.set("g", "SG3SG3IN");
VowelPrefixes.set("h", "SG2SG3IN");
VowelPrefixes.set("ts", "SG2SG3INB");

var ConsonantPrefixes = new Map();
ConsonantPrefixes.set("degini", "DL1INCLPL3INB");
ConsonantPrefixes.set("dogini", "DL1EXCLPL3INB");
ConsonantPrefixes.set("gegini", "PL3DL1INCL");
ConsonantPrefixes.set("gogini", "PL3DL1EXCL");
ConsonantPrefixes.set("desdi", "DL2PL3IN");
ConsonantPrefixes.set("detsa", "SG2PL3INB");
ConsonantPrefixes.set("detsi", "PL2PL3IN");
ConsonantPrefixes.set("dosdi", "DL1EXCLPL3IN");
ConsonantPrefixes.set("dotsi", "PL1EXCLPL3IN");
ConsonantPrefixes.set("gatsi", "SG1PL3AN");
ConsonantPrefixes.set("gesdi", "PL3DL2");
ConsonantPrefixes.set("getsi", "PL3PL2");
ConsonantPrefixes.set("gosdi", "DL1EXCLPL3AN");
ConsonantPrefixes.set("gotsi", "PL1EXCLPL3AN");
ConsonantPrefixes.set("ogini", "DL1EXCLSG3INB");
ConsonantPrefixes.set("sgini", "DL2DL1EXCL");
ConsonantPrefixes.set("dagi", "SG1PL3INB");
ConsonantPrefixes.set("dani", "PL3PL3IN");
ConsonantPrefixes.set("dedi", "PL1INCLPL3IN");
ConsonantPrefixes.set("degi", "SG3PL3IN");
ConsonantPrefixes.set("dehi", "SG2PL3IN");
ConsonantPrefixes.set("deni", "DL1INCLPL3IN");
ConsonantPrefixes.set("dogi", "PL1EXCLPL3INB");
ConsonantPrefixes.set("dugi", "PL1INCLPL3INB");
ConsonantPrefixes.set("duni", "PL3PL3INB");
ConsonantPrefixes.set("esdi", "DL2SG3AN");
ConsonantPrefixes.set("etsi", "PL2SG3AN");
ConsonantPrefixes.set("gahi", "SG2PL3AN");
ConsonantPrefixes.set("gedi", "PL1INCLPL3AN");
ConsonantPrefixes.set("gegi", "PL3PL1INCL");
ConsonantPrefixes.set("geni", "DL1INCLPL3AN");
ConsonantPrefixes.set("gini", "DL1INCLSG3INB");
ConsonantPrefixes.set("gogi", "PL3PL1EXCL");
ConsonantPrefixes.set("gvgi", "PL3SG1");
ConsonantPrefixes.set("isgi", "PL2DL1EXCL");
ConsonantPrefixes.set("itsi", "PL2SG3IN");
ConsonantPrefixes.set("itsv", "PL1EXCLSG2");
ConsonantPrefixes.set("osdi", "DL1EXCLSG3IN");
ConsonantPrefixes.set("otsi", "PL1EXCLSG3IN");
ConsonantPrefixes.set("agi", "SG1SG3INB");
ConsonantPrefixes.set("ani", "PL3SG3IN");
ConsonantPrefixes.set("edi", "PL1INCLSG3AN");
ConsonantPrefixes.set("eni", "DL1INCLSG3AN");
ConsonantPrefixes.set("idi", "PL1INCLSG3IN");
ConsonantPrefixes.set("igi", "PL1INCLSG3INB");
ConsonantPrefixes.set("ini", "DL1INCLSG3IN");
ConsonantPrefixes.set("ogi", "PL1EXCLSG3INB");
ConsonantPrefixes.set("sdi", "DL2SG3IN");
ConsonantPrefixes.set("sdv", "DL1EXCLDL2");
ConsonantPrefixes.set("sgi", "SG2SG1");
ConsonantPrefixes.set("tsa", "SG2SG3INB");
ConsonantPrefixes.set("tsi", "SG3SG2");
ConsonantPrefixes.set("uni", "PL3SG3INB");
ConsonantPrefixes.set("du", "SG3PL3INB");
ConsonantPrefixes.set("ga", "SG3SG3IN");
ConsonantPrefixes.set("gv", "SG1SG2");
ConsonantPrefixes.set("hi", "SG2SG3IN");
ConsonantPrefixes.set("u", "SG3SG3INB");

var InitialPrefixes = new Map();
InitialPrefixes.set("yi", "negative");
InitialPrefixes.set("tsi", "relative");
InitialPrefixes.set("wi", "away from speaker");
InitialPrefixes.set("ni", "lateral position, already");
InitialPrefixes.set("de", "plural object");
InitialPrefixes.set("da", "future/motion toward speaker");
InitialPrefixes.set("di", "distant position");
InitialPrefixes.set("i", "again");
InitialPrefixes.set("ga", "since");
InitialPrefixes.set("e", "distant imperative");

var ReflexivePrefix = new Map();