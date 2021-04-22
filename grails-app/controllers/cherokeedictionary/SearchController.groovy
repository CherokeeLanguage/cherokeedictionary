package cherokeedictionary

import grails.converters.JSON
import grails.converters.XML

class SearchController {
    def newSearchService

    def index() {}

    def specificLookup(params) {

        def searchResults = getSearchResults(params)

        def bob = (searchResults as JSON).toString()

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
        def searchParam;
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

        println searchParam

        def searchResults = newSearchService.newSearch(params, searchParam)

        return searchResults
    }
}