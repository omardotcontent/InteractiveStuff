package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.util.Objects;

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
        if (player == null) {
            return null;
        } else {
            assert client.world != null;
            return new Block(player.getSteppingPos(), client.world);
        }
    }

    @VynFunc
    public Block getTargetBlock() {
        final BlockHitResult blockHit = (BlockHitResult) client.crosshairTarget;
        if (blockHit == null) {
            return null;
        } else {
            if (player == null) {
                return (null);
            } else {
                assert client.world != null;
                return (new Block(blockHit.getBlockPos(), client.world));
            }
        }
    }

    @VynFunc
    public String getGamemode() {
        return player == null ? null : Objects.requireNonNull(player.getGameMode()).asString();
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
    public boolean isHoldingItem(final String itemId) {
        final Item mainHandItem = getMainHandItem(),
                offHandItem = getOffHandItem();

        final String mainItemName = mainHandItem == null ? "AIR" : mainHandItem.getName(),
                offItemName = offHandItem == null ? "AIR" : offHandItem.getName();

        return mainItemName.equals(itemId) || offItemName.equals(itemId);
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
        if (player != null) {
            assert client.world != null;
            client.world.playSoundClient(
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    Registries.SOUND_EVENT.get(Identifier.of(soundId))
                    , SoundCategory.BLOCKS,
                    (float) volume,
                    (float) pitch,
                    true);
        }
    }

    @VynFunc
    public void playSoundWorld(final Position position, final Sound sound) {
        if (player != null) {
            assert client.world != null;
            client.world.playSoundClient(
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    Registries.SOUND_EVENT.get(Identifier.of(sound.getName()))
                    , SoundCategory.BLOCKS,
                    (float) sound.getVolume(),
                    (float) sound.getPitch(),
                    true);
        }
    }

    public void sendMessage(final String message) {
        if (player != null)
            player.sendMessage(Text.of("§e[InteractiveStuff] " + message), false);
    }

    public Item getMainHandItem() {
        return player == null ? null : new Item(player.getMainHandStack());
    }


    public Item getOffHandItem() {
        return player == null ? null : new Item(player.getOffHandStack());
    }


    public ClientPlayerEntity getPlayer() {
        return player;
    }
}