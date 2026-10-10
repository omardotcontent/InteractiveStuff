# Multi-version verification — 2026-10-10

Each row passed `build runClientGameTest`. The latest particle implementation passes **135 runtime/render checks** per target, including the original 67 checks. Tests ran against the real target Minecraft JAR, actual Fabric mixins and the OpenGL client; 1.21 clients ran on Java 21, 26.x clients on Java 25. Historical results below retain their original check counts.

| Target folder | Minecraft | Client Java | Release class version | Build/client result |
| --- | --- | --- | --- | --- |
| 1.21.5 | 1.21.5 | 21 | 65 | Passed |
| 1.21.10 | 1.21.10 | 21 | 65 | Passed |
| 1.21.11 | 1.21.11 | 21 | 65 | Passed |
| 26.1.X | 26.1 | 25 | 69 | Passed |
| 26.1.X | 26.1.1 | 25 | 69 | Passed |
| 26.1.X | 26.1.2 | 25 | 69 | Passed |
| 26.2 | 26.2 | 25 | 69 | Passed |
| 26.3 | 26.3 | 25 | 69 | Passed |

## What the real client verifies

- All 13 held-item feature families, off-hand rendering, a real dominant-hand preference change, debug overlay, and healthy scripts through reload.
- Item-frame yaw sign follows the physical arm under both main/off-hand identities; GPU screenshot pairs show consistent mirrored orientation. Chest/vault side-dependent transforms use the same physical-side API.
- Actual native jump-state setters reach the shared player binding and reset correctly.
- Real model resolution, colored geometry, native shield special rendering, exclusive/complementary quad selection, light/glint where the submission API exposes them, balanced matrices, and the matrix-editing toggle. The 1.21.5 fixture records immediate buffer vertices; later fixtures record native queued submissions.
- Quoted/nested SNBT through Minecraft codecs, inventory copy isolation, config save/reload/cache invalidation, independent same-duration waits, resource reload and disconnect cleanup.

No InteractiveStuff script/render/mixin failures or missing mod textures appear in the successful runs. Minecraft development-account Realms/profile authentication messages and newer clients' anisotropic-option validation messages are separate vanilla diagnostics.

## Release verification

`verifyReleaseJar` checks Vyn, the license, Minecraft/Java metadata, mixin compatibility, class-file versions and exclusion of game/test classes. For 1.21, it checks the **remapped** distributable. The release mixin selector was inspected and remaps to the intermediary method name.

The three 26.1 builds have byte-identical mod class entries. The 26.1 baseline JAR declares `~26.1`; the patch-specific artifacts differ in their target filename/license entry.

## Artifacts and evidence

JARs: `build/<target>/<minecraft>/libs/`. Client logs and eleven screenshots per current run: `build/<target>/<minecraft>/run/clientGameTest/`. Test worlds are disposable and isolated from user saves. Checksums are in [RELEASE_CHECKSUMS.json](RELEASE_CHECKSUMS.json).

## Practical scope

These checks validate the port and native render/lifecycle boundaries. They are not an FPS benchmark or coverage of every external shader/animation/resource pack. Vyn behavior still depends on a pack's model layout and scripts. Generic quad tint/selection applies to baked geometry; native special renderers retain their own geometry.

Dependency profiles were resolved against the published releases and compiled/run against their actual APIs. The 26.3 profile uses Fabric API 0.162.0+26.3 because the earlier cached 0.160.0 build failed a Fabric renderer startup mixin.

GitHub Actions reads the shared target catalog and runs builds and isolated client checks for all eight Minecraft targets. Screenshots/logs are uploaded as diagnostic artifacts per version.

