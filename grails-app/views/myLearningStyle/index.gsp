<%@ page import="cherokee.mylearningstyle.LearningEntry" %>
<meta name="layout" content="manager"/>

<%
	def lst = cherokee.mylearningstyle.LearningEntry.findAll()
%>
<a href="#" id="showHideSyllabary">Show/Hide Syllabary</a> | <a href="#" id="showHidePhonetic">Show/Hide Phonetic</a> | <a href="#" id="showHideEnglish">Show/Hide English</a> |&nbsp;
<a href="#" id="showHideAudiolink">Show/Hide Audio</a> | <a href="#" id="showHideReference">Show/Hide Source</a><br/>
<div style="display:table">
	<div style="display:table-row">
		<div style="display:table-cell;padding-right:20px" class="syllabary">Syllabary</div>
		<div style="display:table-cell;padding-right:20px" class="phonetic">Phonetic</div>
		<div style="display:table-cell;padding-right:20px" class="english">English</div>
		<div style="display:table-cell;padding-right:20px" class="audiolink">Audio Link</div>
		<div style="display:table-cell" class="reference">Reference</div>
	</div>
	<g:each var="learningEntry" in="${lst}">
		<div style="display:table-row">
			<div style="display:table-cell" class="syllabary"><span id="syllabary${learningEntry.id}">${learningEntry.syllabary}</span><span id="syllabarya${learningEntry.id}">show</span></div>
			<div style="display:table-cell" class="phonetic"><span id="phonetic${learningEntry.id}">${learningEntry.phonetic}</span><span id="phonetica${learningEntry.id}">show</span></div>
			<div style="display:table-cell" class="english"><span id="english${learningEntry.id}">${learningEntry.english}</span><span id="englisha${learningEntry.id}">show</span></div>
			<div style="display:table-cell" class="audiolink">
				<audio controls style="width: 100px;height:20px">
					<source src="http://cherokeedictionary.net/${learningEntry.audioLink}" type="audio/mpeg">
				</audio>
			</div>
			<div style="display:table-cell" class="reference">${learningEntry.sourceInformation}</div>
		</div>
	</g:each>
</div>

%{--<pre>
ᎦᏙ ᎤᏍᏗ?
Gado usdi?
What is it?
C3L1#7 - WALC1


ᎤᏍᎦᏃᎵᎨ ᏂᏫ.
Usganolige niwi.
Say it slower.
C3L1#8 - WALC1

ᏙᎩ?
Dogi?
What, again?
C3L1#9 - WALC1

ᎯᎠᏃ ____.
Hi’ahno ____.
This is a ____.
C3L1#9 - WALC1

ᎣᏍᏓ ᎾᏆᏛᎿᏕᎦ.
Osda nagwadvhnadega.
I am doing well.
C3L2#3 - WALC1

ᎦᏙ ᏕᏣᏙᎠ?
Gado dejado’a?
What is your name?
C3L2#4 - WALC1

Number counter words
iyanii
</pre>--}%

<script>
	$(function() {

		$('#showHideSyllabary').click(function() {
			$('.syllabary').toggle();
		});

		$('#showHidePhonetic').click(function() {
			$('.phonetic').toggle();
		});

		$('#showHideEnglish').click(function() {
			$('.english').toggle();
		});

		$('#showHideAudiolink').click(function() {
			$('.audiolink').toggle();
		});

		$('#showHideReference').click(function() {
			$('.reference').toggle();
		});

		<g:each var="learningEntry" in="${lst}">
			$('#syllabary${learningEntry.id}').click(function() {
				$('#syllabary${learningEntry.id}').toggle();
				$('#syllabarya${learningEntry.id}').toggle();
			});

			$('#syllabarya${learningEntry.id}').click(function() {
				$('#syllabarya${learningEntry.id}').toggle();
				$('#syllabary${learningEntry.id}').toggle();
			});
			$('#syllabary${learningEntry.id}').toggle();

			$('#phonetic${learningEntry.id}').click(function() {
				$('#phonetic${learningEntry.id}').toggle();
				$('#phonetica${learningEntry.id}').toggle();
			});

			$('#phonetica${learningEntry.id}').click(function() {
				$('#phonetic${learningEntry.id}').toggle();
				$('#phonetica${learningEntry.id}').toggle();
			});
			$('#phonetic${learningEntry.id}').toggle();

			$('#english${learningEntry.id}').click(function() {
				$('#english${learningEntry.id}').toggle();
				$('#englisha${learningEntry.id}').toggle();
			});

			$('#englisha${learningEntry.id}').click(function() {
				$('#english${learningEntry.id}').toggle();
				$('#englisha${learningEntry.id}').toggle();
			});
			$('#english${learningEntry.id}').toggle();

			$('#audiolink${learningEntry.id}').click(function() {
				$('#audiolink${learningEntry.id}').toggle();
				$('#audiolinka${learningEntry.id}').toggle();
			});
		%{--$('#audiolinka${learningEntry.id}').click(function() {--}%
		%{--	$('#audiolink${learningEntry.id}').toggle();--}%
		%{--	$('#audiolinka${learningEntry.id}').toggle();--}%
		%{--});--}%
		%{--	$('#audiolink${learningEntry.id}').toggle();--}%
			%{--$('#reference${learningEntry.id}').click(function() {--}%
			%{--	$('#reference${learningEntry.id}').toggle();--}%
			%{--});--}%


			%{--$('#reference${learningEntry.id}').toggle();--}%
		</g:each>

		function hideAll() {
			$('.syllabary').toggle();
			$('.phonetic').toggle();
			$('.english').toggle();
			$('.audiolink').toggle();
			$('.reference').toggle();
		}

		// hideAll();
	});
</script>


