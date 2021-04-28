package cherokeedictionary

import grails.converters.JSON
import grails.converters.XML
import net.cherokeedictionary.transliteration.SyllabaryUtil

class SearchController {
    def newSearchService

    def index() {}

    def specificLookup(params) {
        params.isSyllabary = true
        params.includeCED = true;
        params.includeSentence = false
        params.searchForExactMatch = true
        params.searchForExactMatch = true
        params.includeCED = true
        params.includeConsortiumWordList = true
        params.includeCNOMedWordList = true
        params.includeNCMedWordList = true
        params.includeMicrosoftWordList = true
        params.includeNOQWordList = true
        params.includeRRD = true

        def searchResults = getSearchResults(params)
        println searchResults.size()

        def bob = "";
        if (searchResults.isEmpty()) {
            bob = "null";
        } else {
            bob = (searchResults as JSON).toString()
        }

        render bob
    }

    def renderJson(params, searchResults) {
        def bob = (searchResults as JSON).toString()

        //replacing the unicode character so the html is unbroken - i'd rather figure out a way to replace all of it
        bob = bob.replaceAll("u002f", "")
        bob = bob.replaceAll("\\\\", "/")

        //wrap the data in a JSON class
//        bob = "{'data' : ${bob}}"
        bob = bob

        if (params.callback) {

            render (text: "${params.callback}($bob)",
                    contentType: "text/javascript",
                    encoding: "UTF-8")
        } else {
            render bob
        }
    }

    def jsonshow() {
        def searchResults = getSearchResults(params)

        renderJson(params, searchResults)
    }

    def xmlshow() {
        def searchResults = getSearchResults(params)
        render searchResults as XML
    }

    def getSearchResults(params) {
        def searchParam
        if (params.definition) {
            params.englishSearch=params.definition
            searchParam = params.englishSearch
        } else if (params.entry) {
            params.tsalagiSearch = params.entry
            searchParam = params.tsalagiSearch
        } else if (params.syllabary) {
            params.syllabarySearch=params.syllabary
            searchParam = params.syllabarySearch
        }

//        println SyllabaryUtil.parseSyllabary(params.syllabary)

        def searchResults = newSearchService.newSearch(params, searchParam);

        return searchResults
    }
}