<%@ page import="net.cherokeedictionary.util.Tense" %>
<g:if test="${vtense == Tense.PRESENT}">
    <div id="presentNote"><g:render template="/newSearch/presentNote"/></div>
</g:if>

<g:if test="${vtense == Tense.REMOTE_PAST}">
    <div id="remotePastNote"><g:render template="/newSearch/remotePastNote"/></div>
</g:if>

<g:if test="${vtense == Tense.HABITUAL}">
    <div id="habitualNote"><g:render template="/newSearch/habitualNote"/></div>
</g:if>

<g:if test="${vtense == Tense.FUTURE_COMMAND}">
    <div id="futureCommandNote"><g:render template="/newSearch/futureCommandNote"/></div>
</g:if>

<g:if test="${vtense == Tense.INFINITIVE}">
    <div id="infinitiveNote"><g:render template="/newSearch/infinitiveNote"/></div>
</g:if>