# Pull requests

How to write a pull request for this repo, and what to do after opening it.

## The body

Write prose first: what changed and why, before any heading. Headings below are situational — include one when you have something to put under it, and skip it otherwise. A one-line fix gets a one-paragraph body.

**Title**: one imperative line describing what changed. No type prefix, no ticket number.

**Breaking changes** go directly after the opening prose, under their own heading. Name what breaks, and what has to land alongside this to keep it working. A reviewer who reads only the first screen should still learn that something breaks.

**Cross-repo pairing**: when a change spans `floaty-backend` and `floaty-frontend`, each body names the other pull request and states that they merge together. The deployed system breaks if one lands alone.

**Verification**: the commands you actually ran and what they produced. Every claim here should be one a reviewer can re-run. When you have not run anything — a docs-only change — say that plainly rather than implying a test run.

**Pre-existing failures** get their own heading, separate from verification. Say how you established they predate the change — the comparison method, not just the conclusion.

**Deliberately not fixed**: problems you found and left alone, so they can become tickets instead of surprises.

**Issue reference**: when the work came from a tracker issue, reference it. See `issue-tracker.md` for the `gh` conventions.

Name domain concepts with the glossary's terms (`CONTEXT.md`), so they mean the same thing in the tracker as in the code.

## After pushing

**Pushing is the middle of the task.** Wait for the pipeline before handing the pull request back.

Watch the run (`gh run watch`, or poll `gh pr checks`). Cap the wait at five minutes; past that, report that CI was still running and hand back what you know. Your turn is longer by design — the alternative leaves the maintainer to discover a red check you never looked at.

## When the pipeline is red

Diagnose it, then record the diagnosis in the pull request.

Run the same suite against a clean checkout of the base branch — `git worktree add` is the cheapest way — and compare failure counts and failing test names against your branch. Then state in the pull request which failures your change introduced and which it inherited, with the numbers behind the claim.

A red check with that comparison is a fine outcome when the base branch is already red. A red check on its own leaves the reviewer to work out whether it matters.
