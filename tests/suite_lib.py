"""Shared machinery for the regression suite (cherokeedictionary#133).

Target selection lives in tests/target.py (the named-targets table,
the local name→loopback mapping and the Cloudflare-leak guardrail —
single point of truth; this module carries no URL logic).

Perf record (R-7): every check records its wall-clock latency into the
append-only run log (tests/runlog.jsonl — local and untracked, gitignored;
never committed). The trailing
baseline is the median of that check's previous good-run latencies in
the log (capped at BASELINE_WINDOW). A check flagged when its latency
exceeds BOTH bands (config: PERF_MULT x baseline, and the tier's
absolute band) FAILS the run as `fail:perf`, distinct from the behavior
verdict `fail`. The first BASELINE_WINDOW runs of any check are
baseline-collection only: no perf verdict is issued and the record says
so.
"""
import json
import os
import statistics
import subprocess
import time
from datetime import datetime, timezone

import regression_config

HERE = os.path.dirname(os.path.abspath(__file__))
RUNLOG = os.path.join(HERE, "runlog.jsonl")


def git_head():
    try:
        return subprocess.run(
            ["git", "rev-parse", "HEAD"], capture_output=True, text=True,
            cwd=os.path.dirname(HERE)).stdout.strip()[:12]
    except OSError:
        return "unknown"


class Runner:
    """One line per check (`pass|fail: name`) plus a summary; nonzero
    exit on any failed check (behavior or perf)."""

    def __init__(self, tier, base_url, target_class, log_path=RUNLOG):
        self.tier = tier
        self.base_url = base_url
        # the latency class of the target (local loopback vs remote edge)
        # — selects the absolute band and keys the trailing baseline:
        # a loopback PoC latency is not a baseline for a remote-edge run
        # (R-2 makes targets first-class; the runlog records base_url per
        # record and the baseline window is per target)
        self.target_class = target_class
        self.log_path = log_path
        self.results = []  # dicts: name, ok, kind ('behavior'|'perf'), detail, latency

    def _baseline(self, name):
        """Trailing baseline: the median of the check's previous good-RUN
        latencies FOR THIS TARGET. Only behavior-kind records count —
        each post-window run also appends a perf-kind record (a duplicate
        observation, not a run); counting both would halve the effective
        window."""
        latencies = []
        if os.path.exists(self.log_path):
            with open(self.log_path) as f:
                for line in f:
                    try:
                        r = json.loads(line)
                    except ValueError:
                        continue
                    if (r.get("tier") == self.tier
                            and r.get("check") == name
                            and r.get("base_url") == self.base_url
                            and r.get("kind") == "behavior"
                            and r.get("verdict") == "pass"
                            and r.get("latency_s") is not None):
                        latencies.append(r["latency_s"])
        window = latencies[-regression_config.BASELINE_WINDOW:]
        return (statistics.median(window) if window else None, len(latencies))

    def _log(self, rec):
        with open(self.log_path, "a") as f:
            f.write(json.dumps(rec) + "\n")

    def record(self, name, fn):
        """Run fn() -> (ok, detail); time it; evaluate the perf bands (the
        absolute band comes from the config's table for this tier and
        target class). Transport exceptions (connection reset, HTTPError)
        are check FAILURES, never crashes — the suite must always produce
        its one-line verdicts and a summary (R-3)."""
        absolute = regression_config.PERF_ABS_OVERRIDES.get(
            (self.tier, self.target_class, name),
            regression_config.PERF_ABS[(self.tier, self.target_class)])
        t0 = time.monotonic()
        try:
            ok, detail = fn()
        except Exception as e:
            ok, detail = False, f"transport: {type(e).__name__}: {e}"
        latency = round(time.monotonic() - t0, 3)

        baseline, good_runs = self._baseline(name)
        collecting = good_runs < regression_config.BASELINE_WINDOW

        rec = {
            "ts": datetime.now(timezone.utc).isoformat(timespec="seconds"),
            "head": git_head(),
            "tier": self.tier,
            "base_url": self.base_url,
            "check": name,
            "verdict": "pass" if ok else "fail",
            "kind": "behavior",
            "latency_s": latency,
            "baseline_s": baseline,
        }
        if collecting:
            rec["baseline_collection"] = True
            self._log(rec)
            # collection still enforces the BEHAVIOR verdict (R-3) — only
            # the perf verdict is withheld during the window
            print(f"{'pass' if ok else 'fail'}:{latency:.2f}s {name}"
                  f"  [baseline-collection run {good_runs + 1}"
                  f"/{regression_config.BASELINE_WINDOW}, no perf verdict]"
                  + (f"  ({detail})" if detail else ""))
            self.results.append(dict(name=name, ok=ok, kind="behavior",
                                     detail=detail, latency=latency))
            return ok, "behavior"

        perf_flagged = (baseline is not None
                        and latency > regression_config.PERF_MULT * baseline
                        and latency > absolute)
        self._log(rec)
        print(f"{'pass' if ok else 'fail'}:{latency:.2f}s {name}"
              f"  [baseline={baseline:.2f}s]"
              + (f"  ({detail})" if detail else ""))

        # perf record (kind=perf) so drift is visible even on baseline shifts
        prefs = dict(rec, kind="perf",
                     verdict="fail:perf" if perf_flagged else "pass")
        self._log(prefs)

        # the behavior verdict keeps its own result entry even when the
        # perf band also fires — a slow AND broken check is reported as
        # both, never silently reclassified as perf-only
        self.results.append(dict(name=name, ok=ok, kind="behavior",
                                 detail=detail, latency=latency))
        if perf_flagged:
            print(f"flagged → fail:perf {name}: {latency:.2f}s exceeds both"
                  f" bands ({regression_config.PERF_MULT}x"
                  f" {baseline:.2f}s and {absolute:.2f}s absolute)")
            self.results.append(dict(name=name, ok=False, kind="perf",
                                     detail=detail, latency=latency))
        return ok and not perf_flagged, "perf" if perf_flagged else "behavior"

    def summary(self):
        n = len(self.results)
        failed = [r for r in self.results if not r["ok"]]
        behavior = [r["name"] for r in failed if r["kind"] == "behavior"]
        perf = [r["name"] for r in failed if r["kind"] == "perf"]
        lines = [f"{n - len(failed)}/{n} checks passed ({self.tier} tier)"]
        if behavior:
            lines.append(f"FAILED: {', '.join(behavior)}")
        if perf:
            lines.append(f"PERF (fail:perf): {', '.join(perf)}")
        return "\n".join(lines)
