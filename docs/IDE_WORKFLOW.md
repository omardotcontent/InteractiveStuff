# Working in the multi-version IDE project

Open the repository root (the folder containing `settings.gradle`) as a Gradle project. All eight Minecraft releases are imported together; changing the target or switching Git branches is no longer needed to work on a different Minecraft API.

Use the repository's default `main` branch. Historical version branches live under `archive/`; the [repository workflow](REPOSITORY_WORKFLOW.md) explains how they were preserved and how other checkouts move to `main`.

## Initial setup

1. Use Java 25 for IntelliJ's Project SDK and Gradle JVM. Install a Java 21 JDK as well for the 1.21 modules. Gradle toolchains select Java 21 or 25 for each compiler and client automatically.
2. Reload all Gradle projects. Run `./gradlew prepareIde` once to prepare inherited sources/resources and all client run configurations. IntelliJ's Gradle import also runs this preparation through Loom's IDE sync tasks.
3. Select `IS Minecraft 26.2`, `IS Minecraft 26.3`, or another version in the **InteractiveStuff** run-configuration folder. Run normally to playtest or Debug to use breakpoints. These configurations invoke the matching Gradle task, which selects the correct Java runtime.
4. To browse Minecraft's own source, optionally run `./gradlew sources26_2` (or the matching version command), then refresh Gradle.

`prepareIde` backs up obsolete Loom-generated root run configurations to `build/ide-config-backups/` and replaces them with the versioned configurations. Custom run configurations are preserved.

On Linux/macOS use `./gradlew`. On Windows substitute `gradlew.bat`. The examples below run from the repository root. Set `JAVA_HOME` to Java 25 when using a terminal; IntelliJ's Gradle JVM setting does not change a terminal's Java installation.

Gradle discovers standard installed JDK locations. Custom installations can be supplied through `JAVA_HOME_21_X64` and `JAVA_HOME_25_X64`, or `org.gradle.java.installations.paths` in your personal Gradle properties. Do not commit machine-specific JDK paths.

## Where to edit

| Source | Purpose |
| --- | --- |
| `src/main/java` | Editable shared 26.2 implementation: scripting, scheduling, config and baseline rendering. It belongs to the 26.2 IDE module. |
| `src/main/resources` | Editable shared scripts, translations, textures, models and metadata. |
| `versions/<profile>/src` | Editable native adapters, resources and test overrides for that Minecraft API. |
| `src/regression` and `src/gametest` | Shared deterministic and native client tests. |
| `build/<profile>/<minecraft>/generated` | Generated inherited sources/resources for each target's classpath. Edit the original source above; these copies are replaced. |

Each module has its own Minecraft dependencies. A version-specific file wins over its inherited profile, which wins over `src/`. For example, 1.21.5 inherits 1.21.10 and then 1.21.11. The 26.1.1 and 26.1.2 modules use the editable `versions/26.1.X/src` adapters.

Only the 26.2 module owns the shared source directories. Other modules get generated copies of inherited files and directly own their native adapter folders. This prevents IntelliJ from assigning one source directory to several incompatible Minecraft classpaths. Use native Minecraft names in version adapter files; legacy name substitution applies to inherited shared copies.

Builds and launches refresh inherited copies automatically. After changing shared code, run `prepareIde` to refresh all the other modules' copies for code completion. Adding or deleting source files is picked up by the next Gradle invocation; reload Gradle when adding source directories.

## Commands

Run `./gradlew commands` (or just `./gradlew`) to print this command reference.

