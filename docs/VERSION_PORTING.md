# Use 26.2 as the shared base

Start each new Minecraft port from the reviewed 26.2 overhaul commit/branch. Keep future fixes to the shared runtime and bundled behavior on this base first, then carry them into the version branches. The existing unfinished 26.3 checkout has been preserved for comparison.

This remains the repository's branch-per-version model. The overhaul separates responsibilities inside the code; it does not create a multi-loader/multi-version build framework.

## Shared systems

Keep these consistent across versions unless the dependency or language level changes:

- `runtime/TickScheduler` and `FrameClock`
- `scripts/ScriptRuntime`, `ScriptExecutionContext`, `objects/Script`, `PackScripts`
- Import/exclusion logic and deferred ownership
- Item filter contract, callback indexes and deterministic pack ordering
- `render/ColorMath` and `QuadSelection`
- Feature scripts and their public native API
- Regression fixtures and behavior checks

The shared scheduler/clock/script core has no Minecraft imports. Tests use the same local Vyn interpreter as production.

## Version boundaries

| Boundary | What to verify on the target Minecraft JAR |
| --- | --- |
| `render/MinecraftItemRenderer` | Item resolver entry point, item/layer render states, collector submission shape, foil types, native tint representation |
| `render/ColoredQuadCache` | Baked quad and material representation, tint index and immutable geometry fields |
| `render/RenderTransforms` | Quaternion and matrix operations on the pose stack; keep normal-matrix updates intact |
| `mixin/render/HeldItemRendererMixin` | Item-only first-person hook, local player/context detection, resolver ownership, cancellation point |
| Render accessors/invokers | Native layer fields, submission and transform methods |
| Game/camera mixins | Render/rotation hook signatures; frame timing stays backend-independent |
| Other mixins | Swing, key input, sound result and HUD extraction signatures |
| `ScriptInterpreter`, `ScriptLoader`, `BackgroundLoopHandler` | Fabric lifecycle/resource APIs and main-thread dispatch |
| Native variable wrappers/components | Registry IDs, camera/player/world methods, component codecs |
| Config/UI | Platform folder-opening API, optional Mod Menu/YACL versions |
| Gradle/resources/CI | Minecraft/loader/API versions, Java target, pack formats, mixin target compatibility and supported-version metadata |

For 26.3, inspect the newer first-person renderer's item submission path. The older `ItemInHandRenderer` resolver field is not a valid shadow on the newer renderer. An arm-level cancellation hook can omit arm/use/swing transforms, so choose an item-only boundary or explicitly preserve those transforms. Keep timing in `FrameClock`; do not substitute SDL milliseconds into a seconds-based delta formula.

Older branches may use different names/mappings and baked-quad layouts. Check their actual Java target and native APIs rather than copying 26.2 accessor names. The public Vyn function names should remain stable even when the Java adapter changes.

## Port process

1. Create the version branch from the finished base; use a separate checkout/worktree.
2. Update version dependencies, metadata, pack formats and mixin compatibility.
3. Compile to find changed native symbols. Check cached/source Minecraft and Fabric APIs for exact signatures.
4. Change the version adapter/mixins and affected native wrappers. Keep runtime algorithms and callback semantics intact.
5. Run `bash ./gradlew build` to exercise shared regression checks.
6. Adapt and run `bash ./gradlew runClientGameTest` on that version. Check plain items, baked tint, special renderer, split quads, extras, both hands, left-handed preference, resource reload and disconnect.
7. Inspect captured frames, logs and the release JAR. Confirm Vyn/license inclusion and absence of test/game classes.
8. Fix shared behavior on the base too, so a port does not become an independent implementation of the runtime.

The release metadata uses the selected Minecraft version's minor range rather than claiming compatibility with all future versions. Every port needs its own compiled and tested artifact.
