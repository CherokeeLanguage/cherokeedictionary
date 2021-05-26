
<%@ page import="cherokee.dictionary.Likespreadsheets" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'likespreadsheets.label', default: 'Likespreadsheets')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-likespreadsheets" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-likespreadsheets" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list likespreadsheets">
			
				<g:if test="${likespreadsheetsInstance?.entrya}">
				<li class="fieldcontain">
					<span id="entrya-label" class="property-label"><g:message code="likespreadsheets.entrya.label" default="Entrya" /></span>
					
						<span class="property-value" aria-labelledby="entrya-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="entrya"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.syllabaryb}">
				<li class="fieldcontain">
					<span id="syllabaryb-label" class="property-label"><g:message code="likespreadsheets.syllabaryb.label" default="Syllabaryb" /></span>
					
						<span class="property-value" aria-labelledby="syllabaryb-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="syllabaryb"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.partofspeechc}">
				<li class="fieldcontain">
					<span id="partofspeechc-label" class="property-label"><g:message code="likespreadsheets.partofspeechc.label" default="Partofspeechc" /></span>
					
						<span class="property-value" aria-labelledby="partofspeechc-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="partofspeechc"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.definitiond}">
				<li class="fieldcontain">
					<span id="definitiond-label" class="property-label"><g:message code="likespreadsheets.definitiond.label" default="Definitiond" /></span>
					
						<span class="property-value" aria-labelledby="definitiond-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="definitiond"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.nounadjplurale}">
				<li class="fieldcontain">
					<span id="nounadjplurale-label" class="property-label"><g:message code="likespreadsheets.nounadjplurale.label" default="Nounadjplurale" /></span>
					
						<span class="property-value" aria-labelledby="nounadjplurale-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="nounadjplurale"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.nounadjpluralsyllf}">
				<li class="fieldcontain">
					<span id="nounadjpluralsyllf-label" class="property-label"><g:message code="likespreadsheets.nounadjpluralsyllf.label" default="Nounadjpluralsyllf" /></span>
					
						<span class="property-value" aria-labelledby="nounadjpluralsyllf-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="nounadjpluralsyllf"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vfirstpresg}">
				<li class="fieldcontain">
					<span id="vfirstpresg-label" class="property-label"><g:message code="likespreadsheets.vfirstpresg.label" default="Vfirstpresg" /></span>
					
						<span class="property-value" aria-labelledby="vfirstpresg-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vfirstpresg"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vfirstpresh}">
				<li class="fieldcontain">
					<span id="vfirstpresh-label" class="property-label"><g:message code="likespreadsheets.vfirstpresh.label" default="Vfirstpresh" /></span>
					
						<span class="property-value" aria-labelledby="vfirstpresh-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vfirstpresh"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdpasti}">
				<li class="fieldcontain">
					<span id="vthirdpasti-label" class="property-label"><g:message code="likespreadsheets.vthirdpasti.label" default="Vthirdpasti" /></span>
					
						<span class="property-value" aria-labelledby="vthirdpasti-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdpasti"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdpastsyllj}">
				<li class="fieldcontain">
					<span id="vthirdpastsyllj-label" class="property-label"><g:message code="likespreadsheets.vthirdpastsyllj.label" default="Vthirdpastsyllj" /></span>
					
						<span class="property-value" aria-labelledby="vthirdpastsyllj-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdpastsyllj"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdpresk}">
				<li class="fieldcontain">
					<span id="vthirdpresk-label" class="property-label"><g:message code="likespreadsheets.vthirdpresk.label" default="Vthirdpresk" /></span>
					
						<span class="property-value" aria-labelledby="vthirdpresk-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdpresk"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdpressylll}">
				<li class="fieldcontain">
					<span id="vthirdpressylll-label" class="property-label"><g:message code="likespreadsheets.vthirdpressylll.label" default="Vthirdpressylll" /></span>
					
						<span class="property-value" aria-labelledby="vthirdpressylll-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdpressylll"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vsecondimperm}">
				<li class="fieldcontain">
					<span id="vsecondimperm-label" class="property-label"><g:message code="likespreadsheets.vsecondimperm.label" default="Vsecondimperm" /></span>
					
						<span class="property-value" aria-labelledby="vsecondimperm-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vsecondimperm"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vsecondimpersylln}">
				<li class="fieldcontain">
					<span id="vsecondimpersylln-label" class="property-label"><g:message code="likespreadsheets.vsecondimpersylln.label" default="Vsecondimpersylln" /></span>
					
						<span class="property-value" aria-labelledby="vsecondimpersylln-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vsecondimpersylln"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdinfo}">
				<li class="fieldcontain">
					<span id="vthirdinfo-label" class="property-label"><g:message code="likespreadsheets.vthirdinfo.label" default="Vthirdinfo" /></span>
					
						<span class="property-value" aria-labelledby="vthirdinfo-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdinfo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdinfsyllp}">
				<li class="fieldcontain">
					<span id="vthirdinfsyllp-label" class="property-label"><g:message code="likespreadsheets.vthirdinfsyllp.label" default="Vthirdinfsyllp" /></span>
					
						<span class="property-value" aria-labelledby="vthirdinfsyllp-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdinfsyllp"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.sentenceq}">
				<li class="fieldcontain">
					<span id="sentenceq-label" class="property-label"><g:message code="likespreadsheets.sentenceq.label" default="Sentenceq" /></span>
					
						<span class="property-value" aria-labelledby="sentenceq-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="sentenceq"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.sentencesyllr}">
				<li class="fieldcontain">
					<span id="sentencesyllr-label" class="property-label"><g:message code="likespreadsheets.sentencesyllr.label" default="Sentencesyllr" /></span>
					
						<span class="property-value" aria-labelledby="sentencesyllr-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="sentencesyllr"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.sentenceenglishs}">
				<li class="fieldcontain">
					<span id="sentenceenglishs-label" class="property-label"><g:message code="likespreadsheets.sentenceenglishs.label" default="Sentenceenglishs" /></span>
					
						<span class="property-value" aria-labelledby="sentenceenglishs-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="sentenceenglishs"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.crossreferencet}">
				<li class="fieldcontain">
					<span id="crossreferencet-label" class="property-label"><g:message code="likespreadsheets.crossreferencet.label" default="Crossreferencet" /></span>
					
						<span class="property-value" aria-labelledby="crossreferencet-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="crossreferencet"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.nounadjpluraltranslit}">
				<li class="fieldcontain">
					<span id="nounadjpluraltranslit-label" class="property-label"><g:message code="likespreadsheets.nounadjpluraltranslit.label" default="Nounadjpluraltranslit" /></span>
					
						<span class="property-value" aria-labelledby="nounadjpluraltranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="nounadjpluraltranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.entrytranslit}">
				<li class="fieldcontain">
					<span id="entrytranslit-label" class="property-label"><g:message code="likespreadsheets.entrytranslit.label" default="Entrytranslit" /></span>
					
						<span class="property-value" aria-labelledby="entrytranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="entrytranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vfirstprestranslit}">
				<li class="fieldcontain">
					<span id="vfirstprestranslit-label" class="property-label"><g:message code="likespreadsheets.vfirstprestranslit.label" default="Vfirstprestranslit" /></span>
					
						<span class="property-value" aria-labelledby="vfirstprestranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vfirstprestranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdpasttranslit}">
				<li class="fieldcontain">
					<span id="vthirdpasttranslit-label" class="property-label"><g:message code="likespreadsheets.vthirdpasttranslit.label" default="Vthirdpasttranslit" /></span>
					
						<span class="property-value" aria-labelledby="vthirdpasttranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdpasttranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdprestranslit}">
				<li class="fieldcontain">
					<span id="vthirdprestranslit-label" class="property-label"><g:message code="likespreadsheets.vthirdprestranslit.label" default="Vthirdprestranslit" /></span>
					
						<span class="property-value" aria-labelledby="vthirdprestranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdprestranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vsecondimpertranslit}">
				<li class="fieldcontain">
					<span id="vsecondimpertranslit-label" class="property-label"><g:message code="likespreadsheets.vsecondimpertranslit.label" default="Vsecondimpertranslit" /></span>
					
						<span class="property-value" aria-labelledby="vsecondimpertranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vsecondimpertranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdinftranslit}">
				<li class="fieldcontain">
					<span id="vthirdinftranslit-label" class="property-label"><g:message code="likespreadsheets.vthirdinftranslit.label" default="Vthirdinftranslit" /></span>
					
						<span class="property-value" aria-labelledby="vthirdinftranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdinftranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.sentencetranslit}">
				<li class="fieldcontain">
					<span id="sentencetranslit-label" class="property-label"><g:message code="likespreadsheets.sentencetranslit.label" default="Sentencetranslit" /></span>
					
						<span class="property-value" aria-labelledby="sentencetranslit-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="sentencetranslit"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.entrytone}">
				<li class="fieldcontain">
					<span id="entrytone-label" class="property-label"><g:message code="likespreadsheets.entrytone.label" default="Entrytone" /></span>
					
						<span class="property-value" aria-labelledby="entrytone-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="entrytone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.nounadjpluraltone}">
				<li class="fieldcontain">
					<span id="nounadjpluraltone-label" class="property-label"><g:message code="likespreadsheets.nounadjpluraltone.label" default="Nounadjpluraltone" /></span>
					
						<span class="property-value" aria-labelledby="nounadjpluraltone-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="nounadjpluraltone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vfirstprestone}">
				<li class="fieldcontain">
					<span id="vfirstprestone-label" class="property-label"><g:message code="likespreadsheets.vfirstprestone.label" default="Vfirstprestone" /></span>
					
						<span class="property-value" aria-labelledby="vfirstprestone-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vfirstprestone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdpasttone}">
				<li class="fieldcontain">
					<span id="vthirdpasttone-label" class="property-label"><g:message code="likespreadsheets.vthirdpasttone.label" default="Vthirdpasttone" /></span>
					
						<span class="property-value" aria-labelledby="vthirdpasttone-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdpasttone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdprestone}">
				<li class="fieldcontain">
					<span id="vthirdprestone-label" class="property-label"><g:message code="likespreadsheets.vthirdprestone.label" default="Vthirdprestone" /></span>
					
						<span class="property-value" aria-labelledby="vthirdprestone-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdprestone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vsecondimpertone}">
				<li class="fieldcontain">
					<span id="vsecondimpertone-label" class="property-label"><g:message code="likespreadsheets.vsecondimpertone.label" default="Vsecondimpertone" /></span>
					
						<span class="property-value" aria-labelledby="vsecondimpertone-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vsecondimpertone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.vthirdinftone}">
				<li class="fieldcontain">
					<span id="vthirdinftone-label" class="property-label"><g:message code="likespreadsheets.vthirdinftone.label" default="Vthirdinftone" /></span>
					
						<span class="property-value" aria-labelledby="vthirdinftone-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="vthirdinftone"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.source}">
				<li class="fieldcontain">
					<span id="source-label" class="property-label"><g:message code="likespreadsheets.source.label" default="Source" /></span>
					
						<span class="property-value" aria-labelledby="source-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="source"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.definitionlarge}">
				<li class="fieldcontain">
					<span id="definitionlarge-label" class="property-label"><g:message code="likespreadsheets.definitionlarge.label" default="Definitionlarge" /></span>
					
						<span class="property-value" aria-labelledby="definitionlarge-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="definitionlarge"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${likespreadsheetsInstance?.etymology}">
				<li class="fieldcontain">
					<span id="etymology-label" class="property-label"><g:message code="likespreadsheets.etymology.label" default="Etymology" /></span>
					
						<span class="property-value" aria-labelledby="etymology-label"><g:fieldValue bean="${likespreadsheetsInstance}" field="etymology"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:likespreadsheetsInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${this.likespreadsheetsInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
