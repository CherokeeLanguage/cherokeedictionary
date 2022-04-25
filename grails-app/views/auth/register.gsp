<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav" role="navigation">
            <ul>
                <li><g:link uri="/" elementId="homelink">Home</g:link> </li>
            </ul>
        </div>

        <div id="create-user" class="content scaffold-create" role="main">
            <g:if test="${nosave}">
                <h1>Error: ${nosave}, Username = ${username}</h1>
            </g:if>

            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.userInstance}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${this.userInstance}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>

            <g:form controller="auth" action="create" method="POST">
                <fieldset class="form">
                    <g:textField name="username" placeholder="Username"/><br/>
                    <g:passwordField name="password" placeholder="Password"/><br/>
                </fieldset>
                <fieldset class="buttons">
                    <g:actionSubmit name="register" class="save" value="Register" action="create" />
                </fieldset>
            </g:form>
        </div>

    </body>
</html>