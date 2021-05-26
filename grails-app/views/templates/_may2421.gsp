<%
    def lst = cherokee.mylearningstyle.LearningEntry.findAll()
%>

<div style="display:table; margin-left:20px">
    <div style="display:table-row">
        <div style="display:table-cell;padding-right:20px" class="syllabary">Syllabary</div>
        <div style="display:table-cell;padding-right:20px" class="phonetic">Phonetic</div>
        <div style="display:table-cell;padding-right:20px" class="english">English</div>
        <div style="display:table-cell;padding-right:20px" class="audiolink">Audio Link</div>
        <div style="display:table-cell" class="reference">Reference</div>
    </div>
    <g:each var="learningEntry" in="${lst}">
        <div style="display:table-row">
            <div style="display:table-cell" class="syllabary"><span id="syllabary${learningEntry.id}">${learningEntry.syllabary}</span>%{--<span id="syllabarya${learningEntry.id}">show</span>--}%</div>
            <div style="display:table-cell" class="phonetic"><span id="phonetic${learningEntry.id}">${learningEntry.phonetic}</span>%{--<span id="phonetica${learningEntry.id}">show</span>--}%</div>
            <div style="display:table-cell" class="english"><span id="english${learningEntry.id}">${learningEntry.english}</span>%{--<span id="englisha${learningEntry.id}">show</span>--}%</div>
            <div style="display:table-cell" class="audiolink">
                <audio controls style="width: 100px;height:20px">
                    <source src="http://cherokeedictionary.net/${learningEntry.audioLink}" type="audio/mpeg">
                </audio>
            </div>
            <div style="display:table-cell" class="reference">${learningEntry.sourceInformation}</div>
        </div>
    </g:each>
</div>

<g:render template="may2421Testyourself"/>