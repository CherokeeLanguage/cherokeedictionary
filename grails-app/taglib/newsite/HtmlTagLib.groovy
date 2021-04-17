package newsite

import org.springframework.web.context.request.RequestContextHolder

class HtmlTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static namespace = 'html'

    def render = {attrs ->

        def filePath = attrs.file


        if (!filePath) {
            throwTagError("'file' attribute must be provided")
        }

        URL resource = getServletContext().getResource(filePath)
        println resource;
//        InputStream content = resource.getContent()
//        String text = content?.text
//        log.info "includeFile($filePath) - resource: $resource, content.text.size(): ${text?.size()}"
//        out << text

//        def htmlContent = new File(filePath as String).text
//        out << htmlContent
    }
}
