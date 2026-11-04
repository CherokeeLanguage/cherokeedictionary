# Regression suite config (cherokeedictionary#133 R-7).
# All tunables are constants here — overrides ride this file, versioned,
# never ad-hoc.

# A check is flagged when its latency exceeds BOTH bands:
#   (a) PERF_MULT x the trailing baseline (median of the check's previous
#       BASELINE_WINDOW recorded good-run latencies, per target), AND
#   (b) its absolute band (seconds).
# A flagged check FAILS the run as `fail:perf`, distinct from `fail`.

PERF_MULT = 2.0

# Absolute bands are per latency class. The spec's 2.0 s curl figure was
# derived against the loopback-published PoC stack (local RTT); the remote
# tiers ride the public edge, where measured single-request jitter
# (2026-11-04, dev tier, back-to-back runs: 0.17 s – 26.8 s across the same
# checks) routinely exceeds any tight constant. The bands stay constants in
# this file — the sanctioned, versioned override path — with the local
# class keeping the spec's original values.
PERF_ABS = {
    ("curl", "local"): 2.0,      # curl tier == single requests, loopback RTT
    ("curl", "remote"): 30.0,    # curl tier over the public edge
    ("playwright", "local"): 10.0,   # playwright page-flow checks, loopback
    ("playwright", "remote"): 60.0,  # playwright page-flow checks, remote
}

# Per-check absolute-band overrides: a check whose latency belongs to a
# surface other than its target rides that surface's reality, not the
# target's band. The audio-asset check (curl) fetches the EXTERNAL CDN
# (data.cherokee.org) even on a local target — the internet's normal
# jitter (1.3–6 s observed) sits far above the local 2.0 s band, and a
# perf flag on the CDN's weather is noise, not signal.
PERF_ABS_OVERRIDES = {
    ("curl", "local", "audio:asset-200"): 8.0,
}

# The first BASELINE_WINDOW runs of any check (per target) are
# baseline-collection only: no perf verdict is issued, and the run log
# records that window.
BASELINE_WINDOW = 10

# The dead-reference sweep's per-run ref cap (R-8): the curl tier must
# stay fast — the crawl verifies at most this many unique same-host
# references (deterministic order; overages are noted in the detail).
DEAD_REF_MAX = 60
