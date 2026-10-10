Changelogs from 0.7.0

## Changes

### Rendering and Performance Overhaul

InteractiveStuff 0.8.0 reworks the rendering and shared systems to make item animations more efficient and reliable! Items now run only the scripts they need, with fewer unnecessary item copies and less work during rendering.

### Better Item Rendering

Improved how animated items preserve Minecraft's original rendering, including colors, lighting, enchantment glint and special models such as shields. Fixed tint handling, extra models and partial-model rendering.

### More Reliable Vyn Scripts

Improved script loading, imports, animation timing and delayed actions. Multiple waits can now run independently, and pending actions are cleaned up correctly when reloading resources, changing worlds or disconnecting.

### Left-Handed Fixes

Fixed item frames, chests and vault animations using the wrong side when playing left-handed! Their movement now follows the actual left or right hand.

### Lots of Bug Fixes

- Fixed the InteractiveStuff settings button in Mod Menu incorrectly treating installed YACL as missing.
- Fixed Mod Menu integration failing to load when the optional YACL library is absent.
- Fixed component data handling for nested values and quoted text.
- Improved swing events, hit sounds and keyboard input handling.
- Fixed configuration reloads, saving and material settings.
- Improved camera rotation smoothing and player-position calculations.
- Fixed extra-model cleanup and hand-context handling.
- Corrected missing particle references in custom models.

### Others

- Optimized the enchanting table's nearby-block checks.
- Improved script ordering and resource-pack reload behavior.
- Reduced work from debug scripts when debug mode is disabled.
- Made the 26.2 overhaul the shared base for every supported version!

## Additions

### New Vyn Hand Helpers

Added `isLeftHand()` and `isRightHand()` so resource-pack scripts can follow the physical hand holding an item. The existing `isMainHand()` and `isOffHand()` helpers still identify the inventory slots.

### Shared Version Support

The same 0.8.0 overhaul is available for:

- 1.21.5
- 1.21.10
- 1.21.11
- 26.1, 26.1.1 and 26.1.2
- 26.2
- 26.3
