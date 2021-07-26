function Explanation() {
  var gSobject = gs.init('Explanation');
  gSobject.clazz = { name: 'net.cherokeedictionary.Explanation', simpleName: 'Explanation'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.phonetic = null;
  gSobject.syllabary = null;
  gSobject.meaning = null;
  gSobject.purpose = null;
  gSobject.name = null;
  gSobject.type = null;
  gSobject['Explanation6'] = function(phonetic, syllabary, meaning, type, purpose, name) {
    gs.sp(this,"phonetic",phonetic);
    gs.sp(this,"syllabary",syllabary);
    gs.sp(this,"meaning",meaning);
    gs.sp(this,"purpose",purpose);
    gs.sp(this,"name",name);
    gs.sp(this,"type",type);
    return this;
  }
  if (arguments.length==6) {gSobject.Explanation6(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5]); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
