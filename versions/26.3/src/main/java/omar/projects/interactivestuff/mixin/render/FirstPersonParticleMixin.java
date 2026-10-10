package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import omar.projects.interactivestuff.render.MinecraftParticleRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonParticleMixin {
    @Inject(method = "submitHandsWithItems", at = @At("TAIL"))
    private void interactivestuff$particles(float partialTicks, PoseStack matrices, SubmitNodeCollector queue,
                                           PlayerRenderState player, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci) {
        MinecraftParticleRenderer.submit(queue);
    }
}
