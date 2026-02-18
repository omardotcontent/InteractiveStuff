package omar.projects.interactivestuff.handlers.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import omar.projects.interactivestuff.gui.InteractiveStuffConfigScreen;

public final class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return InteractiveStuffConfigScreen::new;
    }
}