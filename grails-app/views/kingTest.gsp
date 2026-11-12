<%@page import="net.cherokeedictionary.dictionary.NewSearchService; net.cherokeedictionary.dictionary.King; net.cherokeedictionary.dictionary.Likespreadsheets; net.cherokeedictionary.transliteration.SyllabaryUtil" %>
<%--
From the King Dictionary
1. All letters should be considered lower case except the first letter in the family, order, or genus names of plant and animal species.
2. In the Cherokee entries:
a. A question mark (?) should be interpreted as a glottal stop (?).
b. A slash (/) should be interpreted as an acute accent mark (') over the preceding vowel.
c. A period (.) should be interpreted as vowel length (v).
3. Cherokee verbs are entered by either root or stem and are designated VBST in columns 16 through 20 on the key punch cards.

Columns 22 through 47 contain the aspect suffixes required by the verb and are listed in the following order:

present (columns 22-24),
imperfective (26-29),
perfective (31-34),
imperative (36-40), and
infinitive (42-47).

The imperative entry (columns 36-40) contains both aspect and modal suffixes.
An asterisk (*) in any cf these columns indicates that the aspect suffix replaces the last letter in the verb root.

The symbol @ indicates that multiple letters are replaced.
A few verb stems which are altered by suppletion with certain aspect suffixes are designated IRR (irregular in the aspect suffix column).

The first person singular pronominal prefix is entered in columns 71-74,
the third person singular prefix is found in column 75-76,
and obligatory prepronominal prefixes are listed in columns 79- 80 where applicable.
A dash (-) in column 71 indicates an intransitive verb which is not used with a first person pronominal prefix

A zero (0) in column 75 indicates Class II stems with vowel initials which have zero representation of third person singular subjects.

Classificatory verbs have the class recorded in parentheses following the English translation.
Because of space some are abbreviated, e. g. , FLEX, FLX, and FL flexible, ANIM and AN animate, RG rigid, RD round, and LQ liquid.

4. Noun roots are designated NR in columns 19 and 20
The first person singular pronominal prefix is listed in columns 22-24 and third person singular prefix is given in columns 27-28.
The prefix U* indicates that this prefix replaces the initial vowel /a/b y a regular morphophonemic rule.
Commonly used plurals are given in column 32 and following columns.
If two plural forms are listed, the first is attached to the first person singular pronominal, the second to the third person singular prefix.
5. The following designations in columns 19 and 20 indicate affixes: PP pre pronominal prefix, *PR pronominal prefix, DS derivative suffix, MS modal suffix, and ATS attributive suffix. The numeral following * PR is correlated with the numbers on Chart 7.
6. All Cherokee entries other than those designated VBST, NR, PP, *PR, DS, MS, and ATS are complete words.
7. Names of plants and animals are designated by GN in columns 19 and 20. Genus and species names are provided in columns 22-47. The family or order names are listed for terms which apply to more than one genus.
8. Nouns derived from verbs are designated DN, particles PT, and nouns which cannot be inflected by a pronominal prefix NU.
9. In the English to Cherokee section columns 48- 8 0 are printed on the left side of the page, with columns 1-47 on the right
10. The alphabetical order used by the computer is as follows: blank space, .,/,? , A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z.
--%>
<%!
    def convert = {toConvert ->
        toConvert = toConvert
                .replaceAll("\\?", "")
                .replaceAll("\\.", "")
                .replaceAll("/", "")
                .replaceAll("0", "")
                .replaceAll("hs", "s")
                .replaceAll("kw", "gw")
                .replaceAll("uh", "u")
                .replaceAll("kh", "k")
                .replaceAll("to", "do")
                .replaceAll("th", "t")
                .replaceAll("hna", "na")
                .replaceAll("-", "")
                .replaceAll("ke", "ge")
                .replaceAll("ki", "gi")
                .replaceAll("ko", "go")
                .replaceAll("ku", "gu")
                .replaceAll("kv", "gv")
                .replaceAll("hl", "l")
                .replaceAll("tiha", "diha")
                .replaceAll("ehl", "el")
                .replaceAll("sk", "sg")

//                    .replaceAll("ka", "ga")

//                    .replaceAll("k", "g")
//                    .replaceAll("kh", "k")
//                    .replaceAll("t", "d")
//                    .replaceAll("th", "t")
//                    .replaceAll("hs", "s")
//                    .replaceAll("hl", "l")
//                    .replaceAll("dh", "t")
//                    .replaceAll("wuo", "woo")
//                    .replaceAll("ws", "was")
//                    .replaceAll("hn", "n")
//                    .replaceAll("hm", "m")
//                    .replaceAll("hd", "d")
//                    .replaceAll("ht", "t")
//                    .replaceAll("nh", "nv")
//                    .replaceAll("ua", "u")
//                    .replaceAll("dt", "dit")
//                    .replaceAll("dita", "diha")
//                    .replaceAll("kt", "gat")
//                    .replaceAll("aat", "at")
//                    .replaceAll("aad", "ad")
//                    .replaceAll("hy", "y")
//                    .replaceAll("ds", "ts")
//                    .replaceAll("gt", "gat")
//                    .replaceAll("hgh", "g")
//                    .replaceAll("gaa", "ga")
//                    .replaceAll("lha", "hla")

//                .replaceAll("ls", "lis")
//                .replaceAll("ls", "las")

        if (toConvert.startsWith("ua")) {
            toConvert = "a" + toConvert.substring(2)
        }

        if (toConvert.endsWith("ka")) {
            toConvert = toConvert.substring(0, toConvert.length() - 2) + "ga"
        }

        if (toConvert.endsWith("ti")) {
            toConvert = toConvert.substring(0, toConvert.length() - 2) + "di"
        }

        //SyllabaryUtil.replace(toConvert, true)
        return toConvert
    }

    def lookup = { look ->
        def result = Likespreadsheets.find(
                "from Likespreadsheets l where l.entrya like ?0 or l.entrytranslit like ?1",
                [look, look]
        )
        if (!result) {
            def returnList = []
            def tsalagiList = Likespreadsheets.withCriteria {
                or {
                    like 'entrya', '%' + look + '%'
                    like 'nounadjpluraltranslit', '%' + look + '%'
                    like 'vfirstprestranslit', '%' + look + '%'
                    like 'vsecondimpertranslit', '%' + look + '%'
                    like 'vthirdinftranslit', '%' + look + '%'
                    like 'vthirdpasttranslit', '%' + look + '%'
                    like 'vthirdprestranslit', '%' + look + '%'
                    like 'entrytranslit', '%' + look + '%'
                }
            }

            if (tsalagiList.contains(look)) {
                tsalagiList.each {
                    if (it.entrya == look) {
                        returnList << it.entrya
                    }
                }
            } else {
                tsalagiList.each {
                    returnList << it.entrya
                }
            }

            result = returnList.join(":::")
        }

        return result
    }

    //TODO: NOTES FOR FUTURE VERB GORM SQL - timo 10Nov26
//            def tsalagiList = Likespreadsheets.withCriteria {
//                or {
//                    like 'entrya', '%' + look + '%'
//                    like 'nounadjpluraltranslit', '%' + look + '%'
//                    like 'vfirstprestranslit', '%' + look + '%'
//                    like 'vsecondimpertranslit', '%' + look + '%'
//                    like 'vthirdinftranslit', '%' + look + '%'
//                    like 'vthirdpasttranslit', '%' + look + '%'
//                    like 'vthirdprestranslit', '%' + look + '%'
//                    like 'entrytranslit', '%' + look + '%'
//                }
//            }
//
//            result = tsalagiList
    def createLookup = { searchTerm ->
        // Combine the field lists
        def allFields = NewSearchService.tsalagiFields + NewSearchService.tsalagiTranslitFields

        def tmp = Likespreadsheets.createCriteria().list(max: 40) {
            or {
                for (tsalagi in allFields) {
                    rlike(tsalagi, searchTerm)
                }
            }
        }

        return tmp
    }

    def lookups = { look ->
        def result = Likespreadsheets.find(
                "from Likespreadsheets l where l.entrya like ?0 or l.entrytranslit like ?1",
                [look, look]
        )

        if (!result) {
            result = createLookup(look)
        } else {
            result = [result]
        }

        return result
    }

    def replaceAsterisk = {king, entry, tmpOutput ->
        if (king."$entry".contains("*")) {
            tmpOutput << king.nounRoot.substring(0, king.nounRoot.size() - 1)
            tmpOutput << king."$entry".substring(1)
        } else {
            tmpOutput << king.nounRoot
            tmpOutput << king."$entry"
        }
    }

    //select * from king where present like '%*%' or imperf like '%*%' or perfective like '%*%' or imperative like '%*%' or infintive like '%*%';
    def kingMain = { king ->
        def tmpOutput = new StringBuilder()
        tmpOutput << king.thirdPresSing

        replaceAsterisk(king, "present", tmpOutput)

        tmpOutput << "a"
        tmpOutput = convert(tmpOutput.toString())
        return tmpOutput
    }

    def kingFirstPresSing = { king ->
        def tmpOutput = new StringBuilder()
        tmpOutput << king.firstPresSing

        replaceAsterisk(king, "present", tmpOutput)

        tmpOutput << "a"
        tmpOutput = convert(tmpOutput.toString())
        return tmpOutput
    }

    def kingPerfective = { king ->
        def tmpOutput = new StringBuilder()
        tmpOutput << king.thirdPresSing

        replaceAsterisk(king, "perfective", tmpOutput)

        tmpOutput << "vi"
        tmpOutput = convert(tmpOutput.toString())
        return tmpOutput
    }

    def kingImperative = { king ->
        def tmpOutput = new StringBuilder()
        tmpOutput << king.thirdPresSing

        replaceAsterisk(king, "imperative", tmpOutput)

        //tmpOutput << "a"
        tmpOutput = convert(tmpOutput.toString())
        return tmpOutput
    }

    def kingInfinitive = { king ->
        def tmpOutput = new StringBuilder()
        tmpOutput << king.thirdPresSing

        replaceAsterisk(king, "infintive", tmpOutput)

        tmpOutput << "i"
        tmpOutput = convert(tmpOutput.toString())
        return tmpOutput
    }

    def kingImperf = { king ->
        def tmpOutput = new StringBuilder()
        tmpOutput << king.thirdPresSing

        replaceAsterisk(king, "imperf", tmpOutput)

        tmpOutput << "oi"
        tmpOutput = convert(tmpOutput.toString())
        return tmpOutput
    }
%>