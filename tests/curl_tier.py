#!/usr/bin/env python3
"""Regression suite — curl tier (cherokeedictionary#133 R-1, R-3).

Pure-HTTP status + payload-shape assertions over the session-independent
critical surfaces. Runs in seconds. This is the enforced deploy gate
(deploy-stack.sh); the playwright tier (playwright_tier.py) is the
explicit deeper check, NOT a deploy blocker (browser dependency weight).

Targets: --target poc|dev|apex / CED_TARGET / --base-url (when meant to
be local-only; the mapping, the :8443 quirk and the Cloudflare-leak
guardrail live in tests/target.py — see it for the doctrine).

Run:
    python3 tests/curl_tier.py                         # dev tier
    python3 tests/curl_tier.py --target poc            # local PoC stack
    python3 tests/curl_tier.py --base-url <url>        # explicit override

Every check's provenance regression is in-line (R-5); the runbook list
is tests/README.md. Per-check latency is recorded into the local, untracked run log
(tests/runlog.jsonl — gitignored, never committed) and gated by the
R-7 bands (R-3: nonzero exit on any
failed check, behavior or perf).
"""
import json
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
import http.cookiejar
import ssl

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from suite_lib import Runner  # noqa: E402
import regression_config  # noqa: E402
from target import from_argv, assert_not_edge_leaked  # noqa: E402

CTX = ssl.create_default_context()
CTX.check_hostname = False
CTX.verify_mode = ssl.CERT_NONE  # the PoC stack rides a Caddy internal CA

# artifact extensions that make a collected ref a FILE (assets) rather
# than a bare endpoint; query-ful hrefs are skipped (parameterized
# generated endpoints carry their own context — the dedicated checks
# cover the ones that matter)
ASSET_EXTS = (".css", ".js", ".mjs", ".png", ".jpg", ".jpeg", ".gif",
              ".svg", ".ico", ".woff", ".woff2", ".ttf", ".otf", ".eot",
              ".txt", ".pdf", ".mp3", ".wav", ".m4a", ".json", ".map")

# paths the REAL stack serves outside the app (#141): /audio/* is the
# edge/origin lesson-audio tree (#138's site-relative lesson refs);
# /cnt is the Cherokee New Testament static deployment
# (cherokeelanguage-devops#29) riding the nav layout. A stack without
# these mounts — the repository-alone PoC stack — can never resolve
# them; the sweep skips them there (and says so in its detail line),
# and checks them strictly on every stack that has the mounts.
EDGE_SERVED_PATHS = ("/audio/", "/cnt/")


def is_edge_served(ref_url):
    """True when the ref's PATH is one the real edge/origin serves
    outside the app: the /audio/* tree, the exact /cnt nav target, or
    the /cnt/* tree."""
    p = urllib.parse.urlparse(ref_url).path
    return p == "/cnt" or p.startswith(EDGE_SERVED_PATHS)


