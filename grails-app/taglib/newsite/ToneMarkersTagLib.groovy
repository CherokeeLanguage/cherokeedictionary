package newsite

class ToneMarkersTagLib {
    def frontEndFormatService

    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def readingDisplay = {params ->
        def toneValue = params.tone;
        def latinValue = params.value;
        
        def sb = new StringBuilder();
        
        boolean includes = toneValue != null && (toneValue.contains("1") || toneValue.contains("2") || toneValue.contains("3") || toneValue.contains("4") || toneValue.contains("\\?") || toneValue.contains("\\."))

        if (latinValue) {
            latinValue = latinValue.replaceAll("1", "")
            latinValue = latinValue.replaceAll("2", "")
            latinValue = latinValue.replaceAll("3", "")
            latinValue = latinValue.replaceAll("4", "")

            latinValue = latinValue.replaceAll("\\?", "")
            latinValue = latinValue.replaceAll("\\.", "")

            sb << latinValue
        }

        if (includes) {
            sb << "<br/>"
            toneValue = toneValue.replaceAll("1", "&#x00B9;")
            toneValue = toneValue.replaceAll("2", "&#x00B2;")
            toneValue = toneValue.replaceAll("3", "&#x00B3;")
            toneValue = toneValue.replaceAll("4", "&#x2074;")

//                    toneValue = toneValue.replaceAll("\\?", "<sup>&#x0294;</sup>")//02c0
                    toneValue = toneValue.replaceAll("\\?", "&#x0294;")//02c0
//                    toneValue = toneValue.replaceAll("\\?", "&#x02C0;")
//            toneValue = toneValue.replaceAll("\\?", "<sup>&#x0242;</sup>")
//            toneValue = toneValue.replaceAll("\\?", "&#x0242;")
            //        toneValue = toneValue.replaceAll("\\.", "&#803;")
            toneValue = toneValue.replaceAll("a\\.", "&#x1ea1;")
            toneValue = toneValue.replaceAll("e\\.", "&#x1eb9;")
            toneValue = toneValue.replaceAll("i\\.", "&#x1ecb;")
            toneValue = toneValue.replaceAll("o\\.", "&#x1ecd;")
            toneValue = toneValue.replaceAll("u\\.", "&#x1ee5;")
            toneValue = toneValue.replaceAll("v\\.", "&#x1e7f;")

            toneValue = toneValue.replaceAll("A\\.", "&#x1ea0;")
            toneValue = toneValue.replaceAll("E\\.", "&#x1eb8;")
            toneValue = toneValue.replaceAll("I\\.", "&#x1eca;")
            toneValue = toneValue.replaceAll("O\\.", "&#x1ecc;")
            toneValue = toneValue.replaceAll("U\\.", "&#x1ee4;")
            toneValue = toneValue.replaceAll("V\\.", "&#x1e7e;")

            sb << toneValue
        }
        
        out << raw(sb.toString())
    }

    def pronunciation = {params ->
        def latinValue = params.value;

        out << raw(frontEndFormatService.pronunciation(latinValue))
    }

    def intonation = {params ->
        def toneValue = params.tone;
        def showSup = session.getAttribute("showSup");

        out << raw(frontEndFormatService.intonation(toneValue, showSup))
    }
}
