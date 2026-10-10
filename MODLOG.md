# InteractiveStuff 26.2 overhaul journal

- Request: overhaul rendering and shared systems on Minecraft 26.2 as the foundation for future version ports.
- Original checkout: /mnt/hdd/Projects/Java/InteractiveStuff, branch 26.3. Its three uncommitted port changes are preserved.
- Isolated managed worktree: /home/omar/.codex/worktrees/26-2-system-overhaul/InteractiveStuff.
- Baseline: branch 26.2, commit 25ae15a, Java 25, Loom 1.17.19, Fabric Loader 0.19.3, API 0.158.0+26.2.
- Route: Fabric/Mixin Java source changes and bundled Vyn script refinements; no game install or user worlds modified.
- Knowledge checked: plugin knowledge INDEX and Minecraft engine playbook; existing project uses the supported loader route.
- Planned boundaries: thin version-specific mixins/renderer; stable script-facing API; deterministic reload/runtime; tick scheduler; lazy visual state.
- Verification: offline baseline/build, meaningful runtime regression checks, artifact contents, then isolated development-client smoke test if display/runtime permits.

- Implemented: deterministic script core/imports/indexes, independent waits and session cleanup, scoped extras, native render-layer preservation, correct tint slots, lazy stack/transform state, config/component/input fixes, feature filters and cached world queries.
- Headless checks: 66 runtime/render checks passed; bundled dispatch measured stone=0, chest=1, redstone torch=2 item bindings versus 15 previously.
- Client verification: isolated Fabric client tests passed with actual 26.2 resolver/mixins on existing OpenGL GPU/display. All item features, both hands, propagated left-handed preference, partial ranges, special shield, glint/light, SNBT, config, waits, reload and disconnect covered.
- Screenshots: chest, oak leaves, water bucket and left-handed frame reviewed; logs show no InteractiveStuff render/script failures.
- Resource cleanup: added valid custom-model particle references after warnings observed on the first run.
- Packaging: ordinary JAR bundles Vyn and LICENSE.txt; automated release-JAR guard rejects missing interpreter/license, wrong version metadata, and game/test classes.
- Documentation: current PROJECT_MAP, overhaul evidence/limits and VERSION_PORTING guide added.
- Final verification: clean build plus client integration suite; preserve the final test log/screenshots under build/run/clientGameTest.
- Distribution: local implementation only; no remote push, PR, mod-install or user-save edits.

## Multi-version ports and physical-hand correction (2026-10-10)

- User requested shared version folders for 1.21.5, 1.21.10, 1.21.11, 26.1.X, 26.2 and 26.3, and direct updates to the existing GitHub version branches. Missing version branches will use the same version names.
- Investigated all bundled main/off-hand angle branches. Item-frame yaw, chest hinge pivot/axis and vault contents used inventory identity for physical-side movement. Added `isLeftHand`/`isRightHand` and switched those scripts; main/off-hand semantics remain inventory-based.
- Added signed-yaw checks for both physical arms under both inventory identities. Paired GPU screenshots confirm the item frame mirrors consistently after dominant-hand changes.
- Shared build selects a dependency/Java profile and exactly one set of native adapters. 1.21 targets use the remapping Loom plugin and Java 21 bytecode; 26.x uses the unobfuscated plugin and Java 25. Native namespace substitutions are generated into separate build outputs.
- 1.21.5 uses immediate `MultiBufferSource` rendering; 1.21.10 uses queues with packed vertex quads; 1.21.11 uses queues with position/UV quads; 26.3 uses `ItemQuads` batches and updated material/glint/shading fields. The fast native path and native special renderers remain intact.
- 26.3 has an item submission redirect after native swing/use transforms. Its successful local-player swing hook is on `LivingEntity`; the old `LocalPlayer.swing` hook no longer exists.
- Cached Fabric API 0.160.0+26.3 failed a native Fabric renderer mixin during startup. Upgraded the profile to 0.162.0+26.3 and verified a healthy client launch and test run.
- 26.1 uses the native HUD extraction argument directly rather than the later 26.2 local-capture signature. The family has ~26.1 metadata and explicit patch selection for validation.
- Older book texture paths are resource overrides. The bundled pack description now follows the mod version.
- Native player/world/client API differences are isolated in `MinecraftAccess`. 1.21.5 accesses the native jumping flag through an accessor and uses its dimension cloud layer for cloud-height queries.
- Client fixtures and temporary saves are isolated by target/version. Test state, actual native setters, lifecycle, render geometry, component codec behavior, both hand preferences and resource reload are exercised on the target runtime.
- Original checkout edits and user saves remain untouched. Source and release verification evidence is recorded in docs/MULTIVERSION_VALIDATION.md.
- Final local result: all eight Minecraft releases passed build + real-client validation, 67 shared regression checks each. 26.1 patch mod class entries are byte-identical; artifact Java and remapped mixin selectors were verified.

