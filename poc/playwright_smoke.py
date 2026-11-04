"""PoC smoke suite (cherokeelanguage-devops#23 / cherokeedictionary#124 validation).

Playwright-driven checks against the local compose stack (project ced-poc):
browser -> TLS (Caddy internal CA) -> catch-all route -> dict-app:8080 ->
env-provided datasource -> seeded MariaDB 11.8.

Run: python3 poc/playwright_smoke.py
Requires: pip install playwright && playwright install chromium
The host-resolver rule points cherokeedictionary.net at the loopback-published
Caddy (:8443) — the full edge-equivalent chain, no Cloudflare locally.
"""
import json
import sys
from playwright.sync_api import sync_playwright

BASE = "https://cherokeedictionary.net:8443"
ARGS = ["--host-resolver-rules=MAP cherokeedictionary.net 127.0.0.1"]
results = []


def check(name, ok, detail=""):
    results.append((name, bool(ok), detail))
    print(f"{'PASS' if ok else 'FAIL'}  {name}  {detail}")


def main():
    with sync_playwright() as pw:
        browser = pw.chromium.launch(headless=True, args=ARGS)
        ctx = browser.new_context(ignore_https_errors=True)
        page = ctx.new_page()

        # 1. Home: 200, the search form, and Cherokee syllabary glyphs rendered
        r = page.goto(BASE + "/", wait_until="domcontentloaded")
        check("home:200", r.status == 200, f"status={r.status}")
        # g:form's id attr becomes a URL segment, not an HTML id — select by action:
        check("home:search-form",
              page.locator("form[action*='/newSearch/dictionary']").count() == 1)
        body = page.inner_text("body")
        glyphs = [ch for ch in body if 0x13A0 <= ord(ch) <= 0x13F5]
        check("home:syllabary-glyphs", len(glyphs) >= 1, f"glyphs={len(glyphs)}")

        # 2. Autocomplete (DB-backed JSON through the stack). The fetch runs
        # IN-PAGE so it rides the browser's host-resolver mapping — Playwright's
        # APIRequestContext resolves via the OS and would hit the real
        # cherokeedictionary.net through Cloudflare instead of the PoC stack.
        resp_json = page.evaluate(
            "fetch('/newSearch/ajaxEnglishFinder?term=a').then(r => r.json())")
        check("autocomplete:db-backed-json",
              isinstance(resp_json, list) and len(resp_json) >= 1,
              f"results={len(resp_json) if isinstance(resp_json, list) else resp_json}")

        # 3. DB-backed search flow (english search matches definitiond):
        page.fill("#englishSearch", "belt")
        page.click("#englishSearchButton")
        page.wait_for_load_state("domcontentloaded")
        check("search:200", page.url.startswith(BASE), page.url)
        rows = page.locator("tr[id^='edit']").count()
        check("search:results-rows", rows >= 1, f"rows={rows}")
        if rows:
            first = page.locator("tr[id^='edit'] a.iframe").first
            if first.count():
                href = first.get_attribute("href")
                r2 = page.goto(href if href.startswith("http") else BASE + href,
                               wait_until="domcontentloaded")
                check("entry:individual-200", r2.status == 200,
                      f"status={r2.status}")
        page.screenshot(path="poc/search-results.png", full_page=False)

        # 4. Grammar guide: 200 and the TOC loaded its content (hold1.txt fetch)
        r = page.goto(BASE + "/grammar", wait_until="networkidle")
        check("grammar:200", r.status == 200, f"status={r.status}")
        toc_len = page.evaluate(
            "document.querySelector('#toc') ? document.querySelector('#toc').innerText.length : -1")
        check("grammar:toc-loaded", toc_len > 100, f"tocLength={toc_len}")

        # 5. 404: the custom notFound view
        r = page.goto(BASE + "/nonexistent-xyz-poc")
        check("notfound:404-view", r.status == 404, f"status={r.status}")

        # 6. Console errors observed while browsing (informational)
        browser.close()

    failed = [n for n, ok, _ in results if not ok]
    print(f"\n{len(results) - len(failed)}/{len(results)} passed")
    if failed:
        print("FAILED: " + ", ".join(failed))
        sys.exit(1)


if __name__ == "__main__":
    main()