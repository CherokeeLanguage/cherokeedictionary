package docrepo

/**
 * The account. Deliberately separate from how the account signs in —
 * see [[docrepo.DocRepoAuthIdentity]]. passwordHash stays null for users
 * who only ever sign in through an external identity (future OAuth seam).
 */
class DocRepoUser {

    static constraints = {
        username blank: false, unique: true, maxSize: 64, matches: /^[a-z0-9_.-]+$/
        displayName blank: false, maxSize: 120
        email nullable: true, email: true, maxSize: 200
        passwordHash nullable: true, maxSize: 255
        role inList: ['admin', 'certified']
        status inList: ['active', 'pending', 'disabled']
        lastLoginAt nullable: true
    }

    String username
    String displayName
    String email
    String passwordHash
    String role = 'certified'
    String status = 'active'

    Date lastLoginAt
    Date dateCreated
    Date lastUpdated

    static mapping = {
        table name: 'docrepo_user'
        version false
        lastLoginAt column: 'last_login_at'
        passwordHash column: 'password_hash'
        displayName column: 'display_name'
        dateCreated column: 'date_created'
        lastUpdated column: 'last_updated'
    }

    String toString() { username }
}
