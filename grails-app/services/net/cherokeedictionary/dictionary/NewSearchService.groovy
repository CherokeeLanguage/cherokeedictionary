package net.cherokeedictionary.dictionary

import net.cherokeedictionary.audio.AudioLink

import net.cherokeedictionary.admin.SourceManagement
import net.cherokeedictionary.relational.PartOfSpeech
import net.cherokeedictionary.relational.Category
import net.cherokeedictionary.transliteration.SyllabaryUtil
import net.cherokeedictionary.corpus.Verse
import net.cherokeedictionary.searchLogic.SortOrderComparator

import java.text.SimpleDateFormat

import net.cherokeedictionary.searchLogic.SortOrder

class NewSearchService {
    final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    def tsalagiFields = ['entrya', 'nounadjplurale', 'vfirstpresg', 'vsecondimperm', 'vthirdinfo', 'vthirdpasti', 'vthirdpresk']
    def tsalagiTranslitFields = ['entrytranslit', 'nounadjpluraltranslit', 'vfirstprestranslit', 'vsecondimpertranslit', 'vthirdinftranslit', 'vthirdpasttranslit', 'vthirdprestranslit']
    def syllabaryFields = ['syllabaryb', 'nounadjpluralsyllf', 'vfirstpresh', 'vthirdpastsyllj', 'vthirdpressylll', 'vsecondimpersylln', 'vthirdinfsyllp']
    def englishFields = ['definitiond']

