function DeconstructorUtil() {
  var gSobject = gs.init('DeconstructorUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.DeconstructorUtil', simpleName: 'DeconstructorUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.startsWithVowel = function(x0) { return DeconstructorUtil.startsWithVowel(x0); }
  gSobject.endsWithVowel = function(x0) { return DeconstructorUtil.endsWithVowel(x0); }
  gSobject.getReflexivePrefix = function(x0) { return DeconstructorUtil.getReflexivePrefix(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
DeconstructorUtil.startsWithVowel = function(word) {
  return (((((gs.mc(gs.mc(word,"substring",[0, 1]),"startsWith",["a"])) || (gs.mc(gs.mc(word,"substring",[0, 1]),"startsWith",["e"]))) || (gs.mc(gs.mc(word,"substring",[0, 1]),"startsWith",["i"]))) || (gs.mc(gs.mc(word,"substring",[0, 1]),"startsWith",["o"]))) || (gs.mc(gs.mc(word,"substring",[0, 1]),"startsWith",["u"]))) || (gs.mc(gs.mc(word,"substring",[0, 1]),"startsWith",["v"]));
 }
DeconstructorUtil.endsWithVowel = function(word) {
  return (((((gs.mc(word,"endsWith",["a"])) || (gs.mc(word,"endsWith",["e"]))) || (gs.mc(word,"endsWith",["i"]))) || (gs.mc(word,"endsWith",["o"]))) || (gs.mc(word,"endsWith",["u"]))) || (gs.mc(word,"endsWith",["v"]));
 }
DeconstructorUtil.getReflexivePrefix = function(wholeWord) {
  return wholeWord;
 }
