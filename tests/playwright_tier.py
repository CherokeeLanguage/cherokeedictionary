#!/usr/bin/env python3
"""Regression suite — playwright tier (cherokeedictionary#133 R-1, R-3).

The browser tier: the PoC-validated smoke suite extended with the
session-flow checks (search form-flow RESULTS, CSV download rows) and
the optional session-survival item. NOT a deploy gate (browser
dependency weight) — the explicit deeper check, invoked from the
runbook.

Run:
    pip install playwright && playwright install chromium
    python3 tests/playwright_tier.py                    # dev tier
    python3 tests/playwright_tier.py --base-url https://cherokeedictionary.net:8443

Negative control (SC-3): rehearse by breaking a surface deliberately
(e.g. stop the dict-app service, or point --base-url at a bad port) and
confirm the suite exits nonzero (see tests/README.md).

Admin session survival (devops#14 smoke item 15's executable form, R-1):
rides the suite's ONLY sanctioned credential surface — the seeded test
account (poc/seeds/98-poc-test-admin.sh, credentials from the
operator-held .env, local test containers only). The two-step flow:
    python3 tests/playwright_tier.py --dump-session /tmp/ced-state.json
    # ... restart the containers (the survival condition) ...
    python3 tests/playwright_tier.py --storage-state /tmp/ced-state.json
The first run logs in (POST /admin/login, plaintext DB match per
AdminController.groovy:120) and saves the browser storage state; the
second asserts /admin is still reachable (no redirect home) with the
carried session — check name admin:session-survived.

Targets: --target poc|dev|apex / --base-url — the name->loopback
mapping, the :8443 quirk and the Cloudflare-leak guardrail live in
tests/target.py (the single point of truth; no per-tier hack remains).
"""
import os
import sys
import tempfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from suite_lib import Runner  # noqa: E402
from target import from_argv  # noqa: E402
import regression_config  # noqa: E402

from playwright.sync_api import sync_playwright  # noqa: E402


def dump_session(argv, base, launch_args, dump_path):
    """Pre-restart half of the survival item: login + save storage state."""
    import getpass
    user = os.environ.get("CED_TEST_ADMIN_USER") or input("admin user: ")
    password = os.environ.get("CED_TEST_ADMIN_PASSWORD") or getpass.getpass("admin password: ")
    ok = True
    with sync_playwright() as pw:
        browser = pw.chromium.launch(headless=True, args=launch_args)
        ctx = browser.new_context(ignore_https_errors=True)
        page = ctx.new_page()
        # the login view posts to /admin/login (UrlMappings "/login")
        page.goto(base + "/login", wait_until="domcontentloaded")
        page.fill("input[name='username']", user)
        page.fill("input[name='password']", password)
        page.click("form[action*='/admin/login'] button, "
                   "form[action*='/admin/login'] input[type='submit']")
        page.wait_for_load_state("domcontentloaded")
        # login redirects to "/"; a successful login set session.loggedin,
        # so /admin (AdminController.index) must NOT bounce back home
        page.goto(base + "/admin", wait_until="domcontentloaded")
        if not page.url.startswith(base + "/admin"):
            print("fail:login session not established (redirected to "
                  f"{page.url}) — check CED_TEST_ADMIN_USER/PASSWORD and the "
                  "seeded admin row")
            ok = False
        else:
            ctx.storage_state(path=dump_path)
            print(f"session state saved to {dump_path} — restart the "
                  "containers, then run --storage-state with the same path")
        browser.close()
    return 0 if ok else 1


