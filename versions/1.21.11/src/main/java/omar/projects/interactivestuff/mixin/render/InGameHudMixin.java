package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
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
            method = "render",
            at = @At("TAIL")
    )
    private void debugText(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (DebugTextHandler.getRenderedTexts().isEmpty() || !ConfigHandler.INSTANCE.resourcePackDebugMode) return;

        int y = 10;
        for (final String string : DebugTextHandler.getRenderedTexts()) {
            graphics.drawString(this.minecraft.font, string, 10, y, 0xFFFFFFFF, false);
            y += 10;
        }
    }
}