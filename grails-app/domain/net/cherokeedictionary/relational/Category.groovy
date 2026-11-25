package net.cherokeedictionary.relational

class Category {

    static constraints = { category blank: false, unique: true; name nullable: true, blank: true; description nullable: true, blank: true }

    String category
    String name
    String description
    
    

    public String toString() {
        return category
    }
}
