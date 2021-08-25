<%@ page import="net.cherokeedictionary.dictionary.Likespreadsheets;  net.cherokeedictionary.dictionary.Likespreadsheets; java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
<%@ page import="net.cherokeedictionary.util.Tense" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
%{--        why was this set to false? was that intentional? --}%
%{--        <g:set var="showLinks" value="false" scope="request"/>--}%
        <meta name="layout" content="manager"/>
        <title>Conjugation Display</title>
        <script type="text/javascript">
            $(function() {
                $('#showtranslit').click(function() {
                    $('span').toggle();
                });

                $('#verbTense').change(function() {
                    var selectedValue = $('#verbTense').find(":selected").val();
                    hideAllNotes();

                    if (selectedValue == "${Tense.PRESENT}") {
                        $('#presentNote').show();
                    } else if (selectedValue == "${Tense.REMOTE_PAST}") {
                        $('#remotePastNote').show();
                    } else if (selectedValue == "${Tense.HABITUAL}") {
                        $('#habitualNote').show();
                    } else if (selectedValue == "${Tense.FUTURE_COMMAND}") {
                        $('#futureCommandNote').show();
                    } else if (selectedValue == "${Tense.INFINITIVE}") {
                        $('#infinitiveNote').show();
                    }
                });

                function hideAllNotes() {
                    $('#presentNote').hide();
                    $('#remotePastNote').hide();
                    $('#futureCommandNote').hide();
                    $('#infinitiveNote').hide();
                    $('#habitualNote').hide();
                }

                hideAllNotes();
                $('#presentNote').show();
            });
        </script>
    </head>
    <body>
        <script>
            $(function() {
                var verbType;//same as verbset
                function processVerbSet(verbset) {
                    verbType = verbset;

                    // intransitive verbs are a different breed - both have set a and set b
        //            the set a vs set b should be handled by determining the start of the verb stem - if du,nu,u,wu in 3rd sing present then set b rest are a
                    if (verbType == 'vi') {
                        $('#subject').empty();
                        $('#object').remove();
                        resetSubject();

                        processIntransitive("SG1");
                    } else {
                        processTransitive("SG1");
                    }
                }

                $('#subject').change(function() {
                    if (verbType == 'vi') {
                        processIntransitive();
                    } else {
                        processTransitive(this.value);
                    }
                });

                function processIntransitive(value) {

                }

                function processTransitive(value) {
                    $('#object').empty();
                    resetObject();

                    if (value == "SG1") {
                        $('#object option[value="SG1"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="DL1EXCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="PL1EXCL"]').remove();
                    } else if (value == "SG2") {
                        $('#object option[value="SG2"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="DL2"]').remove();
                        $('#object option[value="PL2"]').remove();
                    } else if (value == "SG3") {
                    } else if (value == "DL1INCL") {
                        $('#object option[value="SG1"]').remove();
                        $('#object option[value="SG2"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="DL1EXCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="PL1EXCL"]').remove();
                        $('#object option[value="DL2"]').remove();
                        $('#object option[value="PL2"]').remove();
                    } else if (value == "DL1EXCL") {
                        $('#object option[value="SG1"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="DL1EXCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="PL1EXCL"]').remove();
                    } else if (value == "PL1INCL") {
                        $('#object option[value="SG1"]').remove();
                        $('#object option[value="SG2"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="DL1EXCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="PL1EXCL"]').remove();
                        $('#object option[value="DL2"]').remove();
                        $('#object option[value="PL2"]').remove();
                    } else if (value == "PL1EXCL") {
                        $('#object option[value="SG1"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="DL1EXCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="PL1EXCL"]').remove();
                        $('#object option[value="DL2"]').remove();
                        $('#object option[value="PL2"]').remove();
                    } else if (value == "DL2") {
                        $('#object option[value="SG2"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="DL2"]').remove();
                        $('#object option[value="PL2"]').remove();
                    } else if (value == "PL2") {
                        $('#object option[value="SG2"]').remove();
                        $('#object option[value="DL1INCL"]').remove();
                        $('#object option[value="PL1INCL"]').remove();
                        $('#object option[value="PL1EXCL"]').remove();
                        $('#object option[value="DL2"]').remove();
                        $('#object option[value="PL2"]').remove();
                    } else if (value == "PL3") {
                    } else {
                    }
                }

                function resetSubject() {
                    $('#subject').append($("<option></option>").attr("value", "SG1").text("SG1"));
                    $('#subject').append($("<option></option>").attr("value", "SG2").text("SG2"));
                    $('#subject').append($("<option></option>").attr("value", "SG3").text("SG3"));
                    $('#subject').append($("<option></option>").attr("value", "DL1INCL").text("DL1INCL"));
                    $('#subject').append($("<option></option>").attr("value", "DL1EXCL").text("DL1EXCL"));
                    $('#subject').append($("<option></option>").attr("value", "PL1INCL").text("PL1INCL"));
                    $('#subject').append($("<option></option>").attr("value", "PL1EXCL").text("PL1EXCL"));

                    $('#subject').append($("<option></option>").attr("value", "DL2INCL").text("DL2INCL"));
                    $('#subject').append($("<option></option>").attr("value", "DL2EXCL").text("DL2EXCL"));
                    $('#subject').append($("<option></option>").attr("value", "PL2INCL").text("PL2INCL"));
                    $('#subject').append($("<option></option>").attr("value", "PL2EXCL").text("PL2EXCL"));

        //            $('#subject').append($("<option></option>").attr("value", "DL3INCL").text("DL3INCL"));
        //            $('#subject').append($("<option></option>").attr("value", "DL3EXCL").text("DL3EXCL"));
                    $('#subject').append($("<option></option>").attr("value", "PL3INCL").text("PL3INCL"));
                    $('#subject').append($("<option></option>").attr("value", "PL3EXCL").text("PL3EXCL"));
                }

                function resetObject() {
                    var objectSelect = $('#object');
                    objectSelect.append($("<option></option>").attr("value", "SG1").text("SG1"));
                    objectSelect.append($("<option></option>").attr("value", "SG2").text("SG2"));
                    objectSelect.append($("<option></option>").attr("value", "SG3AN").text("SG3AN"));
                    objectSelect.append($("<option></option>").attr("value", "SG3IN").text("SG3IN"));
                    objectSelect.append($("<option></option>").attr("value", "DL1INCL").text("DL1INCL"));
                    objectSelect.append($("<option></option>").attr("value", "DL1EXCL").text("DL1EXCL"));
                    objectSelect.append($("<option></option>").attr("value", "PL1INCL").text("PL1INCL"));
                    objectSelect.append($("<option></option>").attr("value", "PL1EXCL").text("PL1EXCL"));
                    objectSelect.append($("<option></option>").attr("value", "DL2").text("DL2"));
                    objectSelect.append($("<option></option>").attr("value", "PL2").text("PL2"));
                    objectSelect.append($("<option></option>").attr("value", "PL3AN").text("PL3AN"));
                    objectSelect.append($("<option></option>").attr("value", "PL3IN").text("PL3IN"));
                }

                processVerbSet('${params.set}');
            });
        </script>
        <%--
        todo: on the page, I'll have to remember to do an initial check of the verb and then only set those values that apply to set b if the verb is set b
        todo: javascript to limit options of object based on subject and vice versa
        --%>
        <!-- eventually some javascript to only allow you to pick the correct matching items -->
        <%
            def entry = Likespreadsheets.findById(params.id)
            String verbTense = params.verbTense ?: "PRESENT"
            Tense vtense = Tense.valueOf(verbTense)
        %>
        <%@ page import="java.util.regex.Matcher; java.util.regex.Pattern" contentType="text/html;charset=UTF-8" %>
        <g:render template="simplifiedOriginalEntry" model="[entry:entry]"/>
        <g:form controller="conjugation">
            <input type="hidden" name="id" value="${entry.id}"/>
            <g:render template="initialPrefixes" mode="[vtense: vtense]"/>
            <input type="hidden" name="id" value="${params.id}"/>
            <select name="subject" id="subject">
                <option value="SG1" <g:if test="${subject == "SG1"}">selected</g:if>>1SG</option>
                <option value="SG2" <g:if test="${subject == "SG2"}">selected</g:if>>2SG</option>
                <option value="SG3" <g:if test="${subject == "SG3"}">selected</g:if>>3SG</option>
                <option value="DL1INCL" <g:if test="${subject == "DL1INCL"}">selected</g:if>>1DLINCL</option>
                <option value="DL1EXCL" <g:if test="${subject == "DL1EXCL"}">selected</g:if>>1DLEXCL</option>
                <option value="PL1INCL" <g:if test="${subject == "PL1INCL"}">selected</g:if>>1PLINCL</option>
                <option value="PL1EXCL" <g:if test="${subject == "PL1EXCL"}">selected</g:if>>1PLEXCL</option>
                <option value="DL2" <g:if test="${subject == "DL2"}">selected</g:if>>2DL</option>
                <option value="PL2" <g:if test="${subject == "PL2"}">selected</g:if>>2PL</option>
                <option value="PL3" <g:if test="${subject == "PL3"}">selected</g:if>>3PL</option>
            </select>
            <select name="object" id="object">
                <option value="SG1" <g:if test="${object == "SG1"}">selected</g:if>>1SG</option>
                <option value="SG2" <g:if test="${subject == "SG2"}">selected</g:if>>2SG</option>
                <option value="SG3AN" <g:if test="${subject == "SG3AN"}">selected</g:if>>3SGAN</option>
                <option value="SG3IN" <g:if test="${subject == "SG3IN"}">selected</g:if>>3SGIN</option>
                <option value="DL1INCL" <g:if test="${subject == "DL1INCL"}">selected</g:if>>1DLINCL</option>
                <option value="DL1EXCL" <g:if test="${subject == "DL1EXCL"}">selected</g:if>>1DLEXCL</option>
                <option value="PL1INCL" <g:if test="${subject == "PL1INCL"}">selected</g:if>>1PLINCL</option>
                <option value="PL1EXCL" <g:if test="${subject == "PL1EXCL"}">selected</g:if>>1PLEXCL</option>
                <option value="DL2" <g:if test="${subject == "DL2"}">selected</g:if>>2DL</option>
                <option value="PL2" <g:if test="${subject == "PL2"}">selected</g:if>>2PL</option>
                <option value="PL3AN" <g:if test="${subject == "PL3AN"}">selected</g:if>>3PLAN</option>
                <option value="PL3IN" <g:if test="${subject == "PL3IN"}">selected</g:if>>3PLIN</option>
            </select>
            <g:render template="verbTenseSelector" model="[vtense:vtense]"/>
            <input onclick="jQuery.ajax({type:'POST',data:jQuery(this).parents('form:first').serialize(), url:'/conjugation/index',success:function(data,textStatus){jQuery('#searchresults').html(data);},error:function(XMLHttpRequest,textStatus,errorThrown){}});return false" type="button" value="Search">
            %{-- submitToRemote was an ajax tag - the code above is what it was replaced with in the page anyway so because it was removed I just used the code above--}%
            %{--<g:submitToRemote update="searchresults" url="[controller:'conjugation', action:'index']" value="Search"/>--}%
        </g:form>
        <br/>

        <div id="searchresults"></div><br/>
        * disclaimer - conjugations may be incorrect - they are for potential reference only<br/><br/>
        <a href="#" id="showtranslit">Show/Hide Transliteration</a><br/><br/>
        <div id="presentNote"><g:render template="/newSearch/presentNote"/></div>
        <div id="remotePastNote"><g:render template="/newSearch/remotePastNote"/></div>
        <div id="habitualNote"><g:render template="/newSearch/habitualNote"/></div>
        <div id="futureCommandNote"><g:render template="/newSearch/futureCommandNote"/></div>
        <div id="infinitiveNote"><g:render template="/newSearch/infinitiveNote"/></div>
    </body>
</html>