<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>Cherokee English Dictionary</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Cherokee English Dictionary">

    <script src="https://code.jquery.com/jquery-latest.min.js"></script>
    <script src="https://code.jquery.com/ui/1.11.2/jquery-ui.min.js"></script>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.5.0/css/all.css" integrity="sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU" crossorigin="anonymous">
    <asset:javascript src="jquery.colorbox-min.js"/>

    <style type="text/css">
        .popper {
            color: black;
            text-decoration: none
        }
        .popbox {
            display: none;
            position: absolute;
            z-index: 99999;
            width: 400px;
            padding: 10px;
            background: #EEEFEB;
            color: #000000;
            border: 1px solid #4D4F53;
            margin: 0px;
            -webkit-box-shadow: 0px 0px 5px 0px rgba(164, 164, 164, 1);
            box-shadow: 0px 0px 5px 0px rgba(164, 164, 164, 1);
        }
        .popbox h2 {
            background-color: #4D4F53;
            color:  #E3E5DD;
            font-size: 14px;
            display: block;
            width: 100%;
            margin: -10px 0px 8px -10px;
            padding: 5px 10px;
        }


        /*
    Colorbox Core Style:
    The following CSS is consistent between example themes and should not be altered.
*/
        #colorbox, #cboxOverlay, #cboxWrapper{position:absolute; top:0; left:0; z-index:9999; overflow:hidden;}
        #cboxWrapper {max-width:none;}
        #cboxOverlay{position:fixed; width:100%; height:100%;}
        #cboxMiddleLeft, #cboxBottomLeft{clear:left;}
        #cboxContent{position:relative;}
        #cboxLoadedContent{overflow:auto; -webkit-overflow-scrolling: touch;}
        #cboxTitle{margin:0;}
        #cboxLoadingOverlay, #cboxLoadingGraphic{position:absolute; top:0; left:0; width:100%; height:100%;}
        #cboxPrevious, #cboxNext, #cboxClose, #cboxSlideshow{cursor:pointer;}
        .cboxPhoto{float:left; margin:auto; border:0; display:block; max-width:none; -ms-interpolation-mode:bicubic;}
        .cboxIframe{width:100%; height:100%; display:block; border:0; padding:0; margin:0;}
        #colorbox, #cboxContent, #cboxLoadedContent{box-sizing:content-box; -moz-box-sizing:content-box; -webkit-box-sizing:content-box;}

        /*
            User Style:
            Change the following styles to modify the appearance of Colorbox.  They are
            ordered & tabbed in a way that represents the nesting of the generated HTML.
        */

        
        #cboxOverlay{background:url(${resource(dir: "images", file: "overlay.png")}) repeat 0 0; opacity: 0.9; filter: alpha(opacity = 90);}
        #colorbox{outline:0;}
        #cboxTopLeft{width:21px; height:21px; background:url(${resource(dir: "images", file: "controls.png")}) no-repeat -101px 0;}
        #cboxTopRight{width:21px; height:21px; background:url(${resource(dir: "images", file: "controls.png")}) no-repeat -130px 0;}
        #cboxBottomLeft{width:21px; height:21px; background:url(${resource(dir: "images", file: "controls.png")}) no-repeat -101px -29px;}
        #cboxBottomRight{width:21px; height:21px; background:url(${resource(dir: "images", file: "controls.png")}) no-repeat -130px -29px;}
        #cboxMiddleLeft{width:21px; background:url(${resource(dir: "images", file: "controls.png")}) left top repeat-y;}
        #cboxMiddleRight{width:21px; background:url(${resource(dir: "images", file: "controls.png")}) right top repeat-y;}
        #cboxTopCenter{height:21px; background:url(${resource(dir: "images", file: "border.png")}) 0 0 repeat-x;}
        #cboxBottomCenter{height:21px; background:url(${resource(dir: "images", file: "border.png")}) 0 -29px repeat-x;}
        #cboxContent{background:#fff; overflow:hidden;}
        .cboxIframe{background:#fff;}
        #cboxError{padding:50px; border:1px solid #ccc;}
        #cboxLoadedContent{margin-bottom:28px;}
        #cboxTitle{position:absolute; bottom:4px; left:0; text-align:center; width:100%; color:#949494;}
        #cboxCurrent{position:absolute; bottom:4px; left:58px; color:#949494;}
        #cboxLoadingOverlay{background:url(${resource(dir: "images", file: "loading_background.png")}) no-repeat center center;}
        #cboxLoadingGraphic{background:url(${resource(dir: "images", file: "loading.png")}) no-repeat center center;}

        /* these elements are buttons, and may need to have additional styles reset to avoid unwanted base styles */
        #cboxPrevious, #cboxNext, #cboxSlideshow, #cboxClose {border:0; padding:0; margin:0; overflow:visible; width:auto; background:none; }

        /* avoid outlines on :active (mouseclick), but preserve outlines on :focus (tabbed navigating) */
        #cboxPrevious:active, #cboxNext:active, #cboxSlideshow:active, #cboxClose:active {outline:0;}

        #cboxSlideshow{position:absolute; bottom:4px; right:30px; color:#0092ef;}
        #cboxPrevious{position:absolute; bottom:0; left:0; background:url(${resource(dir: "images", file: "controls.png")}) no-repeat -75px 0; width:25px; height:25px; text-indent:-9999px;}
        #cboxPrevious:hover{background-position:-75px -25px;}
        #cboxNext{position:absolute; bottom:0; left:27px; background:url(${resource(dir: "images", file: "controls.png")}) no-repeat -50px 0; width:25px; height:25px; text-indent:-9999px;}
        #cboxNext:hover{background-position:-50px -25px;}
        #cboxClose{position:absolute; bottom:0; right:0; background:url(${resource(dir: "images", file: "controls.png")}) no-repeat -25px 0; width:25px; height:25px; text-indent:-9999px;}
        #cboxClose:hover{background-position:-25px -25px;}

        /*
          The following fixes a problem where IE7 and IE8 replace a PNG's alpha transparency with a black fill
          when an alpha filter (opacity change) is set on the element or ancestor element.  This style is not applied to or needed in IE9.
          See: http://jacklmoore.com/notes/ie-transparency-problems/
        */
        .cboxIE #cboxTopLeft,
        .cboxIE #cboxTopCenter,
        .cboxIE #cboxTopRight,
        .cboxIE #cboxBottomLeft,
        .cboxIE #cboxBottomCenter,
        .cboxIE #cboxBottomRight,
        .cboxIE #cboxMiddleLeft,
        .cboxIE #cboxMiddleRight {
            filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#00FFFFFF,endColorstr=#00FFFFFF);
        }


        @font-face {
            font-family: 'FreeSerif';
            src: url('${resource(dir: 'font', file: 'FreeSerif.otf')}');
            src: url('${resource(dir: 'font', file: 'FreeSerif.woff')}') format('woff');
            src: url('${resource(dir: 'font', file: 'FreeSerif.ttf')}') format('truetype');
            font-weight: normal;
            font-style: normal;
        }

        @font-face {
            font-family: 'FreeSerif';
            src: url('${resource(dir: 'font', file: 'FreeSerifBold.otf')}');
            src: url('${resource(dir: 'font', file: 'FreeSerifBold.woff')}') format('woff');
            src: url('${resource(dir: 'font', file: 'FreeSerifBold.ttf')}') format('truetype');
            font-weight: bold;
            font-style: normal;
        }

        @font-face {
            font-family: 'FreeSerif';
            src: url('${resource(dir: 'font', file: 'FreeSerifItalic.otf')}');
            src: url('${resource(dir: 'font', file: 'FreeSerifItalic.woff')}') format('woff');
            src: url('${resource(dir: 'font', file: 'FreeSerifItalic.ttf')}') format('truetype');
            font-weight: normal;
            font-style: italic;
        }

        @font-face {
            font-family: 'FreeSerif';
            src: url('${resource(dir: 'font', file: 'FreeSerifBoldItalic.otf')}');
            src: url('${resource(dir: 'font', file: 'FreeSerifBoldItalic.woff')}') format('woff');
            src: url('${resource(dir: 'font', file: 'FreeSerifBoldItalic.ttf')}') format('truetype');
            font-weight: bold;
            font-style: italic;
        }

        body{
            /*font-size: 13px;*/
                font-family: "FreeSerif";
        }
    </style>
    <script>
        $(function() {
            var moveLeft = 0;
            var moveDown = 0;
            $('a.popper').hover(function(e) {

                var target = '#' + ($(this).attr('data-popbox'));

                $(target).show();
                moveLeft = $(this).outerWidth();
                moveDown = ($(target).outerHeight() / 2);
            }, function() {
                var target = '#' + ($(this).attr('data-popbox'));
                $(target).hide();
            });

            $('a.popper').mousemove(function(e) {
                var target = '#' + ($(this).attr('data-popbox'));

                leftD = e.pageX + parseInt(moveLeft);
                maxRight = leftD + $(target).outerWidth();
                windowLeft = $(window).width() - 40;
                windowRight = 0;
                maxLeft = e.pageX - (parseInt(moveLeft) + $(target).outerWidth() + 20);

                if(maxRight > windowLeft && maxLeft > windowRight)
                {
                    leftD = maxLeft;
                }

                topD = e.pageY - parseInt(moveDown);
                maxBottom = parseInt(e.pageY + parseInt(moveDown) + 20);
                windowBottom = parseInt(parseInt($(document).scrollTop()) + parseInt($(window).height()));
                maxTop = topD;
                windowTop = parseInt($(document).scrollTop());
                if(maxBottom > windowBottom)
                {
                    topD = windowBottom - $(target).outerHeight() - 20;
                } else if(maxTop < windowTop){
                    topD = windowTop + 20;
                }

                $(target).css('top', topD).css('left', leftD);


            });

        });
    </script>
    <g:layoutHead/>
