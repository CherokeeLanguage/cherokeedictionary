function AjaxCall() {
  var gSobject = gs.init('AjaxCall');
  gSobject.clazz = { name: 'net.cherokeedictionary.ajax.AjaxCall', simpleName: 'AjaxCall'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.lookupWordInCED = function(x0) { return AjaxCall.lookupWordInCED(x0); }
  gSobject.checkWord = function(x0) { return AjaxCall.checkWord(x0); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
 };
AjaxCall.lookupWordInCED = async function(word) {
  let content = await AjaxCall.checkWord(word);

        let values = await Promise.all([content]);

        if (values.length > 0 && values[0] !== "null") {
            return values;
        } else {
            return [];
        }
 }
AjaxCall.checkWord = async function(word) {
        let url = `http://localhost:8080/jsonlookup/${word}`;
        console.log("url " + url);

        const content = await $.ajax({
            method: "GET",
            url: url
        })

        console.log("content " + JSON.stringify(content));

        // let response = await fetch(url);
        //
        // if (!response.ok) {
        //     throw new Error(`HTTP error! status: ${response.status}`);
        // }
        //
        // console.log("response is " + JSON.stringify(response));
        //
        // content = await response.text();

        return content;
 }
