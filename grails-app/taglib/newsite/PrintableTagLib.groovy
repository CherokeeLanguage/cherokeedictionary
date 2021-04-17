package newsite

import cherokee.dictionary.myLearningStyle.MLS
import net.cherokeedictionary.transliteration.SyllabaryUtil

class PrintableTagLib {
    static defaultEncodeAs = [taglib:'html']

    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def su = new SyllabaryUtil();

    def printWholeLesson = {params ->
        def sb = new StringBuilder();
        final lst = params.sentences
        sb << "<div class=\"row\">"
        sb << "<div class=\"column\">"
        lst.eachWithIndex { MLS mls, idx ->
            def syll = su.tsalagiToSyllabary(mls.phonetic)
            sb <<  "<span class=\"syllabary\">${idx + 1}."
            sb << syll
            sb << "</span><br/>"
            sb << "<span class=\"redPhonetic\">${mls.phonetic}</span>"
            sb << "<br/>"
        }

	    sb << "</div>"
        sb << "<div>"

        lst.eachWithIndex { MLS mls, idx ->
            sb << "<span class=\"english\">${idx + 1}. ${mls.english}</span><br/>"
        }

	    sb << "</div>"
        sb << "</div>"

        out << raw(sb.toString())
    }

}
