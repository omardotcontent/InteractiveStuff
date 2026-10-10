# Minecraft 26.1.X

Build from the repository root with `./gradlew build26_1`.

Shared runtime, scripts and resources live in `src/`. Files under this folder override only Minecraft integration boundaries.

See [the adapter and verification guide](../../docs/VERSION_PORTING.md).

The production baseline is 26.1 with `~26.1` compatibility. `-PminecraftPatch=26.1.1` or `26.1.2` selects patch validation builds.

This folder is an IDE module in the root multi-version project. See [IDE commands](../../docs/IDE_WORKFLOW.md).
