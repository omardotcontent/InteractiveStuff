package omar.projects.interactivestuff;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import omar.projects.interactivestuff.compat.MinecraftAccess;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;

/** Uses Mod Menu's registered factory and renders the real optional YACL screen. */
public final class ClientConfigScreenChecks implements FabricClientGameTest {
    @Override public void runTest(ClientGameTestContext context) {
        boolean yacl = FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3");
        Screen[] screens = new Screen[2];
        context.runOnClient(client -> {
            check(FabricLoader.getInstance().isModLoaded("modmenu"), "Mod Menu is installed in the client fixture");
            check(ModMenu.hasConfigScreen("interactivestuff"), "Mod Menu registered the IS configuration factory");
            Screen parent = new ModsScreen(new TitleScreen());
            screens[0] = parent;
            // Suppress opening an external file manager in the missing-YACL test.
            ConfigHandler original = ConfigHandler.INSTANCE;
            ConfigHandler.INSTANCE = null;
            try {
                Screen config = ModMenu.getConfigScreen("interactivestuff", parent);
                screens[1] = config;
                if (!yacl) {
                    check(config == parent, "missing YACL keeps the parent screen without loading YACL classes");
                    check(ConfigHandler.INSTANCE == null, "missing YACL does not construct a YACL configuration");
                    return;
                }
                check(config != null && config != parent, "installed YACL opens a configuration screen");
                check(config.getTitle().getString().equals("InteractiveStuff Config"), "the configuration screen belongs to IS");
                MinecraftAccess.openScreen(client, config);
                check(MinecraftAccess.screen(client) == config, "the native client opens the configuration screen");
            } finally {
                ConfigHandler.INSTANCE = original;
            }
        });
        if (yacl) {
            context.waitTicks(3);
            context.takeScreenshot("interactivestuff-config");
            context.runOnClient(client -> {
                check(MinecraftAccess.screen(client) == screens[1], "the YACL screen survives native initialization and rendering");
                screens[1].onClose();
                check(MinecraftAccess.screen(client) == screens[0], "closing settings returns to Mod Menu");
            });
        }
        context.runOnClient(client -> MinecraftAccess.openScreen(client, new TitleScreen()));
        IS.LOGGER.info("InteractiveStuff Mod Menu configuration checks passed (YACL installed: {}).", yacl);
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
