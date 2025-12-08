package omar.projects.interactivestuff.handlers.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import omar.projects.interactivestuff.objects.InteractionMaterial;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ConfigHandler {

    public static ConfigHandler INSTANCE;

    // GSON setup handles converting Strings ("minecraft:stone") to Item objects automatically
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(Item.class, (JsonSerializer<Item>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(Registries.ITEM.getId(src).toString()))
            .registerTypeHierarchyAdapter(Item.class, (JsonDeserializer<Item>) (json, typeOfT, context) ->
                    Registries.ITEM.get(Identifier.of(json.getAsString())))
            .create();

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("interactivestuff.json");
    private static final String DEFAULT_CONFIG_PATH = "/assets/interactivestuff/default_config.json";

    // Config Fields
    public boolean enableInteractiveHits = true;
    public boolean enableSculkSensorFeature = true;
    public boolean enableNoteBlockCrouchFeature = true;
    public boolean enableTextureChanges = true;
    public int HitCooldownTicks = 4;

    public @NotNull List<Item> excludedBlocks = new ArrayList<>();
    public List<InteractionMaterial> materials = new ArrayList<>();

    // Internal cache
    private transient final Map<Item, InteractionMaterial> itemLookupCache = new HashMap<>();

    public ConfigHandler() {
        // Constructor no longer generates defaults.
        // It initializes empty lists to prevent crashes if config fails.
    }

    public void reload() {
        ConfigHandler loaded = load();
        // Copy loaded values to current instance (or replace instance reference in main class)
        this.enableInteractiveHits = loaded.enableInteractiveHits;
        this.enableSculkSensorFeature = loaded.enableSculkSensorFeature;
        this.enableNoteBlockCrouchFeature = loaded.enableNoteBlockCrouchFeature;
        this.enableTextureChanges = loaded.enableTextureChanges;
        this.HitCooldownTicks = loaded.HitCooldownTicks;
        this.excludedBlocks = loaded.excludedBlocks;
        this.materials = loaded.materials;

        refreshCache();
    }

    public static ConfigHandler load() {
        // 1. Try to load from the user's config folder
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
                final ConfigHandler loaded = GSON.fromJson(reader, ConfigHandler.class);
                if (loaded != null) {
                    loaded.refreshCache();
                    return loaded;
                }
            } catch (Exception e) {
                System.err.println("[InteractiveStuff] Failed to load user config, falling back to defaults.");
                e.printStackTrace();
            }
        }

        // 2. Fallback: Load from the JAR resources (The "Default" file)
        try (InputStream stream = ConfigHandler.class.getResourceAsStream(DEFAULT_CONFIG_PATH)) {
            if (stream == null) {
                throw new RuntimeException("Default config not found in jar at: " + DEFAULT_CONFIG_PATH);
            }

            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                final ConfigHandler defaults = GSON.fromJson(reader, ConfigHandler.class);
                if (defaults != null) {
                    // Save these defaults to the config folder so the user can edit them
                    defaults.save();
                    defaults.refreshCache();
                    return defaults;
                }
            }
        } catch (Exception e) {
            System.err.println("[InteractiveStuff] CRITICAL: Failed to load default configuration from jar!");
            e.printStackTrace();
        }

        // 3. Absolute failsafe (empty config)
        return new ConfigHandler();
    }

    public void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            System.err.println("[InteractiveStuff] Failed to save config.");
            e.printStackTrace();
        }
    }

    public void refreshCache() {
        itemLookupCache.clear();
        if (materials == null) return;

        for (final InteractionMaterial material : materials) {
            if (material == null || material.items == null) continue;

            for (final Item itemId : material.items) {
                if (itemId != null) {
                    itemLookupCache.put(itemId, material);
                }
            }
        }
    }

    public InteractionMaterial getMaterial(final Item item) {
        return itemLookupCache.get(item);
    }

    public boolean isExcluded(final Block block) {
        if (block == null) return false;
        return excludedBlocks.contains(block.asItem());
    }
}