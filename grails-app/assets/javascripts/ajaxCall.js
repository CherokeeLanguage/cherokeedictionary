// modified example from https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Asynchronous/Async_await

async function checkWord(word) {
    var url = `/jsonlookup/${word}`;

    let response = await fetch(url);

    let content;

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    content = await response.text();

    return content;
}

async function lookupWordInCED(word) {
    var content = await checkWord(word);

    let values = await Promise.all([content]);

    console.log("values lookup " + values);

    if (values.length > 0 && values[0] !== "null") {
        return values;
    } else {
        return [];
    }
}
