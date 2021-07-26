<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'genericAudio.label', default: 'GenericAudio')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>

    </head>
    <body>
    <script>
        $(function() {

            $('#phonetic').on('blur', function () {
                $.ajax({
                    method: "POST",
                    url: "/transliterate/index",
                    data: {text:$('#phonetic').val(), from:'true'},
                    success: function(data) {
                        $( "#syllabary" ).val( data );
                    }
                })
            });
        });
    </script>
        <a href="#create-genericAudio" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-genericAudio" class="content scaffold-create" role="main">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.genericAudio}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.genericAudio}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.genericAudio}" method="POST">
                <fieldset class="form">
                    <f:field property="english" bean="genericAudio"/><br/>
                    <f:field property="phonetic" bean="genericAudio"/><br/>
                    <f:field property="syllabary" bean="genericAudio"/><br/>
                    <f:field property="sourceInformation" bean="genericAudio" value="C#L##"/><br/>
                    <f:field property="source" bean="genericAudio" value="walc1"/><br/>
                    <f:field property="audioLink" bean="genericAudio"/><br/>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
