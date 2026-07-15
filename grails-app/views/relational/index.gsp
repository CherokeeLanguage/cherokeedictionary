<!DOCTYPE html>
<html>
<body>
<h1>${word?.syllabary} <small>(${word.transliteration})</small></h1>

<g:each in="${word.entries}" var="entry">
    <h3>Source: ${entry.source}</h3>
    <g:each in="${entry.senses}" var="sense">
        <p><strong>Definition:</strong> ${sense.definition}</p>
        <p><strong>Part of Speech:</strong> ${sense.partOfSpeech}</p>

        <h4>Example Sentences:</h4>
        <ul>
            <g:each in="${sense.sentences}" var="s">
                <li>${s?.syllabary} - <em>${s?.english}</em></li>
            </g:each>
        </ul>
    </g:each>
</g:each>

<h3>Verb Paradigm:</h3>
<table border="1">
    <g:each in="${groupedTenses}" var="tenseEntry">
        <tr>
            <th>${tenseEntry.key}</th>
            <td>
                <%-- Display comma-separated list of child words --%>
                ${tenseEntry.value.collect { it.childWord?.syllabary }.join(', ')}
            </td>
        </tr>
    </g:each>
</table>

%{--<g:each in="${wordList}" var="word">
    <g:if test="${word}">
        <div>
            <g:link action="show" id="${word.id}">${word.syllabary}</g:link>
        </div>
    </g:if>
</g:each>--}%
</body>
</html>