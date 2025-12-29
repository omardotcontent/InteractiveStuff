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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public final class HeldItemRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

	@ModifyArg(
			method = "renderFirstPersonItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"
			),
			index = 1
	)
	private ItemStack modifyRenderedStack(ItemStack stack) {
		return ItemVisualHelper.applyVisuals(stack, client.player);
	}



	@Inject(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V")
    )
    private void interactivestuff$modifyItem(
            final AbstractClientPlayerEntity player,
            final float tickProgress, float pitch,
            final Hand hand,
            final float swingProgress,
            final ItemStack item,
            final float equipProgress,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue orderedRenderCommandQueue,
            final int light,
            final CallbackInfo ci) {
        if (! shouldShake(item)) return;

        float shakePower = VibrationTracker.getIntensity();
        matrices.push();
        matrices.translate(
                (Math.random() - 0.5D) * shakePower,
                (Math.random() - 0.5D) * shakePower,
                0
        );
    }


    @Inject(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
                    shift = org.spongepowered.asm.mixin.injection.At.Shift.AFTER)
    )
    private void afterVibratingStack(
            final AbstractClientPlayerEntity player,
            final float tickProgress, float pitch,
            final Hand hand,
            final float swingProgress,
            final ItemStack item,
            final float equipProgress,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue orderedRenderCommandQueue,
            final int light,
            final CallbackInfo ci) {
        if (! shouldShake(item)) return;
        matrices.pop();
    }

    @Unique
    private boolean shouldShake(ItemStack item) {
        return ! client.isPaused()
                && (item.isOf(Items.SCULK_SENSOR)
                || item.isOf(Items.CALIBRATED_SCULK_SENSOR));
    }

}