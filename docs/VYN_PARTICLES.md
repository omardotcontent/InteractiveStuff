# Vyn first-person particles

Create a persistent `ParticleEmitter` at the top of a resource-pack script and attach it to an `ItemModel` inside `onItemUpdate`. InteractiveStuff simulates and draws these effects in the local player's first-person held-item pass. They never enter Minecraft's world particle engine, appear to other players or render in third person.

The API follows the existing Vyn object/setter style and is shared by every supported Minecraft version. Enable **First-person particles → Enable particles** in IS settings. The bundled pack demonstrates flame/smoke in `torches_and_more.vyn`, glyphs in `enchanting_table.vyn` and activation effects in `sculk_sensors.vyn`.

## First emitter

Save this as a `.vyn` script under your pack's `assets/<namespace>/scripts/`:

```vyn
lock interactiveItems = ["minecraft:torch"]

make sparks = new ParticleEmitter("minecraft:flame")
sparks.setPosition(0.5, 0.75, 0.5)
sparks.setSpread(0.03, 0.02, 0.03)
sparks.setRate(0.5)
sparks.setLifetime(16)
sparks.setVelocity(0, 0.015, 0)
sparks.setSizeOverLife(0.04, 0.01, "easeOutQuad")
sparks.setOpacityOverLife(1, 0, "easeInSine")
sparks.setLight(15)
sparks.setAttached(true)

task onItemUpdate takes itemRendered do
    sparks.attach(itemRendered)
end
```

`setRate(0.5)` creates ten particles per second at Minecraft's normal 20-tick rate. All durations, velocity and gravity use **ticks**, matching `wait`; emission does not depend on FPS. Create emitters once, rather than allocating one per frame. A session can own at most 256 emitter objects.

`attach` records a binding for that rendered hand. It does not itself create particles. Call it on each `onItemUpdate` where the effect should run. The same object maintains independent main-hand/off-hand channels, including positions, rates and texture settings captured during each hand's render. Existing particles retain their birth settings when you change emitter setters.

## Coordinates, attachment and depth

Positions use the attached model's coordinates, after script transforms, hierarchy/pivots and its first native render-layer transform. A normal cuboid spans roughly `(0,0,0)` to `(1,1,1)`; `(0.5,0.75,0.5)` is near its upper center. Sprite and special models have their own layouts. Extra `ItemModel` objects are valid anchors: create/transform/render the extra, then call `sparks.attach(extra)`. Attaching twice in one hand's callback uses the last binding.

- `setAttached(true)`: positions, velocity and gravity stay in model coordinates. Alive particles follow the model, including physical-arm mirroring.
- `setAttached(false)`, the default: the model transforms the initial position and velocity into view coordinates at birth. Particles then drift in the camera layer without following later item movement. Gravity acts down the view's Y axis. They never stay at a world location.
- Sprites always face the view camera. `setRotation` rolls the sprite in degrees; `setSize` is its width/height in first-person view units. Size remains camera-facing and does not inherit later item scaling.
- `setDepthTest(true)`, the default: particles respect held-item depth. `false` uses an overlay that can show through the item. Effects belong to the hand projection, rather than world depth/collisions.

Changing inventory items or stopping attachment stops new emission. Alive particles finish at their last anchor by default; `setClearOnItemChange(true)` discards them when the hand loses its binding or changes item. `stop()` also allows particles to finish; `clear()` discards them immediately. Global disable clears live particles; third person hides particles/stops emission while their remaining lifetimes advance. Pausing single-player pauses simulation. Resource reload, world/dimension changes and disconnect release all particles and their script ownership.

## Texture types and custom packs

`new ParticleEmitter("minecraft:smoke")` loads the type's **appearance** from Minecraft's `assets/minecraft/particles/smoke.json`. Flame, soul fire, smoke, enchant and sculk charge textures work this way. This does not instantiate a vanilla particle class: movement, lifetime and easing come from your setters. Types requiring special world geometry or sprites derived from blocks/items need an explicit PNG or custom texture list.

A custom type needs no Java registry entry. For `new ParticleEmitter("mypack:spark")`, provide:

```text
assets/mypack/particles/spark.json
assets/mypack/textures/particle/spark_0.png
assets/mypack/textures/particle/spark_1.png
assets/mypack/textures/particle/spark_2.png
```

