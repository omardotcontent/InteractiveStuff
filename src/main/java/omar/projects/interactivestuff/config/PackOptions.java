package omar.projects.interactivestuff.config;

import omar.projects.interactivestuff.scripts.variables.ConfigOption;
import java.util.*;

/** Registry is replaced on reload; player values survive even when a pack is absent. Client-thread only. */
public final class PackOptions {
    private static final Map<String, Map<String, List<ConfigOption>>> definitions = new LinkedHashMap<>();
    private static Map<String, Map<String, Object>> values = new LinkedHashMap<>();
    private PackOptions() {}
    public static void beginRegistration() { definitions.clear(); }
    public static void register(ConfigOption option) {
        Map<String, List<ConfigOption>> pack = definitions.computeIfAbsent(option.owner.getPackId(), ignored -> new LinkedHashMap<>());
        if (pack.size() >= 256 && !pack.containsKey(option.id)) throw new IllegalArgumentException("A pack may declare at most 256 options");
        pack.computeIfAbsent(option.id, ignored -> new ArrayList<>()).add(option);
    }
    public static void finishRegistration() {
        for (var pack : definitions.values()) for (var bindings : pack.values()) {
            List<Object> schema = null;
            for (ConfigOption option : bindings) if (option.active()) {
                try {
                    var next = option.schema();
                    if (schema != null && !schema.equals(next)) throw new IllegalArgumentException("Conflicting declarations for option " + option.id);
                    schema = next;
                    if (!option.onChange.isEmpty() && !option.owner.getEnvironment().hasFunction(option.onChange))
                        throw new IllegalArgumentException("Missing config callback task: " + option.onChange);
                } catch (Exception error) { option.owner.fail("config declaration", error); }
            }
        }
    }
    public static List<ConfigOption> options() {
        List<ConfigOption> result = new ArrayList<>();
        for (var pack : definitions.values()) for (var bindings : pack.values())
            bindings.stream().filter(ConfigOption::active).findFirst().ifPresent(result::add);
        return List.copyOf(result);
    }
    public static void bind(Map<String, Map<String, Object>> saved) {
        values = copy(saved);
        LegacyPackOptions.upgradeBundledValues(values);
    }
    public static Map<String, Map<String, Object>> snapshot() { return copy(values); }
    public static Map<String, Map<String, Object>> copy(Map<String, Map<String, Object>> source) {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        if (source != null) source.forEach((pack, settings) -> { if (settings != null) result.put(pack, new LinkedHashMap<>(settings)); });
        return result;
    }
    public static Object value(ConfigOption option) { return value(option, values); }
    /** Compatibility for older scripts: look up the declared toggle in their own source pack. */
    public static boolean toggleValue(String packId, String id, boolean fallback) {
        for (ConfigOption option : definitions.getOrDefault(packId, Map.of()).getOrDefault(id, List.of()))
            if (option.active() && option.type.equals("toggle")) return (Boolean) value(option);
        return fallback;
    }
    public static Object value(ConfigOption option, Map<String, Map<String, Object>> source) {
        Object raw = source.getOrDefault(option.owner.getPackId(), Map.of()).getOrDefault(option.id, option.defaultValue);
        try { return option.normalize(raw); }
        catch (IllegalArgumentException invalidSavedValue) { return option.normalize(option.defaultValue); }
    }
    public static void put(Map<String, Map<String, Object>> draft, ConfigOption option, Object value) {
        draft.computeIfAbsent(option.owner.getPackId(), ignored -> new LinkedHashMap<>()).put(option.id, option.normalize(value));
    }
    public static Map<String, Map<String, Object>> validated(Map<String, Map<String, Object>> draft) {
        var next = copy(draft);
        for (ConfigOption option : options()) put(next, option, value(option, draft));
        return next;
    }
    /** Must follow successful persistence. Importers each receive their own callback once. */
    public static void commit(Map<String, Map<String, Object>> saved) {
        var previous = values;
        values = validated(saved);
        for (var pack : definitions.values()) for (var bindings : pack.values()) {
            Set<Object> called = Collections.newSetFromMap(new IdentityHashMap<>());
            for (ConfigOption option : List.copyOf(bindings)) if (option.active() && called.add(option.owner)) {
                Object old = value(option, previous), next = value(option, values);
                if (!old.equals(next) && !option.onChange.isEmpty()) option.owner.callTask(option.onChange, option, old, next);
            }
        }
    }
}
