package newsite

import net.cherokeedictionary.transliteration.SyllabaryUtil

class TransliterationTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def translit = {params ->
        out << SyllabaryUtil.tsalagiToSyllabary(params.src)
    }
}
