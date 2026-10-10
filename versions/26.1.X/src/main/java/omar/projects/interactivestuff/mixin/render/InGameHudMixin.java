package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(
            method = "extractRenderState",
            at = @At("TAIL")
    )
    private void debugText(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        var texts = DebugTextHandler.getRenderedTexts();
        if (texts.isEmpty()) return;

        int y = 10;
        for (final String string : texts) {
            graphics.text(this.minecraft.font, string, 10, y, 0xFFFFFFFF, false);
            y += 10;
        }
    }
}