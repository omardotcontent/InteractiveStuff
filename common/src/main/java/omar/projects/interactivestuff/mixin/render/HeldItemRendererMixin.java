package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemInHandRenderer.class, priority = 9999)
public abstract class HeldItemRendererMixin {

    @Inject(
            method = "renderItem",
            at = @At("HEAD"),
            cancellable  = true
    )
    private void IS$renderItem(
            final LivingEntity pEntity,
            final ItemStack pItemStack,
            final ItemDisplayContext pDisplayContext,
            final boolean pLeftHand,
            final PoseStack pPoseStack,
            final MultiBufferSource pBuffer,
            final int pSeed, CallbackInfo ci) {
        System.out.println(pItemStack.getDisplayName().getString());


    }


}
