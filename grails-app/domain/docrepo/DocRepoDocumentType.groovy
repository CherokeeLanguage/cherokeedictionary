package docrepo

/**
 * Controlled-ish vocabulary for what kind of item this is (dissertation,
 * wordlist, learning article, ...). Seeded idempotently by
 * DocRepoAuthService.ensureDefaultTypes; new names arrive on the fly when
 * a caller posts a type that doesn't exist yet.
 */
class DocRepoDocumentType {

    static constraints = {
        name blank: false, unique: true, maxSize: 80
    }

    String name
    int sortOrder

    static mapping = {
        table name: 'docrepo_document_type'
        version false
        sortOrder column: 'sort_order'
    }

    String toString() { name }
}
