<meta name="layout" content="manager"/>

<g:clearCitations/>
<ol>
	<li><a href="#chapter1">Chapter 1</a></li>
	<li><a href="#chapter2">Chapter 2</a></li>
</ol>
%{-- pulls file from grails webapp directory --}%
%{--<audio type="audio/mp3" src="/myLearningStyle/audio?fileName=/mystyle/chap1/CherokeeConsortiumbyAnnaSixkiller.mp3" sound-id="10" version="full" controls=""></audio>--}%

%{-- pulls file directly from server -- this is preferable because the war will be smaller and the audio files can just be uploaded to a directory --}%
%{--<audio type="audio/mp3" src="http://cherokeedictionary.net/audio/audio.mp3" sound-id="10" version="full" controls=""></audio>--}%

<a name="chapter1"></a>
<g:render template="1Greetings/Chapter"/>
<br/><br/>
<a name="chapter2"></a>
<g:render template="2WhatDoYouWantToDo/Chapter"/>
%{--<g:render template="hold"/>--}%


<script>
	$(function() {
		$('#toggleLat').click(function() {
			$('#lat0').toggle();
			$('#lat1').toggle();
			$('#lat2').toggle();
			$('#lat3').toggle();
		});

		$('#toggleEn').click(function() {
			$('#en0').toggle();
			$('#en1').toggle();
			$('#en2').toggle();
			$('#en3').toggle();
		});

		$("#toggleEn").click();
		$("#toggleLat").click();

		$('#hideTranslit').click(function() {
			$('.redPhonetic').toggle();
			$('.redPhonetic').attri("display", "hide")
		});

		$('#hideEnglish').click(function() {
			$('.english').toggle();
		});

		$('#hideSyllabary').click(function() {
			$('.syllabary').toggle();
		});

		$('#showGrammar').click(function() {
			$('.grammarLesson').toggle();
		});

		$('.redPhonetic').toggle();
	});
</script>
%{--http://www.joshjacob.com/mac-development/qrjournal.php#download -- qr code reader for mac--}%
%{--<pre>--}%
<h3>Bibliography</h3>
<g:printCitations/>
%{--</pre>--}%

