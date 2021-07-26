function NonFinalSuffixUtil() {
  var gSobject = gs.init('NonFinalSuffixUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.NonFinalSuffixUtil', simpleName: 'NonFinalSuffixUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.getNonFinalSuffixes = function(x0) { return NonFinalSuffixUtil.getNonFinalSuffixes(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
NonFinalSuffixUtil.getNonFinalSuffixes = function(wholeWord) {
  var word = gs.gp(wholeWord,"tmpParse");
  var nonFinalSuffixesList = gs.list([]);
  var foundAResult = true;
  while (gs.bool(foundAResult)) {
    foundAResult = false;
    var nonFinalEndings = gs.gp(NonFinalEnding(),"NonFinalEndings");
    for (_i24 = 0, nonFinalEnding = gs.mc(nonFinalEndings,"keySet",[])[0]; _i24 < gs.mc(nonFinalEndings,"keySet",[]).length; nonFinalEnding = gs.mc(nonFinalEndings,"keySet",[])[++_i24]) {
      if (gs.mc(word,"endsWith",[nonFinalEnding])) {
        if (gs.equals(nonFinalEnding, "is")) {
          if ((gs.minus(gs.mc(gs.mc(word,"split",[nonFinalEnding]),"size",[]), 1)) > 1) {
            foundAResult = true;
            word = gs.mc(word,"substring",[0, gs.minus(gs.mc(word,"size",[]), gs.mc(nonFinalEnding,"size",[]))]);
            gs.mc(nonFinalSuffixesList,"push",[Affix(gs.map().add("affix",nonFinalEnding).add("meaning",gs.mc(nonFinalEndings,"get",[nonFinalEnding])))]);
           };
         } else {
          foundAResult = true;
          word = gs.mc(word,"substring",[0, gs.minus(gs.mc(word,"size",[]), gs.mc(nonFinalEnding,"size",[]))]);
          gs.mc(nonFinalSuffixesList,"push",[Affix(gs.map().add("affix",nonFinalEnding).add("meaning",gs.mc(nonFinalEndings,"get",[nonFinalEnding])))]);
         };
       };
     };
   };
  gs.sp(wholeWord,"tmpParse",word);
  gs.sp(wholeWord,"nonFinalSuffixes",nonFinalSuffixesList);
  return wholeWord;
 }
