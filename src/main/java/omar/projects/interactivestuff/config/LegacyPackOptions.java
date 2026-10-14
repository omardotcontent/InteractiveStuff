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
            "enableTextureChanges", "texture_changes");
    private LegacyPackOptions() {}

    public static void migrate(JsonObject legacy, Map<String, Map<String, Object>> values) {
        IDS.forEach((field, id) -> {
            var value = legacy.get(field);
            if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean())
                values.computeIfAbsent(BUNDLED_PACK, ignored -> new LinkedHashMap<>()).putIfAbsent(id, value.getAsBoolean());
        });
    }
}
