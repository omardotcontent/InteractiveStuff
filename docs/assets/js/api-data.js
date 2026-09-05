/* ==========================================================================
   InteractiveStuff — Vyn API reference data
   Source of truth: https://github.com/omardotcontent/InteractiveStuff/wiki
   Consumed by api.html (see api.js). Plain data, no dependencies.
   ========================================================================== */
(function () {
  "use strict";

  /* Shorthand builders ---------------------------------------------------- */
  function E(s, p, r, d, e) {
    return { s: s, p: p || [], r: r || "", d: d || "", e: e || "" };
  }
  function P(n, t, d) { return { n: n, t: t, d: d || "" }; }
  function G(title, items) { return { title: title, items: items }; }

  var types = [];

  /* =========================== PLAYER ==================================== */
  types.push({
    id: "player",
    name: "Player",
    varName: "player",
    blurb: "Represents the local player. Provides access to position, health, held items, movement state, world interaction, and sound playback.",
    note: "The built-in resource pack spells some accessors with a capital M — player.getGameMode() works exactly like player.getGamemode().",
    groups: [
      G("World & Blocks", [
        E("getSteppingBlock()", [], "Block", "Returns the block the player is currently standing on.",
          "make block player.getSteppingBlock()\nsay block.getName()"),
        E("getNearbyBlocks(blockRadius)", [P("blockRadius", "Integer", "Radius in blocks")], "List<Block>",
          "Returns all non-air blocks within a cubic radius around the player.",
          "make blocks player.getNearbyBlocks(3)\nsay size(blocks)"),
        E("getTargetBlock()", [], "Block",
          "Returns the block the player is currently looking at, or nothing if not targeting a block.",
          "make target player.getTargetBlock()\ncheck target != nothing do\n    say target.getName()\nend"),
        E("getWorld()", [], "World", "Returns the current world the player is in.",
          "make world player.getWorld()\nsay world.getDimension()")
      ]),
      G("Position & Movement", [
        E("getPosition()", [], "Position", "Returns the player's current block position.",
          "make pos player.getPosition()\nsay pos.toString()"),
        E("getVelocityX()", [], "Double",
          "Returns the player's velocity along their local forward/backward axis (relative to yaw)."),
        E("getVelocityY()", [], "Double",
          "Returns the player's vertical velocity. Returns 0.0 during standard gravity fall."),
        E("getVelocityZ()", [], "Double",
          "Returns the player's velocity along their local left/right axis (relative to yaw)."),
        E("getCameraPitchVelocity()", [], "Float",
          "Returns the rate of change of the camera's pitch (vertical look speed)."),
        E("getCameraYawVelocity()", [], "Float",
          "Returns the rate of change of the camera's yaw (horizontal look speed).",
          "make item player.getMainHandItem()\nitem.rotateX(player.getCameraPitchVelocity() * 2)\nitem.rotateY(player.getCameraYawVelocity() * 2)"),
        E("getEyePosX()", [], "Double", "Returns the X coordinate of the player's eye position."),
        E("getEyePosY()", [], "Double", "Returns the Y coordinate of the player's eye position."),
        E("getEyePosZ()", [], "Double", "Returns the Z coordinate of the player's eye position."),
        E("getHeadYaw()", [], "Double", "Returns the player's current head yaw (horizontal rotation in degrees)."),
        E("getDamageTiltYaw()", [], "Double", "Returns the yaw direction of the most recent damage tilt effect."),
        E("getStepHeight()", [], "Double",
          "Returns the player's step height (how tall a block they can step up without jumping).")
      ]),
      G("Stats", [
        E("getHealth()", [], "Double", "Returns the player's current health."),
        E("getFoodLevel()", [], "Double", "Returns the player's current food/hunger level (0–20)."),
        E("getSaturationLevel()", [], "Double", "Returns the player's current saturation level."),
        E("getNauseaIntensity()", [], "Float", "Returns the current nausea effect intensity."),
        E("getExperienceLevel()", [], "Integer", "Returns the player's current experience level."),
        E("getExperienceProgress()", [], "Double", "Returns progress toward the next experience level (0.0–1.0)."),
        E("getMoodPercentage()", [], "Float", "Returns the player's current mood percentage (used for cave ambience sounds)."),
        E("getPermissionLevel()", [], "Integer", "Returns the player's operator permission level (0–4)."),
        E("getMountJumpStrength()", [], "Float", "Returns the jump strength of the player's current mount."),
        E("getGamemode()", [], "String", "Returns the player's gamemode as a string, e.g. \"survival\", \"creative\". Also available as getGameMode().",
          "debugText(\"Gamemode: \" + player.getGamemode())"),
        E("getArmor()", [], "Integer", "Returns the player's current total armor value."),
        E("getItemUseTime()", [], "Integer", "Returns how many ticks the player has been using their current item."),
        E("getItemUseTimeLeft()", [], "Integer", "Returns how many ticks remain before the player finishes using their current item."),
        E("getLuck()", [], "Float", "Returns the player's current luck attribute value."),
        E("getAbsorptionAmount()", [], "Float", "Returns the player's current absorption (bonus health) amount."),
        E("getMaxAbsorption()", [], "Float", "Returns the player's maximum absorption amount."),
        E("getStuckArrowCount()", [], "Integer", "Returns the number of arrows currently stuck in the player."),
        E("getStingerCount()", [], "Integer", "Returns the number of bee stingers currently stuck in the player."),
        E("getScaleFactor()", [], "Float", "Returns the player's scale factor (used for size-affecting effects)."),
        E("getScale()", [], "Float", "Returns the player's current scale.")
      ]),
      G("Held Items", [
        E("getActiveItem()", [], "ItemModel", "Returns the item currently being used (e.g. while eating or drawing a bow)."),
        E("getMainHandItem()", [], "ItemModel", "Returns the item in the player's main hand."),
        E("getOffHandItem()", [], "ItemModel", "Returns the item in the player's off hand."),
        E("getBlockingItem()", [], "ItemModel", "Returns the item currently being used to block (e.g. a shield being held up).",
          "make blocking player.getBlockingItem()\ncheck blocking != nothing do\n    debugText(\"Blocking with: \" + blocking.getName())\nend"),
        E("getWeaponItem()", [], "ItemModel", "Returns the item considered the player's active weapon."),
        E("getPickBlockItem()", [], "ItemModel", "Returns the item that would be picked when using pick-block (middle-click)."),
        E("isMainHand(item)", [P("item", "ItemModel")], "Boolean", "Returns whether the given ItemModel is in the main hand."),
        E("isOffHand(item)", [P("item", "ItemModel")], "Boolean", "Returns whether the given ItemModel is in the off hand."),
        E("isHoldingItem(itemId)", [P("itemId", "String")], "Boolean",
          "Returns whether the player is holding an item with the given ID in either hand.",
          "check player.isHoldingItem(\"minecraft:diamond_sword\") do\n    make item player.getMainHandItem()\n    item.setColor(100, 180, 255)\nend")
      ]),
      G("Movement State", [
        E("isSwimming()", [], "Boolean", "Returns whether the player is swimming."),
        E("isSprinting()", [], "Boolean", "Returns whether the player is sprinting."),
        E("isSneaking()", [], "Boolean", "Returns whether the player is sneaking."),
        E("isInSneakingPose()", [], "Boolean", "Returns whether the player is in a sneaking pose (visual)."),
        E("isCrawling()", [], "Boolean", "Returns whether the player is crawling."),
        E("isClimbing()", [], "Boolean", "Returns whether the player is climbing (e.g. ladder or vine)."),
        E("isDescending()", [], "Boolean", "Returns whether the player is descending (e.g. on a ladder)."),
        E("isHoldingOntoLadder()", [], "Boolean", "Returns whether the player is holding onto a ladder."),
        E("isOnGround()", [], "Boolean", "Returns whether the player is on the ground."),
        E("isOnRail()", [], "Boolean", "Returns whether the player is on a rail."),
        E("isJumping()", [], "Boolean", "Returns whether the player is currently jumping."),
        E("isRiding()", [], "Boolean", "Returns whether the player is riding an entity."),
        E("isRidingJumpable()", [], "Boolean", "Returns whether the player is riding a mount that can jump."),
        E("isFlyingVehicle()", [], "Boolean", "Returns whether the player is in a flying vehicle."),
        E("shouldSlowDown()", [], "Boolean", "Returns whether movement should be slowed (e.g. in a cobweb or powder snow)."),
        E("isHorizontalCollision()", [], "Boolean", "Returns whether the player is colliding with a block horizontally."),
        E("isAutoJumpEnabled()", [], "Boolean", "Returns whether auto-jump is enabled."),
        E("isFirstPerson()", [], "Boolean", "Returns whether the player is currently in first-person perspective. Added in 0.7.0-SNAPSHOT-2.",
          "check player.isFirstPerson() do\n    make item player.getMainHandItem()\n    item.translateY(-0.05)\nend")
      ]),
      G("Player Status", [
        E("isInLava()", [], "Boolean", "Returns whether the player is in lava."),
        E("isInFluid()", [], "Boolean", "Returns whether the player is in any fluid."),
        E("isTouchingWater()", [], "Boolean", "Returns whether the player is touching water."),
        E("isSubmergedInWater()", [], "Boolean", "Returns whether the player is fully submerged in water."),
        E("isOnFire()", [], "Boolean", "Returns whether the player is on fire."),
        E("isFireImmune()", [], "Boolean", "Returns whether the player is immune to fire damage."),
        E("isFrozen()", [], "Boolean", "Returns whether the player is frozen (in powder snow)."),
        E("isInsideWall()", [], "Boolean", "Returns whether the player is clipping inside a block."),
        E("isAtCloudHeight()", [], "Boolean", "Returns whether the player is at cloud height."),
        E("isBlocking()", [], "Boolean", "Returns whether the player is blocking with a shield."),
        E("isUsingItem()", [], "Boolean", "Returns whether the player is currently using an item."),
        E("isUsingRiptide()", [], "Boolean", "Returns whether the player is currently using a riptide trident."),
        E("isUsingSpyglass()", [], "Boolean", "Returns whether the player is currently using a spyglass."),
        E("isGlowing()", [], "Boolean", "Returns whether the player has the glowing effect (server-side)."),
        E("isGlowingLocal()", [], "Boolean", "Returns whether the player is glowing locally (client-side)."),
        E("isPushable()", [], "Boolean", "Returns whether the player can be pushed by other entities."),
        E("isPushedByFluids()", [], "Boolean", "Returns whether the player is pushed by fluid currents."),
        E("isInvulnerable()", [], "Boolean", "Returns whether the player is currently invulnerable."),
        E("isCamera()", [], "Boolean", "Returns whether the player is the current camera entity."),
        E("isMainPlayer()", [], "Boolean", "Returns whether this is the main/local player."),
        E("showsDeathScreen()", [], "Boolean", "Returns whether the player would show the death screen upon dying."),
        E("isLimitedCraftingEnabled()", [], "Boolean", "Returns whether limited crafting is enabled for this player."),
        E("isDead()", [], "Boolean", "Returns whether the player is dead."),
        E("isBaby()", [], "Boolean", "Returns whether the player is in baby form."),
        E("canBreatheInWater()", [], "Boolean", "Returns whether the player can breathe underwater (helmet enchantment or effect)."),
        E("hasLandedInFluid()", [], "Boolean", "Returns whether the player has just landed in a fluid."),
        E("canTakeDamage()", [], "Boolean", "Returns whether the player is currently able to take damage."),
        E("isPartOfGame()", [], "Boolean", "Returns whether the player entity is considered an active part of the game world."),
        E("shouldSwimInFluids()", [], "Boolean", "Returns whether the player should swim when in fluids."),
        E("hasNoDrag()", [], "Boolean", "Returns whether the player has no drag applied (no velocity dampening from fluids or air).")
      ]),
      G("Sound", [
        E("playSound(soundId, volume, pitch)",
          [P("soundId", "String", "Namespaced sound ID"), P("volume", "Double"), P("pitch", "Double")], "",
          "Plays a sound at the player's position."),
        E("playSound(sound)", [P("sound", "Sound")], "", "Plays a Sound object at the player's position."),
        E("playSoundWorld(position, soundId, volume, pitch)",
          [P("position", "Position"), P("soundId", "String"), P("volume", "Double"), P("pitch", "Double")], "",
          "Plays a sound at a specific world position."),
        E("playSoundWorld(position, sound)", [P("position", "Position"), P("sound", "Sound")], "",
          "Plays a Sound object at a specific world position.",
          "~ Play a simple sound on the player\nplayer.playSound(\"minecraft:entity.experience_orb.pickup\", 1.0, 1.0)\n\n~ Play using a Sound object\nmake pos new Position(0, 64, 0)\nmake snd new Sound(\"minecraft:block.note_block.harp\", 1.0, 1.2, pos)\nplayer.playSound(snd)\n\n~ Play at a world position\nplayer.playSoundWorld(player.getPosition(), \"minecraft:ambient.cave\", 0.5, 1.0)")
      ])
    ]
  });

  /* =========================== ITEM MODEL ================================ */
  types.push({
    id: "itemmodel",
    name: "ItemModel",
    varName: "itemRendered / item",
    blurb: "Represents a renderable item model. Supports full transform control (translation, rotation, scale, shear), color/opacity, lighting, glint, tinting, data components, and parent-child hierarchies.",
    ctor: {
      s: "ItemModel(itemId)",
      p: [P("itemId", "String", "The item's namespaced ID, e.g. \"minecraft:diamond_sword\"")],
      d: "Creates a new ItemModel from an item ID and registers it for rendering.",
      e: "make sword new ItemModel(\"minecraft:diamond_sword\")"
    },
    groups: [
      G("Item Info", [
        E("getName()", [], "String", "Returns the registry name of the item.",
          "make item player.getMainHandItem()\nsay item.getName()"),
        E("isDamaged()", [], "Boolean", "Returns whether the item is currently damaged."),
        E("isDamageable()", [], "Boolean", "Returns whether the item can take damage."),
        E("isEnchantable()", [], "Boolean", "Returns whether the item can be enchanted."),
        E("isStackable()", [], "Boolean", "Returns whether the item can stack."),
        E("getCount()", [], "Integer", "Returns the current stack count."),
        E("getMaxCount()", [], "Integer", "Returns the maximum stack size."),
        E("getBobbingAnimationTime()", [], "Integer", "Returns the bobbing animation time of the item.")
      ]),
      G("Rendering State", [
        E("isExclusive()", [], "Boolean", "Returns whether this model is in exclusive (quad-selection) mode."),
        E("getLight()", [], "Integer", "Returns the current custom light value, or -1 if not set. Inherits from parent if unset."),
        E("getGlint()", [], "Integer", "Returns the current glint override value, or -1 if not set. Inherits from parent if unset."),
        E("isMainHand()", [], "Boolean", "Returns whether the item is being rendered in the main hand (first or third person)."),
        E("isOffHand()", [], "Boolean", "Returns whether the item is being rendered in the off hand (first or third person)."),
        E("isFirstPerson()", [], "Boolean", "Returns whether the item is being rendered in first person.",
          "make item player.getMainHandItem()\ncheck item.isFirstPerson() do\n    item.translate(0, 0.1, 0)\nend"),
        E("getDisplayContextName()", [], "String",
          "Returns the name of the current display context, e.g. \"FIRST_PERSON_RIGHT_HAND\".")
      ]),
      G("Data Components", [
        E("setItemModel(model)", [P("model", "String", "The model ID to apply")], "",
          "Sets the minecraft:item_model data component. This is how the built-in pack swaps chest lids, pistons, saplings and more.",
          "make item player.getMainHandItem()\nitem.setItemModel(\"mypack:custom_sword\")"),
        E("setBobbingTime(bobbingTime)", [P("bobbingTime", "Integer")], "", "Sets the bobbing animation time."),
        E("setDataComponent(key, value)", [P("key", "String", "Component key"), P("value", "Object", "Converted to string internally")], "",
          "Sets an arbitrary data component by key."),
        E("getDataComponent(key)", [P("key", "String")], "String", "Returns the value of a data component as a string."),
        E("hasDataComponent(key)", [P("key", "String")], "Boolean", "Returns whether the item has the specified data component."),
        E("getDataComponentIds()", [], "List<String>", "Returns a list of all data component keys on this item."),
        E("getDataComponents()", [], "List<Map<String, String>>", "Returns all data components as a list of key-value maps."),
        E("removeDataComponent(key)", [P("key", "String")], "", "Removes a data component by key.",
          "make item player.getMainHandItem()\ncheck item.hasDataComponent(\"minecraft:custom_name\") do\n    say item.getDataComponent(\"minecraft:custom_name\")\nend\nitem.setDataComponent(\"minecraft:custom_name\", \"My Sword\")\nitem.removeDataComponent(\"minecraft:enchantments\")")
      ]),
      G("Color & Appearance", [
        E("setColor(r, g, b)", [P("r", "Integer", "0–255"), P("g", "Integer", "0–255"), P("b", "Integer", "0–255")], "",
          "Sets the render color using RGB values."),
        E("setOpacity(opacity)", [P("opacity", "Double", "0.0 transparent – 1.0 opaque")], "",
          "Sets the opacity/alpha."),
        E("setLight(light)", [P("light", "Integer", "-1 to 15; -1 disables")], "",
          "Sets a custom light level override. The built-in pack uses 15 for lit torches and campfires."),
        E("setGlint(glint)", [P("glint", "Integer", "-1 default, 0 none, 1–2 glint modes")], "",
          "Sets the glint override."),
        E("setTint(index, color)", [P("index", "Integer", "Tint index"), P("color", "Integer", "ARGB color value")], "",
          "Sets a tint color for a specific quad tint index. Used for biome-tinted sapling leaves and bucket water."),
        E("setTint(color)", [P("color", "Integer", "ARGB color value")], "", "Sets a global tint color (applies to index -1)."),
        E("setQuadColor(color)", [P("color", "Integer", "ARGB color value")], "", "Sets a selection/quad override color."),
        E("setColor / setOpacity / setLight / setGlint together", [], "", "Example combining the appearance setters.",
          "make item player.getMainHandItem()\nitem.setColor(255, 80, 0)\nitem.setOpacity(0.8)\nitem.setLight(15)\nitem.setGlint(0)")
      ]),
      G("Quad Selection", [
        E("setQuads(start, end)", [P("start", "Integer"), P("end", "Integer")], "",
          "Defines the range of quads to render."),
        E("select(quadStart, quadEnd)", [P("quadStart", "Integer"), P("quadEnd", "Integer")], "",
          "Same as setQuads, but also enables exclusive mode (only selected quads are rendered).",
          "make item player.getMainHandItem()\n~ Only render quads 0 through 3\nitem.select(0, 3)")
      ]),
      G("Transforms", [
        E("translate(dx, dy, dz)", [P("dx", "Double"), P("dy", "Double"), P("dz", "Double")], "",
          "Moves the model by the given offset."),
        E("translateX(dx)", [P("dx", "Double")], "", "Moves the model along the X axis."),
        E("translateY(dy)", [P("dy", "Double")], "", "Moves the model along the Y axis."),
        E("translateZ(dz)", [P("dz", "Double")], "", "Moves the model along the Z axis."),
        E("rotate(dx, dy, dz)", [P("dx", "Double"), P("dy", "Double"), P("dz", "Double")], "",
          "Rotates the model around all three axes (in degrees)."),
        E("rotateAxis(angle, axisX, axisY, axisZ)",
          [P("angle", "Double", "Degrees"), P("axisX", "Double"), P("axisY", "Double"), P("axisZ", "Double")], "",
          "Rotates the model by angle degrees around an arbitrary axis vector (normalized internally)."),
        E("rotateX(angle)", [P("angle", "Double")], "", "Rotates the model around the X axis (degrees)."),
        E("rotateY(angle)", [P("angle", "Double")], "", "Rotates the model around the Y axis (degrees)."),
        E("rotateZ(angle)", [P("angle", "Double")], "", "Rotates the model around the Z axis (degrees)."),
        E("scale(sx, sy, sz)", [P("sx", "Double"), P("sy", "Double"), P("sz", "Double")], "",
          "Multiplies the model's scale on all axes."),
        E("scaleX(sx)", [P("sx", "Double")], "", "Scales the model along the X axis."),
        E("scaleY(sy)", [P("sy", "Double")], "", "Scales the model along the Y axis."),
        E("scaleZ(sz)", [P("sz", "Double")], "", "Scales the model along the Z axis."),
        E("shear(xy, xz, yx, yz, zx, zy)",
          [P("xy", "Double"), P("xz", "Double"), P("yx", "Double"), P("yz", "Double"), P("zx", "Double"), P("zy", "Double")], "",
          "Applies a shear transformation using the six shear components — great for organic squash-and-stretch."),
        E("setPivot(x, y, z)", [P("x", "Double"), P("y", "Double"), P("z", "Double")], "",
          "Sets the pivot point for rotation and scale. Default is 0.5, 0.5, 0.5.",
          "make item player.getMainHandItem()\n\n~ Smooth floating animation\nmake targetY 0.1\nmake currentY item.smooth(0, targetY, 0.1)\nitem.translateY(currentY)\n\n~ Spin on Y axis\nitem.rotateY(45 * getDelta())\n\n~ Scale up slightly\nitem.scale(1.2, 1.2, 1.2)\n\n~ Rotate around a custom axis\nitem.rotateAxis(90, 0, 1, 0)")
      ]),
      G("Hierarchy", [
        E("setParent(parent)", [P("parent", "ItemModel")], "",
          "Sets another ItemModel as the parent. Transforms, light, glint, and tints are inherited. Circular dependencies are automatically rejected."),
        E("detach()", [], "", "Removes the parent reference from this model."),
        E("hasParent()", [], "Boolean", "Returns whether this model currently has a parent.",
          "make base player.getMainHandItem()\nmake overlay base.copy()\n\n~ overlay inherits transforms from base\noverlay.setParent(base)\noverlay.setItemModel(\"mypack:overlay_model\")\n\n~ Later, detach if needed\noverlay.detach()")
      ]),
      G("Utility", [
        E("smooth(current, target, speed)",
          [P("current", "Double"), P("target", "Double"), P("speed", "Double", "Interpolation speed 0.0–1.0")], "Double",
          "Returns a framerate-independent interpolated value between current and target."),
        E("copy()", [], "ItemModel",
          "Creates a copy of this model with the same item stack and display context, and registers it for rendering.",
          "~ Smooth bob animation using delta time\nmake item player.getMainHandItem()\nmake bobY item.smooth(0, 0.05, 0.08 * getDelta())\nitem.translateY(bobY)")
      ])
    ]
  });

  /* =========================== WORLD ===================================== */
  types.push({
    id: "world",
    name: "World",
    varName: "world",
    blurb: "Represents the current Minecraft world. Provides access to blocks, biomes, time, dimension, and color data.",
    groups: [
      G("Blocks", [
        E("getBlock(x, y, z)", [P("x", "Integer"), P("y", "Integer"), P("z", "Integer")], "Block",
          "Returns the block at the given world coordinates.",
          "make world player.getWorld()\nmake block world.getBlock(0, 64, 0)\nsay block.getName()")
      ]),
      G("Dimension & Time", [
        E("getDimension()", [], "String",
          "Returns the identifier of the current dimension, e.g. \"minecraft:overworld\"."),
        E("isDay()", [], "Boolean", "Returns whether it is currently daytime."),
        E("getTimeOfDay()", [], "Long", "Returns the time of day in ticks (0–24000)."),
        E("getTime()", [], "Long", "Returns the total world time elapsed in ticks (ever-increasing).",
          "make world player.getWorld()\nsay world.getDimension()\n\ncheck world.isDay() do\n    say \"It's daytime! Time: \" + world.getTimeOfDay()\notherwise\n    say \"It's nighttime!\"\nend")
      ]),
      G("Biomes & Colors", [
        E("getBiomeAt(x, y, z)", [P("x", "Integer"), P("y", "Integer"), P("z", "Integer")], "String",
          "Returns the biome ID at the given coordinates, e.g. \"minecraft:plains\"."),
        E("getBiomeAt(position)", [P("position", "Position")], "String", "Returns the biome ID at the given Position."),
        E("getBiomeColorAt(x, y, z)", [P("x", "Integer"), P("y", "Integer"), P("z", "Integer")], "Integer",
          "Returns the biome color as a packed ARGB integer."),
        E("getBiomeColorAt(position)", [P("position", "Position")], "Integer", "Returns the biome color as a packed ARGB integer."),
        E("getGrassColor(position)", [P("position", "Position")], "Integer",
          "Returns the grass color at the given position, blended for the local biome."),
        E("getFoliageColor(position)", [P("position", "Position")], "Integer",
          "Returns the foliage (leaf) color at the given position."),
        E("getDryFoliageColor(position)", [P("position", "Position")], "Integer",
          "Returns the dry foliage color at the given position."),
        E("getWaterColor(position)", [P("position", "Position")], "Integer",
          "Returns the water color at the given position.",
          "make world player.getWorld()\nmake pos player.getPosition()\n\nsay world.getBiomeAt(pos)\n~ Output: \"minecraft:forest\"\n\nmake grassColor world.getGrassColor(pos)\nmake waterColor world.getWaterColor(pos)\n\nmake item player.getMainHandItem()\nitem.setTint(grassColor)")
      ]),
      G("Utility", [
        E("calculateDistanceBetweenPositions(pos1, pos2)", [P("pos1", "Position"), P("pos2", "Position")], "Long",
          "Returns the distance between two Position objects."),
        E("toString()", [], "String",
          "Returns a string summary of the world state, e.g. \"World{dimension=minecraft:overworld, isDay=true, timeOfDay=6000, time=142300}\".",
          "make world player.getWorld()\nmake pos1 player.getPosition()\nmake pos2 new Position(0, 64, 0)\nmake dist world.calculateDistanceBetweenPositions(pos1, pos2)\nsay \"Distance to origin: \" + dist\n\nsay world.toString()")
      ])
    ]
  });

  /* =========================== BLOCK ===================================== */
  types.push({
    id: "block",
    name: "Block",
    varName: "block",
    blurb: "Represents a block in the world. Provides information about its state, position, light levels, and properties.",
    groups: [
      G("Functions", [
        E("getName()", [], "String",
          "Returns the item/block name as a string, e.g. \"minecraft:stone\".",
          "make block player.getSteppingBlock()\nsay block.getName()\n~ Output: \"minecraft:stone\""),
        E("hasBlockTag(tagID)", [P("tagID", "String", "e.g. \"minecraft:logs\"")], "Boolean",
          "Checks whether the block has a tag matching the given string (case-insensitive, partial match).",
          "make block player.getSteppingBlock()\ncheck block.hasBlockTag(\"minecraft:logs\") do\n    say \"Standing on a log!\"\nend"),
        E("getPosition()", [], "Position", "Returns the block's world position."),
        E("getBlockLightLevel()", [], "Integer", "Returns the block light level at this block's position (0–15)."),
        E("getSkyLightLevel()", [], "Integer", "Returns the sky light level at this block's position (0–15)."),
        E("getInstrument()", [], "String",
          "Returns the note block instrument sound ID associated with this block, e.g. \"block.note_block.harp\"."),
        E("isSolid()", [], "Boolean", "Returns whether this block is a solid block."),
        E("isAir()", [], "Boolean", "Returns whether this block is air."),
        E("isBurnable()", [], "Boolean", "Returns whether this block can catch fire."),
        E("isTransparent()", [], "Boolean", "Returns whether this block is transparent."),
        E("isOpaque()", [], "Boolean", "Returns whether this block is opaque."),
        E("isOpaqueFullCube()", [], "Boolean", "Returns whether this block is both opaque and a full cube."),
        E("toString()", [], "String",
          "Returns the full block state as a string, including block properties, e.g. \"Block{minecraft:oak_log, axis=y}\".",
          "make block player.getSteppingBlock()\ncheck block.isSolid() do\n    say \"Solid block\"\nend\ncheck block.isAir() do\n    say \"Air block\"\nend\ncheck block.isBurnable() do\n    say \"This block can burn\"\nend")
      ])
    ]
  });

  /* =========================== POSITION ================================== */
  types.push({
    id: "position",
    name: "Position",
    varName: "pos",
    blurb: "Represents a block position in the world using integer X, Y, Z coordinates.",
    ctor: {
      s: "Position(x, y, z)",
      p: [P("x", "Integer"), P("y", "Integer"), P("z", "Integer")],
      d: "Creates a new Position with the given coordinates.",
      e: "make pos new Position(100, 64, -200)\nsay pos.toString()\n~ Output: \"Position{x=100, y=64, z=-200}\""
    },
    groups: [
      G("Functions", [
        E("getX()", [], "Integer", "Returns the X coordinate."),
        E("getY()", [], "Integer", "Returns the Y coordinate."),
        E("getZ()", [], "Integer", "Returns the Z coordinate."),
        E("setX(x)", [P("x", "Integer")], "", "Sets the X coordinate."),
        E("setY(y)", [P("y", "Integer")], "", "Sets the Y coordinate."),
        E("setZ(z)", [P("z", "Integer")], "", "Sets the Z coordinate.",
          "make pos player.getPosition()\nsay pos.getX()\nsay pos.getY()\nsay pos.getZ()\n\npos.setY(100)\nsay pos.toString()"),
        E("getDistanceTo(other)", [P("other", "Position")], "Long", "Returns the distance to another Position."),
        E("getDistanceTo(x, y, z)", [P("x", "Integer"), P("y", "Integer"), P("z", "Integer")], "Long",
          "Returns the distance to a position given as raw coordinates.",
          "make playerPos player.getPosition()\nmake spawnPos new Position(0, 64, 0)\nmake dist playerPos.getDistanceTo(spawnPos)\nsay \"Distance from spawn: \" + dist"),
        E("toString()", [], "String", "Returns a string representation, e.g. \"Position{x=10, y=64, z=-30}\".")
      ])
    ]
  });

  /* =========================== SOUND ===================================== */
  types.push({
    id: "sound",
    name: "Sound",
    varName: "snd",
    blurb: "Represents a sound with a namespaced ID, volume, pitch, and optional world position.",
    ctor: {
      s: "Sound(soundId, volume, pitch, position)",
      p: [P("soundId", "String"), P("volume", "Double"), P("pitch", "Double"), P("position", "Position")],
      d: "Creates a new Sound object.",
      e: "make pos new Position(0, 64, 0)\nmake snd new Sound(\"minecraft:block.note_block.harp\", 1.0, 1.0, pos)\nplayer.playSound(snd)"
    },
    groups: [
      G("Functions", [
        E("getName()", [], "String", "Returns the namespaced sound ID."),
        E("getVolume()", [], "Double", "Returns the volume of the sound."),
        E("getPitch()", [], "Double", "Returns the pitch of the sound."),
        E("getPosition()", [], "Position", "Returns the world position this sound is associated with."),
        E("setSoundId(soundId)", [P("soundId", "String")], "", "Sets the namespaced sound ID."),
        E("setVolume(volume)", [P("volume", "Double")], "", "Sets the volume."),
        E("setPitch(pitch)", [P("pitch", "Double")], "", "Sets the pitch."),
        E("setPosition(position)", [P("position", "Position")], "", "Sets the world position.",
          "make pos new Position(0, 64, 0)\nmake snd new Sound(\"minecraft:entity.player.levelup\", 1.0, 1.0, pos)\n\n~ Modify after creation\nsnd.setPitch(1.5)\nsnd.setVolume(0.5)\nsnd.setSoundId(\"minecraft:block.note_block.bell\")\n\nplayer.playSound(snd)"),
        E("toString()", [], "String",
          "Returns a string representation of this sound.",
          "make pos new Position(0, 64, 0)\nmake snd new Sound(\"minecraft:ambient.cave\", 0.8, 1.0, pos)\nsay snd.toString()")
      ])
    ]
  });

  /* =========================== MOD LOADER ================================ */
  types.push({
    id: "modloader",
    name: "ModLoader",
    varName: "modLoader",
    blurb: "Provides information about the current mod and resource pack environment. Indispensable for compatibility guards.",
    groups: [
      G("Functions", [
        E("isModLoaded(modid)", [P("modid", "String", "The mod ID, e.g. \"sodium\"")], "Boolean",
          "Returns whether a mod with the given mod ID is currently loaded.",
          "check modLoader.isModLoaded(\"sodium\") do\n    say \"Sodium is loaded!\"\nend"),
        E("isResourcePackLoaded(packName)", [P("packName", "String", "The display name of the resource pack")], "Boolean",
          "Returns whether a resource pack with the given name is currently active (case-insensitive).",
          "check modLoader.isResourcePackLoaded(\"MyCoolPack\") do\n    importScript(\"mypack\", \"effects\")\nend"),
        E("getRawGameVersion()", [], "String",
          "Returns the raw Minecraft game version string, e.g. \"1.21.10\".")
      ])
    ]
  });

  /* =========================== CONFIG ==================================== */
  types.push({
    id: "config",
    name: "InteractiveStuffConfig",
    varName: "config",
    blurb: "Provides read access to the current configuration values of the InteractiveStuff mod at runtime. Use these to respect the player's settings in your own scripts.",
    groups: [
      G("Functions", [
        E("isResourcePackDebugModeEnabled()", [], "Boolean",
          "Returns whether resource pack debug mode is currently enabled. debugText() only renders when this is on.",
          "check config.isResourcePackDebugModeEnabled() do\n    debugText(\"Debug mode is ON\")\nend"),
        E("isInteractiveHitsEnabled()", [], "Boolean", "Returns whether the interactive hits feature is enabled."),
        E("isSculkSensorFeatureEnabled()", [], "Boolean", "Returns whether the sculk sensor feature is enabled."),
        E("getHitCooldownTicks()", [], "Integer",
          "Returns the cooldown duration in ticks between hits (20 ticks = 1 second).",
          "say config.getHitCooldownTicks()\n~ Output: 10  (= 0.5 seconds)"),
        E("isTextureChangesEnabled()", [], "Boolean", "Returns whether dynamic texture changes are enabled."),
        E("isNoteBlockCrouchFeatureEnabled()", [], "Boolean", "Returns whether the note block crouch feature is enabled."),
        E("getCooldownTicks()", [], "Integer", "Returns the hit cooldown in ticks. Alias of getHitCooldownTicks()."),
        E("isSpecializedNoteblockHitsEnabled()", [], "Boolean", "Returns whether specialized note block hits are enabled."),
        E("isResourcePackMatrixEditingEnabled()", [], "Boolean", "Returns whether resource pack matrix editing is enabled."),
        E("isResourcePackColorChangingEnabled()", [], "Boolean", "Returns whether resource pack color changing is enabled.",
          "~ Guard all matrix/color logic behind config checks\ncheck config.isResourcePackMatrixEditingEnabled() do\n    item.translate(0, 0.1, 0)\nend\ncheck config.isResourcePackColorChangingEnabled() do\n    item.setColor(255, 100, 0)\nend")
      ])
    ]
  });

  /* =========================== KEY ======================================= */
  types.push({
    id: "key",
    name: "Key",
    varName: "keyHelper",
    blurb: "Provides utility functions for working with Minecraft translation keys.",
    groups: [
      G("Functions", [
        E("getTranslatedKey(key)", [P("key", "String", "A Minecraft translation key")], "String",
          "Returns the translated display text for a given translation key in the current game language.",
          "make keyHelper new Key()\nmake name keyHelper.getTranslatedKey(\"item.minecraft.diamond_sword\")\nsay name\n~ Output: \"Diamond Sword\"  (in English)")
      ])
    ]
  });

  /* =========================== ISP ======================================= */
  types.push({
    id: "isp",
    name: "ISP",
    varName: "isp",
    blurb: "InteractiveStuff player helpers. Not yet covered by the official wiki, but used throughout the built-in resource pack — it is the reliable way to ask \"is the player holding X in either hand?\" and to read camera velocity.",
    note: "Documented from the mod source (omar.projects.interactivestuff.scripts.variables.ISP).",
    groups: [
      G("Functions", [
        E("isMainHand(item)", [P("item", "ItemModel")], "Boolean", "Returns whether the given ItemModel is in the main hand."),
        E("isOffHand(item)", [P("item", "ItemModel")], "Boolean", "Returns whether the given ItemModel is in the off hand."),
        E("isHoldingItem(itemId)", [P("itemId", "String", "e.g. \"minecraft:sculk_sensor\"")], "Boolean",
          "Returns whether the player holds the given item in either hand. This is the check the built-in scripts use to gate features.",
          "check isp.isHoldingItem(\"minecraft:sculk_sensor\") do\n    say \"Sensor armed\"\nend"),
        E("getCameraPitchVelocity()", [], "Float", "Returns the rate of change of the camera's pitch. Used by the physics engine."),
        E("getCameraYawVelocity()", [], "Float", "Returns the rate of change of the camera's yaw. Used by the physics engine."),
        E("getMainHandItem()", [], "ItemModel", "Returns the item in the player's main hand."),
        E("getOffHandItem()", [], "ItemModel", "Returns the item in the player's off hand.")
      ])
    ]
  });

  /* =========================== GLOBALS =================================== */
  var globals = {
    functions: [
      E("importScript(packId, script)",
        [P("packId", "String", "The resource pack ID that contains the script"), P("script", "String", "The name of the script to import")], "",
        "Imports a reusable Vyn script library from a resource pack. Share common logic across multiple scripts. The built-in physics engine is imported this way.",
        "importScript(\"mypack\", \"math_utils\")\nimportScript(\"interactivestuff:interactive_resourcepack\", \"physics_lib\")"),
      E("excludeScript(packId, script)",
        [P("packId", "String", "The resource pack ID that owns the script"), P("script", "String", "The name of the script to disable")], "",
        "Disables an active script, preventing it from running.",
        "~ Disable a script conditionally\ncheck modLoader.isModLoaded(\"somemod\") do\n    excludeScript(\"mypack\", \"compat_script\")\nend"),
      E("debugText(text)", [P("text", "String", "The text to display")], "",
        "Displays a debug string on screen. Only works when Resource Pack Debug Mode is enabled in the mod config. Recommended to use it inside the onTick event!",
        "task onTick do\n    debugText(\"Player health: \" + player.getHealth())\n    debugText(\"Biome: \" + player.getWorld().getBiomeAt(player.getPosition()))\n    debugText(\"Velocity Y: \" + player.getVelocityY())\nend"),
      E("getDelta()", [], "Double",
        "Returns the normalized frame delta time. Use it to make animations framerate-independent — the result scales consistently at any FPS.",
        "~ Rotate item at a consistent speed regardless of FPS\nmake item player.getMainHandItem()\nitem.rotateY(90 * getDelta())")
    ],
    statements: [
      E("wait <ticks> do ... end", [P("ticks", "Integer", "20 ticks = 1 second")], "",
        "Executes a block of code after a delay. The rest of the script keeps running immediately — the wait block runs in the background.",
        "~ Play a sound, then play another sound 1 second later\nplayer.playSound(\"minecraft:entity.experience_orb.pickup\", 1.0, 1.0)\n\nwait 20 do\n    player.playSound(\"minecraft:block.note_block.bell\", 1.0, 1.2)\nend"),
      E("wait chaining", [], "",
        "Wait blocks can be nested to build timed sequences.",
        "wait 10 do\n    debugText(\"0.5 seconds\")\n    wait 10 do\n        debugText(\"1 second\")\n        wait 20 do\n            debugText(\"2 seconds\")\n        end\n    end\nend")
    ],
    events: [
      E("task onTick do ... end", [], "",
        "Called every game tick (20 times per second). Use it for logic that needs a fixed tick schedule rather than every render frame.",
        "task onTick do\n    debugText(\"XP Level: \" + player.getExperienceLevel())\nend"),
      E("task onSwingHand do ... end", [], "",
        "Called when the player swings their hand (left-click attack or animation trigger).",
        "task onSwingHand do\n    player.playSound(\"minecraft:entity.player.attack.weak\", 0.5, 1.2)\nend"),
      E("task onPlaySound takes sound do ... end", [P("sound", "Sound", "The sound being played")], "",
        "Called when the game plays a sound. React to or inspect the sound before it plays. The built-in sculk sensor script listens to this.",
        "task onPlaySound takes sound do\n    check sound.getName() == \"minecraft:block.note_block.harp\" do\n        player.playSound(\"minecraft:block.note_block.bell\", sound.getVolume(), sound.getPitch())\n    end\nend"),
      E("task onItemUpdate takes itemRendered do ... end", [P("itemRendered", "ItemModel", "The item currently being rendered")], "",
        "Called every render frame for each item being rendered. The primary event for transforms, physics, color changes and model overrides. Always guard with player.getGamemode() != \"spectator\".",
        "task onItemUpdate takes itemRendered do\n    check player.getGamemode() != \"spectator\" do\n        check itemRendered.getName() == \"minecraft:diamond_sword\" do\n            itemRendered.setColor(100, 180, 255)\n        end\n    end\nend"),
      E("task onKeyPress takes keyInput do ... end", [P("keyInput", "Integer", "The key code of the pressed key")], "",
        "Called when the player presses a key. Added in 0.7.0-SNAPSHOT-2.",
        "~ React to a specific key (69 = E)\ntask onKeyPress takes keyInput do\n    check keyInput == 69 do\n        player.playSound(\"minecraft:ui.button.click\", 1.0, 1.0)\n    end\nend")
    ]
  };

  window.IS_API = { types: types, globals: globals };
})();
