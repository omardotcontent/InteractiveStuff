package omar.projects.interactivestuff.handlers.config;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
public final class SettingsKeyBinding {
    private SettingsKeyBinding() {}
    public static KeyMapping create() {
        return new KeyMapping("key.interactivestuff.settings", InputConstants.UNKNOWN.getValue(), "key.categories.interactivestuff");
    }
}
