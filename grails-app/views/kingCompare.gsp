<%@page import="net.cherokeedictionary.dictionary.NewSearchService; net.cherokeedictionary.dictionary.King; net.cherokeedictionary.dictionary.KingRRDLookup; net.cherokeedictionary.dictionary.Likespreadsheets; net.cherokeedictionary.transliteration.SyllabaryUtil" %>
<%--
From the King Dictionary
1. All letters should be considered lower case except the first letter in the family, order, or genus names of plant and animal species.
2. In the Cherokee entries:
a. A question mark (?) should be interpreted as a glottal stop (?).
b. A slash (/) should be interpreted as an acute accent mark (') over the preceding vowel.
c. A period (.) should be interpreted as vowel length (v).
3. Cherokee verbs are entered by either root or stem and are designated VBST in columns 16 through 20 on the key punch cards.

Columns 22 through 47 contain the aspect suffixes required by the verb and are listed in the following order:

present (columns 22-24),
imperfective (26-29),
perfective (31-34),
imperative (36-40), and
infinitive (42-47).

The imperative entry (columns 36-40) contains both aspect and modal suffixes.
An asterisk (*) in any cf these columns indicates that the aspect suffix replaces the last letter in the verb root.

The symbol @ indicates that multiple letters are replaced.
A few verb stems which are altered by suppletion with certain aspect suffixes are designated IRR (irregular in the aspect suffix column).

The first person singular pronominal prefix is entered in columns 71-74,
the third person singular prefix is found in column 75-76,
and obligatory prepronominal prefixes are listed in columns 79- 80 where applicable.
A dash (-) in column 71 indicates an intransitive verb which is not used with a first person pronominal prefix

A zero (0) in column 75 indicates Class II stems with vowel initials which have zero representation of third person singular subjects.

Classificatory verbs have the class recorded in parentheses following the English translation.
Because of space some are abbreviated, e. g. , FLEX, FLX, and FL flexible, ANIM and AN animate, RG rigid, RD round, and LQ liquid.

4. Noun roots are designated NR in columns 19 and 20
The first person singular pronominal prefix is listed in columns 22-24 and third person singular prefix is given in columns 27-28.
The prefix U* indicates that this prefix replaces the initial vowel /a/b y a regular morphophonemic rule.
Commonly used plurals are given in column 32 and following columns.
If two plural forms are listed, the first is attached to the first person singular pronominal, the second to the third person singular prefix.
5. The following designations in columns 19 and 20 indicate affixes: PP pre pronominal prefix, *PR pronominal prefix, DS derivative suffix, MS modal suffix, and ATS attributive suffix. The numeral following * PR is correlated with the numbers on Chart 7.
6. All Cherokee entries other than those designated VBST, NR, PP, *PR, DS, MS, and ATS are complete words.
7. Names of plants and animals are designated by GN in columns 19 and 20. Genus and species names are provided in columns 22-47. The family or order names are listed for terms which apply to more than one genus.
8. Nouns derived from verbs are designated DN, particles PT, and nouns which cannot be inflected by a pronominal prefix NU.
9. In the English to Cherokee section columns 48- 8 0 are printed on the left side of the page, with columns 1-47 on the right
10. The alphabetical order used by the computer is as follows: blank space, .,/,? , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z.
--%>
<%
    def convert = {toConvert ->
        toConvert = toConvert
                .replaceAll("\\?", "")
                .replaceAll("\\.", "")
                .replaceAll("/", "")
                .replaceAll("0", "")
                .replaceAll("hs", "s")
                .replaceAll("kw", "gw")
                .replaceAll("uh", "u")
                .replaceAll("kh", "k")
                .replaceAll("to", "do")
                .replaceAll("th", "t")
                .replaceAll("hna", "na")
                .replaceAll("-", "")
                .replaceAll("ke", "ge")
                .replaceAll("ki", "gi")
                .replaceAll("ko", "go")
                .replaceAll("ku", "gu")
                .replaceAll("kv", "gv")
                .replaceAll("hl", "l")
                .replaceAll("tiha", "diha")
                .replaceAll("ehl", "el")
                .replaceAll("sk", "sg")

        if (toConvert.startsWith("ua")) {
            toConvert = "a" + toConvert.substring(2)
        }

        if (toConvert.endsWith("ka")) {
            toConvert = toConvert.substring(0, toConvert.length() - 2) + "ga"
        }

        if (toConvert.endsWith("ti")) {
            toConvert = toConvert.substring(0, toConvert.length() - 2) + "di"
        }

        //SyllabaryUtil.replace(toConvert, true)
        return toConvert
    }

    def createLookup = { searchTerm ->
        // Combine the field lists
        def allFields = NewSearchService.tsalagiFields + NewSearchService.tsalagiTranslitFields

        def tmp = Likespreadsheets.createCriteria().list(max: 40) {
            // only RRD-sourced entries participate in matching
            eq('source', 'rrd')
            or {
                for (tsalagi in allFields) {
                    rlike(tsalagi, searchTerm)
                }
            }
        }

        return tmp
    }

    def lookups = { look ->
        def result = Likespreadsheets.find(
                "from Likespreadsheets l where l.source = 'rrd' and (l.entrya like ?0 or l.entrytranslit like ?1)",
                [look, look]
        )

        if (!result) {
            result = createLookup(look)
        } else {
            result = [result]
        }

        return result
    }

    def replaceAsterisk = {king, entry, tmpOutput ->
        if (king."$entry".contains("*")) {
            tmpOutput << king.nounRoot.substring(0, king.nounRoot.size() - 1)
            tmpOutput << king."$entry".substring(1)
        } else {
            tmpOutput << king.nounRoot
            tmpOutput << king."$entry"
        }
    }

    def kingMain = { king ->
        def tmpOutput = new StringBuilder()
        tmpOutput << king.thirdPresSing

        replaceAsterisk(king, "present", tmpOutput)

        tmpOutput << "a"
        tmpOutput = convert(tmpOutput.toString())
        return tmpOutput
    }
%>
<script type="text/javascript">
    function toggleRrd(divId) {
        var el = document.getElementById(divId);
        el.style.display = (el.style.display === "none") ? "block" : "none";
    }

    var manualByIdUrl = "${createLink(controller: 'kingCompare', action: 'lookupById')}";
    var manualByTermUrl = "${createLink(controller: 'kingCompare', action: 'lookupByTerm')}";

    function manualLookup(kingId, mode) {
        var url;
        if (mode === 'id') {
            var lsId = document.getElementById('lsid_' + kingId).value.trim();
            if (!lsId) { return; }
            url = manualByIdUrl + '?kingId=' + kingId + '&id=' + encodeURIComponent(lsId);
        } else {
            var term = document.getElementById('term_' + kingId).value.trim();
            if (!term) { return; }
            url = manualByTermUrl + '?kingId=' + kingId + '&term=' + encodeURIComponent(term);
        }

        fetch(url)
            .then(function (r) { return r.text(); })
            .then(function (html) {
                document.getElementById('manual_' + kingId).innerHTML = html;
            })
            .catch(function (e) {
                document.getElementById('manual_' + kingId).innerHTML =
                    '<span class="noMatch">lookup failed: ' + e + '</span>';
            });
    }

    function manualEnter(event, kingId, mode) {
        if (event.key === 'Enter') {
            manualLookup(kingId, mode);
            return false;
        }
        return true;
    }
</script>
<style>
    td {
        vertical-align: top;
        text-align: left;
    }

    .mainRow {
        background-color: #f2f2f2;
    }

    .kingEntry {
        width: 160px;
    }

    .kingDefinition {
        width: 260px;
    }

    .rrdCell {
        width: 70%;
    }

    .rrdBlock {
        margin-bottom: 6px;
        padding-bottom: 6px;
        border-bottom: 1px solid #cccccc;
    }

    .rrdSummary {
        font-weight: bold;
    }

    .rrdTable {
        margin-top: 4px;
        border-collapse: collapse;
    }

    .rrdTable td, .rrdTable th {
        border: 1px solid #999999;
        padding: 2px 4px;
        font-size: 11px;
        white-space: nowrap;
    }

    .rrdTable th {
        background-color: #d9d9d9;
    }

    .noMatch {
        color: #b30000;
        font-weight: bold;
    }

    .toggleLink {
        font-weight: normal;
        font-size: 11px;
        color: #0000cc;
    }
</style>

<g:if test="${flash.message}">
    <div style="padding:8px; margin-bottom:10px; background-color:#dfffdf; border:1px solid #4f8a10;">
        <b>${flash.message}</b>
    </div>
</g:if>

<%
    def list = King.list()//max: 10
    def unfoundList = []
    def filter = params.filter ?: 'all'

    // King ids that already have at least one checked (saved) match
    def savedKingIds = KingRRDLookup.findAll()*.kingid as Set<Integer>
    int totalVbst = 0
    int shownCount = 0
    int withMatchCount = 0
    int withoutMatchCount = 0
    int withoutIndex = 0
    // paging for the "without matches" view: 10 entries per page (override with ?max=)
    int pageSize = params.int('max') ?: 10
    int offset = params.int('offset') ?: 0
%>

<div style="margin-bottom: 10px;">
    <b>Show:</b>
    <a href="?filter=all" <%= filter == 'all' ? 'style="font-weight:bold"' : '' %>>all</a> |
    <a href="?filter=with" <%= filter == 'with' ? 'style="font-weight:bold"' : '' %>>only entries WITH checked matches</a> |
    <a href="?filter=without" <%= filter == 'without' ? 'style="font-weight:bold"' : '' %>>only entries WITHOUT checked matches</a>
</div>

<form method="post" action="${createLink(controller: 'kingCompare', action: 'saveMatches')}">
    <button type="submit">Save checked matches</button>
    <table>
        <tr>
            <th>King entry</th>
            <th>King definition</th>
            <th>RRD matches (checked = match)</th>
        </tr>

        <% list.each { king ->
            if (king.useCase == "vbst" && !king.present.contains("see ")) {
                totalVbst++

                // "checked" means this King entry has at least one saved match
                boolean hasMatch = savedKingIds.contains(king.id as Integer)
                if (hasMatch) { withMatchCount++ } else { withoutMatchCount++ }

                // on the "without" view only show one page of pageSize unmatched entries
                if (filter == 'without') {
                    int idx = withoutIndex
                    withoutIndex++
                    if (idx < offset || idx >= offset + pageSize) { return }
                }

                if (filter == 'with' && !hasMatch) { return }
                if (filter == 'without' && hasMatch) { return }
                shownCount++

                def tmpOutput = kingMain(king)

                // previously saved matches for this King entry
                def existingCedIds = KingRRDLookup.findAllByKingid(king.id)*.cedid as Set<Integer>

                def tsalagiList
                if (hasMatch) {
                    // already has checked boxes: just display the saved matches,
                    // don't run the automatic lookups again
                    tsalagiList = existingCedIds.collect { Likespreadsheets.get(it) }.findAll { it != null }
                } else {
                    tsalagiList = lookups(tmpOutput)
                }

                if (tsalagiList.isEmpty()) {
                    unfoundList << king
                }
        %>
        <tr class="mainRow">
            <td class="kingEntry"><%= tmpOutput %> (id <%= king.id %>)</td>
            <td class="kingDefinition"><%= king.definition %></td>
            <td class="rrdCell">
                <input type="hidden" name="kingids" value="<%= king.id %>"/>
                <% if (hasMatch) { %>
                <span style="color:#4f8a10; font-weight:bold;">HAS SAVED MATCH(ES)</span><br/>
                <% } %>
                <% if (tsalagiList.isEmpty()) { %>
                <span class="noMatch">NO RRD MATCH FOUND</span>
                <div style="margin-top:4px;">
                    paste Likespreadsheets id:
                    <input type="text" id="lsid_<%= king.id %>" size="8"
                           onkeydown="return manualEnter(event, <%= king.id %>, 'id')"/>
                    <button type="button" onclick="manualLookup(<%= king.id %>, 'id')">look up id</button>
                    <br/>
                    search english:
                    <input type="text" id="term_<%= king.id %>" size="30"
                           value="<%= (king.definition ?: '').replaceAll('"', '&quot;') %>"
                           onkeydown="return manualEnter(event, <%= king.id %>, 'term')"/>
                    <button type="button" onclick="manualLookup(<%= king.id %>, 'term')">search</button>
                </div>
                <div id="manual_<%= king.id %>"></div>
                <% } else {
                       tsalagiList.each { tsalagi ->
                           // exactly one result (or already saved) => checked by default, still uncheckable
                           boolean autoChecked = tsalagiList.size() == 1 || existingCedIds.contains(tsalagi.id as Integer)
                           String divId = "rrd_${king.id}_${tsalagi.id}"
                           // saved-match rows show their tables right away; fresh multi-match rows start collapsed
                           boolean collapsed = !hasMatch && tsalagiList.size() > 1
                %>
                <div class="rrdBlock">
                    <div class="rrdSummary">
                        <input type="checkbox" name="match" value="<%= king.id %>:<%= tsalagi.id %>" <%= autoChecked ? 'checked' : '' %>/>
                        <b><%= tsalagi.entrya %></b> &mdash; <%= tsalagi.definitiond %>
                        <% if (collapsed) { %>
                        <a class="toggleLink" href="javascript:void(0)"
                           onclick="toggleRrd('<%= divId %>')">[<%= tsalagiList.size() %> matches &mdash; click to show/hide table]</a>
                        <% } %>
                    </div>
                    <div id="<%= divId %>" style="display: <%= collapsed ? 'none' : 'block' %>;">
                        <g:render template="/kingCompare/rrdTable" model="[tsalagi: tsalagi]"/>
                    </div>
                </div>
                <% } %>
                <% } %>
            </td>
        </tr>
        <% } } %>
    </table>
    <button type="submit">Save checked matches</button>
</form>

<g:if test="${filter == 'without' && withoutMatchCount > 0}">
    <div style="margin-top: 10px; font-weight: bold;">
        Showing unmatched entries <%= Math.min(offset + 1, withoutMatchCount) %>&ndash;<%= Math.min(offset + pageSize, withoutMatchCount) %> of <%= withoutMatchCount %>
        <% if (offset > 0) { %>
        &nbsp;&nbsp;<a href="?filter=without&offset=<%= Math.max(offset - pageSize, 0) %>&max=<%= pageSize %>">&laquo; previous <%= pageSize %></a>
        <% } %>
        <% if (offset + pageSize < withoutMatchCount) { %>
        &nbsp;&nbsp;<a href="?filter=without&offset=<%= offset + pageSize %>&max=<%= pageSize %>">next <%= pageSize %> &raquo;</a>
        <% } %>
    </div>
</g:if>

<div style="margin-top: 20px;">
    <b>Summary:</b>
    <%= totalVbst %> vbst King entries total,
    <%= withMatchCount %> with checked matches,
    <%= withoutMatchCount %> without,
    <%= shownCount %> shown by current filter (<%= filter %>).
</div>
<%--
<g:if test="${filter == 'all'}">
<div style="margin-top: 20px; font-weight: bold;">Unfound List -- <%= unfoundList.size() %></div>

<% unfoundList.each { king ->
    def tmpOutput = kingMain(king)
%>
<div><%= tmpOutput %>|<%= king.nounRoot %>|<%= king.definition %></div>
<% } %>
</g:if>
--%>