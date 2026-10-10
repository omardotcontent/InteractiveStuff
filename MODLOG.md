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
