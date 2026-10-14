package omar.projects.interactivestuff.handlers.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public final class NativeSettingsScreen extends SettingsScreen {
    public NativeSettingsScreen(Screen parent) { super(parent); }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderBackground(graphics, mouseX, mouseY, delta);
        Painter painter = new Painter() {
            public void fill(int l, int t, int r, int b, int c) { graphics.fill(l, t, r, b, c); }
            public void text(String text, int x, int y, int color) { graphics.drawString(font, text, x, y, color); }
            public void item(ItemStack stack, int x, int y) { graphics.renderItem(stack, x, y); }
            public void foreground(Runnable draw) {
                graphics.pose().pushPose();
                try { graphics.pose().translate(0, 0, 300); draw.run(); }
                finally { graphics.pose().popPose(); }
            }
            public void tooltip(String text, int x, int y) { setTooltipForNextRenderPass(tooltipLines(text)); }
            public void texture(net.minecraft.resources.ResourceLocation texture, int x, int y, int width, int height) { graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, texture, x, y, 0, 0, width, height, 135, 135, 135, 135); }
        };
        drawSurface(painter, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, delta);
        drawNavigation(painter);
    }
    // Screen.render calls this virtually; the adapter draws the background before its own surface.
    @Override public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {}
    @Override public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
        return scrollContent(x, y, vertical) || super.mouseScrolled(x, y, horizontal, vertical);
    }
}
