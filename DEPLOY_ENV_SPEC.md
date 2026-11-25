# Deploy operator-config specification (deploy-stack.sh)

**Status:** Approved — 2026-11-24, operator decision (option 4 of the deploy-identity brainstorm)
**Scope:** how `deploy-stack.sh` obtains the stack host's ssh target (`STACK_HOST`); the `.gitignore` entry; the committed template; the wiki setup runbook
**Runbook:** https://github.com/CherokeeLanguage/cherokeedictionary/wiki/deploy-stack-ssh-env-setup

---

## 1. Problem

`deploy-stack.sh` hard-coded the deploying operator's account:

    STACK_HOST="mconrad@vhost-58.cherokeedictionary.net"

A person's identity is not a host coordinate. Baked into the script, it
mis-attributes every write the script performs on the host once anyone
else deploys, makes the script unrunnable by other operators or CI
without a per-person edit, and rots as tribal knowledge. The
2026-11-24 brainstorm weighed six remedies; **option 4** was chosen.

## 2. Decision

- The tracked repo carries **no person's account name, anywhere**.
- The ssh target is sourced from an **untracked operator env file**
  defining `STACK_HOST`.
- **Both locations are checked; first match wins** (§3).
- A missing file — or a file that leaves `STACK_HOST` empty — fails
  **loudly with setup instructions before any host contact** (§4).

## 3. Config locations, precedence, semantics

| # | Path | Role |
|---|---|---|
| 1 | `./deploy-stack.env` | repo-local override (gitignored — §5; never committed) |
| 2 | `$HOME/.config/cherokeedictionary/deploy.env` | per-operator default (outside the repo) |

- **First match wins.** When both exist the script prints a one-line
  note that the repo-local copy wins. The `~/.config` file is the
  normal per-operator home; the repo-local copy exists for a
  machine- or checkout-local override.
- The file is **plain `KEY=value` shell** (sourced with `set -a`):
  - Required key: `STACK_HOST=<deploy-user>@vhost-58.cherokeedictionary.net`
  - Optional keys: the script's existing env-overridable knobs —
    `GATE_BASE_URL`, `GATE_BOOT_TIMEOUT`, `GATE_POLL`, `BUILD_TIMEOUT`
    (each still defaults per the script when absent).
  - Because the file is sourced as shell, a file value **wins over a
    same-named variable from the invoking environment**. Define only
    the documented keys: a stray `KEY=` could shadow a script-internal
    variable (`SERVICE`, `SLOT_*`, …).
  - Only `KEY=value` shell lines are valid — no quotes-mangling tricks,
    no non-shell syntax; the file must be readable by `bash`.

## 4. Script behavior

- The location check runs immediately after the script's `cd` to the
  repo root — before the first `ssh`, so nothing deploys
  half-configured.
- The loaded file is announced: `operator config: <path>`.
- If neither file defines `STACK_HOST`, the script prints a stderr
  banner and exits 1. The banner lists both locations and the required
  line, points at the committed template `deploy-stack.env.example`,
  gives the `mkdir -p` hint, and links the wiki runbook (ssh key,
  ssh-copy-id, env file).
- A file that fails to source (not valid `KEY=value` shell) fails
  loudly at load time, not later at ssh time.

## 5. `.gitignore`

The exact addition (the per-operator file lives outside the repo and
needs no ignore rule):

    deploy-stack.env

The committed template `deploy-stack.env.example` is deliberately a
*differently-named* file so the ignore rule cannot swallow it.

## 6. Deliverables

| File | Purpose |
|---|---|
| `DEPLOY_ENV_SPEC.md` | this spec |
| `.gitignore` | ignores `deploy-stack.env` (§5) |
| `deploy-stack.env.example` | committed, commented operator-config template |
| `deploy-stack.sh` | the hard-coded `STACK_HOST` replaced by the §4 behavior |
| wiki `deploy-stack-ssh-env-setup` | operator setup runbook: ssh key, ssh-copy-id, env file, verification |
| `SOP.md` | the Deploy section records the operator-local posture (pointer, never a copy) |

## 7. Acceptance criteria

- [x] No person's account name remains anywhere in the tracked tree
- [x] No env file (or empty `STACK_HOST`) → exit 1 with the setup banner, no host contact, nothing deployed
- [x] `./deploy-stack.env` alone → picked up (announced on stdout)
- [x] Both present → repo-local wins, with the note printed
- [x] `~/.config/cherokeedictionary/deploy.env` alone → picked up
- [x] `git check-ignore` flags `deploy-stack.env` while `deploy-stack.env.example` stays trackable
- [x] Template and runbook describe both locations and the exact required line
- [x] `bash -n` clean; the operator-config block is the only identity-related script change

## 8. Alternatives considered and not taken

From the 2026-11-24 brainstorm:

- **(1) Required shell env per invocation** — `STACK_HOST` must be
  passed every time; more friction per deploy and no operator
  persistence.
- **(2) Role-account default** — once a dedicated `deploy@vhost-58`
  service account is provisioned (devops), the example/comment may
  re-point to it as the default; open as the eventual follow-up, not a
  prerequisite for removing the person's name.
- **(3) Per-operator ssh config only** — script keeps the bare
  hostname and each operator's `~/.ssh/config` supplies the user; the
  onboarding is then invisible to the repo.
- **(5) CI-owned deploy identity** — a runner/service-agent runs the
  deploy; the long-term shape, a separate piece of work.
- **(6) Document-only** — the person stays in the tree with a
  clarifying comment; rejected outright.

Option 4 was chosen for zero identity in the tree plus operator
persistence across invocations.
