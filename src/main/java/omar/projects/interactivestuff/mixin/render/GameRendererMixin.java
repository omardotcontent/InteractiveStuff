package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique
    private float prevTime;


    @Inject(method = "render", at = @At(value = "HEAD"))
    private void deltaTime(final DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        final float currentTime = (float) GLFW.glfwGetTime();
        final float deltaTime = currentTime - prevTime;
        this.prevTime = currentTime;

        if (Minecraft.getInstance().isPaused()) {
            RenderTickHandler.normalizedDelta = 0;
            return;
        }
        RenderTickHandler.normalizedDelta = (float) Math.min(0.05, deltaTime) * 60f;
    }
}