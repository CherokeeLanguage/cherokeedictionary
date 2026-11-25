package docrepo

/**
 * DocRepo routing — mirrors the tsalagi module shape:
 * host page at /docrepo, static frontend assets under /docrepo/assets/**,
 * and the JSON API at /docrepo/api/**. Auto-discovered by Grails alongside
 * the main UrlMappings. Specific paths are declared before the $id routes
 * they could be mistaken for (upload before $id, password before $userId).
 */
class DocRepoUrlMappings {

    static mappings = {
        // host page + static frontend assets
        "/docrepo"(controller: 'docRepo', action: 'index')
        "/docrepo/"(controller: 'docRepo', action: 'index')
        "/docrepo/assets/$filename**"(controller: 'docRepo', action: 'serveAsset', method: 'GET')

        // CORS preflight for headless SPA clients
        "/docrepo/api/$rest**"(controller: 'docRepo', action: 'corsPreflight', method: 'OPTIONS')

        // health
        "/docrepo/api/health"(controller: 'docRepo', action: 'health', method: 'GET')

        // auth
        "/docrepo/api/auth/login"(controller: 'docRepo', action: 'authLogin', method: 'POST')
        "/docrepo/api/auth/register"(controller: 'docRepo', action: 'authRegister', method: 'POST')
        "/docrepo/api/auth/logout"(controller: 'docRepo', action: 'authLogout', method: 'POST')

        // self
        "/docrepo/api/me"(controller: 'docRepo', action: 'me', method: 'GET')
        "/docrepo/api/me/password"(controller: 'docRepo') {
            action = [PUT: 'mePassword', PATCH: 'mePassword', POST: 'mePassword']
        }

        // documents — upload declared before /documents/$id
        "/docrepo/api/documents/upload"(controller: 'docRepo', action: 'uploadDocument', method: 'POST')
        "/docrepo/api/documents"(controller: 'docRepo') {
            action = [GET: 'listDocuments', POST: 'createMetadataDocument']
        }
        "/docrepo/api/documents/$id/download"(controller: 'docRepo', action: 'downloadDocument', method: 'GET')
        "/docrepo/api/documents/$id/file/$fileId"(controller: 'docRepo', action: 'downloadFile', method: 'GET')
        "/docrepo/api/documents/$id"(controller: 'docRepo') {
            action = [GET: 'showDocument', PUT: 'updateDocument', PATCH: 'updateDocument', DELETE: 'deleteDocument']
        }

        // tags & types
        "/docrepo/api/tags"(controller: 'docRepo', action: 'listTags', method: 'GET')
        "/docrepo/api/types"(controller: 'docRepo', action: 'listTypes', method: 'GET')

        // admin — password route declared before /admin/users/$userId
        "/docrepo/api/admin/users"(controller: 'docRepo') {
            action = [GET: 'adminUsers', POST: 'adminCreateUser']
        }
        "/docrepo/api/admin/users/$userId/password"(controller: 'docRepo', action: 'adminSetPassword') {
            action = [PUT: 'adminSetPassword', PATCH: 'adminSetPassword']
        }
        "/docrepo/api/admin/users/$userId"(controller: 'docRepo') {
            action = [PATCH: 'adminUpdateUser', PUT: 'adminUpdateUser', DELETE: 'adminDeleteUser']
        }
    }
}
