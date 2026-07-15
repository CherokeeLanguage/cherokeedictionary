package net.cherokeedictionary.newRelational;

class WordRelationship {
    Word parentWord
    Word childWord
    String relationshipType // e.g., "vfirstpres", "plural"

    static constraints = {
        parentWord nullable: false
        childWord nullable: false
        relationshipType nullable: false

    }
}