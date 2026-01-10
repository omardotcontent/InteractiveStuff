package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow public abstract TextRenderer getTextRenderer();

    @Unique
    private final DebugTextHandler debugTextHandler = DebugTextHandler.INSTANCE;

    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            at = @At("TAIL")
    )
    private void debugText(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(!debugTextHandler.getRenderedTexts().isEmpty()) {
            int y = 10;
            for (final String string : debugTextHandler.getRenderedTexts()) {
                context.drawText(getTextRenderer(), string, 10, y, 0xFFFFFFFF, false);
                y += 10;
            }
        }
    }
}