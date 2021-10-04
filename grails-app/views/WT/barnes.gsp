<!DOCTYPE html>
<html lang="en">
    <head>
        <meta name="layout" content="manager"/>
        <meta charset="UTF-8">
        <title>Barnes Form Test</title>
%{--        <asset:stylesheet src="main1.css"/>--}%
%{--        <asset:stylesheet src="main2.css"/>--}%

    </head>
    <body>
        <div id="savedMessage"></div>
    %{--    <g:form controller="WT" action="barnes" id="barnes" name="barnes">--}%
            Definition: <input type="text" id="definition" name="definition"/>
            <table id="formentries">
                <tr><td>Syllabary</td><td>Phonetic</td><td>Tone</td><td>Classifier</td><td>Audio File Path</td></tr>
            </table>
            <input type="hidden" value="0" name="numberOfEntries" id="numberOfEntries"/>
            <g:submitButton id="submitEntryButton" name="submitEntryButton" value="Submit Entry"/>
%{--        </g:form><br/>--}%
        <a onclick="addNewRow()">Add New Row</a><br/><br/>
        ${definition}<br/>
        <g:each in="${wordList}" var="word">
            ${word.syllabary} - ${word.tsalagi} - ${word.tone} <br/>
        </g:each>

        <script>
            var count = 0;
            var progress_bar_id = "#progress-wrp";

            //https://stackoverflow.com/questions/4006520/using-html5-file-uploads-with-ajax-and-jquery
            var Upload = function (file) {
                this.file = file;
            };

            Upload.prototype.getType = function() {
                return this.file.type;
            };

            Upload.prototype.getSize = function() {
                return this.file.size;
            };

            Upload.prototype.getName = function() {
                return this.file.name;
            };

            Upload.prototype.doUpload = function(tmpCount) {
                var that = this;
                var formData = new FormData();

                // add assoc key values, this will be posts values
                formData.append("file", this.file, this.getName());
                formData.append("upload_file", true);
                formData.append("count", tmpCount);

                $.ajax({
                    type: "POST",
                    url: "/WT/uploadFeatureImage",
                    xhr: function () {
                        var myXhr = $.ajaxSettings.xhr();
                        if (myXhr.upload) {
                            myXhr.upload.addEventListener('progress', that.progressHandling, false);
                        }
                        return myXhr;
                    },
                    success: function (data) {
                        $("#audioLink" + data.fileCount).html("<audio controls=\"controls\" ><source class=\"controls\"  id=\"controls" + data.fileCount + "\" src=\"" + data.filePath + "\" type=\"audio/x-wav\"/></audio>");
                    },
                    error: function (error) {
                        // handle error
                    },
                    async: true,
                    data: formData,
                    cache: false,
                    contentType: false,
                    processData: false,
                    timeout: 60000
                });
            };

            Upload.prototype.progressHandling = function (event) {
                var percent = 0;
                var position = event.loaded || event.position;
                var total = event.total;

                if (event.lengthComputable) {
                    percent = Math.ceil(position / total * 100);
                }
                // update progressbars classes so it fits your code
                $(progress_bar_id + " .progress-bar").css("width", +percent + "%");
                $(progress_bar_id + " .status").text(percent + "%");
            };

            function addNewRow() {
                $('#formentries').append("<tr><td><input type=\"text\" id=\"syllabary" + count + "\" name=\"syllabary" + count + "\"/></td><td><input type=\"text\" id=\"phonetic" + count + "\" name=\"phonetic" + count + "\"/></td><td><input type=\"text\" id=\"tone" + count + "\" name=\"tone" + count + "\"/></td><td><input type=\"text\" id=\"classifier" + count + "\" name=\"classifier" + count + "\"/></td><td><div id=\"audioLink" + count + "\"></div></td></tr>");

                $('#classifier'+ count).autocomplete({
                    source: '<g:createLink controller='WT' action='wordType'/>'
                });

                $('#audioLink' + count).append("<input type=\"file\" name=\"file" + count + "\" id=\"file" + count + "\"><button name=\"uploadIt" + count + "\" id=\"uploadIt" + count + "\">Upload it yo</button><div id=\"progress-wrp" + count + "\"><div class=\"progress-bar\"></div><div class=\"status\">0%</div></div>")

                createClickLink(count);

                $('#numberOfEntries').attr('value', '' + (count + 1));
                count++;
            }

            addNewRow();

            $('#definition').autocomplete({
                source: '<g:createLink controller='WT' action='definitionLookup'/>'
            });

            function createClickLink(tmpCount) {
                $('#audioLink' + tmpCount).append("<style>#progress-wrp" + tmpCount + " {border: 1px solid #0099CC;padding: 1px;position: relative;height: 30px;border-radius: 3px;margin: 10px;text-align: left;background: #fff;box-shadow: inset 1px 3px 6px rgba(0, 0, 0, 0.12);} #progress-wrp" + tmpCount + " .progress-bar {height: 100%;border-radius: 3px;background-color: #f39ac7;width: 0;box-shadow: inset 1px 1px 10px rgba(0, 0, 0, 0.11);}#progress-wrp" + tmpCount + " .status {top: 3px;left: 50%;position: absolute;display: inline-block;color: #000000;}</style>");

                //Change id to your id
                $("#uploadIt" + tmpCount).on("click", function (e) {
                    var file = $('#file' + tmpCount)[0].files[0];
                    var upload = new Upload(file);
                    progress_bar_id = "#progress-wrp" + tmpCount;
                    // maybe check size or type here with upload.getSize() and upload.getType()

                    // execute upload
                    upload.doUpload(tmpCount);
                });
            }

            $('#submitEntryButton').click(function() {
                var datum = {}
                datum.definition = $("#definition").val();
                $("input").each(function() {
                    var name = $( this ).attr("id");
                    datum[name] = $(this).val();
                });

                $('.controls').each(function() {
                    var name = $(this).attr("id");

                    datum[name] = $('#'+name).attr("src");//$(this).val();
                });

                console.log(JSON.stringify(datum));
                //{"definition":"new definition","syllabary0":"ᎦᏬᏂᏓ","phonetic0":"asdf","tone0":"gạ²wo¹ni²³da","classifier0":"v1sgpres","numberOfEntries":"1","submitEntryButton":"Submit Entry","undefined":"","controls0":"http://cherokeedictionary.net/audio/file_example_WAV_10MG.wav"}
                $.ajax({
                    type: "POST",
                    url: "/WT/saveFormData",
                    async: true,
                    data: datum,
/*                    cache: false,
                    contentType: false,
                    processData: false,*/
                    timeout: 60000,
                    success: function (data) {
                        $("#savedMessage").text("Entry Saved");
                    },
                    error: function (error) {
                        $("#savedMessage").text("Error Saving Entry");
                        //need an error message to go with this
                    }
                });
            });
        </script>
        <br/>

    </body>
</html>
