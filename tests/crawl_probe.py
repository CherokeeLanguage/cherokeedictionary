#!/usr/bin/env python3
"""Site crawl probe (cherokeedictionary#133 family) — the deep diagnostic
the four-page dead-reference sweep deliberately is not.

BFS over the same-host link graph from `/`, collecting every response the
browser itself issues plus explicit probes of media/download-shaped links
(the browser only fetches those on click — the /audio lesson-audio class,
#138, hides from any passive crawl). Every same-host >=400 lands in the
defect list WITH its referring page; foreign-host surfaces (the audio CDN,
jquery, fontawesome) are out of scope — they have their own checks.

NOT a deploy gate: a diagnostic instrument (runbook-invoked), bounded so
it always terminates — MAX_PAGES pages, MAX_DEPTH hops, an overall clock.
Run:
    python3 tests/crawl_probe.py                    # dev tier (default)
    python3 tests/crawl_probe.py --target poc
    python3 tests/crawl_probe.py --json out.json    # machine-readable report
"""
import json
import os
import sys
import time
import urllib.parse
from collections import deque

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from target import from_argv, assert_not_edge_leaked  # noqa: E402

from playwright.sync_api import sync_playwright  # noqa: E402

MAX_PAGES = 300       # bound: the crawl must always terminate
MAX_DEPTH = 5
CLOCK_S = 900         # overall bound
MEDIA_EXTS = (".mp3", ".wav", ".ogg", ".m4a", ".zip", ".pdf", ".mp4",
              ".doc", ".docx", ".ppt", ".csv", ".xls", ".xlsx", ".tsv")


def norm(url, base):
    u = urllib.parse.urljoin(base, url.split("#", 1)[0])
    return u.rstrip("/") or None


def main(argv):
    tgt = from_argv(argv)
    base = tgt.base_url
    json_out = None
    if "--json" in argv:
        json_out = argv[argv.index("--json") + 1]

    defects = []          # (status, url, referrer)
    seen_pages = set()
    queue = deque([(base + "/", 0)])
    fetched_media = set()
    t0 = time.monotonic()
    current = [""]

    with sync_playwright() as pw:
        browser = pw.chromium.launch(headless=True, args=tgt.browser_args())
        ctx = browser.new_context(ignore_https_errors=True)

        def guard(response):
            if urllib.parse.urlparse(response.url).hostname != tgt.host:
                return
            assert_not_edge_leaked(response.headers, tgt)
            if response.status >= 400 and "nonexistent-xyz" not in response.url:
                defects.append((response.status, response.url, current[0]))
        ctx.on("response", guard)
        page = ctx.new_page()

        while queue and len(seen_pages) < MAX_PAGES \
                and time.monotonic() - t0 < CLOCK_S:
            url, depth = queue.popleft()
            if url in seen_pages or depth > MAX_DEPTH:
                continue
            seen_pages.add(url)
            current[0] = url
            try:
                resp = page.goto(url, wait_until="domcontentloaded",
                                 timeout=30000)
            except Exception:
                defects.append(("goto-error", url, "-"))
                continue
            if resp is None or resp.status >= 400:
                continue
            # harvest links + probe media-shaped ones the browser won't
            refs = page.eval_on_selector_all(
                "a[href], source[src], img[src], script[src], link[href]",
                """els => els.map(e => ({
                    tag: e.tagName, href: e.href || e.src || '' }))""")
            for r in refs:
                href = r["href"]
                if not href or href.startswith(
                        ("javascript:", "mailto:", "data:", "tel:", "#")):
                    continue
                n = norm(href, url)
                if not n:
                    continue
                if urllib.parse.urlparse(n).hostname != tgt.host:
                    continue
                path = urllib.parse.urlparse(n).path.lower()
                if r["tag"] != "A":
                    continue  # subresources are already wire-guarded
                # download-shaped links: no page navigation is possible
                # (chromium aborts the goto) — probe them instead; the
                # /corpus/download/* endpoints carry the format as a
                # PATH SEGMENT, not an extension
                if any(path.endswith(e) for e in MEDIA_EXTS) \
                        or "/download" in path:
                    if n not in fetched_media:
                        fetched_media.add(n)
                        try:
                            m = ctx.request.head(n, timeout=20000)
                            if m.status >= 400:
                                defects.append((m.status, n, url))
                        except Exception:
                            defects.append(("probe-error", n, url))
                else:
                    queue.append((n, depth + 1))

        browser.close()

    # dedupe by (status, url) keeping the first referrer
    uniq = {}
    for s, u, ref in defects:
        uniq.setdefault((s, u), ref)
    lines = [f"crawl probe @ {base} (target={tgt.name}): "
             f"{len(seen_pages)} pages, {len(uniq)} defects"]
    for (s, u), ref in sorted(uniq.items(),
                              key=lambda x: (str(x[0][0]), x[0][1])):
        lines.append(f"  {s}  {u}   [via {ref}]")
    print("\n".join(lines))
    if json_out:
        json.dump({"base": base, "pages": len(seen_pages),
                   "defects": [{"status": str(s), "url": u, "via": r}
                               for (s, u), r in
                               sorted(uniq.items(),
                                      key=lambda x: (str(x[0][0]),
                                                     x[0][1]))]},
                  open(json_out, "w"), indent=1)
    sys.exit(1 if uniq else 0)


if __name__ == "__main__":
    main(sys.argv[1:])
