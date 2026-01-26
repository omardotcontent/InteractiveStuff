package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import omar.projects.interactivestuff.handlers.CameraVelocityAccessor;

@VynType(name = "Player")
public final class Player {

    private ClientPlayerEntity player;
    private final MinecraftClient client;

    public Player(final ClientPlayerEntity player, final MinecraftClient client) {
        this.player = player;
        this.client = client;
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
    public Block getTargetBlock() {
        if (!(client.crosshairTarget instanceof BlockHitResult blockHit)) { return null;}
        if (player == null) {
            return (null);
        } else {
            assert client.world != null;
            return (new Block(blockHit.getBlockPos(), client.world));
        }
    }

    @VynFunc
    public String getGamemode() {
        if (player.getGameMode() == null) return null;
        return player == null ? null : player.getGameMode().asString();
    }

    @VynFunc
    public Position getPosition() {
        return player == null ? null : new Position((int) player.getX(), (int) player.getY(), (int) player.getZ());
    }

    @VynFunc
    public World getWorld() {
        return client.world == null ? null : new World(client.world);
    }

    @VynFunc
    public ItemModel getActiveItem() {
        return player == null ? null : new ItemModel(player.getActiveItem());
    }

    @VynFunc
    public double getVelocityX() {
        if (player == null) return 0;
        double vx = player.getVelocity().getX();
        double vz = player.getVelocity().getZ();
        double yawRad = Math.toRadians(player.getYaw());
        // Rotate to get strafe velocity (left/right relative to player)
        return (vx * Math.cos(yawRad) + vz * Math.sin(yawRad)) * 0.3333;
    }

    @VynFunc
    public double getVelocityY() {
        if (player == null) return 0;
        double y = player.getVelocity().getY();
        if (Math.abs(y + 0.0784) < 0.001) return 0;
        return y * 0.3333;
    }

    @VynFunc
    public double getVelocityZ() {
        if (player == null) return 0;
        double vx = player.getVelocity().getX();
        double vz = player.getVelocity().getZ();
        double yawRad = Math.toRadians(player.getYaw());
        // Rotate to get forward velocity (forward/backward relative to player)
        return (-vx * Math.sin(yawRad) + vz * Math.cos(yawRad)) * 0.3333;
    }

    @VynFunc
    public float getCameraPitch() {
        if (client.gameRenderer == null) return 0.0f;
        final Camera camera = client.gameRenderer.getCamera();
        return (camera instanceof CameraVelocityAccessor acc) ? acc.interactivestuff$getPitchVelocity() : 0.0f;
    }

    @VynFunc
    public float getCameraYaw() {
        if (client.gameRenderer == null) return 0.0f;
        final Camera camera = client.gameRenderer.getCamera();
        return (camera instanceof CameraVelocityAccessor acc) ? acc.interactivestuff$getYawVelocity() : 0.0f;
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
    public boolean isMainHand(final ItemModel item) {
        if (player == null || item == null) return false;
        return player.getMainHandStack().equals(item.getFinalStack());
    }

    @VynFunc
    public boolean isOffHand(final ItemModel item) {
        if (player == null || item == null) return false;
        return player.getOffHandStack().equals(item.getFinalStack());
    }

    @VynFunc
    public boolean isHoldingItem(final String itemId) {
        final ItemModel mainHandItem = getMainHandItem(),
                offHandItem = getOffHandItem();

        final String mainItemName = mainHandItem == null ? "AIR" : mainHandItem.getName(),
                offItemName = offHandItem == null ? "AIR" : offHandItem.getName();

        return mainItemName.equals(itemId) || offItemName.equals(itemId);
    }

    @VynFunc
    public ItemModel getMainHandItem() {
        return player == null ? null : new ItemModel(player.getMainHandStack());
    }

    @VynFunc
    public ItemModel getOffHandItem() {
        return player == null ? null : new ItemModel(player.getOffHandStack());
    }

    @VynFunc
    public void playSound(final String soundId, final double volume, final double pitch) {
        if (player != null)
            player.playSound(Registries.SOUND_EVENT.get(Identifier.of(soundId)), (float) volume, (float) pitch);
    }

    @VynFunc
    public void playSound(final Sound sound) {
        if (player != null)
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