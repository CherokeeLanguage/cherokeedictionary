function FinalSuffixUtil() {
  var gSobject = gs.init('FinalSuffixUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.FinalSuffixUtil', simpleName: 'FinalSuffixUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.getFinalSuffixes = function(x0) { return FinalSuffixUtil.getFinalSuffixes(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
FinalSuffixUtil.getFinalSuffixes = function(wholeWord) {
  var finalSuffixesList = gs.list([]);
  var word = gs.gp(wholeWord,"phonetic");
  var foundAResult = true;
  while (gs.bool(foundAResult)) {
    foundAResult = false;
    var finalEndings = gs.gp(FinalEnding(),"FinalEndings");
    for (_i22 = 0, finalEnding = gs.mc(finalEndings,"keySet",[])[0]; _i22 < gs.mc(finalEndings,"keySet",[]).length; finalEnding = gs.mc(finalEndings,"keySet",[])[++_i22]) {
      var tmpEnding = gs.mc(finalEndings,"get",[finalEnding]);
      if (gs.mc(word,"endsWith",[finalEnding])) {
        foundAResult = true;
        gs.mc(finalSuffixesList,"push",[Explanation(finalEnding, "", "suffix", "", tmpEnding, "")]);
        word = gs.mc(word,"substring",[0, gs.minus(gs.mc(word,"size",[]), gs.mc(finalEnding,"size",[]))]);
       };
     };
   };
  gs.sp(wholeWord,"finalSuffixes",gs.mc(finalSuffixesList,"reverse",[]));
  gs.sp(wholeWord,"tmpParse",word);
  return wholeWord;
 }
