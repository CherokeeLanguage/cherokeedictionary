package net.cherokeedictionary

import org.springframework.core.io.Resource

/**
 * Serves the Google Search Console domain-verification file at its exact
 * legacy path (cherokeedictionary#140). On the legacy stack nginx served
 * this file from the doc root; the Grails app now owns it. The file lives
 * on the classpath (src/main/resources) so it is packaged with the app and
 * served byte-exact — no GSP view processing in the path.
 */
class SiteVerificationController {

    static allowedMethods = [index: "GET"]

    def index() {
        Resource res = grailsApplication.parentContext
                .getResource("classpath:google593a568777046d5c.html")
        if (res == null || !res.exists()) {
            response.status = 404
            return
        }
        response.contentType = "text/html"
        response.characterEncoding = "UTF-8"
        render(file: res.inputStream, contentType: "text/html")
    }
}
