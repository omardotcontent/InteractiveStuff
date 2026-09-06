package omar.projects.interactivestuff;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import omar.projects.interactivestuff.handlers.BackgroundLoopHandler;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.ScriptLoader;

@Environment(EnvType.CLIENT)
public final class ISClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BackgroundLoopHandler.getInstance().register();
        ScriptInterpreter.register();
        ScriptLoader.register();
    }

}