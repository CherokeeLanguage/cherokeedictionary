package docrepo

/**
 * Bearer token, 30-day TTL, issued identically no matter how the user
 * authenticated (password today, OAuth callback later) — the API only
 * ever consumes tokens.
 */
class DocRepoAuthToken {

    static constraints = {
        token blank: false, unique: true, maxSize: 128
        user nullable: false
        expiresAt nullable: false
    }

    String token
    DocRepoUser user
    Date expiresAt

    Date dateCreated

    static mapping = {
        table name: 'docrepo_auth_token'
        version false
        token index: 'idx_docrepo_auth_token_token'
        expiresAt column: 'expires_at'
        dateCreated column: 'date_created'
    }
}
