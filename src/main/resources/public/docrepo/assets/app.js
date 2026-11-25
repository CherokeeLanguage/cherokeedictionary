/* DocRepo — no-build static frontend. Talks to /docrepo/api/** only;
 * a future Svelte/React build can replace this file without API changes. */
'use strict';

const API = '/docrepo/api';
const $app = document.getElementById('app');
const $nav = document.getElementById('nav');
const $topbar = document.getElementById('topbar');
const $userChip = document.getElementById('userChip');
const $modalHost = document.getElementById('modalHost');
const $toastHost = document.getElementById('toastHost');

const state = {
  token: localStorage.getItem('docrepo_token') || null,
  user: null,
  types: [],
  tags: [],
};

/* ---------------- helpers ---------------- */

function esc(s) {
  const d = document.createElement('div');
  d.textContent = s == null ? '' : String(s);
  return d.innerHTML;
}

function toast(msg, kind, ms) {
  const t = document.createElement('div');
  t.className = 'toast ' + (kind || 'info');
  t.textContent = msg;
  $toastHost.appendChild(t);
  setTimeout(() => t.remove(), ms || 4500);
}

async function api(path, opts) {
  opts = opts || {};
  const headers = {};
  if (state.token) headers['Authorization'] = 'Bearer ' + state.token;
  let body = opts.body;
  if (body !== undefined && body !== null && !(body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
    body = JSON.stringify(body);
  }
  const res = await fetch(API + path, { method: opts.method || 'GET', headers, body });
  let data = null;
  const ct = res.headers.get('content-type') || '';
  if (ct.indexOf('json') >= 0) data = await res.json().catch(() => null);
  if (res.status === 401 && state.token) {
    doLogout(true);
    throw new Error((data && data.error) || 'Your session has expired — please sign in again.');
  }
  if (!res.ok) throw new Error((data && data.error) || ('Request failed (' + res.status + ')'));
  return data;
}

function fmtSize(b) {
  b = b || 0;
  if (b < 1024) return b + ' B';
  if (b < 1048576) return (b / 1024).toFixed(1) + ' KB';
  if (b < 1073741824) return (b / 1048576).toFixed(1) + ' MB';
  return (b / 1073741824).toFixed(2) + ' GB';
}

function openModal(html) {
  $modalHost.innerHTML = '<div class="modal-backdrop"><div class="modal">' + html + '</div></div>';
  $modalHost.querySelector('.modal-backdrop').addEventListener('click', (e) => {
    if (e.target === e.currentTarget) closeModal();
  });
}
function closeModal() { $modalHost.innerHTML = ''; }

function chipList(names) {
  return (names || []).map((n) => '<span class="chip">' + esc(n) + '</span>').join('');
}

async function download(url, filename) {
  const res = await fetch(API + url, { headers: { Authorization: 'Bearer ' + state.token } });
  if (!res.ok) {
    let msg = 'Download failed (' + res.status + ')';
    try { const j = await res.json(); if (j && j.error) msg = j.error; } catch (e) { /* ignore */ }
    toast(msg, 'error');
    return;
  }
  const blob = await res.blob();
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = filename || 'download';
  a.click();
  setTimeout(() => URL.revokeObjectURL(a.href), 10000);
}

/* ---------------- auth ---------------- */

function showLogin(msg) {
  $topbar.classList.add('hidden');
  $app.innerHTML =
    '<div class="loginwrap"><div class="card">' +
    '<h1>📚 DocRepo</h1>' +
    '<p class="muted" style="text-align:center">Cherokee Education Documents — certified-user library</p>' +
    '<form id="loginForm">' +
    '<label>Username</label><input type="text" id="loginUser" autocomplete="username" autofocus>' +
    '<label>Password</label><input type="password" id="loginPass" autocomplete="current-password">' +
    '<div class="formerror" id="loginErr">' + esc(msg || '') + '</div>' +
    '<button class="btn" type="submit" style="width:100%">Sign in</button>' +
    '</form>' +
    '<details><summary>Request access</summary>' +
    '<p class="muted">Creates a pending account — an administrator must approve it before you can sign in. If signup is closed on this deployment, an admin will create your account.</p>' +
    '<form id="regForm">' +
    '<label>Username</label><input type="text" id="regUser">' +
    '<label>Display name</label><input type="text" id="regName">' +
    '<label>Email</label><input type="text" id="regEmail">' +
    '<label>Password</label><input type="password" id="regPass">' +
    '<div class="formerror" id="regErr"></div>' +
    '<button class="btn ghost" type="submit" style="width:100%">Request account</button>' +
    '</form></details>' +
    '</div></div>';

  document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const err = document.getElementById('loginErr');
    err.textContent = '';
    try {
      const d = await api('/auth/login', { method: 'POST', body: {
        username: document.getElementById('loginUser').value,
        password: document.getElementById('loginPass').value,
      }});
      state.token = d.token;
      localStorage.setItem('docrepo_token', d.token);
      state.user = d.user;
      await afterLogin();
    } catch (ex) { err.textContent = ex.message; }
  });

  document.getElementById('regForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const err = document.getElementById('regErr');
    err.textContent = '';
    try {
      const d = await api('/auth/register', { method: 'POST', body: {
        username: document.getElementById('regUser').value,
        displayName: document.getElementById('regName').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPass').value,
      }});
      err.textContent = '';
      err.style.color = '#1d6a3c';
      err.textContent = d.message || 'Request received.';
    } catch (ex) { err.style.color = ''; err.textContent = ex.message; }
  });
}

