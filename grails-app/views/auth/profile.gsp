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
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
            </ul>
        </div>

        <div class="controls">
            <ul>
                <li><g:link controller="AuthController" action="update"><g:message code="default.new.label" args="[entityName]" />Update</g:link></li>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/>Cancel</a></li> <%-- temporary cancel button --%>
            </ul>
        </div>
    </body>
</html>