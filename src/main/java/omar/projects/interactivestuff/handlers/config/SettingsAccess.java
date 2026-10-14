package omar.projects.interactivestuff.handlers.config;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import omar.projects.interactivestuff.compat.MinecraftAccess;
import omar.projects.interactivestuff.objects.InteractionMaterial;
import java.util.Map;
import java.util.WeakHashMap;

public final class SettingsAccess {
    // Newer vanilla Options screens retain widgets when resize only repositions their layout.
    private static final Map<Screen, Button> optionsButtons = new WeakHashMap<>();
    private SettingsAccess() {}
    public static NativeSettingsScreen create(Screen parent) { return new NativeSettingsScreen(parent); }
    public static void open(Minecraft client) { MinecraftAccess.openScreen(client, create(MinecraftAccess.screen(client))); }
    public static void register() {
        var key = SettingsFabric.registerKey();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (key.consumeClick()) if (MinecraftAccess.screen(client) == null) open(client);
        });
        // Mod Menu supplies the settings entry; keep Options as the standalone fallback.
        if (FabricLoader.getInstance().isModLoaded("modmenu")) return;
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (SettingsScreenKind.isOptions(screen)) {
                Button button = Button.builder(SettingsText.component("title"), ignored -> open(client))
                        .bounds(width - 114, 6, 108, 20).build();
                button.setTooltip(Tooltip.create(SettingsText.component("entry.tooltip")));
                SettingsFabric.replaceButton(screen, optionsButtons.put(screen, button), button);
            }
        });
    }
    public static void preview(InteractionMaterial material) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(material.getSoundEvent(), material.pitch, material.volume));
    }
}