</head>
<body>
    <g:if test="${!showLinks}">
        <div id="header" class="clearfix">
            <div id="navmenu">
                    <g:link uri="/" elementId="homelink">Home</g:link>  |  <g:link uri="/transliteration" elementId="transliterationlink">Transliterate</g:link>  |  <g:link uri="/grammar" elementId="grammarLink">Grammar Guide</g:link>%{--  |  <g:link uri="/translation">translation</g:link>--}%  |  <g:link uri="/corpus/corpusMain" elementId="corpusMain">Corpus Main</g:link>  |  <g:link uri="/first500">First 500</g:link>  |  <g:link uri="/readingAndListening">Reading and Listening Links</g:link>  |  <g:link uri="/cnt" elementId="cntMain">Cherokee New Testament</g:link>  |  <g:link uri="/about" elementId="aboutLink">About</g:link><%if (!session.getAttribute("loggedin")){%>  |  <g:link uri="/login" elementId="loginLink">Login</g:link><%} else {%>  |  <g:link uri="/sourceManagement" elementId="sourceManagementLink">Source Management</g:link>  |  <g:link uri="/likespreadsheets/simplifiedEntry" elementId="simplifiedEntryLInk">New Entry</g:link>  |  <g:link uri="/announcements" elementId="announcementsLink">Announcements</g:link>  |  <g:link uri="/admin/logout" elementId="logoutLInk">Logout</g:link><%}%><br/><br/>
            </div>
        </div>
    </g:if>

    <g:layoutBody/>
    <br/>
    <div id="footer" style="float:left">
        <g:message code="copyright"/>
    </div>
</body>
</html>