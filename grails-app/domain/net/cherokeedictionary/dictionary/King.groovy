package net.cherokeedictionary.dictionary

class King {
    static constraints = {
        nounRoot blank: true, nullable: true
        useCase blank: true, nullable: true
        present blank: true, nullable: true
        imperf blank: true, nullable: true
        perfective blank: true, nullable: true
        imperative blank: true, nullable: true
        infintive blank: true, nullable: true
        definition blank: true, nullable: true
        firstPresSing blank: true, nullable: true
        thirdPresSing blank: true, nullable: true
        prepronomPrefix blank: true, nullable: true
        notes blank: true, nullable: true
    }

    String nounRoot
    String useCase
    String present
    String imperf
    String perfective
    String imperative
    String infintive
    String definition
    String firstPresSing
    String thirdPresSing
    String prepronomPrefix
    String notes
}
