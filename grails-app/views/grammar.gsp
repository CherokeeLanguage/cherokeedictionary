<html>
<head>
    <meta name="layout" content="manager"/>
    <title>Grammar</title>
    <script>
        $(function() {
            $("#toc").load('${resource(dir: 'js', file: 'hold1.txt')}');
            $("#toc2").load('${resource(dir: 'js', file: 'hold2.txt')}');
            %{--$("#toc").load('${resource(dir: 'stylesheets', file: 'GrammarTableOfContents.html')}');--}%
            %{--$("#introduction").load('${resource(dir: 'stylesheets', file: 'GrammarIntroduction.html')}');--}%
            %{--$("#verbtable").load('${resource(dir: 'stylesheets', file: 'VerbTable1Grammar.html')}');--}%
            %{--$("#verbtableyprefix").load('${resource(dir: 'stylesheets', file: 'YprefixGrammar.html')}');--}%
            %{--$("#tlwedit").load('${resource(dir: 'stylesheets', file: 'tlwEdit.html')}');--}%
            %{--$("#tlwedit2").load('${resource(dir: 'stylesheets', file: 'tlwEdit2.html')}');--}%
        });
    </script>
</head>
<body>

<div id="toc" style="padding-left:10px;padding-right:10px"></div>
<center><img alt="" style="width: 767px; height: 511px;"
             src="${resource(dir: 'images', file: 'classificatorytree.png')}"></center>
<div id="toc2" style="padding-left:10px;padding-right:10px"></div>
%{--<iframe src="/hold" style="position:fixed; top:80px; left:0; bottom:0; right:0; width:100%; height:100%; border:none; margin:0; padding:0; overflow:hidden; z-index:999999;"></iframe>--}%
%{--<div id="introduction" style="padding-left:10px;padding-right:10px"></div>--}%
%{--<div id="verbtable" style="padding-left:10px;padding-right:10px"></div>--}%
%{--<div id="verbtableyprefix" style="padding-left:10px;padding-right:10px"></div>--}%
%{--<div id="tlwedit" style="padding-left:10px;padding-right:10px"></div>--}%
%{--<div id="tlwedit2" style="padding-left:10px;padding-right:10px"></div>--}%
</body>
</html>