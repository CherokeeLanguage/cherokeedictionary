<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'Profile')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><g:link uri="/" elementId="homelink">Home</g:link></li>
            </ul>
        </div>

        <div class="showProfile">
            <ul>
                <li>Username: ${todisplay.username}</li>
                <li>Password: ${todisplay.password}</li>
                <li>First Name: ${todisplay.firstName}</li>
                <li>Last Name: ${todisplay.lastName}</li>
                <li>Email: ${todisplay.email}</li>
                <li>Facebook: ${todisplay.facebook}</li>
                <li>Twitter: ${todisplay.twitter}</li>
                <li>YouTube: ${todisplay.youtube}</li>
                <li></li>
                <li></li>
            </ul>
        </div>

        <div class="controls">
            <ul>
                <li><g:link controller="auth" action="update">Update</g:link></li>
                <li><a class="home" href="${createLink(uri: '/')}">Cancel</a></li> <%-- temporary cancel button --%>
            </ul>
        </div>
    </body>
</html>