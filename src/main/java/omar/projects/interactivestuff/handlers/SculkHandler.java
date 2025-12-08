package omar.projects.interactivestuff.handlers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;

import java.util.Set;

public final class SculkHandler {

    private static boolean wasOnGround;

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
        if (!ConfigHandler.INSTANCE.enableSculkSensorFeature) {
            return;
        }

        final ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        final boolean isOnGround = player.isOnGround();

        if (!wasOnGround && isOnGround) {
            handleLanding(player, client);
        }

        wasOnGround = isOnGround;
    }

    private static void handleLanding(final ClientPlayerEntity player, final MinecraftClient client) {
        if (player.getEntityWorld().getBlockState(player.getSteppingPos()).isIn(BlockTags.WOOL)) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        processVibration(player.getX(), player.getY(), player.getZ(), client);
    }

    public static void sculkCheck(final SoundInstance sound) {
        final ConfigHandler config = ConfigHandler.INSTANCE;

        if (!config.enableSculkSensorFeature) {
            return;
        }

        if (sound == null) {
            return;
        }

        if (sound.getAttenuationType() == SoundInstance.AttenuationType.NONE) {
            return;
        }

        if (sound.getCategory() == SoundCategory.AMBIENT) {
            return;
        }

        if (IGNORED_SOUND_IDS.contains(sound.getId())) {
            return;
        }

        processVibration(sound.getX(), sound.getY(), sound.getZ(), MinecraftClient.getInstance());
    }

    private static void processVibration(final double x, final double y, final double z, final MinecraftClient client) {
        final ClientPlayerEntity player = client.player;
        if (player == null || player.isSpectator()) {
            return;
        }

        final double distanceSq = player.squaredDistanceTo(x, y, z);

        if (isHoldingSculkSensor(player)) {
            handleNormalSensor(client, distanceSq);
        }

        if (isHoldingCalibratedSculkSensor(player)) {
            handleCalibratedSensor(client, distanceSq);
        }
    }

    private static boolean isHoldingSculkSensor(final ClientPlayerEntity player) {
        return player.getMainHandStack().isOf(Items.SCULK_SENSOR) || player.getOffHandStack().isOf(Items.SCULK_SENSOR);
    }

    private static boolean isHoldingCalibratedSculkSensor(final ClientPlayerEntity player) {
        return player.getMainHandStack().isOf(Items.CALIBRATED_SCULK_SENSOR) || player.getOffHandStack().isOf(Items.CALIBRATED_SCULK_SENSOR);
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
        final ClientPlayerEntity player = client.player;
        if (player != null) {
            player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 1.0f, 1.0f);
        }

        VibrationTracker.setVibrating(true);

        BackgroundLoopHandler.getInstance().waitTicks("SculkDeactivateNormal", COOLDOWN_NORMAL, () -> client.execute(() -> {
            final ClientPlayerEntity currentPlayer = client.player;
            if (currentPlayer == null) {
                return;
            }
            if (!VibrationTracker.isVibrating()) {
                return;
            }

            VibrationTracker.setVibrating(false);
            if(isHoldingSculkSensor(currentPlayer)) currentPlayer.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, 1.0f, 1.0f);
        }));
    }

    private static void activateCalibrated(final MinecraftClient client) {
        final ClientPlayerEntity player = client.player;
        if (player != null) {
            player.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 1.0f, 1.0f);
        }

        VibrationTracker.setCalibratedVibrating(true);

        BackgroundLoopHandler.getInstance().waitTicks("SculkDeactivateCalibrated", COOLDOWN_CALIBRATED, () -> client.execute(() -> {
            final ClientPlayerEntity currentPlayer = client.player;
            if (currentPlayer == null) {
                return;
            }
            if (!VibrationTracker.isCalibratedVibrating()) {
                return;
            }

            VibrationTracker.setCalibratedVibrating(false);
            if(isHoldingCalibratedSculkSensor(currentPlayer)) currentPlayer.playSound(SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, 1.0f, 1.0f);
        }));
    }
}