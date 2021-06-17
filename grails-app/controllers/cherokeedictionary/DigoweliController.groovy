package cherokeedictionary

import cherokee.corpus.Verse

class DigoweliController {

    def index() {}

    def parameters() {}

    def parse() {
        def MAT_1_1 = new Verse(Book: "MAT", Chapter: "1", Verse: "1", LineMark: "", Context: "Ꭿ̲Ꭰ ᎪᏪᎵ ᎧᏃᎮᎭ ᏧᏁᏢᏔᏅᏒ Ꮵ̲Ꮜ Ꭶ̲ᎶᏁᏛ, Ꮥ̲Ꮻ ᎤᏪᏥ, Ꭱ̲ᏆᎭᎻ ᎤᏪᏥ.", source: "chr")
        def MAT_1_2 = new Verse(Book: "MAT", Chapter: "1", Verse: "2", LineMark: "", Context: "Ꭱ̲ᏆᎭᎻ Ꭱ̲ᏏᎩ ᎤᏕᏁᎴᎢ; Ꭱ̲ᏏᎩᏃ Ꮴ̲ᎦᏈ ᎤᏕᏁᎴᎢ, Ꮴ̲ᎦᏈᏃ Ꮷ̲Ꮣ ᎠᎴ ᎾᏍᎩ ᎠᎾᏓᏅᏟ ᎬᏩᏕᏁᎴᎢ;", source: "chr")
        MAT_1_1.save()
        MAT_1_2.save()

    }
}
