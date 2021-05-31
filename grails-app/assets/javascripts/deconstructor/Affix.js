function Affix() {
  var gSobject = gs.init('Affix');
  gSobject.clazz = { name: 'net.cherokeedictionary.affixes.Affix', simpleName: 'Affix'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.affix = null;
  gSobject.meaning = null;
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
