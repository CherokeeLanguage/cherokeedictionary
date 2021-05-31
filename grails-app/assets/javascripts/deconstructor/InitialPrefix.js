function InitialPrefix() {
  var gSobject = gs.init('InitialPrefix');
  gSobject.clazz = { name: 'net.cherokeedictionary.affixes.InitialPrefix', simpleName: 'InitialPrefix'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.InitialPrefixes = gs.map();
  gSobject['InitialPrefix0'] = function(it) {
    gs.mc(gSobject.InitialPrefixes,"put",["yi", "negative/conditional"]);
    gs.mc(gSobject.InitialPrefixes,"put",["yu", "negative/conditional"]);
    gs.mc(gSobject.InitialPrefixes,"put",["tsi", "relative"]);
    gs.mc(gSobject.InitialPrefixes,"put",["wi", "away from speaker"]);
    gs.mc(gSobject.InitialPrefixes,"put",["ni", "lateral position, already"]);
    gs.mc(gSobject.InitialPrefixes,"put",["de", "plural object"]);
    gs.mc(gSobject.InitialPrefixes,"put",["d", "plural object"]);
    gs.mc(gSobject.InitialPrefixes,"put",["da", "future/motion toward speaker"]);
    gs.mc(gSobject.InitialPrefixes,"put",["di", "distant position"]);
    gs.mc(gSobject.InitialPrefixes,"put",["i", "again"]);
    gs.mc(gSobject.InitialPrefixes,"put",["ga", "since"]);
    gs.mc(gSobject.InitialPrefixes,"put",["e", "distant imperative"]);
    return this;
  }
  if (arguments.length==0) {gSobject.InitialPrefix0(); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
