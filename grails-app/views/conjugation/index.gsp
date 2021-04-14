<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 8/16/2015
  Time: 3:48 PM
--%>

<html>
<head>
    <title></title>
</head>

<body>
%{--    if (vth) {
        def present = vth.present

        def remotePast = vth.remotePast
        def habitual = vth.habitual
//        def immediate = vth.immediate
//        def deverbal = vth.deverbal
    def infinitive = vth.infinitive

        def completiveFuture = vth.completiveFuture
        def futureCommand = vth.futureCommand
        def progressivePast = vth.progressivePast
        def progressiveFuture = vth.progressiveFuture
        def immediateCommand = vth.immediateCommand

        def conjugate = {verbTense, verbForm ->
            def vc = new VerbConjugation();
            def sb = new StringBuilder();
            sb << "<tr>"
            sb << "<td>${verbTense}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.I, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.YOU, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.THEY, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.WE_ALL, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.I_YOU, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.I_SHE, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.I_THEY, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.YOU_TWO, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "<td>${vc.conjugate(PronominalPrefix.YOU_ALL, verbTense, VerbSet.A, verbForm)}</td>"
            sb << "</tr>"

            out << raw(sb)
        }

        out << raw("<table>")
        out << raw("<tr><td></td><td>I</td><td>You</td><td>They</td><td>We All</td><td>I You</td><td>I She</td><td>I They</td><td>You Two</td><td>You All</td></tr>")
        conjugate(Tense.PRESENT, present)
        conjugate(Tense.COMPLETIVE_FUTURE, completiveFuture)
        conjugate(Tense.FUTURE_COMMAND, completiveFuture)
        conjugate(Tense.PROGRESSIVE_PAST, progressivePast)
        conjugate(Tense.REMOTE_PAST, remotePast)
        conjugate(Tense.HABITUAL, habitual)
        conjugate(Tense.PROGRESSIVE_FUTURE, progressiveFuture)
        conjugate(Tense.IMMEDIATE_COMMAND, immediateCommand)
        conjugate(Tense.INFINITIVE, infinitive)
        out << raw("</table>")
    }

    if (displayValue) {
        out << displayValue
    }--}%
</body>
</html>