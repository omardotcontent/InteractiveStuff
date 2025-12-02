package omar.projects.interactivestuff.handlers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public final class SculkHandler {

    private static boolean wasOnGround = false;

    // Pre-define ignored sounds to avoid creating new objects every check
    private static final Set<Identifier> IGNORED_SOUND_IDS = Set.of(
            SoundEvents.BLOCK_SCULK_SENSOR_CLICKING.id(),
            SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP.id(),
            SoundEvents.BLOCK_WOOL_BREAK.id(),
            SoundEvents.BLOCK_WOOL_FALL.id(),
            SoundEvents.BLOCK_WOOL_HIT.id(),
            SoundEvents.BLOCK_WOOL_STEP.id(),
            SoundEvents.BLOCK_WOOL_PLACE.id()
    );

    public static void register() {
        // Register the tick listener only ONCE here.
        ClientTickEvents.END_CLIENT_TICK.register(SculkHandler::onTick);
    }

    private static void onTick(final MinecraftClient client) {
        if (client.player == null) return;

        final ClientPlayerEntity player = client.player;
        final boolean onGround = player.isOnGround();

        // Detect landing (previously in air, now on ground)
        if (!wasOnGround && onGround) {
            handleLanding(player);
        }

        wasOnGround = onGround;
    }

    private static void handleLanding(final ClientPlayerEntity player) {
        // Sculk logic
        if (!isWool(player.getSteppingPos(), player.getEntityWorld()) && !player.isSneaking()) {
            // Pass null to indicate a physical vibration (landing), not a sound event
            sculkCheck(null);
        }
    }

    public static void sculkCheck(final SoundInstance sound) {
        // External tracker check
        if (VibrationTracker.isVibrating()) return;

        // Player must be holding a sensor
        if (!isHoldingSensor()) return;

        final MinecraftClient client = MinecraftClient.getInstance();

        // Sound validation
        if (sound != null) {
            if (isIgnoredSound(sound)) return;

            // Distance check (squared is faster than sqrt)
            if (client.player != null && client.player.squaredDistanceTo(sound.getX(), sound.getY(), sound.getZ()) >= 64.0) {
                return;
            }
        }

        // Activate Sculk Effect
        if (client.player != null) {
            client.player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 1.0f, 1.0f);
        }

        VibrationTracker.setVibrating(true);

        // Schedule deactivation
        BackgroundLoopHandler.getInstance().waitTicks("SculkDeactivate", 50, () -> client.execute(() -> {
            if (client.player == null || !VibrationTracker.isVibrating()) return;

            VibrationTracker.setVibrating(false);
            client.player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, 1.0f, 1.0f);
        }));
    }

    private static boolean isHoldingSensor() {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;

        final ItemStack main = player.getMainHandStack();
        final ItemStack off = player.getOffHandStack();

        return isSensorItem(main) || isSensorItem(off);
    }

    private static boolean isSensorItem(final ItemStack stack) {
        return stack.isOf(Items.SCULK_SENSOR) || stack.isOf(Items.CALIBRATED_SCULK_SENSOR);
    }

    private static boolean isIgnoredSound(final SoundInstance sound) {
        // 1. Check Attenuation (Global sounds vs Positional sounds)
        if (sound.getAttenuationType() == SoundInstance.AttenuationType.NONE) return true;

        // 2. Check Category
        if (sound.getCategory() == SoundCategory.AMBIENT) return true;

        // 3. Check Specific IDs (using the Set defined at the top)
        return IGNORED_SOUND_IDS.contains(sound.getId());
    }

    /**
     * Checks if a block is part of the WOOL tag.
     * This replaces the manual check for every color.
     */
    public static boolean isWool(final BlockPos pos, final net.minecraft.world.World world) {
        if (world == null) return false;
        final BlockState state = world.getBlockState(pos);
        return state.isIn(BlockTags.WOOL);
    }
}