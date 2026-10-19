package omar.projects.interactivestuff.handlers.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import omar.projects.interactivestuff.objects.InteractionMaterial;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import omar.projects.interactivestuff.IS;
import java.util.*;

public final class ConfigHandler {

    public static ConfigHandler INSTANCE;


    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(Item.class, (JsonSerializer<Item>) (src, ignoredType, ignoredContext) ->
                    new JsonPrimitive(BuiltInRegistries.ITEM.getKey(src).toString()))
            .registerTypeHierarchyAdapter(Item.class, (JsonDeserializer<Item>) (json, typeOfT, context) -> {
                final String idStr = json.getAsString();
                final Identifier id = Identifier.parse(idStr);
                Item item = omar.projects.interactivestuff.compat.MinecraftRegistries.item(id);
                if (item == null) System.err.println("[InteractiveStuff] Unknown item id in config, skipping: " + idStr);
                return item;
            }).create();

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("interactivestuff.json");
    private static final String DEFAULT_CONFIG_PATH = "/default_config.json";


    public boolean enableInteractiveHits = true;
    public int HitCooldownTicks = 4;

    public boolean resourcePackMatrixEditing = true;
    public boolean resourcePackColorChanging = true;
    public boolean menuAnimations = true;
    public boolean pivotMarkers = false;
    public boolean particleEmitterMarkers = false;
    public boolean debugMarkersThroughItems = true;
    public Map<String, Map<String, Object>> packOptionValues = new LinkedHashMap<>();
    public boolean enableParticles = true;
    public int particleDensity = 100;
    public int particleLimit = 512;

    public @NotNull List<Item> excludedBlocks = new ArrayList<>();
    public List<InteractionMaterial> materials = new ArrayList<>();

    private transient final Map<Item, InteractionMaterial> itemLookupCache = new HashMap<>();

    public ConfigHandler() {
    }

    public void reload() {
        final ConfigHandler loaded = load();

        copyFrom(loaded);
        omar.projects.interactivestuff.config.PackOptions.commit(packOptionValues);
    }

    /** Gson uses the same item adapters for drafts as it does for the on-disk format. */
    public ConfigHandler copy() { return GSON.fromJson(GSON.toJson(this), ConfigHandler.class); }

    public void copyFrom(ConfigHandler source) {
        ConfigHandler copy = source.copy();
        try {
            for (var field : ConfigHandler.class.getFields())
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) field.set(this, field.get(copy));
        } catch (IllegalAccessException impossible) { throw new IllegalStateException(impossible); }
        refreshCache();
    }

    public void openConfigDirectory() {
        try {
            omar.projects.interactivestuff.compat.MinecraftAccess.openDirectory(FabricLoader.getInstance().getConfigDir());
        } catch (final Exception e) {
            System.err.println("[InteractiveStuff] Failed to open config.");
            e.printStackTrace();
        }
    }


    public static ConfigHandler load() {
        if (Files.exists(CONFIG_PATH)) {
            try (final Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
                final ConfigHandler loaded = readConfig(reader);
                if (loaded != null) {
                    loaded.refreshCache();
                    return loaded;
                }
            } catch (final Exception e) {
                System.err.println("[InteractiveStuff] Failed to load user config, falling back to defaults.");
                e.printStackTrace();
            }
        }

        final ConfigHandler defaults = loadDefaults();
        if (defaults != null) {
            if (!Files.exists(CONFIG_PATH)) defaults.save();
            return defaults;
        }

        return new ConfigHandler();
    }

    public static ConfigHandler loadDefaults() {
        try (final InputStream stream = ConfigHandler.class.getResourceAsStream(DEFAULT_CONFIG_PATH)) {
            if (stream == null) {
                System.err.println("[InteractiveStuff] Default config not found in jar at: " + DEFAULT_CONFIG_PATH);
                return null;
            }

            try (final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                final ConfigHandler defaults = readConfig(reader);
                if (defaults != null) {
                    defaults.refreshCache();
                    return defaults;
                }
            }
        } catch (final Exception e) {
            System.err.println("[InteractiveStuff] Failed to load default configuration from jar!");
            e.printStackTrace();
        }

        return null;
    }

    private static ConfigHandler readConfig(Reader reader) {
        JsonElement json = JsonParser.parseReader(reader);
        final ConfigHandler loaded = GSON.fromJson(json, ConfigHandler.class);
        if (loaded != null && json.isJsonObject()) {
            if (loaded.packOptionValues == null) loaded.packOptionValues = new LinkedHashMap<>();
            omar.projects.interactivestuff.config.LegacyPackOptions.migrate(json.getAsJsonObject(), loaded.packOptionValues);
            var legacy = json.getAsJsonObject();
            loaded.pivotMarkers = omar.projects.interactivestuff.config.LegacyPackOptions.markerValue(
                    legacy, loaded.packOptionValues, "pivotMarkers", "debug_mode", false);
            loaded.particleEmitterMarkers = omar.projects.interactivestuff.config.LegacyPackOptions.markerValue(
                    legacy, loaded.packOptionValues, "particleEmitterMarkers", "debug_emitters", false);
            loaded.debugMarkersThroughItems = omar.projects.interactivestuff.config.LegacyPackOptions.markerValue(
                    legacy, loaded.packOptionValues, "debugMarkersThroughItems", "debug_markers_through_items", true);
        }
        return loaded;
    }

    public void save() {
        try { saveChecked(); } catch (Exception error) { IS.LOGGER.error("Failed to save config", error); }
    }

    /** A screen can report failure and retain its draft instead of closing as if saved. */
    public void saveChecked() throws java.io.IOException {
        refreshCache();
        Path temporary = null;
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            temporary = Files.createTempFile(CONFIG_PATH.getParent(), "interactivestuff-", ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
            try {
                Files.move(temporary, CONFIG_PATH, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); }
                catch (Exception error) { IS.LOGGER.warn("Failed to remove temporary config", error); }
            }
        }
    }

    public void refreshCache() {
        itemLookupCache.clear();
        if (packOptionValues == null) packOptionValues = new LinkedHashMap<>();
        HitCooldownTicks = Math.max(0, HitCooldownTicks);
        particleDensity = Math.max(0, Math.min(200, particleDensity));
        particleLimit = Math.max(0, Math.min(4096, particleLimit));
        if (excludedBlocks == null) excludedBlocks = new ArrayList<>();
        excludedBlocks.removeIf(Objects::isNull);
        if (materials == null) materials = new ArrayList<>();
        materials.removeIf(Objects::isNull);

        for (final InteractionMaterial material : materials) {
            if (material.items == null) material.items = new ArrayList<>();
            material.items.removeIf(Objects::isNull);
            if (material.customPitches == null) material.customPitches = new ArrayList<>();
            material.customPitches.removeIf(entry -> entry == null || entry.item == null || !Float.isFinite(entry.pitch));
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
        if (block == null) {
            return false;
        }
        return excludedBlocks.contains(block.asItem());
    }
}
