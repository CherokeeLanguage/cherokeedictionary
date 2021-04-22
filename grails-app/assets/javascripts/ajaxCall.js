// modified example from https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Asynchronous/Async_await

async function checkWord(word) {
    var url = `/jsonlookup/${word}`;
    if (word === "") {
        // return;
    }

    let response = await fetch(url);

    let content;

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    content = await response.text();

    return content;
}