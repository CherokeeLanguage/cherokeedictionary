<%@ page import="net.cherokeedictionary.admin.SourceManagement" %>
<html>
    <head>
        <meta name="layout" content="manager"/>
        <title>Stemmers</title>
        <style>
             td, th {
                 line-height: 1.5em;
                 padding: 0.5em 0.6em;
                 text-align: left;
                 vertical-align: top;
                 border: 1px solid black;
             }
            table {
                border-top: 1px solid #DFDFDF;
                border-collapse: collapse;
                width: 100%;
                margin-bottom: 1em;
            }

            tr {
                border: 0;
            }

            tr>td:first-child, tr>th:first-child {
                padding-left: 1.25em;
            }

            tr>td:last-child, tr>th:last-child {
                padding-right: 1.25em;
            }

            .odd {
                background: #f7f7f7;
            }

            .even {
                background: #ffffff;
            }

            @media screen and (min-width: 0px) and (max-width: 640px) {
                .smallishes { display: block; }  /* show it on small screens */
                .large { display: none; }   /* hide it elsewhere */
                .accordion {
                    /*background-color: #eee;*/
                    color: #444;
                    cursor: pointer;
                    padding: 18px;
                    width: 100%;
                    text-align: left;
                    border: none;
                    outline: none;
                    transition: 0.4s;
                }

                /* Add a background color to the button if it is clicked on (add the .active class with JS), and when you move the mouse over it (hover) */
                .active, .accordion:hover {
                    background-color: #ccc;
                }

                /* Style the accordion panel. Note: hidden by default */
                .panel {
                    padding: 0 18px;
                    background-color: white;
                    display: none;
                    overflow: hidden;
                }
            }

            @media screen and (min-width: 641px) {
                .smallishes { display: none; }   /* hide it elsewhere */
                .large { display: block; }  /* show it on small screens */
                /*sup { vertical-align: top; position: relative; top: -0.2em; }*/

                th:hover, tr:hover {
                    background: #E1F2B6;
                }

                /*fix for 14*/
                /* To change position of close button to Top Right Corner */
                #colorbox #cboxClose
                {
                    top: 0;
                    right: 0;
                }
                #cboxLoadedContent{
                    margin-top:28px;
                    margin-bottom:0;
                }
            }
        </style>
    </head>
    <body>
        <h1>Stemmers</h1>
        <table class="table table-striped table-bordered" style="">
            <thead>
            <tr>
                <th>
                    <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; text-transform:lowercase;width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
                        source
                    </div>
                </th>
                <th>#</th>
                <th>Definition</th>
                <th>Part of Speech</th>
                <th>3rd Pres</th>
                <th>3rd Pres (Stemmer)</th>
                <th>3rd Pres (King)</th>
                <th>1st Pres</th>
                <th>1st Pres (Stemmer)</th>
                <th>1st Pres (King)</th>
                <th>3rd Past</th>
                <th>3rd Past (Stemmer)</th>
                <th>3rd Past (King)</th>
                <th>3rd Pres Habitual</th>
                <th>3rd Pres Habitual (Stemmer)</th>
                <th>3rd Pres Habitual (King)</th>
                <th>2nd Imperative</th>
                <th>2nd Imperative (Stemmer)</th>
                <th>2nd Imperative (King)</th>
                <th>3rd Inf</th>
                <th>3rd Inf (Stemmer)</th>
                <th>3rd Inf (King)</th>
            </tr>
            </thead>
            <tbody>

            <g:if test="${results}">
                <g:each in="${results}" var="stem" status="i">
                    <tr>
                        <% def sourceMgmt = net.cherokeedictionary.admin.SourceManagement.findByCode(stem?.source) %>
                        <td style="width:1px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;background-color:${sourceMgmt?.color};text-color${sourceMgmt?.textColor}">
                            <div style="transform: rotate(90deg); -webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -ms-transform: rotate(90deg); position:relative; top: 5px; text-align:center; display:inline-block; text-transform:lowercase;width:20px; margin: 0 0 0 0; padding: 0 0 0 0; border-spacing: 0;">
                                <a href="#" class="popper" data-popbox="tsalagidigoweli2" style="<% println "color:${sourceMgmt?.textColor};"%>">
                                    ${sourceMgmt.code}
                                </a>
                            </div>
                            <div id="tsalagidigoweli2" class="popbox">
                            <%-- not sure why the colors work above but here the code is always the same - i'm missing something - timo 4may21--%>
                            %{--                    <% out << sourceMgmt.code--}%
                            %{--                    out << sourceMgmt.bibliographyFullAPA %>--}%
                                <g:each var="source" in="${SourceManagement.findAll([sort: 'code', order: 'asc'])}">
                                    <b>${source.code.toUpperCase()}</b> - ${source.bibliographyFullAPA}<br/>
                                </g:each>
                            </div>
                        </td>
                        <td>${stem?.likespreadsheetsid}</td>
                        <td>${stem?.definition}</td>
                        <td>${stem?.partofspeech}</td>
                        <td>${stem?.vthirdpres}</td>
                        <td>${stem?.vthirdpresStemmer}</td>
                        <td>${stem?.vthirdpresKing}</td>
                        <td>${stem?.vfirstpres}</td>
                        <td>${stem?.vfirstpresStemmer}</td>
                        <td>${stem?.vfirstpresKing}</td>
                        <td>${stem?.vthirdpast}</td>
                        <td>${stem?.vthirdpastStemmer}</td>
                        <td>${stem?.vthirdpastKing}</td>
                        <td>${stem?.vthirdpreshabitual}</td>
                        <td>${stem?.vthirdpresStemmerhabitual}</td>
                        <td>${stem?.vthirdpresKinghabitual}</td>
                        <td>${stem?.vsecondimper}</td>
                        <td>${stem?.vsecondimperStemmer}</td>
                        <td>${stem?.vsecondimperKing}</td>
                        <td>${stem?.vthirdinf}</td>
                        <td>${stem?.vthirdinfStemmer}</td>
                        <td>${stem?.vthirdinfKing}</td>
                    </tr>
                </g:each>
            </g:if>
            <g:else>
                <tr>
                    <td colspan="24">No results to display.</td>
                </tr>
            </g:else>
%{--            <g:if test="${results}">--}%
%{--                <g:each in="${results}" var="verb" status="i">--}%
%{--                    <tr>--}%
%{--                        <td>${i + 1}</td>--}%
%{--                        <td>${verb?.stemmer}</td>--}%
%{--                        <td>${verb?.tense}</td>--}%
%{--                        <td>${verb?.verbset}</td>--}%
%{--                        <td>${verb?.compoundPrefix}</td>--}%
%{--                        <td>${verb?.root}</td>--}%
%{--                        <td>${verb?.rootEnding}</td>--}%
%{--                        <td>${verb?.wholeWord}</td>--}%
%{--                        <td>${verb?.pronounReflexiveRoot}</td>--}%
%{--                    </tr>--}%
%{--                </g:each>--}%
%{--            </g:if>--}%
%{--            <g:else>--}%
%{--                <tr>--}%
%{--                    <td colspan="9">No results to display.</td>--}%
%{--                </tr>--}%
%{--            </g:else>--}%
            </tbody>
        </table>
    </body>
</html>