function doLogout(silent) {
  if (state.token) { api('/auth/logout', { method: 'POST' }).catch(() => {}); }
  state.token = null;
  state.user = null;
  localStorage.removeItem('docrepo_token');
  showLogin(silent ? 'Your session has expired — please sign in again.' : '');
}

document.getElementById('logoutBtn').addEventListener('click', () => doLogout(false));

async function afterLogin() {
  const d = await api('/me');
  state.user = d.user;
  const vocab = await Promise.all([api('/types'), api('/tags')]);
  state.types = vocab[0].types;
  state.tags = vocab[1].tags;
  $topbar.classList.remove('hidden');
  $userChip.textContent = state.user.displayName + (state.user.role === 'admin' ? ' · admin' : '');
  setNav();
  showList();
}

function setNav() {
  const isAdmin = state.user && state.user.role === 'admin';
  $nav.innerHTML = '';
  const mk = (label, fn, id) => {
    const b = document.createElement('button');
    b.textContent = label;
    if (id) b.id = 'nav-' + id;
    b.addEventListener('click', fn);
    $nav.appendChild(b);
  };
  mk('Documents', () => showList(), 'list');
  mk('Upload', () => showUpload(), 'upload');
  if (isAdmin) mk('Admin', () => showAdmin(), 'admin');
  markNav('nav-list');
}

function markNav(id) {
  $nav.querySelectorAll('button').forEach((b) => b.classList.remove('active'));
  const el = document.getElementById(id);
  if (el) el.classList.add('active');
}

/* password change dialog */
document.getElementById('pwBtn').addEventListener('click', () => {
  if (!state.user) return;
  openModal(
    '<h2>Change password</h2><form id="pwForm">' +
    '<label>Current password</label><input type="password" id="pwOld">' +
    '<label>New password</label><input type="password" id="pwNew">' +
    '<div class="formerror" id="pwErr"></div>' +
    '<div class="actions"><button class="btn" type="submit">Save</button>' +
    '<button class="btn ghost" type="button" id="pwCancel">Cancel</button></div></form>');
  document.getElementById('pwCancel').addEventListener('click', closeModal);
  document.getElementById('pwForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      await api('/me/password', { method: 'PUT', body: {
        oldPassword: document.getElementById('pwOld').value,
        newPassword: document.getElementById('pwNew').value,
      }});
      closeModal();
      toast('Password changed.', 'ok');
    } catch (ex) { document.getElementById('pwErr').textContent = ex.message; }
  });
});

/* ---------------- documents list ---------------- */

const listState = { query: '', type: '', tag: '', author: '', sort: 'dateCreated', order: 'desc', offset: 0, limit: 20 };

