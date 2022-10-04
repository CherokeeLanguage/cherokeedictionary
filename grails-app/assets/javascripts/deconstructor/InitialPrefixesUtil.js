function InitialPrefixesUtil() {
  var gSobject = gs.init('InitialPrefixesUtil');
  gSobject.clazz = { name: 'net.cherokeedictionary.util.InitialPrefixesUtil', simpleName: 'InitialPrefixesUtil'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.getInitialPrefixes = function(x0) { return InitialPrefixesUtil.getInitialPrefixes(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
InitialPrefixesUtil.getInitialPrefixes = function(wholeWord) {
  var initialPrefixList = gs.list([]);
  var tmp = gs.gp(wholeWord,"tmpParse");
  var foundAResult = true;
  while (gs.bool(foundAResult)) {
    foundAResult = false;
    var initialPrefixes = gs.gp(InitialPrefix(),"InitialPrefixes");
    for (_i2 = 0, initialPrefix = gs.mc(initialPrefixes,"keySet",[])[0]; _i2 < gs.mc(initialPrefixes,"keySet",[]).length; initialPrefix = gs.mc(initialPrefixes,"keySet",[])[++_i2]) {
      if (gs.mc(tmp,"startsWith",[initialPrefix])) {
        if (gs.equals(initialPrefix, "tsi")) {
          if ((gs.minus(gs.gp(gs.mc(tmp,"split",[initialPrefix]),"length"), 1)) > 1) {
            foundAResult = true;
            tmp = gs.mc(tmp,"substring",[gs.mc(initialPrefix,"size",[])]);
            gs.mc(initialPrefixList,"push",[Affix(gs.map().add("affix",initialPrefix).add("meaning",gs.mc(initialPrefixes,"get",[initialPrefix])))]);
           };
         } else {
          if (gs.equals(initialPrefix, "ga")) {
            if ((gs.minus(gs.gp(gs.mc(tmp,"split",[initialPrefix]),"length"), 1)) > 1) {
              foundAResult = true;
              tmp = gs.mc(tmp,"substring",[gs.mc(initialPrefix,"size",[])]);
              gs.mc(initialPrefixList,"push",[Affix(gs.map().add("affix",initialPrefix).add("meaning",gs.mc(initialPrefixes,"get",[initialPrefix])))]);
             };
           } else {
            foundAResult = true;
            tmp = gs.mc(tmp,"substring",[gs.mc(initialPrefix,"size",[])]);
            gs.mc(initialPrefixList,"push",[Affix(gs.map().add("affix",initialPrefix).add("meaning",gs.mc(initialPrefixes,"get",[initialPrefix])))]);
           };
         };
       };
     };
   };
  gs.sp(wholeWord,"tmpParse",tmp);
  gs.sp(wholeWord,"initialPrefixes",initialPrefixList);
  return wholeWord;
 }
