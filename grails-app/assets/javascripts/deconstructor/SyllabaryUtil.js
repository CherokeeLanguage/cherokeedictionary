function SyllabaryUtil() {
  var gSobject = gs.init('SyllabaryUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.transliteration.SyllabaryUtil', simpleName: 'SyllabaryUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  Object.defineProperty(gSobject, 'syllabaryMap', { get: function() { return SyllabaryUtil.syllabaryMap; }, set: function(gSval) { SyllabaryUtil.syllabaryMap = gSval; }, enumerable: true });
  Object.defineProperty(gSobject, 'syllabaryMap2', { get: function() { return SyllabaryUtil.syllabaryMap2; }, set: function(gSval) { SyllabaryUtil.syllabaryMap2 = gSval; }, enumerable: true });
  gSobject.parseHyphen = function(x0) { return SyllabaryUtil.parseHyphen(x0); }
  gSobject.parseSyllabaryWithPunctuationAndLatin = function(x0) { return SyllabaryUtil.parseSyllabaryWithPunctuationAndLatin(x0); }
  gSobject.parseSyllabary = function(x0) { return SyllabaryUtil.parseSyllabary(x0); }
  gSobject.reverseReplace = function(x0) { return SyllabaryUtil.reverseReplace(x0); }
  gSobject.replace = function(x0) { return SyllabaryUtil.replace(x0); }
  gSobject.replace = function(x0,x1) { return SyllabaryUtil.replace(x0,x1); }
  gSobject.mixedTransliteration = function(x0) { return SyllabaryUtil.mixedTransliteration(x0); }
  gSobject.tsalagiToSyllabaryNormalize = function(x0) { return SyllabaryUtil.tsalagiToSyllabaryNormalize(x0); }
  gSobject.tsalagiToSyllabary = function(x0,x1) { return SyllabaryUtil.tsalagiToSyllabary(x0,x1); }
  gSobject.newTsalagiToSyllabary = function(x0,x1,x2) { return SyllabaryUtil.newTsalagiToSyllabary(x0,x1,x2); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
SyllabaryUtil.parseHyphen = function(value) {
  var returnValue = "";
  var values = gs.mc(value,"split",["-"]);
  for (var i = 0 ; i < gs.mc(values,"size",[]) ; i++) {
    returnValue += (SyllabaryUtil.syllabaryMap2[(values[i])]);
   };
  return returnValue;
 }
SyllabaryUtil.parseSyllabaryWithPunctuationAndLatin = function(value) {
  var returnValue = "";
  for (var i = 0 ; i < gs.mc(value,"length",[]) ; i++) {
    var val = gs.mc(value,"charAt",[i]);
    var tmp = SyllabaryUtil.syllabaryMap[(gs.plus("", val))];
    if (gs.equals(tmp, null)) {
      if (((((((((((((((((((((((((((((((((((((((((((((gs.equals(val, "a")) || (gs.equals(val, "e"))) || (gs.equals(val, "i"))) || (gs.equals(val, "o"))) || (gs.equals(val, "u"))) || (gs.equals(val, "v"))) || (gs.equals(val, "g"))) || (gs.equals(val, "k"))) || (gs.equals(val, "h"))) || (gs.equals(val, "l"))) || (gs.equals(val, "m"))) || (gs.equals(val, "n"))) || (gs.equals(val, "q"))) || (gs.equals(val, "s"))) || (gs.equals(val, "d"))) || (gs.equals(val, "t"))) || (gs.equals(val, "w"))) || (gs.equals(val, "y"))) || (gs.equals(val, "'"))) || (gs.equals(val, "-"))) || (gs.equals(val, " "))) || (gs.equals(val, ","))) || (gs.equals(val, "."))) || (gs.equals(val, "!"))) || (gs.equals(val, "?"))) || (gs.equals(val, "["))) || (gs.equals(val, "];"))) || (gs.equals(val, ":"))) || (gs.equals(val, ";"))) || (gs.equals(val, "~"))) || (gs.equals(val, "("))) || (gs.equals(val, ")"))) || (gs.equals(val, " "))) || (gs.equals(val, "/"))) || (gs.equals(val, "1"))) || (gs.equals(val, "2"))) || (gs.equals(val, "3"))) || (gs.equals(val, "4"))) || (gs.equals(val, "5"))) || (gs.equals(val, "6"))) || (gs.equals(val, "7"))) || (gs.equals(val, "8"))) || (gs.equals(val, "9"))) || (gs.equals(val, "0"))) || ("-")) {
        tmp = val;
       };
     };
    returnValue += tmp;
   };
  returnValue = SyllabaryUtil.replace(returnValue, true);
  return returnValue;
 }
SyllabaryUtil.parseSyllabary = function(value) {
  var returnValue = "";
  for (var i = 0 ; i < gs.mc(value,"length",[]) ; i++) {
    returnValue += (SyllabaryUtil.syllabaryMap[(gs.plus("", gs.mc(value,"charAt",[i])))]);
   };
  returnValue = SyllabaryUtil.replace(returnValue);
  return returnValue;
 }
SyllabaryUtil.reverseReplace = function(data) {
  data = gs.mc(data,"replace",["qua", "gwa"]);
  data = gs.mc(data,"replace",["que", "gwe"]);
  data = gs.mc(data,"replace",["qui", "gwi"]);
  data = gs.mc(data,"replace",["quo", "gwo"]);
  data = gs.mc(data,"replace",["quu", "gwu"]);
  data = gs.mc(data,"replace",["quv", "gwv"]);
  data = gs.mc(data,"replace",["tsa", "ja"]);
  data = gs.mc(data,"replace",["tse", "je"]);
  data = gs.mc(data,"replace",["tsi", "ji"]);
  data = gs.mc(data,"replace",["tso", "jo"]);
  data = gs.mc(data,"replace",["tsu", "ju"]);
  data = gs.mc(data,"replace",["tsv", "jv"]);
  data = gs.mc(data,"replace",["cha", "ja"]);
  data = gs.mc(data,"replace",["che", "je"]);
  data = gs.mc(data,"replace",["chi", "ji"]);
  data = gs.mc(data,"replace",["cho", "jo"]);
  data = gs.mc(data,"replace",["chu", "ju"]);
  data = gs.mc(data,"replace",["chv", "jv"]);
  data = gs.mc(data,"replace",["dle", "tle"]);
  data = gs.mc(data,"replace",["dli", "tli"]);
  data = gs.mc(data,"replace",["dlo", "tlo"]);
  data = gs.mc(data,"replace",["dlu", "tlu"]);
  data = gs.mc(data,"replace",["dlv", "tlv"]);
  return data;
 }
SyllabaryUtil.replace = function(data) {
  return SyllabaryUtil.replace(data, true);
 }
SyllabaryUtil.replace = function(data, replaceHyphens) {
  data = gs.mc(data,"replace",["gwa", "qua"]);
  data = gs.mc(data,"replace",["gwe", "que"]);
  data = gs.mc(data,"replace",["gwi", "qui"]);
  data = gs.mc(data,"replace",["gwo", "quo"]);
  data = gs.mc(data,"replace",["gwu", "quu"]);
  data = gs.mc(data,"replace",["kwv", "quv"]);
  data = gs.mc(data,"replace",["kwa", "qua"]);
  data = gs.mc(data,"replace",["kwe", "que"]);
  data = gs.mc(data,"replace",["kwi", "qui"]);
  data = gs.mc(data,"replace",["kwo", "quo"]);
  data = gs.mc(data,"replace",["kwu", "quu"]);
  data = gs.mc(data,"replace",["kwv", "quv"]);
  data = gs.mc(data,"replace",["cha", "tsa"]);
  data = gs.mc(data,"replace",["che", "tse"]);
  data = gs.mc(data,"replace",["chi", "tsi"]);
  data = gs.mc(data,"replace",["cho", "tso"]);
  data = gs.mc(data,"replace",["chu", "tsu"]);
  data = gs.mc(data,"replace",["chv", "tsv"]);
  data = gs.mc(data,"replace",["ja", "tsa"]);
  data = gs.mc(data,"replace",["je", "tse"]);
  data = gs.mc(data,"replace",["ji", "tsi"]);
  data = gs.mc(data,"replace",["jo", "tso"]);
  data = gs.mc(data,"replace",["ju", "tsu"]);
  data = gs.mc(data,"replace",["jv", "tsv"]);
  data = gs.mc(data,"replace",["dle", "tle"]);
  data = gs.mc(data,"replace",["dli", "tli"]);
  data = gs.mc(data,"replace",["dlo", "tlo"]);
  data = gs.mc(data,"replace",["dlu", "tlu"]);
  data = gs.mc(data,"replace",["dlv", "tlv"]);
  data = gs.mc(data,"replace",["hla", "tla"]);
  data = gs.mc(data,"replace",["hli", "tli"]);
  data = gs.mc(data,"replace",["hyah", "ya"]);
  data = gs.mc(data,"replace",["hya", "ya"]);
  data = gs.mc(data,"replace",["hyeh", "ye"]);
  data = gs.mc(data,"replace",["htsv", "tsv"]);
  data = gs.mc(data,"replace",["ki", "gi"]);
  data = gs.mc(data,"replace",["hga", "ga"]);
  data = gs.mc(data,"replace",["hgi", "gi"]);
  data = gs.mc(data,"replace",["hgo", "go"]);
  data = gs.mc(data,"replace",["hgu", "gu"]);
  data = gs.mc(data,"replace",["hgv", "gv"]);
  data = gs.mc(data,"replace",["l ", "li "]);
  data = gs.mc(data,"replace",["d ", "da "]);
  data = gs.mc(data,"replace",["hwi", "wi"]);
  data = gs.mc(data,"replace",["wh", "wv"]);
  data = gs.mc(data,"replace",["hdi", "di"]);
  data = gs.mc(data,"replace",["hje", "je"]);
  data = gs.mc(data,"replace",["hta", "ta"]);
  if (gs.bool(replaceHyphens)) {
    while (gs.mc(data,"indexOf",["-"]) != -1) {
      data = gs.mc(data,"replace",["-", ""]);
     };
   };
  return data;
 }
SyllabaryUtil.mixedTransliteration = function(data) {
  var sb = gs.list([]);
  var words = gs.mc(data,"split",[" "]);
  var hasCR = false;
  var crIndex = -1;
  gs.mc(words,"each",[function(it) {
    if ((gs.mc(it,"contains",["\n"])) || (gs.mc(it,"contains",[""]))) {
      hasCR = true;
      it = gs.mc(gs.mc(it,"replace",["\n", "111"]),"replace",["", "222"]);
     } else {
      crIndex = -1;
     };
    if ((!gs.bool(gs.mc(it,"startsWith",["<e>"]))) && (!gs.bool(gs.mc(it,"contains",["<e>"])))) {
      gs.mc(sb,'leftShift', gs.list([SyllabaryUtil.tsalagiToSyllabary(it, false)]));
     } else {
      if (gs.mc(it,"startsWith",["<e>"])) {
        gs.mc(sb,'leftShift', gs.list([it]));
       } else {
        if (gs.mc(it,"contains",["<e>"])) {
          var word = gs.mc(it,"split",["<e>"]);
          var sb2 = gs.list([]);
          gs.mc(word,"eachWithIndex",[function(str, idx) {
            if (gs.equals(idx, 0)) {
              return gs.mc(sb2,'leftShift', gs.list([SyllabaryUtil.tsalagiToSyllabary(str, false)]));
             } else {
              return gs.mc(sb2,'leftShift', gs.list([str]));
             };
           }]);
          gs.mc(sb,'leftShift', gs.list([gs.mc(sb2,"join",[""])]));
         };
       };
     };
    return gs.mc(sb,'leftShift', gs.list([" "]));
   }]);
  gs.mc(sb,'leftShift', gs.list(["\n"]));
  sb = gs.mc(sb,"join",[""]);
  sb = gs.mc(gs.mc(gs.mc(sb,"replaceAll",["<e>", ""]),"replaceAll",["111", "\n"]),"replaceAll",["222", ""]);
  return sb;
 }
SyllabaryUtil.tsalagiToSyllabaryNormalize = function(data) {
  var dataLatin = SyllabaryUtil.tsalagiToSyllabary(data);
  return SyllabaryUtil.reverseReplace(dataLatin);
 }
SyllabaryUtil.tsalagiToSyllabary = function(data, replaceHyphens) {
  if (replaceHyphens === undefined) replaceHyphens = true;
  return SyllabaryUtil.newTsalagiToSyllabary(data, replaceHyphens);
 }
SyllabaryUtil.newTsalagiToSyllabary = function(data, replaceHyphens, includeAlpha) {
  if (replaceHyphens === undefined) replaceHyphens = false;
  if (includeAlpha === undefined) includeAlpha = false;
  data = gs.mc(data,"toLowerCase",[]);
  data = SyllabaryUtil.replace(data, replaceHyphens);
  data = gs.mc(data,"replaceAll",["dla", gs.gp(SyllabaryUtil.syllabaryMap2,"dla")]);
  data = gs.mc(data,"replaceAll",["gwa", gs.gp(SyllabaryUtil.syllabaryMap2,"qua")]);
  data = gs.mc(data,"replaceAll",["gwe", gs.gp(SyllabaryUtil.syllabaryMap2,"que")]);
  data = gs.mc(data,"replaceAll",["gwi", gs.gp(SyllabaryUtil.syllabaryMap2,"qui")]);
  data = gs.mc(data,"replaceAll",["gwo", gs.gp(SyllabaryUtil.syllabaryMap2,"quo")]);
  data = gs.mc(data,"replaceAll",["gwu", gs.gp(SyllabaryUtil.syllabaryMap2,"quu")]);
  data = gs.mc(data,"replaceAll",["gwv", gs.gp(SyllabaryUtil.syllabaryMap2,"quv")]);
  data = gs.mc(data,"replaceAll",["hna", gs.gp(SyllabaryUtil.syllabaryMap2,"hna")]);
  data = gs.mc(data,"replaceAll",["kwa", gs.gp(SyllabaryUtil.syllabaryMap2,"qua")]);
  data = gs.mc(data,"replaceAll",["kwe", gs.gp(SyllabaryUtil.syllabaryMap2,"que")]);
  data = gs.mc(data,"replaceAll",["kwi", gs.gp(SyllabaryUtil.syllabaryMap2,"qui")]);
  data = gs.mc(data,"replaceAll",["kwo", gs.gp(SyllabaryUtil.syllabaryMap2,"quo")]);
  data = gs.mc(data,"replaceAll",["kwu", gs.gp(SyllabaryUtil.syllabaryMap2,"quu")]);
  data = gs.mc(data,"replaceAll",["kwv", gs.gp(SyllabaryUtil.syllabaryMap2,"quv")]);
  data = gs.mc(data,"replaceAll",["qua", gs.gp(SyllabaryUtil.syllabaryMap2,"qua")]);
  data = gs.mc(data,"replaceAll",["que", gs.gp(SyllabaryUtil.syllabaryMap2,"que")]);
  data = gs.mc(data,"replaceAll",["qui", gs.gp(SyllabaryUtil.syllabaryMap2,"qui")]);
  data = gs.mc(data,"replaceAll",["quo", gs.gp(SyllabaryUtil.syllabaryMap2,"quo")]);
  data = gs.mc(data,"replaceAll",["quu", gs.gp(SyllabaryUtil.syllabaryMap2,"quu")]);
  data = gs.mc(data,"replaceAll",["quv", gs.gp(SyllabaryUtil.syllabaryMap2,"quv")]);
  data = gs.mc(data,"replaceAll",["tla", gs.gp(SyllabaryUtil.syllabaryMap2,"tla")]);
  data = gs.mc(data,"replaceAll",["tle", gs.gp(SyllabaryUtil.syllabaryMap2,"tle")]);
  data = gs.mc(data,"replaceAll",["tli", gs.gp(SyllabaryUtil.syllabaryMap2,"tli")]);
  data = gs.mc(data,"replaceAll",["tlo", gs.gp(SyllabaryUtil.syllabaryMap2,"tlo")]);
  data = gs.mc(data,"replaceAll",["tlu", gs.gp(SyllabaryUtil.syllabaryMap2,"tlu")]);
  data = gs.mc(data,"replaceAll",["tlv", gs.gp(SyllabaryUtil.syllabaryMap2,"tlv")]);
  data = gs.mc(data,"replaceAll",["tsa", gs.gp(SyllabaryUtil.syllabaryMap2,"tsa")]);
  data = gs.mc(data,"replaceAll",["tse", gs.gp(SyllabaryUtil.syllabaryMap2,"tse")]);
  data = gs.mc(data,"replaceAll",["tsi", gs.gp(SyllabaryUtil.syllabaryMap2,"tsi")]);
  data = gs.mc(data,"replaceAll",["tso", gs.gp(SyllabaryUtil.syllabaryMap2,"tso")]);
  data = gs.mc(data,"replaceAll",["tsu", gs.gp(SyllabaryUtil.syllabaryMap2,"tsu")]);
  data = gs.mc(data,"replaceAll",["tsv", gs.gp(SyllabaryUtil.syllabaryMap2,"tsv")]);
  data = gs.mc(data,"replaceAll",["da", gs.gp(SyllabaryUtil.syllabaryMap2,"da")]);
  data = gs.mc(data,"replaceAll",["de", gs.gp(SyllabaryUtil.syllabaryMap2,"de")]);
  data = gs.mc(data,"replaceAll",["di", gs.gp(SyllabaryUtil.syllabaryMap2,"di")]);
  data = gs.mc(data,"replaceAll",["do", gs.gp(SyllabaryUtil.syllabaryMap2,"do")]);
  data = gs.mc(data,"replaceAll",["du", gs.gp(SyllabaryUtil.syllabaryMap2,"du")]);
  data = gs.mc(data,"replaceAll",["dv", gs.gp(SyllabaryUtil.syllabaryMap2,"dv")]);
  data = gs.mc(data,"replaceAll",["ga", gs.gp(SyllabaryUtil.syllabaryMap2,"ga")]);
  data = gs.mc(data,"replaceAll",["ge", gs.gp(SyllabaryUtil.syllabaryMap2,"ge")]);
  data = gs.mc(data,"replaceAll",["gi", gs.gp(SyllabaryUtil.syllabaryMap2,"gi")]);
  data = gs.mc(data,"replaceAll",["go", gs.gp(SyllabaryUtil.syllabaryMap2,"go")]);
  data = gs.mc(data,"replaceAll",["gu", gs.gp(SyllabaryUtil.syllabaryMap2,"gu")]);
  data = gs.mc(data,"replaceAll",["gv", gs.gp(SyllabaryUtil.syllabaryMap2,"gv")]);
  data = gs.mc(data,"replaceAll",["ha", gs.gp(SyllabaryUtil.syllabaryMap2,"ha")]);
  data = gs.mc(data,"replaceAll",["he", gs.gp(SyllabaryUtil.syllabaryMap2,"he")]);
  data = gs.mc(data,"replaceAll",["hi", gs.gp(SyllabaryUtil.syllabaryMap2,"hi")]);
  data = gs.mc(data,"replaceAll",["ho", gs.gp(SyllabaryUtil.syllabaryMap2,"ho")]);
  data = gs.mc(data,"replaceAll",["hu", gs.gp(SyllabaryUtil.syllabaryMap2,"hu")]);
  data = gs.mc(data,"replaceAll",["hv", gs.gp(SyllabaryUtil.syllabaryMap2,"hv")]);
  data = gs.mc(data,"replaceAll",["ka", gs.gp(SyllabaryUtil.syllabaryMap2,"ka")]);
  data = gs.mc(data,"replaceAll",["la", gs.gp(SyllabaryUtil.syllabaryMap2,"la")]);
  data = gs.mc(data,"replaceAll",["le", gs.gp(SyllabaryUtil.syllabaryMap2,"le")]);
  data = gs.mc(data,"replaceAll",["li", gs.gp(SyllabaryUtil.syllabaryMap2,"li")]);
  data = gs.mc(data,"replaceAll",["lo", gs.gp(SyllabaryUtil.syllabaryMap2,"lo")]);
  data = gs.mc(data,"replaceAll",["lu", gs.gp(SyllabaryUtil.syllabaryMap2,"lu")]);
  data = gs.mc(data,"replaceAll",["lv", gs.gp(SyllabaryUtil.syllabaryMap2,"lv")]);
  data = gs.mc(data,"replaceAll",["ma", gs.gp(SyllabaryUtil.syllabaryMap2,"ma")]);
  data = gs.mc(data,"replaceAll",["me", gs.gp(SyllabaryUtil.syllabaryMap2,"me")]);
  data = gs.mc(data,"replaceAll",["mi", gs.gp(SyllabaryUtil.syllabaryMap2,"mi")]);
  data = gs.mc(data,"replaceAll",["mo", gs.gp(SyllabaryUtil.syllabaryMap2,"mo")]);
  data = gs.mc(data,"replaceAll",["mu", gs.gp(SyllabaryUtil.syllabaryMap2,"mu")]);
  data = gs.mc(data,"replaceAll",["nah", gs.gp(SyllabaryUtil.syllabaryMap2,"nah")]);
  data = gs.mc(data,"replaceAll",["na", gs.gp(SyllabaryUtil.syllabaryMap2,"na")]);
  data = gs.mc(data,"replaceAll",["ne", gs.gp(SyllabaryUtil.syllabaryMap2,"ne")]);
  data = gs.mc(data,"replaceAll",["ni", gs.gp(SyllabaryUtil.syllabaryMap2,"ni")]);
  data = gs.mc(data,"replaceAll",["no", gs.gp(SyllabaryUtil.syllabaryMap2,"no")]);
  data = gs.mc(data,"replaceAll",["nu", gs.gp(SyllabaryUtil.syllabaryMap2,"nu")]);
  data = gs.mc(data,"replaceAll",["nv", gs.gp(SyllabaryUtil.syllabaryMap2,"nv")]);
  data = gs.mc(data,"replaceAll",["sa", gs.gp(SyllabaryUtil.syllabaryMap2,"sa")]);
  data = gs.mc(data,"replaceAll",["se", gs.gp(SyllabaryUtil.syllabaryMap2,"se")]);
  data = gs.mc(data,"replaceAll",["si", gs.gp(SyllabaryUtil.syllabaryMap2,"si")]);
  data = gs.mc(data,"replaceAll",["so", gs.gp(SyllabaryUtil.syllabaryMap2,"so")]);
  data = gs.mc(data,"replaceAll",["su", gs.gp(SyllabaryUtil.syllabaryMap2,"su")]);
  data = gs.mc(data,"replaceAll",["sv", gs.gp(SyllabaryUtil.syllabaryMap2,"sv")]);
  data = gs.mc(data,"replaceAll",["wa", gs.gp(SyllabaryUtil.syllabaryMap2,"wa")]);
  data = gs.mc(data,"replaceAll",["we", gs.gp(SyllabaryUtil.syllabaryMap2,"we")]);
  data = gs.mc(data,"replaceAll",["wi", gs.gp(SyllabaryUtil.syllabaryMap2,"wi")]);
  data = gs.mc(data,"replaceAll",["wo", gs.gp(SyllabaryUtil.syllabaryMap2,"wo")]);
  data = gs.mc(data,"replaceAll",["wu", gs.gp(SyllabaryUtil.syllabaryMap2,"wu")]);
  data = gs.mc(data,"replaceAll",["wv", gs.gp(SyllabaryUtil.syllabaryMap2,"wv")]);
  data = gs.mc(data,"replaceAll",["ya", gs.gp(SyllabaryUtil.syllabaryMap2,"ya")]);
  data = gs.mc(data,"replaceAll",["ye", gs.gp(SyllabaryUtil.syllabaryMap2,"ye")]);
  data = gs.mc(data,"replaceAll",["yi", gs.gp(SyllabaryUtil.syllabaryMap2,"yi")]);
  data = gs.mc(data,"replaceAll",["yo", gs.gp(SyllabaryUtil.syllabaryMap2,"yo")]);
  data = gs.mc(data,"replaceAll",["yu", gs.gp(SyllabaryUtil.syllabaryMap2,"yu")]);
  data = gs.mc(data,"replaceAll",["yv", gs.gp(SyllabaryUtil.syllabaryMap2,"yv")]);
  data = gs.mc(data,"replaceAll",["ta", gs.gp(SyllabaryUtil.syllabaryMap2,"ta")]);
  data = gs.mc(data,"replaceAll",["te", gs.gp(SyllabaryUtil.syllabaryMap2,"te")]);
  data = gs.mc(data,"replaceAll",["ti", gs.gp(SyllabaryUtil.syllabaryMap2,"ti")]);
  data = gs.mc(data,"replaceAll",["to", gs.gp(SyllabaryUtil.syllabaryMap2,"to")]);
  data = gs.mc(data,"replaceAll",["tu", gs.gp(SyllabaryUtil.syllabaryMap2,"tu")]);
  data = gs.mc(data,"replaceAll",["tv", gs.gp(SyllabaryUtil.syllabaryMap2,"tv")]);
  data = gs.mc(data,"replaceAll",["uh", gs.gp(SyllabaryUtil.syllabaryMap2,"u")]);
  data = gs.mc(data,"replaceAll",["a", gs.gp(SyllabaryUtil.syllabaryMap2,"a")]);
  data = gs.mc(data,"replaceAll",["e", gs.gp(SyllabaryUtil.syllabaryMap2,"e")]);
  data = gs.mc(data,"replaceAll",["i", gs.gp(SyllabaryUtil.syllabaryMap2,"i")]);
  data = gs.mc(data,"replaceAll",["o", gs.gp(SyllabaryUtil.syllabaryMap2,"o")]);
  data = gs.mc(data,"replaceAll",["u", gs.gp(SyllabaryUtil.syllabaryMap2,"u")]);
  data = gs.mc(data,"replaceAll",["v", gs.gp(SyllabaryUtil.syllabaryMap2,"v")]);
  data = gs.mc(data,"replaceAll",["s", gs.gp(SyllabaryUtil.syllabaryMap2,"s")]);
  if (!gs.bool(includeAlpha)) {
    data = gs.mc(data,"replaceAll",["[a-zA-Z]", "not a valid letter"]);
   };
  return data;
 }
SyllabaryUtil.syllabaryMap = gs.map().add("???","a").add("???","e").add("???","i").add("???","o").add("???","u").add("???","v").add("???","ga").add("???","ka").add("???","ge").add("???","gi").add("???","go").add("???","gu").add("???","gv").add("???","ha").add("???","he").add("???","hi").add("???","ho").add("???","hu").add("???","hv").add("???","la").add("???","le").add("???","li").add("???","lo").add("???","lu").add("???","lv").add("???","ma").add("???","me").add("???","mi").add("???","mo").add("???","mu").add("???","na").add("???","hna").add("???","nah").add("???","ne").add("???","ni").add("???","no").add("???","nu").add("???","nv").add("???","qua").add("???","que").add("???","qui").add("???","quo").add("???","quu").add("???","quv").add("???","sa").add("???","s").add("???","se").add("???","si").add("???","so").add("???","su").add("???","sv").add("???","da").add("???","ta").add("???","de").add("???","te").add("???","di").add("???","ti").add("???","do").add("???","du").add("???","dv").add("???","dla").add("???","tla").add("???","tle").add("???","tli").add("???","tlo").add("???","tlu").add("???","tlv").add("???","tsa").add("???","tse").add("???","tsi").add("???","tso").add("???","tsu").add("???","tsv").add("???","wa").add("???","we").add("???","wi").add("???","wo").add("???","wu").add("???","wv").add("???","ya").add("???","ye").add("???","yi").add("???","yo").add("???","yu").add("???","yv").add(" "," ");
SyllabaryUtil.syllabaryMap2 = gs.map().add("a","???").add("e","???").add("i","???").add("o","???").add("u","???").add("v","???").add("ga","???").add("ka","???").add("ge","???").add("gi","???").add("go","???").add("gu","???").add("gv","???").add("ha","???").add("he","???").add("hi","???").add("ho","???").add("hu","???").add("hv","???").add("la","???").add("le","???").add("li","???").add("lo","???").add("lu","???").add("lv","???").add("ma","???").add("me","???").add("mi","???").add("mo","???").add("mu","???").add("na","???").add("hna","???").add("nah","???").add("ne","???").add("ni","???").add("no","???").add("nu","???").add("nv","???").add("qua","???").add("que","???").add("qui","???").add("quo","???").add("quu","???").add("quv","???").add("kwa","???").add("kwe","???").add("kwi","???").add("kwo","???").add("kwu","???").add("kwv","???").add("sa","???").add("s","???").add("se","???").add("si","???").add("so","???").add("su","???").add("sv","???").add("da","???").add("ta","???").add("de","???").add("te","???").add("di","???").add("ti","???").add("do","???").add("to","???").add("du","???").add("tu","???").add("dv","???").add("tv","???").add("dla","???").add("tla","???").add("tle","???").add("tli","???").add("tlo","???").add("tlu","???").add("tlv","???").add("tsa","???").add("tse","???").add("tsi","???").add("tso","???").add("tsu","???").add("tsv","???").add("wa","???").add("we","???").add("wi","???").add("wo","???").add("wu","???").add("wv","???").add("ya","???").add("ye","???").add("yi","???").add("yo","???").add("yu","???").add("yv","???").add(" "," ");
