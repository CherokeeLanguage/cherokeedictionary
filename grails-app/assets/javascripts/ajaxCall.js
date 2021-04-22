function putReturnValueIntoDiv(result) {
    $('#resultData').innerText = result;
    console.log(result);
    // alert($('#resultData').innerText);
}

async function checkWord(word, callback) {
    var url = `/jsonlookup/${word}`;
    if (word === "") {
        return;
    }
    console.log(url);
    var ajaxCall = $.ajax({
        method: "POST",
        url: url
    });

    ajaxCall.done(function( msg ) {
        callback(msg);
    });
}
