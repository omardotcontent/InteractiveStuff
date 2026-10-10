# Vyn first-person particles

Create a persistent `ParticleEmitter` at the top of a resource-pack script and attach it to an `ItemModel` inside `onItemUpdate`. InteractiveStuff simulates and draws these effects in the local player's first-person held-item pass. They never enter Minecraft's world particle engine, appear to other players or render in third person.

The API follows the existing Vyn object/setter style and is shared by every supported Minecraft version. Enable **First-person particles → Enable particles** in IS settings. The bundled pack demonstrates flame/smoke in `torches_and_more.vyn`, glyphs in `enchanting_table.vyn` and activation effects in `sculk_sensors.vyn`.

## API objects and argument types

| Object | How you get it | Purpose |
| --- | --- | --- |
| `ParticleEmitter` | `new ParticleEmitter("namespace:type")` | Configure, bind and spawn particles |
| `Particle` | The argument to a spawn/update/death task | Read or modify one existing particle |
| `particles` | Built-in global object | Inspect or clear particles across all scripts |
| `config` | Built-in global object | Read the player's particle settings |

`number` means a Vyn numeric value; RGB, light, burst counts and random seeds use integers. `boolean` means `true` or `false`; strings are quoted; an `ItemModel` is an object such as the argument received by `onItemUpdate`. Setter/control functions return `nothing`: call them separately rather than chaining them. Getters return the value described in their table.

Durations are in ticks: **20 ticks = 1 second** at normal game speed. Velocity is displacement per tick; gravity changes Y velocity per tick. Lifetime arguments can be fractional; expiry happens on the first simulation tick whose age reaches the lifetime. Global density, the live cap, binding and enable settings can reduce actual emissions.

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

`setRate(0.5)` requests ten particles per second at Minecraft's normal 20-tick rate, with 100% density and available capacity. All durations, velocity and gravity use **ticks**, matching `wait`; emission does not depend on FPS. Create emitters once, rather than allocating one per frame. A session can own at most 256 emitter objects.

`attach` records a binding for that rendered hand. It does not itself create particles. Call it on each `onItemUpdate` where the effect should run. The same object maintains independent main-hand/off-hand channels, including positions, rates and texture settings captured during each hand's render. Configure the emitter **before** calling `attach`; changes to its style/rate are captured on the next binding and used by subsequent births. Existing particles retain their birth settings. `start`, `stop`, `clear`, random seed and the item-change clearing policy act on the emitter directly.

## Why `interactiveItems` is there

```vyn
lock interactiveItems = ["minecraft:sculk_sensor", "minecraft:calibrated_sculk_sensor"]
```

`interactiveItems` is optional metadata read by InteractiveStuff's **Java script loader**, so it does not need a reference elsewhere in the Vyn script. Keep this exact variable name. The loader reads the array when the script loads and uses it to select which scripts receive `onItemUpdate` for a held item.

For this example, the sensor script receives item updates only for the normal or calibrated sculk sensor. Holding stone or a torch skips this script's item callback, avoiding unnecessary script calls and item bindings during rendering. It does not filter `onTick`, `onPlaySound`, `onSwingHand` or `onKeyPress`.

The declaration is optional: omitting it makes `onItemUpdate` run for every rendered held item. In that case, the callback needs its own item-name checks before changing models or attaching emitters; otherwise effects can bind to unrelated items. Keeping the declaration avoids that extra callback work. Add new supported item IDs to this array as well as the script's own item checks, then reload resources so the loader rebuilds its callback index.

## Constructor and defaults

```vyn
make effect = new ParticleEmitter("minecraft:smoke")
```

The constructor requires a lowercase namespaced texture-type ID. It creates an enabled emitter, registers it with this script/session and returns the emitter object. It does not spawn anything until the emitter is bound and has a positive rate or a queued burst.

