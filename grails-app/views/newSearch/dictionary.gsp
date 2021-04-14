<%--
/**
 *
 * @author torr
 * @since 7/11/13 2:16 PM
 */
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head>
      %{-- https://www.thesitewizard.com/css/mobile-friendly-responsive-design.shtml --}%
      <meta name="layout" content="manager"/>
      <title>Dictionary</title>
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
      </style>
     <script>
         $(function() {
             $(".iframe").colorbox({iframe: true, width: "80%", height: "80%", escKey:true});
         });
     </script>
    </head>
    <body>
    <%
        if (session.getAttribute("loggedin")) {
            out << raw("<div style=\"color : red\">you are logged in</div>")
        }
    %>
    <div id="message"></div>
    <b>If you see <i class="fas fa-plus-square"></i> click on the link next to it to see more about the word.</b><br/>

    <!-- desktop view -->
    <div class="large">
        <a href="/newSearch/download/csv">Download results as CSV</a>&nbsp;&nbsp;
        <a href="/newSearch/download/tab">Download results as Tab Delimited</a>&nbsp;&nbsp;
        <a href="/newSearch/download/excel">Download results as Excel Spreadsheet</a>
%{--        <% if (session.getAttribute("loggedin")) { %>--}%
        <a href="/newSearch/download/pdf">Download results as PDF</a>
%{--        <% } %>--}%
        <br/><br/>
%{--        <a href="/newSearch/download/pdf">Download results as Excel Spreadsheet</a><br/>--}%
        <g:render template="/newSearch/desktopView" model="[entries:entries]"/>
    </div>
    <!-- mobile view -->
    <div class="smallishes">
        <!-- desktop view -->
        <g:render template="/newSearch/mobileView"  model="[entries:entries]"/>
    </div>
    </body>
</html>
