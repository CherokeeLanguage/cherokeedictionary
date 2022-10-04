package net.cherokeedictionary.relational

class Word {

    static constraints = {
        syllabary nullable: true,blank:true
        originalOrthography nullable: true,blank:true
        mcoOrthography nullable: true,blank:true
        phonetic nullable: true,blank:true
        autotransliteration nullable: true,blank:true
    }

    String syllabary
    String phonetic
    Orthography originalOrthography
    Orthography autotransliterateMCOOrthography
    String autotransliteration
    boolean canSave() {
        return syllabary && phonetic
    }
}
