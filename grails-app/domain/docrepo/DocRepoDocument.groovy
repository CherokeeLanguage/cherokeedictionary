package docrepo

/**
 * One catalog entry. May hold stored files (docrepo_file, one per rendition,
 * with full byte-history) or no file at all when the item lives elsewhere
 * (externalUrl is then the plain hyperlink to wherever it lives).
 */
class DocRepoDocument {

    static constraints = {
        title blank: false, maxSize: 500
        authors nullable: true, maxSize: 1000
        type nullable: true
        externalUrl nullable: true, maxSize: 1000
        copyrightStatus nullable: true, inList: ['Public domain', 'Creative Commons', 'Used with permission', 'All rights reserved', 'Unknown']
        copyrightNote nullable: true, blank: true, maxSize: 2000
        notes nullable: true, blank: true, maxSize: 10000
        uploader nullable: false
    }

    static hasMany = [files: DocRepoFile]

    String title
    String authors
    DocRepoDocumentType type
    String externalUrl
    String copyrightStatus
    String copyrightNote
    String notes
    DocRepoUser uploader

    Date dateCreated
    Date lastUpdated

    static mapping = {
        table name: 'docrepo_document'
        version false
        notes type: 'text'
        copyrightNote type: 'text'
        externalUrl column: 'external_url'
        copyrightStatus column: 'copyright_status'
        copyrightNote column: 'copyright_note'
        dateCreated column: 'date_created'
        lastUpdated column: 'last_updated'
    }

    String toString() { title }
}
