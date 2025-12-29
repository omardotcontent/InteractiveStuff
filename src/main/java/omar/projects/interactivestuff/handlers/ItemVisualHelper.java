package omar.projects.interactivestuff.handlers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import omar.projects.interactivestuff.ISComponents;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;

@Environment(EnvType.CLIENT)
public final class ItemVisualHelper {

    public static ItemStack applyVisuals(
            final ItemStack original,
            final AbstractClientPlayerEntity player
    ) {
        if (original.isEmpty()) return original;

        /* ---------------- SCULK ---------------- */
        if (original.isOf(Items.SCULK_SENSOR) && VibrationTracker.isVibrating()) {
            ItemStack copy = original.copy();
            copy.set(ISComponents.VIBRATING, true);
            return copy;
        }

        if (original.isOf(Items.CALIBRATED_SCULK_SENSOR) && VibrationTracker.isCalibratedVibrating()) {
            ItemStack copy = original.copy();
            copy.set(ISComponents.CALIBRATED_VIBRATING, true);
            return copy;
        }

        /* ---------------- WATER SENSITIVE ---------------- */
        if (ConfigHandler.INSTANCE.enableTextureChanges
                && player.isSubmergedInWater()
                && isWaterSensitive(original)) {
            ItemStack copy = original.copy();
            copy.set(ISComponents.WATERLOGGED, true);
            return copy;
        }

        return original;
    }

    private static boolean isWaterSensitive(ItemStack stack) {
        return stack.isOf(Items.TORCH)
                || stack.isOf(Items.SOUL_TORCH)
                || stack.isOf(Items.CAMPFIRE)
                || stack.isOf(Items.SOUL_CAMPFIRE)
                || stack.isOf(Items.COPPER_TORCH)
                || stack.isOf(Items.REDSTONE_TORCH)
                || stack.isOf(Items.WATER_BUCKET);
    }
}