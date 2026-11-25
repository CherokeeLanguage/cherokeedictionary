package docrepo

import grails.converters.JSON
import groovy.json.JsonSlurper
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder

/**
 * DocRepo — certified-upload document repository.
 * Host page at /docrepo, JSON API at /docrepo/api/**. The frontend under
 * src/main/resources/public/docrepo/ is a plain no-build static app that
 * talks to this API — a future Svelte/React build drops in over it without
 * any API change.
 */
class DocRepoController {

    static responseFormats = ['json', 'html']

    DocRepoAuthService docRepoAuthService
    DocRepoService docRepoService

    private static final Map<String, String> MIME_BY_EXT = [
        html: 'text/html; charset=utf-8', js: 'text/javascript; charset=utf-8',
        css: 'text/css; charset=utf-8', json: 'application/json',
        png: 'image/png', jpg: 'image/jpeg', jpeg: 'image/jpeg', gif: 'image/gif',
        svg: 'image/svg+xml', ico: 'image/x-icon', woff: 'font/woff', woff2: 'font/woff2',
        ttf: 'font/ttf', txt: 'text/plain'
    ]

    /* ================================================================
     * HOST PAGE + STATIC ASSETS (the no-build frontend)
     * ================================================================ */

    def index() {
        docRepoAuthService.ensureBootstrapAdmin()
        docRepoAuthService.ensureDefaultTypes()
        File disk = new File('src/main/resources/public/docrepo/index.html')
        if (disk.isFile()) {
            renderHostFile(disk, 'text/html; charset=utf-8')
            return
        }
        InputStream stream = classpathAsset('index.html')
        if (stream == null) {
            render( status: 404, text: 'DocRepo frontend is missing (public/docrepo/index.html not found).')
            return
        }
        response.contentType = 'text/html; charset=utf-8'
        response.outputStream << stream
        response.outputStream.flush()
    }

    def serveAsset() {
        String uri = request.requestURI ?: ''
        String marker = '/docrepo/assets/'
        String relPath = uri.contains(marker) ? uri.substring(uri.indexOf(marker) + marker.length()) : (params.filename ?: '')
        relPath = URLDecoder.decode(relPath, 'UTF-8').replace('\\', '/')
        if (!relPath || relPath.contains('..')) {
            render(status: 404, text: 'Not found')
            return
        }
        File disk = new File("src/main/resources/public/docrepo/assets/${relPath}")
        if (disk.isFile()) {
            renderHostFile(disk, guessMime(relPath))
            return
        }
        InputStream stream = classpathAsset("assets/${relPath}")
        if (stream == null) {
            render(status: 404, text: 'Not found')
            return
        }
        response.contentType = guessMime(relPath)
        response.outputStream << stream
        response.outputStream.flush()
    }

    private InputStream classpathAsset(String relPath) {
        ClassLoader cl = Thread.currentThread().contextClassLoader ?: this.class.classLoader
        return cl.getResourceAsStream("public/docrepo/${relPath}")
    }

    private void renderHostFile(File file, String contentType) {
        response.contentType = contentType
        response.contentLengthLong = file.length()
        response.outputStream << file.newInputStream()
        response.outputStream.flush()
    }

    private String guessMime(String name) {
        String ext = name.contains('.') ? name.tokenize('.').last().toLowerCase() : ''
        return MIME_BY_EXT[ext] ?: 'application/octet-stream'
    }

    /* ================================================================
     * API PLUMBING
     * ================================================================ */

    def corsPreflight() {
        applyCorsHeaders()
        response.status = 204
        render ''
    }

    def health() {
        applyCorsHeaders()
        render([status: 'ok', section: 'docrepo', timestamp: new Date().toString()] as JSON)
    }

    private void applyCorsHeaders() {
        String origin = request.getHeader('Origin')
        response.setHeader('Access-Control-Allow-Origin', origin ?: '*')
        response.setHeader('Vary', 'Origin')
        response.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, PATCH, DELETE, OPTIONS')
        response.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization')
        response.setHeader('Access-Control-Max-Age', '86400')
    }

    private List readAuthHeaders() {
        String authHeader = request.getHeader('Authorization') ?: ''
        def matcher = (authHeader =~ /(?i)^Bearer\s+(.+)$/)
        String token = matcher.matches() ? matcher.group(1).trim() : null
        return [token ?: null]
    }

    private DocRepoUser requireAuth() {
        String token = readAuthHeaders()[0]
        DocRepoUser user = docRepoAuthService.resolveUserByToken(token)
        if (!user) {
            renderError(401, 'Authentication required. Please sign in.')
            return null
        }
        return user
    }

    private DocRepoUser requireAdmin() {
        DocRepoUser user = requireAuth()
        if (!user) return null
        if (user.role != 'admin') {
            renderError(403, 'Admin privileges are required.')
            return null
        }
        return user
    }

