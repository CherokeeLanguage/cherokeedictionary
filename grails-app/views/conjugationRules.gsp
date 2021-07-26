<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 6/5/2021
  Time: 12:38 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="cherokee.dictionary.conjugationRules.ConjugationRule" %>
<html>
<head>
    <title>Conjugation Rules</title>
    <style>
        .table   { display: table; border-collapse: collapse;}
        .tableRow  { display: table-row}
        .tableCell { display: table-cell; padding-left:5px; padding-bottom:5px; padding-top:5px;padding-right: 10px; border: 1px solid #000; width:35px; white-space: nowrap;}
        sup {font-size:8pt}
    </style>
</head>
<body>
<%
    def createSuper = {sup ->
        "<a href=\"#${sup}\"><sup>[${sup}]</sup></a>"
    }

    def conjugationRuleList = new LinkedList<ConjugationRule>()

    def hla = new ConjugationRule(name: 'hla/tla')
    conjugationRuleList << hla
    hla.meaning = "negative"
    hla.y = "negative"

    def yi = new ConjugationRule(name: 'y-')
    conjugationRuleList << yi
    yi.solo = "y-<br/>yi-"
    yi.intrusiveH = "hy- ${createSuper("1")}"
    yi.meaning = "1.uncertainty<br/>2.conditional${createSuper(2)}"
    yi.w = "yu-"
    yi.notes = "2.past conditionality uses non-progressive form of the verb stem present progressive suffix -a ${createSuper(3)}"

//    yi.notes = "DF CED pp 241-242 #1,2,3"

    def j = new ConjugationRule(name: 'j-')
    conjugationRuleList << j
    j.solo = "j-<br/>ji-"
    j.w = "ju-"
    j.meaning = "1.relative clauses${createSuper(4)}<br/>2.specific past time<br/>3.negative imperative<br/>4.\"again\" imperative"
    j.negative = "n/ni-{verb}-na jigi ${createSuper(5)}"
    j.ga = "ga-{verb} jigi ${createSuper(6)}"
    j.notes = "2.definitive past time or understood past time ${createSuper(7)}<br/>3.must be used to mark negative commands ${createSuper(8)}<br/>4.such as throw it again${createSuper(9)}"

    def w = new ConjugationRule(name: 'w-')
    conjugationRuleList << w
    w.solo = "w-<br/>wi-"
    w.meaning = "1.position or motion away from speaker<br/>2.motion away${createSuper(11)}<br/>3.let${createSuper(12)}<br/>4.2nd imperative${createSuper(13)}5.distance${createSuper(14)}6.dative meaning${createSuper(15)}"
    w.intrusiveH = "hw-"
    w.y = "yu(w)-${createSuper(10)}"
    w.notes = "A few verbs which inherently contain the notion of motion away from the speaker always occur with w- , e.g. wạ²hṿ³sga \"he's sending it.\""

    def n = new ConjugationRule(name: 'n-')
    conjugationRuleList << n
    n.solo = "n-<br/>ni-"
    n.intrusiveH = "hn-"
    n.negative = "3.n-{verb}-na"
    n.meaning = "1.lateral position${createSuper(16)}<br/>2.already${createSuper(17)}<br/>3.negative<br/>4.specified action (done)${createSuper(18)}<br/>5.toward or facing speaker"
    n.notes = "2.typically forms a clause. Requires use of habitual suffix -oʔ1 and may be used with future progressive tense"
    n.da = "5.ni-${createSuper(19)}"
    n.di = "5.ni-${createSuper(20)}"

    def de = new ConjugationRule(name: 'de-')
    conjugationRuleList << de
    de.solo = "d-<br/>de-${createSuper(117)}"
    de.meaning = "plural object"//pp247
    yi.de = "di-${createSuper(118)}"
    w.de = "di-${createSuper(118)}"
    n.de = "di-${createSuper(118)}"
    de.notes = "de- also becomes di- in infinitive and imperative forms when followed by a consonant.<br/>de- becomes j- in infinitive and imperative forms when followed by a vowel other than a-. When followed by a-, de- becomes di-${createSuper(120)}"

//
//    def da = new ConjugationRule(name: 'da-')
//    conjugationRuleList << da1
//    da.solo = "d-<br/>da-"
//
    def di = new ConjugationRule(name: 'di-')
    conjugationRuleList << di
    di.solo = "d-<br/>di-<br/>j-${createSuper(113)}"
    di.meaning = "facing or motion toward speaker"
    di.notes = "di- + past tense o?i di- becomes da-${createSuper(114)}${createSuper(115)}${createSuper(116)}"

    def i = new ConjugationRule(name: 'i-')
    conjugationRuleList << i
    i.meaning = "1.again<br/>2.reocurring${createSuper(112)}"
    i.solo = "iʔ-<br/>i-"
    i.ga = "v-"
    i.de = "do-"
    i.notes = "i- also becomes v- in verb forms with the past tense -vʔi (whether used in its progressive or non-progressive function), with motion*ii verbs in the present, and in infinitive forms;${createSuper(110)}"
    i.di = "di-${createSuper(111)}"

    def ga = new ConjugationRule(name: 'ga-')
    conjugationRuleList << ga
    ga.solo = "ga-<br/>gay-<br/>gv-${createSuper(100)}"
    ga.meaning = "1.since<br/>2.certain time period elapsed${createSuper(101)}"
    ga.notes = "2nd person ge-<br/>third person gvw-<br/>ga- + u- = gvwa-"

    def e = new ConjugationRule(name: 'e-')
    conjugationRuleList << e
    e.solo = "e-<br/>e-"
    e.meaning = "distant imperative${createSuper(102)}"
    e.de = "di- (pl)${createSuper(103)}"
    
//    def pronounPrefix = new ConjugationRule(name: 'pron pref')
//    conjugationRuleList << pronounPrefix
//    w.solo = "-<br/>-"
%>
<div class="tableRow">
    <div class="tableCell"></div>
    <div class="tableCell">meaning</div>
    <div class="tableCell">solo</div>
    <div class="tableCell">pre+h</div>
    <div class="tableCell">negative</div>
    <div class="tableCell">y</div>
    <div class="tableCell">j</div>
    <div class="tableCell">w</div>
    <div class="tableCell">n</div>
    <div class="tableCell">de</div>
    <div class="tableCell">da</div>
    <div class="tableCell">di</div>
    <div class="tableCell">i</div>
    <div class="tableCell">ga</div>
    <div class="tableCell">e</div>
    <div class="tableCell">notes</div>
</div>

<g:each var="rule" in="${conjugationRuleList}">
    <div style="display:table-row;border:2px solid black">
        <div class="tableCell">${raw(rule.name)}</div>
        <div class="tableCell">${raw(rule.meaning)}</div>
        <div class="tableCell">${raw(rule.solo)}</div>
        <div class="tableCell">${raw(rule.intrusiveH)}</div>
        <div class="tableCell">${raw(rule.negative)}</div>
        <div class="tableCell">${raw(rule.y)}</div>
        <div class="tableCell">${raw(rule.j)}</div>
        <div class="tableCell">${raw(rule.w)}</div>
        <div class="tableCell">${raw(rule.n)}</div>
        <div class="tableCell">${raw(rule.de)}</div>
        <div class="tableCell">${raw(rule.da)}</div>
        <div class="tableCell">${raw(rule.di)}</div>
        <div class="tableCell">${raw(rule.i)}</div>
        <div class="tableCell">${raw(rule.ga)}</div>
        <div class="tableCell">${raw(rule.e)}</div>
        <div class="tableCell">${raw(rule.notes)}</div>
    </div>
</g:each>
<br/>

<a name="1">[1]</a> this prefix followed by 'h' results in metathesis where the 'h' moves to the left such as yi- + hi- = hyi-
<br/><a name="2">[2]</a> must occur as subordinate clauses in longer sentences
<br/><a name="3">[3]</a> When used to indicate conditionality in past time, y- requires the non-progressive form of the verb stem and the suffix ordinarily employed with present progressive forms, -a; see (7). yu²wo³²ni⁴sa "if he spoke" DF CED PP 242
<br/><a name="4">[4]</a> function: j- marks relative clauses; it corresponds to the English relative pronouns "who," "which," and "that,"
<br/><a name="5">[5]</a> Since the relative prefix cannot be attached directly to a negated verb form, negative relatives are formed by adding the negative affixes n- (or ni- ) and -na to the verb form; the relative j- is then attached to the auxiliary verb -igi,yielding jigi, as in (12). When j- is prefixed, the second last syllable in the word takes pitch 4.
<br/><a name="6">[6]</a> Similarly, j- cannot occur in the same verb form with ga- "since," but it is possible for j- to be attached to -igi in instances in which the main verb is marked with ga-
<br/><a name="7">[7]</a> Used with a definite past time such as "he spoke yesterday" or understood past time "what did he do yesterday? he spoke"
<br/><a name="8">[8]</a> Don't throw them! hlesdi jidojadesi
<br/>See Section I-C for a discussion of the suffixes employed in imperative forms; see the discussion of de- in this section for the forms which this prefix takes when it occurs together with j- as well as with other prefixes.
<br/>
<br/><a name="9">[9]</a> When the following prefix begins with a vowel, j- may appear either as ji- or as j-; if it appears as ji-, a glottal stop is inserted between it and the vowel which follows.
<br/><a name="10">[10]</a> In some forms, w- may be deleted after the negative y- ; note (28), in which the negative prefix y- becomes yu- with w- optionally deleted.
<br/><a name="11">[11]</a> function: w- is employed optionally to indicate that the subject of the verb is facing away from the speaker. In the case of motion verbs, it follows that the motion will be away from the speaker.
<br/><a name="12">[12]</a> w- must be used with non-second person forms with the meaning "let"
<br/><a name="13">[13]</a> The use of w- with second person imperative forms results in a change in meaning; compare (31b) with (32b). e.g. speak (facing away) hwiwonihi
<br/><a name="14">[14]</a> When the subject of the verb is not within sight of the speaker, the use of w- indicates distance
<br/><a name="15">[15]</a> In the case of certain verbs, in forms with the dative-benefactive suffix, w- is used to indicate dative meaning (he's writing them <u>to</u> him)
<br/><a name="16">[16]</a> used optionally to indicate that the person spoken of is in a lateral position with respect to the speaker the subject of the verb is understood to be standing with his side to the speaker
<br/><a name="17">[17]</a> function: n- may be attached to a past tense verb form to indicate that the action or state expressed by the verb had already taken place or was already in progress prior to some subsequent point in time; Note that the verb in the independent clause may itself be in the habitual. When this is the case, the verb marked with n- is also understood to be habitual; note (41).
<br/><a name="18">[18]</a> function: This prefix occurs with the verb a¹dv³nẹ²ha "to do"; it must be used with this verb when the activity being done has been specified or is understood;
<br/><a name="19">[19]</a> There is an n- prefix which occurs in infinitive forms before da-, motion toward speaker, and before the da- form of di- position facing speaker; This n- prefix does not seem to add any meaning to the verb forms in which it appears; it is simply inserted before the da- prefix in infinitive forms.
<br/><a name="20">[20]</a> The da- form of di-
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%



<br/><a name="100">[100]</a> ga- + -a- = gv-
<br/><a name="101">[101]</a> ga- is used to indicate that a certain period of time has elapsed since an event occurred
<br/><a name="102">[102]</a> function: e- is used with imperative forms when the person spoken to is facing the speaker from a position some distance away. comments: An alternative to considering e- and di-, position facing speaker, to be two distinct prefixes is to analyze them as a single prefix di-, with a variant form e- which occurs in imperative forms when not preceded by another prefix.
<br/><a name="103">[103]</a> form: di- after de-, plural
<br/><a name="110">[110]</a> A peculiarity of the i- prefix is its behavior in forms with a vowel following. In these cases, instead of a glottal stop being inserted, the prefix may appear simply as high pitch on the vowel which would otherwise have directly followed i- The option of deleting the prefix with a change in pitch taking place on the following vowel is a peculiarity of i-, and apparently has to do with the fact that i- carries high pitch, i.e. pitch three.
<br/><a name="111">[111]</a> di- + -i- = di- When such a coalescence occurs, note that the deletion of -a- following di- does not take place; a glottal stop is inserted instead, as in (118c). Before vowels, the coalescence of a preceding prefix and -i- is optional. Instead, the -i- prefix may occur only as high pitch;
<br/><a name="112">[112]</a> Note that i- does not occur in imperative forms; see the description of ji-, "again"-imperative, given above.
<br/><a name="113">[113]</a> form: di- before consonants, j- before vowels other than -a- or -i- If there is a following -a- or -i- is deleted after di-;
<br/><a name="114">[114]</a> When di- appears in a verb form ending in the past tense suffix -vʔi, it changes to da- before consonants and vowels, with a -y- inserted before vowels other than -a-; a following -a- is deleted; cf. (103-105). These changes occur in both progressive and non-progressive past tense forms with -vʔi, but not with -eʔi
<br/><a name="115">[115]</a> An -i- is deleted after da-
<br/><a name="116">[116]</a> The changes noted above in (103-105) also take place in infinitive forms; in addition, any infinitive form with di- must be preceded by ni-. Note (107), in which di- changes to da- with -y- inserted before a vowel, and in which ni- is added to the infinitive form. While ni- is also inserted before da-, motion toward speaker, in infinitive forms, the forms of da- remain the same; i.e., the forms of da- which occur following ni- in infinitives are those illustrated by (88-91).  function: di- is employed with non-motion verbs to indicate that the subject of the verb is facing the speaker. It also occurs with verbs such as "to fight," which involve motion which does not take place in a straight line; i.e., although there is obviously motion involved in fighting, the motion is essentially non-directional. Transitive motion verbs such as "to bring" also take di-; in forms in like (108), it is understood that the subject of the verb is bringing the object in question to a position adjacent to the speaker. With a few verbs, such as "to fight," di- simply indicates that the subject of the verb is in a distant view from the speaker. This function of di- seems to be limited to verbs such as "to fight" in which the nature of the action indicated by the verb involves non-directional motion.
<br/><a name="117">[117]</a> d- before vowels except de- + -i- = de-
<br/><a name="118">[118]</a> except when followed by a vowel with pitch 3 then it remains de-
<br/><a name="120">[120]</a> In those instances in which de- becomes di-, as noted above, and also preceding -h-, the prefix vowel drops and the combination of d- plus -h- yields t-
<br/>de- becomes do- before da-, future, before da-, motion toward speaker, and before di-, position facing speaker
<br/>function: The primary function of de- is to indicate plurality of inanimate direct objects of transitive verbs
<br/>de- may also indicate plurality of the action or state referred to by the verb
<br/>Note that de- cannot be used to make the distinction between single and plural acts of the verb in instances such as (69), since its use is required in these cases to signal the plurality of the direct object of the verb.
<br/>de- must be used with certain intransitive verbs in the plural. The verbs "to sit," "to stand," "to be hungry," and "to be thirsty," for example, require de- when the subject of the verb is plural,
<br/>In addition, other intransitive verbs require the use of de- with both singular and plural forms
<br/>de- is apparently used with the singular form of the verb "to be tired" because the sensation of tiredness is perceived simultaneously in various parts of the body.
<br/>comments: The use of de- with transitive verbs is illustrated in Section I-B, in Paradigm One.
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%
%{--<br/><a name="3">[3]</a>--}%












</body>
</html>