| Setting | Default |
| --- | --- |
| Emission rate / enabled | `0` / `true` |
| Lifetime / lifetime spread | `20` ticks / `0` |
| Position / position spread | `(0,0,0)` / `(0,0,0)` |
| Velocity / velocity spread | `(0,0,0)` / `(0,0,0)` |
| Gravity / drag | `0` / `0` |
| Size / opacity / rotation | `0.06` / `1` / `0` degrees |
| RGB | `(255,255,255)` |
| Attached / depth test | `false` / `true` |
| Clear on item change | `false` |
| Light | `-1`, environmental light |
| Animation / frame duration | `"lifetime"` / `1` tick |
| Callbacks | Empty strings, disabled |

## Coordinates, attachment and depth

Positions use the attached model's coordinates, after script transforms, hierarchy/pivots and its first native render-layer transform. A normal cuboid spans roughly `(0,0,0)` to `(1,1,1)`; `(0.5,0.75,0.5)` is near its upper center. Sprite and special models have their own layouts. Extra `ItemModel` objects are valid anchors: create/transform/render the extra, then call `sparks.attach(extra)`. Attaching twice in one hand's callback uses the last binding.

- `setAttached(true)`: positions, velocity and gravity stay in model coordinates. Alive particles follow the model, including physical-arm mirroring.
- `setAttached(false)`, the default: the model transforms the initial position and velocity into view coordinates at birth. Particles then drift in the camera layer without following later item movement. Gravity acts down the view's Y axis. They never stay at a world location.
- Sprites always face the view camera. `setRotation` rolls the sprite in degrees; `setSize` is its width/height in first-person view units. Size remains camera-facing and does not inherit later item scaling.
- `setDepthTest(true)`, the default: particles respect held-item depth. `false` uses an overlay that can show through the item. Effects belong to the hand projection, rather than world depth/collisions.

Changing inventory items or stopping attachment stops new emission. Alive particles finish at their last anchor by default; `setClearOnItemChange(true)` discards them when the hand loses its binding or changes item. `stop()` also allows particles to finish; `clear()` discards them immediately. Clearing keeps emitters usable and does not stop a configured continuous rate: call `stop()` as well to prevent new births. Global disable clears live particles; third person hides particles/stops emission while their remaining lifetimes advance, or clears them if the item-change clearing policy is enabled. Pausing single-player pauses simulation. Resource reload, world/dimension changes and disconnect release all particles and their script ownership.

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

The PNGs can have transparent pixels and different dimensions. Each frame fills one square sprite quad; square images preserve their aspect ratio. Frames use the JSON's order. Up to 256 frame textures per type are supported. Definitions are cached with a bounded cache and refreshed on resource reload; a missing/malformed definition or PNG logs a diagnostic and uses a generic fallback.

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

| Mode | Behavior |
| --- | --- |
| `"lifetime"` | Advance through the JSON frames over the particle's lifetime; the default |
| `"loop"` | Advance one frame every `frameTicks` ticks and repeat the frame list |
| `"random"` | Choose one frame at birth and keep it for that particle |
| `"first"` | Always use the first frame |

Mode strings are lowercase and case-sensitive. Frame duration is used by `loop`; it must be between 0.05 and 12000 ticks for every mode. A single direct PNG has no frame list to animate. This API animates explicit PNG lists; it does not use `.png.mcmeta` texture-atlas animation.

## Emitter functions: binding and emission

Call these on your emitter, for example `sparks.setRate(0.5)`.