    private Map readJsonBody() {
        try {
            def parsed = request.JSON
            return (parsed instanceof Map) ? (Map) parsed : [:]
        } catch (Exception ignored) {
            return [:]
        }
    }

    private void renderError(int statusCode, String msg) {
        response.status = statusCode
        render([error: msg ?: 'Unexpected server error'] as JSON)
    }

    private Map userJson(DocRepoUser u) {
        return [
            id         : u.id,
            username   : u.username,
            displayName: u.displayName,
            email      : u.email,
            role       : u.role,
            status     : u.status,
            createdAt  : fmtDate(u.dateCreated),
            lastLoginAt: fmtDate(u.lastLoginAt)
        ]
    }

    private String fmtDate(Date d) {
        return d ? new java.text.SimpleDateFormat('yyyy-MM-dd HH:mm').format(d) : null
    }

    /* ================================================================
     * AUTH
     * ================================================================ */

    def authLogin() {
        applyCorsHeaders()
        try {
            docRepoAuthService.ensureBootstrapAdmin()
            docRepoAuthService.ensureDefaultTypes()
            Map body = readJsonBody()
            Map result = docRepoAuthService.authenticateLocal(
                (body.username ?: body.identifier)?.toString(),
                body.password?.toString())

            if (result.status == 'ok') {
                render([token: docRepoAuthService.issueToken(result.user), user: userJson(result.user)] as JSON)
                return
            }
            renderError(result.status == 'badCredentials' ? 401 : 403, result.message)
        } catch (Exception ex) {
            log.error('POST /docrepo/api/auth/login failed', ex)
            renderError(500, ex.message)
        }
    }

    def authLogout() {
        applyCorsHeaders()
        String token = readAuthHeaders()[0]
        docRepoAuthService.revokeToken(token)
        render([ok: true] as JSON)
    }

    def authRegister() {
        applyCorsHeaders()
        try {
            Map body = readJsonBody()
            String username = (body.username?.toString() ?: '').trim().toLowerCase()
            String password = body.password?.toString() ?: ''
            if (username.length() < 3 || !(username ==~ /[a-z0-9_.-]+/)) {
                renderError(400, 'Username must be at least 3 characters (letters, numbers, . _ - only).')
                return
            }
            if (password.length() < 6) {
                renderError(400, 'Password must be at least 6 characters.')
                return
            }
            if (DocRepoUser.findByUsername(username)) {
                renderError(400, "Username \"${username}\" is already taken.")
                return
            }
            Map result = docRepoAuthService.resolveOrCreateUser(
                'local', username, body.email?.toString(), body.displayName?.toString(), password)
            if (result.status == 'pending') {
                response.status = 201
                render([ok: true, status: 'pending', message: result.message] as JSON)
                return
            }
            renderError(403, result.message)
        } catch (Exception ex) {
            log.error('POST /docrepo/api/auth/register failed', ex)
            renderError(400, ex.message)
        }
    }

