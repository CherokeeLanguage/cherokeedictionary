package net.cherokeedictionary.relational

class Dialect {
    String dialect     // Code token, e.g., 'O', 'G', 'S'
    String name        // Full name, e.g., "Otali", "Giduwah", "Snowbird"
    String description // Regional or historical notes

    static constraints = {
        dialect blank: false, unique: true
        name blank: false
        description nullable: true
    }
}