| Function | Arguments and behavior | Returns |
| --- | --- | --- |
| `attach(itemModel)` | Bind an `ItemModel` within `onItemUpdate`. Repeat each render update; the last binding in that hand wins. | `nothing` |
| `setRate(particlesPerTick)` | Number `0–64` per bound hand; `0` disables continuous emission. Fractional requests accumulate. Default `0`. | `nothing` |
| `emit()` | Queue one particle for each hand; equivalent to `burst(1)`. | `nothing` |
| `burst(count)` | Queue an integer count for both hands; only actively bound hands spawn. Negative counts become `0`; each hand's queued total is capped at `128`. | `nothing` |
| `burstForHand(mainHand, count)` | Same queue/cap behavior for one logical hand: `true` is the main inventory hand, `false` the off hand. | `nothing` |
| `start()` | Enable new births; attachment is still required. Enabled initially. | `nothing` |
| `stop()` | Disable new births and clear queued bursts; existing particles keep advancing. | `nothing` |
| `clear()` | Immediately discard this emitter's particles, bursts and fractional requests in both hands. Does not change its enabled state. | `nothing` |
| `getCount()` | Number of particles currently tracked by this emitter across both hands. Killed handles can remain counted until simulation cleanup. | Integer |
| `setSeed(seed)` | Integer random seed for reproducible future position, velocity, lifetime and frame variations. Does not reset existing particles. | `nothing` |

Call `burst` from an event such as `onSwingHand` or `onPlaySound`, after the emitter has been attached by a render. Queued bursts spawn on the next simulation tick; unbound hands ignore them. Avoid calling `burst` every render frame because that request rate would depend on FPS. Use `setRate` for continuous effects.

To emit a burst from the main inventory hand whenever the swing event fires, add these lines to a script that already attaches the emitter:

```vyn
sparks.setRate(0)

task onSwingHand do
    sparks.burstForHand(true, 8)
end
```

The boolean selects the inventory slot, independently of the player's left/right dominant-hand preference. A hand can create at most 128 particles in one simulation tick from its combined continuous/burst requests, before the shared live cap. Density scales these requests; `burst(8)` therefore requests four particles per bound hand at 50% density.

## Emitter functions: lifetime and motion

All functions in this table return `nothing`.

| Function | Arguments, behavior and defaults |
| --- | --- |
| `setLifetime(ticks)` | Number `1–12000`; default `20`. How long new particles live. |
| `setLifetimeSpread(ticks)` | Number `0–12000`; default `0`. Uniform variation ±this amount around lifetime. Final lifetime is at least `1`; variation can extend it beyond the base limit. |
| `setPosition(x,y,z)` | Three finite numbers. Model-local spawn origin; default `(0,0,0)`. |
| `setSpread(x,y,z)` | Three numbers, each `0–100`. Uniform random spawn variation ±each axis; default zero. |
| `setVelocity(x,y,z)` | Three finite numbers. Initial model-local displacement per tick; default zero. Detached particles transform this into view coordinates at birth. |
| `setVelocitySpread(x,y,z)` | Three numbers, each `0–100`. Uniform random velocity variation ±each axis; default zero. |
| `setGravity(gravity)` | Finite number; default `0`. Subtract it from Y velocity each tick: positive falls, negative rises, in the particle's current coordinate space. |
| `setDrag(drag)` | Number `0–1`; default `0`. Multiply all velocity components by `1-drag` after applying gravity each tick. |

For example, `setSpread(0.1,0.02,0.1)` chooses a random offset in X/Z from `-0.1` to `0.1` and Y from `-0.02` to `0.02`. `setVelocity(0,0.01,0)` starts particles upward; `setDrag(0.1)` retains 90% of velocity per tick. Motion applies gravity, then drag, then movement.

## Emitter functions: texture, attachment and rendering

All functions in this table return `nothing`.