- First GitHub checks passed for 1.21.5, 1.21.11, 26.1.X and 26.2. The 1.21.10 client repeatedly timed out before joining its test world; its Fabric test-only network synchronizer is now disabled, with native state waits retained and a passing local suite.
- 26.3's headless backend rejected the software GLX visual and waited in Minecraft's startup error dialog. SDL's EGL path passed the full native suite on local Mesa llvmpipe. Linux CI selects this path; ordinary desktop rendering is unchanged.
- Reuse an explicitly supplied display instead of nesting Xvfb wrappers; split CI build/client steps and bound client/job durations so failures return diagnostics.

- GitHub confirmed the 1.21.10 synchronization workaround. 1.21.11 later showed the same initial-world-loading stall despite an earlier successful run; its test profile now uses the same mode and passed local native checks.
- The 26.3 EGL CI retry reported a missing EGL library. Install libegl1, libegl-mesa0 and libgl1-mesa-dri on the ephemeral runner so it matches the locally verified software renderer.

## 0.8.0 release preparation

- Bumped the shared mod version from 0.7.1 to 0.8.0 to reflect the overhaul since 0.7.0.
- Added a user-facing changelog and rebuilt the eight supported Minecraft targets.
- Release artifacts and SHA-256 checksums are generated from the final 0.8.0 builds.

## Mod Menu / YACL settings fix (0.8.0, unpublished)

- User reported that the settings button stayed in Mod Menu with Mod Menu and YACL installed. The shared availability check and optional dependency metadata incorrectly named YACL as `yacl`; its real Fabric mod ID is `yet_another_config_lib_v3` in all six dependency profiles.
- Reproduced with actual Mod Menu/YACL on Minecraft 26.2: the registered IS factory returned the parent instead of a configuration screen. The native test failed with `installed YACL opens a configuration screen`.
- Corrected the runtime check and optional dependency hint. Added development-only optional integrations and a native settings-screen fixture to every target.
- User requested keeping 0.8.0 because it has not been uploaded.
- The missing-YACL fixture reproduced a second error: Mod Menu could not register IS because entrypoint verification loaded `dev.isxander.yacl3.api.Option` before the guard. Moved the unchanged YACL screen-building code into a separately loaded `YaclConfigScreen`; the Mod Menu entrypoint references only Mod Menu/Fabric types until the presence check succeeds.
- Final local validation: all eight builds passed 67 shared regression checks and actual Mod Menu/YACL screen opening, rendering and return-navigation checks, alongside the native render/lifecycle suite. Java 21/1.21.5 and Java 25/26.2 also passed with Mod Menu alone. CI now covers both combinations in separate test directories.

## All-in-one Gradle / IDE workspace (2026-10-10)

- Replaced the single-target root project with eight independently typed Minecraft modules: 1.21.5, 1.21.10, 1.21.11, 26.1, 26.1.1, 26.1.2, 26.2 and 26.3. The target catalog is `gradle/targets.json`; all module build rules live in `gradle/minecraft.gradle`.
- The 26.2 module owns editable shared sources. Other modules own native adapters and compile generated copies of inherited sources/resources, keeping IDE source roots unique. Native 1.21.5/1.21.10 adapter names now use their actual API directly instead of requiring generation of editable files.
- Added version build/run/test/source commands, `prepareIde`, `buildAll`, `checkAll`, `testAll`, `cleanAll`, `releaseAll`, and default-target conveniences. Unqualified `build` builds every version; branch CI uses qualified `:buildSelected` / `:testSelected`.
- Configured each module's compiler and JavaExec launcher for Java 21 or 25. Existing IntelliJ-downloaded JDKs were detected automatically without the explicit Java installation environment variables.
- Generated eight Gradle-backed IntelliJ client configurations. Archived obsolete root-module Loom configurations under `build/ide-config-backups`. Development saves are isolated under `run/<minecraft>` and survive cleaning build outputs. A shared Gradle build service serializes graphical clients even under parallel execution.
- `releaseAll` collects actual verified release artifacts into `build/dist/<mod_version>`, adds SHA-256 checksums and creates the all-version ZIP. Legacy JAR checksums changed after compiling on their native Java 21 JDK; runtime behavior remained covered by the same suites.
- Validation: all eight `buildAll` targets passed 67 deterministic checks and release guards; all eight `testAll` native clients passed rendering/lifecycle and actual Mod Menu/YACL screen checks. Minecraft 26.2 additionally passed without YACL through the new shortcut command.
- The actual Gradle IDE model contained root + eight target modules, 50 unique Java source directories, correct Java 21/25 language levels and target-specific Minecraft libraries. Command selection, branch-default patch selection, shared-resource additions/updates, native override precedence and stale-copy removal were checked independently.
- Full usage and command reference: `docs/IDE_WORKFLOW.md`. Mod version remains 0.8.0.

