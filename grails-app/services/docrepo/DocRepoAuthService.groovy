package docrepo

import grails.util.Environment
import grails.web.mapping.LinkGenerator

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * The auth seam. Two contract points keep sign-in extensible:
 *
 *  1. resolveOrCreateUser(provider, providerUserId, email, displayName[, password])
 *     — the ONLY way a successful authentication becomes an account. The disabled
 *     register endpoint calls it with provider='local'; a future Google OAuth
 *     callback calls it with provider='google' and the verified `sub`. Same
 *     approval pipeline either way (pending -> admin PATCH /admin/users/:id).
 *  2. issueToken(user) — whatever the provider, the outcome is a 30-day bearer
 *     token; the rest of the API is provider-agnostic.
 *
 * Password hashing is PBKDF2WithHmacSHA256 in the same wire format the tsalagi
 * module uses ("pbkdf2$iterations$salt$hash").
 */
class DocRepoAuthService {

    def grailsApplication
    LinkGenerator grailsLinkGenerator

    private static final int PBKDF2_ITERATIONS = 100_000
    private static final SecureRandom SECURE_RANDOM = new SecureRandom()
    private static final long TOKEN_TTL_MS = 30L * 24L * 60L * 60L * 1000L

    private static final List<String> DEFAULT_TYPES = [
        'Dissertation', 'Wordlist', 'Learning Article', 'Grammar/Reference',
        'Presentation', 'Corpus/Data', 'Other'
    ]

    // ------------------------------------------------------------ passwords