async function showList() {
  markNav('nav-list');
  $app.innerHTML =
    '<h1>Document library</h1>' +
    '<div class="toolbar">' +
    '<input class="grow" id="fQuery" type="text" placeholder="Search title, author, tags, notes…" value="' + esc(listState.query) + '">' +
    '<select id="fType"><option value="">All types</option>' +
    state.types.map((t) => '<option' + (listState.type === t ? ' selected' : '') + '>' + esc(t) + '</option>').join('') +
    '</select>' +
    '<select id="fTag"><option value="">All tags</option>' +
    state.tags.map((t) => '<option' + (listState.tag === t ? ' selected' : '') + '>' + esc(t) + '</option>').join('') +
    '</select>' +
    '<input id="fAuthor" type="text" placeholder="Author…" value="' + esc(listState.author) + '">' +
    '<select id="fSort">' +
    '<option value="dateCreated"' + (listState.sort === 'dateCreated' ? ' selected' : '') + '>Newest</option>' +
    '<option value="dateCreated_asc"' + (listState.sort === 'dateCreated' && listState.order === 'asc' ? ' selected' : '') + '>Oldest</option>' +
    '<option value="title"' + (listState.sort === 'title' ? ' selected' : '') + '>Title A→Z</option>' +
    '</select>' +
    '<button class="btn small" id="fGo">Search</button>' +
    '</div>' +
    '<div id="listBody"></div>';
  document.getElementById('fGo').addEventListener('click', () => {
    listState.query = document.getElementById('fQuery').value;
    listState.type = document.getElementById('fType').value;
    listState.tag = document.getElementById('fTag').value;
    listState.author = document.getElementById('fAuthor').value;
    const sv = document.getElementById('fSort').value;
    if (sv === 'dateCreated_asc') { listState.sort = 'dateCreated'; listState.order = 'asc'; }
    else { listState.sort = sv; listState.order = 'desc'; }
    listState.offset = 0;
    renderList();
  });
  document.getElementById('fQuery').addEventListener('keydown', (e) => {
    if (e.key === 'Enter') document.getElementById('fGo').click();
  });
  renderList();
}

async function renderList() {
  const body = document.getElementById('listBody');
  if (!body) return;
  body.innerHTML = '<div class="card empty">Loading…</div>';
  const q = new URLSearchParams();
  if (listState.query) q.set('query', listState.query);
  if (listState.type) q.set('type', listState.type);
  if (listState.tag) q.set('tag', listState.tag);
  if (listState.author) q.set('author', listState.author);
  q.set('sort', listState.sort);
  q.set('order', listState.order);
  q.set('offset', String(listState.offset));
  q.set('max', String(listState.limit));

  let d;
  try { d = await api('/documents?' + q.toString()); }
  catch (ex) { body.innerHTML = '<div class="card empty">' + esc(ex.message) + '</div>'; return; }

  if (!d.items.length) {
    body.innerHTML = '<div class="card empty">No entries match. Use the Upload button to add the first one.</div>';
    return;
  }

  const rows = d.items.map((it) =>
    '<tr class="entry" data-id="' + it.id + '">' +
    '<td><strong>' + esc(it.title) + '</strong>' +
    (it.hasFile ? '' : (it.externalUrl ? ' <span class="muted">· external link</span>' : ' <span class="muted">· metadata only</span>')) + '<br>' +
    '<span class="muted">' + esc(it.authors || '—') + '</span></td>' +
    '<td>' + esc(it.type || '') + '</td>' +
    '<td>' + chipList(it.tags) + '</td>' +
    '<td class="muted">' + esc(it.copyrightStatus || '') + '</td>' +
    '<td class="muted">' + esc(it.uploader ? it.uploader.username : '') + '<br>' + esc(it.dateCreated) + '</td>' +
    '</tr>'
  ).join('');

  const from = listState.offset + 1;
  const to = Math.min(listState.offset + d.items.length, d.total);
  body.innerHTML =
    '<table class="list"><thead><tr><th>Title / Author</th><th>Type</th><th>Tags</th><th>Copyright</th><th>Uploaded</th></tr></thead>' +
    '<tbody>' + rows + '</tbody></table>' +
    '<div class="pager">' +
    '<button class="btn small ghost" id="pgPrev"' + (listState.offset === 0 ? ' disabled' : '') + '>← Prev</button>' +
    '<span>' + from + '–' + to + ' of ' + d.total + '</span>' +
    '<button class="btn small ghost" id="pgNext"' + (to >= d.total ? ' disabled' : '') + '>Next →</button>' +
    '</div>';

  body.querySelectorAll('tr.entry').forEach((tr) =>
    tr.addEventListener('click', () => showDetail(tr.getAttribute('data-id'))));
  const prev = document.getElementById('pgPrev');
  const next = document.getElementById('pgNext');
  if (prev) prev.addEventListener('click', () => { listState.offset = Math.max(0, listState.offset - listState.limit); renderList(); });
  if (next) next.addEventListener('click', () => { listState.offset += listState.limit; renderList(); });
}