## Unified repository and branch consolidation (2026-10-10)

- Made `main` the canonical development branch for all eight Minecraft targets, retaining the complete histories of the six previous version branches in a merge with the validated unified tree.
- Preserved the previous remote branch tips under `archive/<version>` and selected 26.2 as the convenience-command default. Shared code and adapters now move together on `main`.
- Replaced branch-default CI with a matrix generated from `gradle/targets.json`. Every target receives build/regression/release checks and both real-client dependency combinations. Successful runs assemble all eight verified JARs into an upload ZIP with checksums.
- Updated the README, architecture/porting/IDE documentation and repository description for the unified workflow. Added `docs/REPOSITORY_WORKFLOW.md` and a `main` CI badge. Mod version remains 0.8.0.
- Validation: all six old branch heads are ancestors of `main`; the default 26.2 build passed 67 regression checks and release guards. actionlint 1.7.12 accepted the workflow. Actual CI matrix/bundle scripts selected all eight targets, preserved all JAR bytes, verified ZIP checksums and rejected a missing-JAR probe.

## Vyn first-person particle system (0.8.0, unpublished)

- User selected persistent emitter objects/setters, vanilla and pack PNG appearances, attached/detached modes, script-selectable depth (enabled by default), a core motion/easing feature set, optional spawn/update/death tasks, tick units and natural completion on item changes with configurable clearing.
- Added shared particle simulation, per-hand birth-style snapshots, rates/bursts, random spread, gravity/drag, animated texture lists, immutable easing/keyframes for opacity/size/color/rotation, Vyn particle handles and owner-generation cleanup. No Minecraft world particle spawning or registry additions are used.
- Added global enable/density/shared cap, live counters and emitter-origin markers. Settings persist through JSON reload and are available through the optional YACL menu and Vyn config getters.
- Bundled scripts demonstrate torch/soul flame and smoke, enchanting glyphs and both normal/calibrated sculk activation. Resource-pack examples and the complete API are documented in docs/VYN_PARTICLES.md, included in local/CI upload bundles.
- Rendering captures the complete model/native hand anchor once per model in the render scope, normalizes camera coordinates and draws immutable camera-facing snapshots. Queued submissions batch by texture/depth. Matrix adapters account for native pass timing: 26.2/26.3 draw while the camera stack is active; older queues draw after restoration; 1.21.5 flushes immediate particles before restoration.
- Deterministic checks exposed frame-recency gating below 20 FPS; bindings now stay active between frames while each tick validates the held item. Natural completion freezes an attached particle's final anchor so rebinding cannot move old particles onto a new item.
- Native screenshot review caught a camera-matrix error that simulation/submission checks could not detect. Added a screenshot assertion for custom orange/blue texture pixels beside the right-hand model after a 90-degree camera rotation, alongside simulation/native-state checks.
- The user's pre-existing local leaves.vyn filter removal is preserved and excluded from implementation commits. Dispatch checks continue to accept deliberate wildcard scripts while testing that unrelated filtered features are skipped.
- src/gametest remains separate from ordinary build/release tasks. Release guards also reject particle regression classes and custom test-only texture resources.
- Final local result: all eight targets passed 135 deterministic checks each, native first-person demo/custom-texture pixel assertions and existing settings/render/lifecycle suites. All eight release JARs passed metadata, Java bytecode, particle-hook registration and test-exclusion guards. The 1.21.5 separate mixin list is explicitly covered after a missing registration was caught by the visible-pixel test.
- Refreshed the unpublished 0.8.0 upload bundle and local checksums. The bundle contains eight JARs, README, changelog, license, checksum manifest and the complete Vyn particle guide.
- The final 26.3 suite additionally passed the visible-particle assertion on Mesa software graphics through EGL with YACL absent. No changes to the release implementation were needed for this run.
