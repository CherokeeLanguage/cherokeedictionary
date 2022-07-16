package net.cherokeedictionary.relational

class Word {

    static constraints = {
        syllabary nullable: true,blank:true
        originalOrthography nullable: true,blank:true
        mcoOrthography nullable: true,blank:true
        phonetic nullable: true,blank:true
        autotransliteration nullable: true,blank:true
        wordType nullable: true,blank:true
    }

    String syllabary
    String phonetic
    Orthography originalOrthography
    Orthography mcoOrthography
    String autotransliteration
    WordType wordType

    boolean canSave() {
        return syllabary && phonetic
    }
}
