package docrepo

/**
 * Explicit many-to-many link row (document <-> tag). Kept as a first-class
 * domain instead of a GORM-managed join collection: it is written
 * deterministically in DocRepoService.attachTags and survives a transient
 * owner (the managed hasMany silently dropped join rows).
 */
class DocRepoDocumentTag implements Serializable {

    static constraints = {
        document nullable: false
        tag nullable: false
    }

    DocRepoDocument document
    DocRepoTag tag

    static mapping = {
        table name: 'docrepo_document_tag'
        version false
        id composite: ['document', 'tag']
    }

    String toString() { "${documentId}/${tagId}" }
}
