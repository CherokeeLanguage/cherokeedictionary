#!/usr/bin/env python3
"""Regression suite — curl tier (cherokeedictionary#133 R-1, R-3).

Pure-HTTP status + payload-shape assertions over the session-independent
critical surfaces. Runs in seconds. This is the enforced deploy gate
(deploy-stack.sh); the playwright tier (playwright_tier.py) is the
explicit deeper check, NOT a deploy blocker (browser dependency weight).

when meant to be local-only; the mapping, the :8443 quirk and the
Cloudflare-leak guardrail live in tests/target.py — see it for the
doctrine). The playwright tier (playwright_tier.py) is the
explicit deeper check, NOT a deploy blocker (browser dependency weight).

Run:
    python3 tests/curl_tier.py                         # dev tier
    python3 tests/curl_tier.py --target poc            # local PoC stack
    python3 tests/curl_tier.py --base-url <url>        # explicit override

Every check's provenance regression is in-line (R-5); the runbook list
is tests/README.md. Per-check latency is recorded into
tests/runlog.jsonl and gated by the R-7 bands (R-3: nonzero exit on any
failed check, behavior or perf).
"""
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
import http.cookiejar
import ssl

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from suite_lib import Runner  # noqa: E402
from target import from_argv  # noqa: E402
import regression_config  # noqa: E402

CTX = ssl.create_default_context()
CTX.check_hostname = False
CTX.verify_mode = ssl.CERT_NONE  # the PoC stack rides a Caddy internal CA


class Session:
    """One cookie jar shared by the form-flow checks: the search session
    contract (server-side session contents, invisible from a GET) is the
    regression class these checks remember — #122's un-inventoried
    session attribute only ever surfaced on the form-FLOW, so these
    checks must ride one session, not isolated requests."""

    def __init__(self, base):
        self.base = base
        self.jar = http.cookiejar.CookieJar()
        self.opener = urllib.request.build_opener(
            urllib.request.HTTPSHandler(context=CTX),
            urllib.request.HTTPCookieProcessor(self.jar),
            # don't auto-follow on this opener: we assert on redirects
        )

    def get(self, path, timeout=30, follow=True):
        req = urllib.request.Request(self.base + path,
                                     headers={"User-Agent": "ced-regression/1"})
        req.get_method = lambda: "GET"
        return self.opener.open(req, timeout=timeout) if follow else \
            self._no_follow(req, timeout)

    def post(self, path, fields, timeout=30, follow=True):
        data = urllib.parse.urlencode(fields).encode()
        req = urllib.request.Request(self.base + path, data=data,
                                     headers={"User-Agent": "ced-regression/1",
                                              "Content-Type":
                                              "application/x-www-form-urlencoded"})
        req.get_method = lambda: "POST"
        return self.opener.open(req, timeout=timeout) if follow else \
            self._no_follow(req, timeout)

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

    return checks


def main(argv):
    tgt = from_argv(argv)
    base = tgt.base_url
    sess = Session(base)
    runner = Runner("curl", base)
    print(f"regression suite: curl tier @ {base} (target={tgt.name})")
    for name, fn, _prov in build_checks(sess):
        runner.record(name, fn, regression_config.PERF_ABS_CURL)
    print(runner.summary())
    sys.exit(1 if any(not r["ok"] for r in runner.results) else 0)


if __name__ == "__main__":
    main(sys.argv[1:])
