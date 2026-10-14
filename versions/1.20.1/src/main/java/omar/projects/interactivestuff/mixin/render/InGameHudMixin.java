package omar.projects.interactivestuff.mixin.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Gui.class)
public abstract class InGameHudMixin {
 @Shadow @Final private Minecraft minecraft;
 @Inject(method="render",at=@At("TAIL")) private void interactivestuff$debug(GuiGraphics graphics,float partialTicks,CallbackInfo ci){int y=10;for(String text:DebugTextHandler.getRenderedTexts()){graphics.drawString(minecraft.font,text,10,y,0xFFFFFFFF,false);y+=10;}}
}
