<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Migration Preview — Class Design Review</title>
    <style>
        body { font-family: "Segoe UI", Arial, sans-serif; background: #f4f5f7; margin: 0; padding: 20px; color: #222; }
        h1 { font-size: 1.4em; }
        .legend { color: #666; font-size: .85em; margin-bottom: 20px; }
        /* one legacy row */
        .row-block { background: #fff; border: 1px solid #ccc; border-radius: 8px; margin-bottom: 30px; padding: 16px; }
        .row-header { background: #2c3e50; color: #fff; padding: 8px 12px; border-radius: 6px; margin-bottom: 14px; font-size: 1.05em; }
        .row-header .raw { display: block; font-size: .8em; color: #bbb; margin-top: 4px; font-family: monospace; }
        /* entity cards laid out as flex wrap */
        .entities { display: flex; flex-wrap: wrap; gap: 14px; align-items: flex-start; }
        .entity-card { background: #fafbfd; border: 1px solid #bbb; border-radius: 6px; min-width: 260px; flex: 0 1 auto; overflow: hidden; }
        .entity-card h3 { margin: 0; padding: 6px 10px; font-size: .95em; color: #fff; }
        table.fields { border-collapse: collapse; width: 100%; font-size: .88em; }
        table.fields th { text-align: left; background: #eef1f5; padding: 4px 10px; border-bottom: 1px solid #ddd; white-space: nowrap; color: #555; font-weight: 600; }
        table.fields td { padding: 4px 10px; border-bottom: 1px solid #eee; vertical-align: top; }
        table.fields tr:last-child th, table.fields tr:last-child td { border-bottom: none; }
        .empty { color: #aaa; font-style: italic; }
        /* per-class header colours */
        .cls-entry h3 { background: #2e6da4; }
        .cls-sense h3 { background: #4a90d9; }
        .cls-wordform h3 { background: #6a8d5c; }
        .cls-examples h3 { background: #a2764d; }
        .cls-lookup h3 { background: #808080; }
        .lookup-group { display: flex; gap: 10px; flex-wrap: wrap; }
        .lookup-group .entity-card { min-width: 170px; }
    </style>
</head>
<body>
    <h1>Migration Preview — Class Design Review</h1>
    <p class="legend">
        Each legacy <code>Likespreadsheets</code> row is displayed as the entity tables it would become.
        Grey = lookup tables, blue = entry/sense, green = word forms, brown = example sentence.
        Empty fields are shown deliberately so you can review the full field list per class.
    </p>

    <g:each in="${results}" var="row" status="i">
        <div class="row-block">
            <div class="row-header">
                Legacy row ${i + 1}: <strong>${row.legacy.entrya}</strong> — <strong>${row.legacy.syllabary}</strong> <span style="color:#9fd;">(${row.legacy.pos})</span>
                <span class="raw">raw definitiond: ${row.rawDefinition}
                    <g:link action="entry" params="[search: row.legacy.entrya]" style="color:#7fb3ff; font-family:'Segoe UI'">full entry &#8595;</g:link>
                </span>
            </div>

            <div class="entities">
                <%-- ================= DictionaryEntry card ================= --%>
                <div class="entity-card cls-entry">
                    <h3>RelTestDictionaryEntry</h3>
                    <table class="fields">
                        <tr><th>source</th><td><g:if test="${row.dictionaryEntry.source}">${row.dictionaryEntry.source}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        <tr><th>etymology</th><td><g:if test="${row.dictionaryEntry.etymology}">${row.dictionaryEntry.etymology}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        <tr><th>notes</th><td><g:if test="${row.dictionaryEntry.notes}">${row.dictionaryEntry.notes}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        <tr><th>crossReference</th><td><g:if test="${row.dictionaryEntry.crossReference}">${row.dictionaryEntry.crossReference}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                    </table>
                </div>

                <%-- ================= Lookups card ================= --%>
                <div class="lookup-group">
                    <div class="entity-card cls-lookup">
                        <h3>RelTestPartOfSpeech</h3>
                        <table class="fields">
                            <tr><th>partOfSpeech</th><td><g:if test="${row.partOfSpeech}">${row.partOfSpeech}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        </table>
                    </div>
                    <div class="entity-card cls-lookup">
                        <h3>RelTestCategory</h3>
                        <table class="fields">
                            <tr><th>category</th><td><g:if test="${row.category}">${row.category}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        </table>
                    </div>
                </div>

                <%-- ================= Sense cards (one per split) ================= --%>
                <div class="entity-card cls-sense">
                    <h3>RelTestSense &times; ${row.senses.size()}</h3>
                    <table class="fields">
                        <g:each in="${row.senses}" var="s" status="n">
                            <tr><th>sense ${n + 1}</th><td><strong>${s.definition}</strong></td></tr>
                            <tr><td></td><td style="color:#777">definitionLarge: <g:if test="${s.definitionLarge}">${s.definitionLarge}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                            <tr><td></td><td style="color:#777">pos: ${s.pos} | category: <g:if test="${s.category}">${s.category}</g:if><g:else><span class="empty">(empty)</span></g:else> | dialect: <g:if test="${s.dialect}">${s.dialect}</g:if><g:else><span class="empty">(none)</span></g:else></td></tr>
                            <tr><td></td><td style="color:#777">hasMany: forms (WordForm), examples (ExampleSentence)</td></tr>
                        </g:each>
                    </table>
                </div>

                <%-- ================= WordForm cards (one per form) ================= --%>
                <div class="entity-card cls-wordform">
                    <h3>RelTestWordForm &times; ${row.wordForms.size()}</h3>
                    <table class="fields">
                        <g:each in="${row.wordForms}" var="f">
                            <tr><th>wordType</th><td>${f.type}</td></tr>
                            <tr><td></td><td>latin: <g:if test="${f.latin}">${f.latin}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                            <tr><td></td><td>syllabary: <g:if test="${f.syllabary}">${f.syllabary}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                            <tr><td></td><td>translit: <g:if test="${f.translit}">${f.translit}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                            <tr><td></td><td>tone: <g:if test="${f.tone}">${f.tone}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        </g:each>
                    </table>
                </div>

                <%-- ================= ExampleSentence card ================= --%>
                <div class="entity-card cls-examples">
                    <h3>RelTestExampleSentence</h3>
                    <table class="fields">
                        <tr><th>syllabary</th><td><g:if test="${row.exampleSentence.syllabary}">${row.exampleSentence.syllabary}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        <tr><th>translit</th><td><g:if test="${row.exampleSentence.transliteration}">${row.exampleSentence.transliteration}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        <tr><th>english</th><td><g:if test="${row.exampleSentence.english}">${row.exampleSentence.english}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                        <tr><th>legacyNote</th><td><g:if test="${row.exampleSentence.legacyNote}">${row.exampleSentence.legacyNote}</g:if><g:else><span class="empty">(empty)</span></g:else></td></tr>
                    </table>
                </div>
            </div>
        </div>
    </g:each>
</body>
</html>
