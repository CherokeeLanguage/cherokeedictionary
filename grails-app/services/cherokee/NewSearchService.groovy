package cherokee

import cherokee.dictionary.Likespreadsheets
import cherokee.dictionary.SourceManagement
import cherokee.relational.PartOfSpeech
import cherokee.relational.Category
import net.cherokeedictionary.transliteration.SyllabaryUtil

import java.text.SimpleDateFormat


class NewSearchService {
    final static def su = new SyllabaryUtil();
    final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    def tsalagiFields = ['entrya', 'nounadjplurale', 'vfirstpresg', 'vsecondimperm', 'vthirdinfo', 'vthirdpasti', 'vthirdpresk']
    def tsalagiTranslitFields = ['entrytranslit', 'nounadjpluraltranslit', 'vfirstprestranslit', 'vsecondimpertranslit', 'vthirdinftranslit', 'vthirdpasttranslit', 'vthirdprestranslit']
    def syllabaryFields = ['syllabaryb', 'nounadjpluralsyllf', 'vfirstpresh', 'vthirdpastsyllj', 'vthirdpressylll', 'vsecondimpersylln', 'vthirdinfsyllp']
    def englishFields = ['definitiond']

    def search(params) {
        def tsalagiSearchParam = params.tsalagiSearch?.trim()
        def englishSearchParam = params.englishSearch?.trim()
        def syllabarySearchParam = params.syllabarySearch?.trim()
        def categorySearchParam = params.categorySearch?.trim()

        def definitionId = params.definitionId

        def searchParam = "";

        def searchResults = []

//        final Date today = Calendar.getInstance().getTime()

        if (tsalagiSearchParam) {
            searchParam = tsalagiSearchParam.trim().toLowerCase()
        } else if (englishSearchParam) {
            searchParam = englishSearchParam.trim()
        } else if (syllabarySearchParam) {
            searchParam = syllabarySearchParam.trim()
        } else if (definitionId) {
            //noop
        } else {
            //            need to send the user to an error page
        }

        if (categorySearchParam) {
            searchResults = categorySearch(params)
        } else {
            searchResults = newSearch(params, searchParam)
        }

//        new UserSearch(searchTerm: searchParam, dateTime: DATE_FORMAT.format(today), ipAddress: remoteAddress).save()

        return searchResults;
    }

    def advancedSearch(params) {
        def searchResults = advSearch(params)

//        final Date today = Calendar.getInstance().getTime()
//        new UserSearch(searchTerm: searchParam, dateTime: DATE_FORMAT.format(today), ipAddress: remoteAddress).save()

        return searchResults;
    }

