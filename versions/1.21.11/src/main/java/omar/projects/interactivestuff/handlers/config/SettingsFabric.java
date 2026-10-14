package omar.projects.interactivestuff.handlers.config;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
public final class SettingsFabric {
    private SettingsFabric() {}
    public static KeyMapping registerKey() { return KeyBindingHelper.registerKeyBinding(SettingsKeyBinding.create()); }
    public static void replaceButton(Screen screen, Button previous, Button button) {
        var widgets = Screens.getButtons(screen);
        if (previous != null) widgets.remove(previous);
        widgets.add(button);
    }
}
