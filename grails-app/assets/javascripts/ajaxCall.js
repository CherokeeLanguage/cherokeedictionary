function checkWord(word) {
    var returnValue = "";
    $(function () {


        // $('#view').click(function () {
        //     var corpusSource = $('#cSource').val();
        //     if (!corpusSource) {
        //         //message for the user to make a selection from the dropdown
        //     }

            $.ajax({
                method: "POST",
                url: "https://cherokeedictionary.net/jsonsearch/chr/"+word,
                // data: {corpusSource: corpusSource},
                success: function (data) {
                    // window.location = data;
                    alert(data);
                    returnValue = data;
                    // $("#sourceChanged").html(data);
                }
            })
        // });
    });

    // var xhttp = new XMLHttpRequest();

    // xhttp.open("GET", "https://cherokeedictionary.net/jsonsearch/chr/" + word, true);
    // xhttp.send();

    // xhttp.onreadystatechange = function() {
    //     if (this.readyState == 4 && this.status == 200) {
    //         returnValue = this.responseText;
    //         // document.getElementById("demo").innerHTML = this.responseText;
    //     }
    // };

    return returnValue;
}
