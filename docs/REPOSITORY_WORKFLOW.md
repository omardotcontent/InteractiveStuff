# One repository and one development branch

`main` is the default branch for the complete InteractiveStuff multi-version workspace. Changes to shared code, Vyn scripts and native adapters belong in the same commit. All eight Minecraft releases are available in the IDE and through Gradle without switching Git branches.

## Branches

| Branch | Purpose |
| --- | --- |
| `main` | Current development and fixes for all supported Minecraft versions. |
| Feature branches from `main` | Work in progress, merged back into `main`. Codex-created feature branches use `codex/<change>`. |
| `archive/1.21.5`, `archive/1.21.10`, `archive/1.21.11` | Preserved former 1.21 version-branch tips. |
| `archive/26.1.X`, `archive/26.2`, `archive/26.3` | Preserved former 26.x version-branch tips. |

The six old branches had identical source/build trees except for their selected default target. Their complete commit histories were joined into `main` while retaining the already validated unified tree. GitHub's branch renaming preserves each original tip under its `archive/` name. No commit history was rewritten or discarded.

Archive branches are reference snapshots; keep current development on `main`. New Minecraft ports extend `gradle/targets.json`, their dependency profile and native adapters. They do not need another permanent version branch.

## Move an existing checkout to main

Commit or stash any local edits first. The workspace used for this consolidation is already on `main`. For another clone:

```sh
git fetch origin --prune
git switch main
git pull --ff-only origin main
git remote set-head origin --auto
./gradlew prepareIde
```

If that clone has a local branch named `main` already, review its work before integrating it. Reload Gradle in the IDE after switching. Java 25 runs Gradle, and a Java 21 JDK is also needed for the 1.21 targets.

Work directly on `main` or start a feature branch, then commit shared changes and native adapters together:

```sh
git switch main
git switch -c codex/my-change
./gradlew :build26_2 :build1_21_5
./gradlew :test26_2
git add src versions
git commit -m "Describe the change"
git push -u origin codex/my-change
```

Merge completed work into `main`. There is no need to copy or cherry-pick each change into six version branches.

## GitHub checks and downloadable JARs

`.github/workflows/multiversion.yml` reads the same `gradle/targets.json` catalog used by Gradle. Pushes to active branches, pull requests targeting `main`, and manual runs check every target. The current workflow excludes `archive/*` pushes.

Each target job builds the distributable, runs the 67 regression checks and release guards, and launches the native Minecraft tests with YACL present and absent. Per-version diagnostics remain available if a job fails. When all jobs pass, the final packaging job validates the exact JAR filenames, assembles all eight distributables with the README, changelog and license, computes SHA-256 checksums, and uploads one all-version ZIP. These are workflow artifacts; creating a run does not publish a GitHub Release.

The selected default remains 26.2 for `buildSelected`, `runSelected` and `testSelected`. CI explicitly selects every module, so changing that convenience default does not reduce version coverage.

See [IDE_WORKFLOW.md](IDE_WORKFLOW.md) for editing and Gradle commands, and [VERSION_PORTING.md](VERSION_PORTING.md) for adapter ownership.
