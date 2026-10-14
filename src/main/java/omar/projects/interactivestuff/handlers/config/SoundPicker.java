package omar.projects.interactivestuff.handlers.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.google.gson.JsonParser;
import java.util.*;

/** Registered sound names are searchable; selecting never requires typing a sound ID. */
public final class SoundPicker {
    public record Entry(String id, String name) {}
    private static Map<String, String> subtitles;
    public static void clearCache() { subtitles = null; }
    private static Map<String, String> subtitles() {
        if (subtitles != null) return subtitles;
        Map<String, String> loaded = new HashMap<>();
        var resources = Minecraft.getInstance().getResourceManager();
        for (String namespace : resources.getNamespaces()) {
            for (var resource : resources.getResourceStack(Identifier.parse(namespace + ":sounds.json"))) {
                try (var reader = resource.openAsReader()) {
                    var definitions = JsonParser.parseReader(reader).getAsJsonObject();
                    for (var entry : definitions.entrySet()) {
                        if (!entry.getValue().isJsonObject()) continue;
                        var definition = entry.getValue().getAsJsonObject();
                        if (definition.has("replace") && definition.get("replace").getAsBoolean()) loaded.remove(namespace + ":" + entry.getKey());
                        if (definition.has("subtitle")) loaded.put(namespace + ":" + entry.getKey(), definition.get("subtitle").getAsString());
                    }
                } catch (Exception ignored) { /* Invalid sound resources retain their searchable registry name. */ }
            }
        }
        return subtitles = Map.copyOf(loaded);
    }
    private final List<Entry> catalog = BuiltInRegistries.SOUND_EVENT.keySet().stream().map(id -> {
        return new Entry(id.toString(), name(id.toString()));
    }).sorted(Comparator.comparing(Entry::name).thenComparing(Entry::id)).toList();
    private List<Entry> matches = catalog;
    private String query = "";
    public void filter(String text) {
        query = text;
        String needle = text.strip();
        matches = catalog.stream().filter(entry -> SettingsText.matches(entry.name + " " + entry.id + " " + entry.id.replace('.', ' ').replace('_', ' '), needle)).toList();
    }
    public List<Entry> matches() { return matches; }
    public String query() { return query; }
    public static String name(String id) {
        String subtitle = subtitles().get(id);
        if (subtitle != null && net.minecraft.locale.Language.getInstance().has(subtitle)) return Component.translatable(subtitle).getString();
        String path = id.substring(id.indexOf(':') + 1).replace('.', ' ').replace('_', ' ');
        return path.isEmpty() ? id : path.substring(0, 1).toUpperCase(Locale.ROOT) + path.substring(1);
    }
}
