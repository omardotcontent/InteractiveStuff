package omar.projects.interactivestuff;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;


public final class IS implements ModInitializer {

    public static final String MOD_ID = "interactivestuff";


    @Override
    public void onInitialize() {
        ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.of(MOD_ID, "interactive_resourcepack"),
                FabricLoader.getInstance()
                        .getModContainer(MOD_ID)
                        .orElseThrow(),
                ResourcePackActivationType.DEFAULT_ENABLED
        );
    }
}