Additional CI-environment validation: 1.21.10 and 1.21.11 passed their native suites with the test packet synchronizer disabled; 26.3 passed on Mesa llvmpipe 25.2.8/OpenGL 4.5 using EGL. The CI setup and initial diagnostic failures are documented in [VERSION_PORTING.md](VERSION_PORTING.md#ci-client-execution).

## Mod Menu / YACL settings verification

The shared settings factory originally checked `yacl` rather than YACL's actual Fabric ID, `yet_another_config_lib_v3`. A 26.2 client with both integrations installed reproduced the failure: Mod Menu's registered factory returned the parent screen. The missing-YACL test then reproduced a `NoClassDefFoundError` while Mod Menu loaded the entrypoint.

The corrected factory uses the actual mod ID and loads the separate YACL screen builder only after the presence check. Optional dependency metadata uses the same ID. The existing options and bindings were preserved during extraction.

All eight Minecraft targets passed `build runClientGameTest` with their actual Mod Menu and YACL versions. The fixture uses Mod Menu's registered IS factory, opens the actual screen in Minecraft, checks its title, renders it, captures a screenshot and verifies that closing settings returns to Mod Menu. The rendering/lifecycle suite and all 67 shared regression checks also passed.

Additional native runs passed on Java 21/Minecraft 1.21.5 and Java 25/Minecraft 26.2 with Mod Menu installed and YACL absent. These verify factory registration and the missing-library branch without launching an external file manager. The entrypoint remains usable without loading YACL classes.

CI exercises both dependency combinations for every supported target. Missing-YACL runs use the separate `clientGameTestWithoutYacl` directory; diagnostics from both modes are uploaded. Development-only `localRuntime`/`modLocalRuntime` dependencies are excluded from publication and release JARs.

The user confirmed that 0.8.0 has not been uploaded and requested keeping that version. The upload bundle and checksums were refreshed with the corrected builds.

## Multi-project Gradle / IDE validation (2026-10-10)

The repository now imports all eight Minecraft releases as separate modules. `prepareIde releaseAll` built every target in one Gradle invocation: **67 regression checks per target**, bytecode/metadata/release guards and upload ZIP checksums all passed. `prepareIde testAll` passed all eight real-client rendering/lifecycle suites and actual Mod Menu/YACL settings-screen checks. The `test26_2` shortcut also passed with YACL absent.

The Gradle Tooling API's IDE model reported nine projects (root + eight Minecraft modules), 50 distinct Java source roots, correct Java 21/25 language levels and each target's own Minecraft library. Versioned client configurations invoke fully qualified Gradle tasks and select their Java runtime automatically. Existing Java 21 and 25 JDKs were discovered without installation environment overrides.

Shortcut selection was checked for a legacy build, an explicit 26.2 launch and the 26.1.2 default-target patch. Temporary shared-resource additions and edits propagated to inherited copies; a native 26.3 override took precedence; removing the files removed all generated copies. The temporary probes were removed before final packaging. Qualified `:commands` prints help without configuring Minecraft modules.

Artifacts from this setup are in `build/dist/0.8.0/` and `build/dist/InteractiveStuff-0.8.0-all-versions.zip`. Updated release hashes are recorded in `docs/RELEASE_CHECKSUMS.json`; see `docs/IDE_WORKFLOW.md` for current commands and source ownership.

## Unified repository workflow validation (2026-10-10)

All six former version-branch heads are ancestors of `main`; the validated shared tree was preserved during consolidation. `:commands :buildSelected` selected the new 26.2 default and passed the 67 regression checks and release-JAR guards. The mod remains 0.8.0.

The catalog-driven workflow passed actionlint 1.7.12. Its actual matrix and packaging scripts were executed locally using the eight validated release JARs: all eight targets were selected, the twelve-file upload ZIP retained every JAR byte-for-byte, all SHA-256 entries matched, and a missing-JAR probe was rejected. GitHub runs the full matrix with both native dependency combinations and the final packaging job.

## Vyn first-person particle verification (2026-10-10)

All eight targets passed their final builds, **135 deterministic runtime/render checks each**, release guards and real-client suites with Mod Menu/YACL installed. `releaseAll` produced the eight 0.8.0 distributables and the thirteen-file upload ZIP, including `docs/VYN_PARTICLES.md`.

- Deterministic coverage includes all easing families and immutable keyframes, attached/detached coordinates, gravity/drag, interpolation, independent hand styles/rates, bursts, density/cap/drop handling, item-change completion, callbacks and owner/session cleanup. Equivalent 10/20/120 FPS simulations emit the same particle count.
- Native clients render torch and soul-torch effects with both dominant-hand preferences, enchanting glyphs, normal/calibrated sculk activation and a custom two-frame pack texture. A screenshot assertion verifies visible orange/blue custom-texture pixels beside the right-hand item after a 90-degree camera rotation. Simulation counts alone cannot verify native render-pass camera transforms.
- The final 26.3 native suite also passed with YACL absent on Mesa 25.2.8/OpenGL 4.5 through EGL and forced software rendering, including the visible custom-texture assertion and the existing Mod Menu fallback/lifecycle checks.
- Screenshot review confirms flame origins on the actual torch sprite tips, glyphs around the book and effects on both activated sensor variants. Complete native item/Vyn transforms are captured for the particle anchor, including physical-arm placement.
- Settings save/reload and range clamping, third-person hiding, global disable, resource reload and disconnect cleanup pass in the actual client. Emission uses the local held-item render pass; it never calls the Minecraft world particle engine.
- Native adapters account for render timing: 26.2/26.3 consume particle submissions with the camera stack active; 1.21.10/1.21.11/26.1 consume their queued pass after restoration. The 1.21.5 immediate renderer compensates for the active camera and flushes its particle buffers before restoration. Its separate mixin list includes the particle hook; the release guard checks hook registration on every target.
- Ordinary build/release tasks do not compile or run `src/gametest`. Inspection of all eight final distributables confirms that regression/client fixtures and `interactivestuff_test` textures are absent. The CI bundle retains every JAR byte-for-byte and includes matching SHA-256 entries and the Vyn guide; missing-JAR input is rejected.

The user's existing local removal of the `leaves.vyn` item filter was preserved and excluded from implementation commits. The recorded local release hashes include that working-tree edit; GitHub CI builds the committed script and produces its own checksums. Both forms retain coverage for deliberately unfiltered scripts and filtered dispatch.

This verifies visible rendering and lifecycle behavior on the supported native clients. It does not establish an FPS improvement or compatibility with every external shader/resource pack.
