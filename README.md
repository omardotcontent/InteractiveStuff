# InteractiveStuff

First-person item interactions driven by Vyn resource-pack scripts.

The 26.2 overhaul is the shared implementation for Minecraft **1.21.5, 1.21.10, 1.21.11, 26.1.X, 26.2 and 26.3**. Version folders contain dependency profiles and native API adapters; the scheduler, script runtime, behavior scripts and most bindings are shared.

## Build

Use a JDK with a compiler (Java 25 builds every target). Minecraft 1.21 releases require Java 21 at runtime; 26.x requires Java 25.

```sh
bash ./gradlew -Ptarget_version=26.2 build
bash ./gradlew -Ptarget_version=1.21.5 build
bash ./gradlew -Ptarget_version=26.1.X -PminecraftPatch=26.1.2 build
```

The branch default lives in `gradle.properties`. Outputs are separated under `build/<target>/<minecraft>/libs/`. For 1.21 targets, install the remapped JAR; the `-dev.jar` is for development. The release includes Vyn and excludes test/game classes.

## Verification

`build` runs deterministic runtime/render checks and validates the release JAR. The real client test suite is separate:

```sh
bash ./gradlew -Ptarget_version=26.3 runClientGameTest
```

It needs a working display/OpenGL context and the target Java runtime. It creates and deletes its own test directory under `build/<target>/<minecraft>/run/clientGameTest/`; it does not use player saves. Screenshots and client logs are kept there after the run. CI uses Xvfb and runs both build and client tests for the branch target.

See [the architecture map](docs/PROJECT_MAP.md), [the shared base overhaul](docs/26.2_OVERHAUL.md), [version porting](docs/VERSION_PORTING.md), and [validation results](docs/MULTIVERSION_VALIDATION.md).
