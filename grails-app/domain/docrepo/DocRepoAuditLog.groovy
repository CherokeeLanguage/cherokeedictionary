package docrepo

/**
 * What happened to each entry and by whom. Stores a snapshot of the
 * document id + title instead of a live FK so the history survives a
 * document being deleted.
 */
class DocRepoAuditLog {

    static constraints = {
        documentId nullable: false
        documentTitle nullable: true, blank: true, maxSize: 500
        actor nullable: true
        action blank: false, maxSize: 40
        detail nullable: true, blank: true, maxSize: 2000
    }

    Long documentId
    String documentTitle
    DocRepoUser actor
    String action    // created | replaced_file | cataloged_different | skipped_duplicate | metadata_updated | deleted
    String detail

    Date dateCreated

    static mapping = {
        table name: 'docrepo_audit_log'
        version false
        documentId column: 'document_id'
        documentTitle column: 'document_title'
        detail type: 'text'
        action column: 'action'
        dateCreated column: 'date_created'
    }
}