/* ---------------- detail ---------------- */

async function showDetail(id) {
  $app.innerHTML = '<div class="card empty">Loading…</div>';
  let d;
  try { d = await api('/documents/' + id); }
  catch (ex) { $app.innerHTML = '<div class="card empty">' + esc(ex.message) + '</div>'; return; }

  const canEdit = state.user && (state.user.role === 'admin' || (d.uploader && d.uploader.id === state.user.id));
  const canDelete = state.user && state.user.role === 'admin';

  const fileLines = (d.files || []).map((f) => {
    const cur = f.isCurrent;
    return '<div class="fileline">' +
      '<span class="badge ' + (cur ? 'current">current' : 'superseded">superseded') + '</span>' +
      '<strong>' + esc(f.originalFilename) + '</strong>' +
      (f.renditionNote ? '<span class="chip">' + esc(f.renditionNote) + '</span>' : '') +
      '<span class="meta">' + esc(f.sizeHuman) + ' · ' + esc(f.uploadedAt) + (f.uploader ? ' · by ' + esc(f.uploader.username) : '') +
      (cur ? '' : ' · replaced ' + esc(f.supersededAt)) + '</span>' +
      '<span class="spacer"></span>' +
      '<button class="btn small ghost" data-dl="' + esc(f.downloadUrl) + '" data-name="' + esc(f.originalFilename) + '">Download</button>' +
      '</div>' +
      '<div class="fileline" style="padding-top:0"><span class="meta sha">sha256 ' + esc(f.sha256) + '</span></div>';
  }).join('');

  const identical = (d.identicalEntries || []).map((x) =>
    '<a href="#" data-doc="' + x.id + '">' + esc('#' + x.id + ' — ' + x.title) + '</a>').join(', ');

  const auditItems = (d.audit || []).map((a) =>
    '<li><strong>' + esc(a.action) + '</strong> — ' + esc(a.detail || '') +
    ' <span class="muted">(' + esc(a.actor ? a.actor.username : '') + ', ' + esc(a.at) + ')</span></li>').join('');

  $app.innerHTML =
    '<h1>' + esc(d.title) + '</h1>' +
    '<div class="card">' +
    '<dl class="kv">' +
    '<dt>Authors</dt><dd>' + esc(d.authors || '—') + '</dd>' +
    '<dt>Type</dt><dd>' + esc(d.type || '—') + '</dd>' +
    '<dt>Tags</dt><dd>' + (chipList(d.tags) || '<span class="muted">none</span>') + '</dd>' +
    '<dt>Copyright</dt><dd>' + (d.copyrightStatus || d.copyrightNote
      ? esc(d.copyrightStatus || '') + (d.copyrightNote ? '<br><span class="muted">' + esc(d.copyrightNote) + '</span>' : '')
      : '—') + '</dd>' +
    '<dt>Lives at</dt><dd>' + (d.externalUrl ? '<a href="' + esc(d.externalUrl) + '" target="_blank" rel="noopener noreferrer">' + esc(d.externalUrl) + '</a>' : '<span class="muted">this server</span>') + '</dd>' +
    '<dt>Notes</dt><dd>' + esc(d.notes || '—') + '</dd>' +
    '<dt>Uploaded by</dt><dd>' + esc(d.uploader ? (d.uploader.displayName + ' (' + d.uploader.username + ')') : '—') + ' · ' + esc(d.dateCreated) + (d.lastUpdated !== d.dateCreated ? ' · last edited ' + esc(d.lastUpdated) : '') + '</dd>' +
    '</dl>' +
    '<div class="actions" style="margin-top:14px;display:flex;gap:10px">' +
    (canEdit ? '<button class="btn ghost small" id="editBtn">Edit metadata</button>' : '') +
    (canDelete ? '<button class="btn danger small" id="delBtn">Delete entry</button>' : '') +
    '<button class="btn ghost small" id="backBtn">← Back to list</button>' +
    '</div></div>' +
    '<h2>Files & renditions</h2><div class="card">' +
    (fileLines || '<span class="muted">No file stored on this server — the item lives at the external link above (or is metadata only).</span>') +
    '</div>' +
    (d.identicalEntries && d.identicalEntries.length ? '<h2>Entries with identical bytes</h2><div class="card">' + identical + '</div>' : '') +
    '<h2>History</h2><div class="card"><ul class="auditlist">' + (auditItems || '<li class="muted">No history recorded.</li>') + '</ul></div>';

  $app.querySelectorAll('[data-dl]').forEach((b) =>
    b.addEventListener('click', () => download(b.getAttribute('data-dl'), b.getAttribute('data-name'))));
  $app.querySelectorAll('[data-doc]').forEach((a) =>
    a.addEventListener('click', (e) => { e.preventDefault(); showDetail(a.getAttribute('data-doc')); }));
  const back = document.getElementById('backBtn');
  if (back) back.addEventListener('click', showList);
  const edit = document.getElementById('editBtn');
  if (edit) edit.addEventListener('click', () => showEditForm(d));
  const del = document.getElementById('delBtn');
  if (del) del.addEventListener('click', () => {
    if (!confirm('Delete entry "' + d.title + '"? Its stored files are removed and this cannot be undone.')) return;
    api('/documents/' + d.id, { method: 'DELETE' })
      .then(() => { toast('Entry deleted.', 'ok'); showList(); })
      .catch((ex) => toast(ex.message, 'error'));
  });
}

