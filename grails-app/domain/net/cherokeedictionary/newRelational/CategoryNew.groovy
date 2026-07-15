package net.cherokeedictionary.newRelational;

class CategoryNew {
    String name
    String description

    static constraints = {
        name nullable: true
        description nullable: true

    }
}