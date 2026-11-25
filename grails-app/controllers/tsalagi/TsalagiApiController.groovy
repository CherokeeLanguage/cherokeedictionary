package tsalagi

import grails.converters.JSON
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.sql.DataSource
import java.nio.file.Files
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * TsalagiApiController (v3.0 — Headless REST API + GSP Host)
 *
 * - Serves optional GSP view at `/tsalagiTrainer`
 * - Exposes complete stateless Bearer-token JSON REST API at `/tsalagiTrainer/api/**`
 *   ready to be consumed directly by a standalone Svelte / SvelteKit SPA without GSP!
 * - Uses Grails' configured MySQL `dataSource` (`tsalagi_*` tables, auto-migrated on boot).
 */
class TsalagiApiController {

    DataSource dataSource

    // Optional: automatically injected if grails-spring-security-core is installed
    def springSecurityService

    static responseFormats = ['json', 'html']

    private static final AtomicBoolean SCHEMA_INITIALIZED = new AtomicBoolean(false)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom()
    private static final int PBKDF2_ITERATIONS = 100_000
    private static final long TOKEN_TTL_MS = 30L * 24L * 60L * 60L * 1000L

    private static final String T_USERS               = 'tsalagi_users'
    private static final String T_AUTH_TOKENS         = 'tsalagi_auth_tokens'
    private static final String T_USER_STATS          = 'tsalagi_user_stats'
    private static final String T_USER_AWARDS         = 'tsalagi_user_awards'
    private static final String T_DAILY_XP            = 'tsalagi_daily_xp_log'
    private static final String T_MISTAKES            = 'tsalagi_user_mistakes'
    private static final String T_COMPLETED_LESSONS   = 'tsalagi_user_completed_lessons'
    private static final String T_MASTERED_WORDS      = 'tsalagi_user_mastered_words'
    private static final String T_GROUPS              = 'tsalagi_user_groups'
    private static final String T_GROUP_MEMBERS       = 'tsalagi_user_group_members'
    private static final String T_WORDS               = 'tsalagi_words'
    private static final String T_CONVERSATIONS       = 'tsalagi_conversations'
    private static final String T_LESSONS             = 'tsalagi_lessons'
    private static final String T_LESSON_USER_SHARES  = 'tsalagi_lesson_user_shares'
    private static final String T_LESSON_GROUP_SHARES = 'tsalagi_lesson_group_shares'
    private static final String T_SECTIONS            = 'tsalagi_sections'
    private static final String T_LESSON_SECTIONS     = 'tsalagi_lesson_sections'
    private static final String T_UNITS               = 'tsalagi_units'

    // Syllabary -> phonetic transliteration parity with src/utils/SyllabaryUtil.ts
    private static final Map<String, String> SYLLABARY_TO_PHONETIC = [
        'Ꭰ': 'a',
        'Ꭱ': 'e',
        'Ꭲ': 'i',
        'Ꭳ': 'o',
        'Ꭴ': 'u',
        'Ꭵ': 'v',
        'Ꭶ': 'ga',
        'Ꭷ': 'ka',
        'Ꭸ': 'ge',
        'Ꭹ': 'gi',
        'Ꭺ': 'go',
        'Ꭻ': 'gu',
        'Ꭼ': 'gv',
        'Ꭽ': 'ha',
        'Ꭾ': 'he',
        'Ꭿ': 'hi',
        'Ꮀ': 'ho',
        'Ꮁ': 'hu',
        'Ꮂ': 'hv',
        'Ꮃ': 'la',
        'Ꮄ': 'le',
        'Ꮅ': 'li',
        'Ꮆ': 'lo',
        'Ꮇ': 'lu',
        'Ꮈ': 'lv',
        'Ꮉ': 'ma',
        'Ꮊ': 'me',
        'Ꮋ': 'mi',
        'Ꮌ': 'mo',
        'Ꮍ': 'mu',
        'Ꮎ': 'na',
        'Ꮏ': 'hna',
        'Ꮐ': 'nah',
        'Ꮑ': 'ne',
        'Ꮒ': 'ni',
        'Ꮓ': 'no',
        'Ꮔ': 'nu',
        'Ꮕ': 'nv',
        'Ꮖ': 'qua',
        'Ꮗ': 'que',
        'Ꮘ': 'qui',
        'Ꮙ': 'quo',
        'Ꮚ': 'quu',
        'Ꮛ': 'quv',
        'Ꮜ': 'sa',
        'Ꮝ': 's',
        'Ꮞ': 'se',
        'Ꮟ': 'si',
        'Ꮠ': 'so',
        'Ꮡ': 'su',
        'Ꮢ': 'sv',
        'Ꮣ': 'da',
        'Ꮤ': 'ta',
        'Ꮥ': 'de',
        'Ꮦ': 'te',
        'Ꮧ': 'di',
        'Ꮨ': 'ti',
        'Ꮩ': 'do',
        'Ꮪ': 'du',
        'Ꮫ': 'dv',
        'Ꮬ': 'dla',
        'Ꮭ': 'tla',
        'Ꮮ': 'tle',
        'Ꮯ': 'tli',
        'Ꮰ': 'tlo',
        'Ꮱ': 'tlu',
        'Ꮲ': 'tlv',
        'Ꮳ': 'tsa',
        'Ꮴ': 'tse',
        'Ꮵ': 'tsi',
        'Ꮶ': 'tso',
        'Ꮷ': 'tsu',
        'Ꮸ': 'tsv',
        'Ꮹ': 'wa',
        'Ꮺ': 'we',
        'Ꮻ': 'wi',
        'Ꮼ': 'wo',
        'Ꮽ': 'wu',
        'Ꮾ': 'wv',
        'Ꮿ': 'ya',
        'Ᏸ': 'ye',
        'Ᏹ': 'yi',
        'Ᏺ': 'yo',
        'Ᏻ': 'yu',
        'Ᏼ': 'yv',
        ' ': ' '
    ]

    private String parseSyllabaryText(String text) {
        StringBuilder builder = new StringBuilder()
        for (int i = 0; i < text.length(); i++) {
            String value = SYLLABARY_TO_PHONETIC.get(text.substring(i, i + 1))
            if (value != null) builder.append(value)
        }
        return builder.toString()
    }

    private static final List<Map> AWARD_DEFINITIONS = [
        [
            code         : 'first_step',
            title        : 'First Step on the Trail',
            cherokeeTitle: 'ᎢᎬᏱ ᎠᎳᏍᎦᎸ',
            description  : 'Complete your very first Cherokee lesson.',
            category     : 'journey',
            gemsBonus    : 15
        ],
        [
            code         : 'five_lessons',
            title        : 'Pathway Explorer',
            cherokeeTitle: 'ᏅᏃᎯ ᎠᏕᎶᏆᏍᏙᏗ',
            description  : 'Complete 5 lessons across the Cherokee learning journey.',
            category     : 'journey',
            gemsBonus    : 25
        ],
        [
            code         : 'sharp_archer',
            title        : 'Unerring Accuracy',
            cherokeeTitle: 'ᏚᏳᎪᏛ Ꭰាញ់',
            description  : 'Finish a lesson with a 100% accuracy score.',
            category     : 'accuracy',
            gemsBonus    : 20
        ],
        [
            code         : 'daily_keeper',
            title        : 'Council Fire Keeper',
            cherokeeTitle: 'ᎠᏥᎸ ᎠᎦᏎᏍᏗᏍᎩ',
            description  : 'Reach your Daily XP Goal for the first time.',
            category     : 'streak',
            gemsBonus    : 15
        ],
        [
            code         : 'sacred_seven_streak',
            title        : 'Sacred Seven Streak',
            cherokeeTitle: 'ᎦᎵᏉᎩ ᎢᎦ',
            description  : 'Maintain a 7-day learning streak.',
            category     : 'streak',
            gemsBonus    : 50
        ],
        [
            code         : 'syllabary_scholar',
            title        : 'Sequoyah’s Apprentice',
            cherokeeTitle: 'ᏎᏉᏯ ᎠᏕᎶᏆᏍᎩ',
            description  : 'Master 7 Cherokee vocabulary words in Flashcards.',
            category     : 'vocabulary',
            gemsBonus    : 25
        ],
        [
            code         : 'word_weaver',
            title        : 'Fluent Word Weaver',
            cherokeeTitle: 'ᎧᏁᏨ ᎠᏍᏆᏂᎪᏗᏍᎩ',
            description  : 'Master 20 Cherokee vocabulary words.',
            category     : 'vocabulary',
            gemsBonus    : 40
        ],
        [
            code         : 'lesson_author',
            title        : 'Knowledge Sharer',
            cherokeeTitle: 'ᏗᏕᏲᎲᏍᎩ',
            description  : 'Create your own custom Cherokee lesson for the community or a study group.',
            category     : 'community',
            gemsBonus    : 30
        ],
        [
            code         : 'group_founder',
            title        : 'Clan Circle Organizer',
            cherokeeTitle: 'ᏓᏠᎯᏍᏗ ᎠᎴᏂᏍᎩ',
            description  : 'Create a learning group to organize and share lessons with fellow learners.',
            category     : 'community',
            gemsBonus    : 25
        ]
    ]

    /* ========================================================================
     * 0. HEADLESS CORS PREFLIGHT & HEADERS (FOR STANDALONE SVELTE SPA)
     * ======================================================================== */
    private void applyCorsHeaders() {
        String origin = request.getHeader('Origin')
        response.setHeader('Access-Control-Allow-Origin', origin ?: '*')
        response.setHeader('Vary', 'Origin')
        response.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, PATCH, DELETE, OPTIONS')
        response.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, X-CSRF-TOKEN, x-user-timezone, x-user-id')
        response.setHeader('Access-Control-Max-Age', '86400')
    }

    def corsPreflight() {
        applyCorsHeaders()
        response.status = 204
        render ''
    }

    /* ========================================================================
     * 1. OPTIONAL GSP PAGE ENTRYPOINT (GET /tsalagiTrainer)
     * ======================================================================== */
    def index() {
        ensureSchemaInitialized()
        render(view: '/tsalagiApi/index', model: [
            pageTitle: 'ᏣᎳᎩ Tsalagi — Cherokee Language & Curriculum Studio'
        ])
    }

    /* ========================================================================
     * 2. HEALTH & HEADLESS APP BOOTSTRAP
     * ======================================================================== */
    def health() {
        applyCorsHeaders()
        ensureSchemaInitialized()
        render([
            status       : 'ok',
            headlessReady: true,
            driver       : 'grails-mysql',
            timestamp    : Instant.now().toString()
        ] as JSON)
    }

    /* ========================================================================
     * 2b. DICTIONARY LOOKUP (proxies the Cherokee Dictionary app's
     *     NewSearchController ajax*Finder endpoints -> Likespreadsheets table)
     *     Config: tsalagi.dictionary.baseUrl (default http://localhost:8080)
     * ======================================================================== */
    private String dictionaryBaseUrl() {
        String baseUrlCfg = grailsApplication?.config?.getProperty('tsalagi.dictionary.baseUrl', String)
        return (baseUrlCfg ?: 'http://localhost:8080').replaceAll('/+$', '')
    }

    private Object fetchJsonFromDictionary(String pathAndQuery) {
        String urlStr = dictionaryBaseUrl() + pathAndQuery
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection()
        conn.setConnectTimeout(4000)
        conn.setReadTimeout(8000)
        conn.setRequestProperty('Accept', 'application/json')
        int statusCode = conn.responseCode
        InputStream stream = statusCode >= 400 ? conn.errorStream : conn.inputStream
        String bodyText = stream ? stream.getText('UTF-8') : ''
        if (statusCode >= 400) {
            throw new RuntimeException("Dictionary service returned ${statusCode}")
        }
        if (!bodyText.trim() || bodyText.trim() == 'null') {
            return []
        }
        return new JsonSlurper().parseText(bodyText)
    }

    /** Simple autocomplete: returns flat list of strings for the given field. */
    def dictionaryLookup() {
        applyCorsHeaders()
        try {
            String term = (params.term ?: '').toString()
            if (!term.trim()) {
                render([results: []] as JSON)
                return
            }
            String field = (params.field ?: 'english').toString().toLowerCase()
            String endpoint
            switch (field) {
                case 'tsalagi':   endpoint = '/newSearch/ajaxTsalagiFinder';   break
                case 'syllabary': endpoint = '/newSearch/ajaxSyllabaryFinder'; break
                default:          endpoint = '/newSearch/ajaxEnglishFinder';  break
            }
            String qs = '?term=' + URLEncoder.encode(term, 'UTF-8')
            Object parsed = fetchJsonFromDictionary(endpoint + qs)
            List results = (parsed instanceof Collection) ? parsed.collect { it?.toString() }.findAll { it } : []
            render([results: results.unique()] as JSON)
        } catch (Exception e) {
            log.warn("Dictionary lookup failed: ${e.message}")
            response.status = 502
            render([error: (e.message ?: 'Dictionary lookup failed'), results: []] as JSON)
        }
    }

    /** Full-row lookup: returns { id, entrya, syllabaryb, definitiond, audioUrl } rows. */
    def dictionaryRows() {
        applyCorsHeaders()
        try {
            String term = (params.term ?: '').toString()
            if (!term.trim()) {
                render([rows: []] as JSON)
                return
            }
            String field = (params.field ?: 'english').toString().toLowerCase()
            String qs = '?field=' + URLEncoder.encode(field, 'UTF-8') + '&term=' + URLEncoder.encode(term, 'UTF-8')
            Object parsed = fetchJsonFromDictionary('/newSearch/ajaxRowFinder' + qs)
            List rows = (parsed instanceof Collection) ? parsed : []
            String baseUrl = dictionaryBaseUrl()
            List mapped = rows.collect { raw ->
                Map m = (raw instanceof Map) ? raw : [:]
                String audioUrl = m.audioUrl ? m.audioUrl.toString() : null
                // dictionary app returns app-relative audio paths; make them absolute
                if (audioUrl && audioUrl.startsWith('/')) {
                    audioUrl = baseUrl + audioUrl
                }
                [
                    id         : m.id,
                    entrya     : m.entrya ? m.entrya.toString() : '',
                    syllabaryb : m.syllabaryb ? m.syllabaryb.toString() : '',
                    definitiond: m.definitiond ? m.definitiond.toString() : '',
                    audioUrl   : audioUrl
                ]
            }
            render([rows: mapped] as JSON)
        } catch (Exception e) {
            log.warn("Dictionary row lookup failed: ${e.message}")
            response.status = 502
            render([error: (e.message ?: 'Dictionary row lookup failed'), rows: []] as JSON)
        }
    }

    /** AJAX transliteration used by the Visual Reader sentence composer. */
    def transliterateSyllabary() {
        applyCorsHeaders()
        String text = (params.text ?: '').toString()
        boolean hasSyllabary = text.find(/[\u13A0-\u13FF]/) != null
        String phonetics = hasSyllabary ? parseSyllabaryText(text) : ''
        render([text: text, phonetics: phonetics, syllabary: hasSyllabary ? text : ''] as JSON)
    }

    /** Creative Commons image search proxy (Openverse) for visual reader authoring. */
    def searchImages() {
        applyCorsHeaders()
        try {
            String query = (params.q ?: '').toString().trim()
            if (!query) {
                render([results: []] as JSON)
                return
            }
            String urlStr = 'https://api.openverse.org/v1/images/?q=' +
                URLEncoder.encode(query, 'UTF-8') +
                '&page_size=12&license_type=all-cc&mature=false'
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection()
            conn.setConnectTimeout(4000)
            conn.setReadTimeout(10000)
            conn.setRequestProperty('Accept', 'application/json')
            conn.setRequestProperty('User-Agent', 'TsalagiTrainer/1.0 (Cherokee language education)')
            int statusCode = conn.responseCode
            InputStream stream = statusCode >= 400 ? conn.errorStream : conn.inputStream
            String bodyText = stream ? stream.getText('UTF-8') : ''
            if (statusCode >= 400) {
                throw new RuntimeException("Image search service returned ${statusCode}")
            }
            Object parsed = bodyText.trim() ? new JsonSlurper().parseText(bodyText) : [:]
            List rawResults = (parsed instanceof Map && parsed.results instanceof Collection)
                ? parsed.results as List : []
            List results = rawResults.collect { raw ->
                Map m = (raw instanceof Map) ? raw : [:]
                String thumb = m.thumbnail ? m.thumbnail.toString() : (m.url ? m.url.toString() : '')
                String image = m.url ? m.url.toString() : thumb
                [
                    id          : m.id ? m.id.toString() : '',
                    title       : m.title ? m.title.toString() : 'Untitled image',
                    thumbnailUrl: thumb,
                    imageUrl    : image,
                    creator     : m.creator ? m.creator.toString() : null,
                    license     : m.license ? (m.license.toString().toUpperCase() +
                                  (m.license_version ? ' ' + m.license_version.toString() : '')) : null,
                    licenseUrl  : m.license_url ? m.license_url.toString() : null,
                    sourceUrl   : m.foreign_landing_url ? m.foreign_landing_url.toString() : null,
                    provider    : m.source ? m.source.toString() : (m.provider ? m.provider.toString() : 'Openverse')
                ]
            }.findAll { it.imageUrl }
            render([results: results] as JSON)
        } catch (Exception e) {
            log.warn("Image search failed: ${e.message}")
            response.status = 502
            render([error: (e.message ?: 'Image search failed'), results: []] as JSON)
        }
    }