/* ---------------- upload ---------------- */

function metaFormHtml(d) {
  d = d || {};
  return '<div class="formgrid">' +
    '<div><label>Title *</label><input type="text" id="mTitle" value="' + esc(d.title || '') + '"></div>' +
    '<div><label>Author(s)</label><input type="text" id="mAuthors" placeholder="e.g. Kirk, Paul W.; Montgomery-Anderson, Brad" value="' + esc(d.authors || '') + '"></div>' +
    '<div><label>Type of paper</label><input type="text" id="mType" list="typeList" placeholder="dissertation, wordlist, …" value="' + esc(d.type || '') + '">' +
    '<datalist id="typeList">' + state.types.map((t) => '<option value="' + esc(t) + '">').join('') + '</datalist></div>' +
    '<div><label>Tags (comma-separated)</label><input type="text" id="mTags" list="tagList" placeholder="verb, conjugation, year…" value="' + esc((d.tags || []).join(', ')) + '">' +
    '<datalist id="tagList">' + state.tags.map((t) => '<option value="' + esc(t) + '">').join('') + '</datalist></div>' +
    '<div><label>Copyright status</label><select id="mCopy">' +
    [''].concat(['Public domain', 'Creative Commons', 'Used with permission', 'All rights reserved', 'Unknown']).map((c) =>
      '<option' + (d.copyrightStatus === c ? ' selected' : '') + '>' + esc(c) + '</option>').join('') +
    '</select></div>' +
    '<div><label>Copyright note</label><input type="text" id="mCopyNote" value="' + esc(d.copyrightNote || '') + '"></div>' +
    '<div><label>External URL (if not stored here)</label><input type="text" id="mUrl" value="' + esc(d.externalUrl || '') + '"></div>' +
    '</div>' +
    '<label>Notes</label><textarea id="mNotes">' + esc(d.notes || '') + '</textarea>';
}

function readMetaForm() {
  return {
    title: document.getElementById('mTitle').value.trim(),
    authors: document.getElementById('mAuthors').value.trim(),
    type: document.getElementById('mType').value.trim(),
    tags: document.getElementById('mTags').value.trim(),
    copyrightStatus: document.getElementById('mCopy').value,
    copyrightNote: document.getElementById('mCopyNote').value.trim(),
    externalUrl: document.getElementById('mUrl').value.trim(),
    notes: document.getElementById('mNotes').value,
  };
}

