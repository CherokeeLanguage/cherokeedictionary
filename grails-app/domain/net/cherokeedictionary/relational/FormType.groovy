package net.cherokeedictionary.relational

class FormType {
    String name        // e.g., "base_entry", "noun_plural", "v_first_pres"
    String description // Human-readable description
    static constraints = { name blank: false, unique: true }
}