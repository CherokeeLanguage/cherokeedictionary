function callAll(jsfiles) {
    $.getScript(jsfiles, function() {
        // alert("Script loaded but not necessarily executed.");
    });
}

callAll("/assets/cherokeeParser.js");
callAll("/assets/ajaxCall.js");
callAll("/assets/rework.js");
callAll("/assets/verbTables.js");
callAll("/assets/wordDisplay.js");
callAll("/assets/deconstructionMethods.js");
callAll("/assets/display.js");
callAll("/assets/tests.js");