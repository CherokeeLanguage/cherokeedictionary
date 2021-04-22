async function checkWord(word) {
    return new Promise((resolve, reject) => {
        var url = `/jsonlookup/${word}`;
        if (word === "") {
            return;
        }

        console.log(url);
        $.ajax({
            method: "POST",
            url: url,
            success: function (data) {
                resolve(data)
            },
            error: function (error) {
                reject(error)
            }
        });
    });
}
