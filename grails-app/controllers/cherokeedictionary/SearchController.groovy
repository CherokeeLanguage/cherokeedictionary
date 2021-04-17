package cherokeedictionary

import grails.converters.JSON
import grails.converters.XML

class SearchController {
    def newSearchService

    def index() {}

    def jsonshow() {
        def searchParam;
        if (params.definition) {
//            render newSearchService.englishSearch(params.definition, params) as JSON
            params.englishSearch=params.definition
            searchParam = params.englishSearch
        } else if (params.entry) {
//            render newSearchService.tsalagiSearch(params.entry, params) as JSON
            params.tsalagiSearch = params.entry
            searchParam = params.tsalagiSearch
        } else if (params.syllabary) {
//            render newSearchService.syllabarySearch(params.syllabary, params) as JSON
            params.syllabarySearch=params.syllabary
            searchParam = params.syllabarySearch
        }

//        def searchResults = newSearchService.search(params, request.getRemoteAddr())
        def searchResults = newSearchService.newSearch(params, searchParam)

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

    def xmlshow() {
        def searchParam;

        if (params.definition) {
//            render newSearchService.englishSearch(params.definition, params) as JSON
            params.englishSearch=params.definition
            searchParam = params.englishSearch
        } else if (params.entry) {
//            render newSearchService.tsalagiSearch(params.entry, params) as JSON
            params.tsalagiSearch = params.entry
            searchParam = params.tsalagiSearch
        } else if (params.syllabary) {
//            render newSearchService.syllabarySearch(params.syllabary, params) as JSON
            params.syllabarySearch=params.syllabary
            searchParam = params.syllabarySearch
        }

        def searchResults = newSearchService.newSearch(params, searchParam)
//        def searchResults = newSearchService.search(params, request.getRemoteAddr())
        render searchResults as XML
//        if (params.definition) {
//            render newSearchService.englishSearch(params.definition, params) as XML
//        } else if (params.entry) {
//            render newSearchService.tsalagiSearch(params.entry, params) as XML
//        } else if (params.syllabary) {
//            render newSearchService.syllabarySearch(params.syllabary, params) as XML
//        }
    }
}

/*http://sbglasius.tumblr.com/post/16661253375/grails-jsonp
withFormat {
 json {
   if (params.callback) {
     render (
       text: "${params.callback}(${data as JSON})",
       contentType: "text/javascript",
       encoding: "UTF-8"
     )
   } else {
      render data as JSON
   }
 }
 xml { render data as XML }
}
*/