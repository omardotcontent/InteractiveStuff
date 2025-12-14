package omar.projects.interactivestuff.mixin.locomotion;

import com.trainguy9512.locomotion.render.FirstPersonBlockItemRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Arm;
import omar.projects.interactivestuff.ISComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonBlockItemRenderer.class)
public abstract class LocomotionBlockItemMixin {

    @Unique
    private static final ThreadLocal<ItemStack> CURRENT_STACK = new ThreadLocal<>();

    @Inject(
            method = "submit",
            at = @At("HEAD"),
            remap = false,
            require = 0
    )
    private static void captureStackContext(
            ItemStack stack,
            MatrixStack poseStack,
            OrderedRenderCommandQueue nodeCollector,
            int combinedLight,
            Arm side,
            CallbackInfo ci
    ) {
        CURRENT_STACK.set(stack);
    }

    @Inject(
            method = "submit",
            at = @At("RETURN"),
            remap = false
    )
    private static void releaseStackContext(CallbackInfo ci) {
        CURRENT_STACK.remove();
    }

    @ModifyVariable(
            method = "submit",
            at = @At(value = "STORE"),
            ordinal = 0,
            remap = false
    )
    private static BlockState modifyLocalBlockState(BlockState originalState) {
        ItemStack stack = CURRENT_STACK.get();
        if (stack == null) return originalState;

        // Handle Waterlogged blocks
        if (originalState.contains(Properties.WATERLOGGED) && stack.contains(ISComponents.WATERLOGGED)) {
            Boolean isWaterlogged = stack.get(ISComponents.WATERLOGGED);
            if (isWaterlogged != null && isWaterlogged) {
                return originalState.with(Properties.WATERLOGGED, true);
            }
        }

        // Handle Sculk Sensor
        if (stack.isOf(Items.SCULK_SENSOR) && stack.contains(ISComponents.VIBRATING)) {
            Boolean isVibrating = stack.get(ISComponents.VIBRATING);
            if (isVibrating != null && isVibrating && originalState.contains(Properties.SCULK_SENSOR_PHASE)) {
                return originalState.with(Properties.SCULK_SENSOR_PHASE,
                        net.minecraft.block.enums.SculkSensorPhase.ACTIVE);
            }
        }

        // Handle Calibrated Sculk Sensor
        if (stack.isOf(Items.CALIBRATED_SCULK_SENSOR) && stack.contains(ISComponents.CALIBRATED_VIBRATING)) {
            Boolean isVibrating = stack.get(ISComponents.CALIBRATED_VIBRATING);
            if (isVibrating != null && isVibrating && originalState.contains(Properties.SCULK_SENSOR_PHASE)) {
                return originalState.with(Properties.SCULK_SENSOR_PHASE,
                        net.minecraft.block.enums.SculkSensorPhase.ACTIVE);
            }
        }

        return originalState;
    }
}