async function showUpload() {
  markNav('nav-upload');
  $app.innerHTML =
    '<h1>Upload a document</h1><div class="card">' +
    '<label>File (PDF, Word, PowerPoint, Excel, … — up to 250MB)</label>' +
    '<input type="file" id="uFile">' +
    metaFormHtml() +
    '<div class="formerror" id="uErr"></div>' +
    '<button class="btn" id="uGo">Upload & catalog</button>' +
    '<span class="muted" style="margin-left:12px">If identical bytes are already cataloged you will choose: replace, skip, or catalog as a different rendition.</span>' +
    '</div>';

  document.getElementById('uFile').addEventListener('change', (e) => {
    const f = e.target.files && e.target.files[0];
    const t = document.getElementById('mTitle');
    if (f && !t.value) t.value = f.name.replace(/\.[^.]+$/, '').replace(/[_]+/g, ' ');
  });

  document.getElementById('uGo').addEventListener('click', () => {
    const err = document.getElementById('uErr');
    err.textContent = '';
    const fileInput = document.getElementById('uFile');
    const meta = readMetaForm();
    if (!fileInput.files.length && !meta.title) {
      err.textContent = 'Pick a file — or enter a title (and optionally an external URL) to catalog a metadata-only entry for an item that lives elsewhere.';
      return;
    }
    if (!fileInput.files.length) {
      // metadata-only entry
      api('/documents', { method: 'POST', body: meta })
        .then((d) => { toast('Cataloged.', 'ok'); refreshVocab(); showDetail(d.document.id); })
        .catch((ex) => { err.textContent = ex.message; });
      return;
    }
    const fd = new FormData();
    fd.append('file', fileInput.files[0]);
    Object.keys(meta).forEach((k) => fd.append(k, meta[k] == null ? '' : meta[k]));
    submitUpload(fd, err);
  });
}

async function refreshVocab() {
  try {
    const vocab = await Promise.all([api('/types'), api('/tags')]);
    state.types = vocab[0].types;
    state.tags = vocab[1].tags;
  } catch (e) { /* non-fatal */ }
}

async function submitUpload(fd, errEl) {
  const go = document.getElementById('uGo');
  if (go) go.disabled = true;
  try {
    const d = await api('/documents/upload', { method: 'POST', body: fd });
    if (d && d.conflict) {
      showDuplicateDialog(fd, d.matches);
      return;
    }
    if (d && d.warning) toast(d.warning, 'warn', 9000);
    toast('Uploaded and cataloged.', 'ok');
    refreshVocab();
    showDetail(d.document.id);
  } catch (ex) {
    if (errEl) errEl.textContent = ex.message; else toast(ex.message, 'error');
  } finally {
    if (go) go.disabled = false;
  }
}

/* ---------------- duplicate resolution ---------------- */

