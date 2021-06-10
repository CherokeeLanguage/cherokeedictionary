<%@ page import="net.cherokeedictionary.transliteration.SyllabaryUtil" %>
<g:if test="${isPrintVersion}">
    \subsection{Dialog - }<br/>
    \begin{tabular}{p{2cm} p{11cm}}<br/>
    <g:each in="${dialogs}">
        ${SyllabaryUtil.mixedTransliteration(it.name).trim()}:<g:if test="${showPhonetic}">\newline \textcolor{red}{${it.name}}:</g:if> &amp; ${SyllabaryUtil.mixedTransliteration(it.dialog)}\newline<g:if test="${showPhonetic ?: false}"> \textcolor{red}{${it.dialog}}</g:if>\\<br/>
    </g:each>
    \end{tabular}<br/>
    \\<br/>
    \\<br/>
    \\<br/>
    \noindent\begin{tabular}{p{2cm} p{11cm}}<br/>
    <g:each in="${dialogs}">
        ${it.engName}: &amp; ${it.engDialog}\\<br/>
    </g:each>
    \end{tabular}<br/><br/>
</g:if>
<g:else>
    <h4>Dialog - </h4>
    <g:each in="${dialogs}">
        <div style="display:table-row">

            <div style="display:table-cell;padding-right:20px">${SyllabaryUtil.mixedTransliteration(it.name)}:<g:if test="${showPhonetic}"><br/><g:redSpan>${it.name}:</g:redSpan></g:if></div>
            <div style="display:table-cell;padding-right:20px">${SyllabaryUtil.mixedTransliteration(it.dialog)}<g:if test="${showPhonetic}"><br/><g:redSpan>${it.dialog}</g:redSpan></g:if></div>
            <div style="display:table-cell;padding-right:20px">${it.engName}:</div>
            <div style="display:table-cell">${it.engDialog}</div>
        </div>
    </g:each>
%{--    <div style="display:table">--}%
%{--        <g:each in="${dialogs}">--}%
%{--            <div style="display:table-row">--}%
%{--                <div style="display:table-cell;padding-right:20px">${SyllabaryUtil.mixedTransliteration(it.name)}:<g:if test="${showPhonetic ?: false}"><br/><g:redSpan>${it.name}:</g:redSpan></g:if></div>--}%
%{--                <div style="display:table-cell">${SyllabaryUtil.mixedTransliteration(it.dialog)}<g:if test="${showPhonetic ?: false}"><br/><g:redSpan>${it.dialog}</g:redSpan></g:if></div>--}%
%{--            </div>--}%
%{--        </g:each>--}%
%{--    </div>--}%
%{--    <br/>--}%
%{--    <div style="display:table">--}%
%{--        <g:each in="${dialogs}">--}%
%{--            <div style="display:table-row">--}%
%{--                <div style="display:table-cell;padding-right:20px">${it.engName}:</div>--}%
%{--                <div style="display:table-cell">${it.engDialog}</div>--}%
%{--            </div>--}%
%{--        </g:each>--}%
%{--    </div>--}%
</g:else>

<g:vocabulary src="${vocabulary}"/>