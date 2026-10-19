package omar.projects.interactivestuff.handlers.config;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
public final class SettingsKeyBinding {
    private SettingsKeyBinding() {}
    public static KeyMapping create() {
        return new KeyMapping("key.interactivestuff.settings", InputConstants.UNKNOWN.getValue(),
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("interactivestuff", "settings")));
    }
}
