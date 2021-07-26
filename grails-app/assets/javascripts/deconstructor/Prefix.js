function Prefix() {
  var gSobject = gs.init('Prefix');
  gSobject.clazz = { name: 'net.cherokeedictionary.affixes.Prefix', simpleName: 'Prefix'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.preVowel = null;
  gSobject.preConsonant = null;
  gSobject.other = null;
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
