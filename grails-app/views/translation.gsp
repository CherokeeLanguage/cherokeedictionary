

<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Grammar</title>
    <script>
        $(function() {
            $('#tsalagiSearch').autocomplete({
                source: '<g:createLink controller='newSearch' action='ajaxTsalagi2Finder'/>'
            });

            $('#syllabarySearch').autocomplete({
                source: '<g:createLink controller='newSearch' action='ajaxSyllabary2Finder'/>'
            });

            $('#tsalagiSearch').click(function () {
                $('#syllabarySearch').val("");
                $('#tsalagiSearch').val("");
            });

            $('#tsalagiSearch').focus(function () {
                $('#syllabarySearch').val("");
            });

            $('#syllabarySearch').click(function () {
                $('#tsalagiSearch').val("");
                $('#syllabarySearch').val("");
            });

            $('#syllabarySearch').focus(function () {
                $('#tsalagiSearch').val("");
                $('#syllabarySearch').val("");
            });

            $('#translate').click(function() {
                $.ajax({
                    method: "POST",
                    url: "/translate/index",
                    data: {text:$('#text').val(), from:'true'},
                    success: function(data) {
                        $( "#searchresults" ).html( data );
                    }
                })
            });
        });
    </script>
</head>
<body>
<g:form controller="newSearch" action="dictionary" method="POST">
    <td><g:textField id="tsalagiSearch" name="tsalagiSearch"/></td>
    <td><g:submitButton id="tsalagiSearchButton" name="tsalagiSearchButton" value="Cherokee (phonetic)"/></td>
    <br/>
    <td><g:textField id="syllabarySearch" name="syllabarySearch"/></td>
    <td><g:submitButton id="syllabarySearchButton" name="syllabarySearchButton"/></td>
    </tr>
</g:form>
<table>
    <tr>
        <td rowspan="4" align="center"><b>Type or paste latin-script:</b><br/><textarea id="text" name="text" cols="50" rows="25">ᎤᏍᏗ ᎠᏓᏪᎳᎩᏍᎬ ᎤᏃᏛ ᏧᎾᏦᎯᏍᏗ, ᏌᎪᏂᎨ ᏃᎴ ᏓᎶᏂᎨ ᏓᏓᏪᎳᎩᏍᎬ ᏩᏁ ᏃᎴ, ᏘᎵ ᏚᏅᏛ. ᎧᎵ ᎠᏂᏅ ᏗᎦᏅᎯᏓ ᏗᎦᏍᎩᎶᎩ, ᏗᏂᏲᏟ ᎡᏓᏍᏘ ᎠᏂᏅ ᎠᎴ ᎠᏂᏅᎬ.</textarea><br/><button id="textClear">^ Clear Panel ^</button></td>
        <td align="center">
            <button id="translate">to Translate >></button><br/>
            <button id="clear"><< Clear >></button><br/>
            <button id="transliterateb"><< from Syllabary</button>
        </td>
        <td align="center"><div id="searchresults"></div></td>
    </tr>
</table>
</body>
</html>

%{--<%@ page contentType="text/html;charset=UTF-8" %>--}%
%{--<html>--}%
%{--<head>--}%
%{--<meta name="layout" content="manager"/>--}%
%{--<title>Translation</title>--}%
%{--</head>--}%
%{--<body>--}%
%{--<script>--}%
%{--$(function() {--}%
%{--$.ajax({--}%
%{--method: "POST",--}%
%{--url: "http://localhost:9000/?properties={\"annotators\":\"tokenize,ssplit,pos\",\"outputFormat\":\"json\"}",--}%
%{--crossDomain: true,--}%
%{--dataType: 'jsonp',--}%
%{--data: {data:'the quick brown fox jumped over the lazy dog'},--}%
%{--success: function(responseData, textStatus, jqXHR) {--}%
%{--$( "#text" ).val( responseData );--}%
%{--},--}%
%{--//            error: function (responseData, textStatus, errorThrown) {--}%
%{--//                alert('POST failed.');--}%
%{--//            }--}%

%{--});--}%
%{--});--}%
%{--</script>--}%
%{--</body>--}%
%{--<div id="text"></div>--}%
%{--</html>--}%