```json
{
  "textures": ["mypack:spark_0", "mypack:spark_1", "mypack:spark_2"]
}
```

The PNGs can have transparent pixels and different dimensions. Each frame fills one sprite quad; frames use the JSON's order. Up to 256 frame textures per type are supported. Definitions are cached with a bounded cache and refreshed on resource reload; a missing/malformed definition or PNG logs a diagnostic and uses a generic fallback.

For one PNG without a JSON definition:

```vyn
sparks.setTexture("mypack:textures/particle/spark.png")
```

`setType("minecraft:flame")` switches back to a type's JSON and clears a direct PNG override. Direct PNG paths should exist in your pack; Minecraft displays its missing texture if the path is absent.

Animation controls:

```vyn
sparks.setAnimation("lifetime", 1)
sparks.setAnimation("loop", 2)
sparks.setAnimation("random", 1)
sparks.setAnimation("first", 1)
```

Frame duration is used by `loop`; it must be between 0.05 and 12000 ticks. This API animates explicit PNG lists; it does not use `.png.mcmeta` texture-atlas animation.

## Emission and motion setters

| Method | Meaning / supported values |
| --- | --- |
| `attach(itemModel)` | Bind within `onItemUpdate`; requires an active item-render scope |
| `setType("namespace:type")` | Vanilla or pack particle JSON, clears direct PNG override |
| `setTexture("namespace:textures/path.png")` | Single direct PNG |
| `setRate(number)` | 0–64 particles per tick per bound hand; default 0 |
| `emit()` / `burst(count)` | Queue one / up to 128 particles for each currently bound hand |
| `burstForHand(mainHand, count)` | Queue a burst for one logical hand; `true` main, `false` off |
| `start()` / `stop()` | Enable / stop new emission; enabled initially |
| `clear()` / `getCount()` | Discard particles/queued emission in both hands / current particle count |
| `setLifetime(ticks)` | 1–12000 ticks; default 20 |
| `setLifetimeSpread(ticks)` | Uniform lifetime variation ±ticks; 0–12000, minimum final lifetime 1 |
| `setPosition(x,y,z)` | Emitter origin in model coordinates; default zero |
| `setSpread(x,y,z)` | Uniform random position variation ±each axis; 0–100 |
| `setVelocity(x,y,z)` | Initial displacement per tick in model coordinates |
| `setVelocitySpread(x,y,z)` | Uniform random velocity variation ±each axis; 0–100 |
| `setGravity(number)` | Subtract from Y velocity each tick; positive falls, negative rises |
| `setDrag(number)` | Multiply velocity by `1-drag` after gravity; 0–1 |
| `setAttached(boolean)` | Follow the item or drift in view coordinates |
| `setDepthTest(boolean)` | Depth or overlay drawing |
| `setClearOnItemChange(boolean)` | Clear immediately when unbound; default false |
| `setLight(number)` | 0–15 for fixed light, -1 for held-item environmental light; default -1 |
| `setSeed(number)` | Set random seed for reproducible emitter variations |
| `setAnimation(mode, frameTicks)` | Texture-list animation, described above |

Call `burst` from an event such as `onSwingHand` or `onPlaySound`, after the emitter has been attached by a render. Queued bursts spawn on the next simulation tick; unbound hands ignore them. Avoid calling `burst` every render frame because that request rate would depend on FPS. Use `setRate` for continuous effects.

## Easing and keyframes

Every property supports a constant, a start/end curve, or lifetime keys:

```vyn
sparks.setSize(0.04)
sparks.setOpacity(0.8)
sparks.setRotation(20)
sparks.setColor(255, 180, 80)

sparks.setSizeOverLife(0.02, 0.08, "easeOutCubic")
sparks.setOpacityOverLife(0, 0, "linear")
sparks.addOpacityKeyframe(0.15, 1, "easeOutQuad")
sparks.addOpacityKeyframe(0.7, 0.6, "linear")
sparks.setRotationOverLife(0, 180, "easeInOutSine")
sparks.setColorOverLife(255, 240, 120, 255, 40, 10, "easeInQuad")
sparks.addColorKeyframe(0.5, 255, 120, 30, "linear")
```

