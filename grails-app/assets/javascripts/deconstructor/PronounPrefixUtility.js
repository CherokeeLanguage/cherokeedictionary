function PronounPrefixUtility() {
  var gSobject = gs.init('PronounPrefixUtility');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.PronounPrefixUtility', simpleName: 'PronounPrefixUtility'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject['getPronPrefix'] = function(wholeWord) {
    var pronominalPrefixes = gs.gp(PronominalPrefix(),"PronominalPrefixes");
    var pronPrefixes = ((gs.bool(wholeWord)) && (gs.bool(gs.gp(wholeWord,"pronounPrefixes"))) ? gs.gp(wholeWord,"pronounPrefixes")[0] : "");
    var pronPrefix = null;
    if (pronPrefixes != null) {
      pronPrefix = gs.mc(pronominalPrefixes,"get",[pronPrefixes]);
      if (pronPrefix != null) {
        pronPrefix;
        if (gs.execStatic(DeconstructorUtil,'startsWithVowel', this,[gs.gp(wholeWord,"tmpParse")])) {
          pronPrefix = gs.gp(pronPrefix,"preVowel");
         } else {
          pronPrefix = gs.gp(pronPrefix,"preConsonant");
         };
       } else {
       };
      gs.println(gs.plus("pron prefix ", pronPrefix));
      if ((gs.bool(pronPrefixes)) && (gs.mc(pronPrefix,"startsWith",["uw"]))) {
        if (gs.mc(gs.gp(wholeWord,"pronounPrefixes")[0],"endsWith",["B"])) {
          pronPrefix = "ga";
         };
        gs.sp(wholeWord,"tmpParse",(gs.plus("w", gs.gp(wholeWord,"tmpParse"))));
       } else {
        if ((gs.bool(pronPrefixes)) && (gs.mc(pronPrefix,"startsWith",["u"]))) {
          if (gs.mc(gs.gp(wholeWord,"pronounPrefixes")[0],"endsWith",["B"])) {
            pronPrefix = "ga";
           };
         };
       };
     } else {
      pronPrefix = "";
     };
    return pronPrefix;
   }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