| Minecraft | Build + regression checks | Playtest | Real client tests | Browse Minecraft source |
| --- | --- | --- | --- | --- |
| 1.21.5 | `build1_21_5` | `run1_21_5` | `test1_21_5` | `sources1_21_5` |
| 1.21.10 | `build1_21_10` | `run1_21_10` | `test1_21_10` | `sources1_21_10` |
| 1.21.11 | `build1_21_11` | `run1_21_11` | `test1_21_11` | `sources1_21_11` |
| 26.1 (family base) | `build26_1` | `run26_1` | `test26_1` | `sources26_1` |
| 26.1.1 | `build26_1_1` | `run26_1_1` | `test26_1_1` | `sources26_1_1` |
| 26.1.2 | `build26_1_2` | `run26_1_2` | `test26_1_2` | `sources26_1_2` |
| 26.2 | `build26_2` | `run26_2` | `test26_2` | `sources26_2` |
| 26.3 | `build26_3` | `run26_3` | `test26_3` | `sources26_3` |

```sh
./gradlew run26_2
./gradlew build26_2
./gradlew build1_21_5 build26_3
./gradlew prepareIde
./gradlew buildAll
./gradlew testAll
./gradlew releaseAll
```

`build` and `buildAll` build all eight releases and run their regression/release checks. `testAll` launches the isolated native client suites one at a time and requires a working display/OpenGL context. `checkAll` runs every target's regression/release checks. `cleanAll` cleans target build outputs; `clean` cleans the entire root build directory.

Mod Menu and YACL are included in development clients. Test the optional missing-YACL path with:

```sh
./gradlew -Pconfig_screen_test_yacl=false test26_2
./gradlew -Pconfig_screen_test_yacl=false testAll
```

You can also use explicit module tasks, for example `./gradlew :26.2:runClient` and `./gradlew :1.21.5:build`. Qualify client tasks or use the short version commands: an unqualified subproject task such as `runClient` can select every module.

For faster single-version terminal commands, prefix a root shortcut with `:`, for example `./gradlew :build26_2` or `./gradlew :run26_2`. This avoids searching every module for that task name. `./gradlew :commands` prints help without configuring Minecraft modules. Versioned IDE client configurations already use qualified paths.

## Default-target compatibility

`target_version` in root `gradle.properties` selects only `buildSelected`, `runSelected`, and `testSelected`, with 26.2 as the default. It does not change which modules IntelliJ imports. CI addresses every module explicitly, independently of this convenience setting.

```sh
./gradlew -Ptarget_version=1.21.5 buildSelected
./gradlew -Ptarget_version=26.1.X -PminecraftPatch=26.1.2 buildSelected
```

Previous commands such as `-Ptarget_version=26.2 build` should become `build26_2` or `-Ptarget_version=26.2 buildSelected`. Unqualified `build` now means all versions.

## Outputs and development saves

Ordinary release JARs stay in `build/<profile>/<minecraft>/libs/`. Install the JAR without a `-dev` or `-sources` classifier. Vyn is bundled; test classes and Minecraft classes are excluded.

`releaseAll` builds all targets and creates:

- `build/dist/0.8.0/`: eight upload JARs, license, README, changelog and `SHA256SUMS.txt`.
- `build/dist/InteractiveStuff-0.8.0-all-versions.zip`: the same files packaged together.

The paths follow `mod_version` if it changes. This task refreshes the upload bundle from actual Gradle release artifacts and removes stale staged files. It does not publish anything online.

Development clients use separate `run/<minecraft>/` directories. These survive `clean` and `cleanAll`. Existing saves in the old root `run/` folder remain there; copy a wanted save into the appropriate version's `saves` folder if needed. Native client tests retain their resettable `build/<profile>/<minecraft>/run/clientGameTest*` directories.

One checkout on `main` can edit and build all versions. Commit shared changes and any required adapters together. Feature branches start from `main` and return to `main`; there is no separate version-branch synchronization step.

## Editing Vyn particle effects

Edit the shared `.vyn` scripts under `src/main/resources/resourcepacks/interactive_resourcepack/assets/interactivestuff/scripts/`. Emitter globals and `onItemUpdate` attachment follow the existing item scripting style. See [VYN_PARTICLES.md](VYN_PARTICLES.md) for a copyable example and all setters. Shared simulation is in `src/main/java/omar/projects/interactivestuff/particles`; native rendering adapters remain in the version folders.
