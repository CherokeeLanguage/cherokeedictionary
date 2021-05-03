package newsite

import net.cherokeedictionary.transliteration.SyllabaryUtil

class ConjugationDisplayTagLib {
    static defaultEncodeAs = [taglib: 'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def display = {params ->

        def item = params.item

        //v2
//        if (item.pronounReflexiveRoot) {
            def sb = new StringBuilder();
            sb << item.pronounReflexiveRoot
            sb << "<span id=\"showHideDisplay\">"
            sb << "<br/>"
            sb << SyllabaryUtil.parseSyllabary(item.pronounReflexiveRoot)
            sb << "</span>"
            out << raw(sb.toString())
//        } else {
//            out << "---------"
//        }
            //v1
//        if (item.getClass() != "java.lang.String") {
//            item = item.syllabary
//        }

//            def sb = new StringBuilder();
//            sb << item
//            sb << "<span id=\"showHideDisplay\">"
//            sb << "<br/>"
//            sb << SyllabaryUtil.parseSyllabary(item)
//            sb << "</span>"
//            out << raw(sb.toString())
    }
}
