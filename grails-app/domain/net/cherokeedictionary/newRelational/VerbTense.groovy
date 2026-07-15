package net.cherokeedictionary.newRelational;

class VerbTense {
    String name
    String description

    static hasMany = [verbDetails: Verb]

    static constraints = {
        name nullable: false
        description nullable: true


    }
}