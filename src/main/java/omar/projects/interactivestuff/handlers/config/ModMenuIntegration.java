package omar.projects.interactivestuff.handlers.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

/** Keep the optional YACL types out of Mod Menu's entrypoint class loading. */
public final class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (!FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
                if (ConfigHandler.INSTANCE != null) ConfigHandler.INSTANCE.openConfigDirectory();
                return parent;
            }
            return YaclConfigScreen.create(parent);
        };
    }
}
