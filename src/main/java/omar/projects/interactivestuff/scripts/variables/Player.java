package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import omar.projects.interactivestuff.handlers.CameraVelocityAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@VynType(name = "Player")
public final class Player {

    private LocalPlayer player;
    private final Minecraft client;
    private LivingEntity livingEntity;
    private final World worldView = new World(null);
    private long nearbyTick = Long.MIN_VALUE;
    private BlockPos nearbyPosition;
    private final Map<NearbyQuery, Boolean> nearbyQueries = new HashMap<>();
    private record NearbyQuery(String blockId, int radius) {}
    private Vec3 velocitySource;
    private float velocityYaw = Float.NaN;
    private double localVelocityX, localVelocityY, localVelocityZ;

    public Player(final LocalPlayer player, final Minecraft client) {
        this.client = client;
        setPlayer(player);
    }

    public void setLivingEntity(final LivingEntity entity) {
        this.livingEntity = entity;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public void setPlayer(final LocalPlayer player) {
        this.player = player;
        this.livingEntity = player;
        worldView.setWorld(player == null ? null : client.level);
        nearbyTick = Long.MIN_VALUE;
        nearbyQueries.clear();
        velocitySource = null;
    }

    @VynFunc
    public Block getSteppingBlock() {
        if (player == null || client.level == null) {
            return null;
        }
        return new Block(player.getOnPos(), client.level);
    }

    @VynFunc
    public List<Block> getNearbyBlocks(final int blockRadius) {
        if (player == null || client.level == null) {
            return null;
        }
        final List<Block> blocks = new ArrayList<>();
        for (int x = -blockRadius; x <= blockRadius; x++) {
            for (int y = -blockRadius; y <= blockRadius; y++) {
                for (int z = -blockRadius; z <= blockRadius; z++) {
                    final Block block = new Block(player.blockPosition().offset(x, y, z), client.level);
                    if (!Objects.equals(block.getName(), "minecraft:air")) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    @VynFunc
    public Block getTargetBlock() {
        if (!(client.hitResult instanceof BlockHitResult blockHit) || blockHit.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        if (player == null || client.level == null) {
            return null;
        }
        return new Block(blockHit.getBlockPos(), client.level);
    }

    @VynFunc
    public String getGamemode() {
        return (player != null && player.gameMode() != null) ? player.gameMode().getName() : null;
    }

    @VynFunc
    public Position getPosition() {
        return (player != null) ? new Position(player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ()) : null;
    }

    @VynFunc
    public World getWorld() {
        worldView.setWorld(client.level);
        return client.level == null ? null : worldView;
    }

    @VynFunc
    public ItemModel getActiveItem() {
        return (player != null) ? new ItemModel(omar.projects.interactivestuff.compat.MinecraftAccess.activeItem(player)) : null;
    }

    @VynFunc
    public ItemModel getMainHandItem() {
        return (player != null) ? new ItemModel(player.getMainHandItem()) : null;
    }

    @VynFunc
    public ItemModel getOffHandItem() {
        return (player != null) ? new ItemModel(player.getOffhandItem()) : null;
    }

    @VynFunc
    public ItemModel getBlockingItem() {
        return livingEntity != null && livingEntity.getItemBlockingWith() != null
                ? new ItemModel(livingEntity.getItemBlockingWith()) : null;
    }

    @VynFunc
    public ItemModel getWeaponItem() {
        return (livingEntity != null) ? new ItemModel(livingEntity.getWeaponItem()) : null;
    }

    @VynFunc
    public ItemModel getPickBlockItem() {
        return (livingEntity != null) ? new ItemModel(livingEntity.getPickResult()) : null;
    }

    @VynFunc
    public double getHealth() {
        return (player != null) ? player.getHealth() : -1;
    }

    @VynFunc
    public double getFoodLevel() {
        return (player != null) ? player.getFoodData().getFoodLevel() : -1;
    }

    @VynFunc
    public double getSaturationLevel() {
        return (player != null) ? player.getFoodData().getSaturationLevel() : -1;
    }

    @VynFunc
    public float getNauseaIntensity() {
        return player != null ? player.portalEffectIntensity : 0.0f;
    }

    @VynFunc
    public int getExperienceLevel() {
        return player != null ? player.experienceLevel : -1;
    }

    @VynFunc
    public int getItemUseTime() {
        return (livingEntity != null) ? livingEntity.getTicksUsingItem() : -1;
    }

    @VynFunc
    public int getItemUseTimeLeft() {
        return (livingEntity != null) ? livingEntity.getUseItemRemainingTicks() : -1;
    }

    @VynFunc
    public int getArmor() {
        return (livingEntity != null) ? livingEntity.getArmorValue() : -1;
    }

    @VynFunc
    public double getEyePosX() {
        return (livingEntity != null) ? livingEntity.getEyePosition().x : 0;
    }

    @VynFunc
    public double getEyePosY() {
        return (livingEntity != null) ? livingEntity.getEyePosition().y : 0;
    }

    @VynFunc
    public double getEyePosZ() {
        return (livingEntity != null) ? livingEntity.getEyePosition().z : 0;
    }

    @VynFunc
    public double getHeadYaw() {
        return (livingEntity != null) ? livingEntity.getYHeadRot() : 0;
    }

    @VynFunc
    public double getDamageTiltYaw() {
        return (livingEntity != null) ? livingEntity.getHurtDir() : 0;
    }

    @VynFunc
    public double getStepHeight() {
        return (livingEntity != null) ? livingEntity.maxUpStep() : 0;
    }

    @VynFunc
    public double getExperienceProgress() {
        return player != null ? player.experienceProgress : -1f;
    }

    private void updateVelocity() {
        if (player == null) {
            localVelocityX = localVelocityY = localVelocityZ = 0;
            return;
        }
        Vec3 velocity = player.getDeltaMovement();
        float yaw = player.getYRot();
        if (velocity.equals(velocitySource) && yaw == velocityYaw) return;
        velocitySource = velocity;
        velocityYaw = yaw;
        double radians = Math.toRadians(yaw), cosine = Math.cos(radians), sine = Math.sin(radians);
        localVelocityX = (velocity.x * cosine + velocity.z * sine) * 0.3333;
        localVelocityY = Math.abs(velocity.y + 0.0784) < 0.001 ? 0 : velocity.y * 0.3333;
        localVelocityZ = (-velocity.x * sine + velocity.z * cosine) * 0.3333;
    }

    @VynFunc
    public double getVelocityX() { updateVelocity(); return localVelocityX; }
    @VynFunc
    public double getVelocityY() { updateVelocity(); return localVelocityY; }
    @VynFunc
    public double getVelocityZ() { updateVelocity(); return localVelocityZ; }

    /** Memoized per tick and player block position, with no Vyn Block wrappers. */
    @VynFunc
    public boolean hasNearbyBlock(String blockId, int radius) {
        if (player == null || client.level == null || radius < 0) return false;
        long tick = client.level.getGameTime();
        BlockPos center = player.blockPosition();
        if (tick != nearbyTick || !center.equals(nearbyPosition)) {
            nearbyTick = tick;
            nearbyPosition = center;
            nearbyQueries.clear();
        }
        return nearbyQueries.computeIfAbsent(new NearbyQuery(blockId, radius), query -> {
            var block = BuiltInRegistries.BLOCK.get(Identifier.parse(query.blockId()))
                    .map(net.minecraft.core.Holder.Reference::value).orElse(null);
            if (block == null) return false;
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int x = -radius; x <= radius; x++)
                for (int y = -radius; y <= radius; y++)
                    for (int z = -radius; z <= radius; z++) {
                        cursor.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                        if (client.level.getBlockState(cursor).is(block)) return true;
                    }
            return false;
        });
    }

    @VynFunc
    public float getCameraPitchVelocity() {
        final Camera camera = omar.projects.interactivestuff.compat.MinecraftAccess.camera(client);
        if (camera instanceof CameraVelocityAccessor acc) {
            return acc.interactivestuff$getPitchVelocity();
        }
        return 0.0f;
    }

    @VynFunc
    public float getCameraYawVelocity() {
        final Camera camera = omar.projects.interactivestuff.compat.MinecraftAccess.camera(client);
        if (camera instanceof CameraVelocityAccessor acc) {
            return acc.interactivestuff$getYawVelocity();
        }
        return 0.0f;
    }

    @VynFunc
    public boolean isSwimming() {
        return player != null && player.isSwimming();
    }

    @VynFunc
    public boolean isSprinting() {
        return player != null && player.isSprinting();
    }

    @VynFunc
    public boolean isInLava() {
        return player != null && player.isInLava();
    }

    @VynFunc
    public boolean isInFluid() {
        return player != null && player.isInLiquid();
    }

    @VynFunc
    public boolean isOnFire() {
        return player != null && player.isOnFire();
    }

    @VynFunc
    public boolean isTouchingWater() {
        return player != null && player.isInWater();
    }

    @VynFunc
    public boolean isFlyingVehicle() {
        return player != null && omar.projects.interactivestuff.compat.MinecraftAccess.isFlyingVehicle(player);
    }

    @VynFunc
    public boolean isClimbing() {
        return player != null && player.onClimbable();
    }

    @VynFunc
    public boolean isDescending() {
        return player != null && player.isDescending();
    }

    @VynFunc
    public boolean isCrawling() {
        return player != null && player.isVisuallyCrawling();
    }

    @VynFunc
    public boolean isBlocking() {
        return player != null && player.isBlocking();
    }

    @VynFunc
    public boolean isRiding() {
        return player != null && player.isHandsBusy();
    }

    @VynFunc
    public boolean isPushable() {
        return player != null && player.isPushable();
    }

    @VynFunc
    public boolean isPushedByFluids() {
        return player != null && player.isPushedByFluid();
    }

    @VynFunc
    public boolean isInvulnerable() {
        return player != null && player.isInvulnerable();
    }

    @VynFunc
    public boolean isUsingItem() {
        return player != null && player.isUsingItem();
    }

    @VynFunc
    public boolean isUsingRiptide() {
        return player != null && player.isAutoSpinAttack();
    }

    @VynFunc
    public boolean isUsingSpyglass() {
        return player != null && player.isScoping();
    }

    @VynFunc
    public boolean isFrozen() {
        return player != null && player.isFullyFrozen();
    }

    @VynFunc
    public boolean isGlowing() {
        return player != null && player.isCurrentlyGlowing();
    }

    @VynFunc
    public boolean isGlowingLocal() {
        return player != null && player.hasGlowingTag();
    }

    @VynFunc
    public boolean isAtCloudHeight() {
        return player != null && omar.projects.interactivestuff.compat.MinecraftAccess.isInClouds(player);
    }

    @VynFunc
    public boolean isAutoJumpEnabled() {
        return player != null && player.isAutoJumpEnabled();
    }

    @VynFunc
    public boolean isHoldingOntoLadder() {
        return player != null && player.isSuppressingSlidingDownLadder();
    }

    @VynFunc
    public boolean isLimitedCraftingEnabled() {
        return player != null && player.getDoLimitedCrafting();
    }

    @VynFunc
    public boolean isInsideWall() {
        return player != null && player.isInWall();
    }

    @VynFunc
    public boolean isJumping() {
        return player != null && omar.projects.interactivestuff.compat.MinecraftAccess.isJumping(player);
    }

    @VynFunc
    public boolean isOnRail() {
        return player != null && player.isOnRails();
    }

    @VynFunc
    public boolean isFireImmune() {
        return player != null && player.fireImmune();
    }

    @VynFunc
    public boolean isFirstPerson() {
        return player != null && client.options.getCameraType().isFirstPerson();
    }


    @VynFunc
    public boolean isSubmergedInWater() {
        return player != null && player.isUnderWater();
    }

    @VynFunc
    public boolean isSneaking() {
        return player != null && player.isShiftKeyDown();
    }

    @VynFunc
    public boolean isOnGround() {
        return player != null && player.onGround();
    }

    @VynFunc
    public boolean showsDeathScreen() {
        return player != null && player.shouldShowDeathScreen();
    }

    @VynFunc
    public boolean isHorizontalCollision() {
        return player != null && player.horizontalCollision;
    }

    @VynFunc
    public boolean isInSneakingPose() {
        return player != null && player.isCrouching();
    }

    @VynFunc
    public boolean shouldSlowDown() {
        return player != null && player.isMovingSlowly();
    }

    @VynFunc
    public boolean isRidingJumpable() {
        return player != null && player.jumpableVehicle() != null;
    }

    @VynFunc
    public float getMountJumpStrength() {
        return player != null ? player.getJumpRidingScale() : 0.0f;
    }

    @VynFunc
    public boolean isCamera() {
        return player != null && client.getCameraEntity() == player;
    }

    @VynFunc
    public int getPermissionLevel() {
        return omar.projects.interactivestuff.compat.MinecraftAccess.permissionLevel(player);
    }

    @VynFunc
    public boolean isMainPlayer() {
        return player != null && player.isLocalPlayer();
    }

    @VynFunc
    public float getMoodPercentage() {
        return player != null ? player.getCurrentMood() : 0.0f;
    }


    @VynFunc
    public boolean canBreatheInWater() {
        return livingEntity != null && livingEntity.canBreatheUnderwater();
    }

    @VynFunc
    public boolean hasLandedInFluid() {
        return livingEntity != null && livingEntity.hasLandedInLiquid();
    }

    @VynFunc
    public boolean isBaby() {
        return livingEntity != null && livingEntity.isBaby();
    }

    @VynFunc
    public float getScaleFactor() {
        return livingEntity != null ? livingEntity.getAgeScale() : 1.0f;
    }

    @VynFunc
    public float getScale() {
        return livingEntity != null ? livingEntity.getScale() : 1.0f;
    }

    @VynFunc
    public boolean shouldSwimInFluids() {
        return livingEntity != null && livingEntity.isAffectedByFluids();
    }

    @VynFunc
    public boolean canTakeDamage() {
        return livingEntity != null && livingEntity.canBeSeenAsEnemy();
    }

    @VynFunc
    public boolean isPartOfGame() {
        return livingEntity != null && livingEntity.canBeSeenByAnyone();
    }

    @VynFunc
    public boolean isDead() {
        return livingEntity != null && livingEntity.isDeadOrDying();
    }

    @VynFunc
    public float getLuck() {
        return livingEntity != null ? livingEntity.getLuck() : 0.0f;
    }

    @VynFunc
    public float getAbsorptionAmount() {
        return livingEntity != null ? livingEntity.getAbsorptionAmount() : 0.0f;
    }

    @VynFunc
    public float getMaxAbsorption() {
        return livingEntity != null ? livingEntity.getMaxAbsorption() : 0.0f;
    }

    @VynFunc
    public int getStuckArrowCount() {
        return livingEntity != null ? livingEntity.getArrowCount() : 0;
    }

    @VynFunc
    public int getStingerCount() {
        return livingEntity != null ? livingEntity.getStingerCount() : 0;
    }

    @VynFunc
    public boolean hasNoDrag() {
        return livingEntity != null && livingEntity.shouldDiscardFriction();
    }

    @VynFunc
    public boolean isMainHand(final ItemModel item) {
        return player != null && item != null && player.getMainHandItem() == item.getSourceStack();
    }

    @VynFunc
    public boolean isOffHand(final ItemModel item) {
        return player != null && item != null && player.getOffhandItem() == item.getSourceStack();
    }

    @VynFunc
    public boolean isHoldingItem(final String itemId) {
        return player != null && (BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString().equals(itemId)
                || BuiltInRegistries.ITEM.getKey(player.getOffhandItem().getItem()).toString().equals(itemId));
    }

    @VynFunc
    public void playSound(final String soundId, final double volume, final double pitch) {
        if (player != null) {
            player.playSound(BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(soundId)).orElseThrow().value(), (float) volume, (float) pitch);
        }
    }

    @VynFunc
    public void playSound(final Sound sound) {
        if (player == null) {
            return;
        }
        player.playSound(BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(sound.getName())).orElseThrow().value(), (float) sound.getVolume(), (float) sound.getPitch());
    }

    @VynFunc
    public void playSoundWorld(final Position position, final String soundId, final double volume, final double pitch) {
        if (player == null || client.level == null) {
            return;
        }
        client.level.playLocalSound(
                position.getX(),
                position.getY(),
                position.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(soundId)).orElseThrow().value(),
                SoundSource.BLOCKS,
                (float) volume,
                (float) pitch,
                true);
    }

    @VynFunc
    public void playSoundWorld(final Position position, final Sound sound) {
        if (player == null || client.level == null) {
            return;
        }
        client.level.playLocalSound(
                position.getX(),
                position.getY(),
                position.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(Identifier.parse(sound.getName())).orElseThrow().value(),
                SoundSource.BLOCKS,
                (float) sound.getVolume(),
                (float) sound.getPitch(),
                true);
    }

    public void sendMessage(final String message) {
        if (player == null) {
            return;
        }
        omar.projects.interactivestuff.compat.MinecraftAccess.chat(player, Component.literal("§e[InteractiveStuff] " + message));
    }

    public LocalPlayer getPlayer() {
        return player;
    }
}