function showDuplicateDialog(fd, matches) {
  // one choice row per document among the matches (dedupe by documentId)
  const byDoc = {};
  matches.forEach((m) => { byDoc[m.documentId] = m; });
  const docs = Object.keys(byDoc).map((k) => byDoc[k]);

  const matchRows = docs.map((m, i) =>
    '<div class="matchbox"><label style="margin:0"><input type="radio" name="dupDoc" value="' + m.documentId + '"' + (docs.length === 1 ? ' checked' : '') + '> ' +
    '<strong>#' + m.documentId + ' — ' + esc(m.title) + '</strong></label>' +
    '<div class="muted">' + esc(m.type || '') + (m.renditionNote ? ' · ' + esc(m.renditionNote) : '') +
    ' · uploaded by ' + esc(m.uploader ? m.uploader.username : '?') + ' on ' + esc(m.uploadedAt) +
    ' · ' + esc(m.originalFilename) + ' (' + esc(m.sizeHuman) + ')</div>' +
    '<div class="sha">sha256 ' + esc(m.sha256) + '</div></div>'
  ).join('');

  openModal(
    '<h2>⚠ Identical file already cataloged</h2>' +
    '<p class="muted">The file you are uploading is byte-for-byte identical (' + docs.length + ' matching ' + (docs.length === 1 ? 'entry' : 'entries') + '). What would you like to do?</p>' +
    matchRows +
    '<div class="optrow"><label style="margin:0"><input type="radio" name="dupChoice" value="replace" checked> <span class="opttitle">Replace / overwrite</span></label>' +
    '<div class="muted">Swap the stored file of the selected entry for these bytes. The previous version is kept as history.</div></div>' +
    '<div class="optrow"><label style="margin:0"><input type="radio" name="dupChoice" value="skip"> <span class="opttitle">Skip</span></label>' +
    '<div class="muted">Discard this upload and keep the existing entry untouched.</div></div>' +
    '<div class="optrow"><label style="margin:0"><input type="radio" name="dupChoice" value="catalogDifferent"> <span class="opttitle">Catalog as different</span></label>' +
    '<div class="muted">Create a NEW entry with these bytes (e.g. it is an OCR version, another scan, or a different edition of the same content).</div>' +
    '<div style="margin-top:6px"><label style="margin:0">What makes it different?</label>' +
    '<input type="text" id="dupNote" placeholder="e.g. OCR version, re-scan, corrected edition"></div></div>' +
    '<div class="formerror" id="dupErr"></div>' +
    '<div class="actions"><button class="btn" id="dupApply">Apply</button>' +
    '<button class="btn ghost" id="dupCancel">Cancel upload</button></div>');

  document.getElementById('dupCancel').addEventListener('click', closeModal);
  document.getElementById('dupApply').addEventListener('click', async () => {
    const err = document.getElementById('dupErr');
    err.textContent = '';
    const choice = ($modalHost.querySelector('input[name=dupChoice]:checked') || {}).value;
    const targetId = docs.length === 1
      ? docs[0].documentId
      : (($modalHost.querySelector('input[name=dupDoc]:checked') || {}).value);
    fd.set('resolution', choice);
    if (targetId) fd.set('targetDocumentId', String(targetId));
    const noteEl = document.getElementById('dupNote');
    if (noteEl) fd.set('renditionNote', noteEl.value.trim());

    closeModal();
    try {
      const d = await api('/documents/upload', { method: 'POST', body: fd });
      if (d && d.skipped) {
        toast('Upload skipped — existing entry #' + d.documentId + ' kept.', 'ok');
        showDetail(d.documentId);
        return;
      }
      if (d && d.document) {
        toast(choice === 'replace' ? 'File replaced — prior version kept as history.' : 'Cataloged as a new entry.', 'ok');
        refreshVocab();
        showDetail(d.document.id);
        return;
      }
      toast('Unexpected response.', 'error');
    } catch (ex) {
      toast(ex.message, 'error');
    }
  });
}

/* ---------------- edit metadata ---------------- */

function showEditForm(d) {
  openModal('<h2>Edit metadata — #' + d.id + '</h2><div>' + metaFormHtml(d) + '</div>' +
    '<div class="formerror" id="eErr"></div>' +
    '<div class="actions"><button class="btn" id="eSave">Save</button>' +
    '<button class="btn ghost" id="eCancel">Cancel</button></div>');
  document.getElementById('eCancel').addEventListener('click', closeModal);
  document.getElementById('eSave').addEventListener('click', async () => {
    const err = document.getElementById('eErr');
    err.textContent = '';
    try {
      const body = readMetaForm();
      await api('/documents/' + d.id, { method: 'PUT', body: body });
      closeModal();
      refreshVocab();
      toast('Metadata updated.', 'ok');
      showDetail(d.id);
    } catch (ex) { err.textContent = ex.message; }
  });
}

/* ---------------- admin ---------------- */

async function showAdmin() {
  if (!state.user || state.user.role !== 'admin') return;
  markNav('nav-admin');
  $app.innerHTML = '<h1>Admin — certified users</h1>' +
    '<div class="card"><h2 style="margin-top:0">Create account</h2>' +
    '<div class="formgrid">' +
    '<div><label>Username *</label><input type="text" id="nuUser"></div>' +
    '<div><label>Display name</label><input type="text" id="nuName"></div>' +
    '<div><label>Email</label><input type="text" id="nuEmail"></div>' +
    '<div><label>Initial password *</label><input type="text" id="nuPass"></div>' +
    '<div><label>Role</label><select id="nuRole"><option value="certified">certified</option><option value="admin">admin</option></select></div>' +
    '</div><div class="formerror" id="nuErr"></div>' +
    '<button class="btn" id="nuGo">Create user</button>' +
    '<span class="muted" style="margin-left:12px">Self-registration exists (POST /auth/register) but is disabled by config; approved requests would appear here as pending.</span>' +
    '</div>' +
    '<div id="userBody"></div>';

  document.getElementById('nuGo').addEventListener('click', async () => {
    const err = document.getElementById('nuErr');
    err.textContent = '';
    try {
      await api('/admin/users', { method: 'POST', body: {
        username: document.getElementById('nuUser').value.trim(),
        displayName: document.getElementById('nuName').value.trim(),
        email: document.getElementById('nuEmail').value.trim(),
        password: document.getElementById('nuPass').value,
        role: document.getElementById('nuRole').value,
      }});
      toast('User created.', 'ok');
      renderUsers();
    } catch (ex) { err.textContent = ex.message; }
  });
  renderUsers();
}