| Property | Constant | Start/end | Keyframe |
| --- | --- | --- | --- |
| Size (0–100) | `setSize(size)` | `setSizeOverLife(start,end,easing)` | `addSizeKeyframe(progress,size,easing)` |
| Opacity (0–1) | `setOpacity(alpha)` | `setOpacityOverLife(start,end,easing)` | `addOpacityKeyframe(progress,alpha,easing)` |
| Roll (degrees) | `setRotation(degrees)` | `setRotationOverLife(start,end,easing)` | `addRotationKeyframe(progress,degrees,easing)` |
| Color (RGB 0–255) | `setColor(r,g,b)` | `setColorOverLife(r1,g1,b1,r2,g2,b2,easing)` | `addColorKeyframe(progress,r,g,b,easing)` |

Key progress ranges from 0 (birth) to 1 (death). Adding the same progress replaces that key. Its easing controls the interval **arriving at** that key. Constant/start-end setters replace the previous curve; add keys afterwards. Keys are immutable snapshots shared safely by existing particles. Final opacity/color/size are clamped when drawing if an overshooting easing crosses their range.

Use `linear`, `step`, or `easeIn`, `easeOut`, `easeInOut` with `Quad`, `Cubic`, `Quart`, `Quint`, `Sine`, `Expo`, `Circ`, `Back`, `Bounce`, `Elastic`. Names ignore case, underscores and hyphens. Invalid names, nonfinite numbers and invalid setter ranges fail the owning script with a diagnostic.

## Optional particle callbacks

Built-in controls do not require a callback. When a particle needs custom logic, name a task in the same script:

```vyn
sparks.setOnSpawn("sparkBorn")
sparks.setOnUpdate("sparkTick")
sparks.setOnDeath("sparkDied")

task sparkBorn takes particle do
    check particle.isOffHand() do
        particle.setColor(80, 180, 255)
    end
end

task sparkTick takes particle do
    check particle.getAge() > 8 do
        particle.setVelocity(0, 0.02, 0)
    end
end

task sparkDied takes particle do
    debugText("Spark finished")
end
```

Spawn runs once at birth. Update runs once per unpaused tick after motion, while alive. Death runs once on expiry or `kill()` and also when the global cap trims a live particle. Explicit clear, disable, item-change clear and session teardown discard particles without death callbacks. `setOnSpawn("")`, `setOnUpdate("")`, `setOnDeath("")` remove callbacks for future particles. A missing/failing task disables its owning script; healthy scripts continue running. Avoid expensive per-particle scans inside update callbacks.

The `Particle` handle exposes `getAge()`, `getLifetime()`, `getProgress()`, `getX()`, `getY()`, `getZ()`, `isAlive()`, `isMainHand()`, `isOffHand()`, `kill()`, `setPosition(x,y,z)`, `setVelocity(x,y,z)`, `setSize(size)`, `setOpacity(alpha)`, `setRotation(degrees)` and `setColor(r,g,b)`. Attached handles use model coordinates; detached handles use view coordinates. Callback visual setters replace that particle's curve, without changing other particles. Retaining a handle after death does not respawn it.

## Player settings and debug controls

IS → **First-person particles** offers global enable, density (0–200%, default 100%), a shared both-hand live cap (0–4096, default 512), live counters and emitter-origin markers. Density scales rates and bursts; fractional emissions carry forward. At the cap, new births are dropped. Lowering the cap trims existing particles. Markers use green for the logical main hand and orange for the off hand; camera-facing billboards make them readable on either physical arm.

The global Vyn object provides `particles.getCount()`, `particles.getEmitterCount()`, `particles.getDroppedCount()` and `particles.clear()`. Clear discards particles across all scripts while keeping emitters usable. Drop counts reset with the session. Scripts can read `config.isParticlesEnabled()`, `config.getParticleDensity()` (percentage) and `config.getParticleLimit()`.

## Building and checking

Use the normal shared project commands:

```sh
./gradlew build26_2     # Headless regression checks and release separation guard
./gradlew test26_2      # Real Minecraft first-person demos/settings/reload checks
./gradlew buildAll     # Compile and verify all eight targets
./gradlew releaseAll   # Collect upload JARs and checksums
```

`src/gametest` remains a separate test mod. Ordinary build/release commands do not compile or package it. Native tests include custom test-only texture frames and screenshots of both hands, glyphs, both activated sensors and overlay particles. See [MULTIVERSION_VALIDATION.md](MULTIVERSION_VALIDATION.md) for measured results.
