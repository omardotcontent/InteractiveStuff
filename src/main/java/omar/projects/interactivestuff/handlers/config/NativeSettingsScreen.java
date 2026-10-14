package omar.projects.interactivestuff.handlers.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public final class NativeSettingsScreen extends SettingsScreen {
    public NativeSettingsScreen(Screen parent) { super(parent); }
    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        Painter painter = new Painter() {
            public void fill(int l, int t, int r, int b, int c) { graphics.fill(l, t, r, b, c); }
            public void text(String text, int x, int y, int color) { graphics.text(font, text, x, y, color); }
            public void item(ItemStack stack, int x, int y) { graphics.item(stack, x, y); }
            public void foreground(Runnable draw) { graphics.nextStratum(); draw.run(); }
            public void tooltip(String text, int x, int y) { graphics.setTooltipForNextFrame(font, tooltipLines(text), x, y); }
            public void texture(net.minecraft.resources.Identifier texture, int x, int y, int width, int height) { graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, width, height, 135, 135, 135, 135); }
        };
        drawSurface(painter, mouseX, mouseY);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        drawNavigation(painter);
    }
    @Override public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
        return scrollContent(x, y, vertical) || super.mouseScrolled(x, y, horizontal, vertical);
    }
}