| Function | Arguments, behavior and defaults |
| --- | --- |
| `setType(type)` | Lowercase namespaced ID, such as `"minecraft:flame"`. Load that particle JSON's appearance and clear any direct PNG override. Required initially through the constructor. |
| `setTexture(texture)` | String such as `"mypack:textures/particle/spark.png"`. Use this PNG directly. Switch back to JSON textures with `setType`, rather than an empty string. |
| `setAnimation(mode, frameTicks)` | Mode string from the animation table and number `0.05–12000`. Default `"lifetime"`, `1`. |
| `setAttached(attached)` | Boolean; default `false`. `true` follows the model; `false` drifts in view coordinates. Applies to future births. |
| `setDepthTest(depth)` | Boolean; default `true`. `true` respects held-item depth; `false` draws an overlay. |
| `setClearOnItemChange(clear)` | Boolean; default `false`. `true` discards particles when their hand loses its binding or changes items, including leaving first person. |
| `setLight(light)` | Integer `-1–15`; default `-1`. `-1` uses hand environmental light; `0–15` set fixed block/sky light, with `15` full brightness. Does not cast light into the world. |

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

All twelve visual functions above return `nothing`. Size, opacity and rotation use numeric values; RGB channels use integers. Constant defaults are size `0.06`, opacity `1`, rotation `0` and white `(255,255,255)`. Constant setters replace an existing curve; `OverLife` defines values at progress `0` and `1`; `add...Keyframe` inserts an intermediate value into that property.

Key progress ranges from 0 (birth) to 1 (death). A progress of `0.25` is one quarter of the particle's lifetime, rather than 0.25 ticks. Adding the same progress replaces that key. Its easing controls the interval **arriving at** that key. Constant/start-end setters replace the previous curve; add keys afterwards. Keys are immutable snapshots shared safely by existing particles. Final opacity/color/size are clamped when drawing if an overshooting easing crosses their range.

Use `linear`, `step`, or `easeIn`, `easeOut`, `easeInOut` with `Quad`, `Cubic`, `Quart`, `Quint`, `Sine`, `Expo`, `Circ`, `Back`, `Bounce`, `Elastic`. Names ignore case, underscores and hyphens. Invalid names, nonfinite numbers and invalid setter ranges fail the owning script with a diagnostic.

`easeIn` starts slowly; `easeOut` slows toward the end; `easeInOut` does both. For example, `"easeOutQuad"`, `"easeInSine"` and `"easeInOutCubic"` are valid. `linear` changes at a constant rate; `step` holds the previous key's value until the next key. Rotation is a numeric interpolation in degrees, so `setRotationOverLife(0,360,"linear")` makes a complete turn rather than choosing a shortest-angle path.

## Optional particle callbacks

Built-in controls do not require a callback. When a particle needs custom logic, name a task in the same script:

| Emitter function | Callback timing | Returns |
| --- | --- | --- |
| `setOnSpawn(taskName)` | String naming a task receiving one `Particle`; runs once at birth | `nothing` |
| `setOnUpdate(taskName)` | String naming a task receiving one `Particle`; runs after movement on each tick while alive | `nothing` |
| `setOnDeath(taskName)` | String naming a task receiving one `Particle`; runs when simulation removes it after expiry/kill or cap trimming | `nothing` |

All three return `nothing`, default to `""` (disabled), and use an empty string to remove that callback for future particles.

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

## Individual `Particle` functions

Call these on the `particle` argument received by your callback. There is no public Vyn `new Particle(...)` constructor; use an emitter to spawn particles.

| Function | Meaning and arguments | Returns |
| --- | --- | --- |
| `getAge()` | Current age in simulation ticks; starts at `0`. | Number |
| `getLifetime()` | This particle's final lifetime, including random lifetime spread. | Number |
| `getProgress()` | Tick-based `age / lifetime`, clamped to `1`; normalized age, not seconds. Rendered curves interpolate between ticks. | Number `0–1` |
| `getX()` | Current X position in the particle's coordinate space. | Number |
| `getY()` | Current Y position in the particle's coordinate space. | Number |
| `getZ()` | Current Z position in the particle's coordinate space. | Number |
| `isAlive()` | Its lifetime/kill flag; becomes `false` on expiry or `kill()`. | Boolean |
| `isMainHand()` | Born from the main inventory hand. | Boolean |
| `isOffHand()` | Born from the off inventory hand. | Boolean |
| `kill()` | Mark it dead immediately so it stops drawing; the simulation removes it and invokes its death task if configured. | `nothing` |
| `setPosition(x,y,z)` | Three finite numbers; replace this particle's current position. | `nothing` |
| `setVelocity(x,y,z)` | Three finite numbers; replace its current velocity, in units per tick. | `nothing` |
| `setSize(size)` | Finite number; replace its size curve with a constant. Negative values become `0`; rendered size is capped at `100`. | `nothing` |
| `setOpacity(alpha)` | Finite number; clamp to `0–1` and replace its opacity curve with a constant. | `nothing` |
| `setRotation(degrees)` | Finite number; replace its rotation curve with a constant roll. | `nothing` |
| `setColor(r,g,b)` | Three integers; clamp each to `0–255` and replace its color curves with constants. | `nothing` |

