package net.cherokeedictionary.relational

class Sentence {
    static constraints = {
        syllabary nullable: true,blank:true
        mcoOrthography nullable: true,blank:true
        phonetic nullable: true,blank:true
        autotransliteration nullable: true,blank:true
    }

    String syllabary
    String phonetic
    Orthography mcoOrthography
    String autotransliteration
    String translation

    boolean canSave() {
        return syllabary && phonetic
    }
}
