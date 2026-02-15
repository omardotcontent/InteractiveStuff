package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import omar.projects.interactivestuff.handlers.CameraVelocityAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@VynType(name = "Player")
public final class Player {

    private ClientPlayerEntity player;
    private final MinecraftClient client;
    private LivingEntity livingEntity;

    public Player(final ClientPlayerEntity player, final MinecraftClient client) {
        this.player = player;
        this.client = client;
        this.livingEntity = player.getEntity();
    }

    public void setPlayer(final ClientPlayerEntity player) {
        this.player = player;
    }

    @VynFunc
    public Block getSteppingBlock() {
        if (player == null || client.world == null) {
            return null;
        }
        return new Block(player.getSteppingPos(), client.world);
    }

    @VynFunc
    public List<Block> getNearbyBlocks(final int blockRadius) {
        if (player == null || client.world == null) {
            return null;
        }
        final List<Block> blocks = new ArrayList<>();
        for (int x = -blockRadius; x <= blockRadius; x++) {
            for (int y = -blockRadius; y <= blockRadius; y++) {
                for (int z = -blockRadius; z <= blockRadius; z++) {
                    final Block block = new Block(player.getBlockPos().add(x, y, z), client.world);
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
        if (!(client.crosshairTarget instanceof BlockHitResult blockHit)) {
            return null;
        }
        if (player == null) {
            return null;
        }
        return new Block(blockHit.getBlockPos(), client.world);
    }

    @VynFunc
    public String getGamemode() {
        return (player != null && player.getGameMode() != null) ? player.getGameMode().asString() : null;
    }

    @VynFunc
    public Position getPosition() {
        return (player != null) ? new Position((int) player.getX(), (int) player.getY(), (int) player.getZ()) : null;
    }

    @VynFunc
    public World getWorld() {
        return (client.world != null) ? new World(client.world) : null;
    }

    @VynFunc
    public ItemModel getActiveItem() {
        return (player != null) ? new ItemModel(player.getActiveItem()) : null;
    }

    @VynFunc
    public ItemModel getMainHandItem() {
        return (player != null) ? new ItemModel(player.getMainHandStack()) : null;
    }

    @VynFunc
    public ItemModel getOffHandItem() {
        return (player != null) ? new ItemModel(player.getOffHandStack()) : null;
    }

    @VynFunc
    public ItemModel getBlockingItem() {
        return (livingEntity != null) ? new ItemModel(livingEntity.getBlockingItem()) : null;
    }

    @VynFunc
    public ItemModel getWeaponItem() {
        return (livingEntity != null) ? new ItemModel(livingEntity.getWeaponStack()) : null;
    }

    @VynFunc
    public ItemModel getPickBlockItem() {
        return (livingEntity != null) ? new ItemModel(livingEntity.getPickBlockStack()) : null;
    }

    @VynFunc
    public double getHealth() {
        return (player != null) ? player.getHealth() : -1;
    }

    @VynFunc
    public double getFoodLevel() {
        return (player != null) ? player.getHungerManager().getFoodLevel() : -1;
    }

    @VynFunc
    public double getSaturationLevel() {
        return (player != null) ? player.getHungerManager().getSaturationLevel() : -1;
    }

    @VynFunc
    public float getNauseaIntensity() {
        return player != null ? player.nauseaIntensity : 0.0f;
    }

    @VynFunc
    public int getExperienceLevel() {
        return player != null ? player.experienceLevel : -1;
    }

    @VynFunc
    public int getItemUseTime() {
        return (livingEntity != null) ? livingEntity.getItemUseTime() : -1;
    }

    @VynFunc
    public int getItemUseTimeLeft() {
        return (livingEntity != null) ? livingEntity.getItemUseTimeLeft() : -1;
    }

    @VynFunc
    public int getArmor() {
        return (livingEntity != null) ? livingEntity.getArmor() : -1;
    }

    @VynFunc
    public double getEyePosX() {
        return (livingEntity != null) ? livingEntity.getEyePos().x : 0;
    }

    @VynFunc
    public double getEyePosY() {
        return (livingEntity != null) ? livingEntity.getEyePos().y : 0;
    }

    @VynFunc
    public double getEyePosZ() {
        return (livingEntity != null) ? livingEntity.getEyePos().z : 0;
    }

    @VynFunc
    public double getHeadYaw() {
        return (livingEntity != null) ? livingEntity.getHeadYaw() : 0;
    }

    @VynFunc
    public double getDamageTiltYaw() {
        return (livingEntity != null) ? livingEntity.getDamageTiltYaw() : 0;
    }

    @VynFunc
    public double getStepHeight() {
        return (livingEntity != null) ? livingEntity.getStepHeight() : 0;
    }

    @VynFunc
    public double getExperienceProgress() {
        return player != null ? player.experienceProgress : -1f;
    }

    @VynFunc
    public double getVelocityX() {
        if (player == null) {
            return 0;
        }
        final double vx = player.getVelocity().getX();
        final double vz = player.getVelocity().getZ();
        final double yawRad = Math.toRadians(player.getYaw());
        return (vx * Math.cos(yawRad) + vz * Math.sin(yawRad)) * 0.3333;
    }

    @VynFunc
    public double getVelocityY() {
        if (player == null) return 0;
        final double y = player.getVelocity().getY();
        return (Math.abs(y + 0.0784) < 0.001) ? 0 : y * 0.3333;
    }

    @VynFunc
    public double getVelocityZ() {
        if (player == null) {
            return 0;
        }
        final double vx = player.getVelocity().getX();
        final double vz = player.getVelocity().getZ();
        final double yawRad = Math.toRadians(player.getYaw());
        return (-vx * Math.sin(yawRad) + vz * Math.cos(yawRad)) * 0.3333;
    }

    @VynFunc
    public float getCameraPitchVelocity() {
        if (client.gameRenderer == null) {
            return 0.0f;
        }
        final Camera camera = client.gameRenderer.getCamera();
        if (camera instanceof CameraVelocityAccessor acc) {
            return acc.interactivestuff$getPitchVelocity();
        }
        return 0.0f;
    }

    @VynFunc
    public float getCameraYawVelocity() {
        if (client.gameRenderer == null) {
            return 0.0f;
        }
        final Camera camera = client.gameRenderer.getCamera();
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
        return player != null && player.isInFluid();
    }

    @VynFunc
    public boolean isOnFire() {
        return player != null && player.isOnFire();
    }

    @VynFunc
    public boolean isTouchingWater() {
        return player != null && player.isTouchingWater();
    }

    @VynFunc
    public boolean isFlyingVehicle() {
        return player != null && player.isFlyingVehicle();
    }

    @VynFunc
    public boolean isClimbing() {
        return player != null && player.isClimbing();
    }

    @VynFunc
    public boolean isDescending() {
        return player != null && player.isDescending();
    }

    @VynFunc
    public boolean isCrawling() {
        return player != null && player.isCrawling();
    }

    @VynFunc
    public boolean isBlocking() {
        return player != null && player.isBlocking();
    }

    @VynFunc
    public boolean isRiding() {
        return player != null && player.isRiding();
    }

    @VynFunc
    public boolean isPushable() {
        return player != null && player.isPushable();
    }

    @VynFunc
    public boolean isPushedByFluids() {
        return player != null && player.isPushedByFluids();
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
        return player != null && player.isUsingRiptide();
    }

    @VynFunc
    public boolean isUsingSpyglass() {
        return player != null && player.isUsingSpyglass();
    }

    @VynFunc
    public boolean isFrozen() {
        return player != null && player.isFrozen();
    }

    @VynFunc
    public boolean isGlowing() {
        return player != null && player.isGlowing();
    }

    @VynFunc
    public boolean isGlowingLocal() {
        return player != null && player.isGlowingLocal();
    }

    @VynFunc
    public boolean isAtCloudHeight() {
        return player != null && player.isAtCloudHeight();
    }

    @VynFunc
    public boolean isAutoJumpEnabled() {
        return player != null && player.isAutoJumpEnabled();
    }

    @VynFunc
    public boolean isHoldingOntoLadder() {
        return player != null && player.isHoldingOntoLadder();
    }

    @VynFunc
    public boolean isLimitedCraftingEnabled() {
        return player != null && player.isLimitedCraftingEnabled();
    }

    @VynFunc
    public boolean isInsideWall() {
        return player != null && player.isInsideWall();
    }

    @VynFunc
    public boolean isJumping() {
        return player != null && player.isJumping();
    }

    @VynFunc
    public boolean isOnRail() {
        return player != null && player.isOnRail();
    }

    @VynFunc
    public boolean isFireImmune() {
        return player != null && player.isFireImmune();
    }


    @VynFunc
    public boolean isSubmergedInWater() {
        return player != null && player.isSubmergedInWater();
    }

    @VynFunc
    public boolean isSneaking() {
        return player != null && player.isSneaking();
    }

    @VynFunc
    public boolean isOnGround() {
        return player != null && player.isOnGround();
    }

    @VynFunc
    public boolean showsDeathScreen() {
        return player != null && player.showsDeathScreen();
    }

    @VynFunc
    public boolean isHorizontalCollision() {
        return player != null && player.horizontalCollision;
    }

    @VynFunc
    public boolean isInSneakingPose() {
        return player != null && player.isInSneakingPose();
    }

    @VynFunc
    public boolean shouldSlowDown() {
        return player != null && player.shouldSlowDown();
    }

    @VynFunc
    public boolean isRidingJumpable() {
        return player != null && player.getJumpingMount() != null;
    }

    @VynFunc
    public float getMountJumpStrength() {
        return player != null ? player.getMountJumpStrength() : 0.0f;
    }

    @VynFunc
    public boolean isCamera() {
        return player != null && client.getCameraEntity() == player;
    }

    @VynFunc
    public int getPermissionLevel() {
        return player != null ? player.getPermissionLevel() : 0;
    }

    @VynFunc
    public boolean isMainPlayer() {
        return player != null && player.isMainPlayer();
    }

    @VynFunc
    public float getMoodPercentage() {
        return player != null ? player.getMoodPercentage() : 0.0f;
    }


    @VynFunc
    public boolean canBreatheInWater() {
        return livingEntity != null && livingEntity.canBreatheInWater();
    }

    @VynFunc
    public boolean hasLandedInFluid() {
        return livingEntity != null && livingEntity.hasLandedInFluid();
    }

    @VynFunc
    public boolean isBaby() {
        return livingEntity != null && livingEntity.isBaby();
    }

    @VynFunc
    public float getScaleFactor() {
        return livingEntity != null ? livingEntity.getScaleFactor() : 1.0f;
    }

    @VynFunc
    public float getScale() {
        return livingEntity != null ? livingEntity.getScale() : 1.0f;
    }

    @VynFunc
    public boolean shouldSwimInFluids() {
        return livingEntity != null && livingEntity.shouldSwimInFluids();
    }

    @VynFunc
    public boolean canTakeDamage() {
        return livingEntity != null && livingEntity.canTakeDamage();
    }

    @VynFunc
    public boolean isPartOfGame() {
        return livingEntity != null && livingEntity.isPartOfGame();
    }

    @VynFunc
    public boolean isDead() {
        return livingEntity != null && livingEntity.isDead();
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
        return livingEntity != null ? livingEntity.getStuckArrowCount() : 0;
    }

    @VynFunc
    public int getStingerCount() {
        return livingEntity != null ? livingEntity.getStingerCount() : 0;
    }

    @VynFunc
    public boolean hasNoDrag() {
        return livingEntity != null && livingEntity.hasNoDrag();
    }

    @VynFunc
    public boolean isMainHand(final ItemModel item) {
        return player != null && item != null && player.getMainHandStack().equals(item.getFinalStack());
    }

    @VynFunc
    public boolean isOffHand(final ItemModel item) {
        return player != null && item != null && player.getOffHandStack().equals(item.getFinalStack());
    }

    @VynFunc
    public boolean isHoldingItem(final String itemId) {
        final ItemModel mainHandItem = getMainHandItem();
        final ItemModel offHandItem = getOffHandItem();
        final String mainItemName = mainHandItem == null ? "AIR" : mainHandItem.getName();
        final String offItemName = offHandItem == null ? "AIR" : offHandItem.getName();
        return mainItemName.equals(itemId) || offItemName.equals(itemId);
    }

    @VynFunc
    public void playSound(final String soundId, final double volume, final double pitch) {
        if (player != null) {
            player.playSound(Registries.SOUND_EVENT.get(Identifier.of(soundId)), (float) volume, (float) pitch);
        }
    }

    @VynFunc
    public void playSound(final Sound sound) {
        if (player == null) {
            return;
        }
        player.playSound(Registries.SOUND_EVENT.get(Identifier.of(sound.getName())), (float) sound.getVolume(), (float) sound.getPitch());
    }

    @VynFunc
    public void playSoundWorld(final Position position, final String soundId, final double volume, final double pitch) {
        if (player == null || client.world == null) {
            return;
        }
        client.world.playSoundClient(
                position.getX(),
                position.getY(),
                position.getZ(),
                Registries.SOUND_EVENT.get(Identifier.of(soundId)),
                SoundCategory.BLOCKS,
                (float) volume,
                (float) pitch,
                true);
    }

    @VynFunc
    public void playSoundWorld(final Position position, final Sound sound) {
        if (player == null || client.world == null) {
            return;
        }
        client.world.playSoundClient(
                position.getX(),
                position.getY(),
                position.getZ(),
                Registries.SOUND_EVENT.get(Identifier.of(sound.getName())),
                SoundCategory.BLOCKS,
                (float) sound.getVolume(),
                (float) sound.getPitch(),
                true);
    }

    public void sendMessage(final String message) {
        if (player == null) {
            return;
        }
        player.sendMessage(Text.of("§e[InteractiveStuff] " + message), false);
    }

    public ClientPlayerEntity getPlayer() {
        return player;
    }
}