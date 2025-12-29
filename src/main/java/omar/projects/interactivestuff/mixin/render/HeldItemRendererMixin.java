package omar.projects.interactivestuff.mixin.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import omar.projects.interactivestuff.handlers.ItemVisualHelper;
import omar.projects.interactivestuff.handlers.VibrationTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public final class HeldItemRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyVariable(
            method = "renderFirstPersonItem",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private ItemStack interactivestuff$modifyItem(
            final ItemStack stack,
            final AbstractClientPlayerEntity player
    ) {
        return ItemVisualHelper.applyVisuals(stack, player);
    }


    @Inject(
            method = "renderFirstPersonItem",
            at = @At("HEAD")
    )
    private void vibratingStack(
            final AbstractClientPlayerEntity player,
            final float tickProgress, float pitch,
            final Hand hand,
            final float swingProgress,
            final ItemStack item,
            final float equipProgress,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue orderedRenderCommandQueue,
            final int light,
            final CallbackInfo ci)
    {
        if (item.isOf(Items.SCULK_SENSOR) || item.isOf(Items.CALIBRATED_SCULK_SENSOR)) {
            if (client.isPaused()) return;
            final float shakePower = VibrationTracker.getIntensity();

            final double xOffset = (Math.random() - 0.2D) * shakePower;
            final double yOffset = (Math.random() - 0.2D) * shakePower;

            matrices.translate(xOffset, yOffset, 0);

        }
    }
}