<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Full Entry View — ${entry?.legacy?.entrya ?: search}</title>
    <style>
        body { font-family: Georgia, "Times New Roman", serif; background: #f4f5f7; margin: 0; padding: 20px; color: #222; }
        .searchbar { font-family: "Segoe UI", Arial, sans-serif; background: #fff; border: 1px solid #ccc; border-radius: 8px; padding: 10px 14px; margin-bottom: 20px; }
        .searchbar input { padding: 4px 8px; }
        .searchbar a { color: #2e6da4; text-decoration: none; }
        .error { background: #fdecea; border: 1px solid #e0b4b4; border-radius: 8px; padding: 14px; font-family: "Segoe UI", Arial, sans-serif; }
        /* ================= complete entry ================= */
        .entry-sheet { background: #fff; border: 1px solid #999; box-shadow: 0 2px 6px rgba(0,0,0,.15); border-radius: 4px; padding: 26px 30px; }
        .headword h1 { font-size: 2em; margin: 0; }
        .headword .syll { font-size: 2.2em; color: #111; margin-right: .3em; }
        .headword .latin { font-style: italic; color: #333; }
        .headword .tone { color: #8a5a22; font-family: "Segoe UI", Arial, sans-serif; font-size: .8em; }
        .headword .pos { font-style: italic; color: #555; margin-left: .5em; }
        .label { font-family: "Segoe UI", Arial, sans-serif; font-size: .72em; text-transform: uppercase; letter-spacing: .08em; color: #8899aa; margin: 18px 0 4px; border-bottom: 1px solid #e4e7ec; padding-bottom: 2px; }
        ul.forms { list-style: none; margin: 0; padding: 0; font-family: "Segoe UI", Arial, sans-serif; font-size: .9em; column-gap: 30px; }
        ul.forms li { margin: 2px 0; break-inside: avoid; }
        ul.forms .tag { display: inline-block; min-width: 200px; color: #555; }
        ol.senses { margin: 0; padding-left: 26px; font-size: 1.05em; }
        ol.senses li { margin: 4px 0; }
        ol.senses .pos { font-style: italic; color: #777; }
        ol.senses .cat { color: #8899aa; font-family: "Segoe UI", Arial, sans-serif; font-size: .75em; }
        .example { background: #fbf7ef; border-left: 3px solid #a2764d; padding: 10px 14px; margin: 8px 0; font-size: .95em; }
        .example .syl { font-size: 1.05em; }
        .example .trans { font-style: italic; color: #555; }
        .example .eng { color: #222; }
        .example .note { color: #999; font-family: "Segoe UI", Arial, sans-serif; font-size: .75em; }
        .meta { font-family: "Segoe UI", Arial, sans-serif; font-size: .82em; color: #555; margin-top: 16px; }
        .meta dt { display: inline; font-weight: 600; }
        .meta dd { display: inline; margin: 0 14px 0 4px; }
        /* ================= legacy comparison ================= */
        .compare-header { font-family: "Segoe UI", Arial, sans-serif; background: #2c3e50; color: #fff; padding: 8px 12px; border-radius: 6px 6px 0 0; margin-top: 34px; }
        .compare-panel { font-family: "Segoe UI", Arial, sans-serif; font-size: .82em; background: #fff; border: 1px solid #999; border-top: none; border-radius: 0 0 4px 4px; padding: 4px 14px 14px; }
        table.legacy { border-collapse: collapse; width: 100%; }
        table.legacy th { text-align: left; background: #eef1f5; padding: 3px 10px; border-bottom: 1px solid #ddd; white-space: nowrap; color: #555; }
        table.legacy td { padding: 3px 10px; border-bottom: 1px solid #eee; vertical-align: top; }
        table.legacy tr:last-child th, table.legacy tr:last-child td { border-bottom: none; }
        .empty { color: #aaa; font-style: italic; }
        section { margin-bottom: 40px; }
    </style>
</head>
<body>
    <div class="searchbar">
        <g:form action="entry" method="get" style="display:inline">
            <label for="search">Find entry:</label>
            <input type="text" name="search" id="search" value="${search}"/>
            <button type="submit">Search</button>
        </g:form>
        &nbsp;|&nbsp;
        <g:link action="index">Back to Migration Preview (row blocks)</g:link>
    </div>

    <g:if test="${error}">
        <div class="error">${error}</div>
    </g:if>

    <g:if test="${entry}">
        <%-- =========================================================== --%>
        <%-- TOP: the assembled relational entry, one complete card      --%>
        <%-- =========================================================== --%>
        <section class="entry-sheet">
            <div class="headword">
                <%-- Dialect-Specific Header Logic --%>
                <g:if test="${entry.legacy.entrya && entry.legacy.entrya.contains('(')}">
                    <div class="dialect-header" style="margin: 20px 0; border: 1px solid #ddd; padding: 15px; border-radius: 8px; background: #fafafa;">
                        <div style="display: flex; gap: 40px; justify-content: flex-start;">
                            <g:set var="latinParts" value="${entry.legacy.entrya.split(' ')}" />
                            <g:set var="syllParts" value="${entry.legacy.syllabary.split(' ')}" />
                            <g:each in="${latinParts}" var="part" status="i">
                                <g:if test="${part.contains('(')}">
                                    <div style="text-align: left;">
                                        <div style="font-size: 1.8em; font-weight: bold;">
                                            ${syllParts[i-1]}<sup style="font-size: 0.5em; color: #a66; vertical-align: super;">${part.replaceAll(/[\(\)]/, '')}</sup>
                                        </div>
                                        <div style="font-size: 1.2em; font-style: italic; color: #333;">${latinParts[i-1]}</div>
                                    </div>
                                </g:if>
                            </g:each>
                        </div>
                    </div>
                </g:if>
                <g:else>
                    <h1>
                        <span class="syll">${entry.legacy.syllabary}</span><span class="latin">${entry.legacy.entrya}</span>
                        <span class="pos">${entry.partOfSpeech}</span>
                    </h1>
                </g:else>

                <g:if test="${entry.wordForms.find { it.type == 'Entry (headword)' }?.tone}">
                    <span class="tone">tone: ${entry.wordForms.find { it.type == 'Entry (headword)' }?.tone}</span>
                </g:if>
            </div>

            <div class="label">Senses</div>
            <ol class="senses">
                <g:each in="${entry.senses}" var="s" status="n">
                    <li>
                        <strong>${s.definition}</strong> <span class="pos">${s.pos}</span>
                        <g:if test="${s.category}"><span class="cat">· ${s.category}</span></g:if>
                        <g:if test="${s.dialect}"><span class="cat" style="color:#a66; font-weight:bold">· Dialect: ${s.dialect.toUpperCase()}</span></g:if>
                        <g:if test="${s.definitionLarge}"><div style="color:#886; font-size:.8em">large: ${s.definitionLarge}</div></g:if>
                    </li>
                </g:each>
            </ol>

            <g:if test="${entry.wordForms.size() > 1}">
                <div class="label">Forms</div>
                <ul class="forms" style="columns: 2">
                    <g:each in="${entry.wordForms.findAll { it.type != 'Entry (headword)' }}" var="f">
                        <li>
                            <span class="tag">${f.type}:</span>
                            <g:if test="${f.latin}">${f.latin}</g:if>
                            <g:if test="${f.syllabary}"> <span class="syl" style="display:inline">(${f.syllabary})</span></g:if>
                            <g:if test="${f.tone}"> <span class="tone">[${f.tone}]</span></g:if>
                        </li>
                    </g:each>
                </ul>
            </g:if>

            <g:if test="${entry.exampleSentence.syllabary || entry.exampleSentence.english}">
                <div class="label">Example Sentence</div>
                <div class="example">
                    <g:if test="${entry.exampleSentence.syllabary}"><div class="syl">${entry.exampleSentence.syllabary}</div></g:if>
                    <g:if test="${entry.exampleSentence.transliteration}"><div class="trans">${entry.exampleSentence.transliteration}</div></g:if>
                    <g:if test="${entry.exampleSentence.english}"><div class="eng">${entry.exampleSentence.english}</div></g:if>
                    <g:if test="${entry.exampleSentence.legacyNote}"><div class="note">legacy: ${entry.exampleSentence.legacyNote}</div></g:if>
                </div>
            </g:if>

            <g:if test="${entry.dictionaryEntry.etymology || entry.dictionaryEntry.notes || entry.dictionaryEntry.source || entry.dictionaryEntry.crossReference}">
                <div class="label">Entry Metadata</div>
                <div class="meta">
                    <g:if test="${entry.dictionaryEntry.etymology}"><dl><dt>etymology</dt><dd>${entry.dictionaryEntry.etymology}</dd></dl></g:if>
                    <g:if test="${entry.dictionaryEntry.notes}"><dl><dt>notes</dt><dd>${entry.dictionaryEntry.notes}</dd></dl></g:if>
                    <g:if test="${entry.dictionaryEntry.source}"><dl><dt>source</dt><dd>${entry.dictionaryEntry.source}</dd></dl></g:if>
                    <g:if test="${entry.dictionaryEntry.crossReference}"><dl><dt>cross-ref</dt><dd>${entry.dictionaryEntry.crossReference}</dd></dl></g:if>
                </div>
            </g:if>
        </section>

        <%-- =========================================================== --%>
        <%-- BOTTOM: the raw legacy row, every field, for comparison     --%>
        <%-- =========================================================== --%>
        <div class="compare-header">Legacy <code>Likespreadsheets</code> row — all fields, for field-by-field comparison</div>
        <div class="compare-panel">
            <table class="legacy">
                <tr><th style="width:220px">Column</th><th>Value</th></tr>
                <g:each in="${[
                    ['entrya', legacyRow.entrya], ['syllabaryb', legacyRow.syllabaryb],
                    ['partofspeechc', legacyRow.partofspeechc], ['definitiond', legacyRow.definitiond],
                    ['nounadjplurale', legacyRow.nounadjplurale], ['nounadjpluralsyllf', legacyRow.nounadjpluralsyllf],
                    ['nounadjpluraltranslit', legacyRow.nounadjpluraltranslit], ['nounadjpluraltone', legacyRow.nounadjpluraltone],
                    ['vfirstpresg', legacyRow.vfirstpresg], ['vfirstpresh', legacyRow.vfirstpresh],
                    ['vfirstprestranslit', legacyRow.vfirstprestranslit], ['vfirstprestone', legacyRow.vfirstprestone],
                    ['vthirdpasti', legacyRow.vthirdpasti], ['vthirdpastsyllj', legacyRow.vthirdpastsyllj],
                    ['vthirdpasttranslit', legacyRow.vthirdpasttranslit], ['vthirdpasttone', legacyRow.vthirdpasttone],
                    ['vthirdpresk', legacyRow.vthirdpresk], ['vthirdpressylll', legacyRow.vthirdpressylll],
                    ['vthirdprestranslit', legacyRow.vthirdprestranslit], ['vthirdprestone', legacyRow.vthirdprestone],
                    ['vsecondimperm', legacyRow.vsecondimperm], ['vsecondimpersylln', legacyRow.vsecondimpersylln],
                    ['vsecondimpertranslit', legacyRow.vsecondimpertranslit], ['vsecondimpertone', legacyRow.vsecondimpertone],
                    ['vthirdinfo', legacyRow.vthirdinfo], ['vthirdinfsyllp', legacyRow.vthirdinfsyllp],
                    ['vthirdinftranslit', legacyRow.vthirdinftranslit], ['vthirdinftone', legacyRow.vthirdinftone],
                    ['entrytranslit', legacyRow.entrytranslit], ['entrytone', legacyRow.entrytone],
                    ['sentenceq', legacyRow.sentenceq], ['sentencesyllr', legacyRow.sentencesyllr],
                    ['sentencetranslit', legacyRow.sentencetranslit], ['sentenceenglishs', legacyRow.sentenceenglishs],
                    ['crossreferencet', legacyRow.crossreferencet], ['source', legacyRow.source],
                    ['definitionlarge', legacyRow.definitionlarge],
                    ['etymology', legacyRow.etymology], ['notes', legacyRow.notes], ['category', legacyRow.category]
                ]}" var="f">
                    <tr>
                        <th>${f[0]}</th>
                        <td><g:if test="${f[1]}">${f[1]}</g:if><g:else><span class="empty">(empty)</span></g:else></td>
                    </tr>
                </g:each>
            </table>
        </div>
    </g:if>
</body>
</html>
