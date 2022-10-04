function PronominalPrefixesUtil() {
  var gSobject = gs.init('PronominalPrefixesUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.PronominalPrefixesUtil', simpleName: 'PronominalPrefixesUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.getPronominalPrefixes = function(x0) { return PronominalPrefixesUtil.getPronominalPrefixes(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
PronominalPrefixesUtil.getPronominalPrefixes = function(wholeWord) {
  var pronominalPrefixList = gs.list([]);
  var pronSize = 0;
  var tmp = gs.gp(wholeWord,"tmpParse");
  var consonantPrefixes = gs.gp(ConsonantPrefix(),"ConsonantPrefixes");
  for (_i4 = 0, consonantPrefix = gs.mc(consonantPrefixes,"keySet",[])[0]; _i4 < gs.mc(consonantPrefixes,"keySet",[]).length; consonantPrefix = gs.mc(consonantPrefixes,"keySet",[])[++_i4]) {
    if (gs.mc(tmp,"startsWith",[consonantPrefix])) {
      if (gs.mc(consonantPrefix,"size",[]) > pronSize) {
        pronSize = gs.mc(consonantPrefix,"size",[]);
       };
      gs.mc(pronominalPrefixList,"push",[gs.mc(consonantPrefixes,"get",[consonantPrefix])]);
     };
   };
  if (pronSize > 0) {
    tmp = gs.mc(tmp,"substring",[pronSize]);
    pronSize = 0;
   } else {
    var vowelPrefixes = gs.gp(VowelPrefix(),"VowelPrefixes");
    for (_i5 = 0, vowelPrefix = gs.mc(vowelPrefixes,"keySet",[])[0]; _i5 < gs.mc(vowelPrefixes,"keySet",[]).length; vowelPrefix = gs.mc(vowelPrefixes,"keySet",[])[++_i5]) {
      if (gs.mc(tmp,"startsWith",[vowelPrefix])) {
        if (gs.mc(vowelPrefix,"size",[]) > pronSize) {
          pronSize = gs.mc(vowelPrefix,"size",[]);
         };
        gs.mc(pronominalPrefixList,"push",[gs.mc(vowelPrefixes,"get",[vowelPrefix])]);
       };
     };
   };
  if (pronSize > 0) {
    tmp = gs.mc(tmp,"substring",[pronSize]);
   };
  gs.sp(wholeWord,"tmpParse",tmp);
  gs.sp(wholeWord,"pronounPrefixes",pronominalPrefixList);
  gs.sp(wholeWord,"root_phonetic",tmp);
  gs.sp(wholeWord,"root_syllabary",gs.execStatic(SyllabaryUtil,'newTsalagiToSyllabary', this,[tmp, false, true]));
  return wholeWord;
 }
