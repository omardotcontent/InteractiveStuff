# InteractiveStuff

[![Multi-version checks](https://github.com/omardotcontent/InteractiveStuff/actions/workflows/multiversion.yml/badge.svg?branch=main)](https://github.com/omardotcontent/InteractiveStuff/actions/workflows/multiversion.yml)

First-person item interactions driven by Vyn resource-pack scripts.

Current mod version: **0.8.0**. See [the changelog from 0.7.0](CHANGELOG.md).

One Gradle/IDE workspace supports Minecraft **1.20.1, 1.21.1, 1.21.5, 1.21.10, 1.21.11, 26.1, 26.1.1, 26.1.2, 26.2 and 26.3**. The 26.2 overhaul in `src/` is shared; version folders contain only dependency profiles and native API adapters.

**`main` is the development branch for every supported version.** Previous version branches are preserved under `archive/`. Work in this one checkout and select Minecraft versions with Gradle commands.

## Vyn particles

Script first-person effects with persistent `ParticleEmitter` objects: vanilla/custom textures, bursts, motion, item/view/world spaces, depth, lifetime easing/keyframes and optional particle callbacks. World-space particles stay at their spawn location when the player walks or turns, then follow their own motion. The bundled pack uses this mode for enchanting glyphs, sculk-sensor bursts and pitch-colored note-block bursts.

## IDE setup

Open the repository root in IntelliJ IDEA as a Gradle project. Use Java 25 for Gradle and install Java 17 for 1.20.1 and Java 21 for the 1.21 modules. Reload Gradle and run:

```sh
./gradlew prepareIde
```

Choose a versioned client configuration such as **IS Minecraft 26.2**. Each module has its own Minecraft classpath and Gradle selects its Java runtime automatically. Shared editable code belongs to the 26.2 module; native adapters belong to the corresponding version module. Run `./gradlew commands` for the complete command reference.

## Everyday commands

```sh
./gradlew commands                # List all supported version commands
./gradlew run26_2                 # Playtest 26.2 with IS, Mod Menu
./gradlew build1_21_5             # Build/check one version
./gradlew build26_2 build26_3     # Build/check several versions
./gradlew build1_21_1             # Build/check Minecraft 1.21.1
./gradlew run1_21_1               # Playtest Minecraft 1.21.1
./gradlew buildAll                # Build/check all ten releases
./gradlew test26_2                # Run native tests when local fixtures are available
./gradlew releaseAll             # Build and package all upload JARs + checksums
```

On Windows use `gradlew.bat`. Terminal commands need `JAVA_HOME` pointing to Java 25. `build` also builds every target. Default-target commands are `buildSelected`, `runSelected`, and `testSelected`; only these use `target_version` in `gradle.properties` or `-Ptarget_version`. The default is 26.2.

Outputs remain separated under `build/<profile>/<minecraft>/libs/`. Install the ordinary JAR; skip `-dev.jar` and `-sources.jar`. `releaseAll` collects the ten distributables under `build/dist/<mod_version>/` and creates `build/dist/InteractiveStuff-<mod_version>-all-versions.zip`. Vyn is included; test/game classes are excluded.

## Verification and release contents

Every build validates translations and the release JAR's metadata, Java version, bundled Vyn interpreter, license and exclusion of game/test/development files. Local deterministic tests also run when `src/regression` is available. Native client tests require the local `src/gametest` fixtures and a working graphics display; they use isolated build directories.

Developer guides, design notes and all test source/resource folders are kept locally and excluded from Git tracking, GitHub source archives and release downloads. A fresh GitHub checkout builds and imports in the IDE without those files. Public README, changelog and license remain in the repository. Required `libs/Vyn.jar` and the Gradle wrapper remain tracked.

GitHub Actions builds and verifies every target without the local-only test suites. Successful runs provide separate release JARs and an all-version ZIP containing exactly the ten release JARs, README, changelog, license and SHA-256 checksums. Development/source JARs, test assets and internal guides are excluded. Download the bundle from the run's **Artifacts** section. `archive/*` branches retain historical snapshots and are excluded from the current workflow.

## Native settings and pack options

YACL is no longer required. Open InteractiveStuff through Mod Menu when installed; otherwise use the Minecraft Options button. The optional unbound settings key remains available in Controls. The native animated screen uses the mod icon's cyan/sculk panels, centered sidebar buttons, full-width setting rows and compact icon controls that turn cyan on hover. Search guidance appears in a wrapped tooltip; Cancel sits at bottom left, Reset/Apply at bottom right, and the config folder at top right. Materials, per-item pitch overrides and exclusions use searchable inventories. Each pitch override has a numeric editor, reset and preview; sounds use a searchable preview/choose list. Apply saves the draft and Cancel discards it. Item previews also work from the title menu.

Built-in categories contain shared-engine controls; bundled sculk, note-block and texture effects are declared as Vyn resource-pack options, with automatic migration of older saved choices. Minecraft 1.20.1 is Fabric-only and uses Java 17; its adapter supports legacy NBT and the old baked-item renderer.

The settings menu includes 16 language locales, following Minecraft’s selected language. Pack authors can also use translation keys in Vyn options. Pivot debugging uses the same small crosses as emitter markers and works with particles disabled.