    def newSearch(params, searchTerm) {
        def isTsalagi = params.tsalagiSearch?.trim()
        def isEnglish = params.englishSearch?.trim()
        def isSyllabary = params.syllabarySearch?.trim()
        def includeCED = params.includeCED
        def includeSentences = params.includeSentences
        def sourcesSize = 7;
        def searchForExactMatch = params.searchForExactMatch

        Map<String, String> map = new LinkedHashMap<String, String>();

        def isRegex = false;

        if (searchTerm) {
            if (searchTerm.contains("\\*")) {
               isRegex = true;
            }

            searchTerm = searchTerm.replaceAll("\\*", "%")
            if (isTsalagi) {
                String replacedSearchTerm = su.replace(searchTerm);

                //When adding multiple of the same key to the map I've added a suffix of "||" this is removed when adding to the final stringbuilder for the dictionary search -- timo 4Sep15
                tsalagiFields.each {
                    if (searchForExactMatch) {
                        map.put("$it||", replacedSearchTerm)
                        map.put(it, searchTerm)
                    } else {
                        map.put("$it||", "%$replacedSearchTerm%")
                        map.put(it, "%$searchTerm%")
                    }
                }

                tsalagiTranslitFields.each {
                    if (searchForExactMatch) {
                        map.put("$it||", replacedSearchTerm)
                        map.put(it, searchTerm)
                    } else {
                        map.put("$it||", "%$replacedSearchTerm%" as String)
                        map.put(it, "%$searchTerm%" as String)
                    }
                }

                if (includeSentences && includeCED) {
                    if (searchForExactMatch) {
                        map.put('sentenceq', replacedSearchTerm)
                        map.put('sentencetranslit', replacedSearchTerm)
                        map.put('sentenceq||', searchTerm)
                        map.put('sentencetranslit||', searchTerm)
                    } else {
                        map.put('sentenceq', "%$replacedSearchTerm%" as String)
                        map.put('sentencetranslit', "%$replacedSearchTerm%" as String)

                        map.put('sentenceq||', "%$searchTerm%" as String)
                        map.put('sentencetranslit||', "%$searchTerm%" as String)
                    }
                }

                /*if (includeSentences && includeCED) {
                if (searchForExactMatch) {
                    map.put('sentenceq', searchTerm)
                    map.put('sentencetranslit', searchTerm)
                } else {
                    map.put('sentenceq', "%$searchTerm%" as String)
                    map.put('sentencetranslit', "%$searchTerm%" as String)
                }
            }*/
            }

            if (isEnglish) {
                englishFields.each {
                    if (searchForExactMatch) {
                        map.put(it, searchTerm)
                    } else {
                        map.put(it, "%$searchTerm%" as String)
                    }
                }

                if (includeSentences && includeCED) {
                    if (searchForExactMatch) {
                        map.put('sentenceenglishs', searchTerm)
                    } else {
                        map.put('sentenceenglishs', "%$searchTerm%" as String)
                    }
                }
            }

            if (isSyllabary) {
                syllabaryFields.each {
                    if (searchForExactMatch) {
                        map.put(it, searchTerm)
                    } else {
                        map.put(it, "%$searchTerm%" as String)
                    }
                }

                if (includeSentences && includeCED) {
                    if (searchForExactMatch) {
                        map.put('sentencesyllr', searchTerm)
                    } else {
                        map.put('sentencesyllr', "%$searchTerm%" as String)
                    }
                }
            }

            def sb = new StringBuilder()
            sb << "from Likespreadsheets l where "

            def lst = new LinkedList<String>();

            def sources = []

            def sourceList = SourceManagement.findAll()

            sourceList.each {
                if (params."${it.searchParameter}") {
                    sources << "${it.code}" as String
                }
            }

            if (sources.size != sourcesSize && sources.size != 0) {
                sb << "("
            }

            map.eachWithIndex { it, idx ->
                if (idx > 0) {
                    sb << " or"
                }

                sb << " l.${it.key.endsWith("||") ? it.key.substring(0, it.key.size() - 2) : it.key}"

                if (searchForExactMatch) {
                    sb << " = ?${idx}"
                } else {
                    sb << " like ?${idx}"
                }

                lst << it.value
            }

            if (sources.size != sourcesSize && sources.size != 0) {
                sb << ") and ("
                sources.eachWithIndex { it, idx ->
                    if (idx > 0) {
                        sb << " or "
                    }

                    sb << "source = ?${idx}"
                    lst << it
                }
                sb << ")"
            }

            sb << " order by l.entrya"

            if (isRegex) {
                return Likespreadsheets.findAll(sb, lst, [max: 40]);
            } else {
                return Likespreadsheets.findAll(sb, lst);
            }
        }

        return []
    }