def main(argv):
    tgt = from_argv(argv)
    base = tgt.base_url
    launch_args = tgt.browser_args()
    runner = Runner("playwright", base)
    print(f"regression suite: playwright tier @ {base} (target={tgt.name})")

    # optional session-survival state file (runbook step)
    storage_state = None
    if "--storage-state" in argv:
        storage_state = argv[argv.index("--storage-state") + 1]

    # --dump-session: log in with the seeded test account (env creds), save
    # the storage state, exit. The pre-restart half of the survival item.
    if "--dump-session" in argv:
        dump_path = argv[argv.index("--dump-session") + 1]
        sys.exit(dump_session(argv, base, launch_args, dump_path))

    results = []

    def record(name, fn, detail_prov=""):
        ok, kind = runner.record(name, fn, regression_config.PERF_ABS_PLAYWRIGHT)
        results.append((name, ok, kind))

    with sync_playwright() as pw:
        browser = pw.chromium.launch(headless=True, args=launch_args)
        ctx = browser.new_context(ignore_https_errors=True,
                                  storage_state=storage_state)
        page = ctx.new_page()

        # 1. Home: 200, search form, syllabary glyphs (home surface)
        def home():
            r = page.goto(base + "/", wait_until="domcontentloaded")
            return r.status == 200, f"status={r.status}"
        record("home:200", home,
               "core surface — first spot-check proven in the #122 deploy")

        def form():
            page.goto(base + "/", wait_until="domcontentloaded")
            # g:form's id is a DOM attribute — select by action
            return page.locator(
                "form[action*='/newSearch/dictionary']").count() == 1, ""
        record("home:search-form", form, "same provenance as home:200")

        def glyphs():
            body = page.inner_text("body")
            n = sum(1 for c in body if 0x13A0 <= ord(c) <= 0x13F5)
            return n >= 1, f"glyphs={n}"
        record("home:syllabary-glyphs", glyphs, "fonts/render surface")

        # 2. Autocomplete: fetch runs IN-PAGE so it rides the browser's
        # host mapping (PoC quirk) / real edge (dev tier)
        def autocomplete():
            page.goto(base + "/", wait_until="domcontentloaded")
            js = ("fetch('/newSearch/ajaxEnglishFinder?term=a')"
                  ".then(r => r.json())")
            j = page.evaluate(js)
            return isinstance(j, list) and len(j) >= 1, f"results={len(j)}"
        record("autocomplete:db-backed-json", autocomplete,
               "DB-backed JSON through the edge")

        # 3. THE form-flow: fill + click → assert RESULTS (not
        # ?noresults=true). Remembers #122: session["results"] only
        # observable here.
        def form_flow():
            page.goto(base + "/", wait_until="domcontentloaded")
            page.fill("#englishSearch", "belt")
            page.click("#englishSearchButton")
            page.wait_for_load_state("domcontentloaded")
            landed = page.url
            noresults = "noresults" in landed
            rows = page.locator("tr[id^='edit']").count()
            return base in landed and not noresults and rows >= 1, \
                f"url={landed} rows={rows} noresults={noresults}"
        record("search:form-flow-results", form_flow,
               "remembers #122 (search session defect)")

        # 4. CSV download (download?id=csv reads session["results"] —
        # same session, in-page fetch) — row count sane
        def download():
            js = ("fetch('/newSearch/download?id=csv')"
                  ".then(r => r.text())")
            text = page.evaluate(js)
            rows = len([l for l in text.splitlines() if l.strip()])
            return rows >= 5, f"rows={rows}"
        record("download:csv-with-rows", download,
               "remembers #122 (session contract of the download)")

        # 5. Entry page: open the first result's detail iframe href
        def entry():
            hrefs = page.locator("tr[id^='edit'] a.iframe")
            if not hrefs.count():
                return False, "no entry links found"
            href = hrefs.first.get_attribute("href")
            r = page.goto(href if href.startswith("http") else base + href,
                          wait_until="domcontentloaded")
            return r.status == 200, f"status={r.status}"
        record("entry:individual-200", entry, "entry detail surface")

        # 6. Audio on the entry page (DB-backed audio path). GATED: the
        # app renders <audio> only when Settings showAudio is true (the
        # PoC dataset seeds it false — 99-poc-settings.sql); a gated-off
        # check is a pass-with-note, not a failure. A RENDERED <audio>
        # without a src IS the failure (the audio asset class).
        def audio():
            if page.locator("audio").count() == 0:
                return True, "audio gated off (showAudio=false) — not applicable"
            src = page.locator("audio source").first.get_attribute("src")
            return bool(src), f"src={src}"
        record("entry:audio-present", audio,
               "remembers the audio asset 200 class (src must resolve)")

        # 7. Grammar guide TOC loaded (hold1.txt fetch)
        def grammar():
            r = page.goto(base + "/grammar", wait_until="networkidle")
            toc = page.evaluate(
                "document.querySelector('#toc') ? "
                "document.querySelector('#toc').innerText.length : -1")
            return r.status == 200 and toc > 100, f"toc={toc}"
        record("grammar:toc-loaded", grammar,
               "the grammar guide (hold1.txt) surface")

        # 8. 404: the custom notFound view (catch-all routing class)
        def notfound():
            r = page.goto(base + "/nonexistent-xyz-regression")
            return r.status == 404, f"status={r.status}"
        record("notfound:404-view", notfound,
               "remembers the catch-all-routing class")

        # 9. Session survival (devops#14 smoke item 15's executable form):
        # rides --storage-state (the runbook's restart carried session);
        # absent the flag, this check reports as a documented skip — never
        # a failure, and never a credentially-dependent gate.
        def survived():
            if not storage_state:
                return True, "skip: no --storage-state supplied (survival " \
                             "item requires the restart flow; see tests/README.md)"
            page.goto(base + "/admin", wait_until="domcontentloaded")
            still_authed = page.url.startswith(base + "/admin")
            return still_authed, \
                f"url={page.url} still_authed={still_authed}"
        record("admin:session-survived", survived,
               "remembers the cookie-contract/session-survival class "
               "(devops#14 smoke item 15); container restart must not "
               "orphan the session")

    print(runner.summary())
    sys.exit(1 if any(not ok for _, ok, _ in results) else 0)


if __name__ == "__main__":
    main(sys.argv[1:])
