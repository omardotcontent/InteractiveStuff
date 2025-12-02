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

    private static final double RANGE_NORMAL_SQ = 64.0;
    private static final double RANGE_CALIBRATED_SQ = 256.0;

    private static final int COOLDOWN_NORMAL = 40;
    private static final int COOLDOWN_CALIBRATED = 20;

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
        ClientTickEvents.END_CLIENT_TICK.register(SculkHandler::onTick);
    }

    private static void onTick(final MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        final ClientPlayerEntity player = client.player;
        final boolean onGround = player.isOnGround();

        if (!wasOnGround && onGround) {
            handleLanding(player);
        }

        wasOnGround = onGround;
    }

    private static void handleLanding(final ClientPlayerEntity player) {
        if (!isWool(player.getSteppingPos(), player.getEntityWorld()) && !player.isSneaking()) {
            sculkCheck(null);
        }
    }

    public static void sculkCheck(final SoundInstance sound) {
        if (sound != null && isIgnoredSound(sound)) {
            return;
        }

        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        double distanceSq = 0.0;
        if (sound != null) {
            distanceSq = client.player.squaredDistanceTo(sound.getX(), sound.getY(), sound.getZ());
        }

        // Process Main Hand
        processSensor(client, client.player.getMainHandStack(), distanceSq);

        // Process Off Hand
        processSensor(client, client.player.getOffHandStack(), distanceSq);
    }

    private static void processSensor(final MinecraftClient client, final ItemStack stack, final double distanceSq) {
        if (stack.isOf(Items.SCULK_SENSOR)) {
            handleNormalSensor(client, distanceSq);
            return;
        }

        if (stack.isOf(Items.CALIBRATED_SCULK_SENSOR)) {
            handleCalibratedSensor(client, distanceSq);
        }
    }

    private static void handleNormalSensor(final MinecraftClient client, final double distanceSq) {
        if (VibrationTracker.isVibrating()) {
            return;
        }

        if (distanceSq > RANGE_NORMAL_SQ) {
            return;
        }

        activateNormal(client);
    }

    private static void handleCalibratedSensor(final MinecraftClient client, final double distanceSq) {
        if (VibrationTracker.isCalibratedVibrating()) {
            return;
        }

        if (distanceSq > RANGE_CALIBRATED_SQ) {
            return;
        }

        activateCalibrated(client);
    }

    private static void activateNormal(final MinecraftClient client) {
        if (client.player != null) {
            client.player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 1.0f, 1.0f);
        }

        VibrationTracker.setVibrating(true);

        BackgroundLoopHandler.getInstance().waitTicks("SculkDeactivateNormal", COOLDOWN_NORMAL, () -> client.execute(() -> {
            if (client.player == null || !VibrationTracker.isVibrating()) {
                return;
            }

            VibrationTracker.setVibrating(false);
            client.player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, 1.0f, 1.0f);
        }));
    }

    private static void activateCalibrated(final MinecraftClient client) {
        if (client.player != null) {
            client.player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 1.0f, 1.0f);
        }

        VibrationTracker.setCalibratedVibrating(true);

        BackgroundLoopHandler.getInstance().waitTicks("SculkDeactivateCalibrated", COOLDOWN_CALIBRATED, () -> client.execute(() -> {
            if (client.player == null || !VibrationTracker.isCalibratedVibrating()) {
                return;
            }

            VibrationTracker.setCalibratedVibrating(false);
            client.player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, 1.0f, 1.0f);
        }));
    }

    private static boolean isIgnoredSound(final SoundInstance sound) {
        if (sound.getAttenuationType() == SoundInstance.AttenuationType.NONE) {
            return true;
        }
        if (sound.getCategory() == SoundCategory.AMBIENT) {
            return true;
        }
        return IGNORED_SOUND_IDS.contains(sound.getId());
    }

    public static boolean isWool(final BlockPos pos, final net.minecraft.world.World world) {
        if (world == null) {
            return false;
        }
        final BlockState state = world.getBlockState(pos);
        return state.isIn(BlockTags.WOOL);
    }
}