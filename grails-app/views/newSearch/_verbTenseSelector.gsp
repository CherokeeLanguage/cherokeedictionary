<%@ page import="net.cherokeedictionary.util.Tense" %>

<select name="verbTense" id="verbTense">
    <option value="${net.cherokeedictionary.util.Tense.PRESENT}" <g:if test="${vtense == Tense.PRESENT}">selected</g:if>>Present</option>
    %{--        <option value="${Tense.RECENT_PAST_IMPERATIVE}">Present/Recent Past Imperative</option>--}%
    <option value="${Tense.REMOTE_PAST}" <g:if test="${vtense == Tense.REMOTE_PAST}">selected</g:if>>Remote Past</option>
    <option value="${Tense.HABITUAL}" <g:if test="${vtense == Tense.HABITUAL}">selected</g:if>>Habitual</option>
    <option value="${Tense.FUTURE_COMMAND}" <g:if test="${vtense == Tense.FUTURE_COMMAND}">selected</g:if>>Immediate</option>
    %{--        <option value="${Tense.FUTURE_COMMAND}">Future Imperative</option>--}%
    <option value="${Tense.INFINITIVE}" <g:if test="${vtense == Tense.INFINITIVE}">selected</g:if>>Infinitive</option>
    %{--        <option value="reportative">Reportative</option>--}%
    %{--        <option value="${Tense.PROGRESSIVE_FUTURE}">Future Progressive</option>--}%
    %{--        <option value="pluperfect">Pluperfect</option>--}%
</select>