package dictionary

import net.cherokeedictionary.stemmer.DefinitionLine
import net.cherokeedictionary.stemmer.Stemmer

class ConjugationController {

    def index() {
        def s = new Stemmer()
        s.habitual = new DefinitionLine(syllabary: "syllabary")
    }
}
