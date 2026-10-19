Changes from 0.7.0

## Additions

### Particles in Vyn!
- Resource-pack creators can now add particles to held items using Vyn! Use vanilla particle textures or your own custom textures, create bursts or continuous effects, and control their position, velocity, gravity, drag and lifetime.
- You can also animate their size, color, opacity and rotation with easing and keyframes. Spawn, update and death callbacks are available for more advanced effects.
- Particles can follow the item, stay relative to your view, or move independently as you walk and look around. They only render in first person and don't spawn particles into Minecraft's world.

### More Minecraft versions!
Added Fabric support for:

- 1.20.1
- 1.21.1
- 1.21.5
- 26.1, 26.1.1 and 26.1.2
- 26.3

The existing 1.21.10, 1.21.11 and 26.2 versions are still supported!

### More languages
Added Dutch, Filipino, French (France and Canada), Polish, Simplified Chinese, Spanish (Argentina and Mexico), Turkish, Ukrainian, Thai, Hindi, and English (UK, Canada and Australia).

## Changes

### A new settings menu!
Replaced the YACL menu with InteractiveStuff's own Minecraft-style settings screen! It includes animated transitions, category buttons, themed icons, search, tooltips and Apply, Cancel and Reset controls.

**YACL is no longer required.**

### More ways to open the settings
You can open the settings through Mod Menu, the Minecraft Options screen when Mod Menu isn't installed, or an optional keybind.

### Easier item and sound selection
Materials, excluded blocks and item-specific pitches now use searchable inventory-style selectors. Sounds also have their own searchable selector, so there's less need to enter IDs manually.

### Rendering and Vyn improvements
Reworked model rendering, transforms, tinting and script handling to avoid repeating unnecessary work.

### Better debug markers
Pivot points and particle emitters now have different marker shapes and colors. You can also choose to show the markers through held items.

## Bug fixes

- Fixed Vyn waits with the same duration interfering with each other.
- Improved script cleanup when leaving or changing worlds.
- Fixed interaction sounds not playing without a targeted block when air swings were allowed.

## Other

- Broadened support for older stable Fabric Loader and Fabric API versions for each Minecraft version. Compatible newer versions still work.

---

## Bundled resource-pack update

These are changes to the resource pack included with the mod.

## Additions

### More particle effects!
- Enchanting-table glyphs.
- Particle bursts when sculk sensors activate.
- Note-block particles that rise with easing and change color depending on the pitch.

### Resource-pack options
The bundled pack now has its own settings! You can find them in the **Packs** section of the settings menu.

| Category | Options |
| --- | --- |
| Appearance | Block, sapling and water tinting; showing the enchanting-table book. |
| Physics | A switch for the entire physics engine, plus separate switches for the mace, item frames, paintings and hanging signs. |
| Animations | Shake intensity, pistons, chests, vaults, redstone lamps, enchanting books, sculk sensors and torch extinguishing. |
| Particles | Separate switches for enchanting-table, sculk-sensor and note-block particles. |
| Sounds | Note-block sounds when crouching or swinging. |
| Debug | Vyn debug text and particle counters. |

## Changes

- Reworked the item physics and animation scripts for more consistent movement.
- Animations now keep their progress separately for each hand.
- Improved the enchanting table's nearby-bookshelf checks.
- Moved resource-pack-specific settings into the pack's own options. Your choices are saved for that pack.

## Bug fixes

- Fixed item-frame orientation when using left-handed controls.
- Fixed chest, piston, enchanting-book and mace-lean animations running faster when holding matching items in both hands.
- Fixed totems shaking when the player's health was above 20.
- Fixed the enchanting-table book opening unevenly.