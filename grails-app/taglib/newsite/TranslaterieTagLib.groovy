package newsite

import cherokee.ONLPService

class TranslaterieTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def ONLPService

    def translat = {
        out << raw(ONLPService.serviceMethod())
    }
}
