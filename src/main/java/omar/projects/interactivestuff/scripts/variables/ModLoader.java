package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import omar.projects.interactivestuff.compat.MinecraftAccess;

@VynType(name = "ModLoader")
public final class ModLoader {

    private final FabricLoader loader;

    public ModLoader(final FabricLoader loader) {
        this.loader = loader;
    }

    @VynFunc
    public boolean isModLoaded(final String modid) {
        return loader.isModLoaded(modid);
    }

    @VynFunc
    public static boolean isResourcePackLoaded(final String packName) {
        final Minecraft client = Minecraft.getInstance();

        return client.getResourcePackRepository()
                .getSelectedPacks()
                .stream()
                .anyMatch(profile -> profile.getId().equalsIgnoreCase(packName));
    }


    @VynFunc
    public String getRawGameVersion() {
        return MinecraftAccess.gameVersion();
    }

}
