package net.cherokeedictionary.relational

class Orthography {

    static constraints = {
        orthography nullable: true,blank:true
    }

    def orthography
    OrthographyType orthographyType
}
/*
    create view
    add items to view
    go through view and split items into
*/