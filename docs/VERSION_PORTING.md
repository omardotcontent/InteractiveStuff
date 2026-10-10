# Shared 26.2 base and version adapters

The root `src/` is the corrected 26.2 overhaul. All version branches carry this same multi-version tree. `gradle.properties` selects the branch's default `target_version`; `-Ptarget_version=<folder>` overrides it.

| Folder | Runtime Java | Native boundary |
| --- | --- | --- |
| `versions/1.21.5` | 21 | Immediate `MultiBufferSource` rendering, legacy keyboard/sound hooks |
| `versions/1.21.10` | 21 | Submission queue with packed vertex-array quads |
| `versions/1.21.11` | 21 | Submission queue with position/UV quad records |
| `versions/26.1.X` | 25 | 26.1 client access and permission API |
| `versions/26.2` | 25 | Shared baseline; no redundant source copy |
| `versions/26.3` | 25 | Extracted first-person state, `ItemQuads` batches, updated pose/material/swing APIs |

## Source and resource selection

Build one target per Gradle invocation. Profiles select the correct Loom plugin: the 1.21 releases use official Mojang mappings and the remapping plugin; 26.x uses the unobfuscated plugin. Release JARs and test directories have distinct target/version paths.

For a Java or resource path, the selected target wins over its `base_target`, which wins over root `src/`. 1.21.5 inherits the 1.21.10 adapters, and 1.21.10 inherits 1.21.11. All inherit the shared root. Only native boundaries are overridden; the scheduler, frame clock, script/runtime/import logic, callback dispatch, Vyn scripts, config logic and most player/model bindings remain shared.

`legacy_names=true` generates only the sources needing deterministic Minecraft name substitutions (`Identifier`/`ResourceLocation`, resource-key and sound identifier access, and the older `Util`/`RenderType` packages). Generated files go under the selected build directory and are never committed. This keeps the bindings shared without reflection or runtime version checks. API behavior differences belong in `MinecraftAccess` or a native renderer/mixin override.

When adding an adapter file, match the shared relative path. The build replaces that path, preventing duplicate classes/resources. A version-specific fixture under `src/gametest/java` or `src/regression/java` follows the same rule. Source JARs contain the selected, compilable sources.

## 26.1 patch family

The family profile compiles against 26.1 and declares `~26.1` compatibility. Build another patch with:

```sh
bash ./gradlew -Ptarget_version=26.1.X -PminecraftPatch=26.1.1 build runClientGameTest
bash ./gradlew -Ptarget_version=26.1.X -PminecraftPatch=26.1.2 build runClientGameTest
```

Unknown targets and patches fail at configuration time. Verify each supported patch; do not extend the metadata range across untested major/minor releases.

## Hand identity in Vyn

`isMainHand()`/`isOffHand()` identify the actual inventory hand, including left-handed players. `isLeftHand()`/`isRightHand()` identify the physical render arm. Use physical-side checks for mirrored yaw, pivots or movement; use main/off checks for inventory/gameplay behavior. Bundled item-frame, chest and vault scripts now use physical sides for their angles. Extra models inherit both identities from the active render scope.

## Port verification

Run `build` for shared regression and distributable checks, then `runClientGameTest` with the target Java runtime and a working display/OpenGL context. Test real native model resolution/submission, native special renderers, tint slots, partial/complementary geometry, both physical arms under either hand identity, matrix/color toggles, copy-on-component changes, config persistence, waits, resource reload and disconnect. Inspect logs for missing mod textures and render fallbacks; compiler success alone does not validate mixin targets.

The suite uses its own automatically reset game directory. It never opens user saves. Keep known results in `docs/MULTIVERSION_VALIDATION.md` and `MODLOG.md`.

## CI client execution

An existing `DISPLAY` disables Loom's extra Xvfb wrapper, so CI uses a single virtual display. The workflow checks builds and client tests separately and bounds client execution to five minutes.

The 1.21.10 and 1.21.11 profiles disable Fabric's test-only packet synchronizer after reproducible initial-world-loading stalls on CI. Assertions still wait for actual native client state; production networking is unaffected. Both modes also passed their local native suites.

26.3 requests an accelerated GLX visual, which CI's Mesa software renderer rejects. The workflow installs Mesa's EGL loader and driver libraries. Linux CI uses SDL's [EGL context path](https://wiki.libsdl.org/SDL3/SDL_HINT_VIDEO_FORCE_EGL); the complete native suite also passed locally on Mesa llvmpipe through this path. Desktop runs retain their default graphics configuration.
