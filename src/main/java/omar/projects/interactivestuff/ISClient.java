package omar.projects.interactivestuff;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import omar.projects.interactivestuff.handlers.*;

@Environment(EnvType.CLIENT)
public final class ISClient implements ClientModInitializer {



    @Override
    public void onInitializeClient() {
        BackgroundLoopHandler.getInstance().register();
        NoteBlockSoundHandler.register();
        CampAndTorchHandler.register();
        SculkHandler.register();
    }



}