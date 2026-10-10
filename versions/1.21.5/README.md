# Minecraft 1.21.5

Build from the repository root with `bash ./gradlew -Ptarget_version=1.21.5 build`.

Shared runtime, scripts and resources live in `src/`. Files under this folder override only Minecraft integration boundaries.

See [the adapter and verification guide](../../docs/VERSION_PORTING.md).

Inherits 1.21.10, then replaces the queue boundary with the native immediate renderer. Native namespace changes are generated at build time.
