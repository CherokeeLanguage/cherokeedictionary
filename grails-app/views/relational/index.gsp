<%--

Do lookup from king
assemble verbs and normalize
do lookups from likespreadsheets


--%>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Word List Display</title>
<style>
      td, th {
          line-height: 1.5em;
          padding: 0.5em 0.6em;
          text-align: left;
          vertical-align: top;
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

      .tableCell {
          display: table-cell;
          padding: 0 5px 0 0;
      }
      </style>
</head>
<body>
    <h2>Dictionary Word List Entries</h2>

    <g:if test="${wordList}">
        <g:each in="${wordList}" var="item" status="i">
            <div class="accordion ${(i % 2) == 0 ? 'even' : 'odd'}" style="margin: 0 0 0 0;padding: 0 0 0 0; border-spacing: 0; width:1000px;" id="accordionClick${i}">
                <div style="display:table-cell;width:950px;">
                    <div style="display:table-cell;vertical-align: middle;padding-left:10px;padding-top:5px; padding-bottom:5px">
                            ${item.king.nounRoot} - ${item.likespreadsheets.entrya}<br>
                    </div>
                </div>

                <div style="display:table-cell;width:50px;padding-bottom:5px;vertical-align:middle;text-align:right;" id="arrowClick${i}"><i class="fas fa-sort-up fa-2x"></i></div>

                <script>
                    $(function() {
                        $('#accordionClick${i}').click(function() {
                            var downarrow = $('#arrowClick${i}').children('.fa-sort-down');
                            var uparrow = $('#arrowClick${i}').children('.fa-sort-up');
                            if (downarrow != null) {
                                downarrow.attr('class', 'fas fa-sort-up fa-2x');
                            }

                            if (uparrow != null) {
                                uparrow.attr('class', 'fas fa-sort-down fa-2x');
                            }
                        });
                    });
                </script>
            </div>

            <div class="panel" style="display: none;">
                <div style="display:table-row">
                    <div class="tableCell">Third Present Singular</div>
                    <div class="tableCell">Root</div>
                    <div class="tableCell">Part of Speech</div>
                    <div class="tableCell">First Present Singular</div>
                    <div class="tableCell">Present</div>
                    <div class="tableCell">Imperfect</div>
                    <div class="tableCell">Perfective</div>
                    <div class="tableCell">Imperative</div>
                    <div class="tableCell">Infinitive</div>
                    <div class="tableCell">Definition</div>
                    <div class="tableCell">Prepronominal Prefix</div>
                    <div class="tableCell">Notes</div>
                </div>
                <div style="display:table-row">
                    <div class="tableCell">${item.king.thirdPresSing}</div>
                    <div class="tableCell">${item.king.nounRoot}</div>
                    <div class="tableCell">${item.king.useCase}</div>
                    <div class="tableCell">${item.king.firstPresSing}</div>
                    <div class="tableCell">${item.king.present}</div>
                    <div class="tableCell">${item.king.imperf}</div>
                    <div class="tableCell">${item.king.perfective}</div>
                    <div class="tableCell">${item.king.imperative}</div>
                    <div class="tableCell">${item.king.infintive}</div>
                    <div class="tableCell">${item.king.definition}</div>
                    <div class="tableCell">${item.king.prepronomPrefix}</div>
                    <div class="tableCell">${item.king.notes}</div>
                </div>
                <div style="display:table-row">
                    <div class="tableCell">${item.likespreadsheets.entrya}</div>
                    <div class="tableCell"></div>
                    <div class="tableCell">${item.likespreadsheets.partofspeechc}</div>
                    <div class="tableCell">${item.likespreadsheets.vfirstpresg}</div>
                    <div class="tableCell">${item.likespreadsheets.entrya}</div>
                    <div class="tableCell">${item.likespreadsheets.vthirdpresk}</div>
                    <div class="tableCell">${item.likespreadsheets.vthirdpasti}</div>
                    <div class="tableCell">${item.likespreadsheets.vsecondimperm}</div>
                    <div class="tableCell">${item.likespreadsheets.vthirdinfo}</div>
                    <div class="tableCell">${item.likespreadsheets.definitiond}</div>
                    <div class="tableCell"></div>
                    <div class="tableCell"></div>
                </div>
            </div>
        </g:each>
    </g:if>
    <g:else>
        <p>No entries found in wordList.</p>
    </g:else>
    <script>
        var acc = document.getElementsByClassName("accordion");
        var i;

        for (i = 0; i < acc.length; i++) {
            acc[i].addEventListener("click", function() {
                /* Toggle between adding and removing the "active" class,
                to highlight the button that controls the panel */
                this.classList.toggle("active");

                /* Toggle between hiding and showing the active panel */
                var panel = this.nextElementSibling;
                if (panel.style.display === "block") {
                    panel.style.display = "none";
                } else {
                    panel.style.display = "block";
                }
            });

            // acc[i].click();
        }
    </script>
</body>
</html>