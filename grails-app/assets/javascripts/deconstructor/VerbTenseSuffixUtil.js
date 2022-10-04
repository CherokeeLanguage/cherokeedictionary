function VerbTenseSuffixUtil() {
  var gSobject = gs.init('VerbTenseSuffixUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.VerbTenseSuffixUtil', simpleName: 'VerbTenseSuffixUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject['getVerbTenseSuffixes'] = function(wholeWord) {
    var word = gs.gp(wholeWord,"tmpParse");
    var verbTenses = gs.gp(VerbTense(),"VerbTenses");
    for (_i6 = 0, verbTense = gs.mc(verbTenses,"keySet",[])[0]; _i6 < gs.mc(verbTenses,"keySet",[]).length; verbTense = gs.mc(verbTenses,"keySet",[])[++_i6]) {
      if (gs.mc(word,"endsWith",[verbTense])) {
        var verbTenseItem = gs.mc(verbTenses,"get",[verbTense]);
        gs.sp(wholeWord,"verbTense",TenseHolder(gs.map().add("tense",verbTenseItem).add("ending",verbTense)));
        word = gs.mc(word,"substring",[0, gs.minus(gs.mc(word,"size",[]), gs.mc(verbTense,"size",[]))]);
       };
     };
    gs.sp(wholeWord,"tmpParse",word);
    return wholeWord;
   }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
