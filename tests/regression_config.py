# Regression suite config (cherokeedictionary#133 R-7).
# All tunables are constants here — overrides ride this file, versioned,
# never ad-hoc.

# A check is flagged when its latency exceeds BOTH bands:
#   (a) PERF_MULT x the trailing baseline (median of the check's previous
#       BASELINE_WINDOW recorded good-run latencies), AND
#   (b) its absolute band (seconds).
# A flagged check FAILS the run as `fail:perf`, distinct from `fail`.

PERF_MULT = 2.0
PERF_ABS_CURL = 2.0        # curl tier == single requests (seconds)
PERF_ABS_PLAYWRIGHT = 10.0  # playwright tier page-flow checks (seconds)

# The first BASELINE_WINDOW runs of any check are baseline-collection only:
# no perf verdict is issued, and the run log records that window.
BASELINE_WINDOW = 10