    def bootstrap() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (String token, String reqTz) = readAuthHeaders()

            withSql { Sql sql ->
                Map currentUser = resolveUserByToken(sql, token, reqTz)
                Map curriculum = getCurriculumInternal(sql, currentUser)

                if (currentUser == null) {
                    render([
                        isAuthenticated: false,
                        currentUser    : null,
                        users          : [],
                        groups         : [],
                        words          : curriculum.words,
                        conversations  : curriculum.conversations,
                        sections       : curriculum.sections,
                        lessons        : curriculum.lessons,
                        units          : curriculum.units,
                        progress       : buildGuestProgress(),
                        dbDriver       : 'mysql'
                    ] as JSON)
                    return
                }

                List<Map> directoryUsers = listDirectoryUsersInternal(sql)
                List<Map> groups = listGroupsForUserInternal(sql, currentUser)
                Map userProgress = assembleUserProgress(sql, (String) currentUser.id, reqTz ?: (String) currentUser.timezone)

                render([
                    isAuthenticated: true,
                    currentUser    : currentUser,
                    users          : directoryUsers,
                    groups         : groups,
                    words          : curriculum.words,
                    conversations  : curriculum.conversations,
                    sections       : curriculum.sections,
                    lessons        : curriculum.lessons,
                    units          : curriculum.units,
                    progress       : userProgress,
                    dbDriver       : 'mysql'
                ] as JSON)
            }
        } catch (Exception ex) {
            log.error('GET /tsalagiTrainer/api/bootstrap failed', ex)
            renderError(500, ex.message)
        }
    }

    /* ========================================================================
     * 3. AUTHENTICATION: REGISTER, LOGIN, LOGOUT
     * ======================================================================== */
    def authRegister() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (unused1, String headerTz) = readAuthHeaders()
            Map body = readJsonBody()

            String cleanUsername = (asString(body.username) ?: '').trim().toLowerCase().replaceAll(/[^a-z0-9_-]/, '')
            String password = asString(body.password) ?: ''
            if (cleanUsername.length() < 3) {
                renderError(400, 'Username must be at least 3 letters or numbers.')
                return
            }
            if (password.length() < 6) {
                renderError(400, 'Password must be at least 6 characters.')
                return
            }

            String cleanEmail = (asString(body.email) ?: '').trim().toLowerCase() ?: null
            String displayName = (asString(body.displayName) ?: '').trim() ?: cleanUsername
            String heritageClan = (asString(body.heritageClan) ?: '').trim() ?: null
            String timezone = (asString(body.timezone) ?: headerTz ?: 'America/Chicago').trim()

            withSql { Sql sql ->
                def dupUser = sql.firstRow('SELECT id FROM ' + T_USERS + ' WHERE username = ?', [cleanUsername])
                if (dupUser) {
                    renderError(400, "Username \"${cleanUsername}\" is already registered.")
                    return
                }
                if (cleanEmail) {
                    def dupEmail = sql.firstRow('SELECT id FROM ' + T_USERS + ' WHERE LOWER(email) = ?', [cleanEmail])
                    if (dupEmail) {
                        renderError(400, "Email \"${cleanEmail}\" is already registered.")
                        return
                    }
                }

                Map account = [
                    id          : "u-${cleanUsername}-${Long.toString(System.currentTimeMillis(), 36)}".toString(),
                    username    : cleanUsername,
                    email       : cleanEmail,
                    displayName : displayName,
                    heritageClan: heritageClan,
                    role        : 'member',
                    timezone    : timezone,
                    createdAt   : Instant.now().toString()
                ]

                insertAccountWithZeroedStats(sql, account, hashPasswordPbkdf2(password))
                String token = issueAuthToken(sql, (String) account.id)

                response.status = 201
                render([
                    token   : token,
                    user    : account.findAll { unused2, v -> v != null },
                    progress: assembleUserProgress(sql, (String) account.id, timezone)
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def authLogin() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (unused3, String headerTz) = readAuthHeaders()
            Map body = readJsonBody()

            String cleanId = (asString(body.identifier) ?: asString(body.username) ?: '').trim().toLowerCase()
            String password = asString(body.password) ?: ''
            if (!cleanId || !password) {
                renderError(400, 'Username/email and password are required.')
                return
            }

            withSql { Sql sql ->
                def row = sql.firstRow(
                    'SELECT * FROM ' + T_USERS + ' WHERE LOWER(username) = ? OR (email IS NOT NULL AND LOWER(email) = ?) LIMIT 1',
                    [cleanId, cleanId]
                )
                if (!row || !verifyPasswordPbkdf2(password, asString(row.password_hash))) {
                    renderError(401, 'Invalid username/email or password.')
                    return
                }

                String tz = (asString(body.timezone) ?: headerTz ?: '').trim()
                if (tz && tz != asString(row.timezone)) {
                    sql.executeUpdate('UPDATE ' + T_USERS + ' SET timezone = ? WHERE id = ?', [tz, row.id])
                    row.timezone = tz
                }

                Map user = mapUserRow(row)
                String token = issueAuthToken(sql, (String) user.id)
                render([
                    token   : token,
                    user    : user,
                    progress: assembleUserProgress(sql, (String) user.id, (String) user.timezone)
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(401, ex.message)
        }
    }

    def authLogout() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (String token, unused4) = readAuthHeaders()
            if (token) {
                withSql { Sql sql ->
                    sql.execute('DELETE FROM ' + T_AUTH_TOKENS + ' WHERE token = ?', [token])
                }
            }
            render([success: true] as JSON)
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    /* ========================================================================
     * 4. USER PROFILE & DIRECTORY
     * ======================================================================== */
    def profile() {
        applyCorsHeaders()
        if (request.method == 'PATCH' || request.method == 'PUT' || request.method == 'POST') {
            updateProfile()
            return
        }
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                render([
                    user    : user,
                    progress: assembleUserProgress(sql, (String) user.id, (String) user.timezone)
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def updateProfile() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            Map patch = readJsonBody()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                String userId = (String) user.id

                def row = sql.firstRow('SELECT * FROM ' + T_USERS + ' WHERE id = ?', [userId])
                if (!row) {
                    renderError(404, 'User profile not found.')
                    return
                }

                String nextDisplayName = patch.containsKey('displayName')
                    ? ((asString(patch.displayName) ?: '').trim() ?: asString(row.username))
                    : asString(row.display_name)

                String nextEmail = asString(row.email)
                if (patch.containsKey('email')) {
                    String cleaned = (asString(patch.email) ?: '').trim().toLowerCase()
                    if (cleaned) {
                        def dup = sql.firstRow(
                            'SELECT id FROM ' + T_USERS + ' WHERE LOWER(email) = ? AND id != ?',
                            [cleaned, userId]
                        )
                        if (dup) {
                            renderError(400, "Email \"${cleaned}\" is already used by another account.")
                            return
                        }
                        nextEmail = cleaned
                    } else {
                        nextEmail = null
                    }
                }

                String nextBio = patch.containsKey('bio')
                    ? ((asString(patch.bio) ?: '').trim() ?: null)
                    : asString(row.bio)
                String nextClan = patch.containsKey('heritageClan')
                    ? ((asString(patch.heritageClan) ?: '').trim() ?: null)
                    : asString(row.heritage_clan)
                String nextTz = (patch.containsKey('timezone') && (asString(patch.timezone) ?: '').trim())
                    ? asString(patch.timezone).trim()
                    : (asString(row.timezone) ?: 'America/Chicago')

                String nextPwHash = asString(row.password_hash) ?: ''
                String newPw = asString(patch.newPassword) ?: ''
                if (newPw.trim()) {
                    if (newPw.length() < 6) {
                        renderError(400, 'New password must be at least 6 characters.')
                        return
                    }
                    String currPw = asString(patch.currentPassword) ?: ''
                    if (nextPwHash && (!currPw || !verifyPasswordPbkdf2(currPw, nextPwHash))) {
                        renderError(400, 'Current password is incorrect.')
                        return
                    }
                    nextPwHash = hashPasswordPbkdf2(newPw)
                }

                sql.withTransaction {
                    sql.executeUpdate(
                        'UPDATE ' + T_USERS + ' SET display_name = ?, email = ?, bio = ?, heritage_clan = ?, timezone = ?, password_hash = ? WHERE id = ?',
                        [nextDisplayName, nextEmail, nextBio, nextClan, nextTz, nextPwHash, userId]
                    )
                    sql.executeUpdate(
                        'UPDATE ' + T_LESSONS + ' SET owner_name = ? WHERE owner_user_id = ?',
                        [nextDisplayName, userId]
                    )
                    if (patch.dailyGoalXp != null) {
                        int nextGoal = Math.max(5, Math.min(1000, asInt(patch.dailyGoalXp, 50)))
                        sql.executeUpdate(
                            'UPDATE ' + T_USER_STATS + ' SET daily_goal_xp = ? WHERE user_id = ?',
                            [nextGoal, userId]
                        )
                    }
                }

                def updatedRow = sql.firstRow('SELECT * FROM ' + T_USERS + ' WHERE id = ?', [userId])
                Map updatedUser = mapUserRow(updatedRow)
                render([
                    user    : updatedUser,
                    progress: assembleUserProgress(sql, userId, nextTz)
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def users() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                render([users: listDirectoryUsersInternal(sql)] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    /* ========================================================================
     * 5. ADMIN USER MANAGEMENT (ASSIGN ADMIN / EDITOR / MEMBER)
     * ======================================================================== */
    def adminUsers() {
        applyCorsHeaders()
        if (request.method == 'POST') {
            adminCreateUser()
            return
        }
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map admin = requireAdmin(sql)
                if (!admin) return
                render([users: listAdminUsersInternal(sql)] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def adminCreateUser() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            Map body = readJsonBody()

            withSql { Sql sql ->
                Map admin = requireAdmin(sql)
                if (!admin) return

                String cleanUsername = (asString(body.username) ?: '').trim().toLowerCase().replaceAll(/[^a-z0-9_-]/, '')
                String password = asString(body.password) ?: ''
                if (cleanUsername.length() < 3) {
                    renderError(400, 'Username must be at least 3 letters or numbers.')
                    return
                }
                if (password.length() < 6) {
                    renderError(400, 'Password must be at least 6 characters.')
                    return
                }

                def dup = sql.firstRow('SELECT id FROM ' + T_USERS + ' WHERE username = ?', [cleanUsername])
                if (dup) {
                    renderError(400, "Username \"${cleanUsername}\" already exists.")
                    return
                }

                String cleanEmail = (asString(body.email) ?: '').trim().toLowerCase() ?: null
                if (cleanEmail) {
                    def dupE = sql.firstRow('SELECT id FROM ' + T_USERS + ' WHERE LOWER(email) = ?', [cleanEmail])
                    if (dupE) {
                        renderError(400, "Email \"${cleanEmail}\" already exists.")
                        return
                    }
                }

                String role = normalizeRole(asString(body.role))
                Map account = [
                    id         : "u-${cleanUsername}-${Long.toString(System.currentTimeMillis(), 36)}".toString(),
                    username   : cleanUsername,
                    email      : cleanEmail,
                    displayName: (asString(body.displayName) ?: '').trim() ?: cleanUsername,
                    role       : role,
                    timezone   : (asString(body.timezone) ?: (String) admin.timezone ?: 'America/Chicago').trim(),
                    createdAt  : Instant.now().toString()
                ]

                insertAccountWithZeroedStats(sql, account, hashPasswordPbkdf2(password))
                List<Map> allUsers = listAdminUsersInternal(sql)
                Map created = allUsers.find { it.id == account.id }

                response.status = 201
                render([user: created, users: allUsers] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def adminUpdateRole() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            String targetUserId = asString(params.userId) ?: ''
            Map body = readJsonBody()
            String rawRole = asString(body.role) ?: ''
            if (!(rawRole in ['admin', 'editor', 'member'])) {
                renderError(400, 'Valid role (admin, editor, or member) is required.')
                return
            }

            withSql { Sql sql ->
                Map admin = requireAdmin(sql)
                if (!admin) return

                def target = sql.firstRow('SELECT id, role FROM ' + T_USERS + ' WHERE id = ?', [targetUserId])
                if (!target) {
                    renderError(404, 'Target user not found.')
                    return
                }

                if (normalizeRole(asString(target.role)) == 'admin' && rawRole != 'admin') {
                    def cntRow = sql.firstRow("SELECT COUNT(*) AS cnt FROM " + T_USERS + " WHERE role = 'admin'")
                    if (asLong(cntRow?.cnt, 0L) <= 1L) {
                        renderError(400, 'Cannot demote the last remaining Admin account.')
                        return
                    }
                }

                sql.executeUpdate('UPDATE ' + T_USERS + ' SET role = ? WHERE id = ?', [rawRole, targetUserId])
                List<Map> allUsers = listAdminUsersInternal(sql)
                Map updated = allUsers.find { it.id == targetUserId }
                render([user: updated, users: allUsers] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def adminDeleteUser() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            String targetUserId = asString(params.userId) ?: ''

            withSql { Sql sql ->
                Map admin = requireAdmin(sql)
                if (!admin) return

                if (targetUserId == admin.id) {
                    renderError(400, 'You cannot delete your own active account.')
                    return
                }

                def target = sql.firstRow('SELECT id, role FROM ' + T_USERS + ' WHERE id = ?', [targetUserId])
                if (!target) {
                    renderError(404, 'User not found.')
                    return
                }

                if (normalizeRole(asString(target.role)) == 'admin') {
                    def cntRow = sql.firstRow("SELECT COUNT(*) AS cnt FROM " + T_USERS + " WHERE role = 'admin'")
                    if (asLong(cntRow?.cnt, 0L) <= 1L) {
                        renderError(400, 'Cannot delete the last remaining Admin account.')
                        return
                    }
                }

                sql.execute('DELETE FROM ' + T_USERS + ' WHERE id = ?', [targetUserId])
                render([success: true, users: listAdminUsersInternal(sql)] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    /* ========================================================================
     * 6. USER GROUPS ("Beginners", "Intermediate Fall 2026", etc.)
     * ======================================================================== */
    def groups() {
        applyCorsHeaders()
        if (request.method == 'POST') {
            createGroup()
            return
        }
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                render([groups: listGroupsForUserInternal(sql, user)] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def createGroup() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            Map body = readJsonBody()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                String cleanName = (asString(body.name) ?: '').trim()
                if (!cleanName) {
                    renderError(400, 'Group name is required (e.g. "Beginners" or "Intermediate Fall 2026").')
                    return
                }

                String desc = (asString(body.description) ?: '').trim() ?: null
                String nowIso = Instant.now().toString()
                String slug = cleanName.toLowerCase().replaceAll(/[^a-z0-9]+/, '-').replaceAll(/^-|-$/, '')
                if (slug.length() > 36) slug = slug.substring(0, 36)
                String groupId = "grp-${slug ?: 'circle'}-${Long.toString(System.currentTimeMillis(), 36)}".toString()

                List<String> memberIds = (body.memberIds instanceof Collection)
                    ? ((Collection) body.memberIds).collect { asString(it) }.findAll { it }.unique()
                    : []

                sql.withTransaction {
                    sql.executeInsert(
                        'INSERT INTO ' + T_GROUPS + ' (id, name, description, owner_user_id, created_at) VALUES (?, ?, ?, ?, ?)',
                        [groupId, cleanName, desc, user.id, nowIso]
                    )
                    memberIds.each { String uid ->
                        if (sql.firstRow('SELECT id FROM ' + T_USERS + ' WHERE id = ?', [uid])) {
                            sql.executeUpdate(
                                'INSERT IGNORE INTO ' + T_GROUP_MEMBERS + ' (group_id, user_id, added_at) VALUES (?, ?, ?)',
                                [groupId, uid, nowIso]
                            )
                        }
                    }
                    evaluateAndUnlockAwards(sql, (String) user.id, nowIso)
                }

                def groupRow = sql.firstRow('SELECT * FROM ' + T_GROUPS + ' WHERE id = ?', [groupId])
                response.status = 201
                render([
                    group   : hydrateGroupRow(sql, groupRow),
                    groups  : listGroupsForUserInternal(sql, user),
                    progress: assembleUserProgress(sql, (String) user.id, (String) user.timezone)
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def groupItem() {
        applyCorsHeaders()
        if (request.method == 'DELETE') {
            deleteGroup()
            return
        }
        updateGroup()
    }

    def updateGroup() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            String groupId = asString(params.groupId) ?: ''
            Map body = readJsonBody()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                def existing = sql.firstRow('SELECT * FROM ' + T_GROUPS + ' WHERE id = ?', [groupId])
                if (!existing) {
                    renderError(404, 'Group not found.')
                    return
                }
                if (user.role != 'admin' && asString(existing.owner_user_id) != user.id) {
                    renderError(403, 'Only the group creator or an Admin can modify this group.')
                    return
                }

                String cleanName = (asString(body.name) ?: '').trim()
                if (!cleanName) {
                    renderError(400, 'Group name cannot be empty.')
                    return
                }
                String desc = (asString(body.description) ?: '').trim() ?: null
                String nowIso = Instant.now().toString()
                List<String> memberIds = (body.memberIds instanceof Collection)
                    ? ((Collection) body.memberIds).collect { asString(it) }.findAll { it }.unique()
                    : []

                sql.withTransaction {
                    sql.executeUpdate(
                        'UPDATE ' + T_GROUPS + ' SET name = ?, description = ? WHERE id = ?',
                        [cleanName, desc, groupId]
                    )
                    sql.execute('DELETE FROM ' + T_GROUP_MEMBERS + ' WHERE group_id = ?', [groupId])
                    memberIds.each { String uid ->
                        if (sql.firstRow('SELECT id FROM ' + T_USERS + ' WHERE id = ?', [uid])) {
                            sql.executeUpdate(
                                'INSERT IGNORE INTO ' + T_GROUP_MEMBERS + ' (group_id, user_id, added_at) VALUES (?, ?, ?)',
                                [groupId, uid, nowIso]
                            )
                        }
                    }
                }

                def updated = sql.firstRow('SELECT * FROM ' + T_GROUPS + ' WHERE id = ?', [groupId])
                render([
                    group : hydrateGroupRow(sql, updated),
                    groups: listGroupsForUserInternal(sql, user)
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def deleteGroup() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            String groupId = asString(params.groupId) ?: ''

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                def existing = sql.firstRow('SELECT owner_user_id FROM ' + T_GROUPS + ' WHERE id = ?', [groupId])
                if (!existing) {
                    renderError(404, 'Group not found.')
                    return
                }
                if (user.role != 'admin' && asString(existing.owner_user_id) != user.id) {
                    renderError(403, 'Only the group creator or an Admin can delete this group.')
                    return
                }

                sql.execute('DELETE FROM ' + T_GROUPS + ' WHERE id = ?', [groupId])
                render([success: true, groups: listGroupsForUserInternal(sql, user)] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    /* ========================================================================
     * 7. USER-AUTHORED LESSONS & PER-LESSON SHARING / VISIBILITY
     * ======================================================================== */
    def upsertLesson() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            Map body = readJsonBody()
            Map lesson = (body.lesson instanceof Map) ? (Map) body.lesson : body
            String lessonId = asString(lesson.id)
            String title = asString(lesson.title)
            if (!lessonId || !title) {
                renderError(400, 'Valid lesson object is required.')
                return
            }

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                def existing = sql.firstRow('SELECT * FROM ' + T_LESSONS + ' WHERE id = ?', [lessonId])
                String ownerUserId = (String) user.id
                String ownerName = (String) user.displayName
                int sortOrder = 0

                if (existing) {
                    String prevOwner = asString(existing.owner_user_id)
                    boolean canEdit = (user.role == 'admin') ||
                        (user.role == 'editor' && !prevOwner) ||
                        (prevOwner == user.id)
                    if (!canEdit) {
                        renderError(403, 'You do not have permission to edit this lesson.')
                        return
                    }
                    ownerUserId = prevOwner
                    ownerName = asString(existing.owner_name)
                    sortOrder = asInt(existing.sort_order, 0)
                } else {
                    def maxRow = sql.firstRow('SELECT COALESCE(MAX(sort_order), 0) + 1 AS next_sort FROM ' + T_LESSONS)
                    sortOrder = asInt(maxRow?.next_sort, 0)
                }

                String visibility = asString(lesson.visibility) == 'restricted' ? 'restricted' : 'public'
                String nowIso = Instant.now().toString()
                List<String> sharedUsers = (lesson.sharedWithUserIds instanceof Collection)
                    ? ((Collection) lesson.sharedWithUserIds).collect { asString(it) }.findAll { it }.unique()
                    : []
                List<String> sharedGroups = (lesson.sharedWithGroupIds instanceof Collection)
                    ? ((Collection) lesson.sharedWithGroupIds).collect { asString(it) }.findAll { it }.unique()
                    : []

                sql.withTransaction {
                sql.executeUpdate(
                    'INSERT INTO ' + T_LESSONS + ' ' +
                    '(id, unit_id, title, title_cherokee, description, type, level, xp_reward, ' +
                    'word_ids_json, conversation_id, sentence_challenges_json, visual_reading_json, ' +
                    'reader_json, custom_content_json, image_url, owner_user_id, owner_name, visibility, sort_order) ' +
                    'VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ' +
                    'ON DUPLICATE KEY UPDATE ' +
                    'unit_id = VALUES(unit_id), title = VALUES(title), title_cherokee = VALUES(title_cherokee), ' +
                    'description = VALUES(description), type = VALUES(type), level = VALUES(level), ' +
                    'xp_reward = VALUES(xp_reward), word_ids_json = VALUES(word_ids_json), ' +
                    'conversation_id = VALUES(conversation_id), sentence_challenges_json = VALUES(sentence_challenges_json), ' +
                    'visual_reading_json = VALUES(visual_reading_json), reader_json = VALUES(reader_json), custom_content_json = VALUES(custom_content_json), ' +
                    'image_url = VALUES(image_url), ' +
                    'visibility = VALUES(visibility)',
                    [
                        lessonId,
                        asString(lesson.unitId) ?: 'unit-1',
                        title,
                        asString(lesson.titleCherokee),
                        asString(lesson.description) ?: '',
                        asString(lesson.type) ?: 'flashcards',
                        asString(lesson.level) ?: 'Beginner',
                        asInt(lesson.xpReward, 15),
                        JsonOutput.toJson(lesson.wordIds ?: []),
                        asString(lesson.conversationId),
                        lesson.sentenceChallenges != null ? JsonOutput.toJson(lesson.sentenceChallenges) : null,
                        lesson.visualReadingContent != null ? JsonOutput.toJson(lesson.visualReadingContent) : null,
                        lesson.readerContent != null ? JsonOutput.toJson(lesson.readerContent) : null,
                        lesson.customContent != null ? JsonOutput.toJson(lesson.customContent) : null,
                        asString(lesson.imageUrl),
                        ownerUserId,
                        ownerName,
                        visibility,
                        sortOrder
                    ]
                )

                    syncLessonSections(sql, lessonId, lesson.sectionIds)
                    syncLessonShares(sql, lessonId, sharedUsers, sharedGroups)
                    evaluateAndUnlockAwards(sql, (String) user.id, nowIso)
                }

                def savedRow = sql.firstRow('SELECT * FROM ' + T_LESSONS + ' WHERE id = ?', [lessonId])
                Map curr = getCurriculumInternal(sql, user)
                render([
                    lesson  : hydrateLessonRow(sql, savedRow),
                    lessons : curr.lessons,
                    units   : curr.units,
                    progress: assembleUserProgress(sql, (String) user.id, (String) user.timezone),
                    groups  : listGroupsForUserInternal(sql, user)
                ] as JSON)
            }
        } catch (Exception ex) {
            log.error('POST /tsalagiTrainer/api/lessons failed', ex)
            renderError(400, ex.message)
        }
    }

    def shareLesson() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            String lessonId = asString(params.lessonId) ?: ''
            Map body = readJsonBody()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                def existing = sql.firstRow('SELECT * FROM ' + T_LESSONS + ' WHERE id = ?', [lessonId])
                if (!existing) {
                    renderError(404, 'Lesson not found.')
                    return
                }

                String prevOwner = asString(existing.owner_user_id)
                boolean canShare = (user.role == 'admin') ||
                    (user.role == 'editor' && !prevOwner) ||
                    (prevOwner == user.id)
                if (!canShare) {
                    renderError(403, 'Only the lesson creator or an Admin can change sharing settings.')
                    return
                }

                String visibility = asString(body.visibility) == 'restricted' ? 'restricted' : 'public'
                List<String> sharedUsers = (body.sharedWithUserIds instanceof Collection)
                    ? ((Collection) body.sharedWithUserIds).collect { asString(it) }.findAll { it }.unique()
                    : []
                List<String> sharedGroups = (body.sharedWithGroupIds instanceof Collection)
                    ? ((Collection) body.sharedWithGroupIds).collect { asString(it) }.findAll { it }.unique()
                    : []

                sql.withTransaction {
                    sql.executeUpdate('UPDATE ' + T_LESSONS + ' SET visibility = ? WHERE id = ?', [visibility, lessonId])
                    syncLessonShares(sql, lessonId, sharedUsers, sharedGroups)
                }

                def updatedRow = sql.firstRow('SELECT * FROM ' + T_LESSONS + ' WHERE id = ?', [lessonId])
                Map curr = getCurriculumInternal(sql, user)
                render([
                    lesson : hydrateLessonRow(sql, updatedRow),
                    lessons: curr.lessons,
                    units  : curr.units,
                    groups : listGroupsForUserInternal(sql, user)
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def deleteLesson() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            String lessonId = asString(params.lessonId) ?: ''

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                def existing = sql.firstRow('SELECT owner_user_id FROM ' + T_LESSONS + ' WHERE id = ?', [lessonId])
                if (existing) {
                    String prevOwner = asString(existing.owner_user_id)
                    boolean canDelete = (user.role == 'admin') ||
                        (user.role == 'editor' && !prevOwner) ||
                        (prevOwner == user.id)
                    if (!canDelete) {
                        renderError(403, 'Only the lesson creator or an Admin can delete this lesson.')
                        return
                    }
                    sql.execute('DELETE FROM ' + T_LESSONS + ' WHERE id = ?', [lessonId])
                }

                Map curr = getCurriculumInternal(sql, user)
                render([
                    success: true,
                    lessons: curr.lessons,
                    units  : curr.units
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    /* ========================================================================
     * 7b. SECTIONS: STANDALONE REUSABLE LESSON BUILDING BLOCKS
     * ======================================================================== */
    def sections() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (String token, String reqTz) = readAuthHeaders()

            withSql { Sql sql ->
                Map viewer = resolveUserByToken(sql, token, reqTz)
                render([sections: listSectionsInternal(sql, viewer)] as JSON)
            }
        } catch (Exception ex) {
            log.error('GET /tsalagiTrainer/api/sections failed', ex)
            renderError(500, ex.message)
        }
    }

    def sectionUpsert() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                def json = request.JSON
                Map section = (json.section instanceof Map) ? (Map) json.section : null
                if (section == null) {
                    renderError(400, 'Valid section payload is required.')
                    return
                }

                Map saved = upsertSectionInternal(sql, section, user)
                Map curr = getCurriculumInternal(sql, user)
                render([
                    section : saved,
                    sections: curr.sections,
                    lessons : curr.lessons,
                    units   : curr.units
                ] as JSON)
            }
        } catch (IllegalAccessException ex) {
            renderError(403, ex.message)
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def sectionDelete() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                String sectionId = asString(params.sectionId) ?: ''

                deleteSectionInternal(sql, sectionId, user)
                Map curr = getCurriculumInternal(sql, user)
                render([
                    success : true,
                    sections: curr.sections,
                    lessons : curr.lessons,
                    units   : curr.units
                ] as JSON)
            }
        } catch (IllegalAccessException ex) {
            renderError(403, ex.message)
        } catch (IllegalStateException ex) {
            renderError(409, ex.message)
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    /* ========================================================================
     * 7c. LEARNING UNITS (CHAPTERS)
     * ======================================================================== */
    def unitUpsert() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()

            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return

                def json = request.JSON
                Map unit = (json.unit instanceof Map) ? (Map) json.unit : null
                String unitId = unit ? asString(unit.id) : ''
                if (!unit || !unitId || !asString(unit.title)) {
                    renderError(400, 'Valid unit (id, title) is required.')
                    return
                }

                sql.executeUpdate(
                    'INSERT INTO ' + T_UNITS + ' ' +
                    '(id, unit_number, title, title_cherokee, description, banner_image, lesson_ids_json, accent_color) ' +
                    'VALUES (?, ?, ?, ?, ?, ?, ?, ?) ' +
                    'ON DUPLICATE KEY UPDATE ' +
                    'unit_number = VALUES(unit_number), title = VALUES(title), title_cherokee = VALUES(title_cherokee), ' +
                    'description = VALUES(description), banner_image = VALUES(banner_image), ' +
                    'lesson_ids_json = VALUES(lesson_ids_json), accent_color = VALUES(accent_color)',
                    [
                        unitId,
                        asInt(unit.unitNumber, 1),
                        asString(unit.title) ?: '',
                        asString(unit.titleCherokee),
                        asString(unit.description) ?: '',
                        asString(unit.bannerImage) ?: '',
                        JsonOutput.toJson(unit.lessonIds ?: []),
                        asString(unit.accentColor) ?: '#fbbf24'
                    ]
                )

                Map curr = getCurriculumInternal(sql, editor)
                render([units: curr.units, lessons: curr.lessons] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def unitDelete() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()

            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return
                String unitId = asString(params.unitId) ?: ''

                def lessonRow = sql.firstRow(
                    'SELECT COUNT(*) AS cnt FROM ' + T_LESSONS + ' WHERE unit_id = ?', [unitId]
                )
                long lessonCount = asLong(lessonRow?.cnt, 0L)
                if (lessonCount > 0L) {
                    renderError(400, 'This unit still contains ' + lessonCount + ' lesson(s). Move or delete them first.')
                    return
                }

                sql.executeUpdate('DELETE FROM ' + T_UNITS + ' WHERE id = ?', [unitId])
                Map curr = getCurriculumInternal(sql, editor)
                render([units: curr.units] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    /* ========================================================================
     * 8. DYNAMIC PROGRESS, DAILY GOAL, STREAKS, AWARDS & MISTAKES
     * ======================================================================== */
    def progress() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (unused5, String reqTz) = readAuthHeaders()
            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                Map prog = assembleUserProgress(sql, (String) user.id, reqTz ?: (String) user.timezone)
                render([progress: prog, currentUser: user] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def updatePreferences() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (unused6, String reqTz) = readAuthHeaders()
            Map patch = readJsonBody()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                String tz = reqTz ?: (String) user.timezone ?: 'America/Chicago'
                String localToday = getLocalTodayDate(tz)
                Map current = assembleUserProgress(sql, (String) user.id, tz)

                int nextDailyGoal = patch.dailyGoalXp != null
                    ? Math.max(5, Math.min(1000, asInt(patch.dailyGoalXp, 50)))
                    : asInt(current.dailyGoalXp, 50)
                int nextGems = patch.gems != null
                    ? Math.max(0, asInt(patch.gems, 50))
                    : asInt(current.gems, 50)
                int nextHearts = patch.hearts != null
                    ? Math.max(0, Math.min(5, asInt(patch.hearts, 5)))
                    : asInt(current.hearts, 5)
                int nextFreezes = patch.freezesRemaining != null
                    ? Math.max(0, Math.min(3, asInt(patch.freezesRemaining, 1)))
                    : asInt(current.freezesRemaining, 1)
                boolean nextFrozen = patch.streakFrozen != null
                    ? asBool(patch.streakFrozen, false)
                    : asBool(current.streakFrozen, false)

                Map mergedSettings = [
                    showPhonetics     : true,
                    showAudioGuides   : true,
                    enableSoundEffects: true
                ]
                if (current.settings instanceof Map) {
                    mergedSettings.putAll((Map) current.settings)
                }
                if (patch.settings instanceof Map) {
                    mergedSettings.putAll((Map) patch.settings)
                }

                sql.withTransaction {
                    sql.executeUpdate(
                        'UPDATE ' + T_USER_STATS +
                        ' SET daily_goal_xp = ?, gems = ?, hearts = ?, freezes_remaining = ?, streak_frozen = ?, settings_json = ? WHERE user_id = ?',
                        [
                            nextDailyGoal,
                            nextGems,
                            nextHearts,
                            nextFreezes,
                            nextFrozen ? 1 : 0,
                            JsonOutput.toJson(mergedSettings),
                            user.id
                        ]
                    )

                    sql.executeUpdate(
                        'UPDATE ' + T_DAILY_XP + ' SET goal_target_snapshot = ? WHERE user_id = ? AND local_date = ?',
                        [nextDailyGoal, user.id, localToday]
                    )
                }

                render([progress: assembleUserProgress(sql, (String) user.id, tz)] as JSON)
            }
        } catch (Exception ex) {
            log.error('PATCH /tsalagiTrainer/api/progress/preferences failed', ex)
            renderError(500, ex.message)
        }
    }

    def recordActivity() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            def (unused7, String reqTz) = readAuthHeaders()
            Map body = readJsonBody()

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                String userId = (String) user.id
                String tz = reqTz ?: (String) user.timezone ?: 'America/Chicago'
                String localToday = getLocalTodayDate(tz)
                String nowIso = Instant.now().toString()

                int previousTodayXp = 0
                int currentTodayXp = 0
                int dailyGoalXp = 50
                boolean justReachedDailyGoal = false
                int bonusGems = 0
                List<Map> newlyUnlockedAwards = []

                sql.withTransaction {
                    reconcileStreakForToday(sql, userId, localToday)

                    def stats = sql.firstRow(
                        'SELECT xp, daily_goal_xp, gems, streak, last_active_date FROM ' + T_USER_STATS + ' WHERE user_id = ?',
                        [userId]
                    )
                    if (!stats) {
                        throw new IllegalStateException("User stats not found for ${userId}")
                    }

                    dailyGoalXp = Math.max(5, asInt(stats.daily_goal_xp, 50))

                    def existingDaily = sql.firstRow(
                        'SELECT xp_earned, goal_reached_at FROM ' + T_DAILY_XP + ' WHERE user_id = ? AND local_date = ?',
                        [userId, localToday]
                    )

                    previousTodayXp = asInt(existingDaily?.xp_earned, 0)
                    int earnedXp = Math.max(0, asInt(body.earnedXp, 0))
                    currentTodayXp = previousTodayXp + earnedXp

                    boolean alreadyReachedToday = existingDaily?.goal_reached_at != null && asString(existingDaily.goal_reached_at).trim() != ''
                    justReachedDailyGoal = !alreadyReachedToday && earnedXp > 0 && currentTodayXp >= dailyGoalXp
                    bonusGems = justReachedDailyGoal ? 10 : 0

                    String goalReachedAt = alreadyReachedToday
                        ? asString(existingDaily.goal_reached_at)
                        : (justReachedDailyGoal ? nowIso : null)

                    if (existingDaily) {
                        sql.executeUpdate(
                            'UPDATE ' + T_DAILY_XP +
                            ' SET xp_earned = ?, goal_target_snapshot = ?, goal_reached_at = ? WHERE user_id = ? AND local_date = ?',
                            [currentTodayXp, dailyGoalXp, goalReachedAt, userId, localToday]
                        )
                    } else {
                        sql.executeInsert(
                            'INSERT INTO ' + T_DAILY_XP +
                            ' (user_id, local_date, xp_earned, goal_target_snapshot, goal_reached_at) VALUES (?, ?, ?, ?, ?)',
                            [userId, localToday, currentTodayXp, dailyGoalXp, goalReachedAt]
                        )
                    }

                    String prevLastActive = asString(stats.last_active_date) ?: ''
                    int currentStreak = asInt(stats.streak, 0)
                    boolean isFirstActivityToday = prevLastActive != localToday && earnedXp > 0
                    int nextStreak = isFirstActivityToday
                        ? currentStreak + 1
                        : (earnedXp > 0 ? Math.max(1, currentStreak) : currentStreak)
                    String nextLastActive = earnedXp > 0 ? localToday : prevLastActive
                    int nextXp = asInt(stats.xp, 0) + earnedXp
                    int gemsDelta = asInt(body.gemsDelta, 0)
                    int nextGems = Math.max(0, asInt(stats.gems, 0) + gemsDelta + bonusGems)

                    sql.executeUpdate(
                        'UPDATE ' + T_USER_STATS +
                        ' SET xp = ?, gems = ?, streak = ?, last_active_date = ? WHERE user_id = ?',
                        [nextXp, nextGems, nextStreak, nextLastActive, userId]
                    )

                    if (body.lessonCompletion instanceof Map) {
                        Map lc = (Map) body.lessonCompletion
                        String lessonId = asString(lc.lessonId)
                        int incomingScore = asInt(lc.score, 100)
                        if (lessonId) {
                            def prevComp = sql.firstRow(
                                'SELECT score FROM ' + T_COMPLETED_LESSONS + ' WHERE user_id = ? AND lesson_id = ?',
                                [userId, lessonId]
                            )
                            int bestScore = prevComp ? Math.max(asInt(prevComp.score, 0), incomingScore) : incomingScore
                            if (prevComp) {
                                sql.executeUpdate(
                                    'UPDATE ' + T_COMPLETED_LESSONS +
                                    ' SET completed_at = ?, score = ?, high_accuracy = ? WHERE user_id = ? AND lesson_id = ?',
                                    [localToday, bestScore, bestScore >= 80 ? 1 : 0, userId, lessonId]
                                )
                            } else {
                                sql.executeInsert(
                                    'INSERT INTO ' + T_COMPLETED_LESSONS +
                                    ' (user_id, lesson_id, completed_at, score, high_accuracy) VALUES (?, ?, ?, ?, ?)',
                                    [userId, lessonId, localToday, bestScore, bestScore >= 80 ? 1 : 0]
                                )
                            }
                        }
                    }

                    String masteredWordId = asString(body.masteredWordId)
                    if (masteredWordId) {
                        sql.executeUpdate(
                            'INSERT IGNORE INTO ' + T_MASTERED_WORDS + ' (user_id, word_id, mastered_at) VALUES (?, ?, ?)',
                            [userId, masteredWordId, nowIso]
                        )
                    }

                    if (body.clearedMistakeIds instanceof Collection) {
                        ((Collection) body.clearedMistakeIds).each { Object rawKey ->
                            String key = asString(rawKey)
                            if (key) {
                                sql.executeUpdate(
                                    'UPDATE ' + T_MISTAKES + ' SET resolved_at = ? WHERE user_id = ? AND item_key = ?',
                                    [nowIso, userId, key]
                                )
                            }
                        }
                    }

                    newlyUnlockedAwards = evaluateAndUnlockAwards(sql, userId, nowIso)
                    newlyUnlockedAwards.each { Map a ->
                        bonusGems += asInt(a.gemsBonus, 0)
                    }
                }

                render([
                    progress            : assembleUserProgress(sql, userId, tz),
                    justReachedDailyGoal: justReachedDailyGoal,
                    previousTodayXp     : previousTodayXp,
                    currentTodayXp      : currentTodayXp,
                    dailyGoalXp         : dailyGoalXp,
                    bonusGems           : bonusGems,
                    newlyUnlockedAwards : newlyUnlockedAwards
                ] as JSON)
            }
        } catch (Exception ex) {
            log.error('POST /tsalagiTrainer/api/progress/activity failed', ex)
            renderError(500, ex.message)
        }
    }

    def mistakes() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            Map draft = readJsonBody()
            String itemKey = asString(draft.id)
            String cherokee = asString(draft.cherokee)

            if (!itemKey || !cherokee) {
                renderError(400, 'Valid mistake payload is required.')
                return
            }

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                String userId = (String) user.id
                String nowIso = Instant.now().toString()

                def existing = sql.firstRow(
                    'SELECT id, mistake_count FROM ' + T_MISTAKES + ' WHERE user_id = ? AND item_key = ?',
                    [userId, itemKey]
                )

                String kind = asString(draft.kind) == 'word' ? 'word' : 'sentence'
                String wordId = asString(draft.wordId)
                String phonetics = asString(draft.phonetics)
                String english = asString(draft.english) ?: ''
                String sentenceType = asString(draft.sentenceType)
                String optWordsJson = draft.optionalWords != null ? JsonOutput.toJson(draft.optionalWords) : null
                String herringsJson = draft.redHerrings != null ? JsonOutput.toJson(draft.redHerrings) : null
                String explanation = asString(draft.explanation)
                String sourceTitle = asString(draft.sourceTitle)

                if (existing) {
                    int nextCount = asInt(existing.mistake_count, 0) + 1
                    sql.executeUpdate(
                        'UPDATE ' + T_MISTAKES + ' ' +
                        'SET kind = ?, word_id = ?, cherokee = ?, phonetics = ?, english = ?, ' +
                        'sentence_type = ?, optional_words_json = ?, red_herrings_json = ?, ' +
                        'explanation = ?, source_title = ?, mistake_count = ?, last_missed_at = ?, resolved_at = NULL ' +
                        'WHERE id = ?',
                        [
                            kind, wordId, cherokee, phonetics, english,
                            sentenceType, optWordsJson, herringsJson,
                            explanation, sourceTitle, nextCount, nowIso, existing.id
                        ]
                    )
                } else {
                    sql.executeInsert(
                        'INSERT INTO ' + T_MISTAKES + ' ' +
                        '(user_id, item_key, kind, word_id, cherokee, phonetics, english, ' +
                        'sentence_type, optional_words_json, red_herrings_json, ' +
                        'explanation, source_title, mistake_count, last_missed_at, resolved_at) ' +
                        'VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?, NULL)',
                        [
                            userId, itemKey, kind, wordId, cherokee, phonetics, english,
                            sentenceType, optWordsJson, herringsJson,
                            explanation, sourceTitle, nowIso
                        ]
                    )
                }

                render([mistakes: getActiveMistakes(sql, userId)] as JSON)
            }
        } catch (Exception ex) {
            log.error('POST /tsalagiTrainer/api/mistakes failed', ex)
            renderError(500, ex.message)
        }
    }

    def resolveMistake() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            String rawKey = params.itemKey ?: ''
            String itemKey = URLDecoder.decode(rawKey, 'UTF-8')

            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return
                String userId = (String) user.id
                sql.executeUpdate(
                    'UPDATE ' + T_MISTAKES + ' SET resolved_at = ? WHERE user_id = ? AND item_key = ?',
                    [Instant.now().toString(), userId, itemKey]
                )
                render([mistakes: getActiveMistakes(sql, userId)] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    /* ========================================================================
     * 9. SHARED CURRICULUM CRUD (EDITOR OR ADMIN PROTECTED)
     * ======================================================================== */
    def saveWords() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return
                Map body = readJsonBody()
                if (!(body.words instanceof List)) {
                    renderError(400, 'words array is required.')
                    return
                }
                writeWords(sql, (List<Map>) body.words)
                render([words: body.words] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def saveConversations() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return
                Map body = readJsonBody()
                if (!(body.conversations instanceof List)) {
                    renderError(400, 'conversations array is required.')
                    return
                }
                writeConversations(sql, (List<Map>) body.conversations)
                render([conversations: body.conversations] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def saveLessons() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return
                Map body = readJsonBody()
                if (!(body.lessons instanceof List)) {
                    renderError(400, 'lessons array is required.')
                    return
                }
                writeLessons(sql, (List<Map>) body.lessons, editor)
                Map refreshed = getCurriculumInternal(sql, editor)
                render([lessons: refreshed.lessons, units: refreshed.units] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def exportCurriculum() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return
                Map curr = getCurriculumInternal(sql, editor)
                render([
                    app            : 'Tsalagi Cherokee Language Studio',
                    version        : '3.1-sections-api',
                    exportedAt     : Instant.now().toString(),
                    words          : curr.words,
                    conversations  : curr.conversations,
                    sections       : curr.sections,
                    lessonTemplates: curr.lessons,
                    units          : curr.units
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    def importCurriculum() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return
                Map data = readJsonBody()

                if (data.words instanceof List) writeWords(sql, (List<Map>) data.words)
                if (data.conversations instanceof List) writeConversations(sql, (List<Map>) data.conversations)
                if (data.sections instanceof List) writeSections(sql, (List<Map>) data.sections)
                List<Map> incomingLessons = (data.lessonTemplates instanceof List)
                    ? (List<Map>) data.lessonTemplates
                    : ((data.lessons instanceof List) ? (List<Map>) data.lessons : null)
                if (incomingLessons != null) writeLessons(sql, incomingLessons, editor)
                if (data.units instanceof List) writeUnits(sql, (List<Map>) data.units)

                Map curr = getCurriculumInternal(sql, editor)
                render([
                    success      : true,
                    message      : "Imported ${curr.words.size()} words, ${curr.conversations.size()} conversations, ${curr.sections.size()} sections, and ${curr.lessons.size()} lessons into MySQL!",
                    words        : curr.words,
                    conversations: curr.conversations,
                    sections     : curr.sections,
                    lessons      : curr.lessons,
                    units        : curr.units
                ] as JSON)
            }
        } catch (Exception ex) {
            renderError(400, ex.message)
        }
    }

    def resetCurriculum() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map editor = requireEditorOrAdmin(sql)
                if (!editor) return
                Map data = readJsonBody()

                if (data.words instanceof List) writeWords(sql, (List<Map>) data.words)
                if (data.conversations instanceof List) writeConversations(sql, (List<Map>) data.conversations)
                if (data.sections instanceof List) {
                    sql.execute('DELETE FROM ' + T_SECTIONS + ' WHERE owner_user_id IS NULL')
                    writeSections(sql, (List<Map>) data.sections)
                }
                List<Map> incomingLessons = (data.lessonTemplates instanceof List)
                    ? (List<Map>) data.lessonTemplates
                    : ((data.lessons instanceof List) ? (List<Map>) data.lessons : null)
                if (incomingLessons != null) {
                    sql.execute('DELETE FROM ' + T_LESSONS + ' WHERE owner_user_id IS NULL')
                    writeLessons(sql, incomingLessons, null)
                }
                if (data.units instanceof List) writeUnits(sql, (List<Map>) data.units)

                render(getCurriculumInternal(sql, editor) as JSON)
            }
        } catch (Exception ex) {
            renderError(500, ex.message)
        }
    }

    /* ========================================================================
     * 10. MEDIA UPLOADS & STATIC ASSETS
     * ======================================================================== */
    def uploadMedia() {
        applyCorsHeaders()
        try {
            ensureSchemaInitialized()
            withSql { Sql sql ->
                Map user = requireAuth(sql)
                if (!user) return

                Map body = readJsonBody()
                String dataUrl = asString(body.dataUrl)
                String filename = asString(body.filename) ?: 'asset'

                if (!dataUrl || !dataUrl.startsWith('data:')) {
                    renderError(400, 'Valid dataUrl is required.')
                    return
                }

                def matcher = (dataUrl =~ /^data:([a-zA-Z0-9\/+.-]+);base64,(.+)$/)
                if (!matcher.matches()) {
                    renderError(400, 'Malformed base64 dataUrl.')
                    return
                }

                String mimeType = matcher.group(1)
                String base64Data = matcher.group(2)
                String ext = (mimeType.contains('/') ? mimeType.split('/')[1] : 'png').replaceAll(/[^a-zA-Z0-9]/, '')
                String safeStem = filename.replaceAll(/\.[^.]+$/, '').replaceAll(/[^a-zA-Z0-9_-]/, 'unused8')
                if (safeStem.length() > 48) safeStem = safeStem.substring(0, 48)

                String finalName = "${System.currentTimeMillis()}-${safeStem}.${ext}"
                File dir = resolveUploadsDir()
                dir.mkdirs()

                File target = new File(dir, finalName)
                target.bytes = Base64.decoder.decode(base64Data)

                String publicUrl = g.createLink(uri: "/tsalagiTrainer/uploads/${finalName}").toString()
                response.status = 201
                render([url: publicUrl] as JSON)
            }
        } catch (Exception ex) {
            log.error('POST /tsalagiTrainer/api/media/upload failed', ex)
            renderError(500, ex.message)
        }
    }

    def serveUpload() {
        applyCorsHeaders()
        String uri = request.requestURI ?: ''
        String rawName = uri.contains('/uploads/') ? uri.substring(uri.indexOf('/uploads/') + '/uploads/'.length()) : (params.filename ?: '')
        String safeName = new File(URLDecoder.decode(rawName, 'UTF-8')).name
        File target = new File(resolveUploadsDir(), safeName)
        if (!target.exists() || !target.isFile()) {
            response.status = 404
            render 'Not found'
            return
        }
        String contentType = Files.probeContentType(target.toPath()) ?: guessMimeType(safeName)
        response.contentType = contentType
        response.contentLengthLong = target.length()
        response.outputStream << target.newInputStream()
        response.outputStream.flush()
    }

    def serveAsset() {
        applyCorsHeaders()
        String uri = request.requestURI ?: ''
        String relPath = uri.contains('/tsalagiTrainer/assets/')
            ? uri.substring(uri.indexOf('/tsalagiTrainer/assets/') + '/tsalagiTrainer/assets/'.length())
            : (params.filename ?: '')
        relPath = URLDecoder.decode(relPath, 'UTF-8').replace('\\', '/')

        if (!relPath || relPath.contains('..')) {
            response.status = 404
            render 'Not found'
            return
        }

        response.contentType = guessMimeType(relPath)

        List<File> diskCandidates = [
            new File('src/main/resources/public/tsalagiTrainer/assets', relPath),
            new File('src/main/resources/public/tsalagiTrainer/assets/assets', relPath)
        ]
        File diskHit = diskCandidates.find { it.exists() && it.isFile() }
        if (diskHit != null) {
            response.contentLengthLong = diskHit.length()
            response.outputStream << diskHit.newInputStream()
            response.outputStream.flush()
            return
        }

        ClassLoader cl = Thread.currentThread().contextClassLoader ?: this.class.classLoader
        InputStream stream = cl.getResourceAsStream("public/tsalagiTrainer/assets/${relPath}")
            ?: cl.getResourceAsStream("public/tsalagiTrainer/assets/assets/${relPath}")
            ?: cl.getResourceAsStream("tsalagiTrainer/assets/${relPath}")

        if (stream == null) {
            response.status = 404
            render "Asset not found: ${relPath}"
            return
        }

        try {
            response.outputStream << stream
            response.outputStream.flush()
        } finally {
            stream.close()
        }
    }

    private String guessMimeType(String filename) {
        String lower = (filename ?: '').toLowerCase()
        if (lower.endsWith('.js') || lower.endsWith('.mjs')) return 'application/javascript;charset=UTF-8'
        if (lower.endsWith('.css')) return 'text/css;charset=UTF-8'
        if (lower.endsWith('.svg')) return 'image/svg+xml'
        if (lower.endsWith('.png')) return 'image/png'
        if (lower.endsWith('.jpg') || lower.endsWith('.jpeg')) return 'image/jpeg'
        if (lower.endsWith('.webp')) return 'image/webp'
        if (lower.endsWith('.woff2')) return 'font/woff2'
        if (lower.endsWith('.mp3')) return 'audio/mpeg'
        if (lower.endsWith('.wav')) return 'audio/wav'
        return 'application/octet-stream'
    }

    /* ========================================================================
     * INTERNAL SQL, MIGRATIONS & AUTH HELPERS
     * ======================================================================== */
    private File resolveUploadsDir() {
        String configured = grailsApplication?.config?.getProperty('tsalagi.uploadsDir', String)
        return configured ? new File(configured) : new File(System.getProperty('user.home'), '.tsalagi-uploads')
    }

    private <T> T withSql(Closure<T> work) {
        Sql sql = new Sql(dataSource)
        try {
            return work.call(sql)
        } finally {
            sql.close()
        }
    }

    private void ensureSchemaInitialized() {
        if (SCHEMA_INITIALIZED.get()) return
        synchronized (SCHEMA_INITIALIZED) {
            if (SCHEMA_INITIALIZED.get()) return
            withSql { Sql sql ->
                createTablesIfNeeded(sql)
                runSchemaMigrations(sql)
                seedDefaultAccountsIfEmpty(sql)
            }
            SCHEMA_INITIALIZED.set(true)
        }
    }

    private void createTablesIfNeeded(Sql sql) {
        List<String> statements = [
            '''CREATE TABLE IF NOT EXISTS ''' + T_USERS + ''' (
                 id VARCHAR(64) NOT NULL PRIMARY KEY,
                 username VARCHAR(64) NOT NULL UNIQUE,
                 email VARCHAR(160) DEFAULT NULL,
                 password_hash VARCHAR(255) DEFAULT NULL,
                 display_name VARCHAR(120) NOT NULL,
                 bio TEXT DEFAULT NULL,
                 heritage_clan VARCHAR(120) DEFAULT NULL,
                 role VARCHAR(20) NOT NULL DEFAULT 'member',
                 timezone VARCHAR(64) NOT NULL DEFAULT 'America/Chicago',
                 created_at VARCHAR(32) NOT NULL
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_AUTH_TOKENS + ''' (
                 token VARCHAR(128) NOT NULL PRIMARY KEY,
                 user_id VARCHAR(64) NOT NULL,
                 expires_at BIGINT NOT NULL,
                 created_at VARCHAR(32) NOT NULL,
                 KEY idx_tsalagi_tokens_user (user_id),
                 CONSTRAINT fk_tsalagi_tokens_user FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_USER_STATS + ''' (
                 user_id VARCHAR(64) NOT NULL PRIMARY KEY,
                 xp INT NOT NULL DEFAULT 0,
                 daily_goal_xp INT NOT NULL DEFAULT 50,
                 gems INT NOT NULL DEFAULT 50,
                 hearts INT NOT NULL DEFAULT 5,
                 streak INT NOT NULL DEFAULT 0,
                 last_active_date VARCHAR(10) NOT NULL DEFAULT '',
                 streak_frozen TINYINT(1) NOT NULL DEFAULT 0,
                 freezes_remaining INT NOT NULL DEFAULT 1,
                 last_heart_regen_time BIGINT NOT NULL DEFAULT 0,
                 settings_json LONGTEXT NOT NULL,
                 CONSTRAINT fk_tsalagi_stats_user FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_USER_AWARDS + ''' (
                 user_id VARCHAR(64) NOT NULL,
                 award_code VARCHAR(64) NOT NULL,
                 unlocked_at VARCHAR(32) NOT NULL,
                 PRIMARY KEY (user_id, award_code),
                 CONSTRAINT fk_tsalagi_awards_user FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_DAILY_XP + ''' (
                 user_id VARCHAR(64) NOT NULL,
                 local_date CHAR(10) NOT NULL,
                 xp_earned INT NOT NULL DEFAULT 0,
                 goal_target_snapshot INT NOT NULL DEFAULT 50,
                 goal_reached_at VARCHAR(32) DEFAULT NULL,
                 PRIMARY KEY (user_id, local_date),
                 CONSTRAINT fk_tsalagi_daily_user FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_MISTAKES + ''' (
                 id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                 user_id VARCHAR(64) NOT NULL,
                 item_key VARCHAR(128) NOT NULL,
                 kind VARCHAR(20) NOT NULL,
                 word_id VARCHAR(64) DEFAULT NULL,
                 cherokee TEXT NOT NULL,
                 phonetics TEXT DEFAULT NULL,
                 english TEXT NOT NULL,
                 sentence_type VARCHAR(32) DEFAULT NULL,
                 optional_words_json LONGTEXT DEFAULT NULL,
                 red_herrings_json LONGTEXT DEFAULT NULL,
                 explanation TEXT DEFAULT NULL,
                 source_title VARCHAR(160) DEFAULT NULL,
                 mistake_count INT NOT NULL DEFAULT 1,
                 last_missed_at VARCHAR(32) NOT NULL,
                 resolved_at VARCHAR(32) DEFAULT NULL,
                 UNIQUE KEY uq_tsalagi_mistake (user_id, item_key),
                 KEY idx_tsalagi_mistakes_active (user_id, resolved_at, mistake_count),
                 CONSTRAINT fk_tsalagi_mistakes_user FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_COMPLETED_LESSONS + ''' (
                 user_id VARCHAR(64) NOT NULL,
                 lesson_id VARCHAR(64) NOT NULL,
                 completed_at CHAR(10) NOT NULL,
                 score INT NOT NULL DEFAULT 100,
                 high_accuracy TINYINT(1) NOT NULL DEFAULT 1,
                 PRIMARY KEY (user_id, lesson_id),
                 CONSTRAINT fk_tsalagi_comp_user FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_MASTERED_WORDS + ''' (
                 user_id VARCHAR(64) NOT NULL,
                 word_id VARCHAR(64) NOT NULL,
                 mastered_at VARCHAR(32) NOT NULL,
                 PRIMARY KEY (user_id, word_id),
                 CONSTRAINT fk_tsalagi_mast_user FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_GROUPS + ''' (
                 id VARCHAR(64) NOT NULL PRIMARY KEY,
                 name VARCHAR(140) NOT NULL,
                 description TEXT DEFAULT NULL,
                 owner_user_id VARCHAR(64) NOT NULL,
                 created_at VARCHAR(32) NOT NULL,
                 CONSTRAINT fk_tsalagi_grp_owner FOREIGN KEY (owner_user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_GROUP_MEMBERS + ''' (
                 group_id VARCHAR(64) NOT NULL,
                 user_id VARCHAR(64) NOT NULL,
                 added_at VARCHAR(32) NOT NULL,
                 PRIMARY KEY (group_id, user_id),
                 CONSTRAINT fk_tsalagi_gm_grp FOREIGN KEY (group_id) REFERENCES ''' + T_GROUPS + '''(id) ON DELETE CASCADE,
                 CONSTRAINT fk_tsalagi_gm_usr FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_WORDS + ''' (
                 id VARCHAR(64) NOT NULL PRIMARY KEY,
                 cherokee TEXT NOT NULL,
                 phonetics TEXT NOT NULL,
                 english TEXT NOT NULL,
                 category VARCHAR(32) NOT NULL,
                 audio_hint TEXT DEFAULT NULL,
                 tone_guide TEXT DEFAULT NULL,
                 audio_url LONGTEXT DEFAULT NULL,
                 image_url LONGTEXT DEFAULT NULL,
                 notes TEXT DEFAULT NULL,
                 breakdown_json LONGTEXT DEFAULT NULL,
                 sort_order INT NOT NULL DEFAULT 0
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_CONVERSATIONS + ''' (
                 id VARCHAR(64) NOT NULL PRIMARY KEY,
                 title VARCHAR(200) NOT NULL,
                 title_cherokee VARCHAR(200) DEFAULT NULL,
                 description TEXT NOT NULL,
                 scenario TEXT NOT NULL,
                 banner_image LONGTEXT DEFAULT NULL,
                 turns_json LONGTEXT NOT NULL,
                 sort_order INT NOT NULL DEFAULT 0
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_LESSONS + ''' (
                 id VARCHAR(64) NOT NULL PRIMARY KEY,
                 unit_id VARCHAR(64) NOT NULL,
                 title VARCHAR(200) NOT NULL,
                 title_cherokee VARCHAR(200) DEFAULT NULL,
                 description TEXT NOT NULL,
                 type VARCHAR(32) NOT NULL,
                 level VARCHAR(32) NOT NULL,
                 xp_reward INT NOT NULL DEFAULT 15,
                 word_ids_json LONGTEXT NOT NULL,
                 conversation_id VARCHAR(64) DEFAULT NULL,
                 sentence_challenges_json LONGTEXT DEFAULT NULL,
                 visual_reading_json LONGTEXT DEFAULT NULL,
                 reader_json LONGTEXT DEFAULT NULL,
                 custom_content_json LONGTEXT DEFAULT NULL,
                 image_url LONGTEXT DEFAULT NULL,
                 owner_user_id VARCHAR(64) DEFAULT NULL,
                 owner_name VARCHAR(120) DEFAULT NULL,
                 visibility VARCHAR(20) NOT NULL DEFAULT 'public',
                 sort_order INT NOT NULL DEFAULT 0
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_LESSON_USER_SHARES + ''' (
                 lesson_id VARCHAR(64) NOT NULL,
                 user_id VARCHAR(64) NOT NULL,
                 PRIMARY KEY (lesson_id, user_id),
                 CONSTRAINT fk_tsalagi_lus_l FOREIGN KEY (lesson_id) REFERENCES ''' + T_LESSONS + '''(id) ON DELETE CASCADE,
                 CONSTRAINT fk_tsalagi_lus_u FOREIGN KEY (user_id) REFERENCES ''' + T_USERS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_LESSON_GROUP_SHARES + ''' (
                 lesson_id VARCHAR(64) NOT NULL,
                 group_id VARCHAR(64) NOT NULL,
                 PRIMARY KEY (lesson_id, group_id),
                 CONSTRAINT fk_tsalagi_lgs_l FOREIGN KEY (lesson_id) REFERENCES ''' + T_LESSONS + '''(id) ON DELETE CASCADE,
                 CONSTRAINT fk_tsalagi_lgs_g FOREIGN KEY (group_id) REFERENCES ''' + T_GROUPS + '''(id) ON DELETE CASCADE
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_UNITS + ''' (
                 id VARCHAR(64) NOT NULL PRIMARY KEY,
                 unit_number INT NOT NULL,
                 title VARCHAR(200) NOT NULL,
                 title_cherokee VARCHAR(200) NOT NULL,
                 description TEXT NOT NULL,
                 banner_image LONGTEXT NOT NULL,
                 lesson_ids_json LONGTEXT NOT NULL,
                 accent_color VARCHAR(32) NOT NULL
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_SECTIONS + ''' (
                 id VARCHAR(64) NOT NULL PRIMARY KEY,
                 kind VARCHAR(32) NOT NULL,
                 title VARCHAR(200) NOT NULL,
                 payload_json LONGTEXT NOT NULL,
                 owner_user_id VARCHAR(64) DEFAULT NULL,
                 owner_name VARCHAR(120) DEFAULT NULL,
                 visibility VARCHAR(20) NOT NULL DEFAULT 'public',
                 created_at VARCHAR(32) NOT NULL
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci''',

            '''CREATE TABLE IF NOT EXISTS ''' + T_LESSON_SECTIONS + ''' (
                 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                 lesson_id VARCHAR(64) NOT NULL,
                 section_id VARCHAR(64) NOT NULL,
                 position INT NOT NULL DEFAULT 0,
                 CONSTRAINT fk_tsalagi_ls_lesson FOREIGN KEY (lesson_id) REFERENCES ''' + T_LESSONS + '''(id) ON DELETE CASCADE,
                 CONSTRAINT fk_tsalagi_ls_section FOREIGN KEY (section_id) REFERENCES ''' + T_SECTIONS + '''(id) ON DELETE CASCADE,
                 INDEX idx_tsalagi_ls_lesson (lesson_id, position),
                 INDEX idx_tsalagi_ls_section (section_id)
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci'''
        ]

        statements.each { String ddl ->
            sql.execute(ddl)
        }
    }

    private void ensureColumnExists(Sql sql, String tableName, String columnName, String definition) {
        def row = sql.firstRow(
            '''SELECT COUNT(*) AS cnt FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?''',
            [tableName, columnName]
        )
        if (asLong(row?.cnt, 0L) == 0L) {
            sql.execute("ALTER TABLE ${tableName} ADD COLUMN ${columnName} ${definition}".toString())
        }
    }

    private void runSchemaMigrations(Sql sql) {
        migrateLegacyLessonContent(sql)

        ensureColumnExists(sql, T_USERS, 'email', 'VARCHAR(160) DEFAULT NULL')
        ensureColumnExists(sql, T_USERS, 'password_hash', 'VARCHAR(255) DEFAULT NULL')
        ensureColumnExists(sql, T_USERS, 'bio', 'TEXT DEFAULT NULL')
        ensureColumnExists(sql, T_USERS, 'heritage_clan', 'VARCHAR(120) DEFAULT NULL')

        ensureColumnExists(sql, T_LESSONS, 'owner_user_id', 'VARCHAR(64) DEFAULT NULL')
        ensureColumnExists(sql, T_LESSONS, 'owner_name', 'VARCHAR(120) DEFAULT NULL')
        ensureColumnExists(sql, T_LESSONS, 'visibility', "VARCHAR(20) NOT NULL DEFAULT 'public'")
        ensureColumnExists(sql, T_LESSONS, 'reader_json', 'LONGTEXT DEFAULT NULL')
        ensureColumnExists(sql, T_LESSONS, 'image_url', 'LONGTEXT DEFAULT NULL')

        // Ensure all users have admin role so everyone can edit lessons
        sql.executeUpdate('UPDATE ' + T_USERS + " SET role = 'admin'")

        sql.rows("SELECT id, username FROM " + T_USERS + " WHERE password_hash IS NULL OR password_hash = ''").each { r ->
            String uname = asString(r.username) ?: ''
            String defaultPw = (uname in ['teacher', 'admin']) ? 'admin123' : (uname == 'sequoyah' ? 'editor123' : 'welcome123')
            sql.executeUpdate("UPDATE " + T_USERS + " SET password_hash = ? WHERE id = ?", [hashPasswordPbkdf2(defaultPw), r.id])
        }

        sql.execute('DELETE FROM ' + T_AUTH_TOKENS + ' WHERE expires_at < ?', [System.currentTimeMillis()])
    }

    /**
     * One-time migration: converts pre-section lessons (content stored in per-slot
     * JSON columns) into standalone Sections linked via tsalagi_lesson_sections.
     * Lessons that already have section links are left untouched, so this is idempotent.
     */
    private void migrateLegacyLessonContent(Sql sql) {
        List<Map> legacyRows = sql.rows(
            'SELECT l.* FROM ' + T_LESSONS + ' l WHERE NOT EXISTS (SELECT 1 FROM ' + T_LESSON_SECTIONS + ' ls WHERE ls.lesson_id = l.id)'
        )
        legacyRows.each { r ->
            String lessonId = asString(r.id)
            List wordIds = parseJsonSafe(asString(r.word_ids_json), [])
            boolean hasLegacyContent =
                (wordIds as List)?.any { it } ||
                asString(r.sentence_challenges_json) ||
                asString(r.visual_reading_json) ||
                asString(r.reader_json) ||
                asString(r.custom_content_json) ||
                asString(r.conversation_id)
            if (!hasLegacyContent) return

            String ownerUserId = asString(r.owner_user_id)
            String ownerName = asString(r.owner_name)
            String visibility = asString(r.visibility) == 'restricted' ? 'restricted' : 'public'
            String nowIso = Instant.now().toString()
            List<Map> synthesized = []

            List<String> wordIdList = (wordIds as List)?.findAll { it }?.collect { it.toString() } ?: []
            if (wordIdList) {
                synthesized << [kind: 'flashcards', title: 'Vocabulary Flashcards',
                                payload: [kind: 'flashcards', wordIds: wordIdList]]
            }

            List challenges = parseJsonSafe(asString(r.sentence_challenges_json), null) as List
            if (challenges) {
                synthesized << [kind: 'sentence-scramble', title: 'Sentence Scramble',
                                payload: [kind: 'sentence-scramble', challenges: challenges]]
            }

            def visualReading = parseJsonSafe(asString(r.visual_reading_json), null)
            if (visualReading) {
                synthesized << [kind: 'visual-reading', title: 'Visual Reading',
                                payload: [kind: 'visual-reading', content: visualReading]]
            }

            def reader = parseJsonSafe(asString(r.reader_json), null)
            if (reader) {
                synthesized << [kind: 'reading', title: 'Reading Practice',
                                payload: [kind: 'reading', content: reader, showSyllabary: true]]
            }

            String conversationId = asString(r.conversation_id)
            if (conversationId) {
                synthesized << [kind: 'conversation', title: 'Conversation Practice',
                                payload: [kind: 'conversation', conversationId: conversationId]]
            }

            def customPage = parseJsonSafe(asString(r.custom_content_json), null)
            if (customPage) {
                synthesized << [kind: 'custom-page',
                                title: asString(customPage.pageTitle) ?: 'Lesson Page',
                                payload: [kind: 'custom-page', content: customPage]]
            }

            if (!synthesized) return

            sql.withTransaction {
                synthesized.eachWithIndex { Map entry, int idx ->
                    String sectionId = 'sec-legacy-' + lessonId + '-' + entry.kind
                    sql.executeUpdate(
                        'INSERT IGNORE INTO ' + T_SECTIONS + ' ' +
                        '(id, kind, title, payload_json, owner_user_id, owner_name, visibility, created_at) ' +
                        'VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
                        [sectionId, entry.kind, entry.title, JsonOutput.toJson(entry.payload),
                         ownerUserId, ownerName, visibility, nowIso]
                    )
                    sql.executeInsert(
                        'INSERT INTO ' + T_LESSON_SECTIONS + ' (lesson_id, section_id, position) VALUES (?, ?, ?)',
                        [lessonId, sectionId, idx]
                    )
                }
            }
        }
    }

    private void seedDefaultAccountsIfEmpty(Sql sql) {
        def row = sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_USERS)
        long count = asLong(row?.cnt, 0L)
        String nowIso = Instant.now().toString()

        if (count == 0L) {
            insertAccountWithZeroedStats(sql, [
                id          : 'u-teacher-default',
                username    : 'teacher',
                email       : 'admin@tsalagi.local',
                displayName : 'Instructor (Admin)',
                bio         : 'Administrator and curriculum steward for Tsalagi language revitalization.',
                heritageClan: 'Aniwaya (Wolf Clan)',
                role        : 'admin',
                timezone    : 'America/Chicago',
                createdAt   : nowIso
            ], hashPasswordPbkdf2('admin123'))

            insertAccountWithZeroedStats(sql, [
                id          : 'u-editor-sequoyah',
                username    : 'sequoyah',
                email       : 'editor@tsalagi.local',
                displayName : 'Sequoyah (Editor)',
                bio         : 'Curriculum author creating syllabary, conversation, and visual reading lessons.',
                heritageClan: 'Anigilohi (Long Hair Clan)',
                role        : 'editor',
                timezone    : 'America/Chicago',
                createdAt   : nowIso
            ], hashPasswordPbkdf2('editor123'))

            insertAccountWithZeroedStats(sql, [
                id          : 'u-student-awi',
                username    : 'awi',
                email       : 'awi@tsalagi.local',
                displayName : 'Awi (Member)',
                bio         : 'Learning Cherokee greetings, animal vocabulary, and conversational phrases.',
                heritageClan: 'Anikawi (Deer Clan)',
                role        : 'member',
                timezone    : 'America/Chicago',
                createdAt   : nowIso
            ], hashPasswordPbkdf2('welcome123'))
        } else {
            def seqRow = sql.firstRow("SELECT id FROM " + T_USERS + " WHERE username = 'sequoyah'")
            if (!seqRow) {
                insertAccountWithZeroedStats(sql, [
                    id          : 'u-editor-sequoyah',
                    username    : 'sequoyah',
                    email       : 'editor@tsalagi.local',
                    displayName : 'Sequoyah (Editor)',
                    bio         : 'Curriculum author creating syllabary, conversation, and visual reading lessons.',
                    heritageClan: 'Anigilohi (Long Hair Clan)',
                    role        : 'editor',
                    timezone    : 'America/Chicago',
                    createdAt   : nowIso
                ], hashPasswordPbkdf2('editor123'))
            }
        }

        def grpCnt = sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_GROUPS)
        if (asLong(grpCnt?.cnt, 0L) == 0L) {
            def adminRow = sql.firstRow("SELECT id FROM " + T_USERS + " WHERE role = 'admin' ORDER BY created_at ASC LIMIT 1")
            def awiRow = sql.firstRow("SELECT id FROM " + T_USERS + " WHERE username = 'awi' LIMIT 1")
            if (adminRow) {
                sql.executeInsert(
                    'INSERT INTO ' + T_GROUPS + ' (id, name, description, owner_user_id, created_at) VALUES (?, ?, ?, ?, ?)',
                    ['grp-beginners', 'Beginners', 'Foundational Cherokee syllabary, greetings, and core conversational phrases.', adminRow.id, nowIso]
                )
                sql.executeInsert(
                    'INSERT INTO ' + T_GROUPS + ' (id, name, description, owner_user_id, created_at) VALUES (?, ?, ?, ?, ?)',
                    ['grp-intermediate-fall-2026', 'Intermediate Fall 2026', 'Fall 2026 cohort focusing on sentence building, tone, and storytelling.', adminRow.id, nowIso]
                )
                if (awiRow) {
                    sql.executeUpdate(
                        'INSERT IGNORE INTO ' + T_GROUP_MEMBERS + ' (group_id, user_id, added_at) VALUES (?, ?, ?)',
                        ['grp-beginners', awiRow.id, nowIso]
                    )
                }
            }
        }
    }

    private void insertAccountWithZeroedStats(Sql sql, Map account, String passwordHash) {
        Map defaultSettings = [
            showPhonetics     : true,
            showAudioGuides   : true,
            enableSoundEffects: true
        ]
        sql.withTransaction {
            sql.executeInsert(
                'INSERT INTO ' + T_USERS +
                ' (id, username, email, password_hash, display_name, bio, heritage_clan, role, timezone, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                [
                    account.id,
                    account.username,
                    account.email ?: null,
                    passwordHash,
                    account.displayName,
                    account.bio ?: null,
                    account.heritageClan ?: null,
                    account.role,
                    account.timezone,
                    account.createdAt
                ]
            )
            sql.executeInsert(
                'INSERT INTO ' + T_USER_STATS +
                ' (user_id, xp, daily_goal_xp, gems, hearts, streak, last_active_date, streak_frozen, freezes_remaining, last_heart_regen_time, settings_json) VALUES (?, 0, 50, 50, 5, 0, \'\', 0, 1, ?, ?)',
                [account.id, System.currentTimeMillis(), JsonOutput.toJson(defaultSettings)]
            )
        }
    }

    private String normalizeRole(String rawRole) {
        String r = (rawRole ?: '').trim().toLowerCase()
        if (r == 'admin') return 'admin'
        if (r == 'editor' || r == 'teacher') return 'editor'
        return 'member'
    }

    private String hashPasswordPbkdf2(String password, byte[] customSalt = null) {
        byte[] salt = customSalt
        if (salt == null) {
            salt = new byte[16]
            SECURE_RANDOM.nextBytes(salt)
        }
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, 256)
        byte[] derived = SecretKeyFactory.getInstance('PBKDF2WithHmacSHA256').generateSecret(spec).encoded
        return "pbkdf2\$${PBKDF2_ITERATIONS}\$${bytesToHex(salt)}\$${bytesToHex(derived)}"
    }

    private boolean verifyPasswordPbkdf2(String password, String storedHash) {
        if (!storedHash || !storedHash.startsWith('pbkdf2$')) return false
        String[] parts = storedHash.split('\\$')
        if (parts.length != 4) return false
        int iterations = asInt(parts[1], 0)
        byte[] salt = hexToBytes(parts[2])
        byte[] expected = hexToBytes(parts[3])
        if (iterations <= 0 || salt.length == 0 || expected.length == 0) return false

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256)
        byte[] actual = SecretKeyFactory.getInstance('PBKDF2WithHmacSHA256').generateSecret(spec).encoded
        return MessageDigest.isEqual(actual, expected)
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2)
        for (byte b : bytes) {
            sb.append(String.format('%02x', b & 0xff))
        }
        return sb.toString()
    }

    private byte[] hexToBytes(String hex) {
        if (!hex || hex.length() % 2 != 0) return new byte[0]
        int len = hex.length()
        byte[] out = new byte[len / 2]
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16))
        }
        return out
    }

    private String issueAuthToken(Sql sql, String userId) {
        byte[] rand = new byte[32]
        SECURE_RANDOM.nextBytes(rand)
        String token = "tsl_${bytesToHex(rand)}".toString()
        long now = System.currentTimeMillis()
        sql.executeInsert(
            'INSERT INTO ' + T_AUTH_TOKENS + ' (token, user_id, expires_at, created_at) VALUES (?, ?, ?, ?)',
            [token, userId, now + TOKEN_TTL_MS, Instant.ofEpochMilli(now).toString()]
        )
        return token
    }

    private Map resolveUserByToken(Sql sql, String token, String timezone) {
        if (!token) return null
        def tokRow = sql.firstRow('SELECT user_id, expires_at FROM ' + T_AUTH_TOKENS + ' WHERE token = ?', [token])
        if (!tokRow) return null
        if (asLong(tokRow.expires_at, 0L) < System.currentTimeMillis()) {
            sql.execute('DELETE FROM ' + T_AUTH_TOKENS + ' WHERE token = ?', [token])
            return null
        }

        def userRow = sql.firstRow('SELECT * FROM ' + T_USERS + ' WHERE id = ?', [asString(tokRow.user_id)])
        if (!userRow) return null

        if (timezone && timezone != asString(userRow.timezone)) {
            sql.executeUpdate('UPDATE ' + T_USERS + ' SET timezone = ? WHERE id = ?', [timezone, userRow.id])
            userRow.timezone = timezone
        }
        return mapUserRow(userRow)
    }

    private Map requireAuth(Sql sql) {
        def (String token, String tz) = readAuthHeaders()
        Map user = resolveUserByToken(sql, token, tz)
        if (!user) {
            renderError(401, 'Authentication required. Please sign in.')
            return null
        }
        return user
    }

    private Map requireEditorOrAdmin(Sql sql) {
        Map user = requireAuth(sql)
        if (!user) return null
        if (!(user.role in ['admin', 'editor'])) {
            renderError(403, 'Editor or Admin privileges are required for shared curriculum changes.')
            return null
        }
        return user
    }

    private Map requireAdmin(Sql sql) {
        Map user = requireAuth(sql)
        if (!user) return null
        if (user.role != 'admin') {
            renderError(403, 'Admin privileges are required.')
            return null
        }
        return user
    }

    private Map mapUserRow(Object r) {
        return [
            id          : asString(r.id),
            username    : asString(r.username),
            email       : asString(r.email),
            displayName : asString(r.display_name),
            bio         : asString(r.bio),
            heritageClan: asString(r.heritage_clan),
            role        : normalizeRole(asString(r.role)),
            timezone    : asString(r.timezone) ?: 'America/Chicago',
            createdAt   : asString(r.created_at)
        ].findAll { unused9, v -> v != null }
    }

    private List<Map> listDirectoryUsersInternal(Sql sql) {
        return sql.rows(
            'SELECT id, username, display_name, role FROM ' + T_USERS + " ORDER BY CASE role WHEN 'admin' THEN 0 WHEN 'editor' THEN 1 ELSE 2 END, display_name ASC"
        ).collect {
            [
                id         : asString(it.id),
                username   : asString(it.username),
                displayName: asString(it.display_name),
                role       : normalizeRole(asString(it.role))
            ]
        }
    }

    private List<Map> listAdminUsersInternal(Sql sql) {
        return sql.rows(
            'SELECT u.*, ' +
            'COALESCE(s.xp, 0) AS xp, ' +
            'COALESCE(s.streak, 0) AS streak, ' +
            'COALESCE(s.gems, 50) AS gems, ' +
            '(SELECT COUNT(*) FROM ' + T_COMPLETED_LESSONS + ' c WHERE c.user_id = u.id) AS completed_cnt, ' +
            '(SELECT COUNT(*) FROM ' + T_LESSONS + ' l WHERE l.owner_user_id = u.id) AS created_cnt ' +
            'FROM ' + T_USERS + ' u ' +
            'LEFT JOIN ' + T_USER_STATS + ' s ON s.user_id = u.id ' +
            "ORDER BY CASE u.role WHEN 'admin' THEN 0 WHEN 'editor' THEN 1 ELSE 2 END, u.created_at ASC"
        ).collect { r ->
            Map base = mapUserRow(r)
            base.xp = asInt(r.xp, 0)
            base.streak = asInt(r.streak, 0)
            base.gems = asInt(r.gems, 50)
            base.completedLessonCount = asInt(r.completed_cnt, 0)
            base.createdLessonCount = asInt(r.created_cnt, 0)
            return base
        }
    }

    private Map hydrateGroupRow(Sql sql, Object groupRow) {
        String groupId = asString(groupRow.id)
        def ownerRow = sql.firstRow('SELECT display_name FROM ' + T_USERS + ' WHERE id = ?', [asString(groupRow.owner_user_id)])
        List<Map> members = sql.rows(
            'SELECT u.id, u.username, u.display_name, u.role ' +
            'FROM ' + T_GROUP_MEMBERS + ' gm ' +
            'JOIN ' + T_USERS + ' u ON u.id = gm.user_id ' +
            'WHERE gm.group_id = ? ' +
            'ORDER BY u.display_name ASC',
            [groupId]
        ).collect { m ->
            [
                id         : asString(m.id),
                username   : asString(m.username),
                displayName: asString(m.display_name),
                role       : normalizeRole(asString(m.role))
            ]
        }
        def shareCnt = sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_LESSON_GROUP_SHARES + ' WHERE group_id = ?', [groupId])

        return [
            id               : groupId,
            name             : asString(groupRow.name),
            description      : asString(groupRow.description) ?: '',
            ownerUserId      : asString(groupRow.owner_user_id),
            ownerName        : asString(ownerRow?.display_name) ?: 'Unknown',
            memberIds        : members.collect { it.id },
            members          : members,
            sharedLessonCount: asInt(shareCnt?.cnt, 0),
            createdAt        : asString(groupRow.created_at)
        ]
    }

    private List<Map> listGroupsForUserInternal(Sql sql, Map viewer) {
        if (!viewer) return []
        List rows
        if (viewer.role == 'admin') {
            rows = sql.rows('SELECT * FROM ' + T_GROUPS + ' ORDER BY created_at DESC')
        } else {
            rows = sql.rows(
                'SELECT DISTINCT g.* ' +
                'FROM ' + T_GROUPS + ' g ' +
                'LEFT JOIN ' + T_GROUP_MEMBERS + ' gm ON gm.group_id = g.id ' +
                'WHERE g.owner_user_id = ? OR gm.user_id = ? ' +
                'ORDER BY g.created_at DESC',
                [viewer.id, viewer.id]
            )
        }
        return rows.collect { hydrateGroupRow(sql, it) }
    }

    private void syncLessonShares(Sql sql, String lessonId, List<String> userIds, List<String> groupIds) {
        sql.execute('DELETE FROM ' + T_LESSON_USER_SHARES + ' WHERE lesson_id = ?', [lessonId])
        sql.execute('DELETE FROM ' + T_LESSON_GROUP_SHARES + ' WHERE lesson_id = ?', [lessonId])

        if (userIds) {
            userIds.unique().each { String uid ->
                if (sql.firstRow('SELECT id FROM ' + T_USERS + ' WHERE id = ?', [uid])) {
                    sql.executeUpdate(
                        'INSERT IGNORE INTO ' + T_LESSON_USER_SHARES + ' (lesson_id, user_id) VALUES (?, ?)',
                        [lessonId, uid]
                    )
                }
            }
        }
        if (groupIds) {
            groupIds.unique().each { String gid ->
                if (sql.firstRow('SELECT id FROM ' + T_GROUPS + ' WHERE id = ?', [gid])) {
                    sql.executeUpdate(
                        'INSERT IGNORE INTO ' + T_LESSON_GROUP_SHARES + ' (lesson_id, group_id) VALUES (?, ?)',
                        [lessonId, gid]
                    )
                }
            }
        }
    }

    private Map hydrateLessonRow(Sql sql, Object r) {
        String lessonId = asString(r.id)
        List<String> sectionIds = sql.rows(
            'SELECT section_id FROM ' + T_LESSON_SECTIONS + ' WHERE lesson_id = ? ORDER BY position ASC, id ASC',
            [lessonId]
        ).collect { asString(it.section_id) }
        List<String> uShares = sql.rows('SELECT user_id FROM ' + T_LESSON_USER_SHARES + ' WHERE lesson_id = ?', [lessonId]).collect { asString(it.user_id) }
        List<String> gShares = sql.rows('SELECT group_id FROM ' + T_LESSON_GROUP_SHARES + ' WHERE lesson_id = ?', [lessonId]).collect { asString(it.group_id) }
        String vis = asString(r.visibility) == 'restricted' ? 'restricted' : 'public'

        return [
            id                  : lessonId,
            unitId              : asString(r.unit_id),
            title               : asString(r.title),
            titleCherokee       : asString(r.title_cherokee),
            description         : asString(r.description),
            level               : asString(r.level),
            xpReward            : asInt(r.xp_reward, 15),
            sectionIds          : sectionIds,
            imageUrl            : asString(r.image_url),
            ownerUserId         : asString(r.owner_user_id),
            ownerName           : asString(r.owner_name),
            visibility          : vis,
            sharedWithUserIds   : uShares,
            sharedWithGroupIds  : gShares
        ].findAll { unused10, v -> v != null }
    }

    /* ------------------------------------------------------------------------ */
    /* SECTIONS: STANDALONE REUSABLE LESSON BUILDING BLOCKS                     */
    /* ------------------------------------------------------------------------ */
    private Map hydrateSectionRow(Object r) {
        String kind = asString(r.kind) ?: 'flashcards'
        Map payload = (parseJsonSafe(asString(r.payload_json), [:]) as Map) ?: [:]
        payload.kind = kind
        Map out = [
            id          : asString(r.id),
            kind        : kind,
            title       : asString(r.title),
            payload     : payload,
            ownerUserId : asString(r.owner_user_id),
            ownerName   : asString(r.owner_name),
            visibility  : asString(r.visibility) == 'restricted' ? 'restricted' : 'public',
            createdAt   : asString(r.created_at)
        ]
        return out.findAll { k, v -> v != null || k == 'payload' }
    }

    private boolean canViewerAccessSection(Map section, Map viewer) {
        if (!section.visibility || section.visibility == 'public') return true
        if (!viewer) return false
        if (viewer.role == 'admin') return true
        return asString(section.ownerUserId) == asString(viewer.id)
    }

    private List<Map> listSectionsInternal(Sql sql, Map viewer) {
        sql.rows('SELECT * FROM ' + T_SECTIONS + ' ORDER BY created_at DESC, id ASC').collect { hydrateSectionRow(it) }
            .findAll { canViewerAccessSection(it, viewer) }
    }

    private Map upsertSectionInternal(Sql sql, Map section, Map user) {
        String sectionId = asString(section.id)
        if (!sectionId || !asString(section.kind) || !asString(section.title) || section.payload == null) {
            throw new IllegalArgumentException('Valid section (id, kind, title, payload) is required.')
        }

        def existing = sql.firstRow('SELECT * FROM ' + T_SECTIONS + ' WHERE id = ?', [sectionId])
        if (existing) {
            String prevOwner = asString(existing.owner_user_id)
            boolean canEdit = (user.role == 'admin') ||
                (user.role == 'editor' && !prevOwner) ||
                (prevOwner == user.id)
            if (!canEdit) {
                throw new IllegalAccessException('You do not have permission to edit this section.')
            }
        }

        String visibility = asString(section.visibility) == 'restricted' ? 'restricted' : 'public'
        String nowIso = Instant.now().toString()
        String ownerUserId = existing ? asString(existing.owner_user_id) : (String) user.id
        String ownerName = existing ? asString(existing.owner_name) : (String) user.displayName
        String createdAt = existing ? asString(existing.created_at) : nowIso

        sql.executeUpdate(
            'INSERT INTO ' + T_SECTIONS + ' ' +
            '(id, kind, title, payload_json, owner_user_id, owner_name, visibility, created_at) ' +
            'VALUES (?, ?, ?, ?, ?, ?, ?, ?) ' +
            'ON DUPLICATE KEY UPDATE ' +
            'kind = VALUES(kind), title = VALUES(title), payload_json = VALUES(payload_json), visibility = VALUES(visibility)',
            [sectionId, asString(section.kind), asString(section.title), JsonOutput.toJson(section.payload),
             ownerUserId, ownerName, visibility, createdAt]
        )

        def savedRow = sql.firstRow('SELECT * FROM ' + T_SECTIONS + ' WHERE id = ?', [sectionId])
        return hydrateSectionRow(savedRow)
    }

    private void deleteSectionInternal(Sql sql, String sectionId, Map user) {
        def existing = sql.firstRow('SELECT owner_user_id FROM ' + T_SECTIONS + ' WHERE id = ?', [sectionId])
        if (!existing) return
        String prevOwner = asString(existing.owner_user_id)
        boolean canDelete = (user.role == 'admin') ||
            (user.role == 'editor' && !prevOwner) ||
            (prevOwner == user.id)
        if (!canDelete) {
            throw new IllegalAccessException('Only the section creator or an Admin can delete this section.')
        }

        def usageRow = sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_LESSON_SECTIONS + ' WHERE section_id = ?', [sectionId])
        long usage = asLong(usageRow?.cnt, 0L)
        if (usage > 0L) {
            throw new IllegalStateException('This section is still used by ' + usage + ' lesson(s). Remove it from those lessons first.')
        }

        sql.executeUpdate('DELETE FROM ' + T_SECTIONS + ' WHERE id = ?', [sectionId])
    }

    /** Replaces the lesson's ordered section links with the given sectionIds. */
    private void syncLessonSections(Sql sql, String lessonId, List sectionIds) {
        sql.executeUpdate('DELETE FROM ' + T_LESSON_SECTIONS + ' WHERE lesson_id = ?', [lessonId])
        if (!sectionIds) return
        List<String> ids = (sectionIds as List).collect { asString(it) }.findAll { it }.unique()
        ids.eachWithIndex { String sid, int idx ->
            if (sql.firstRow('SELECT id FROM ' + T_SECTIONS + ' WHERE id = ?', [sid])) {
                sql.executeInsert(
                    'INSERT INTO ' + T_LESSON_SECTIONS + ' (lesson_id, section_id, position) VALUES (?, ?, ?)',
                    [lessonId, sid, idx]
                )
            }
        }
    }

    private boolean canViewerAccessLesson(Map lesson, Map viewer, Set<String> viewerGroupIds) {
        if (!lesson.visibility || lesson.visibility == 'public') return true
        if (!viewer) return false
        if (viewer.role == 'admin') return true
        if (lesson.ownerUserId && lesson.ownerUserId == viewer.id) return true
        if ((lesson.sharedWithUserIds as Collection)?.contains(viewer.id)) return true
        if ((lesson.sharedWithGroupIds as Collection)?.any { viewerGroupIds.contains(it.toString()) }) return true
        return false
    }

    private List<Map> getUserAwards(Sql sql, String userId) {
        Map<String, String> unlockedMap = [:]
        sql.rows('SELECT award_code, unlocked_at FROM ' + T_USER_AWARDS + ' WHERE user_id = ?', [userId]).each { r ->
            unlockedMap[asString(r.award_code)] = asString(r.unlocked_at)
        }
        return AWARD_DEFINITIONS.collect { Map awardDef ->
            String code = (String) awardDef.code
            Map item = new LinkedHashMap(awardDef)
            item.unlocked = unlockedMap.containsKey(code)
            if (unlockedMap.containsKey(code)) {
                item.unlockedAt = unlockedMap[code]
            }
            return item
        }
    }

    private List<Map> evaluateAndUnlockAwards(Sql sql, String userId, String nowIso) {
        Set<String> already = sql.rows('SELECT award_code FROM ' + T_USER_AWARDS + ' WHERE user_id = ?', [userId])
            .collect { asString(it.award_code) } as Set<String>

        def stats = sql.firstRow('SELECT streak, gems FROM ' + T_USER_STATS + ' WHERE user_id = ?', [userId])
        if (!stats) return []

        List compRows = sql.rows('SELECT score FROM ' + T_COMPLETED_LESSONS + ' WHERE user_id = ?', [userId])
        long masteredCnt = asLong(sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_MASTERED_WORDS + ' WHERE user_id = ?', [userId])?.cnt, 0L)
        long goalCnt = asLong(sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_DAILY_XP + ' WHERE user_id = ? AND goal_reached_at IS NOT NULL', [userId])?.cnt, 0L)
        long authoredCnt = asLong(sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_LESSONS + ' WHERE owner_user_id = ?', [userId])?.cnt, 0L)
        long groupsCnt = asLong(sql.firstRow('SELECT COUNT(*) AS cnt FROM ' + T_GROUPS + ' WHERE owner_user_id = ?', [userId])?.cnt, 0L)

        Set<String> eligible = [] as Set<String>
        if (compRows.size() >= 1) eligible.add('first_step')
        if (compRows.size() >= 5) eligible.add('five_lessons')
        if (compRows.any { asInt(it.score, 0) >= 100 }) eligible.add('sharp_archer')
        if (goalCnt >= 1L) eligible.add('daily_keeper')
        if (asInt(stats.streak, 0) >= 7) eligible.add('sacred_seven_streak')
        if (masteredCnt >= 7L) eligible.add('syllabary_scholar')
        if (masteredCnt >= 20L) eligible.add('word_weaver')
        if (authoredCnt >= 1L) eligible.add('lesson_author')
        if (groupsCnt >= 1L) eligible.add('group_founder')

        List<Map> newlyUnlocked = []
        int totalBonus = 0
        AWARD_DEFINITIONS.each { Map awardDef ->
            String code = (String) awardDef.code
            if (eligible.contains(code) && !already.contains(code)) {
                sql.executeUpdate(
                    'INSERT IGNORE INTO ' + T_USER_AWARDS + ' (user_id, award_code, unlocked_at) VALUES (?, ?, ?)',
                    [userId, code, nowIso]
                )
                int b = asInt(awardDef.gemsBonus, 0)
                totalBonus += b
                Map item = new LinkedHashMap(awardDef)
                item.unlocked = true
                item.unlockedAt = nowIso
                newlyUnlocked.add(item)
            }
        }

        if (totalBonus > 0) {
            sql.executeUpdate('UPDATE ' + T_USER_STATS + ' SET gems = gems + ? WHERE user_id = ?', [totalBonus, userId])
        }
        return newlyUnlocked
    }

    private Map buildGuestProgress() {
        return [
            streak            : 0,
            lastActiveDate    : '',
            streakFrozen      : false,
            freezesRemaining  : 1,
            xp                : 0,
            dailyGoalXp       : 50,
            todayXp           : 0,
            gems              : 50,
            hearts            : 5,
            lastHeartRegenTime: System.currentTimeMillis(),
            completedLessons  : [:],
            masteredWords     : [],
            mistakes          : [],
            awards            : AWARD_DEFINITIONS.collect { Map d ->
                Map item = new LinkedHashMap(d)
                item.unlocked = false
                return item
            },
            history           : [],
            settings          : [showPhonetics: true, showAudioGuides: true, enableSoundEffects: true, enableAudioRecording: false]
        ]
    }

    private void reconcileStreakForToday(Sql sql, String userId, String localToday) {
        def stats = sql.firstRow(
            'SELECT streak, last_active_date, streak_frozen, freezes_remaining FROM ' + T_USER_STATS + ' WHERE user_id = ?',
            [userId]
        )
        String lastActive = asString(stats?.last_active_date)
        if (!stats || !lastActive) return

        long dayGap = diffCalendarDays(lastActive, localToday)
        if (dayGap <= 1L) return

        int missedDays = (dayGap - 1L) as int
        int streak = asInt(stats.streak, 0)
        boolean frozen = asBool(stats.streak_frozen, false)
        int freezesRem = asInt(stats.freezes_remaining, 0)

        if (streak > 0 && frozen && freezesRem >= missedDays) {
            int remaining = freezesRem - missedDays
            String yesterday = LocalDate.parse(localToday).minusDays(1).toString()
            sql.executeUpdate(
                'UPDATE ' + T_USER_STATS + ' SET freezes_remaining = ?, streak_frozen = ?, last_active_date = ? WHERE user_id = ?',
                [remaining, remaining > 0 ? 1 : 0, yesterday, userId]
            )
        } else if (streak > 0) {
            sql.executeUpdate('UPDATE ' + T_USER_STATS + ' SET streak = 0 WHERE user_id = ?', [userId])
        }
    }

    private List<Map> getActiveMistakes(Sql sql, String userId) {
        return sql.rows(
            'SELECT item_key, kind, word_id, cherokee, phonetics, english, sentence_type, optional_words_json, red_herrings_json, explanation, source_title, mistake_count, last_missed_at FROM ' +
            T_MISTAKES + ' WHERE user_id = ? AND resolved_at IS NULL ORDER BY mistake_count DESC, last_missed_at DESC',
            [userId]
        ).collect { r ->
            [
                id           : asString(r.item_key),
                kind         : asString(r.kind) == 'word' ? 'word' : 'sentence',
                wordId       : asString(r.word_id),
                cherokee     : asString(r.cherokee),
                phonetics    : asString(r.phonetics),
                english      : asString(r.english),
                sentenceType : asString(r.sentence_type),
                optionalWords: parseJsonSafe(asString(r.optional_words_json), null),
                redHerrings  : parseJsonSafe(asString(r.red_herrings_json), null),
                explanation  : asString(r.explanation),
                sourceTitle  : asString(r.source_title),
                mistakeCount : asInt(r.mistake_count, 1),
                lastMissedAt : asString(r.last_missed_at)
            ].findAll { unused11, v -> v != null }
        }
    }

    private Map assembleUserProgress(Sql sql, String userId, String timezone) {
        String localToday = getLocalTodayDate(timezone)
        reconcileStreakForToday(sql, userId, localToday)
        evaluateAndUnlockAwards(sql, userId, Instant.now().toString())

        def stats = sql.firstRow('SELECT * FROM ' + T_USER_STATS + ' WHERE user_id = ?', [userId])
        if (!stats) {
            return buildGuestProgress()
        }

        def todayRow = sql.firstRow(
            'SELECT xp_earned FROM ' + T_DAILY_XP + ' WHERE user_id = ? AND local_date = ?',
            [userId, localToday]
        )

        List<Map> historyRows = sql.rows(
            'SELECT local_date, xp_earned FROM ' + T_DAILY_XP + ' WHERE user_id = ? ORDER BY local_date DESC LIMIT 30',
            [userId]
        ).collect { [date: asString(it.local_date), xp: asInt(it.xp_earned, 0)] }.reverse()

        Map completedLessons = [:]
        sql.rows(
            'SELECT lesson_id, completed_at, score, high_accuracy FROM ' + T_COMPLETED_LESSONS + ' WHERE user_id = ?',
            [userId]
        ).each { r ->
            completedLessons[asString(r.lesson_id)] = [
                completedAt : asString(r.completed_at),
                score       : asInt(r.score, 100),
                highAccuracy: asBool(r.high_accuracy, true)
            ]
        }

        List<String> masteredWords = sql.rows(
            'SELECT word_id FROM ' + T_MASTERED_WORDS + ' WHERE user_id = ?',
            [userId]
        ).collect { asString(it.word_id) }

        Map mergedSettings = [showPhonetics: true, showAudioGuides: true, enableSoundEffects: true, enableAudioRecording: false]
        Object parsedSettings = parseJsonSafe(asString(stats.settings_json), [:])
        if (parsedSettings instanceof Map) {
            mergedSettings.putAll((Map) parsedSettings)
        }

        return [
            streak            : asInt(stats.streak, 0),
            lastActiveDate    : asString(stats.last_active_date) ?: '',
            streakFrozen      : asBool(stats.streak_frozen, false),
            freezesRemaining  : asInt(stats.freezes_remaining, 1),
            xp                : asInt(stats.xp, 0),
            dailyGoalXp       : Math.max(5, asInt(stats.daily_goal_xp, 50)),
            todayXp           : asInt(todayRow?.xp_earned, 0),
            gems              : asInt(stats.gems, 50),
            hearts            : asInt(stats.hearts, 5),
            lastHeartRegenTime: asLong(stats.last_heart_regen_time, System.currentTimeMillis()),
            completedLessons  : completedLessons,
            masteredWords     : masteredWords,
            mistakes          : getActiveMistakes(sql, userId),
            awards            : getUserAwards(sql, userId),
            history           : historyRows,
            settings          : mergedSettings
        ]
    }

    private Map getCurriculumInternal(Sql sql, Map viewer) {
        List<Map> words = sql.rows('SELECT * FROM ' + T_WORDS + ' ORDER BY sort_order ASC').collect { r ->
            [
                id       : asString(r.id),
                cherokee : asString(r.cherokee),
                phonetics: asString(r.phonetics),
                english  : asString(r.english),
                category : asString(r.category) ?: 'custom',
                audioHint: asString(r.audio_hint),
                toneGuide: asString(r.tone_guide),
                audioUrl : asString(r.audio_url),
                imageUrl : asString(r.image_url),
                notes    : asString(r.notes),
                breakdown: parseJsonSafe(asString(r.breakdown_json), null)
            ].findAll { unused12, v -> v != null }
        }

        List<Map> conversations = sql.rows('SELECT * FROM ' + T_CONVERSATIONS + ' ORDER BY sort_order ASC').collect { r ->
            [
                id           : asString(r.id),
                title        : asString(r.title),
                titleCherokee: asString(r.title_cherokee),
                description  : asString(r.description),
                scenario     : asString(r.scenario),
                bannerImage  : asString(r.banner_image),
                turns        : parseJsonSafe(asString(r.turns_json), [])
            ].findAll { unused13, v -> v != null }
        }

        Set<String> viewerGroupIds = [] as Set<String>
        if (viewer) {
            sql.rows(
                'SELECT group_id FROM ' + T_GROUP_MEMBERS + ' WHERE user_id = ? UNION SELECT id AS group_id FROM ' + T_GROUPS + ' WHERE owner_user_id = ?',
                [viewer.id, viewer.id]
            ).each { viewerGroupIds.add(asString(it.group_id)) }
        }

        List<Map> allLessons = sql.rows('SELECT * FROM ' + T_LESSONS + ' ORDER BY sort_order ASC').collect { hydrateLessonRow(sql, it) }
        List<Map> visibleLessons = allLessons.findAll { canViewerAccessLesson(it, viewer, viewerGroupIds) }

        List<Map> sections = listSectionsInternal(sql, viewer)

        Set<String> visibleIds = visibleLessons.collect { (String) it.id } as Set<String>
        List<Map> units = sql.rows('SELECT * FROM ' + T_UNITS + ' ORDER BY unit_number ASC').collect { r ->
            String unitId = asString(r.id)
            List<String> existingIds = (parseJsonSafe(asString(r.lesson_ids_json), []) as List).collect { it.toString() }
            List<String> assignedIds = visibleLessons.findAll { it.unitId == unitId }.collect { (String) it.id }
            List<String> merged = (existingIds + assignedIds).unique().findAll { visibleIds.contains(it) }

            [
                id           : unitId,
                unitNumber   : asInt(r.unit_number, 1),
                title        : asString(r.title),
                titleCherokee: asString(r.title_cherokee),
                description  : asString(r.description),
                bannerImage  : asString(r.banner_image),
                lessonIds    : merged,
                accentColor  : asString(r.accent_color)
            ]
        }

        return [words: words, conversations: conversations, sections: sections, lessons: visibleLessons, units: units]
    }

    private void writeWords(Sql sql, List<Map> items) {
        sql.withTransaction {
            sql.execute('DELETE FROM ' + T_WORDS)
            items.eachWithIndex { Map w, int idx ->
                sql.executeInsert(
                    'INSERT INTO ' + T_WORDS +
                    ' (id, cherokee, phonetics, english, category, audio_hint, tone_guide, audio_url, image_url, notes, breakdown_json, sort_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                    [
                        asString(w.id),
                        asString(w.cherokee) ?: '',
                        asString(w.phonetics) ?: '',
                        asString(w.english) ?: '',
                        asString(w.category) ?: 'custom',
                        asString(w.audioHint),
                        asString(w.toneGuide),
                        asString(w.audioUrl),
                        asString(w.imageUrl),
                        asString(w.notes),
                        w.breakdown != null ? JsonOutput.toJson(w.breakdown) : null,
                        idx
                    ]
                )
            }
        }
    }

    private void writeConversations(Sql sql, List<Map> items) {
        sql.withTransaction {
            sql.execute('DELETE FROM ' + T_CONVERSATIONS)
            items.eachWithIndex { Map c, int idx ->
                sql.executeInsert(
                    'INSERT INTO ' + T_CONVERSATIONS +
                    ' (id, title, title_cherokee, description, scenario, banner_image, turns_json, sort_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
                    [
                        asString(c.id),
                        asString(c.title) ?: '',
                        asString(c.titleCherokee),
                        asString(c.description) ?: '',
                        asString(c.scenario) ?: '',
                        asString(c.bannerImage),
                        JsonOutput.toJson(c.turns ?: []),
                        idx
                    ]
                )
            }
        }
    }

    /** Bulk upsert of sections (callers decide what to delete first, like the SQLite adapter). */
    private void writeSections(Sql sql, List<Map> items) {
        sql.withTransaction {
            items.each { Map s ->
                String sectionId = asString(s.id)
                if (!sectionId) return
                sql.executeUpdate(
                    'INSERT INTO ' + T_SECTIONS + ' ' +
                    '(id, kind, title, payload_json, owner_user_id, owner_name, visibility, created_at) ' +
                    'VALUES (?, ?, ?, ?, ?, ?, ?, ?) ' +
                    'ON DUPLICATE KEY UPDATE ' +
                    'kind = VALUES(kind), title = VALUES(title), payload_json = VALUES(payload_json), ' +
                    'owner_user_id = VALUES(owner_user_id), owner_name = VALUES(owner_name), visibility = VALUES(visibility)',
                    [
                        sectionId,
                        asString(s.kind) ?: 'flashcards',
                        asString(s.title) ?: '',
                        JsonOutput.toJson(s.payload ?: [:]),
                        asString(s.ownerUserId),
                        asString(s.ownerName),
                        asString(s.visibility) == 'restricted' ? 'restricted' : 'public',
                        asString(s.createdAt) ?: Instant.now().toString()
                    ]
                )
            }
        }
    }

    private void writeLessons(Sql sql, List<Map> items, Map actingUser) {
        sql.withTransaction {
            List<Map> existingHydrated = sql.rows('SELECT * FROM ' + T_LESSONS + ' ORDER BY sort_order ASC').collect { hydrateLessonRow(sql, it) }
            Set<String> incomingIds = items.collect { asString(it.id) } as Set<String>

            existingHydrated.each { Map prev ->
                if (!incomingIds.contains(prev.id)) {
                    boolean canDelete = (actingUser == null) ||
                        (actingUser.role == 'admin') ||
                        (actingUser.role == 'editor' && !prev.ownerUserId) ||
                        (prev.ownerUserId == actingUser.id)
                    if (canDelete) {
                        sql.execute('DELETE FROM ' + T_LESSONS + ' WHERE id = ?', [prev.id])
                    }
                }
            }

            items.eachWithIndex { Map l, int idx ->
                String lid = asString(l.id)
                Map prev = existingHydrated.find { it.id == lid }
                String ownerUserId = l.containsKey('ownerUserId') ? asString(l.ownerUserId) : asString(prev?.ownerUserId)
                String ownerName = l.containsKey('ownerName') ? asString(l.ownerName) : asString(prev?.ownerName)
                String imageUrl = l.containsKey('imageUrl') ? asString(l.imageUrl) : asString(prev?.imageUrl)
                String vis = l.visibility == 'restricted'
                    ? 'restricted'
                    : (l.visibility == 'public' ? 'public' : (asString(prev?.visibility) ?: 'public'))

                sql.executeUpdate(
                    'INSERT INTO ' + T_LESSONS + ' ' +
                    '(id, unit_id, title, title_cherokee, description, type, level, xp_reward, ' +
                    'word_ids_json, conversation_id, sentence_challenges_json, visual_reading_json, ' +
                    'reader_json, custom_content_json, image_url, owner_user_id, owner_name, visibility, sort_order) ' +
                    'VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ' +
                    'ON DUPLICATE KEY UPDATE ' +
                    'unit_id = VALUES(unit_id), title = VALUES(title), title_cherokee = VALUES(title_cherokee), ' +
                    'description = VALUES(description), type = VALUES(type), level = VALUES(level), ' +
                    'xp_reward = VALUES(xp_reward), word_ids_json = VALUES(word_ids_json), ' +
                    'conversation_id = VALUES(conversation_id), sentence_challenges_json = VALUES(sentence_challenges_json), ' +
                    'visual_reading_json = VALUES(visual_reading_json), reader_json = VALUES(reader_json), custom_content_json = VALUES(custom_content_json), ' +
                    'image_url = VALUES(image_url), owner_user_id = VALUES(owner_user_id), owner_name = VALUES(owner_name), visibility = VALUES(visibility), ' +
                    'sort_order = VALUES(sort_order)',
                    [
                        lid,
                        asString(l.unitId) ?: 'unit-1',
                        asString(l.title) ?: '',
                        asString(l.titleCherokee),
                        asString(l.description) ?: '',
                        asString(l.type) ?: 'flashcards',
                        asString(l.level) ?: 'Beginner',
                        asInt(l.xpReward, 15),
                        JsonOutput.toJson(l.wordIds ?: []),
                        asString(l.conversationId),
                        l.sentenceChallenges != null ? JsonOutput.toJson(l.sentenceChallenges) : null,
                        l.visualReadingContent != null ? JsonOutput.toJson(l.visualReadingContent) : null,
                        l.readerContent != null ? JsonOutput.toJson(l.readerContent) : null,
                        l.customContent != null ? JsonOutput.toJson(l.customContent) : null,
                        imageUrl,
                        ownerUserId,
                        ownerName,
                        vis,
                        idx
                    ]
                )

                List<String> uShares = l.containsKey('sharedWithUserIds') && l.sharedWithUserIds instanceof Collection
                    ? ((Collection) l.sharedWithUserIds).collect { asString(it) }.findAll { it }
                    : ((prev?.sharedWithUserIds as List<String>) ?: [])
                List<String> gShares = l.containsKey('sharedWithGroupIds') && l.sharedWithGroupIds instanceof Collection
                    ? ((Collection) l.sharedWithGroupIds).collect { asString(it) }.findAll { it }
                    : ((prev?.sharedWithGroupIds as List<String>) ?: [])
                syncLessonSections(sql, lid, l.sectionIds)
                syncLessonShares(sql, lid, uShares, gShares)
            }
        }
    }

    private void writeUnits(Sql sql, List<Map> items) {
        sql.withTransaction {
            sql.execute('DELETE FROM ' + T_UNITS)
            items.each { Map u ->
                sql.executeInsert(
                    'INSERT INTO ' + T_UNITS +
                    ' (id, unit_number, title, title_cherokee, description, banner_image, lesson_ids_json, accent_color) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
                    [
                        asString(u.id),
                        asInt(u.unitNumber, 1),
                        asString(u.title) ?: '',
                        asString(u.titleCherokee) ?: '',
                        asString(u.description) ?: '',
                        asString(u.bannerImage) ?: '',
                        JsonOutput.toJson(u.lessonIds ?: []),
                        asString(u.accentColor) ?: 'terracotta'
                    ]
                )
            }
        }
    }

    /* ========================================================================
     * REQUEST / JSON / SAFE TYPE UTILITIES
     * ======================================================================== */
    private List<String> readAuthHeaders() {
        String authHeader = request.getHeader('Authorization') ?: ''
        def matcher = (authHeader =~ /(?i)^Bearer\s+(.+)$/)
        String token = matcher.matches() ? matcher.group(1).trim() : null
        String tz = request.getHeader('x-user-timezone')?.trim()
        return [token ?: null, tz ?: null]
    }

    private Map readJsonBody() {
        try {
            def parsed = request.JSON
            if (parsed instanceof Map) {
                return (Map) parsed
            }
            return [:]
        } catch (Exception ignored) {
            return [:]
        }
    }

    private Object parseJsonSafe(String raw, Object fallback) {
        if (!raw) return fallback
        try {
            return new JsonSlurper().parseText(raw)
        } catch (Exception ignored) {
            return fallback
        }
    }

    private String asString(Object val) {
        if (val == null) return null
        if (val instanceof java.sql.Clob) {
            return ((java.sql.Clob) val).characterStream.text
        }
        if (val instanceof byte[]) {
            return new String((byte[]) val, 'UTF-8')
        }
        return val.toString()
    }

    private boolean asBool(Object val, boolean defaultVal = false) {
        if (val == null) return defaultVal
        if (val instanceof Boolean) return (Boolean) val
        if (val instanceof Number) return ((Number) val).intValue() != 0
        String s = val.toString().trim().toLowerCase()
        if (s == 'true' || s == '1') return true
        if (s == 'false' || s == '0') return false
        return defaultVal
    }

    private int asInt(Object val, int defaultVal = 0) {
        if (val == null) return defaultVal
        if (val instanceof Number) return Math.round(((Number) val).doubleValue()) as int
        if (val instanceof Boolean) return ((Boolean) val) ? 1 : 0
        try {
            return Math.round(Double.parseDouble(val.toString().trim())) as int
        } catch (Exception ignored) {
            return defaultVal
        }
    }

    private long asLong(Object val, long defaultVal = 0L) {
        if (val == null) return defaultVal
        if (val instanceof Number) return ((Number) val).longValue()
        if (val instanceof Boolean) return ((Boolean) val) ? 1L : 0L
        try {
            return Long.parseLong(val.toString().trim())
        } catch (Exception ignored) {
            return defaultVal
        }
    }

    private String getLocalTodayDate(String timezone) {
        ZoneId zone
        try {
            zone = ZoneId.of(timezone ?: 'America/Chicago')
        } catch (Exception ignored) {
            zone = ZoneId.of('America/Chicago')
        }
        return LocalDate.now(zone).toString()
    }

    private long diffCalendarDays(String startYmd, String endYmd) {
        try {
            LocalDate start = LocalDate.parse(startYmd)
            LocalDate end = LocalDate.parse(endYmd)
            return ChronoUnit.DAYS.between(start, end)
        } catch (Exception ignored) {
            return 0L
        }
    }

    private void renderError(int statusCode, String msg) {
        response.status = statusCode
        render([error: msg ?: 'Unexpected server error'] as JSON)
    }
}
