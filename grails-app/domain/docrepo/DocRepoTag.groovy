package docrepo

/**
 * Free-form user-defined tag ("verb", "conjugation", "year", ...).
 * Linked many-to-many to documents through docrepo_document_tag.
 */
class DocRepoTag {

    static constraints = {
        name blank: false, unique: true, maxSize: 64
    }

    String name

    static mapping = {
        table name: 'docrepo_tag'
        version false
    }

    String toString() { name }
}
