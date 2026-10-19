Changes from 0.7.0.

### Additions

- **First-person particle system for Vyn.** Resource-pack authors can create emitters using vanilla particle appearances or custom textures, with continuous emission, bursts, randomized positions and velocities, gravity, drag and lifetime controls.
- **Particle animation controls.** Animate size, opacity, color and rotation using easing and keyframes, with optional spawn, update and death callbacks.
- **Particle spaces.** Effects can follow an item, remain relative to the view, or stay at their spawn location as the player walks and turns. They render in first person rather than spawning Minecraft world particles.
- **More Minecraft versions.** Added Fabric support for **1.20.1, 1.21.1, 1.21.5, 26.1, 26.1.1, 26.1.2 and 26.3**, alongside the existing **1.21.10, 1.21.11 and 26.2** builds.
- **More languages.** Added Dutch, Filipino, French (France and Canada), Polish, Simplified Chinese, Spanish (Argentina and Mexico), Turkish, Ukrainian, Thai, Hindi, and English (UK, Canada and Australia).

### Changes

- **New settings menu.** Replaced YACL with an animated Minecraft-style screen featuring sidebar categories, themed icons, search, tooltips, Apply/Cancel and reset controls. YACL is no longer required.
- **Settings access without Mod Menu.** Added a Minecraft Options button and an optional keybind. Mod Menu remains supported.
- **Searchable selectors.** Inventory-style item pickers simplify editing materials, excluded blocks and per-item pitches. Sounds have a searchable selector.
- **Rendering and scripting overhaul.** Reworked model handling, transforms, tint processing, script dispatch and caching to reduce repeated work.
- **Clearer marker tools.** Pivot and emitter markers have distinct shapes and colors, with an option to show them through held items.

### Bug fixes

- Fixed overlapping Vyn waits with the same duration preventing some callbacks from running.
- Improved cleanup of scheduled script work when changing worlds or disconnecting.
- Fixed interaction sounds incorrectly requiring a targeted block when a material was configured to allow air swings.

### Other

- Broadened compatibility with older stable Fabric Loader and Fabric API versions appropriate to each Minecraft target. Newer compatible versions remain supported.

## Bundled resource-pack update

These changes belong to the resource pack included with InteractiveStuff.

### Additions

- **Particle effects for existing items.** Added enchanting-table glyphs, sculk activation bursts and note-block particles with pitch-based colors and eased upward movement.
- **Pack-owned options.** The bundled pack now exposes its own saved settings, organized separately from the mod's global settings:

| Category | Options |
| --- | --- |
| Appearance | Tinted blocks, tinted saplings, tinted water, and enchanting-table book visibility. |
| Physics | Master physics switch, plus individual switches for the mace, item frames, paintings and hanging signs. |
| Animations | Shake intensity; piston, chest, vault, redstone-lamp, enchanting-book and sculk-sensor animations; torch extinguishing. |
| Particles | Separate emission switches for enchanting tables, sculk sensors and note blocks. |
| Sounds | Note-block playback for crouching and swinging interactions. |
| Debug | Vyn debug text and particle counter text. |

### Changes

- Reworked the existing item physics and animation scripts for more consistent motion and independent animation progress in both hands.
- Improved the enchanting-table update logic with cached bookshelf checks.
- Moved pack-specific behavior controls out of the mod's global settings. Saved choices remain associated with the resource pack.

### Bug fixes

- Fixed item-frame orientation when using left-handed controls.
- Fixed chest, piston, enchanting-book and mace-lean animations advancing faster when matching items were held in both hands.
- Fixed the totem shaking at health values above 20.
