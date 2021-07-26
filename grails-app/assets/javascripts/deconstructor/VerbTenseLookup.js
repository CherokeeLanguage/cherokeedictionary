function VerbTenseLookup() {
  var gSobject = gs.init('VerbTenseLookup');
  gSobject.clazz = { name: 'net.cherokeedictionary.VerbTenseLookup', simpleName: 'VerbTenseLookup'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.VerbTenseLookup = gs.map();
  gSobject['VerbTenseLookup0'] = function(it) {
    gs.mc(gSobject.VerbTenseLookup,"put",["FUTURE_PROGRESSIVE", "esdi"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["PRE_INCIPIENT", "idi"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["FUTURE_IMPERATIVE", "vi"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["INFINITIVE", "di"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["HABITUAL", "oi"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["PLUPERFECT", "oi"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["REPORTATIVE", "ei"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["PAST", "vi"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["FUTURE", "i"]);
    gs.mc(gSobject.VerbTenseLookup,"put",["PRESENT", "a"]);
    return this;
  }
  if (arguments.length==0) {gSobject.VerbTenseLookup0(); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
