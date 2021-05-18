<%@ page import="cherokee.dictionary.SourceManagement" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cherokee English Dictionary</title>
    <meta name="layout" content="manager"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="description" content="Cherokee English Dictionary">

    <style>
        input {
            width: 500px
        }
    </style>
</head>
<body>
<g:set var="entityName" value="${message(code: 'likespreadsheets.label', default: 'Likespreadsheets')}" />

<g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
</g:if>
<g:hasErrors bean="${likespreadsheetsInstance}">
    <ul class="errors" role="alert">
        <g:eachError bean="${likespreadsheetsInstance}" var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
        </g:eachError>
    </ul>
</g:hasErrors>

    <div id="create-likespreadsheets" class="content scaffold-create" role="main">
        <h1>Simplified Entry</h1>
        <form action="/likespreadsheets/simplifiedEntry" method="post" >
            <fieldset class="form">
                <div class="fieldcontain  ">
                    <label for="syllabaryb">
                        Entry Syllabary
                    </label>
                    <input type="text" name="syllabaryb" value="" id="syllabaryb" />
                </div>

                <div class="fieldcontain  ">
                    <label for="entrytone">
                        Entry Tone
                    </label>
                    <input type="text" name="entrytone" value="" id="entrytone" />
                </div>

                <div class="fieldcontain  ">
                    <label for="entrya">
                        Entry Tsalagi
                    </label>
                    <input type="text" name="entrya" value="" id="entrya" />
                </div>

                <div class="fieldcontain  ">
                    <label for="definitiond">
                        Entry Definition
                    </label>
                    <input type="text" name="definitiond" value="" id="definitiond" />
                </div>

                <div class="fieldcontain  ">
                    <label for="partofspeechc">
                        Part of Speech
                    </label>

                    <g:select name="partofspeechc"
                              from="${cherokee.relational.PartOfSpeech.list()}"
                              value="partofspeechc.long_name"
                              optionKey="partofspeech"
                              noSelection="['':'-Part of Speech-']"/>
                </div>

                <div id="nounAdj" style="display:none">
                    <div class="fieldcontain  ">
                        <label for="nounadjpluralsyllf">
                            Noun/Adj Plural Syllabary
                        </label>
                        <input type="text" name="nounadjpluralsyllf" value="" id="nounadjpluralsyllf" />
                    </div>

                    <div class="fieldcontain  ">
                        <label for="nounadjpluraltone">
                            Noun/Adj Plural Tone
                        </label>
                        <input type="text" name="nounadjpluraltone" value="" id="nounadjpluraltone" />
                    </div>

                    <div class="fieldcontain  ">
                        <label for="nounadjplurale">
                            Noun/Adj Plural Tsalagi
                        </label>
                        <input type="text" name="nounadjplurale" value="" id="nounadjplurale" />
                    </div>
                </div>

                <div id="verbals" style="display:none">
                    <div id="fpresshowhide">First Present <i class="fas fa-sort-down fa-2x"></i></div>
                    <div id="fpres">
                        <div class="fieldcontain  ">
                            <label for="vfirstpresh">
                                Verb First Present Syllabary
                            </label>
                            <input type="text" name="vfirstpresh" value="" id="vfirstpresh" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vfirstprestone">
                                Verb First Present Tone
                            </label>
                            <input type="text" name="vfirstprestone" value="" id="vfirstprestone" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vfirstpresg">
                                Verb First Present Tsalagi
                            </label>
                            <input type="text" name="vfirstpresg" value="" id="vfirstpresg" />
                        </div>
                    </div>

                    <div id="tpastshowhide">Third Past<i class="fas fa-sort-down fa-2x"></i></div>
                    <div id="tpast" >
                        <div class="fieldcontain  ">
                            <label for="vthirdpastsyllj">
                                Verb Third Remote Past Syllabary
                            </label>
                            <input type="text" name="vthirdpastsyllj" value="" id="vthirdpastsyllj" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vthirdpasttone">
                                Verb Third Remote Past Tone
                            </label>
                            <input type="text" name="vthirdpasttone" value="" id="vthirdpasttone" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vthirdpasti">
                                Verb Third Remote Past Tsalagi
                            </label>
                            <input type="text" name="vthirdpasti" value="" id="vthirdpasti" />
                        </div>
                    </div>

                    <div id="tpresshowhide">Third Present <i class="fas fa-sort-down fa-2x"></i></div>
                    <div id="tpres">
                        <div class="fieldcontain  ">
                            <label for="vthirdpressylll">
                                Verb Third Present Syllabary
                            </label>
                            <input type="text" name="vthirdpressylll" value="" id="vthirdpressylll" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vthirdprestone">
                                Verb Third Present Tone
                            </label>
                            <input type="text" name="vthirdprestone" value="" id="vthirdprestone" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vthirdpresk">
                                Verb Third Present Tsalagi
                            </label>
                            <input type="text" name="vthirdpresk" value="" id="vthirdpresk" />
                        </div>
                    </div>

                    <div id="simpshowhide">Second Imperative <i class="fas fa-sort-down fa-2x"></i></div>
                    <div id="simp">
                        <div class="fieldcontain  ">
                            <label for="vsecondimpersylln">
                                Verb Second Imperative/Future Command Syllabary
                            </label>
                            <input type="text" name="vsecondimpersylln" value="" id="vsecondimpersylln" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vsecondimpertone">
                                Verb Second Imperative/Future Command Tone
                            </label>
                            <input type="text" name="vsecondimpertone" value="" id="vsecondimpertone" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vsecondimperm">
                                Verb Second Imperative/Future Command Tsalagi
                            </label>
                            <input type="text" name="vsecondimperm" value="" id="vsecondimperm" />
                        </div>
                    </div>

                    <div id="tinfshowhide">Third Infinitive<i class="fas fa-sort-down fa-2x"></i></div>
                    <div id="tinf">
                        <div class="fieldcontain  ">
                            <label for="vthirdinfsyllp">
                                Verb Third Infinitve Syllabary
                            </label>
                            <input type="text" name="vthirdinfsyllp" value="" id="vthirdinfsyllp" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vthirdinftone">
                                Verb Third Infinitive Tone
                            </label>
                            <input type="text" name="vthirdinftone" value="" id="vthirdinftone" />
                        </div>

                        <div class="fieldcontain  ">
                            <label for="vthirdinfo">
                                Verb Third Infinitive Tsalagi
                            </label>
                            <input type="text" name="vthirdinfo" value="" id="vthirdinfo" />
                        </div>
                    </div>

                </div>
                <div class="fieldcontain  ">
                    <label for="sentencesyllr">
                        Syllabary Sentence
                    </label>
                    <input type="text" name="sentencesyllr" value="" id="sentencesyllr" />
                </div>

                <div class="fieldcontain  ">
                    <label for="sentenceq">
                        Tsalagi Sentence
                    </label>
                    <input type="text" name="sentenceq" value="" id="sentenceq" />
                </div>

                <div class="fieldcontain  ">
                    <label for="sentenceenglishs">
                        English Sentence
                    </label>
                    <input type="text" name="sentenceenglishs" value="" id="sentenceenglishs"/>
                </div>

                <div class="fieldcontain  ">
                    <label for="category">
                        Category
                    </label>
                    <input type="text" name="category" value="" id="category" />
                </div>

                <div class="fieldcontain  ">
                    <label for="etymology">
                        Etymology
                    </label>
                    <input type="text" name="etymology" value="" id="etymology" />
                </div>

                <div class="fieldcontain  ">
                    <label for="notes">
                        Notes
                    </label>
                    <textarea name="notes" id="notes"></textarea>
                </div>

                <div class="fieldcontain  ">
                    <label for="definitionlarge">
                        Definition Large
                    </label>
                    <textarea name="definitionlarge" id="definitionlarge"></textarea>
                </div>

                <div class="fieldcontain  ">
                    <label for="crossreferencet">
                        Cross References
                    </label>
                    <input type="text" name="crossreferencet" value="" id="crossreferencet" />
                </div>

                <div class="fieldcontain  ">
                    <label for="source">
                        Source
                    </label>

                    <g:select name="source"
                              from="${cherokee.dictionary.SourceManagement.list()}"
                              value="source.code"
                              optionKey="code"
                              noSelection="['':'-Dictionary or Wordlist Source-']"/>
                </div>
            </fieldset>
            <fieldset class="buttons">
                <input type="submit" name="create" class="save" value="Create" id="create" />
            </fieldset>
        </form>

        <script>
            $(function() {

                $('#partofspeechc').change(function () {
                    var selectedValue = $('#partofspeechc').find(":selected").val();
                    if (selectedValue == "vi" || selectedValue == "vt") {
                        $('#verbals').show();
                        $('#nounAdj').hide();
                    } else if (selectedValue == "adj" || selectedValue == "n") {
                        $('#nounAdj').show();
                        $('#verbals').hide();
                    } else {
                        $('#verbals').hide();
                        $('#nounAdj').hide();
                    }
                });
                $('#fpresshowhide').click(function() {
                    showHide('#fpresshowhide', '#fpres');
                });

                $('#tpastshowhide').click(function() {
                    showHide('#tpastshowhide', '#tpast');
                });

                $('#tpresshowhide').click(function() {
                    showHide('#tpresshowhide', '#tpres');
                });

                $('#simpshowhide').click(function() {
                    showHide('#simpshowhide', '#simp');
                });

                $('#tinfshowhide').click(function() {
                    showHide('#tinfshowhide', '#tinf');
                });

                function showHide(classId, showHideId) {
                    var downarrow = $(classId).children('.fa-sort-down');
                    var uparrow = $(classId).children('.fa-sort-up');
                    if (downarrow != null) {
                        downarrow.attr('class', 'fas fa-sort-up fa-2x');
                    }

                    if (uparrow != null) {
                        uparrow.attr('class', 'fas fa-sort-down fa-2x');
                    }

                    if (downarrow.attr('class') == 'fas fa-sort-up fa-2x') {
                        $(showHideId).show();
                    } else {
                        $(showHideId).hide();
                    }
                }

                $('#fpres').hide();
                $('#tpast').hide();
                $('#tpres').hide();
                $('#simp').hide();
                $('#tinf').hide();
            });
        </script>
    </div>
</body>
</html>