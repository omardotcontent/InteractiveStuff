package omar.projects.interactivestuff.handlers.config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
public final class SettingsScreenKind {
    private SettingsScreenKind() {}
    public static boolean isOptions(Screen screen) { return screen instanceof OptionsScreen; }
}
