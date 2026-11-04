"""The suite's target driver — single point of truth (cherokeedictionary#133).

This module is the PERMANENT fix for the recurring "tests leak out
through Cloudflare when they are meant to be local-only" issue: every
tier imports from here; no tier carries its own name-mapping hack.

Doctrine:
- Known targets are a named table (`--target poc|dev|apex`, or
  CED_TARGET). The base URL is the suite's only variable BETWEEN targets
  — never inside per-assertion code.
- LOCAL targets (the PoC stack) are reached as the hostnames they
  SERVE (e.g. https://cherokeedictionary.net:8443) but the name maps to
  loopback, enforced at process level: the driver patches
  socket.getaddrinfo for the serving hostname AND returns browser launch
  args (--host-resolver-rules) for the browser process tiers. Both the
  parent process and the browser are covered — one place, one truth.
- GUARDRAIL: if a local target's hostname resolves to anything other
  than loopback, the driver aborts LOUDLY before the first check runs —
  a Cloudflare 521 from the wrong destination is a driver failure, not
  a check failure.
- Explicit --base-url overrides that carry the local-port quirk get the
  same treatment (quirk lives here, not in per-assertion exceptions).
"""
import os
import socket
import urllib.parse

TARGETS = {
    "poc": "https://cherokeedictionary.net:8443",
    "dev": "https://dev.cherokeedictionary.net",
    "apex": "https://cherokeedictionary.net",
}

DEFAULT_TARGET = "dev"


class Target:
    def __init__(self, name, base_url):
        self.name = name
        self.base_url = base_url.rstrip("/")
        self.host = urllib.parse.urlparse(self.base_url).hostname
        self.port = urllib.parse.urlparse(self.base_url).port
        self.local = self._is_local()

    def _is_local(self):
        """A local target is the loopback-published PoC stack: the port
        quirk (:8443, Caddy published on 127.0.0.1) is the manifest, not
        a heuristic buried in assertion code."""
        return self.port == 8443 or self.name == "poc"

    def browser_args(self):
        if not self.local:
            return []
        return [f"--host-resolver-rules=MAP {self.host} 127.0.0.1"]

    def enforce_resolution(self):
        """The guardrail: a local target MUST resolve to loopback after
        the driver's patch — abort before any check runs otherwise."""
        if not self.local:
            return
        infos = socket.getaddrinfo(self.host, None)
        addrs = {i[4][0] for i in infos}
        if not addrs <= {"127.0.0.1", "::1"}:
            raise SystemExit(
                f"TARGET DRIVER GUARDRAIL: local target '{self.target_name()}' "
                f"({self.host}) resolved to {sorted(addrs)} — NOT loopback. "
                "The run would have leaked to the public edge (Cloudflare). "
                "Aborting before any check runs; do not run the suite past "
                "this line — fix the mapping in tests/target.py only.")

    def target_name(self):
        return self.name


def install_local_resolver(target):
    """Process-level name mapping: ~socket.getaddrinfo for the serving
    hostname, equivalent to curl --resolve / the browser's
    --host-resolver-rules, in the one place it belongs. Returns a
    restore hook (unused in suite lifetimes; suites are one-shot)."""
    orig = socket.getaddrinfo

    def resolve(host, *a, **kw):
        if host == target.host:
            return orig("127.0.0.1", *a, **kw)
        return orig(host, *a, **kw)
    socket.getaddrinfo = resolve
    return orig


def from_argv(argv):
    """Target precedence: --target / CED_TARGET > --base-url > default.
    Both --base-url with the local port quirk and any named local target
    get the loopback enforcement."""
    name = None
    if "--target" in argv:
        name = argv[argv.index("--target") + 1]
    name = os.environ.get("CED_TARGET", name)

    if name:
        if name not in TARGETS:
            raise SystemExit(
                f"unknown target '{name}' — known: {sorted(TARGETS)}")
        url = TARGETS[name]
    elif "--base-url" in argv:
        url = argv[argv.index("--base-url") + 1]
        name = "explicit"
    else:
        name, url = DEFAULT_TARGET, TARGETS[DEFAULT_TARGET]

    t = Target(name, url)
    if t.local:
        install_local_resolver(t)
        t.enforce_resolution()
    return t
