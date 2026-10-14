package omar.projects.interactivestuff.handlers.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import omar.projects.interactivestuff.objects.InteractionMaterial;
import java.util.*;

/** Inventory selections edit only the screen's draft; Apply/Cancel remain owned by the screen. */
public final class MaterialItemPicker {
    public enum Purpose { MATERIAL_ITEMS, ITEM_PITCHES, EXCLUSIONS }
    public record Entry(Item item, ItemStack stack, String id, String name) {}
    private final InteractionMaterial material;
    private final List<Item> items;
    public final Purpose purpose;
    private final Map<Item, Entry> entries = new LinkedHashMap<>();
    private final Set<Item> availableSelection = new LinkedHashSet<>(), selectedSelection = new LinkedHashSet<>();
    private String query = "";
    private List<Entry> matches;
    public int availablePage, selectedPage;

    public MaterialItemPicker(InteractionMaterial material) {
        this(material, material.items, Purpose.MATERIAL_ITEMS);
    }
    public static MaterialItemPicker pitches(InteractionMaterial material) {
        return new MaterialItemPicker(material, null, Purpose.ITEM_PITCHES);
    }
    public static MaterialItemPicker exclusions(List<Item> items) {
        return new MaterialItemPicker(null, items, Purpose.EXCLUSIONS);
    }
    private MaterialItemPicker(InteractionMaterial material, List<Item> items, Purpose purpose) {
        this.material = material; this.items = items; this.purpose = purpose;
        for (Item item : BuiltInRegistries.ITEM) if (item != Items.AIR) {
            ItemStack stack = MenuItems.stack(item);
            entries.put(item, new Entry(item, stack, BuiltInRegistries.ITEM.getKey(item).toString(), Component.translatable(item.getDescriptionId()).getString()));
        }
        filter("");
    }
    public void filter(String text) {
        query = text;
        String needle = text.strip();
        matches = entries.values().stream().filter(entry -> purpose != Purpose.ITEM_PITCHES || material.items.contains(entry.item))
                .filter(entry -> SettingsText.matches(entry.id + " " + entry.name, needle))
                .sorted(Comparator.comparing(Entry::name, String.CASE_INSENSITIVE_ORDER).thenComparing(Entry::id)).toList();
        availablePage = 0;
    }
    public String query() { return query; }
    public List<Entry> available() { return matches; }
    private List<Item> selectedItems() {
        return purpose == Purpose.ITEM_PITCHES ? material.customPitches.stream().map(entry -> entry.item).distinct().toList() : items;
    }
    public List<Entry> selected() { return selectedItems().stream().map(entries::get).filter(Objects::nonNull).toList(); }
    public boolean contains(Item item) { return selectedItems().contains(item); }
    public Item singleSelected() { return selectedSelection.size() == 1 ? selectedSelection.iterator().next() : null; }
    public String name(Item item) { return entries.get(item).name; }
    public float pitch(Item item) {
        return material.customPitches.stream().filter(entry -> entry.item == item).map(entry -> entry.pitch).findFirst().orElse(material.pitch);
    }
    public void pitch(Item item, float value) {
        if (purpose != Purpose.ITEM_PITCHES || !contains(item) || !Float.isFinite(value) || value <= 0) throw new IllegalArgumentException("Pitch must be positive.");
        // Replace duplicate legacy entries with one explicit override for the chosen item.
        material.customPitches.removeIf(entry -> entry.item == item);
        material.customPitches.add(new InteractionMaterial.PitchEntry(item, value));
    }
    public void remove(Item item) {
        if (purpose == Purpose.ITEM_PITCHES) material.customPitches.removeIf(entry -> entry.item == item);
        else items.removeIf(entry -> entry == item);
        selectedSelection.remove(item);
    }
    public boolean marked(Item item, boolean available) { return (available ? availableSelection : selectedSelection).contains(item); }
    public void toggle(Item item, boolean available) {
        if (!entries.containsKey(item) || available && contains(item) || !available && !contains(item)) return;
        if (available && purpose == Purpose.ITEM_PITCHES && !material.items.contains(item)) return;
        Set<Item> selection = available ? availableSelection : selectedSelection;
        if (!selection.remove(item)) selection.add(item);
    }
    public int pendingAdds() { return availableSelection.size(); }
    public int pendingRemovals() { return selectedSelection.size(); }
    public void addMarked() {
        for (Item item : availableSelection) if (!contains(item)) {
            if (purpose == Purpose.ITEM_PITCHES) material.customPitches.add(new InteractionMaterial.PitchEntry(item, material.pitch));
            else items.add(item);
        }
        availableSelection.clear();
    }
    public void removeMarked() {
        for (Item item : new ArrayList<>(selectedSelection)) remove(item);
        selectedSelection.clear();
    }
}
