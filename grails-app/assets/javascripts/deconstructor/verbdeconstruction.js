function VerbDeconstruction() {
  var gSobject = gs.init('VerbDeconstruction');
  gSobject.clazz = { name: 'net.cherokeedictionary.VerbDeconstruction', simpleName: 'VerbDeconstruction'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.process = function(x0,x1) { return VerbDeconstruction.process(x0,x1); }
  gSobject.valueFound = function(x0,x1) { return VerbDeconstruction.valueFound(x0,x1); }
  gSobject.deconstruct = function(x0) { return VerbDeconstruction.deconstruct(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
VerbDeconstruction.process = function(word, isSyllabary) {
  if (isSyllabary === undefined) isSyllabary = true;
  var wholeWord = gs.execStatic(WholeWordFactory,'create', this,[word, isSyllabary]);
  var values = gs.execStatic(AjaxCall,'lookupWordInCED', this,[word]);
  if (gs.mc(values,"size",[]) > 0) {
    gs.println("first value thing found");
    wholeWord = VerbDeconstruction.valueFound(values, wholeWord);
   };
  wholeWord = VerbDeconstruction.deconstruct(wholeWord);
  if (!gs.bool(gs.gp(wholeWord,"definitions"))) {
    if ((gs.bool(gs.gp(wholeWord,"pronounPrefixes"))) && (gs.mc(gs.gp(wholeWord,"pronounPrefixes"),"size",[]) > 0)) {
      var pronPrefix = gs.mc(PronounPrefixUtility(),"getPronPrefix",[wholeWord]);
      if (gs.mc(gs.gp(wholeWord,"tmpParse"),"endsWith",["h"])) {
        gs.sp(wholeWord,"root_ending","h");
       } else {
        if (gs.mc(gs.gp(wholeWord,"tmpParse"),"endsWith",["s"])) {
          gs.sp(wholeWord,"root_ending","s");
         };
       };
      if (gs.bool(gs.gp(wholeWord,"root_ending"))) {
        gs.sp(wholeWord,"tmpParse",gs.mc(gs.gp(wholeWord,"tmpParse"),"substring",[0, gs.minus(gs.mc(gs.gp(wholeWord,"tmpParse"),"size",[]), 1)]));
       };
      gs.sp(wholeWord,"root_phonetic",gs.gp(wholeWord,"tmpParse"));
      gs.sp(wholeWord,"root_syllabary",gs.execStatic(SyllabaryUtil,'newTsalagiToSyllabary', this,[gs.gp(wholeWord,"root_phonetic"), false, true]));
      gs.println(wholeWord);
      var tmpWord = gs.plus((gs.plus(pronPrefix, gs.gp(wholeWord,"tmpParse"))), gs.mc(gs.gp(VerbTenseLookup(),"VerbTenseLookup"),"get",["PRESENT"]));
      gs.mc(gs.gp(wholeWord,"lookupOptions"),"push",[tmpWord]);
      tmpWord = gs.execStatic(SyllabaryUtil,'newTsalagiToSyllabary', this,[tmpWord, false, true]);
      if (gs.equals(gs.mc(gs.gp(wholeWord,"definitions"),"size",[]), 0)) {
        values = gs.execStatic(AjaxCall,'lookupWordInCED', this,[tmpWord]);
        if (gs.mc(values,"size",[]) > 0) {
          wholeWord = VerbDeconstruction.valueFound(values, wholeWord);
         } else {
          gs.println("still could not find a result");
         };
       };
     };
   };
  return wholeWord;
 }
VerbDeconstruction.valueFound = function(values, wholeWord) {
  var vals = values[0];
  for (_i0 = 0, value = vals[0]; _i0 < vals.length; value = vals[++_i0]) {
    gs.mc(gs.gp(wholeWord,"definitions"),"push",[value]);
   };
  return wholeWord;
 }
VerbDeconstruction.deconstruct = function(wholeWord) {
  wholeWord = gs.execStatic(FinalSuffixUtil,'getFinalSuffixes', this,[wholeWord]);
  gs.mc(gs.gp(wholeWord,"lookupOptions"),"push",[gs.gp(wholeWord,"tmpParse")]);
  if (gs.equals(gs.mc(gs.gp(wholeWord,"definitions"),"size",[]), 0)) {
    var tmpValSyllabary = gs.execStatic(SyllabaryUtil,'newTsalagiToSyllabary', this,[gs.gp(wholeWord,"tmpParse"), false, true]);
    var values = gs.execStatic(AjaxCall,'lookupWordInCED', this,[tmpValSyllabary]);
    if (gs.mc(values,"size",[]) > 0) {
      gs.println("second value thing found");
      gs.sp(wholeWord,"syllabary",tmpValSyllabary);
      wholeWord = VerbDeconstruction.valueFound(values, wholeWord);
     };
   };
  wholeWord = gs.mc(VerbTenseSuffixUtil(),"getVerbTenseSuffixes",[wholeWord]);
  wholeWord = gs.execStatic(NonFinalSuffixUtil,'getNonFinalSuffixes', this,[wholeWord]);
  wholeWord = gs.execStatic(InitialPrefixesUtil,'getInitialPrefixes', this,[wholeWord]);
  wholeWord = gs.execStatic(PronominalPrefixesUtil,'getPronominalPrefixes', this,[wholeWord]);
  gs.mc(gs.gp(wholeWord,"lookupOptions"),"push",[gs.gp(wholeWord,"tmpParse")]);
  return wholeWord;
 }
