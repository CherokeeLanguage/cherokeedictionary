<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Bionic Reader</title>
    <style>
    .font-serif {
        font-family: 'IBM Plex Serif', serif;
        font-weight: 400;
    }

    .font-sans {
        font-family: 'Inter', sans-serif;
    }

    /* ibm-plex-serif-regular - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: normal;
        font-weight: 400;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif.otf")}');
    }

    /* ibm-plex-serif-italic - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: italic;
        font-weight: 400;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif-Italic.otf")}');
    }

    /* ibm-plex-serif-500 - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: normal;
        font-weight: 500;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif-Medium.otf")}');
    }

    /* ibm-plex-serif-500italic - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: italic;
        font-weight: 500;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif-MediumItalic.otf")}');
    }

    /* ibm-plex-serif-600 - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: normal;
        font-weight: 600;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif-SemiBold.otf")}');
    }

    /* ibm-plex-serif-600italic - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: italic;
        font-weight: 600;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif-SemiBoldItalic.otf")}');
    }

    /* ibm-plex-serif-700 - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: normal;
        font-weight: 700;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif-Bold.otf")}');
    }

    /* ibm-plex-serif-700italic - latin */
    @font-face {
        font-family: 'IBM Plex Serif';
        font-style: italic;
        font-weight: 700;
        src: url('${resource(dir: "fonts", file: "IBMPlexSerif-BoldItalic.otf")}');
    }

    /* inter */
    @font-face {
        font-family: 'Inter';
        font-style:  normal;
        font-weight: 400;
        font-display: swap;
        src: url("${resource(dir: "fonts", file: "Inter-Regular.woff2")}") format("woff2"),
        url("${resource(dir: "fonts", file: "Inter-Regular.woff")}") format("woff");
    }
    @font-face {
        font-family: 'Inter';
        font-style:  italic;
        font-weight: 400;
        font-display: swap;
        src: url("${resource(dir: "fonts", file: "Inter-Italic.woff2")}") format("woff2"),
        url("${resource(dir: "fonts", file: "Inter-Italic.woff")}") format("woff");
    }
    @font-face {
        font-family: 'Inter';
        font-style:  normal;
        font-weight: 700;
        font-display: swap;
        src: url("${resource(dir: "fonts", file: "Inter-Bold.woff2")}") format("woff2"),
        url("${resource(dir: "fonts", file: "Inter-Bold.woff")}") format("woff");
    }
    @font-face {
        font-family: 'Inter';
        font-style:  italic;
        font-weight: 700;
        font-display: swap;
        src: url("${resource(dir: "fonts", file: "Inter-BoldItalic.woff2")}") format("woff2"),
        url("${resource(dir: "fonts", file: "Inter-BoldItalic.woff")}") format("woff");
    }
    @font-face {
        font-family: 'Inter';
        font-style:  normal;
        font-weight: 900;
        font-display: swap;
        src: url("${resource(dir: "fonts", file: "Inter-Black.woff2")}") format("woff2"),
        url("${resource(dir: "fonts", file: "Inter-Black.woff")}") format("woff");
    }
    @font-face {
        font-family: 'Inter';
        font-style:  italic;
        font-weight: 900;
        font-display: swap;
        src: url("${resource(dir: "fonts", file: "Inter-BlackItalic.woff2")}") format("woff2"),
        url("${resource(dir: "fonts", file: "Inter-BlackItalic.woff")}") format("woff");
    }
    </style>

</head>
<body>


<div id="fontStuff" class="font-sans" style="font-size: 106%">
    <div id="endText"></div>
    <br/><br/><br/><br/>
</div>
<button id="buttonClick">Font Change</button><br/>
<textarea id="startText" cols="25" rows="15"></textarea>
<button id="convert">Convert</button>
<script>
    var count = 0;
    $('#buttonClick').click(function() {
        if (count == 0) {
            $("#fontStuff").removeClass("font-sans");
            $("#fontStuff").addClass("font-serif");
            count++;
        } else {
            $("#fontStuff").removeClass("font-serif");
            $("#fontStuff").addClass("font-sans");
            count--;
        }
    });

    function convert(startText) {
        var spliz = startText.split(" ");
        var html = "";

        for (var i = 0; i < spliz.length; i++) {
            var word = spliz[i];
            var middle = word.length / 2;
            if (middle < 1) {
                middle = 1;
            }

            middle = Math.round(middle);

            var sbstr = word.substr(0, middle);
            html += "<b>";
            html += sbstr;
            html += "</b>";
            html += word.substr(middle);
            html += "&nbsp;&nbsp;&nbsp;";

            if (i > 0 && i % 10 == 0) {
                html += "<br/><br/>";
            }
        }

        return html;
    }

    $('#convert').click(function() {
        var startText = $('#startText').val();
        document.getElementById("endText").innerHTML=convert(startText);
    });
</script>
</body>
</html>