Attached handles use model coordinates; detached handles use view coordinates. Callback setters affect only this particle, without changing the emitter or other particles. The individual visual setters **clamp** out-of-range sizes/opacity/RGB; emitter visual setters **reject** values outside their documented ranges. Individual particles have no lifetime/texture/gravity/keyframe setters in this API.

`kill()` does not invoke the death task synchronously; that happens during simulation cleanup. Clearing discards particles without changing a retained handle's lifetime flag, so `isAlive()` is not a test of whether the system still tracks that handle. Use handles for per-particle callback logic; retaining or modifying a discarded/dead handle does not respawn it.

## Player settings and debug controls

IS → **First-person particles** offers global enable, density (0–200%, default 100%), a shared both-hand live cap (0–4096, default 512), live counters and emitter-origin markers. Density scales rates and bursts; fractional emissions carry forward. At the cap, new births are dropped. Lowering the cap trims existing particles. Markers use green for the logical main hand and orange for the off hand; camera-facing billboards make them readable on either physical arm.

## Global particle functions

These functions act across every script and both hands.

| Function | Meaning | Returns |
| --- | --- | --- |
| `particles.getCount()` | Total currently tracked particles. Hidden particles can remain counted; killed handles can remain counted until cleanup. | Integer |
| `particles.getEmitterCount()` | Registered emitter objects, including stopped or currently unbound ones. | Integer |
| `particles.getDroppedCount()` | Accumulated requested births rejected by the shared live cap; resets with the session. Does not count every reason an emission request may be reduced or discarded. | Integer |
| `particles.clear()` | Discard particles and pending/fractional requests in all scripts. Keep emitters, their enabled state and drop counter. Does not invoke death tasks. | `nothing` |

`particles.clear()` allows active continuous emitters to resume on subsequent ticks. Use `sparks.stop()` when you want to stop one emitter permanently until `start()`.

## Reading player settings

| Function | Meaning | Returns |
| --- | --- | --- |
| `config.isParticlesEnabled()` | Whether the player has enabled particles globally. Does not guarantee an effect is currently bound or visible. Default `true`. | Boolean |
| `config.getParticleDensity()` | User density **percentage**, `0–200`; default `100`. Divide by `100` if you need a multiplier. | Integer |
| `config.getParticleLimit()` | Shared live cap, `0–4096`; default `512`. | Integer |

These getters do not change settings. Emission already applies density and the shared cap; scripts should not multiply their rate by density again.

## Building and checking

Use the normal shared project commands:

```sh
./gradlew build26_2     # Headless regression checks and release separation guard
./gradlew test26_2      # Real Minecraft first-person demos/settings/reload checks
./gradlew buildAll     # Compile and verify all eight targets
./gradlew releaseAll   # Collect upload JARs and checksums
```

`src/gametest` remains a separate test mod. Ordinary build/release commands do not compile or package it. Native tests include custom test-only texture frames and screenshots of both hands, glyphs, both activated sensors and overlay particles. See [MULTIVERSION_VALIDATION.md](MULTIVERSION_VALIDATION.md) for measured results.
