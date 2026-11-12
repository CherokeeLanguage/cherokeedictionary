<%--
    AJAX fragment for KingCompareController.lookupById / lookupByTerm.
    Renders candidate Likespreadsheets matches with (unchecked) checkboxes
    that submit with the rest of the main kingCompare form.

    model: kingId, results, collapsed (boolean)
--%>
<% if (!results) { %>
<span class="noMatch">no RRD entry found</span>
<% } %>
<% results.each { tsalagi ->
       String divId = "manual_${kingId}_${tsalagi.id}"
%>
<div class="rrdBlock">
    <div class="rrdSummary">
        <input type="checkbox" name="match" value="<%= kingId %>:<%= tsalagi.id %>"/>
        <b><%= tsalagi.entrya %></b> &mdash; <%= tsalagi.definitiond %>
        <% if (collapsed) { %>
        <a class="toggleLink" href="javascript:void(0)" onclick="toggleRrd('<%= divId %>')">[click to show/hide table]</a>
        <% } %>
    </div>
    <div id="<%= divId %>" style="display: <%= collapsed ? 'none' : 'block' %>;">
        <g:render template="/kingCompare/rrdTable" model="[tsalagi: tsalagi]"/>
    </div>
</div>
<% } %>
