#!/usr/bin/env python3
"""The SC-7 forced-slow rehearsal tool (cherokeedictionary#133).

A TLS-terminating delay proxy to stand IN FRONT OF a stack (typically the
loopback PoC) while the suite runs against the proxy: one path is delayed
by the injected amount, so that check's latency exceeds both R-7 bands
(2x the trailing baseline AND the tier's absolute band) and the suite
must report `fail:perf` and exit nonzero.

The proxy is rehearsal-only infrastructure: it is never part of the gate,
never pointed at a serving tier, and binds loopback only.

LEAK DOCTRINE (tests/target.py): this tool is exactly the component kind
that reintroduced the Cloudflare-leak class on 2026-11-04 — it parsed its
own argv and resolved the serving hostname itself, sending the "local"
request to the public edge (a 521, noticed only by human inspection).
Both layers of the fix live here now:
- CAUSE: the connect address comes from target.connect_target() — this
  tool NEVER resolves the serving name;
- OUTCOME: every forwarded response passes assert_not_edge_leaked() — a
  Cloudflare-marked response to a local target aborts the proxy loudly.

Run:
    openssl req -x509 -newkey rsa:2048 -nodes -days 1 \
        -subj "/CN=cherokeedictionary.net" -keyout /tmp/proxy.key \
        -out /tmp/proxy.crt
    python3 tests/perf_delay_proxy.py /tmp/proxy.crt /tmp/proxy.key \
        127.0.0.1 8444 poc /newSearch/ajaxEnglishFinder 2.5

    # warm the target's baseline (10 good runs per check — no perf
    # verdict until then), then:
    python3 tests/curl_tier.py --base-url <the target's base URL as the
    proxy serves it>  # e.g. https://localhost:8444 (explicit override)
    # → the delayed check flags: `fail:perf`, summary PERF line, exit 1
"""
import http.client
import os
import ssl
import sys
import threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from target import connect_target, assert_not_edge_leaked  # noqa: E402

CERT, KEY, LISTEN_HOST, LISTEN_PORT, TARGET, DELAY_PATH, DELAY_S = sys.argv[1:8]
DELAY_S = float(DELAY_S)
UPSTREAM_CTX = ssl.create_default_context()
UPSTREAM_CTX.check_hostname = False
UPSTREAM_CTX.verify_mode = ssl.CERT_NONE  # the PoC stack rides a Caddy internal CA

tgt, connect_host, connect_port = connect_target(TARGET)


class Handler(BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def do_GET(self):
        self._forward("GET")

    def do_POST(self):
        self._forward("POST")

    def _forward(self, method):
        if self.path.startswith(DELAY_PATH):
            threading.Event().wait(DELAY_S)
        length = int(self.headers.get("Content-Length") or 0)
        body = self.rfile.read(length) if length else None
        # connect VIA THE SERVING NAME (correct SNI for Caddy's site
        # block) — the loopback pinning is the process-level mapping
        # connect_target() installed; resolving here is therefore still
        # the driver's truth, never the OS resolver's.
        conn = http.client.HTTPSConnection(
            tgt.host, tgt.port or 443, context=UPSTREAM_CTX, timeout=60)
        headers = {k: v for k, v in self.headers.items()
                   if k.lower() not in ("host", "connection")}
        headers["Host"] = tgt.host
        conn.request(method, self.path, body=body, headers=headers)
        resp = conn.getresponse()
        assert_not_edge_leaked(
            dict((k.lower(), v) for k, v in resp.getheaders()), tgt)
        payload = resp.read()
        self.send_response(resp.status)
        for k, v in resp.getheaders():
            if k.lower() in ("transfer-encoding", "connection", "content-length"):
                continue
            self.send_header(k, v)
        self.send_header("Content-Length", str(len(payload)))
        self.send_header("Connection", "close")
        self.end_headers()
        self.wfile.write(payload)
        conn.close()


def main():
    ctx = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    ctx.load_cert_chain(CERT, KEY)
    server = ThreadingHTTPServer((LISTEN_HOST, int(LISTEN_PORT)), Handler)
    server.socket = ctx.wrap_socket(server.socket, server_side=True)
    print(f"delay proxy: https://{LISTEN_HOST}:{LISTEN_PORT} ->"
          f" {tgt.host}:{tgt.port or 443} (pinned {connect_host},"
          f" Host: {tgt.host}), delaying {DELAY_PATH} by {DELAY_S}s",
          flush=True)
    server.serve_forever()


if __name__ == "__main__":
    main()