async function renderUsers() {
  const body = document.getElementById('userBody');
  if (!body) return;
  let d;
  try { d = await api('/admin/users'); }
  catch (ex) { body.innerHTML = '<div class="card empty">' + esc(ex.message) + '</div>'; return; }

  const rows = d.users.map((u) => {
    const isSelf = u.id === state.user.id;
    return '<tr>' +
      '<td><strong>' + esc(u.username) + '</strong>' + (isSelf ? ' <span class="muted">(you)</span>' : '') + '</td>' +
      '<td>' + esc(u.displayName) + '</td>' +
      '<td>' + esc(u.email || '—') + '</td>' +
      '<td>' + (u.role === 'admin' ? '<span class="badge admin">admin</span>' : '<span class="badge current">certified</span>') + '</td>' +
      '<td><span class="badge ' + u.status + '">' + u.status + '</span></td>' +
      '<td class="muted">' + esc(u.lastLoginAt || 'never') + '</td>' +
      '<td>' +
      (u.status !== 'active' ? '<button class="btn small ghost" data-act="activate" data-id="' + u.id + '">Approve</button> ' : '') +
      (u.status === 'active' && !isSelf ? '<button class="btn small ghost" data-act="suspend" data-id="' + u.id + '">Suspend</button> ' : '') +
      (u.role !== 'admin' && !isSelf ? '<button class="btn small ghost" data-act="makeAdmin" data-id="' + u.id + '">Make admin</button> ' : '') +
      (u.role === 'admin' && !isSelf ? '<button class="btn small ghost" data-act="makeCertified" data-id="' + u.id + '">Demote</button> ' : '') +
      '<button class="btn small ghost" data-act="resetPw" data-id="' + u.id + '">Reset password</button> ' +
      (!isSelf ? '<button class="btn small danger" data-act="del" data-id="' + u.id + '">Delete</button>' : '') +
      '</td></tr>';
  }).join('');

  body.innerHTML =
    '<table class="list"><thead><tr><th>User</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Last login</th><th>Actions</th></tr></thead>' +
    '<tbody>' + rows + '</tbody></table>';

  body.querySelectorAll('button[data-act]').forEach((b) => {
    b.addEventListener('click', async () => {
      const id = b.getAttribute('data-id');
      const act = b.getAttribute('data-act');
      const patch = (body_) => api('/admin/users/' + id, { method: 'PATCH', body: body_ });
      try {
        if (act === 'activate') await patch({ status: 'active' });
        else if (act === 'suspend') await patch({ status: 'disabled' });
        else if (act === 'makeAdmin') await patch({ role: 'admin' });
        else if (act === 'makeCertified') await patch({ role: 'certified' });
        else if (act === 'resetPw') {
          const pw = prompt('New password for this user (min 6 chars):');
          if (!pw) return;
          await api('/admin/users/' + id + '/password', { method: 'PUT', body: { newPassword: pw } });
        } else if (act === 'del') {
          if (!confirm('Delete this user? Their uploads remain in the library.')) return;
          await api('/admin/users/' + id, { method: 'DELETE' });
        }
        toast('Done.', 'ok');
        renderUsers();
      } catch (ex) { toast(ex.message, 'error'); }
    });
  });
}

/* ---------------- boot ---------------- */

(async function boot() {
  if (!state.token) { showLogin(); return; }
  try {
    await afterLogin();
  } catch (e) {
    /* api() already logged us out on 401; other errors surface on the login page */
    if (!state.token) showLogin(e.message);
    else toast(e.message, 'error');
  }
})();
