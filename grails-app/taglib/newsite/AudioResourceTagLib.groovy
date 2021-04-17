package newsite

import org.springframework.web.context.support.ServletContextResource

class AudioResourceTagLib {
    def assetResourceLocator;
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def renderAudio = { attrs, body ->
        def filePath = attrs.file
        if (!filePath) {
            throwTagError("'file' attribute must be provided")
        }

        ServletContextResource bar = (ServletContextResource) assetResourceLocator.findAssetForURI(filePath)
        String fileAsPlainString = bar.getFile().getText("UTF-8")
        out << fileAsPlainString
    }

}
