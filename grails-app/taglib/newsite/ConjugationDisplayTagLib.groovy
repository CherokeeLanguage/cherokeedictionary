package newsite

import net.cherokeedictionary.transliteration.SyllabaryUtil

class ConjugationDisplayTagLib {
    static defaultEncodeAs = [taglib: 'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static final def su = new SyllabaryUtil();

    def display = {params ->
        def item = params.item
//        if (item.getClass() != "java.lang.String") {
//            item = item.syllabary
//        }

        def sb = new StringBuilder();
        sb << item
        sb << "<span id=\"showHideDisplay\">"
        sb << "<br/>"
        sb << su.parseSyllabary(item)
        sb << "</span>"
        out << raw(sb.toString())
    }
}
