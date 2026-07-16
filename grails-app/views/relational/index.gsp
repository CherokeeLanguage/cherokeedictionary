<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Relational Dictionary Information</title>
</head>
<body>
<h1>${word?.syllabary} <small>(${word?.transliteration})</small></h1>

<g:each in="${word?.entries}" var="entry">
    <h3>Source: ${entry?.source}</h3>
    <g:each in="${entry?.senses}" var="sense">
        <p><strong>Definition:</strong> ${sense?.definition}</p>
        <p><strong>Part of Speech:</strong> ${sense?.partOfSpeech}</p>

        <h4>Example Sentences:</h4>
        <ul>
            <g:each in="${sense?.sentences}" var="s">
                <li>
                    ${s?.syllabary}
                    <g:if test="${s?.transliteration}">
                        <small>(${s?.transliteration})</small>
                    </g:if>
                    - <em>${s?.english}</em>
                </li>
            </g:each>
        </ul>
    </g:each>
</g:each>

<h3>Verb Paradigm:</h3>
<table border="1" cellpadding="5" cellspacing="0">
    <thead>
        <tr>
            <th>Tense (Description)</th>
            <th>Inflected Forms (Transliteration)</th>
        </tr>
    </thead>
    <tbody>
        <g:each in="${groupedTenses}" var="tenseEntry">
            <tr>
                <td>
                    <strong>${tenseEntry.key}</strong>
                    <g:if test="${tenseEntry.value[0]?.tense?.description}">
                        <br/><small>(${tenseEntry.value[0]?.tense?.description})</small>
                    </g:if>
                </td>
                <td>
                    ${tenseEntry.value.collect { "${it.childWord?.syllabary} (${it.childWord?.transliteration})" }.join(', ')}
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

</body>
</html>
