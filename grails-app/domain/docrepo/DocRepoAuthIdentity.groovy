package docrepo

/**
 * One row per way an account can be signed into. provider 'local' today
 * (providerUserId = username); 'google' or other providers land here later
 * without any schema change (providerUserId = the provider's stable id, e.g.
 * Google's `sub`). A single account may hold identities from several providers.
 */
class DocRepoAuthIdentity {

    static constraints = {
        provider blank: false, maxSize: 32, unique: 'providerUserId'
        providerUserId blank: false, maxSize: 191
        email nullable: true, email: true, maxSize: 200
        user nullable: false
    }

    String provider
    String providerUserId
    String email
    DocRepoUser user

    Date dateCreated

    static mapping = {
        table name: 'docrepo_auth_identity'
        version false
        providerUserId column: 'provider_user_id'
        dateCreated column: 'date_created'
    }

    String toString() { "${provider}:${providerUserId}" }
}
