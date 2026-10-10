# InteractiveStuff

[![Multi-version checks](https://github.com/omardotcontent/InteractiveStuff/actions/workflows/multiversion.yml/badge.svg?branch=main)](https://github.com/omardotcontent/InteractiveStuff/actions/workflows/multiversion.yml)

First-person item interactions driven by Vyn resource-pack scripts.

Current mod version: **0.8.0**. See [the changelog from 0.7.0](CHANGELOG.md).

One Gradle/IDE workspace supports Minecraft **1.21.5, 1.21.10, 1.21.11, 26.1, 26.1.1, 26.1.2, 26.2 and 26.3**. The 26.2 overhaul in `src/` is shared; version folders contain only dependency profiles and native API adapters.

**`main` is the development branch for every supported version.** Previous version branches are preserved under `archive/`. Work in this one checkout and select Minecraft versions with Gradle commands; see [the repository workflow](docs/REPOSITORY_WORKFLOW.md).

## IDE setup

Open the repository root in IntelliJ IDEA as a Gradle project. Use Java 25 for Gradle and install a Java 21 JDK for the 1.21 modules. Reload Gradle and run:

```sh
./gradlew prepareIde
```

Choose a versioned client configuration such as **IS Minecraft 26.2**. Each module has its own Minecraft classpath and Gradle selects its Java runtime automatically. Shared editable code belongs to the 26.2 module; native adapters belong to the corresponding version module. See [the complete IDE workflow and command reference](docs/IDE_WORKFLOW.md).

## Everyday commands

```sh
./gradlew commands                # List all supported version commands
./gradlew run26_2                 # Playtest 26.2 with IS, Mod Menu and YACL
./gradlew build1_21_5             # Build/check one version
./gradlew build26_2 build26_3     # Build/check several versions
./gradlew buildAll                # Build/check all eight releases
./gradlew test26_2                # Test the actual Minecraft client
./gradlew releaseAll             # Build and package all upload JARs + checksums
```

On Windows use `gradlew.bat`. Terminal commands need `JAVA_HOME` pointing to Java 25. `build` also builds every target. Default-target commands are `buildSelected`, `runSelected`, and `testSelected`; only these use `target_version` in `gradle.properties` or `-Ptarget_version`. The default is 26.2.

Outputs remain separated under `build/<profile>/<minecraft>/libs/`. Install the ordinary JAR; skip `-dev.jar` and `-sources.jar`. `releaseAll` collects the eight distributables under `build/dist/<mod_version>/` and creates `build/dist/InteractiveStuff-<mod_version>-all-versions.zip`. Vyn is included; test/game classes are excluded.

## Verification

Builds run deterministic runtime/render checks and validate each release JAR. Native tests need a working display/OpenGL context and use resettable directories under `build/<profile>/<minecraft>/run/clientGameTest/`. Normal development clients use separate `run/<minecraft>/` directories that survive clean builds.

Development clients include optional Mod Menu/YACL integrations. The client suite opens and renders the actual IS settings screen through Mod Menu. Test without YACL using `./gradlew -Pconfig_screen_test_yacl=false test26_2` or `testAll`; those tests use separate `clientGameTestWithoutYacl` directories. CI reads `gradle/targets.json` and checks every Minecraft target with both dependency combinations on each active branch push and pull request to `main`.

Successful GitHub Actions runs provide a complete all-version upload ZIP with SHA-256 checksums, plus separate per-version JARs and client diagnostics. Download these from the run's **Artifacts** section. `archive/*` branches retain earlier snapshots and are excluded from the current workflow.

See [the architecture map](docs/PROJECT_MAP.md), [the shared base overhaul](docs/26.2_OVERHAUL.md), [version porting](docs/VERSION_PORTING.md), and [validation results](docs/MULTIVERSION_VALIDATION.md).
