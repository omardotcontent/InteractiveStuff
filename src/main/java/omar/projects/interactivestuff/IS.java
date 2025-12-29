package omar.projects.interactivestuff;


import net.fabricmc.api.ModInitializer;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;


public final class IS implements ModInitializer {

    public static final String MOD_ID = "interactivestuff";


    @Override
    public void onInitialize() {
        ConfigHandler.INSTANCE = ConfigHandler.load();
        ISComponents.init();
    }
}
