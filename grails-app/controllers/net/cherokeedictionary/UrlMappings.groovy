package net.cherokeedictionary

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller:"dictionary.newSearch", view:"/index")
        "/login"(view:"/login")
        "/search"(view:"/index")
        "/about"(view:"/about")
        "/grammar"(view:"/grammar")
        "/first500"(view:"/first500")
        "/bionic"(view:"/bionic")

//        "/conjugation"(view:"/conjugation")
        "/conj"(controller:"conjugation", view:'index')
        "/index"(controller:"dictionary.newSearch", view:"/index")
        "/share/$id"(controller:"newSearch", action:"share")
        "/individual"(controller:"newSearch", action:"individual")
        "/monday"(view:"/monday")
        "/syllabary"(view:"/syllabary")
        "/vrules"(view:"/conjugationRules")

        "/transliteration"(view:"/transliteration")
        "/transliterate"(view:"/transliterate")
        "/translation"(view:"/translation")
        "/refined"(view:"/refined")
        "/translate"(view:"/translate")
        "/announcementsPage"(view:"/announcementsPage")
        "/advancedSearch"(view:"/advancedSearch")
//        "/readingAndListening"(view:"/readingAndListening")
//        "/xrefdisplay"(controller:"newSearch", view:"/xrefdisplay")
        "/xrefdisplay"(action: "xrefdisplay", controller: "newSearch")

        "/xmlsearch/chr/$entry "(controller: "search", action: "xmlshow")
        "/xmlsearch/en/$definition"(controller: "search", action: "xmlshow")
        "/xmlsearch/syll/$syllabary"(controller: "search", action: "xmlshow")

        "/jsonsearch/chr/$entry "(controller: "search", action: "jsonshow")
        "/jsonsearch/en/$definition"(controller: "search", action: "jsonshow")
        "/jsonsearch/syll/$syllabary"(controller: "search", action: "jsonshow")
        "/jsonlookup/$syllabary"(controller: "search", action: "specificLookup")

        "500"(view:'/error')
        "404"(view:'/notFound')

        "/spreadsheet"(resources:"cherokee.dictionary.Likespreadsheets")
    }
}
