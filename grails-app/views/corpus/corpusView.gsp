<html>
<head>
    <meta name="layout" content="manager"/>
    <title>NLP Corpus View</title>
    <script>
        function edit(id) {
            $(function () {
                var syllabary = $('#syllabary'+id).html();
                $('#syllabary'+id).html("<input id=\"syllabaryText" + id + "\" size=\"" + (syllabary.length + 20) + "\" type=\"text\" value=\"" + syllabary + "\">");
                var english = $('#english'+id).html();
                $('#english'+id).html("<input id=\"englishText" + id + "\" size=\"" + (english.length + 20) + "\" type=\"text\" value=\"" + english + "\">");
            });
        }


        function save(id) {
            $(function () {
                var syllabary = $('#syllabaryText'+id).val();
                var english = $('#englishText'+id).val();

                $.ajax({
                    method: "POST",
                    url: "/corpus/save",
                    data: {id: id, syllabary: syllabary, english: english},
                    success: function (data) {
                        console.log(data);
                        $("#editComplete").html(data.data);
                        $('#syllabary'+id).html(syllabary);
                        $('#english'+id).html(english);
                    },
                    failure: function (data) {
                        $("#editComplete").html(data);
                    }
                });
            });
        }
    </script>
</head>
<body>
<div id="editComplete"></div><br/>
<g:each var="value" in="${lst}">
    <div style="display:table-row">
        <%if (session.getAttribute("loggedin")) { %><a onclick="javascript:edit(${value.id})" id="edit${value.id}" style="display:table-cell;text-decoration:underline;cursor:pointer">edit</a> | <a onclick="javascript:save(${value.id})" id="edit${value.id}"  style="display:table-cell;text-decoration:underline;cursor:pointer">save</a>&nbsp;&nbsp;<%}%><div style="width:auto;display:table-cell" id="syllabary${value.id}">${value.syllabary}</div><div style="width:auto;display:table-cell;" id="english${value.id}">${value.english}</div>
    </div>
</g:each>

</body>
</html>