# Minecraft 26.1.X

Build from the repository root with `bash ./gradlew -Ptarget_version=26.1.X build`.

Shared runtime, scripts and resources live in `src/`. Files under this folder override only Minecraft integration boundaries.

See [the adapter and verification guide](../../docs/VERSION_PORTING.md).

The production baseline is 26.1 with `~26.1` compatibility. `-PminecraftPatch=26.1.1` or `26.1.2` selects patch validation builds.
