package omar.projects.interactivestuff.handlers.config;

import java.util.*;
import omar.projects.interactivestuff.config.LegacyPackOptions;
import omar.projects.interactivestuff.scripts.variables.ConfigOption;

/** Navigation uses pack/category IDs; labels may be translated or shared by unrelated packs. */
public final class PackSettingsNavigation {
    private List<ConfigOption> options = List.of();
    private final Map<String, Map<String, List<ConfigOption>>> packs = new LinkedHashMap<>();
    private String pack, category;

    public void refresh(List<ConfigOption> declarations) {
        options = declarations.stream().sorted(Comparator.comparing((ConfigOption option) -> option.owner.getPackId())
                .thenComparingInt(PackSettingsNavigation::categoryOrder).thenComparing(option -> option.category)
                .thenComparingInt(PackSettingsNavigation::optionOrder)).toList();
        packs.clear();
        for (ConfigOption option : options)
            packs.computeIfAbsent(option.owner.getPackId(), ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(option.category, ignored -> new ArrayList<>()).add(option);
        if (!packs.containsKey(pack)) { pack = null; category = null; }
        else if (!packs.get(pack).containsKey(category)) category = null;
    }
    public String pack() { return pack; }
    public String category() { return category; }
    public List<String> packs() { return List.copyOf(packs.keySet()); }
    public List<String> categories(String packId) { return List.copyOf(packs.getOrDefault(packId, Map.of()).keySet()); }
    public List<ConfigOption> allOptions() { return options; }
    public List<ConfigOption> options(String packId, String categoryId) {
        var categories = packs.getOrDefault(packId, Map.of());
        return categoryId == null ? categories.values().stream().flatMap(List::stream).toList()
                : List.copyOf(categories.getOrDefault(categoryId, List.of()));
    }
    public List<ConfigOption> selectedOptions() { return options(pack, category); }
    /** Missing targets can disappear during a resource reload; return to a valid parent. */
    public void open(String packId, String categoryId) {
        pack = packs.containsKey(packId) ? packId : null;
        category = pack != null && packs.get(pack).containsKey(categoryId) ? categoryId : null;
    }
    private static int categoryOrder(ConfigOption option) {
        if (!option.owner.getPackId().equals(LegacyPackOptions.BUNDLED_PACK)) return 0;
        return switch (option.category) {
            case "interactive_stuff.config.menu.pack.category.appearance" -> 0;
            case "interactive_stuff.config.menu.pack.category.physics" -> 1;
            case "interactive_stuff.config.menu.pack.category.animation" -> 2;
            case "interactive_stuff.config.menu.pack.category.particles" -> 3;
            case "interactive_stuff.config.menu.pack.category.sounds" -> 4;
            case "interactive_stuff.config.menu.pack.category.debug" -> 5;
            default -> 6;
        };
    }
    private static int optionOrder(ConfigOption option) {
        return option.owner.getPackId().equals(LegacyPackOptions.BUNDLED_PACK)
                && (option.id.equals("physics_enabled") || option.id.equals("shake_intensity")) ? -1 : 0;
    }

}
