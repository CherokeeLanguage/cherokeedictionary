function TenseHolder() {
  var gSobject = gs.init('TenseHolder');
  gSobject.clazz = { name: 'net.cherokeedictionary.TenseHolder', simpleName: 'TenseHolder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.tense = null;
  gSobject.ending = null;
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
