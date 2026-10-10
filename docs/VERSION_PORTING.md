# Shared 26.2 base and version adapters

The root `src/` is the corrected 26.2 overhaul. All eight Minecraft releases are separate Gradle/IDE modules in one checkout. `gradle.properties` selects only the default for `buildSelected`, `runSelected` and `testSelected`; it never hides modules. See [the IDE workflow](IDE_WORKFLOW.md) for version commands and run configurations.

All ports are maintained together on `main`. A Minecraft version is a Gradle module and adapter folder. The previous version branches are historical snapshots under `archive/`; see [the repository workflow](REPOSITORY_WORKFLOW.md).

| Folder | Runtime Java | Native boundary |
| --- | --- | --- |
| `versions/1.21.5` | 21 | Immediate `MultiBufferSource` rendering, legacy keyboard/sound hooks |
| `versions/1.21.10` | 21 | Submission queue with packed vertex-array quads |
| `versions/1.21.11` | 21 | Submission queue with position/UV quad records |
| `versions/26.1.X` | 25 | 26.1 client access and permission API |
| `versions/26.2` | 25 | Shared baseline; no redundant source copy |
| `versions/26.3` | 25 | Extracted first-person state, `ItemQuads` batches, updated pose/material/swing APIs |

## Source and resource selection

Each module has a separate Minecraft classpath and build directory, so several targets can build in the same Gradle invocation. Profiles select the correct Loom plugin: the 1.21 releases use official Mojang mappings and the remapping plugin; 26.x uses the unobfuscated plugin. Release JARs and test directories have distinct target/version paths.

For a Java or resource path, the selected target wins over its `base_target`, which wins over root `src/`. 1.21.5 inherits the 1.21.10 adapters, and 1.21.10 inherits 1.21.11. All inherit the shared root. Only native boundaries are overridden; the scheduler, frame clock, script/runtime/import logic, callback dispatch, Vyn scripts, config logic and most player/model bindings remain shared.

`legacy_names=true` applies deterministic Minecraft name substitutions to inherited sources (`Identifier`/`ResourceLocation`, resource-key and sound identifier access, and the older `Util`/`RenderType` packages). Inherited files are generated under each target build directory and are never committed. Only the 26.2 module owns shared editable source roots; other modules directly own their native adapters and compile inherited copies. This keeps IntelliJ source roots unique across Minecraft classpaths. Native adapters must already use native names. The bindings remain shared without reflection or runtime version checks. API behavior differences belong in `MinecraftAccess` or a native renderer/mixin override.

When adding an adapter file, match the shared relative path. The build replaces that path, preventing duplicate classes/resources. A version-specific fixture under `src/gametest/java` or `src/regression/java` follows the same rule. Source JARs contain the selected, compilable sources.

## 26.1 patch family

The family profile compiles against 26.1 and declares `~26.1` compatibility. Build another patch with:

```sh
./gradlew build26_1_1 test26_1_1
./gradlew build26_1_2 test26_1_2
```

Unknown targets and patches fail at configuration time. Verify each supported patch; do not extend the metadata range across untested major/minor releases.

## Hand identity in Vyn

`isMainHand()`/`isOffHand()` identify the actual inventory hand, including left-handed players. `isLeftHand()`/`isRightHand()` identify the physical render arm. Use physical-side checks for mirrored yaw, pivots or movement; use main/off checks for inventory/gameplay behavior. Bundled item-frame, chest and vault scripts now use physical sides for their angles. Extra models inherit both identities from the active render scope.

## Port verification

Run a version build such as `build26_2` for shared regression and distributable checks, then `test26_2` (the module's `runClientGameTest`) with the target Java runtime and a working display/OpenGL context. Test real native model resolution/submission, native special renderers, tint slots, partial/complementary geometry, both physical arms under either hand identity, matrix/color toggles, copy-on-component changes, config persistence, waits, resource reload and disconnect. Inspect logs for missing mod textures and render fallbacks; compiler success alone does not validate mixin targets.

The suite uses its own automatically reset game directory. It never opens user saves. Keep known results in `docs/MULTIVERSION_VALIDATION.md` and `MODLOG.md`.

## CI client execution

CI generates its eight-target matrix from `gradle/targets.json`. Every target builds and runs native tests with Mod Menu/YACL present and with YACL absent, regardless of the default target in `gradle.properties`. Successful runs collect all eight ordinary release JARs into one upload ZIP with checksums. Diagnostic artifacts are named by Minecraft version.

An existing `DISPLAY` disables Loom's extra Xvfb wrapper, so each CI job uses a single virtual display. The workflow checks builds and client tests separately and bounds each client execution to five minutes.

The 1.21.10 and 1.21.11 profiles disable Fabric's test-only packet synchronizer after reproducible initial-world-loading stalls on CI. Assertions still wait for actual native client state; production networking is unaffected. Both modes also passed their local native suites.

26.3 requests an accelerated GLX visual, which CI's Mesa software renderer rejects. The workflow installs Mesa's EGL loader and driver libraries. Linux CI uses SDL's [EGL context path](https://wiki.libsdl.org/SDL3/SDL_HINT_VIDEO_FORCE_EGL); the complete native suite also passed locally on Mesa llvmpipe through this path. Desktop runs retain their default graphics configuration.

## First-person particle adapters

Particle simulation, Vyn bindings, texture lists and geometry stay shared. Port the `FirstPersonParticleMixin` hand-pass tail, `MinecraftParticleRenderer` queue/buffer factories and `MinecraftItemRenderer.anchor` native layer transform.

The anchor includes script transforms and the first native model layer. `ParticleView.capture` converts the submitted model pose to actual view coordinates. The native draw adapter must match when Minecraft consumes that geometry:

| Releases | Native draw timing | Particle pose |
| --- | --- | --- |
| 1.21.5 | Immediate buffers in the hand pass | Cancel the current camera matrix and explicitly flush particle buffers before restoration |
| 1.21.10, 1.21.11, 26.1 family | Queued custom geometry consumed after the hand camera stack is restored | Submit view-coordinate geometry with an identity pose |
| 26.2, 26.3 | Hand custom geometry consumed with the camera stack active | Cancel that camera matrix in the submitted pose |

1.21.5/1.21.10 factories are on `RenderType`; later factories are on `RenderTypes`. Transparent particle batches are grouped by texture/depth while immutable snapshots survive deferred queue consumption.

The 1.21.5 resource adapter also owns a separate mixin list; new hooks must be registered there as well as in the shared list. The release guard verifies particle-hook registration.

Run `test<version>` and review its particle screenshots. Simulation counts and queued submissions cannot prove that pixels are visible or anchored correctly. `ClientParticleChecks` also verifies custom orange/blue PNG pixels beside the right-hand model after rotating the camera. It exercises both hands, a dominant-hand swap, detached smoke, glyphs, both activated sensors, custom animation/overlay, global disable, third-person suppression, resource reload and disconnect. Test textures/classes remain separate from release JARs.
