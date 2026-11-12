<%if (session.getAttribute("loggedin")){%>
This is to generate conjugation tests easier for boilerplate script<br/>

static def verbName = new Stemmer()<br/>
static {<br/>
verbName.habitual = new DefinitionLine(syllabary: "${stemmer.habitual.syllabary}")<br/>
verbName.imperative = new DefinitionLine(syllabary: "${stemmer.imperative.syllabary}")<br/>
verbName.infinitive = new DefinitionLine(syllabary: "${stemmer.infinitive.syllabary}")<br/>
verbName.present1st = new DefinitionLine(syllabary: "${stemmer.present1st.syllabary}")<br/>
verbName.present3rd = new DefinitionLine(syllabary: "${stemmer.present3rd.syllabary}")<br/>
verbName.remotepast = new DefinitionLine(syllabary: "${stemmer.remotepast.syllabary}")<br/>
}
<br/><br/>
paramMap.habitual = "${stemmer.habitual.syllabary}"<br/>
paramMap.imperative = "${stemmer.imperative.syllabary}"<br/>
paramMap.infinitive = "${stemmer.infinitive.syllabary}"<br/>
paramMap.present1st = "${stemmer.present1st.syllabary}"<br/>
paramMap.present3rd = "${stemmer.present3rd.syllabary}"<br/>
paramMap.remotepast = "${stemmer.remotepast.syllabary}"<br/>
paramMap.partofspeechc = "${entry.partofspeechc}"<br/>
paramMap.verbTense = ""<br/>
paramMap.subject = ""<br/>
paramMap.object = ""<br/>
<% } %>