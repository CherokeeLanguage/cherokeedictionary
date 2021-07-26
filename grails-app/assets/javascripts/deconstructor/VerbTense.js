function VerbTense() {
  var gSobject = gs.init('VerbTense');
  gSobject.clazz = { name: 'net.cherokeedictionary.VerbTense', simpleName: 'VerbTense'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.VerbTenses = gs.map();
  gSobject['VerbTense0'] = function(it) {
    gs.mc(gSobject.VerbTenses,"put",["esdi", gs.gp(Tense,"FUTURE_PROGRESSIVE")]);
    gs.mc(gSobject.VerbTenses,"put",["idi", gs.gp(Tense,"PRE_INCIPIENT")]);
    gs.mc(gSobject.VerbTenses,"put",["vi", gs.gp(Tense,"FUTURE_IMPERATIVE")]);
    gs.mc(gSobject.VerbTenses,"put",["di", gs.gp(Tense,"INFINITIVE")]);
    gs.mc(gSobject.VerbTenses,"put",["oi", gs.gp(Tense,"HABITUAL")]);
    gs.mc(gSobject.VerbTenses,"put",["oi", gs.gp(Tense,"PLUPERFECT")]);
    gs.mc(gSobject.VerbTenses,"put",["ei", gs.gp(Tense,"REPORTATIVE")]);
    gs.mc(gSobject.VerbTenses,"put",["vi", gs.gp(Tense,"PAST")]);
    gs.mc(gSobject.VerbTenses,"put",["i", gs.gp(Tense,"FUTURE")]);
    gs.mc(gSobject.VerbTenses,"put",["a", gs.gp(Tense,"PRESENT")]);
    return this;
  }
  if (arguments.length==0) {gSobject.VerbTense0(); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