    def me() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        render([user: userJson(user)] as JSON)
    }

    def mePassword() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        Map body = readJsonBody()
        try {
            docRepoAuthService.changeOwnPassword(user, body.oldPassword?.toString(), body.newPassword?.toString())
            render([ok: true] as JSON)
        } catch (IllegalArgumentException ex) {
            renderError(400, ex.message)
        }
    }

    /* ================================================================
     * DOCUMENTS
     * ================================================================ */

    def listDocuments() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        docRepoService.searchDocuments(params).with { render(it as JSON) }
    }

    def showDocument() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        DocRepoDocument doc = DocRepoDocument.get(params.long('id'))
        if (!doc) {
            renderError(404, 'Document not found.')
            return
        }
        render(docRepoService.documentJson(doc) as JSON)
    }

    def createMetadataDocument() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        try {
            Map result = docRepoService.createMetadataDocument(user, readJsonBody())
            response.status = 201
            render(result as JSON)
        } catch (IllegalArgumentException ex) {
            renderError(400, ex.message)
        }
    }

    def uploadDocument() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        try {
            MultipartFile file = request.getFile('file')
            Map meta = [
                title          : params.title,
                authors        : params.authors,
                type           : params.type,
                tags           : params.tags,
                copyrightStatus: params.copyrightStatus,
                copyrightNote  : params.copyrightNote,
                externalUrl    : params.externalUrl,
                notes          : params.notes
            ]
            Map result = docRepoService.uploadDocument(
                user, file, meta,
                params.resolution,
                params.long('targetDocumentId'),
                params.renditionNote)

            if (result.error) {
                renderError(result.code ?: 400, result.error)
                return
            }
            if (result.conflict) {
                response.status = 409
                render([conflict: true, matches: result.matches] as JSON)
                return
            }
            if (result.skipped) {
                render(result as JSON)
                return
            }
            response.status = 201
            render(result as JSON)
        } catch (IllegalArgumentException ex) {
            renderError(400, ex.message)
        } catch (Exception ex) {
            log.error('POST /docrepo/api/documents/upload failed', ex)
            renderError(500, ex.message)
        }
    }

    def updateDocument() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        DocRepoDocument doc = DocRepoDocument.get(params.long('id'))
        if (!doc) {
            renderError(404, 'Document not found.')
            return
        }
        if (user.role != 'admin' && doc.uploader.id != user.id) {
            renderError(403, 'Only the uploader or an admin can edit this entry.')
            return
        }
        Map result = docRepoService.updateMetadata(user, doc.id, readJsonBody())
        if (result.error) {
            renderError(result.code ?: 400, result.error)
            return
        }
        render(result as JSON)
    }

    def deleteDocument() {
        applyCorsHeaders()
        DocRepoUser admin = requireAdmin()
        if (!admin) return
        Map result = docRepoService.deleteDocument(admin, params.long('id'))
        if (result.error) {
            renderError(result.code ?: 400, result.error)
            return
        }
        render(result as JSON)
    }

    def downloadDocument() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        streamFile(params.long('id'), null)
    }

    def downloadFile() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        streamFile(params.long('id'), params.long('fileId'))
    }

    private void streamFile(Long documentId, Long fileId) {
        Map resolved = docRepoService.resolveDownload(documentId, fileId, fileId == null)
        if (resolved.error) {
            renderError(resolved.code ?: 404, resolved.error)
            return
        }
        DocRepoFile f = (DocRepoFile) resolved.file
        File onDisk = (File) resolved.onDisk
        String ascii = f.originalFilename.replaceAll(/[^\x20-\x7e]/, '_').replaceAll(/["\\]/, '_')
        String encoded = URLEncoder.encode(f.originalFilename, 'UTF-8').replaceAll('\\+', '%20')
        String disposition = docRepoService.mayStreamInline(f.mimeType) ? 'inline' : 'attachment'
        response.contentType = f.mimeType ?: 'application/octet-stream'
        response.contentLengthLong = onDisk.length()
        response.setHeader('Content-Disposition', "${disposition}; filename=\"${ascii}\"; filename*=UTF-8''${encoded}")
        response.setHeader('X-Content-Type-Options', 'nosniff')
        response.outputStream << onDisk.newInputStream()
        response.outputStream.flush()
    }

    def listTags() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        render([tags: docRepoService.tagNames()] as JSON)
    }

    def listTypes() {
        applyCorsHeaders()
        DocRepoUser user = requireAuth()
        if (!user) return
        render([types: docRepoService.typeNames()] as JSON)
    }

    /* ================================================================
     * ADMIN — USER MANAGEMENT (also the future registration-approval path)
     * ================================================================ */

    def adminUsers() {
        applyCorsHeaders()
        DocRepoUser admin = requireAdmin()
        if (!admin) return
        render([users: DocRepoUser.list(sort: 'username').collect { userJson(it) }] as JSON)
    }

    def adminCreateUser() {
        applyCorsHeaders()
        DocRepoUser admin = requireAdmin()
        if (!admin) return
        Map body = readJsonBody()
        try {
            DocRepoUser created = docRepoAuthService.createUser(
                [username: body.username, displayName: body.displayName, email: body.email, role: body.role],
                body.password?.toString(), true)
            response.status = 201
            render([user: userJson(created)] as JSON)
        } catch (IllegalArgumentException ex) {
            renderError(400, ex.message)
        }
    }

    def adminUpdateUser() {
        applyCorsHeaders()
        DocRepoUser admin = requireAdmin()
        if (!admin) return
        DocRepoUser target = DocRepoUser.get(params.long('userId'))
        if (!target) {
            renderError(404, 'User not found.')
            return
        }
        Map body = readJsonBody()
        String error = docRepoAuthService.adminUpdateUser(target, body)
        if (error) {
            renderError(400, error)
            return
        }
        render([user: userJson(target)] as JSON)
    }

    def adminSetPassword() {
        applyCorsHeaders()
        DocRepoUser admin = requireAdmin()
        if (!admin) return
        DocRepoUser target = DocRepoUser.get(params.long('userId'))
        if (!target) {
            renderError(404, 'User not found.')
            return
        }
        try {
            docRepoAuthService.adminSetPassword(target, readJsonBody().newPassword?.toString())
            render([ok: true] as JSON)
        } catch (IllegalArgumentException ex) {
            renderError(400, ex.message)
        }
    }

    def adminDeleteUser() {
        applyCorsHeaders()
        DocRepoUser admin = requireAdmin()
        if (!admin) return
        DocRepoUser target = DocRepoUser.get(params.long('userId'))
        if (!target) {
            renderError(404, 'User not found.')
            return
        }
        String error = docRepoAuthService.adminDeleteUser(admin, target)
        if (error) {
            renderError(400, error)
            return
        }
        render([ok: true] as JSON)
    }
}