    def advSearch(params) {
        def isTsalagi = params.tsalagiSearch?.trim()
        def isEnglish = params.englishSearch?.trim()
        def isSyllabary = params.syllabarySearch?.trim()
        def exactMatch = params.searchForExactMatch
        def regexSearch = params.regex
        def searchTerm
        def categoryParam = params.categorySearch?.trim()
        def definitionId = params.definitionId

        def posParam = params.posSearch

        PartOfSpeech pos
        if (posParam) {
            pos = PartOfSpeech.findById(posParam)
        }

        Category category
        if (categoryParam) {
            category = Category.findById(categoryParam)
        }

        if (isTsalagi) {
            searchTerm = isTsalagi.trim().toLowerCase()
        } else if (isEnglish) {
            searchTerm = isEnglish
        } else if (isSyllabary) {
            searchTerm = isSyllabary
        } else if (definitionId) {
            //noop
        } else {
            //            need to send the user to an error page
        }

        Map<String, String> map = new LinkedHashMap<String, String>();
        def results = []

        if (regexSearch) {
            if (isTsalagi && isEnglish && isSyllabary
                    || isTsalagi && isEnglish
                    || isSyllabary && isEnglish
                    || isTsalagi && isSyllabary) {

                def tmp = Likespreadsheets.createCriteria().list {
                    //            projections {
                    //                property("firstName")
                    //            }
                    and {
                        if (isTsalagi) {
                            or {
                                tsalagiFields.each { tsalagi ->
                                    rlike(tsalagi, isTsalagi)
                                }
                            }
                        }

                        if (isSyllabary) {
                            or {
                                syllabaryFields.each { syllabary ->
                                    rlike(syllabary, isSyllabary)
                                }
                            }
                        }

                        if (isEnglish) {
                            or {
                                englishFields.each { english ->
                                    rlike(english, isEnglish)
                                }
                            }
                        }
                    }

                    if (pos) {
                        eq('partofspeechc', pos.partofspeech)
                    }

                    if (category) {
                        eq('category', category.category)
                    }

                    maxResults(40)
                }

                if (tmp) {
                    results.addAll(tmp)
                }
            } else {
                if (isTsalagi) {
                    def tmp = Likespreadsheets.createCriteria().list {
                        or {
                            tsalagiFields.each { tsalagi ->
                                rlike(tsalagi, searchTerm)
                            }
                        }

                        if (pos) {
                            eq('partofspeechc', pos.partofspeech)
                        }

                        if (category) {
                            eq('category', category.category)
                        }

                        maxResults(40)
                    }

                    results.addAll(tmp)
                }

                if (isSyllabary) {
                    def tmp = Likespreadsheets.createCriteria().list {
                        or {
                            syllabaryFields.each { syllabary ->
                                rlike(syllabary, searchTerm)
                            }
                        }

                        if (pos) {
                            eq('partofspeechc', pos.partofspeech)
                        }

                        if (category) {
                            eq('category', category.category)
                        }

                        maxResults(40)
                    }

                    results.addAll(tmp)
                }

                if (isEnglish) {
                    def tmp = Likespreadsheets.createCriteria().list {
                        englishFields.each { english ->
                            rlike(english, searchTerm)
                        }

                        if (pos) {
                            eq('partofspeechc', pos.partofspeech)
                        }

                        if (category) {
                            eq('category', category.category)
                        }

                        maxResults(40)
                    }

                    results.addAll(tmp)
                }
            }

            return results
        } else if (isTsalagi && isEnglish && isSyllabary
                    || isTsalagi && isEnglish
                    || isSyllabary && isEnglish
                    || isTsalagi && isSyllabary) {

            def tmp = Likespreadsheets.createCriteria().list {
                //            projections {
                //                property("firstName")
                //            }
                and {
                    if (isTsalagi) {
                        or {
                            tsalagiFields.each { tsalagi ->
                                rlike(tsalagi, isTsalagi)
                            }
                        }
                    }

                    if (isSyllabary) {
                        or {
                            syllabaryFields.each { syllabary ->
                                rlike(syllabary, isSyllabary)
                            }
                        }
                    }

                    if (isEnglish) {
                        or {
                            englishFields.each { english ->
                                rlike(english, isEnglish)
                            }
                        }
                    }
                }

                if (pos) {
                    eq('partofspeechc', pos.partofspeech)
                }

                if (category) {
                    eq('category', category.category)
                }

                maxResults(40)
            }

            if (tmp) {
                results.addAll(tmp)
            }
        } else {
            if (isEnglish) {
                def tmp = Likespreadsheets.createCriteria().list {
                    englishFields.each { english ->
                        if (exactMatch) {
                            eq(english, searchTerm)
                        } else {
                            rlike(english, searchTerm)
                        }
                    }

                    if (pos) {
                        eq('partofspeechc', pos.partofspeech)
                    }

                    if (category) {
                        eq('category', category.category)
                    }

                    maxResults(40)
                }

                results.addAll(tmp)
            }

            if (isTsalagi) {
                def tmp = Likespreadsheets.createCriteria().list {
                    or {
                        tsalagiFields.each { tsalagi ->
                            if (exactMatch) {
                                eq(tsalagi, searchTerm)
                            } else {
                               rlike(tsalagi, searchTerm)
                            }
                        }
                    }

                    if (pos) {
                        eq('partofspeechc', pos.partofspeech)
                    }

                    if (category) {
                        eq('category', category.category)
                    }

                    maxResults(40)
                }

                results.addAll(tmp)
            }


            if (isSyllabary) {
                def tmp = Likespreadsheets.createCriteria().list {
                    or {
                        syllabaryFields.each { syllabary ->
                            if (exactMatch) {
                                eq(syllabary, searchTerm)
                            } else {
                                rlike(syllabary, searchTerm)
                            }
                        }
                    }

                    if (pos) {
                        eq('partofspeechc', pos.partofspeech)
                    }

                    if (category) {
                        eq('category', category.category)
                    }

                    maxResults(40)
                }

                results.addAll(tmp)
            }
        }

//        if (results == []) {
//            results = newSearch(params, searchTerm)
//        }

        return results
    }

    def categorySearch(params) {
        cherokee.relational.Category category = cherokee.relational.Category.findById(Integer.parseInt(params.categorySearch))
        def lst = Likespreadsheets.findAll('from Likespreadsheets l where l.category = ?1', [category.category])

        return lst
    }

    def xrefSearch(term) {
        //multiple entries
        def searchTerm = term.indexOf(",") ? term.split(",") : term
        if (searchTerm.size() == 1)
            return Likespreadsheets.findAll("from Likespreadsheets l where l.entrya like ?1", ["%$term%"])
        else {
            List lst = []

            searchTerm.each {
                def leest = Likespreadsheets.findAll("from Likespreadsheets l where l.entrya like ?1", ["%$it%"])
                lst.addAll(leest)
            }

            return lst
        }
    }

    def xrefById(definitionId) {
        return Likespreadsheets.findById(definitionId)
    }
}
