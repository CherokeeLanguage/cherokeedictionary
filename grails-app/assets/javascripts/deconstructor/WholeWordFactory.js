function WholeWordFactory() {
  var gSobject = gs.init('WholeWordFactory');
  gSobject.clazz = { name: 'net.cherokeedictionary.WholeWordFactory', simpleName: 'WholeWordFactory'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.create = function(x0,x1) { return WholeWordFactory.create(x0,x1); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
WholeWordFactory.create = function(word, isSyllabary) {
  if (isSyllabary === undefined) isSyllabary = true;
  var wholeWord = WholeWord();
  if (gs.bool(isSyllabary)) {
    gs.sp(wholeWord,"syllabary",word);
    gs.sp(wholeWord,"phonetic",gs.execStatic(SyllabaryUtil,'parseSyllabary', this,[word]));
   } else {
    gs.sp(wholeWord,"phonetic",word);
   };
  gs.sp(wholeWord,"phonetic",gs.mc(gs.gp(wholeWord,"phonetic"),"replaceAll",["qu", "gw"]));
  return wholeWord;
 }
