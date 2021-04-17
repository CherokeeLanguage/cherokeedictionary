<%@ page import="cherokee.Settings" %>



<div class="fieldcontain ${hasErrors(bean: settingsInstance, field: 'settingName', 'error')} required">
	<label for="settingName">
		<g:message code="settings.settingName.label" default="Setting Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="settingName" required="" value="${settingsInstance?.settingName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: settingsInstance, field: 'value', 'error')} required">
	<label for="value">
		<g:message code="settings.value.label" default="Value" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="value" required="" value="${settingsInstance?.value}"/>

</div>

