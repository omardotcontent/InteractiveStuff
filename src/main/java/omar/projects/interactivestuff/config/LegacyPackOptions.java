package omar.projects.interactivestuff.config;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

/** One-way migration of old bundled-pack controls; schemas remain declared entirely in Vyn. */
public final class LegacyPackOptions {
    public static final String BUNDLED_PACK = "interactivestuff:interactive_resourcepack";
    private static final Map<String, String> IDS = Map.of(
            "specializedNoteblockHits", "note_block_hits",
            "enableNoteBlockCrouchFeature", "note_block_crouch",
            "enableSculkSensorFeature", "sculk_sensors",
            "enableTextureChanges", "texture_changes",
            "resourcePackDebugMode", "debug_mode",
            "particleDebugCounters", "debug_counters");
    private LegacyPackOptions() {}

    public static void migrate(JsonObject legacy, Map<String, Map<String, Object>> values) {
        IDS.forEach((field, id) -> {
            var value = legacy.get(field);
            if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean())
                values.computeIfAbsent(BUNDLED_PACK, ignored -> new LinkedHashMap<>()).putIfAbsent(id, value.getAsBoolean());
        });
        upgradeBundledValues(values);
    }

    /** Explicit mod choices win; fall back to the short-lived bundled-pack marker settings. */
    public static boolean markerValue(JsonObject legacy, Map<String, Map<String, Object>> values,
                                      String modField, String packOption, boolean fallback) {
        var explicit = legacy.get(modField);
        if (explicit != null && explicit.isJsonPrimitive() && explicit.getAsJsonPrimitive().isBoolean())
            return explicit.getAsBoolean();
        Object previous = values.getOrDefault(BUNDLED_PACK, Map.of()).get(packOption);
        return previous instanceof Boolean flag ? flag : fallback;
    }

    /** Split the old catch-all switches without overwriting newer, individual choices. */
    public static void upgradeBundledValues(Map<String, Map<String, Object>> values) {
        Map<String, Object> pack = values.get(BUNDLED_PACK);
        if (pack == null) return;
        copyToggle(pack, "texture_changes", "tint_blocks", "tint_saplings", "tint_water", "animate_vault", "torch_extinguish");
        copyToggle(pack, "sculk_sensors", "animate_sculk_sensor");
        copyToggle(pack, "sculk_particles", "particles_sculk_sensor");
        if (pack.get("note_block_hits") instanceof Boolean || pack.get("note_block_crouch") instanceof Boolean) {
            // Keep the combined sound switch off if either former sound action was disabled.
            boolean hits = !Boolean.FALSE.equals(pack.get("note_block_hits"));
            boolean crouch = !Boolean.FALSE.equals(pack.get("note_block_crouch"));
            pack.putIfAbsent("note_block_sounds", hits && crouch);
        }
    }

    private static void copyToggle(Map<String, Object> pack, String oldId, String... newIds) {
        if (pack.get(oldId) instanceof Boolean value)
            for (String id : newIds) pack.putIfAbsent(id, value);
    }
}
