
<%@ page import="cherokee.dictionary.Likespreadsheets" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'likespreadsheets.label', default: 'Likespreadsheets')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-likespreadsheets" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-likespreadsheets" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="entrya" title="${message(code: 'likespreadsheets.entrya.label', default: 'Entrya')}" />
					
						<g:sortableColumn property="syllabaryb" title="${message(code: 'likespreadsheets.syllabaryb.label', default: 'Syllabaryb')}" />
					
						<g:sortableColumn property="partofspeechc" title="${message(code: 'likespreadsheets.partofspeechc.label', default: 'Partofspeechc')}" />
					
						<g:sortableColumn property="definitiond" title="${message(code: 'likespreadsheets.definitiond.label', default: 'Definitiond')}" />
					
						<g:sortableColumn property="nounadjplurale" title="${message(code: 'likespreadsheets.nounadjplurale.label', default: 'Nounadjplurale')}" />
					
						<g:sortableColumn property="nounadjpluralsyllf" title="${message(code: 'likespreadsheets.nounadjpluralsyllf.label', default: 'Nounadjpluralsyllf')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${likespreadsheetsInstanceList}" status="i" var="likespreadsheetsInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${likespreadsheetsInstance.id}">${fieldValue(bean: likespreadsheetsInstance, field: "entrya")}</g:link></td>
					
						<td>${fieldValue(bean: likespreadsheetsInstance, field: "syllabaryb")}</td>
					
						<td>${fieldValue(bean: likespreadsheetsInstance, field: "partofspeechc")}</td>
					
						<td>${fieldValue(bean: likespreadsheetsInstance, field: "definitiond")}</td>
					
						<td>${fieldValue(bean: likespreadsheetsInstance, field: "nounadjplurale")}</td>
					
						<td>${fieldValue(bean: likespreadsheetsInstance, field: "nounadjpluralsyllf")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${likespreadsheetsInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
