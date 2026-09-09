# Pull requests

How to write a pull request for this repo, and what to do after opening it.

## The body

Write prose first: what changed and why, before any heading. Headings below are situational — include one when you have something to put under it, and skip it otherwise. A one-line fix gets a one-paragraph body.

**Title**: one line, beginning with the verb, describing what changed.

**Breaking changes** go directly after the opening prose, under their own heading. Name what breaks, and what has to land alongside this to keep it working. A reviewer who reads only the first screen should still learn that something breaks.

**Cross-repo pairing**: when a change spans `floaty-backend` and `floaty-frontend`, each body names the other pull request and states that they merge together. The deployed system breaks if one lands alone.

**Verification**: the commands you actually ran and what they produced. Every claim here should be one a reviewer can re-run. When you have not run anything — a docs-only change — say so.

**Pre-existing failures** get their own heading, separate from verification. Give the base-branch and branch counts and the failing test names, not a prose assertion that they match — a claim without numbers hides an off-by-one.

**Deliberately not fixed**: problems you found and left alone, so they can become tickets instead of surprises.

**Issue reference**: when the work came from a tracker issue, close it from the body — `Closes #123` on its own line, so merging resolves the ticket. GitHub shares one number space across issues and pull requests, so confirm the number is the issue you mean.

## After pushing

**Pushing is the middle of the task.** Wait for the pipeline before handing the pull request back.

Watch the run (`gh run watch`, or poll `gh pr checks`). Runs here finish in under three minutes, so cap the wait at five. If the cap trips, say so in the pull request and name the run, so whoever picks it up knows the check is still unread. Your turn is longer by design — the alternative leaves the maintainer to discover a red check you never looked at.

## When the pipeline is red

Diagnose it, then post the diagnosis as a comment on the pull request. A comment notifies reviewers and leaves the original body intact as the record they may already have read.

Run the suite against a clean checkout of the base branch — `git worktree add` into a temp directory — and compare it with your branch. Two things the checkout needs before it will build: `chmod +x mvnw`, and system `mvn` rather than `./mvnw`, because `.mvn/wrapper/` is not committed and the wrapper fails in any fresh checkout. Match the JDK the Dockerfile pins (17); the machine default is newer and compares under a toolchain the project does not target.

Compare the **set of failing test names**, not just the counts. Equal counts hide one failure fixed and another introduced.

Then say in the pull request which failures your change introduced and which it inherited, with both sets of numbers and the names behind them.

CI builds inside Docker (`mvn clean package` under buildx) while your worktree runs on the host, so the two can report the same failing tests through different proximate errors. Where they disagree, the CI log is what gates the pull request; say which one you read.

A red check with that comparison is a fine outcome when the base branch is already red. A red check on its own leaves the reviewer to work out whether it matters.

## The web UI template

`.github/PULL_REQUEST_TEMPLATE.md` pre-fills the body for anyone opening a pull request through GitHub's web UI, and its checkbox format predates this document. `gh pr create --body` bypasses it, so an agent following this document never sees it. Where the two disagree, this document governs agent-authored pull requests. Reconciling them is open work.
