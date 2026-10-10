# Minecraft 1.21.10

Build from the repository root with `bash ./gradlew -Ptarget_version=1.21.10 build`.

Shared runtime, scripts and resources live in `src/`. Files under this folder override only Minecraft integration boundaries.

See [the adapter and verification guide](../../docs/VERSION_PORTING.md).

Inherits the 1.21.11 queue adapter and overrides its quad representation and client access. Native namespace changes are generated at build time.
