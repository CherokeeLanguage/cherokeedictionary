<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Transliteration</title>
    <script>
        $(function() {
            $('#clear').click(function() {
                $('#text').val('');
                $('#searchresults').val('');
            });

            $('#transliteratea').click(function() {
                $.ajax({
                    method: "POST",
                    url: "/transliterate/index",
                    data: {text:$('#text').val(), from:'true'},
                    success: function(data) {
                        $( "#searchresults" ).val( data );
                    }
                })
            });

            $('#transliterateb').click(function() {
                $.ajax({
                    method: "POST",
                    url: "/transliterate/index",
                    data: {text:$('#searchresults').val(), from:'false'},
                    success: function(data) {
                        $( "#text" ).val( data );
                    }
                })
            });

            $('#textClear').click(function() {
                $('#text').val('');
            });

            $('#syllabaryClear').click(function() {
                $('#searchresults').val('');
            })
        })
    </script>
</head>
<body>
    <b>Instructions:</b><br/>
    Type or paste the Cherokee you'd like transliterated to syllabary in the panel on the left side.<br/>
    Click the <em>to Syllabary >></em> button and the transliterated value will appear in the panel on the right.<br/>
    Click the <em><< from Syllabary</em> button and the transliterated value will appear in the panel on the left.<br/>
    To clear both panels click the <em><< Clear >></em> button.<br/>
    If you have any English in your work place a tag '&lt;e&gt;'before each English word and it will be skipped for transliteration.<br/>
    <br/>
    <b>Example:</b>Wadodv ani&lt;e&gt;Ridge &lt;e&gt;Baltimore howa<br/><br/>
    <table>
        <tr>
            <td rowspan="4" align="center"><b>Type or paste latin-script:</b><br/><textarea id="text" name="text" cols="25" rows="5"></textarea><br/><button id="textClear">^ Clear Panel ^</button></td>
            <td align="center">
                <button id="transliteratea">to Syllabary >></button><br/>
                <button id="clear"><< Clear >></button><br/>
                <button id="transliterateb"><< from Syllabary</button>
            </td>
            <td align="center"><b>Type or Paste Syllabary:</b><br/><textarea id="searchresults" name="searchresults" cols="25" rows="5"></textarea><br/><button id="syllabaryClear">^ Clear Panel ^</button></td>
        </tr>
    </table>


</body>
</html>