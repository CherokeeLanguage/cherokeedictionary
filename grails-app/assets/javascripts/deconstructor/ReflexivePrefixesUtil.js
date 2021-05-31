function ReflexivePrefixesUtil() {
  var gSobject = gs.init('ReflexivePrefixesUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.ReflexivePrefixesUtil', simpleName: 'ReflexivePrefixesUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.getReflexivePrefix = function(x0) { return ReflexivePrefixesUtil.getReflexivePrefix(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
ReflexivePrefixesUtil.getReflexivePrefix = function(wholeWord) {
  return wholeWord;
 }
