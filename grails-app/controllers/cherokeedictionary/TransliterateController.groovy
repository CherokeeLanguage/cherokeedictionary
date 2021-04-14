package cherokeedictionary

import cherokee.dictionary.Likespreadsheets
import net.cherokeedictionary.transliteration.SyllabaryUtil

class TransliterateController {

    def index() {
        def toTransliterate = params.text.trim()
        def from = Boolean.parseBoolean(params.from)

        def results

        if (from) {
            results = SyllabaryUtil.mixedTransliteration(toTransliterate)
        } else {
            results = SyllabaryUtil.parseSyllabaryWithPunctuationAndLatin(toTransliterate)
        }

        render([returnedValue: results], "${results}" as String)
    }
}
