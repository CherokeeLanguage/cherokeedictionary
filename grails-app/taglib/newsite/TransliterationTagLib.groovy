package newsite

import net.cherokeedictionary.transliteration.SyllabaryUtil

class TransliterationTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    def su = new SyllabaryUtil()

    def translit = {params ->
        out << su.tsalagiToSyllabary(params.src)
    }
}
