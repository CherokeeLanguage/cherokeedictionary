package docrepo

/**
 * One stored rendition of a document. sha256 drives duplicate detection.
 * A replace never overwrites bytes: the old row flips to isCurrent=false
 * with supersededAt set, and its file stays on disk as history.
 */
class DocRepoFile {

    static constraints = {
        document nullable: false
        originalFilename blank: false, maxSize: 500
        storedFilename blank: false, unique: true, maxSize: 300
        mimeType nullable: true, blank: true, maxSize: 150
        sizeBytes min: 0L
        sha256 blank: false, maxSize: 64
        renditionNote nullable: true, blank: true, maxSize: 500
        uploader nullable: false
        supersededAt nullable: true
    }

    DocRepoDocument document
    String originalFilename
    String storedFilename
    String mimeType
    Long sizeBytes
    String sha256
    String renditionNote
    boolean isCurrent = true
    Date supersededAt
    DocRepoUser uploader

    Date dateCreated

    static mapping = {
        table name: 'docrepo_file'
        version false
        originalFilename column: 'original_filename'
        storedFilename column: 'stored_filename'
        mimeType column: 'mime_type'
        sizeBytes column: 'size_bytes'
        sha256 index: 'idx_docrepo_file_sha256'
        renditionNote column: 'rendition_note'
        // numeric 0/1 — the default BIT(1) mapping misbehaved for updates
        // (supersede writes silently no-oped under the MyISAM dialect)
        isCurrent column: 'is_current', type: 'org.hibernate.type.NumericBooleanType'
        supersededAt column: 'superseded_at'
        dateCreated column: 'date_created'
    }

    String toString() { originalFilename }
}
