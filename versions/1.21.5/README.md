# Minecraft 1.21.5

Build from the repository root with `./gradlew build1_21_5`.

Shared runtime, scripts and resources live in `src/`. Files under this folder override only Minecraft integration boundaries.

See [the adapter and verification guide](../../docs/VERSION_PORTING.md).

Inherits 1.21.10, then replaces the queue boundary with the native immediate renderer. Native namespace changes are generated at build time.

This folder is an IDE module in the root multi-version project. See [IDE commands](../../docs/IDE_WORKFLOW.md).
