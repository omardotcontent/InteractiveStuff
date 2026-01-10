package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.fabricmc.loader.api.FabricLoader;

@VynType(name = "ModLoader")
public class ModLoader {

    private final FabricLoader loader;

    public ModLoader(FabricLoader loader) {
        this.loader = loader;
    }

    @VynFunc
    public boolean isModLoaded(String modid) {
        return loader.isModLoaded(modid);
    }

    @VynFunc
    public String getRawGameVersion() {
        return loader.getRawGameVersion();
    }

}
