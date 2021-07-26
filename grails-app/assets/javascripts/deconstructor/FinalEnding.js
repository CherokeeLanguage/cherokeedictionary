function FinalEnding() {
  var gSobject = gs.init('FinalEnding');
  gSobject.clazz = { name: 'net.cherokeedictionary.affixes.FinalEnding', simpleName: 'FinalEnding'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.FinalEndings = gs.map();
  gSobject['FinalEnding0'] = function(it) {
    gs.mc(gSobject.FinalEndings,"put",["dv", "focus"]);
    gs.mc(gSobject.FinalEndings,"put",["dina", "concessiveImperative"]);
    gs.mc(gSobject.FinalEndings,"put",["isi", "orelse"]);
    gs.mc(gSobject.FinalEndings,"put",["hv", "but"]);
    gs.mc(gSobject.FinalEndings,"put",["na", "andWhatIf"]);
    gs.mc(gSobject.FinalEndings,"put",["ke", "orInterrogative"]);
    gs.mc(gSobject.FinalEndings,"put",["tsu", "interrogative"]);
    gs.mc(gSobject.FinalEndings,"put",["sgo", "interrogative2"]);
    gs.mc(gSobject.FinalEndings,"put",["gwu", "onlyJustStill"]);
    gs.mc(gSobject.FinalEndings,"put",["quu", "onlyJustStill"]);
    return this;
  }
  if (arguments.length==0) {gSobject.FinalEnding0(); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
