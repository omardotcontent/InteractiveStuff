package omar.projects.interactivestuff.mixin.render;

import net.fabricmc.fabric.mixin.client.rendering.InGameHudAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            at = @At("TAIL")
    )
    private void debugText(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        context.drawText(getTextRenderer(), "InteractiveStuff: Hello", 10, 10, 0xFFFFFFFF, false);
    }
}



