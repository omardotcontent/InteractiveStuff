package omar.projects.interactivestuff.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import omar.projects.interactivestuff.ISComponents;
import omar.projects.interactivestuff.handlers.VibrationTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public final class HeldItemRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private ItemStack modifyRenderStack(final ItemStack stack) {
        final boolean isSculk = stack.isOf(Items.SCULK_SENSOR) || stack.isOf(Items.CALIBRATED_SCULK_SENSOR);
        if (isSculk && VibrationTracker.isVibrating()) {
            final ItemStack visualStack = stack.copy();
            visualStack.set(ISComponents.VIBRATING, true);
            return visualStack;
        }

        final boolean isCampfire = stack.isOf(Items.CAMPFIRE) || stack.isOf(Items.SOUL_CAMPFIRE) || stack.isOf(Items.TORCH) || stack.isOf(Items.SOUL_TORCH) || stack.isOf(Items.COPPER_TORCH);
        if (isCampfire && this.client.player != null) {
            final ItemStack visualStack = stack.copy();
            visualStack.set(ISComponents.WATERLOGGED, this.client.player.isSubmergedInWater());
            return visualStack;
        }

        return stack;
    }
}