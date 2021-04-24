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

const VerbTenseLookup = new Map();
VerbTenseLookup.set("FUTURE_PROGRESSIVE",  "esdi");
VerbTenseLookup.set("PRE_INCIPIENT",  "idi");
VerbTenseLookup.set("FUTURE_IMPERATIVE",  "vi");
// VerbTenseLookup.set("a" ",  "PRESENT, );
VerbTenseLookup.set("INFINITIVE",  "di");
VerbTenseLookup.set("HABITUAL",  "oi");
VerbTenseLookup.set("PLUPERFECT",  "oi");
// VerbTenseLookup.set("esdi" ",  "FUTURE_PROGRESSIVE, );
VerbTenseLookup.set("REPORTATIVE",  "ei");
VerbTenseLookup.set("PAST",  "vi");
VerbTenseLookup.set("FUTURE",  "i");
VerbTenseLookup.set("PRESENT",  "a");

const FinalEndings = new Map();
FinalEndings.set("dv", "focus");
FinalEndings.set("dina", "concessiveImperative");
FinalEndings.set("isi", "orelse");
FinalEndings.set("hv", "but");
FinalEndings.set("na", "andWhatIf");
FinalEndings.set("ke", "orInterrogative");
FinalEndings.set("tsu", "interrogative");
FinalEndings.set("sgo", "interrogative2");
FinalEndings.set("gwu", "onlyJustStill");
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
VowelPrefixes.set("dagw", "SG1PL3INB");
VowelPrefixes.set("desd", "DL2PL3IN");
VowelPrefixes.set("dosd", "DL1EXCLPL3IN");
VowelPrefixes.set("gesd", "PL3DL2");
VowelPrefixes.set("gosd", "DL1EXCLPL3AN");
VowelPrefixes.set("gvqu", "PL3SG1");
VowelPrefixes.set("gvgw", "PL3SG1");
VowelPrefixes.set("ogin", "DL1EXCLSG3INB");
VowelPrefixes.set("sdvy", "DL1EXCLDL2");
VowelPrefixes.set("sgin", "DL2DL1EXCL");
VowelPrefixes.set("tsiy", "SG1SG3AN");
VowelPrefixes.set("aqu", "SG1SG3INB");
VowelPrefixes.set("agw", "SG1SG3INB");
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
VowelPrefixes.set("sgw", "SG2SG1");
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

