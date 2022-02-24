package net.cherokeedictionary.dictionary

class FrontEndFormatService {

    def pronunciation(latinValue) {
        def sb = new StringBuilder();
        if (latinValue) {
            latinValue = latinValue.replaceAll("1", "")
            latinValue = latinValue.replaceAll("2", "")
            latinValue = latinValue.replaceAll("3", "")
            latinValue = latinValue.replaceAll("4", "")

            latinValue = latinValue.replaceAll("\\?", "")
            latinValue = latinValue.replaceAll("\\.", "")

            sb << latinValue
        }
        return sb.toString()
    }

    def intonation(toneValue, showSup) {
        boolean includes = toneValue && (toneValue.contains("1") || toneValue.contains("2") || toneValue.contains("3") || toneValue.contains("4") || toneValue.contains("\\?") || toneValue.contains("\\."))

        if (!includes) {
            includes  = toneValue && toneValue =~ /[ạẹịọụṿ¹²³⁴ɂ]/
        }

        def sb = new StringBuilder();

        if (includes) {
            sb << "<br/>"
            toneValue = toneValue.replaceAll("1", "&#x00B9;")
            toneValue = toneValue.replaceAll("2", "&#x00B2;")
            toneValue = toneValue.replaceAll("3", "&#x00B3;")
            toneValue = toneValue.replaceAll("4", "&#x2074;")
            //        toneValue = toneValue.replaceAll("\\?", "<sup>&#x0294;</sup>")//02c0
            //        toneValue = toneValue.replaceAll("\\?", "&#x02C0;")
            if (showSup) {
                toneValue = toneValue.replaceAll("\\?", "<sup>&#x0242;</sup>")
            } else {
                toneValue = toneValue.replaceAll("\\?", "&#x0242;")
            }

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

            toneValue = toneValue.replaceAll("¹", "&#x00B9;")
            toneValue = toneValue.replaceAll("²", "&#x00B2;")
            toneValue = toneValue.replaceAll("³", "&#x00B3;")
            toneValue = toneValue.replaceAll("⁴", "&#x2074;")
            //        toneValue = toneValue.replaceAll("\\?", "<sup>&#x0294;</sup>")//02c0
            //        toneValue = toneValue.replaceAll("\\?", "&#x02C0;")
            if (showSup) {
                toneValue = toneValue.replaceAll("ɂ", "<sup>&#0241;</sup>")
            } else {
                toneValue = toneValue.replaceAll("ɂ", "&#0241;")
            }
            //        toneValue = toneValue.replaceAll("\\.", "&#803;")
            toneValue = toneValue.replaceAll("ạ", "&#x1ea1;")
            toneValue = toneValue.replaceAll("ẹ", "&#x1eb9;")
            toneValue = toneValue.replaceAll("ị", "&#x1ecb;")
            toneValue = toneValue.replaceAll("ọ", "&#x1ecd;")
            toneValue = toneValue.replaceAll("ụ", "&#x1ee5;")
            toneValue = toneValue.replaceAll("ṿ", "&#x1e7f;")

            sb << toneValue
        } else if (toneValue) {//fix for https://bitbucket.org/cdrchops/cherokeedictionaryproject/issues/41/if-tonal-entries-dont-contain-tonal
            sb << "<br/>${toneValue}"
        }

        return sb.toString();
    }
}
