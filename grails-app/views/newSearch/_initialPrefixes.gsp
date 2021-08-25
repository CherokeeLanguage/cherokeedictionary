<div style="display:table-row">
    <div style="display:table-cell"><b>Initial Prefixes</b></div>
</div>
<div style="display:table-row">
    <div style="display:table-cell">
        <input type="checkbox" name="yi" id="yi" <% if (request.getParameter("yi") == 'on') { %> checked="true" <% } %>/>YI<br/>
        <input type="checkbox" name="ji" id="ji" disabled <% if (request.getParameter("ji") == 'on') { %> checked="true" <% } %>/>JI<br/>
    </div>
    <div style="display:table-cell">
        <input type="checkbox" name="wi" id="wi" <% if (request.getParameter("wi") == 'on') { %> checked="true" <% } %>/>WI<br/>
    </div>
    <div style="display:table-cell">
        <input type="checkbox" name="ni" id="ni"  <% if (request.getParameter("ni") == 'on') { %> checked="true" <% } %>/>NI<br/>
    </div>
    <div style="display:table-cell">
        <input type="checkbox" name="de" id="de"  <% if (request.getParameter("de") == 'on') { %> checked="true" <% } %>/>DE<br/>
    </div>
    <div style="display:table-cell">
        <input type="checkbox" name="da" id="da" disabled <% if (request.getParameter("da") == 'on') { %> checked="true" <% } %>/>DA<br/>
        <input type="checkbox" name="di" id="di"  <% if (request.getParameter("di") == 'on') { %> checked="true" <% } %>/>DI<br/>
    </div>
    <div style="display:table-cell">
        <input type="checkbox" name="i" id="i"  <% if (request.getParameter("i") == 'on') { %> checked="true" <% } %>/>I<br/>
    </div>
    <div style="display:table-cell">
        <input type="checkbox" name="ga" id="ga"  <% if (request.getParameter("ga") == 'on') { %> checked="true" <% } %>/>GA<br/>
        <input type="checkbox" name="e" id="e"  <% if (request.getParameter("e") == 'on') { %> checked="true" <% } %>/>E<br/>
    </div>
    <br/>
</div>