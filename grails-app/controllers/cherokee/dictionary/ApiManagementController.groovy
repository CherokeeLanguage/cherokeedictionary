package cherokee.dictionary

import grails.converters.JSON

class ApiManagementController {
    def sourceManagement() {
        def lst = SourceManagement.findAll()
        renderJson(lst)
    }

    def announcements() {
        def lst = Announcements.findAll()
        renderJson(lst)
    }

    def renderJson(searchResults) {
        if (params.callback) {
            render (text: "${params.callback}($searchResults)",
                    contentType: "text/javascript",
                    encoding: "UTF-8")
        } else {
            render (searchResults as JSON)
        }
    }
}