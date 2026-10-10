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
