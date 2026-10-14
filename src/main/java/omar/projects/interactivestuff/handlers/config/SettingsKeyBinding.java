package omar.projects.interactivestuff.handlers.config;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
public final class SettingsKeyBinding {
    private SettingsKeyBinding() {}
    public static KeyMapping create() {
        return new KeyMapping("key.interactivestuff.settings", -1,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("interactivestuff", "settings")));
    }
}