class Session:
    """One cookie jar shared by the form-flow checks: the search session
    contract (server-side session contents, invisible from a GET) is the
    regression class these checks remember — #122's un-inventoried
    session attribute only ever surfaced on the form-FLOW, so these
    checks must ride one session, not isolated requests.

    Every response passes the edge-leak tripwire (target.py): a LOCAL
    target's response carrying Cloudflare markers aborts the run — the
    leak class is observed at the wire, not left to human inspection."""

    def __init__(self, tgt):
        self.tgt = tgt
        self.base = tgt.base_url
        self.jar = http.cookiejar.CookieJar()
        self.opener = urllib.request.build_opener(
            urllib.request.HTTPSHandler(context=CTX),
            urllib.request.HTTPCookieProcessor(self.jar),
            # don't auto-follow on this opener: we assert on redirects
        )

    def _guard(self, resp, url):
        """The tripwire applies only to responses from the TARGET's own
        host — absolute URLs to external surfaces (the audio CDN) ride
        the same opener and must never trip it."""
        if urllib.parse.urlparse(url).hostname == self.tgt.host:
            assert_not_edge_leaked(resp.headers, self.tgt)
        return resp

    def _url(self, path):
        """base-relative by contract; an absolute URL (the audio assets
        live on the external CDN) passes through untouched."""
        return path if path.startswith("http") else self.base + path

    # the per-request timeout sits ABOVE the widest perf band
    # (regression_config.PERF_ABS) so a check can always OBSERVE its own
    # band — a server slow beyond the band must yield a measurable
    # latency (fail:perf), not a client-side timeout artifact
    TIMEOUT = 60

    def get(self, path, timeout=TIMEOUT, follow=True):
        url = self._url(path)
        req = urllib.request.Request(url,
                                     headers={"User-Agent": "ced-regression/1"})
        req.get_method = lambda: "GET"
        return self._guard(self.opener.open(req, timeout=timeout), url) if follow else \
            self._guard(self._no_follow(req, timeout), url)

    def post(self, path, fields, timeout=TIMEOUT, follow=True):
        url = self._url(path)
        data = urllib.parse.urlencode(fields).encode()
        req = urllib.request.Request(url, data=data,
                                     headers={"User-Agent": "ced-regression/1",
                                              "Content-Type":
                                              "application/x-www-form-urlencoded"})
        req.get_method = lambda: "POST"
        return self._guard(self.opener.open(req, timeout=timeout), url) if follow else \
            self._guard(self._no_follow(req, timeout), url)

    def _no_follow(self, req, timeout):
        """Opener that returns redirect responses without following."""
        class NoRedirect(urllib.request.HTTPRedirectHandler):
            def redirect_request(self, creq):
                return None
        op = urllib.request.build_opener(
            NoRedirect(), urllib.request.HTTPSHandler(context=CTX),
            urllib.request.HTTPCookieProcessor(self.jar))
        try:
            return op.open(req, timeout=timeout)
        except urllib.error.HTTPError as e:
            return e

def body(resp):
    return resp.read().decode("utf-8", "replace")


def has_glyphs(s):
    return any(0x13A0 <= ord(c) <= 0x13F5 for c in s)


def fixture_entry_id():
    """The deterministic entry id the fixture generator recorded
    (tests/fixture_audio_entry.txt) — the audio-linked entry every target
    derived from the operator dumps also carries (the fixture is a subset
    of the same dump, ids intact)."""
    path = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                        "fixture_audio_entry.txt")
    if not os.path.exists(path):
        return None
    return open(path).read().strip() or None


