package docrepo

import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.text.SimpleDateFormat

/**
 * Catalog core: upload + duplicate resolution (replace / skip /
 * catalog-different), metadata-only entries, search, downloads, audit.
 */
class DocRepoService {

    def grailsApplication

    private static final List<String> SORTABLE = ['dateCreated', 'lastUpdated', 'title']
    private static final List<String> INLINE_SAFE = ['application/pdf', 'image/jpeg', 'image/png', 'image/gif', 'image/webp', 'text/plain']

    // ------------------------------------------------------------ storage

    /**
     * Upload storage root. Production MUST set DOCREPO_STORAGE_DIR to a
     * bind-mounted host path (the dict-app container has no persistent
     * volumes by default — files written elsewhere are lost on redeploy).
     */
    File resolveStorageDir() {
        String configured = configString('docrepo.storageDir')
        if (configured) return new File(configured)
        return new File(System.getProperty('user.home'), '.docrepo-files')
    }

    private String configString(String path) {
        try {
            String v = grailsApplication?.config?.getProperty(path, String)
            return (v ?: '').trim()
        } catch (Exception ignored) {
            return ''
        }
    }

    // ------------------------------------------------------------- upload

    List<DocRepoFile> findCurrentFilesBySha(String sha) {
        return DocRepoFile.findAllBySha256AndIsCurrent(sha, true)
    }

    /**
     * Handles the whole upload lifecycle. Result map contract with the controller:
     *   [conflict: true, matches: [...]]            -> 409, nothing persisted
     *   [skipped: true, documentId: N]              -> 200, upload discarded
     *   [document: {...}, warning: "..."]           -> 201 (warning optional)
     */
    Map uploadDocument(DocRepoUser actor, MultipartFile file, Map meta,
                       String resolution, Long targetDocumentId, String renditionNote) {
        if (file == null || file.isEmpty()) {
            return [error: 'A file is required for upload.', code: 400]
        }
        File storageDir = resolveStorageDir()
        storageDir.mkdirs()

        String safeOriginal = sanitizeFilename(file.originalFilename ?: 'upload.bin')
        File temp = File.createTempFile('docrepo-upload-', '.tmp', storageDir)
        String sha
        try {
            sha = streamToShaFile(file.inputStream, temp)
            List<DocRepoFile> matches = findCurrentFilesBySha(sha)

            if (matches && !resolution) {
                temp.delete()
                return [conflict: true, matches: matches.collect { fileMatchJson(it) }]
            }

            if (matches && resolution == 'skip') {
                temp.delete()
                DocRepoDocument kept = matches[0].document
                audit(kept, actor, 'skipped_duplicate',
                    "Duplicate upload of \"${safeOriginal}\" (sha256 ${sha}) — skipped; existing entry kept")
                return [skipped: true, documentId: kept.id]
            }

            if (matches && resolution == 'replace') {
                DocRepoDocument target = matches.collect { it.document }.find { it.id == targetDocumentId }
                if (!target) {
                    temp.delete()
                    return [error: "Replace target document #${targetDocumentId} is not among the duplicate matches.", code: 400]
                }
                return replaceFile(target, actor, temp, safeOriginal, file.contentType, sha)
            }

            // 'catalogDifferent' (or no matches) -> a fresh catalog entry
            DocRepoDocument doc = buildDocument(meta, actor)
            doc.save(flush: true, failOnError: true)
            attachTags(doc, meta.tags)
            storeFile(doc, actor, temp, safeOriginal, file.contentType, sha,
                (matches && resolution == 'catalogDifferent') ? (renditionNote ?: 'Alternate rendition') : renditionNote)
            audit(doc, actor,
                matches ? 'cataloged_different' : 'created',
                matches
                    ? "Cataloged as a different rendition of document #${matches[0].document.id} (identical bytes, sha256 ${sha})"
                    : "Uploaded \"${safeOriginal}\" (${humanSize(file.size)})")

            String warning = null
            DocRepoFile sameName = DocRepoFile.findByOriginalFilenameAndIsCurrent(safeOriginal, true)
            if (sameName && sameName.document.id != doc.id) {
                warning = "An entry already stores a file named \"${safeOriginal}\" (#${sameName.document.id}) — review it if this could be the same item in another scan."
            }
            return [document: documentJson(doc), warning: warning]
        } catch (Exception e) {
            try { temp.delete() } catch (Exception ignored) { }
            throw e
        }
    }

