package net.cherokeedictionary.relational

class Word {

    static constraints = {
        syllabary nullable: true,blank:true
        tone nullable: true,blank:true
        tsalagi nullable: true,blank:true
        autotranslit nullable: true,blank:true
    }

    String syllabary
    String tone
    String tsalagi
    String autotranslit
}