var PronominalPrefixes = new Map();
PronominalPrefixes.set('SG1SG2', {preVowel:'gvy', preConsonant:'gv'});
PronominalPrefixes.set('SG1SG3AN', {preVowel:'tsiy', preConsonant:'tsi'});
PronominalPrefixes.set('SG1SG3IN', {preVowel:'g', preConsonant:'tsi'});
PronominalPrefixes.set('SG1DL2', {preVowel:'sdvy', preConsonant:'sdv'});
PronominalPrefixes.set('SG1PL2', {preVowel:'itsvy', preConsonant:'itsv'});
PronominalPrefixes.set('SG1PL3AN', {preVowel:'gatsiy', preConsonant:'gatsi'});
PronominalPrefixes.set('SG1PL3IN', {preVowel:'deg', preConsonant:'detsi'});
PronominalPrefixes.set('SG2SG1', {preVowel:'squ', preConsonant:'sgi'});
PronominalPrefixes.set('SG2SG3AN', {preVowel:'hiy', preConsonant:'hi'});
PronominalPrefixes.set('SG2SG3IN', {preVowel:'h', preConsonant:'hi'});
PronominalPrefixes.set('SG2DL1INCL', {preVowel:'sgin', preConsonant:'sgini'});
PronominalPrefixes.set('SG2PL1EXCL', {preVowel:'isgiy', preConsonant:'isgi'});
PronominalPrefixes.set('SG2PL3AN', {preVowel:'gahiy', preConsonant:'gahi'});
PronominalPrefixes.set('SG2PL3IN', {preVowel:'deh', preConsonant:'dehi'});
PronominalPrefixes.set('SG3SG1', {preVowel:'aqu', preConsonant:'agi'});
PronominalPrefixes.set('SG3SG2', {preVowel:'ts', preConsonant:'tsi'});
PronominalPrefixes.set('SG3SG3AN', {preVowel:'g', preConsonant:'ga'});
PronominalPrefixes.set('SG3SG3IN', {preVowel:'g', preConsonant:'ga'});
PronominalPrefixes.set('SG3DL1INCL', {preVowel:'gin', preConsonant:'gini'});
PronominalPrefixes.set('SG3DL1EXCL', {preVowel:'ogin', preConsonant:'ogini'});
PronominalPrefixes.set('SG3PL1INCL', {preVowel:'ig', preConsonant:'igi'});
PronominalPrefixes.set('SG3PL1EXCL', {preVowel:'og', preConsonant:'ogi'});
PronominalPrefixes.set('SG3DL2', {preVowel:'sd', preConsonant:'sdi'});
PronominalPrefixes.set('SG3PL2', {preVowel:'its', preConsonant:'itsi'});
PronominalPrefixes.set('SG3PL3AN', {preVowel:'deg', preConsonant:'degi'});
PronominalPrefixes.set('SG3PL3IN', {preVowel:'deg', preConsonant:'degi'});
PronominalPrefixes.set('DL1INCLSG3AN', {preVowel:'en', preConsonant:'eni'});
PronominalPrefixes.set('DL1INCLSG3IN', {preVowel:'in', preConsonant:'ini'});
PronominalPrefixes.set('DL1INCLPL3AN', {preVowel:'gen', preConsonant:'geni'});
PronominalPrefixes.set('DL1INCLPL3IN', {preVowel:'den', preConsonant:'deni'});
PronominalPrefixes.set('DL1EXCLSG2', {preVowel:'sdvy', preConsonant:'sdv'});
PronominalPrefixes.set('DL1EXCLSG3AN', {preVowel:'osd', preConsonant:'osdi'});
PronominalPrefixes.set('DL1EXCLSG3IN', {preVowel:'osd', preConsonant:'osdi'});
PronominalPrefixes.set('DL1EXCLDL2', {preVowel:'sdvy', preConsonant:'sdv'});
PronominalPrefixes.set('DL1EXCLPL2', {preVowel:'its', preConsonant:'itsi'});
PronominalPrefixes.set('DL1EXCLPL3AN', {preVowel:'gosd', preConsonant:'gosdi'});
PronominalPrefixes.set('DL1EXCLPL3IN', {preVowel:'dosd', preConsonant:'dosdi'});
PronominalPrefixes.set('PL1INCLSG3AN', {preVowel:'ed', preConsonant:'edi'});
PronominalPrefixes.set('PL1INCLSG3IN', {preVowel:'id', preConsonant:'idi'});
PronominalPrefixes.set('PL1INCLPL3AN', {preVowel:'ged', preConsonant:'gedi'});
PronominalPrefixes.set('PL1INCLPL3IN', {preVowel:'ded', preConsonant:'dedi'});
PronominalPrefixes.set('PL1EXCLSG2', {preVowel:'itsvy', preConsonant:'itsv'});
PronominalPrefixes.set('PL1EXCLSG3AN', {preVowel:'ots', preConsonant:'otsi'});
PronominalPrefixes.set('PL1EXCLSG3IN', {preVowel:'ots', preConsonant:'otsi'});
PronominalPrefixes.set('PL1EXCLPL3AN', {preVowel:'gots', preConsonant:'gotsi'});
PronominalPrefixes.set('PL1EXCLPL3IN', {preVowel:'dots', preConsonant:'dotsi'});
PronominalPrefixes.set('DL2SG1', {preVowel:'sgin', preConsonant:'sgini'});
PronominalPrefixes.set('DL2SG3AN', {preVowel:'esd', preConsonant:'esdi'});
PronominalPrefixes.set('DL2SG3IN', {preVowel:'sd', preConsonant:'sdi'});
PronominalPrefixes.set('DL2DL1EXCL', {preVowel:'sgin', preConsonant:'sgini'});
PronominalPrefixes.set('DL2PL1EXCL', {preVowel:'isgiy', preConsonant:'isgi'});
PronominalPrefixes.set('DL2PL3AN', {preVowel:'gesd', preConsonant:'gesdi'});
PronominalPrefixes.set('DL2PL3IN', {preVowel:'desd', preConsonant:'desdi'});
PronominalPrefixes.set('PL2SG1', {preVowel:'isgiy', preConsonant:'isgi'});
PronominalPrefixes.set('PL2SG3AN', {preVowel:'ets', preConsonant:'etsi'});
PronominalPrefixes.set('PL2SG3IN', {preVowel:'its', preConsonant:'itsi'});
PronominalPrefixes.set('PL2DL1EXCL', {preVowel:'isgiy', preConsonant:'isgi'});
PronominalPrefixes.set('PL2PL3AN', {preVowel:'gets', preConsonant:'getsi'});
PronominalPrefixes.set('PL2PL3IN', {preVowel:'dets', preConsonant:'detsi'});
PronominalPrefixes.set('PL3SG1', {preVowel:'gvqu', preConsonant:'gvgi'});
PronominalPrefixes.set('PL3SG2', {preVowel:'gets', preConsonant:'getsi'});
PronominalPrefixes.set('PL3SG3AN', {preVowel:'an', preConsonant:'ani'});
PronominalPrefixes.set('PL3SG3IN', {preVowel:'an', preConsonant:'ani'});
PronominalPrefixes.set('PL3DL1INCL', {preVowel:'gegin', preConsonant:'gegini'});
PronominalPrefixes.set('PL3DL1EXCL', {preVowel:'gogin', preConsonant:'gogini'});
PronominalPrefixes.set('PL3PL1INCL', {preVowel:'geg', preConsonant:'gegi'});
PronominalPrefixes.set('PL3PL1EXCL', {preVowel:'gog', preConsonant:'gogi'});
PronominalPrefixes.set('PL3DL2', {preVowel:'gesd', preConsonant:'gesdi'});
PronominalPrefixes.set('PL3PL2', {preVowel:'gets', preConsonant:'getsi'});
PronominalPrefixes.set('PL3PL3AN', {preVowel:'dan', preConsonant:'dani'});
PronominalPrefixes.set('PL3PL3IN', {preVowel:'dan', preConsonant:'dani'});
PronominalPrefixes.set('SG1SG3INB', {preVowel:'aqu', preConsonant:'agi'});
PronominalPrefixes.set('SG2SG3INB', {preVowel:'ts', preConsonant:'tsa'});
PronominalPrefixes.set('SG3SG3INB', {preVowel:'uw', preConsonant:'u'});
PronominalPrefixes.set('DL1INCLSG3INB', {preVowel:'gin', preConsonant:'gini'});
PronominalPrefixes.set('PL1INCLSG3INB', {preVowel:'ig', preConsonant:'igi'});
PronominalPrefixes.set('DL1EXCLSG3INB', {preVowel:'ogin', preConsonant:'ogini'});
PronominalPrefixes.set('PL1EXCLSG3INB', {preVowel:'og', preConsonant:'ogi'});
PronominalPrefixes.set('PL3SG3INB', {preVowel:'un', preConsonant:'uni'});
PronominalPrefixes.set('SG1PL3INB', {preVowel:'daqu', preConsonant:'dagi'});
PronominalPrefixes.set('SG2PL3INB', {preVowel:'duts', preConsonant:'detsa'});
PronominalPrefixes.set('SG3PL3INB', {preVowel:'duw', preConsonant:'du'});
PronominalPrefixes.set('DL1INCLPL3INB', {preVowel:'dugin', preConsonant:'degini'});
PronominalPrefixes.set('PL1INCLPL3INB', {preVowel:'dug', preConsonant:'dugi'});
PronominalPrefixes.set('DL1EXCLPL3INB', {preVowel:'dogin', preConsonant:'dogini'});
PronominalPrefixes.set('PL1EXCLPL3INB', {preVowel:'dog', preConsonant:'dogi'});
PronominalPrefixes.set('PL3PL3INB', {preVowel:'dun', preConsonant:'duni'});