def build_checks(sess):
    checks = []

    # home page: the core surface (200, the search form, syllabary rendered)
    def home():
        r = sess.get("/")
        b = body(r)
        ok = (r.status == 200
              and "/newSearch/dictionary" in b
              and has_glyphs(b))
        return ok, (f"status={r.status}" if r.status != 200
                    else "form/glyphs missing" if not ok else "")
    checks.append(("home:200-form-syllabary", home,
                   "the core delivered surface; first curl spot-check proven "
                   "during the #122 deployment"))

    # autocomplete: DB-backed JSON through the full edge chain
    def autocomplete():
        r = sess.get("/newSearch/ajaxEnglishFinder?" +
                     urllib.parse.urlencode({"term": "belt"}))
        b = body(r)
        ok = r.status == 200
        n = len(json.loads(b)) if ok else -1
        return ok and n >= 1 and isinstance(json.loads(b), list) and ok, \
            f"json_results={n}"
    checks.append(("autocomplete:db-backed-json", autocomplete,
                   "DB-backed JSON endpoint (newSearch/ajaxEnglishFinder); "
                   "verifies datasource/edge chain, proven in the PoC "
                   "playwright run"))

    # THE form-flow: POST the search form and assert RESULTS return —
    # not the noresults redirect. Remembers **the #122 regression**: the
    # search 500'd/redirected in production while every GET-only spot
    # check passed, because the un-inventoried session attribute
    # (session["results"], NewSearchController.groovy:123) is observable
    # only through this flow.
    def form_flow():
        r = sess.post("/newSearch/dictionary",
                      {"englishSearch": "belt", "englishSearchButton": ""},
                      follow=False)
        # follow the redirect chain ourselves on the same cookie jar
        hops = 0
        while 300 <= r.status < 400 and r.headers.get("Location") and hops < 5:
            loc = r.headers["Location"]
            path = loc if loc.startswith("/") or loc.startswith("http") \
                else urllib.parse.urljoin(sess.base, loc)
            if path.startswith("http") and not path.startswith(sess.base):
                path = sess.base + urllib.parse.urlparse(loc).path + "?" + \
                    urllib.parse.urlparse(loc).query
            r = sess.get(urllib.parse.urlparse(path).path + "?" +
                         urllib.parse.urlparse(path).query, follow=False)
            hops += 1
        b = body(r)
        landed_noresults = ("noresults" in r.url) or ("noresults" in b)
        return r.status == 200 and not landed_noresults, \
            f"status={r.status} hops={hops} noresults={landed_noresults}"
    checks.append(("search:form-flow-results", form_flow,
                   "remembers #122: the search session defect (un-inventoried "
                   "session attr) only surfaced on this POST flow — GET "
                   "checks never exercised it"))

    # download, ON THE SAME SESSION: /newSearch/download reads
    # session["results"] (NewSearchController.groovy:359-360) — the
    # session contract regression class again; id=csv (the spec's
    # "css|csv" was a typo — the controller accepts excel|csv|tab).
    def download():
        r = sess.get("/newSearch/download?" + urllib.parse.urlencode(
            {"id": "csv"}))
        ctype = r.headers.get("Content-Type", "")
        rows = 0
        if r.status == 200 and "csv" in ctype:
            rows = sum(1 for line in r.read().splitlines() if line.strip())
        return r.status == 200 and "csv" in ctype and rows >= 5, \
            f"status={r.status} ctype={ctype} rows={rows}"
    checks.append(("download:csv-with-rows", download,
                   "remembers #122: download streams from session['results'] "
                   "— a csv with real rows proves the session contract "
                   "end-to-end"))

    # 404: the custom notFound view survives (catch-all routing must not
    # shadow the app's mappings nor the framework's 404)
    def notfound():
        try:
            r = sess.get("/nonexistent-xyz-regression")
            return r.status == 404, f"status={r.status}"
        except urllib.error.HTTPError as e:
            return e.code == 404, f"status={e.code}"
    checks.append(("notfound:404-view", notfound,
                   "remembers the catch-all-routing class: the Caddyfile "
                   "catch-all routes to dict-app, so unknown paths must "
                   "produce the app's 404 (PoC playwright-validated)"))

    # entry detail via the app's own link shape (_desktopView.gsp:
    # /newSearch/individual?id=…): the page renders through the
    # relational JOIN chain — likespreadsheets → SourceManagement
    # (bibliography) — so a broken schema/FK chain (#118's replacement
    # DDL, #124's default_catalog finding) 500s or blanks the source
    # block. Remembers the FK/schema failure class.
    def entry_joins():
        eid = fixture_entry_id()
        if not eid:
            return True, "skip: no fixture entry id recorded"
        r = sess.get("/newSearch/individual?" + urllib.parse.urlencode(
            {"id": eid}))
        if r.status != 200:
            return False, f"status={r.status} (entry {eid})"
        b = body(r)
        m = re.search(r"<b>Source:</b><br/>\s*([^<]{3,})", b)
        return "<b>Source:</b>" in b and m is not None and has_glyphs(b), \
            f"status={r.status} source_attribution={'yes' if m else 'MISSING'}"
    checks.append(("entry:schema-joins-render", entry_joins,
                   "remembers the FK/schema failure class (#118 replacement "
                   "DDL, #124 default_catalog finding): the entry page joins "
                   "likespreadsheets→SourceManagement; a broken chain 500s "
                   "or renders an empty source attribution"))

    # audio asset 200: the entry page's <audio><source src=…> points at
    # the audio CDN (data.cherokee.org LexiconSoundFiles); the asset
    # itself must serve. Session-independent (the entry page writes no
    # session state). Remembers the audio-asset class proven during the
    # #122 deployment spot checks.
    def audio_asset():
        eid = fixture_entry_id()
        if not eid:
            return True, "skip: no fixture entry id recorded"
        r = sess.get("/newSearch/individual?" + urllib.parse.urlencode(
            {"id": eid}))
        if r.status != 200:
            return False, f"entry status={r.status} (entry {eid})"
        b = body(r)
        m = re.search(r'src="([^"]+)"\s*type="audio/mpeg"', b)
        if not m:
            return False, f"no <audio> src on entry {eid} (audio path broken)"
        src = m.group(1)
        a = sess.get(src)
        ctype = a.headers.get("Content-Type", "")
        magic = a.read(4)
        ok = (a.status == 200
              and (ctype.startswith("audio/") or magic[:3] == b"ID3"
                   or magic[:2] in (b"\xff\xfb", b"\xff\xf3")))
        return ok, f"asset status={a.status} ctype={ctype}"
    checks.append(("audio:asset-200", audio_asset,
                   "remembers the audio-asset class (the #122-deploy curl "
                   "spot check): the DB-backed audio path must yield a "
                   "serving asset, not just a rendered <audio> tag"))

    # Google Search Console domain-verification file (#140): the legacy
    # nginx doc-root file must serve from the app at its exact path with
    # the exact legacy content — a failed re-verification is invisible
    # until Google is asked, so the check rides the gate.
    def gsc_verification():
        r = sess.get("/google593a568777046d5c.html")
        b = body(r)
        ok = r.status == 200 and \
            "google-site-verification: google593a568777046d5c.html" in b
        return ok, f"status={r.status} content={'match' if ok else 'MISMATCH'}"
    checks.append(("gsc:verification-200", gsc_verification,
                   "#140: the Google site-verification file must serve at "
                   "the site root with the exact legacy content (the "
                   "ced1→vhost-58 migration dropped the nginx doc-root "
                   "route; a 404 only surfaces at re-verification time)"))

    # dead-reference sweep (R-8): the key pages' GENERATED + STATIC
    # same-host references (src/href attributes, url() refs — the inline
    # <style> blocks carry the @font-face srcs — plus one level into the
    # pages' own same-host CSS) must ALL resolve. Remembers the FreeSerif
    # class (2026-11-04 audit: a @font-face whose sources don't exist in
    # the repo — a dead asset reference never changes a page's status
    # code, so it is invisible to every status-based check).
    def harvest(text, base_url, refs):
        def add(kind, val):
            val = val.strip().split("#", 1)[0]
            if not val or val.startswith(("javascript:", "mailto:", "data:",
                                          "tel:", "about:")):
                return
            absu = urllib.parse.urljoin(base_url, val)
            if urllib.parse.urlparse(absu).hostname != sess.tgt.host:
                return  # external surfaces have their own checks
            if kind == "href" and ("?" in val
                                   or val.lower().endswith(ASSET_EXTS)):
                # parameterized generated endpoints carry their own
                # context (the dedicated checks cover those); files are
                # kept via the extension branch below — this gate only
                # drops query-ful hrefs
                if "?" in val:
                    return
            refs.add(absu)

        for kind, val in re.findall(r'(src|href)\s*=\s*["\']([^"\']+)["\']',
                                    text, re.I):
            add(kind.lower(), val)
        for val in re.findall(r"url\(\s*['\"]?([^'\")]+)['\"]?\s*\)",
                              text, re.I):
            add("url", val)

    def referenced_resolve():
        eid = fixture_entry_id()
        # the crawled set: the delivered pages whose references the old
        # host served and the migration lost — /readingAndListening is
        # here since 2026-11-04 (the /audio lesson-audio class, #138)
        pages = ["/", "/grammar", "/readingAndListening"]
        if eid:
            pages.append("/newSearch/individual?" + urllib.parse.urlencode(
                {"id": eid}))
        refs = set()
        for p in pages:
            r = sess.get(p)
            if r.status != 200:
                return False, f"crawl source {p} status={r.status}"
            harvest(body(r), sess._url(p), refs)
        # one level into the pages' own same-host CSS
        for css in [u for u in list(refs) if u.lower().endswith(".css")]:
            r = sess.get(css)
            if r.status == 200:
                harvest(body(r), css, refs)
        ordered = sorted(refs)
        # edge-served-only refs on a stack without the mounts (#141):
        # skip them and SAY SO — the skip is stated in the detail line,
        # never silent; a stack that serves these paths checks them
        # strictly like every other ref
        skipped_edge = 0
        if not sess.tgt.serves_edge_static:
            skipped_edge = sum(1 for u in ordered if is_edge_served(u))
            ordered = [u for u in ordered if not is_edge_served(u)]
        skip_note = (f" ({skipped_edge} edge-served skipped: stack has no "
                     "/audio, /cnt mounts)" if skipped_edge else "")
        cap = regression_config.DEAD_REF_MAX
        truncated = len(ordered) > cap
        dead = []
        for u in ordered[:cap]:
            try:
                resp = sess.get(u)
                if resp.status >= 400:
                    dead.append(f"{resp.status} {u}")
            except urllib.error.HTTPError as e:
                dead.append(f"{e.code} {u}")
            except Exception as e:
                dead.append(f"transport {type(e).__name__} {u}")
        if dead:
            shown = "; ".join(dead[:5])
            more = f" (+{len(dead) - 5} more)" if len(dead) > 5 else ""
            return False, (f"{len(dead)} dead of {len(ordered[:cap])}: "
                           f"{shown}{more}{skip_note}")
        return True, (f"{len(ordered[:cap])} refs all resolve"
                      + (f" ({len(ordered) - cap} over the bound)"
                         if truncated else "")
                      + skip_note)
    checks.append(("assets:referenced-resolve", referenced_resolve,
                   "remembers the FreeSerif font-face class (2026-11-04 "
                   "audit): generated + static same-host references on the "
                   "key pages (src/href/url(), one CSS level deep) must all "
                   "resolve — dead references are invisible to status-based "
                   "checks. #141: the /audio/* lesson files and the /cnt "
                   "nav item are served OUTSIDE the app on the real stack "
                   "(edge/origin static tree, devops#29 static deployment) "
                   "— on a stack without those mounts (the PoC) the sweep "
                   "skips exactly those refs, explicitly, and checks "
                   "everything else strictly"))

    return checks


def main(argv):
    tgt = from_argv(argv)
    sess = Session(tgt)
    runner = Runner("curl", tgt.base_url, tgt.latency_class())
    # unrecorded warmup: the first request after a boot rides JIT-cold
    # compilation — recording it would flag boot weather (a 2–4 s home
    # hit against a ~0.01 s loopback baseline) as fail:perf instead of
    # the drift the perf record exists to catch. A genuinely broken
    # surface fails in the checks regardless.
    try:
        sess.get("/")
    except Exception:
        pass
    print(f"regression suite: curl tier @ {tgt.base_url} (target={tgt.name})")
    for name, fn, _prov in build_checks(sess):
        runner.record(name, fn)
    print(runner.summary())
    sys.exit(1 if any(not r["ok"] for r in runner.results) else 0)


if __name__ == "__main__":
    main(sys.argv[1:])
