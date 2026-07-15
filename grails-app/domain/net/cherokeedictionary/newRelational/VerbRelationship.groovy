package net.cherokeedictionary.newRelational;

// WordRelationship.groovy (Renamed/Updated)
class VerbRelationship {
    Word parentWord   // The root
    Word childWord    // The inflected form
    VerbTense tense   // Links directly to the Lookup Table

    static constraints = {
        parentWord nullable: false
        childWord nullable: false
        tense nullable: false

    }
}