    private Map replaceFile(DocRepoDocument target, DocRepoUser actor, File temp,
                            String originalFilename, String mimeType, String sha) {
        // in-memory filter (not an AndIsCurrent finder) — the loaded boolean is
        // authoritative; flip each current row, supersede with timestamp
        DocRepoFile.findAllByDocument(target).findAll { it.isCurrent }.each { DocRepoFile old ->
            old.isCurrent = false
            old.supersededAt = new Date()
            old.save(flush: true, failOnError: true)
        }
        DocRepoFile stored = storeFile(target, actor, temp, originalFilename, mimeType, sha, null)
        audit(target, actor, 'replaced_file',
            "Replaced current file with \"${originalFilename}\" (sha256 ${sha}) — prior version kept as history")
        return [document: documentJson(target), storedFileId: stored.id]
    }

    /** Temp file is consumed (moved) — caller must not reuse it afterwards. */
    private DocRepoFile storeFile(DocRepoDocument doc, DocRepoUser actor, File temp,
                                  String originalFilename, String mimeType, String sha, String renditionNote) {
        String storedName = "doc${doc.id}-${System.currentTimeMillis()}-${sanitizeFilename(originalFilename, 120)}"
        File target = new File(resolveStorageDir(), storedName)
        try {
            Files.move(temp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE)
        } catch (Exception atomicFailed) {
            Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        DocRepoFile row = new DocRepoFile(
            document: doc,
            originalFilename: originalFilename,
            storedFilename: storedName,
            mimeType: mimeType ?: null,
            sizeBytes: target.length(),
            sha256: sha,
            renditionNote: renditionNote ?: null,
            isCurrent: true,
            uploader: actor
        ).save(flush: true, failOnError: true)
        return row
    }

    // ------------------------------------------------- metadata-only entry

    Map createMetadataDocument(DocRepoUser actor, Map meta) {
        DocRepoDocument doc = buildDocument(meta, actor)
        doc.save(flush: true, failOnError: true)
        attachTags(doc, meta.tags)
        audit(doc, actor, 'created', "Cataloged metadata-only entry (item lives at ${doc.externalUrl ?: 'no external URL'})")
        return [document: documentJson(doc)]
    }

    // ------------------------------------------------------------ update

    Map updateMetadata(DocRepoUser actor, Long id, Map body) {
        DocRepoDocument doc = DocRepoDocument.get(id)
        if (!doc) return [error: 'Document not found.', code: 404]

        if (body.containsKey('title')) doc.title = trimTo(body.title, 500) ?: doc.title
        if (body.containsKey('authors')) doc.authors = trimTo(body.authors, 1000)
        if (body.containsKey('externalUrl')) doc.externalUrl = trimTo(body.externalUrl, 1000)
        if (body.containsKey('copyrightNote')) doc.copyrightNote = trimTo(body.copyrightNote, 2000)
        if (body.containsKey('notes')) doc.notes = trimTo(body.notes, 10000)
        if (body.containsKey('copyrightStatus')) {
            String cs = trimTo(body.copyrightStatus, 60)
            if (cs && !(cs in ['Public domain', 'Creative Commons', 'Used with permission', 'All rights reserved', 'Unknown'])) {
                return [error: 'Unknown copyright status.', code: 400]
            }
            doc.copyrightStatus = cs ?: null
        }
        if (body.containsKey('type')) {
            doc.type = resolveType(body.type)
        }
        if (body.containsKey('tags')) {
            attachTags(doc, body.tags)
        }
        doc.save(flush: true, failOnError: true)
        audit(doc, actor, 'metadata_updated', describeMetadataChange(body))
        return [document: documentJson(doc)]
    }

    private String describeMetadataChange(Map body) {
        List<String> changed = ['title', 'authors', 'externalUrl', 'copyrightStatus', 'copyrightNote', 'notes', 'type', 'tags']
            .findAll { body.containsKey(it) }
        return "Updated ${changed.join(', ')}"
    }

    // ------------------------------------------------------------ delete

    Map deleteDocument(DocRepoUser actor, Long id) {
        DocRepoDocument doc = DocRepoDocument.get(id)
        if (!doc) return [error: 'Document not found.', code: 404]

        List<DocRepoFile> files = DocRepoFile.findAllByDocument(doc)
        audit(doc, actor, 'deleted', "Deleted entry \"${doc.title}\" (${files.findAll { it.isCurrent }.size()} current file(s))")
        File storageDir = resolveStorageDir()
        files.each { DocRepoFile f ->
            try { new File(storageDir, f.storedFilename).delete() } catch (Exception ignored) { }
            f.delete()
        }
        DocRepoDocumentTag.findAllByDocument(doc)*.delete()
        doc.delete(flush: true)
        return [deleted: true]
    }

    // ------------------------------------------------------------ search

    Map searchDocuments(Map params) {
        int max = clamp(asInt(params.max ?: params.limit ?: 20, 20), 1, 100)
        int offset = Math.max(asInt(params.offset, 0), 0)
        String query = trimToNull(params.query)
        String tag = trimToNull(params.tag)
        String typeFilter = trimToNull(params.type)
        String author = trimToNull(params.author)
        Long uploaderId = asLong(params.uploader, 0)
        String sortField = (params.sort in SORTABLE) ? params.sort : 'dateCreated'
        String sortDir = (params.order == 'asc') ? 'asc' : 'desc'

        // tag matching goes through the explicit join domain (querying a tags
        // association inside or{} traps the whole query in an inner join)
        List<Long> tagDocIds = tag ? docIdsMatchingTag(tag, false) : null
        if (tag && !tagDocIds) return [total: 0L, items: []]
        List<Long> queryTagIds = query ? docIdsMatchingTag(query, true) : null

        def results = DocRepoDocument.createCriteria().list(max: max, offset: offset) {
            // note: closure locals must not shadow criteria builder methods
            // (sort/order/type) — hence sortField/sortDir/typeFilter names
            if (typeFilter) type { eq('name', typeFilter) }
            if (uploaderId) eq('uploader.id', uploaderId)
            if (author) ilike('authors', "%${author}%")
            if (tagDocIds) inList('id', tagDocIds)
            if (query) {
                or {
                    ilike('title', "%${query}%")
                    ilike('authors', "%${query}%")
                    ilike('notes', "%${query}%")
                    if (queryTagIds) inList('id', queryTagIds)
                }
            }
            order(sortField, sortDir)
        }
        return [total: results.totalCount, items: results.collect { listJson(it) }]
    }

    /** Document ids whose tag names match (exact term or contains). Fetches
     * the (small) join table and filters in memory — criteria associations on
     * the composite-id join domain are not in the JPA metamodel. */
    private List<Long> docIdsMatchingTag(String term, boolean contains) {
        String lower = term.toLowerCase()
        List<Long> ids = []
        DocRepoDocumentTag.findAll().each { row ->
            String name = (row.tag?.name ?: '').toLowerCase()
            if (name && (contains ? name.contains(lower) : name == lower)) {
                ids << row.document.id
            }
        }
        return ids.unique()
    }

    // ------------------------------------------------------------ reads

    Map documentJson(DocRepoDocument doc) {
        // always read files/tags through DB queries — in-memory hasMany sets are
        // stale right after a save and their LinkedHashSet blows up on List casts
        List<DocRepoFile> allFiles = DocRepoFile.findAllByDocument(doc, [sort: 'id', order: 'desc'])
        List<DocRepoFile> current = allFiles.findAll { it.isCurrent }
        return [
            id             : doc.id,
            title          : doc.title,
            authors        : doc.authors,
            type           : doc.type?.name,
            externalUrl    : doc.externalUrl,
            copyrightStatus: doc.copyrightStatus,
            copyrightNote  : doc.copyrightNote,
            notes          : doc.notes,
            tags           : tagNamesOf(doc),
            uploader       : uploaderJson(doc.uploader),
            dateCreated    : dateFmt(doc.dateCreated),
            lastUpdated    : dateFmt(doc.lastUpdated),
            files          : allFiles.collect { fileJson(it) },
            currentFile    : current ? fileJson(current[0]) : null,
            identicalEntries: identicalEntries(doc),
            audit          : DocRepoAuditLog.findAllByDocumentId(doc.id, [max: 50, sort: 'dateCreated', order: 'desc']).collect { auditJson(it) }
        ]
    }

    private Map listJson(DocRepoDocument doc) {
        List<DocRepoFile> files = DocRepoFile.findAllByDocument(doc)
        return [
            id             : doc.id,
            title          : doc.title,
            authors        : doc.authors,
            type           : doc.type?.name,
            tags           : tagNamesOf(doc),
            externalUrl    : doc.externalUrl,
            copyrightStatus: doc.copyrightStatus,
            hasFile        : files.any { it.isCurrent },
            fileCount      : files.size(),
            uploader       : uploaderJson(doc.uploader),
            dateCreated    : dateFmt(doc.dateCreated)
        ]
    }

    private Map fileJson(DocRepoFile f) {
        return [
            id             : f.id,
            originalFilename: f.originalFilename,
            mimeType       : f.mimeType,
            sizeBytes      : f.sizeBytes,
            sizeHuman      : humanSize(f.sizeBytes),
            sha256         : f.sha256,
            renditionNote  : f.renditionNote,
            isCurrent      : f.isCurrent,
            supersededAt   : dateFmt(f.supersededAt),
            uploader       : uploaderJson(f.uploader),
            uploadedAt     : dateFmt(f.dateCreated),
            downloadUrl    : "/docrepo/api/documents/${f.document.id}/file/${f.id}"
        ]
    }

    private Map fileMatchJson(DocRepoFile f) {
        return [
            documentId     : f.document.id,
            title          : f.document.title,
            authors        : f.document.authors,
            type           : f.document.type?.name,
            tags           : tagNamesOf(f.document),
            uploader       : uploaderJson(f.uploader),
            uploadedAt     : dateFmt(f.dateCreated),
            originalFilename: f.originalFilename,
            renditionNote  : f.renditionNote,
            sizeBytes      : f.sizeBytes,
            sizeHuman      : humanSize(f.sizeBytes),
            sha256         : f.sha256
        ]
    }

    private List identicalEntries(DocRepoDocument doc) {
        List<String> shas = DocRepoFile.findAllByDocument(doc).findAll { it.isCurrent }*.sha256.unique()
        if (!shas) return []
        Map<Long, Map> found = [:]
        shas.each { String sha ->
            DocRepoFile.findAllBySha256AndIsCurrent(sha, true).each { DocRepoFile f ->
                if (f.document.id != doc.id && !found.containsKey(f.document.id)) {
                    found[f.document.id] = [id: f.document.id, title: f.document.title, sha256: sha]
                }
            }
        }
        return found.values().toList()
    }

    private Map uploaderJson(DocRepoUser user) {
        return [id: user?.id, username: user?.username, displayName: user?.displayName]
    }

    private Map auditJson(DocRepoAuditLog a) {
        return [
            action  : a.action,
            detail  : a.detail,
            actor   : a.actor ? [username: a.actor.username, displayName: a.actor.displayName] : null,
            at      : dateFmt(a.dateCreated)
        ]
    }

    // ------------------------------------------------------------- files

    Map resolveDownload(Long documentId, Long fileId, boolean currentOnly) {
        DocRepoFile f = fileId ? DocRepoFile.get(fileId) : null
        if (!f && currentOnly) {
            f = DocRepoFile.find {
                eq('document.id', documentId)
                eq('isCurrent', true)
            }
        }
        if (!f || f.document.id != documentId) {
            return [error: 'File not found.', code: 404]
        }
        File onDisk = new File(resolveStorageDir(), f.storedFilename)
        if (!onDisk.isFile()) {
            log.warn("DocRepo: stored file missing on disk: ${f.storedFilename}")
            return [error: 'Stored file is missing on the server.', code: 410]
        }
        return [file: f, onDisk: onDisk]
    }

    boolean mayStreamInline(String mimeType) {
        return mimeType in INLINE_SAFE
    }

    // ------------------------------------------------------------- tags

    List<String> tagNames() {
        return DocRepoTag.list(sort: 'name')*.name
    }

    List<String> typeNames() {
        return DocRepoDocumentType.list(sort: 'sortOrder')*.name
    }

    // ------------------------------------------------------------ helpers

    private DocRepoDocument buildDocument(Map meta, DocRepoUser actor) {
        String title = trimTo(meta.title, 500)
        if (!title) throw new IllegalArgumentException('Title is required.')
        DocRepoDocument doc = new DocRepoDocument(
            title: title,
            authors: trimTo(meta.authors, 1000),
            externalUrl: trimTo(meta.externalUrl, 1000),
            copyrightNote: trimTo(meta.copyrightNote, 2000),
            notes: trimTo(meta.notes, 10000),
            uploader: actor
        )
        String cs = trimTo(meta.copyrightStatus, 60)
        if (cs && !(cs in ['Public domain', 'Creative Commons', 'Used with permission', 'All rights reserved', 'Unknown'])) {
            throw new IllegalArgumentException('Unknown copyright status.')
        }
        doc.copyrightStatus = cs ?: null
        doc.type = resolveType(meta.type)
        return doc
    }

    /** Matches an existing type case-insensitively; a new non-empty name creates the type (types stay an open, user-growable list). */
    private DocRepoDocumentType resolveType(Object raw) {
        String name = trimTo(raw, 80)
        if (!name) return null
        DocRepoDocumentType existing = DocRepoDocumentType.findByNameIlike(name) ?: DocRepoDocumentType.findByName(name)
        if (existing) return existing
        return new DocRepoDocumentType(name: name, sortOrder: 999).save(flush: true, failOnError: true)
    }

    /**
     * Rewrites the tag set of a saved document (id must be assigned). Tags are
     * user-defined ("verb, conjugation; year"); case-insensitive de-dupe. Uses
     * explicit DocRepoDocumentTag rows — the GORM-managed many-to-many silently
     * dropped join rows when the owner was transient.
     */
    private void attachTags(DocRepoDocument doc, Object rawTags) {
        List<String> names = parseTagNames(rawTags)
        DocRepoDocumentTag.findAllByDocument(doc)*.delete()
        names.each { String name ->
            DocRepoTag tag = DocRepoTag.findByName(name) ?: DocRepoTag.list().find { it.name.equalsIgnoreCase(name) }
            if (!tag) tag = new DocRepoTag(name: name).save(flush: true, failOnError: true)
            new DocRepoDocumentTag(document: doc, tag: tag).save(flush: true, failOnError: true)
        }
    }

    /** Accepts "verb, conjugation; year" or a JSON list; case-insensitive de-dupe, capped at 64 chars each. */
    private List<String> parseTagNames(Object rawTags) {
        List<String> names
        if (rawTags instanceof Collection) {
            names = rawTags.collect { trimTo(it, 64) }
        } else {
            names = ((rawTags?.toString() ?: '')).split(/[,;]+/).collect { trimTo(it, 64) }
        }
        List<String> clean = names.findAll { it }
        List<String> seen = []
        List<String> out = []
        clean.each { String name ->
            String lower = name.toLowerCase()
            if (!seen.contains(lower)) {
                seen << lower
                out << name
            }
        }
        return out
    }

    private List<String> tagNamesOf(DocRepoDocument doc) {
        return DocRepoDocumentTag.findAllByDocument(doc).collect { it.tag.name }.sort { it.toLowerCase() }
    }

    private void audit(DocRepoDocument doc, DocRepoUser actor, String action, String detail) {
        new DocRepoAuditLog(documentId: doc.id, documentTitle: doc.title, actor: actor, action: action, detail: trimTo(detail, 2000)).save(flush: true, failOnError: true)
    }

    /** Streams in to target while computing the SHA-256 hex digest. */
    private String streamToShaFile(InputStream input, File target) {
        MessageDigest digest = MessageDigest.getInstance('SHA-256')
        OutputStream out = new FileOutputStream(target)
        byte[] buf = new byte[64 * 1024]
        try {
            int n
            while ((n = input.read(buf)) > 0) {
                digest.update(buf, 0, n)
                out.write(buf, 0, n)
            }
        } finally {
            try { out.close() } catch (Exception ignored) { }
            try { input.close() } catch (Exception ignored) { }
        }
        return digest.digest().collect { String.format('%02x', it & 0xff) }.join()
    }

    /** Keeps unicode (Cherokee syllabary filenames are expected); strips path separators/control chars; caps length, keeps extension. */
    private String sanitizeFilename(String raw, int maxLen = 200) {
        String name = (raw ?: 'file').trim()
        name = name.replace('\\', '/').tokenize('/').last()
        name = name.replaceAll(/[\x00-\x1f<>:"|?*]/, '_').replaceAll(/\s+/, ' ').trim() ?: 'file'
        if (name.length() > maxLen) {
            String ext = extensionOf(name)
            name = name.substring(0, maxLen - ext.length()) + ext
        }
        return name
    }

    private String extensionOf(String name) {
        int dot = name.lastIndexOf('.')
        return (dot > 0 && dot < name.length() - 1 && name.length() - dot <= 12) ? name.substring(dot) : ''
    }

    private String humanSize(Long bytes) {
        long b = bytes ?: 0L
        if (b < 1024) return "${b} B"
        if (b < 1024 * 1024) return String.format('%.1f KB', b / 1024.0)
        if (b < 1024L * 1024 * 1024) return String.format('%.1f MB', b / (1024.0 * 1024))
        return String.format('%.2f GB', b / (1024.0 * 1024 * 1024))
    }

    private String dateFmt(Date d) {
        return d ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d) : null
    }

    private String trimTo(Object raw, int max) {
        String s = raw?.toString()?.trim()
        if (!s) return null
        return s.length() > max ? s.substring(0, max) : s
    }

    private String trimToNull(Object raw) {
        return trimTo(raw, Integer.MAX_VALUE - 1)
    }

    private int asInt(Object val, int defaultVal) {
        try { return Integer.parseInt(val.toString().trim()) } catch (Exception ignored) { return defaultVal }
    }

    private long asLong(Object val, long defaultVal) {
        try { return Long.parseLong(val.toString().trim()) } catch (Exception ignored) { return defaultVal }
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v))
    }
}
