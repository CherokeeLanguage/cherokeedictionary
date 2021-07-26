function WholeWord() {
  var gSobject = gs.init('WholeWord');
  gSobject.clazz = { name: 'net.cherokeedictionary.WholeWord', simpleName: 'WholeWord'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.phonetic = "";
  gSobject.syllabary = "";
  gSobject.root_phonetic = "";
  gSobject.root_syllabary = "";
  gSobject.definitions = gs.list([]);
  gSobject.root_ending = "";
  gSobject.constructedVerbToLookup = "";
  gSobject.verbTense = TenseHolder();
  gSobject.verbTenseSuffixes = gs.list([]);
  gSobject.initialPrefixes = gs.list([]);
  gSobject.pronounPrefixes = gs.list([]);
  gSobject.reflexive = false;
  gSobject.nonFinalSuffixes = gs.list([]);
  gSobject.finalSuffixes = gs.list([]);
  gSobject.tmpParse = "";
  gSobject.lookupOptions = gs.list([]);
  gSobject['toString'] = function(it) {
    return gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus((gs.plus("WholeWord{", "phonetic=")), gSobject.phonetic)), ", syllabary=")), gSobject.syllabary)), ", root_phonetic=")), gSobject.root_phonetic)), ", root_syllabary=")), gSobject.root_syllabary)), ", definitions=")), gSobject.definitions)), ", root_ending=")), gSobject.root_ending)), ", constructedVerbToLookup=")), gSobject.constructedVerbToLookup)), ", verbTense=")), gSobject.verbTense)), ", verbTenseSuffixes=")), gSobject.verbTenseSuffixes)), ", initialPrefixes=")), gSobject.initialPrefixes)), ", pronounPrefixes=")), gSobject.pronounPrefixes)), ", reflexive=")), gSobject.reflexive)), ", nonFinalSuffixes=")), gSobject.nonFinalSuffixes)), ", finalSuffixes=")), gSobject.finalSuffixes)), "}");
   }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
