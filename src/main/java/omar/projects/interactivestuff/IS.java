package omar.projects.interactivestuff;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class IS implements ModInitializer {

    public static final String MOD_ID = "interactivestuff";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        ConfigHandler.INSTANCE = ConfigHandler.load();
        omar.projects.interactivestuff.config.PackOptions.bind(ConfigHandler.INSTANCE.packOptionValues);
        ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.fromNamespaceAndPath(MOD_ID, "interactive_resourcepack"),
                FabricLoader.getInstance()
                        .getModContainer(MOD_ID)
                        .orElseThrow(),
                ResourcePackActivationType.DEFAULT_ENABLED
        );
    }
}
