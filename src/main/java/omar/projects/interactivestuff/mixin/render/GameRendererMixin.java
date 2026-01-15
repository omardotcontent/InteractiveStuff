package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private float prevTime;

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void deltaTime(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        float currentTime = (float) GLFW.glfwGetTime();
        float deltaTime = currentTime - prevTime;
        prevTime = currentTime;

        if (MinecraftClient.getInstance().isPaused()) {
            RenderTickHandler.normalizedDelta = 0;
        } else {
            RenderTickHandler.normalizedDelta = (float) Math.min(0.05, deltaTime) * 60f;
        }
    }
}