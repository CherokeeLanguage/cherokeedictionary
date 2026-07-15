package net.cherokeedictionary.relational

class Category {

    static constraints = {
    }

    String category
    String name
    String description

    public String toString() {
        return category
    }
}
