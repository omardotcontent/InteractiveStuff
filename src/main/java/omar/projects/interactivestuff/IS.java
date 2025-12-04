package omar.projects.interactivestuff;

import net.fabricmc.api.ModInitializer;


public final class IS implements ModInitializer {

    @Override
    public void onInitialize() {
       ISComponents.init();
    }

}
