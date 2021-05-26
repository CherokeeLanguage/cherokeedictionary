package cherokee.relational

class Sentence {

    static constraints = {
        autotranslit blank:true, nullable: true
        tone blank:true, nullable: true
    }

    String syllabary
    String tone
    String tsalagi
    String autotranslit
    String english
}
