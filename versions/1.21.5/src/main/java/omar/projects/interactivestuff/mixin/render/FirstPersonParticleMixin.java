package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import omar.projects.interactivestuff.render.MinecraftParticleRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class FirstPersonParticleMixin {
    @Inject(method = "renderHandsWithItems", at = @At("TAIL"))
    private void interactivestuff$particles(float partialTicks, PoseStack matrices, MultiBufferSource.BufferSource queue,
                                           LocalPlayer player, int light, CallbackInfo ci) {
        MinecraftParticleRenderer.submit(queue);
    }
}
