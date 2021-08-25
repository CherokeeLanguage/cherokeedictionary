<%@ page import="net.cherokeedictionary.admin.Admin" %>


<div class="fieldcontain ${hasErrors(bean: adminInstance, field: 'username', 'error')} required">
    <label for="username">
        <g:message code="admin.username.label" default="Username" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="username" required="" value="${adminInstance?.username}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: adminInstance, field: 'password', 'error')} required">
	<label for="password">
		<g:message code="admin.password.label" default="Password" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="password" required="" value="${adminInstance?.password}"/>

</div>


