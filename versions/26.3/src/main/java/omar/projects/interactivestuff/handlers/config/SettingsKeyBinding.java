package omar.projects.interactivestuff.handlers.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class SettingsKeyBinding {
    private SettingsKeyBinding() {}

    public static KeyMapping create() {
        return new KeyMapping("key.interactivestuff.settings", InputConstants.UNKNOWN.getValue(),
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("interactivestuff", "settings"))) {
            @Override
            public void setKey(InputConstants.Key key) {
                // Earlier IS builds saved GLFW's -1; SDL uses 0 for an unbound keyboard key.
                // Normalize this binding when Options loads it, before native focus polling.
                super.setKey(key.getType() == InputConstants.Type.KEYBOARD && key.getValue() == -1
                        ? InputConstants.UNKNOWN : key);
            }
        };
    }
}