    def search(params) {
        def tsalagiSearchParam = params?.tsalagiSearch?.trim()
        def englishSearchParam = params?.englishSearch?.trim()
        def syllabarySearchParam = params?.syllabarySearch?.trim()
        def categorySearchParam = params?.categorySearch?.trim()

        def definitionId = params.definitionId

        def searchParam = "";

        def searchResults = []

//        final Date today = Calendar.getInstance().getTime()

        if (tsalagiSearchParam) {
            searchParam = tsalagiSearchParam?.trim()?.toLowerCase()
        } else if (englishSearchParam) {
            searchParam = englishSearchParam?.trim()
        } else if (syllabarySearchParam) {
            searchParam = syllabarySearchParam?.trim()
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
        def sourcesSize = 7
        def searchForExactMatch = params.searchForExactMatch
        def max = 40
        def moffset = params.offset ? Integer.parseInt(params.offset) : 0

        Map<String, String> map = new LinkedHashMap<String, String>();

        if (searchTerm) {
            //need to fix this so that if it's bible only then no other searches are performed
            //bible=on

            searchTerm = searchTerm.replaceAll("\\*", "%")

            if (isTsalagi) {
                processTsalagiRequest(searchForExactMatch, map, searchTerm, includeSentences, includeCED)
            }

            if (isEnglish) {
                processEnglishRequest(searchForExactMatch, map, searchTerm, includeSentences, includeCED)
            }

            if (isSyllabary) {
                processSyllabaryRequest(searchForExactMatch, map, searchTerm, includeSentences, includeCED)
            }

            def lst = new LinkedList<String>();
            def sb = generateQuery(params, sourcesSize, searchForExactMatch, lst, map)

//            def mapOfRules = [:]
//            if (resultsMax) {
//                println "resultsMax ${resultsMax}"
//                mapOfRules.put("max", resultsMax)
//            }
//
//            if (resultsOffset) {
//                println "resultsOffset ${resultsOffset}"
//                mapOfRules.put("offset", resultsOffset)
//            }
//            [max: resultsMax, offset:resultsOffset]
            def results = new LinkedList<Object>()
            def result = Likespreadsheets.findAll(sb, lst, [max: max, offset:moffset]);
            if (result) {
                results.addAll(result)
            }

            if (params.bible) {
                def returnResults = []

                if (isTsalagi) {
                    searchTerm = SyllabaryUtil.tsalagiToSyllabary(searchTerm)
                    returnResults = Verse.findAll("from Verse where verseContext like ?0 and source = ?1 and ( bookName = 'MAT' or bookName = 'MAK' or bookName = 'LUK' or bookName = 'JHN' or bookName = 'ACT' or bookName = 'ROM' or bookName = '1CO' or bookName = '2CO' or bookName = 'GAL' or bookName = 'EPH' or bookName = 'PHL' or bookName = 'COL' or bookName = '1TS' or bookName = '2TS' or bookName = '1TM' or bookName = '2TM' or bookName = 'TIT' or bookName = 'PHM' or bookName = 'HEB' or bookName = 'JAM' or bookName = '1PE' or bookName = '2PE' or bookName = '1JN' or bookName = '2JN' or bookName = '3JN' or bookName = 'JUD' or bookName = 'REV') order by id asc", ["%${searchTerm}%", 'chr'], [max:40, offset:moffset])
                } else if (isSyllabary) {
                    returnResults = Verse.findAll("from Verse where verseContext like ?0 and source = ?1 and ( bookName = 'MAT' or bookName = 'MAK' or bookName = 'LUK' or bookName = 'JHN' or bookName = 'ACT' or bookName = 'ROM' or bookName = '1CO' or bookName = '2CO' or bookName = 'GAL' or bookName = 'EPH' or bookName = 'PHL' or bookName = 'COL' or bookName = '1TS' or bookName = '2TS' or bookName = '1TM' or bookName = '2TM' or bookName = 'TIT' or bookName = 'PHM' or bookName = 'HEB' or bookName = 'JAM' or bookName = '1PE' or bookName = '2PE' or bookName = '1JN' or bookName = '2JN' or bookName = '3JN' or bookName = 'JUD' or bookName = 'REV') order by id asc", ["%${searchTerm}%", 'chr'], [max:40, offset:moffset])
                } else if (isEnglish) {
                    returnResults = Verse.findAll("from Verse where verseContext like ?0 and source != ?1 and ( bookName = 'MAT' or bookName = 'MAK' or bookName = 'LUK' or bookName = 'JHN' or bookName = 'ACT' or bookName = 'ROM' or bookName = '1CO' or bookName = '2CO' or bookName = 'GAL' or bookName = 'EPH' or bookName = 'PHL' or bookName = 'COL' or bookName = '1TS' or bookName = '2TS' or bookName = '1TM' or bookName = '2TM' or bookName = 'TIT' or bookName = 'PHM' or bookName = 'HEB' or bookName = 'JAM' or bookName = '1PE' or bookName = '2PE' or bookName = '1JN' or bookName = '2JN' or bookName = '3JN' or bookName = 'JUD' or bookName = 'REV') order by id asc", ["%${searchTerm}%", 'chr'], [max:40, offset:moffset])
                }

                if (returnResults) {
                    results.addAll(returnResults)
                }
            }

            if (params.audio) {
                def returnResults = []
                results.each {
                    def tmp = AudioLink.findAll("from AudioLink where likespreadsheets_id=?0", [it.id])
                    if (tmp) {
                        returnResults << it
                    }
                }

                Collections.sort(returnResults, new SortOrderComparator())
                return returnResults
            } else {

                Collections.sort(results, new SortOrderComparator())
                return results
            }
        }

        return []
    }

    def generateQuery(params, sourcesSize, searchForExactMatch, lst, map) {
        def sb = new StringBuilder()
        sb << "from Likespreadsheets l where "

        def sources = []

        def sourceList = SourceManagement.findAll()

        sourceList.each {
            if (params."${it.searchParameter}") {
                sources << "${it.code}" as String
            }
        }

        if (sources.size() != sourcesSize && sources.size() != 0) {
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

        if (sources.size() != sourcesSize && sources.size() != 0) {
            sb << ") and ("
            sources.eachWithIndex { it, idx ->
                if (idx > 0) {
                    sb << " or "
                }

                //because the new sql needs indexed parameters so the current source index plus the total query parameters from the map give us the next index
                sb << "source = ?${idx + map.size()}"
                lst << it
            }
            sb << ")"
        }

        sb << " order by l.entrya"

        return sb
    }

    def processEnglishRequest(searchForExactMatch, map, searchTerm, includeSentences, includeCED) {
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

    def processTsalagiRequest(searchForExactMatch, map, searchTerm, includeSentences, includeCED) {
        String replacedSearchTerm = SyllabaryUtil.replace(searchTerm);

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

    def processSyllabaryRequest(searchForExactMatch, map, searchTerm, includeSentences, includeCED) {
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

        def max = 40
        def moffset = params.offset ? Integer.parseInt(params.offset) : 40

//        println "moffset ${moffset}"

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

                def tmp = Likespreadsheets.createCriteria().list(max:40, offset:moffset) {
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
                }

                if (tmp) {
                    results.addAll(tmp)
                }
            } else {
                if (isTsalagi) {
                    def tmp = Likespreadsheets.createCriteria().list(max: 40, offset: moffset) {
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
                    }

                    results.addAll(tmp)
                }

                if (isSyllabary) {
                    def tmp = Likespreadsheets.createCriteria().list(max: 40, offset: moffset) {
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
                    }

                    results.addAll(tmp)
                }

                if (isEnglish) {
                    def tmp = Likespreadsheets.createCriteria().list(max: 40, offset: moffset) {
                        englishFields.each { english ->
                            rlike(english, searchTerm)
                        }

                        if (pos) {
                            eq('partofspeechc', pos.partofspeech)
                        }

                        if (category) {
                            eq('category', category.category)
                        }
                    }

                    results.addAll(tmp)
                }
            }

            Collections.sort(results, new SortOrderComparator())
            return results
        } else if (isTsalagi && isEnglish && isSyllabary
                    || isTsalagi && isEnglish
                    || isSyllabary && isEnglish
                    || isTsalagi && isSyllabary) {

            def tmp = Likespreadsheets.createCriteria().list(max:40, offset:moffset) {
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
            }

            if (tmp) {
                results.addAll(tmp)
            }
        } else {
            if (isEnglish) {
                def tmp = Likespreadsheets.createCriteria().list(max:40, offset:moffset) {
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
                }

                results.addAll(tmp)
            }

            if (isTsalagi) {
                def tmp = Likespreadsheets.createCriteria().list(max:40, offset:moffset) {
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
                }

                results.addAll(tmp)
            }

            if (isSyllabary) {
                def tmp = Likespreadsheets.createCriteria().list(max:40, offset:moffset) {
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
                }

                results.addAll(tmp)
            }
        }

//        if (results == []) {
//            results = newSearch(params, searchTerm)
//        }

        Collections.sort(results, new SortOrderComparator())
        return results
    }

    def categorySearch(params) {
        Category category = Category.findById(Integer.parseInt(params.categorySearch))
        def lst = Likespreadsheets.findAll('from Likespreadsheets l where l.category = ?0', [category.category])
        Collections.sort(lst, new SortOrderComparator())
        return lst
    }

    def xrefSearch(term) {
        //multiple entries
        def searchTerm = term.indexOf(",") ? term.split(",") : term
        if (searchTerm.size() == 1) {
            def result = Likespreadsheets.findAll("from Likespreadsheets l where l.entrya like ?0", ["%$term%"])
            Collections.sort(result, new SortOrderComparator())
            return result
        } else {
            List lst = []

            searchTerm.each {
                def leest = Likespreadsheets.findAll("from Likespreadsheets l where l.entrya like ?0", ["%$it%"])
                lst.addAll(leest)
            }

            Collections.sort(lst, new SortOrderComparator())
            return lst
        }
    }

    def xrefById(definitionId) {
        return Likespreadsheets.findById(definitionId)
    }
}