package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void interactivestuff$beginFrame(CallbackInfo ci) {
        omar.projects.interactivestuff.render.ModelDebugMarkers.clear();
        RenderTickHandler.beginFrame(Minecraft.getInstance().isPaused());
        omar.projects.interactivestuff.particles.ParticleSystem.INSTANCE.beginFrame(RenderTickHandler.normalizedDelta / 3.0);
    }
}
