# Multi-version verification — 2026-10-10

Each row passed `build runClientGameTest`. Shared regression output is **67 runtime/render checks** per target. Tests ran against the real target Minecraft JAR, actual Fabric mixins and the OpenGL client; 1.21 clients ran on Java 21, 26.x clients on Java 25.

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

JARs: `build/<target>/<minecraft>/libs/`. Client logs and four screenshots per run: `build/<target>/<minecraft>/run/clientGameTest/`. Test worlds are disposable and isolated from user saves. Checksums are in [RELEASE_CHECKSUMS.json](RELEASE_CHECKSUMS.json).

## Practical scope

These checks validate the port and native render/lifecycle boundaries. They are not an FPS benchmark or coverage of every external shader/animation/resource pack. Vyn behavior still depends on a pack's model layout and scripts. Generic quad tint/selection applies to baked geometry; native special renderers retain their own geometry.

Dependency profiles were resolved against the published releases and compiled/run against their actual APIs. The 26.3 profile uses Fabric API 0.162.0+26.3 because the earlier cached 0.160.0 build failed a Fabric renderer startup mixin.

GitHub Actions runs build and isolated client checks for each version branch default; screenshots/logs are uploaded as diagnostic artifacts.

Additional CI-environment validation: 1.21.10 and 1.21.11 passed their native suites with the test packet synchronizer disabled; 26.3 passed on Mesa llvmpipe 25.2.8/OpenGL 4.5 using EGL. The CI setup and initial diagnostic failures are documented in [VERSION_PORTING.md](VERSION_PORTING.md#ci-client-execution).

## Mod Menu / YACL settings verification

The shared settings factory originally checked `yacl` rather than YACL's actual Fabric ID, `yet_another_config_lib_v3`. A 26.2 client with both integrations installed reproduced the failure: Mod Menu's registered factory returned the parent screen. The missing-YACL test then reproduced a `NoClassDefFoundError` while Mod Menu loaded the entrypoint.

The corrected factory uses the actual mod ID and loads the separate YACL screen builder only after the presence check. Optional dependency metadata uses the same ID. The existing options and bindings were preserved during extraction.

All eight Minecraft targets passed `build runClientGameTest` with their actual Mod Menu and YACL versions. The fixture uses Mod Menu's registered IS factory, opens the actual screen in Minecraft, checks its title, renders it, captures a screenshot and verifies that closing settings returns to Mod Menu. The rendering/lifecycle suite and all 67 shared regression checks also passed.

Additional native runs passed on Java 21/Minecraft 1.21.5 and Java 25/Minecraft 26.2 with Mod Menu installed and YACL absent. These verify factory registration and the missing-library branch without launching an external file manager. The entrypoint remains usable without loading YACL classes.

CI exercises both dependency combinations for every branch default. Missing-YACL runs use the separate `clientGameTestWithoutYacl` directory; diagnostics from both modes are uploaded. Development-only `localRuntime`/`modLocalRuntime` dependencies are excluded from publication and release JARs.

The user confirmed that 0.8.0 has not been uploaded and requested keeping that version. The upload bundle and checksums were refreshed with the corrected builds.
