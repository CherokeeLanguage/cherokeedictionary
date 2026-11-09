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
        // the Google Search Console domain-verification file at its exact
        // legacy path (#140) — must precede the catch-all in effect
        "/google593a568777046d5c.html"(controller:"siteVerification")
        // bare /favicon.ico (direct crawler fetches) — the executable WAR does
        // not serve src/main/webapp statically, so route to the asset
        // pipeline's canonical, non-hashed favicon (favicon-di-de follow-up)
        "/favicon.ico"(redirect: "/assets/favicon.ico")
        "/search"(view:"/index")
        "/about"(view:"/about")
        "/grammar"(view:"/grammar")
        "/first500"(view:"/first500")
//        "/conjugation"(view:"/conjugation")
        "/conj"(controller:"conjugation", view:'index')
        "/index"(controller:"dictionary.newSearch", view:"/index")
        "/share/$id"(controller:"newSearch", action:"share")
        "/individual"(controller:"newSearch", action:"individual")
        "/monday"(view:"/monday")
        "/syllabary"(view:"/syllabary")
        "/vrules"(view:"/conjugationRules")
        "/medicalTerms"(controller: "newSearch", action:"/medicalTerms")

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

        "/stems"(controller:"stemmer", action:"stems")
    }
}
