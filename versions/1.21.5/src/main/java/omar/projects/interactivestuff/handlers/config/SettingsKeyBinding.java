package omar.projects.interactivestuff.handlers.config;
import net.minecraft.client.KeyMapping;
public final class SettingsKeyBinding {
    private SettingsKeyBinding() {}
    public static KeyMapping create() {
        return new KeyMapping("key.interactivestuff.settings", -1, "key.categories.interactivestuff");
    }
}
