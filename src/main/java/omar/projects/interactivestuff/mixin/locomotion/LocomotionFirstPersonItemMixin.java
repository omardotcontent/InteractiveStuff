package omar.projects.interactivestuff.mixin.locomotion;

import com.trainguy9512.locomotion.render.FirstPersonPlayerRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import omar.projects.interactivestuff.ISComponents;
import omar.projects.interactivestuff.handlers.VibrationTracker;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FirstPersonPlayerRenderer.class)
public abstract class LocomotionFirstPersonItemMixin {

    @ModifyVariable(
            method = "renderItem",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0 // Explicitly targets the first ItemStack argument
    )
    private ItemStack modifyLocomotionRenderedStack(ItemStack stack) {
        // --- Example: SCULK SENSOR visual vibration ---
        if (stack.isOf(Items.SCULK_SENSOR)) {
            if (VibrationTracker.isVibrating()) {
                ItemStack copy = stack.copy();
                copy.set(ISComponents.VIBRATING, true);
                return copy;
            }
            return stack;
        }

        // --- Example: CALIBRATED SENSOR visual vibration ---
        if (stack.isOf(Items.CALIBRATED_SCULK_SENSOR)) {
            if (VibrationTracker.isCalibratedVibrating()) {
                ItemStack copy = stack.copy();
                copy.set(ISComponents.CALIBRATED_VIBRATING, true);
                return copy;
            }
            return stack;
        }

        // --- WATERLOGGED torch logic ---
        if (isEligibleTorch(stack)) {
            var player = MinecraftClient.getInstance().player;
            if (player != null) {
                ItemStack copy = stack.copy();
                boolean wet = player.isSubmergedInWater()
                        && ConfigHandler.INSTANCE.enableTextureChanges;
                copy.set(ISComponents.WATERLOGGED, wet);
                return copy;
            }
        }

        return stack;
    }

    @Unique
    private boolean isEligibleTorch(ItemStack stack) {
        return stack.isOf(Items.CAMPFIRE)
                || stack.isOf(Items.SOUL_CAMPFIRE)
                || stack.isOf(Items.TORCH)
                || stack.isOf(Items.SOUL_TORCH)
                || stack.isOf(Items.REDSTONE_TORCH)
                || stack.isOf(Items.COPPER_TORCH)
                || stack.isOf(Items.WATER_BUCKET);
    }
}