    String hashPassword(String password) {
        byte[] salt = new byte[16]
        SECURE_RANDOM.nextBytes(salt)
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, 256)
        byte[] derived = SecretKeyFactory.getInstance('PBKDF2WithHmacSHA256').generateSecret(spec).encoded
        return "pbkdf2\$${PBKDF2_ITERATIONS}\$${bytesToHex(salt)}\$${bytesToHex(derived)}"
    }

    boolean verifyPassword(String password, String storedHash) {
        if (!storedHash || !storedHash.startsWith('pbkdf2$')) return false
        String[] parts = storedHash.split('\\$')
        if (parts.length != 4) return false
        int iterations
        try { iterations = Integer.parseInt(parts[1]) } catch (Exception ignored) { return false }
        byte[] salt = hexToBytes(parts[2])
        byte[] expected = hexToBytes(parts[3])
        if (iterations <= 0 || salt.length == 0 || expected.length == 0) return false

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256)
        byte[] actual = SecretKeyFactory.getInstance('PBKDF2WithHmacSHA256').generateSecret(spec).encoded
        return MessageDigest.isEqual(actual, expected)
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2)
        for (byte b : bytes) sb.append(String.format('%02x', b & 0xff))
        return sb.toString()
    }

    private byte[] hexToBytes(String hex) {
        if (!hex || hex.length() % 2 != 0) return new byte[0]
        int len = hex.length()
        byte[] out = new byte[len / 2]
        for (int i = 0; i < len; i += 2) out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) | Character.digit(hex.charAt(i + 1), 16))
        return out
    }

    // --------------------------------------------------------------- tokens

    String issueToken(DocRepoUser user) {
        String token = 'dcr_' + randomHex(32)
        new DocRepoAuthToken(token: token, user: user, expiresAt: new Date(System.currentTimeMillis() + TOKEN_TTL_MS)).save(flush: true, failOnError: true)
        return token
    }

    /** Null unless the token is live AND the account is active (pending/disabled users hold no usable tokens). */
    DocRepoUser resolveUserByToken(String token) {
        if (!token) return null
        DocRepoAuthToken t = DocRepoAuthToken.findByToken(token)
        if (!t) return null
        if (t.expiresAt.before(new Date())) {
            t.delete(flush: true)
            return null
        }
        DocRepoUser user = t.user
        return (user && user.status == 'active') ? user : null
    }

    void revokeToken(String token) {
        if (!token) return
        DocRepoAuthToken t = DocRepoAuthToken.findByToken(token)
        t?.delete(flush: true)
    }

    private String randomHex(int byteCount) {
        byte[] rand = new byte[byteCount]
        SECURE_RANDOM.nextBytes(rand)
        return bytesToHex(rand)
    }

    // ------------------------------------------------------------ sign-in

    /** provider-agnostic login status map: ok | badCredentials | pending | disabled | refused */
    Map authenticateLocal(String identifier, String password) {
        if (!providerEnabled('local')) {
            return [status: 'refused', message: 'Password sign-in is disabled on this deployment.']
        }
        String cleanId = (identifier ?: '').trim().toLowerCase()
        if (!cleanId || !password) return [status: 'badCredentials', message: 'Username and password are required.']

        DocRepoUser user = DocRepoUser.findByUsername(cleanId)
        if (!user || !verifyPassword(password, user.passwordHash)) {
            return [status: 'badCredentials', message: 'Invalid username or password.']
        }
        if (user.status == 'pending') return [status: 'pending', user: user, message: 'Account created — an administrator must approve it before you can sign in.']
        if (user.status == 'disabled') return [status: 'disabled', user: user, message: 'This account has been disabled.']

        touchLogin(user)
        return [status: 'ok', user: user]
    }

    /**
     * The registration seam. Existing identity -> that account (active or
     * pending). New identity -> a pending account when registration is
     * enabled, otherwise a refusal. Both the disabled register endpoint and
     * any future OAuth callback land here.
     */
    Map resolveOrCreateUser(String provider, String providerUserId, String email, String displayName, String password = null) {
        String cleanProvider = (provider ?: 'local').trim().toLowerCase()
        String cleanExternalId = (providerUserId ?: '').trim()
        if (!cleanExternalId) return [status: 'refused', message: 'Provider user id is required.']

        DocRepoAuthIdentity identity = DocRepoAuthIdentity.findByProviderAndProviderUserId(cleanProvider, cleanExternalId)
        if (identity) {
            DocRepoUser user = identity.user
            if (!user || user.status == 'disabled') return [status: 'disabled', user: user, message: 'This account has been disabled.']
            if (user.status == 'pending') return [status: 'pending', user: user, message: 'Account created — an administrator must approve it before you can sign in.']
            touchLogin(user)
            return [status: 'ok', user: user]
        }

        if (!configBool('docrepo.registration.enabled')) {
            return [status: 'refused', message: 'Registration is not open on this deployment — an administrator must create your account.']
        }

        // new account: pending until an admin approves (the approval path is
        // PATCH /docrepo/api/admin/users/:id {status: 'active'})
        String username
        if (cleanProvider == 'local') {
            username = cleanExternalId.toLowerCase()
        } else {
            // future OAuth users arrive without a chosen username — synthesize one
            username = synthesizeUsername(email ?: cleanExternalId, cleanProvider)
        }
        if (cleanProvider == 'local' && !password) {
            return [status: 'refused', message: 'Password is required.']
        }

        DocRepoUser user = new DocRepoUser(
            username: username,
            displayName: (displayName ?: username).trim() ?: username,
            email: (email ?: '').trim() ?: null,
            role: 'certified',
            status: 'pending',
            passwordHash: password ? hashPassword(password) : null
        ).save(flush: true, failOnError: true)
        new DocRepoAuthIdentity(provider: cleanProvider, providerUserId: cleanExternalId, email: user.email, user: user).save(flush: true, failOnError: true)
        return [status: 'pending', user: user, message: 'Account created — an administrator must approve it before you can sign in.']
    }

    private String synthesizeUsername(String seed, String provider) {
        String base = (seed ?: "${provider}-user").toLowerCase()
            .replaceAll(/[^a-z0-9_.-]+/, '-').replaceAll(/^-+|-+$/, '')
        if (!base) base = "${provider}-user"
        String candidate = base.length() > 48 ? base.substring(0, 48) : base
        int suffix = 1
        while (DocRepoUser.findByUsername(candidate)) {
            candidate = "${base}${++suffix}"
        }
        return candidate
    }

    private void touchLogin(DocRepoUser user) {
        user.lastLoginAt = new Date()
        user.save()
    }

    // ----------------------------------------------------- account admin

    /** Admin-created account: active immediately, with its local identity row. */
    DocRepoUser createUser(Map fields, String password, boolean active = true) {
        String username = (fields.username ?: '').trim().toLowerCase()
        if (!username) throw new IllegalArgumentException('Username is required.')
        if (DocRepoUser.findByUsername(username)) throw new IllegalArgumentException("Username \"${username}\" is already registered.")
        DocRepoUser user = new DocRepoUser(
            username: username,
            displayName: (fields.displayName ?: username).trim() ?: username,
            email: (fields.email ?: '').trim() ?: null,
            role: (fields.role == 'admin' ? 'admin' : 'certified'),
            status: active ? 'active' : 'pending',
            passwordHash: password ? hashPassword(password) : null
        ).save(flush: true, failOnError: true)
        new DocRepoAuthIdentity(provider: 'local', providerUserId: username, email: user.email, user: user).save(flush: true, failOnError: true)
        return user
    }

    void changeOwnPassword(DocRepoUser user, String oldPassword, String newPassword) {
        if (!newPassword || newPassword.length() < 6) throw new IllegalArgumentException('New password must be at least 6 characters.')
        if (!user.passwordHash || !verifyPassword(oldPassword ?: '', user.passwordHash)) {
            throw new IllegalArgumentException('Current password is incorrect.')
        }
        user.passwordHash = hashPassword(newPassword)
        user.save(flush: true, failOnError: true)
    }

    void adminSetPassword(DocRepoUser target, String newPassword) {
        if (!newPassword || newPassword.length() < 6) throw new IllegalArgumentException('New password must be at least 6 characters.')
        target.passwordHash = hashPassword(newPassword)
        target.save(flush: true, failOnError: true)
    }

    /** Updates role/status/displayName/email with last-admin protection. Returns the validation error message, or null on success. */
    String adminUpdateUser(DocRepoUser target, Map fields) {
        if (fields.containsKey('displayName')) target.displayName = (fields.displayName ?: target.username).trim() ?: target.username
        if (fields.containsKey('email')) target.email = (fields.email ?: '').trim() ?: null

        if (fields.containsKey('role')) {
            if (!(fields.role in ['admin', 'certified'])) return 'Role must be admin or certified.'
            if (target.role == 'admin' && fields.role != 'admin' && countActiveAdminsExcluding(target) == 0) {
                return 'Cannot demote the last active admin.'
            }
            target.role = fields.role
        }
        if (fields.containsKey('status')) {
            if (!(fields.status in ['active', 'pending', 'disabled'])) return 'Status must be active, pending, or disabled.'
            if (target.status == 'active' && fields.status != 'active' && target.role == 'admin' && countActiveAdminsExcluding(target) == 0) {
                return 'Cannot disable the last active admin.'
            }
            target.status = fields.status
            if (fields.status != 'active') {
                // suspended users lose live sessions immediately
                DocRepoAuthToken.findAllByUser(target)*.delete()
            }
        }
        target.save(flush: true, failOnError: true)
        return null
    }

    /** Returns an error message, or null on success. */
    String adminDeleteUser(DocRepoUser actor, DocRepoUser target) {
        if (target.id == actor.id) return 'You cannot delete your own account.'
        if (target.role == 'admin' && countActiveAdminsExcluding(target) == 0) return 'Cannot delete the last active admin.'
        long uploads = DocRepoDocument.countByUploader(target)
        if (uploads > 0) {
            return "This user has uploaded ${uploads} catalog entr${uploads == 1 ? 'y' : 'ies'} — delete or reassign those entries first (or suspend the account instead)."
        }
        DocRepoAuthToken.findAllByUser(target)*.delete()
        DocRepoAuthIdentity.findAllByUser(target)*.delete()
        // audit rows keep their story; only the actor FK is detached
        DocRepoAuditLog.findAllByActor(target).each { DocRepoAuditLog a ->
            a.actor = null
            a.save(flush: true, failOnError: true)
        }
        target.delete(flush: true)
        return null
    }

    private long countActiveAdminsExcluding(DocRepoUser excluded) {
        long n = DocRepoUser.countByRoleAndStatus('admin', 'active')
        return (excluded.role == 'admin' && excluded.status == 'active') ? n - 1 : n
    }

    // --------------------------------------------------------- bootstrap

    /**
     * Lazy, idempotent: creates the first admin when the table is empty.
     * Password comes from docrepo.bootstrap.adminPassword (env
     * DOCREPO_BOOTSTRAP_ADMIN_PASSWORD in production); development falls
     * back to admin123 so `bootRun` works out of the box.
     */
    void ensureBootstrapAdmin() {
        if (DocRepoUser.count() > 0) return
        String pw = configString('docrepo.bootstrap.adminPassword')
        if (!pw) {
            if (Environment.current == Environment.DEVELOPMENT) {
                pw = 'admin123'
                log.warn('DocRepo bootstrap: no users and no docrepo.bootstrap.adminPassword set — dev default admin/admin123 created (development only).')
            } else {
                log.warn('DocRepo bootstrap: no users exist and docrepo.bootstrap.adminPassword is not set — set DOCREPO_BOOTSTRAP_ADMIN_PASSWORD to create the first admin.')
                return
            }
        }
        createUser([username: 'admin', displayName: 'Administrator', role: 'admin'], pw, true)
        log.info('DocRepo bootstrap: created user "admin"')
    }

    void ensureDefaultTypes() {
        DEFAULT_TYPES.eachWithIndex { String name, int i ->
            if (!DocRepoDocumentType.findByName(name)) {
                new DocRepoDocumentType(name: name, sortOrder: i).save(flush: true, failOnError: true)
            }
        }
    }

    // ------------------------------------------------------------ config

    boolean providerEnabled(String provider) {
        String raw = configString("docrepo.auth.providers.${provider}.enabled")
        if (raw == '') return provider == 'local'   // unset config: local on, external providers (google) off
        return raw == 'true' || raw == '1'
    }

    private boolean configBool(String path) {
        String raw = configString(path)
        if (raw == '') return false
        return raw == 'true' || raw == '1'
    }

    private String configString(String path) {
        try {
            String v = grailsApplication?.config?.getProperty(path, String)
            return (v ?: '').trim()
        } catch (Exception ignored) {
            return ''
        }
    }
}
