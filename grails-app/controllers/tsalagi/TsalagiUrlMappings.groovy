package tsalagi

/**
 * Automatically discovered by Grails alongside your main UrlMappings.groovy.
 * Supports both GSP hosting (/tsalagiTrainer) AND headless CORS REST API consumption
 * (/tsalagiTrainer/api/**) for standalone frontends (e.g. Svelte / SvelteKit).
 */
class TsalagiUrlMappings {

    static mappings = {
        // 0. CORS Preflight OPTIONS handler for headless Svelte / SPA clients
        "/tsalagiTrainer/api/$rest**"(controller: 'tsalagiApi', action: 'corsPreflight', method: 'OPTIONS')

        // 1. Optional GSP Host Page at /tsalagiTrainer
        "/tsalagiTrainer"(controller: 'tsalagiApi', action: 'index')
        "/tsalagiTrainer/"(controller: 'tsalagiApi', action: 'index')

        // 2. Compiled Frontend JS/CSS Assets & Uploaded Media
        "/tsalagiTrainer/assets/$filename**"(controller: 'tsalagiApi', action: 'serveAsset', method: 'GET')
        "/tsalagiTrainer/uploads/$filename**"(controller: 'tsalagiApi', action: 'serveUpload', method: 'GET')

        // 3. Health & Headless Bootstrap
        "/tsalagiTrainer/api/health"(controller: 'tsalagiApi', action: 'health', method: 'GET')
        "/tsalagiTrainer/api/dictionary/lookup"(controller: 'tsalagiApi', action: 'dictionaryLookup', method: 'GET')
        "/tsalagiTrainer/api/dictionary/rows"(controller: 'tsalagiApi', action: 'dictionaryRows', method: 'GET')
        "/tsalagiTrainer/api/syllabary/transliterate"(controller: 'tsalagiApi', action: 'transliterateSyllabary', method: 'GET')
        "/tsalagiTrainer/api/images/search"(controller: 'tsalagiApi', action: 'searchImages', method: 'GET')
        "/tsalagiTrainer/api/bootstrap"(controller: 'tsalagiApi', action: 'bootstrap', method: 'GET')

        // 4. Authentication (Stateless Bearer Token)
        "/tsalagiTrainer/api/auth/register"(controller: 'tsalagiApi', action: 'authRegister', method: 'POST')
        "/tsalagiTrainer/api/auth/login"(controller: 'tsalagiApi', action: 'authLogin', method: 'POST')
        "/tsalagiTrainer/api/auth/logout"(controller: 'tsalagiApi', action: 'authLogout', method: 'POST')

        // 5. User Profile & User Directory
        "/tsalagiTrainer/api/profile"(controller: 'tsalagiApi') {
            action = [GET: 'profile', PATCH: 'updateProfile', PUT: 'updateProfile', POST: 'updateProfile']
        }
        "/tsalagiTrainer/api/users"(controller: 'tsalagiApi', action: 'users', method: 'GET')

        // 6. Admin User Management (Admin / Editor / Member Role Assignment)
        "/tsalagiTrainer/api/admin/users"(controller: 'tsalagiApi') {
            action = [GET: 'adminUsers', POST: 'adminCreateUser']
        }
        "/tsalagiTrainer/api/admin/users/$userId/role"(controller: 'tsalagiApi') {
            action = [PATCH: 'adminUpdateRole', PUT: 'adminUpdateRole', POST: 'adminUpdateRole']
        }
        "/tsalagiTrainer/api/admin/users/$userId"(controller: 'tsalagiApi', action: 'adminDeleteUser', method: 'DELETE')

        // 7. User Groups ("Beginners", "Intermediate Fall 2026", etc.)
        "/tsalagiTrainer/api/groups"(controller: 'tsalagiApi') {
            action = [GET: 'groups', POST: 'createGroup']
        }
        "/tsalagiTrainer/api/groups/$groupId"(controller: 'tsalagiApi') {
            action = [PUT: 'updateGroup', PATCH: 'updateGroup', DELETE: 'deleteGroup']
        }

        // 8. Per-User Lessons & Lesson Visibility / Group+User Sharing
        "/tsalagiTrainer/api/lessons"(controller: 'tsalagiApi', action: 'upsertLesson', method: 'POST')
        "/tsalagiTrainer/api/lessons/$lessonId/share"(controller: 'tsalagiApi') {
            action = [PATCH: 'shareLesson', PUT: 'shareLesson', POST: 'shareLesson']
        }
        "/tsalagiTrainer/api/lessons/$lessonId"(controller: 'tsalagiApi', action: 'deleteLesson', method: 'DELETE')

        // 8b. Sections: Standalone Reusable Lesson Building Blocks
        "/tsalagiTrainer/api/sections"(controller: 'tsalagiApi') {
            action = [GET: 'sections', POST: 'sectionUpsert', PUT: 'sectionUpsert']
        }
        "/tsalagiTrainer/api/sections/$sectionId"(controller: 'tsalagiApi', action: 'sectionDelete', method: 'DELETE')

        // 8c. Learning Units (Chapters)
        "/tsalagiTrainer/api/units"(controller: 'tsalagiApi', action: 'unitUpsert', method: 'POST')
        "/tsalagiTrainer/api/units/$unitId"(controller: 'tsalagiApi', action: 'unitDelete', method: 'DELETE')

        // 9. Learner Progress, Awards, Preferences & Mistakes
        "/tsalagiTrainer/api/progress"(controller: 'tsalagiApi', action: 'progress', method: 'GET')
        "/tsalagiTrainer/api/progress/preferences"(controller: 'tsalagiApi') {
            action = [PATCH: 'updatePreferences', POST: 'updatePreferences']
        }
        "/tsalagiTrainer/api/progress/activity"(controller: 'tsalagiApi', action: 'recordActivity', method: 'POST')
        "/tsalagiTrainer/api/mistakes"(controller: 'tsalagiApi', action: 'mistakes', method: 'POST')
        "/tsalagiTrainer/api/mistakes/$itemKey"(controller: 'tsalagiApi', action: 'resolveMistake', method: 'DELETE')

        // 10. Media Upload & Shared Curriculum CRUD
        "/tsalagiTrainer/api/media/upload"(controller: 'tsalagiApi', action: 'uploadMedia', method: 'POST')
        "/tsalagiTrainer/api/curriculum/words"(controller: 'tsalagiApi', action: 'saveWords', method: 'PUT')
        "/tsalagiTrainer/api/curriculum/conversations"(controller: 'tsalagiApi', action: 'saveConversations', method: 'PUT')
        "/tsalagiTrainer/api/curriculum/lessons"(controller: 'tsalagiApi', action: 'saveLessons', method: 'PUT')
        "/tsalagiTrainer/api/curriculum/export"(controller: 'tsalagiApi', action: 'exportCurriculum', method: 'GET')
        "/tsalagiTrainer/api/curriculum/import"(controller: 'tsalagiApi', action: 'importCurriculum', method: 'POST')
        "/tsalagiTrainer/api/curriculum/reset"(controller: 'tsalagiApi', action: 'resetCurriculum', method: 'POST')
    }
}
