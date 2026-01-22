package omar.projects.interactivestuff.mixin.client;

import net.minecraft.client.MinecraftClient;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickEnd(final CallbackInfo ci) {
        if (DebugTextHandler.INSTANCE == null) {
            return;
        }
        DebugTextHandler.INSTANCE.onTickEnd();
    }
}