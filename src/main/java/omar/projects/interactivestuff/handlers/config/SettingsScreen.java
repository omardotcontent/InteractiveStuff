package omar.projects.interactivestuff.handlers.config;

import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import omar.projects.interactivestuff.compat.MinecraftRegistries;
import omar.projects.interactivestuff.compat.MinecraftAccess;
import omar.projects.interactivestuff.config.PackOptions;
import omar.projects.interactivestuff.objects.InteractionMaterial;
import omar.projects.interactivestuff.scripts.variables.ConfigOption;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/** Shared screen behavior. Only native drawing and entry-point APIs vary by Minecraft release. */
public abstract class SettingsScreen extends Screen {
    public enum Section { GENERAL, PARTICLES, SCRIPTING, MATERIALS, EXCLUSIONS, PACKS }
    public final Screen parent;
    private ConfigHandler draft;
    private final ConfigHandler defaults;
    private Section section = Section.GENERAL;
    private InteractionMaterial editing;
    private String query = "", error = "";
    private int offset, visibleCount, count, contentX, contentWidth, rowTop;
    private long transition = System.nanoTime();
    private final List<Row> displayed = new ArrayList<>();
    private final List<Navigation> navigation = new ArrayList<>();
    private final List<IconControl> icons = new ArrayList<>();
    private final List<SlotControl> slots = new ArrayList<>();
    private MaterialItemPicker picker;
    private SoundPicker soundPicker;
    private Item pitchItem;
    private InventoryPane availablePane, selectedPane;
    private Button addItems, removeItems, editPitch;
    private static final Identifier LOGO = Identifier.parse("interactivestuff:textures/gui/icon.png");
    private final Map<String, ConfigOption> colorControls = new HashMap<>();
    private final Map<AbstractWidget, Integer> animated = new LinkedHashMap<>();
    private Row building;
    private EditBox search;
    private Button apply;
    private final Map<String, String> invalid = new LinkedHashMap<>();
    private final Map<String, String> pendingText = new HashMap<>();
    private final Map<String, Runnable> refreshValues = new HashMap<>();
    private boolean synchronizing;

    protected SettingsScreen(Screen parent) {
        super(Component.literal(SettingsText.text("title")));
        this.parent = parent;
        draft = ConfigHandler.INSTANCE.copy();
        draft.packOptionValues = PackOptions.snapshot();
        ConfigHandler loaded = ConfigHandler.loadDefaults();
        defaults = loaded == null ? new ConfigHandler() : loaded;
    }
    public ConfigHandler draft() { return draft; }
    public Section section() { return section; }
    public void select(Section value) { section = value; editing = null; picker = null; soundPicker = null; pitchItem = null; offset = 0; transition = System.nanoTime(); init(); }
    public void search(String value) {
        if (picker != null) picker.filter(value);
        else if (soundPicker != null) { soundPicker.filter(value); offset = 0; }
        else { query = value; offset = 0; }
        refreshContent();
    }
    public void editMaterial(int index) { section = Section.MATERIALS; query = ""; editing = draft.materials.get(index); picker = null; soundPicker = null; pitchItem = null; offset = 0; init(); }
    public void openMaterialItems() { if (editing != null) { picker = new MaterialItemPicker(editing); soundPicker = null; pitchItem = null; init(); } }
    public void openItemPitches() { if (editing != null) { picker = MaterialItemPicker.pitches(editing); soundPicker = null; pitchItem = null; init(); } }
    public void openExclusions() { section = Section.EXCLUSIONS; editing = null; query = ""; picker = MaterialItemPicker.exclusions(draft.excludedBlocks); soundPicker = null; pitchItem = null; init(); }
    public void openSounds() { if (editing != null) { soundPicker = new SoundPicker(); picker = null; pitchItem = null; offset = 0; init(); } }
    public void editItemPitch(Item item) {
        if (picker != null && picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES && picker.contains(item)) { pitchItem = item; offset = 0; init(); }
    }
    public MaterialItemPicker itemPicker() { return picker; }
    public SoundPicker soundPicker() { return soundPicker; }
    public void chooseSound(String id) {
        if (soundPicker != null && editing != null && MinecraftRegistries.sound(Identifier.parse(id)) != null) {
            editing.sound = id; soundPicker = null; offset = 0; init();
        }
    }
    public boolean applyChanges() {
        if (!invalid.isEmpty()) { error = invalid.values().iterator().next(); return false; }
        try {
            draft.packOptionValues = PackOptions.validated(draft.packOptionValues);
            draft.saveChecked();
            ConfigHandler.INSTANCE.copyFrom(draft);
            PackOptions.commit(draft.packOptionValues);
            onClose();
            return true;
        } catch (Exception failure) {
            error = SettingsText.text("error.save");
            omar.projects.interactivestuff.IS.LOGGER.error("Could not apply InteractiveStuff settings", failure);
            return false;
        }
    }
    @Override public void onClose() { MinecraftAccess.openScreen(minecraft, parent); }
    @Override protected void init() {
        clearWidgets(); displayed.clear(); animated.clear(); navigation.clear(); icons.clear(); slots.clear();
        addItems = null; removeItems = null; editPitch = null;
        int side = Math.max(104, Math.min(126, width / 4));
        contentX = side + 14; contentWidth = width - contentX - 12;
        rowTop = soundPicker == null ? 68 : 96; visibleCount = Math.max(1, (height - 76 - rowTop - 23) / 28 + 1);
        int y = 43;
        for (Section value : Section.values()) {
            Button category = addRenderableWidget(Button.builder(Component.empty(), ignored -> select(value))
                    .createNarration(ignored -> Component.literal(name(value))).bounds(10, y, side - 20, 20).build());
            category.setTooltip(Tooltip.create(Component.literal(name(value))));
            category.active = value != section || editing != null || picker != null;
            navigation.add(new Navigation(category, value));
            y += 24;
        }
        iconButton(SettingsIcon.FOLDER, SettingsText.text("action.folder"), width - 30, 6, () -> ConfigHandler.INSTANCE.openConfigDirectory());
        search = new EditBox(font, contentX, 39, contentWidth, 20, Component.literal(picker != null ? SettingsText.text("search.items") : soundPicker != null ? SettingsText.text("search.sounds") : SettingsText.text("search.settings")));
        search.setMaxLength(160); search.setValue(picker != null ? picker.query() : soundPicker != null ? soundPicker.query() : query);
        search.setHint(Component.literal(picker != null ? SettingsText.text("search.items.hint") : soundPicker != null ? SettingsText.text("search.sounds.hint") : SettingsText.text("search.hint"))
                .withStyle(style -> style.withColor(SettingsTheme.MUTED & 0xFFFFFF)));
        search.setTooltip(Tooltip.create(Component.literal(picker != null ? SettingsText.text("search.items.tooltip") : soundPicker != null ? SettingsText.text("search.sounds.tooltip") : SettingsText.text("search.settings.tooltip"))));
        search.active = pitchItem == null;
        search.setResponder(this::search); addRenderableWidget(search);
        iconButton(SettingsIcon.RESET, SettingsText.text("action.reset"), width - 56, height - 29, this::resetPage);
        iconButton(SettingsIcon.CLOSE, SettingsText.text("action.cancel"), 10, height - 29, this::onClose);
        apply = iconButton(SettingsIcon.CHECK, SettingsText.text("action.apply"), width - 30, height - 29, this::applyChanges);
        refreshContent();
    }
    private static String name(Section value) {
        return SettingsText.text("category." + value.name().toLowerCase(Locale.ROOT));
    }
    private Button button(String text, int x, int y, int w, int h, Runnable action) {
        return addRenderableWidget(Button.builder(Component.literal(text), ignored -> action.run()).bounds(x, y, w, h).build());
    }
    private Button iconButton(SettingsIcon icon, String label, int x, int y, Runnable action) { return iconButton(() -> icon, label, x, y, action); }
    private Button iconButton(Supplier<SettingsIcon> icon, String label, int x, int y, Runnable action) {
        return iconButton(icon, () -> label, x, y, action);
    }
    private Button iconButton(Supplier<SettingsIcon> icon, Supplier<String> label, int x, int y, Runnable action) {
        return iconButton(icon, label, x, y, action, null);
    }
    private Button iconButton(Supplier<SettingsIcon> icon, Supplier<String> label, int x, int y, Runnable action,
                              java.util.function.IntSupplier stateColor) {
        Button button = addRenderableWidget(Button.builder(Component.empty(), ignored -> action.run())
                .createNarration(ignored -> Component.literal(label.get())).bounds(x, y, 20, 20).build());
        button.setTooltip(Tooltip.create(Component.literal(label.get())));
        icons.add(new IconControl(button, icon, stateColor));
        return button;
    }
    private record Row(String key, String label, String help, Consumer<Integer> build, Item icon, Runnable reset) {
        Row(String key, String label, String help, Consumer<Integer> build, Item icon) { this(key, label, help, build, icon, null); }
    }
    private record Navigation(Button button, Section section) {}
    private record IconControl(Button button, Supplier<SettingsIcon> icon, java.util.function.IntSupplier stateColor) {}
    private record SlotControl(Button button, MaterialItemPicker.Entry entry, boolean available) {}
    private record InventoryPane(int x, int y, int columns, int rows, boolean available) {
        int size() { return columns * rows; }
        int bottom() { return y + rows * 18; }
    }
    private void refreshContent() {
        if (animated.containsKey(getFocused())) setFocused(null);
        for (AbstractWidget widget : new ArrayList<>(animated.keySet())) removeWidget(widget);
        icons.removeIf(icon -> animated.containsKey(icon.button));
        animated.clear(); displayed.clear(); colorControls.clear(); refreshValues.clear(); slots.clear();
        if (picker != null && pitchItem == null) { buildInventory(); updateValidation(); return; }
        if (soundPicker != null) {
            var back = iconButton(SettingsIcon.BACK, SettingsText.text("back.material_sound"), contentX, rowTop - 28, () -> { soundPicker = null; offset = 0; init(); });
            animated.put(back, back.getX());
        }
        String needle = picker != null || soundPicker != null ? "" : query.strip();
        List<Row> all = new ArrayList<>();
        if (pitchItem != null) pitchRows(all);
        else if (soundPicker != null) soundRows(all);
        else if (needle.isEmpty() || editing != null) all.addAll(rows());
        else {
            Section savedSection = section;
            for (Section value : Section.values()) {
                section = value;
                for (Row row : rows()) all.add(new Row(row.key, row.label, name(value) + " · " + row.help, row.build, row.icon, row.reset));
            }
            section = savedSection;
        }
        all = all.stream().filter(row -> needle.isEmpty() || SettingsText.matches(row.label + " " + row.help, needle)).toList();
        count = all.size(); offset = Math.max(0, Math.min(offset, Math.max(0, count - visibleCount)));
        for (int index = offset; index < Math.min(count, offset + visibleCount); index++) {
            Row row = all.get(index); int y = rowTop + (index - offset) * 28;
            var before = new HashSet<>(children());
            building = row;
            row.build.accept(y);
            if (row.reset != null) iconButton(SettingsIcon.RESET, row.key.startsWith("pack:") ? SettingsText.text("reset.pack", row.label) : SettingsText.text("reset.pitch", row.label), contentX + contentWidth - 20, y, () -> {
                row.reset.run(); invalid.remove(row.key); pendingText.remove(row.key); refreshContent();
            });
            building = null;
            for (var child : children()) if (!before.contains(child) && child instanceof AbstractWidget widget) {
                animated.put(widget, widget.getX());
                if (icons.stream().noneMatch(control -> control.button == widget)) widget.setTooltip(Tooltip.create(Component.literal(row.label + "\n" + row.help)));
            }
            displayed.add(row);
        }
        if (count > visibleCount) {
            int y = height - 57;
            var previous = iconButton(SettingsIcon.BACK, SettingsText.text("page.previous"), contentX, y, () -> scroll(-visibleCount)); previous.active = offset > 0;
            var next = iconButton(SettingsIcon.NEXT, SettingsText.text("page.next"), contentX + 26, y, () -> scroll(visibleCount)); next.active = offset + visibleCount < count;
            animated.put(previous, previous.getX()); animated.put(next, next.getX());
        }
        updateValidation();
    }
    private void scroll(int distance) { offset += distance; refreshContent(); }
    private void buildInventory() {
        boolean wide = contentWidth >= 380;
        int columns = Math.min(9, contentWidth / 18);
        int rows = wide ? Math.max(1, Math.min(9, (height - rowTop - 116) / 18)) : Math.max(1, (height - 204) / 36);
        int gridWidth = columns * 18;
        if (wide) {
            int total = gridWidth * 2 + 28;
            int x = contentX + (contentWidth - total) / 2;
            int top = rowTop + Math.max(0, (height - 80 - rowTop - (rows * 18 + 36)) / 2) + 12;
            availablePane = new InventoryPane(x, top, columns, rows, true);
            selectedPane = new InventoryPane(x + gridWidth + 28, top, columns, rows, false);
        } else {
            int x = contentX + (contentWidth - gridWidth) / 2;
            availablePane = new InventoryPane(x, rowTop + 12, columns, rows, true);
            selectedPane = new InventoryPane(x, availablePane.bottom() + 44, columns, rows, false);
        }
        var before = new HashSet<>(children());
        iconButton(SettingsIcon.BACK, editing == null ? SettingsText.text("back.exclusions") : SettingsText.text("back.material_items"), contentX, rowTop, () -> { picker = null; init(); });
        inventoryPane(availablePane, availablePane.bottom() + 4);
        inventoryPane(selectedPane, wide ? selectedPane.bottom() + 4 : height - 57);
        for (var child : children()) if (!before.contains(child) && child instanceof AbstractWidget widget) animated.put(widget, widget.getX());
    }
    private void inventoryPane(InventoryPane pane, int toolbarY) {
        List<MaterialItemPicker.Entry> entries = pane.available ? picker.available() : picker.selected();
        int maxPage = Math.max(0, (entries.size() - 1) / pane.size());
        int page = Math.max(0, Math.min(maxPage, pane.available ? picker.availablePage : picker.selectedPage));
        if (pane.available) picker.availablePage = page; else picker.selectedPage = page;
        for (int index = 0; index < pane.size(); index++) {
            int position = page * pane.size() + index;
            var entry = position < entries.size() ? entries.get(position) : null;
            Button slot = addRenderableWidget(Button.builder(Component.empty(), ignored -> {
                picker.toggle(entry.item(), pane.available); updateInventoryActions();
            }).createNarration(ignored -> Component.literal(entry == null ? SettingsText.text("inventory.empty") : SettingsText.text("inventory.item", entry.name(), SettingsText.text(pane.available ? "inventory.available_item" : "inventory.selected_item"))))
                    .bounds(pane.x + index % pane.columns * 18, pane.y + index / pane.columns * 18, 18, 18).build());
            slot.active = entry != null && (!pane.available || !picker.contains(entry.item()));
            if (entry != null) slot.setTooltip(Tooltip.create(Component.literal(entry.name() + "\n" + entry.id()
                    + (picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES && !pane.available ? "\n" + SettingsText.text("inventory.pitch_help", format(picker.pitch(entry.item()))) : "")
                    + (pane.available && picker.contains(entry.item()) ? SettingsText.text("inventory.already_selected") : "\n" + SettingsText.text("inventory.mark_help", SettingsText.text(pane.available ? "action.add" : "action.remove"))))));
            slots.add(new SlotControl(slot, entry, pane.available));
        }
        var previous = iconButton(SettingsIcon.BACK, pane.available ? SettingsText.text("inventory.available.previous") : SettingsText.text("inventory.selected.previous"), pane.x, toolbarY, () -> changeInventoryPage(pane.available, -1));
        previous.active = page > 0;
        var next = iconButton(SettingsIcon.NEXT, pane.available ? SettingsText.text("inventory.available.next") : SettingsText.text("inventory.selected.next"), pane.x + 26, toolbarY, () -> changeInventoryPage(pane.available, 1));
        next.active = page < maxPage;
        Supplier<String> description = () -> SettingsText.text(pane.available ? "inventory.add" : "inventory.remove", pane.available ? picker.pendingAdds() : picker.pendingRemovals());
        var confirm = iconButton(() -> pane.available ? SettingsIcon.ADD : SettingsIcon.REMOVE,
                description, pane.x + pane.columns * 18 - 20, toolbarY, () -> {
                    if (pane.available) picker.addMarked();
                    else {
                        if (picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES) for (var entry : picker.selected()) if (picker.marked(entry.item(), false)) {
                            String key = pitchKey(entry.item()); invalid.remove(key); pendingText.remove(key);
                        }
                        picker.removeMarked();
                    }
                    refreshContent();
                });
        if (pane.available) addItems = confirm; else removeItems = confirm;
        if (!pane.available && picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES) {
            editPitch = iconButton(SettingsIcon.EDIT, SettingsText.text("pitch.edit"), pane.x + pane.columns * 18 - 46, toolbarY,
                    () -> editItemPitch(picker.singleSelected()));
        }
        updateInventoryActions();
    }
    private void updateInventoryActions() {
        if (editPitch != null) editPitch.active = picker.singleSelected() != null;
        if (addItems != null) {
            addItems.active = picker.pendingAdds() > 0;
            addItems.setTooltip(Tooltip.create(Component.literal(SettingsText.text("inventory.add", picker.pendingAdds()))));
        }
        if (removeItems != null) {
            removeItems.active = picker.pendingRemovals() > 0;
            removeItems.setTooltip(Tooltip.create(Component.literal(SettingsText.text("inventory.remove", picker.pendingRemovals()))));
        }
    }
    private void changeInventoryPage(boolean available, int direction) {
        if (available) picker.availablePage += direction; else picker.selectedPage += direction;
        refreshContent();
    }
    public boolean scrollContent(double mouseX, double mouseY, double delta) {
        if (picker != null && pitchItem == null && delta != 0 && mouseX >= contentX && mouseY >= rowTop && mouseY < height - 72) {
            boolean available = mouseY < selectedPane.y - 12 || availablePane.y == selectedPane.y && mouseX < selectedPane.x;
            changeInventoryPage(available, delta < 0 ? 1 : -1); return true;
        }
        if (mouseX >= contentX && mouseY >= rowTop && mouseY < height - 57 && count > visibleCount && delta != 0) {
            scroll(delta < 0 ? 1 : -1); return true;
        }
        return false;
    }
    private List<Row> rows() {
        List<Row> rows = new ArrayList<>();
        switch (section) {
            case GENERAL -> {
                toggle(rows, "hits", SettingsText.text("setting.hits"), SettingsText.text("setting.hits.tooltip"), () -> draft.enableInteractiveHits, value -> draft.enableInteractiveHits = value);
                number(rows, "cooldown", SettingsText.text("setting.cooldown"), SettingsText.text("setting.cooldown.tooltip"), 0, 100, 1, () -> (double) draft.HitCooldownTicks, value -> draft.HitCooldownTicks = value.intValue());
                toggle(rows, "animation", SettingsText.text("setting.animation"), SettingsText.text("setting.animation.tooltip"), () -> draft.menuAnimations, value -> draft.menuAnimations = value);
                action(rows, "reload", SettingsText.text("setting.reload"), SettingsText.text("setting.reload.tooltip"), () -> { draft = ConfigHandler.load(); invalid.clear(); pendingText.clear(); error = ""; init(); });
            }
            case PARTICLES -> {
                toggle(rows, "particles", SettingsText.text("setting.particles"), SettingsText.text("setting.particles.tooltip"), () -> draft.enableParticles, value -> draft.enableParticles = value);
                number(rows, "density", SettingsText.text("setting.density"), SettingsText.text("setting.density.tooltip"), 0, 200, 1, () -> (double) draft.particleDensity, value -> draft.particleDensity = value.intValue());
                number(rows, "limit", SettingsText.text("setting.limit"), SettingsText.text("setting.limit.tooltip"), 0, 4096, 1, () -> (double) draft.particleLimit, value -> draft.particleLimit = value.intValue());
                toggle(rows, "counter", SettingsText.text("setting.counter"), SettingsText.text("setting.counter.tooltip"), () -> draft.particleDebugCounters, value -> draft.particleDebugCounters = value);
                toggle(rows, "markers", SettingsText.text("setting.markers"), SettingsText.text("setting.markers.tooltip"), () -> draft.particleEmitterMarkers, value -> draft.particleEmitterMarkers = value);
            }
            case SCRIPTING -> {
                toggle(rows, "debug", SettingsText.text("setting.debug"), SettingsText.text("setting.debug.tooltip"), () -> draft.resourcePackDebugMode, value -> draft.resourcePackDebugMode = value);
                toggle(rows, "matrix", SettingsText.text("setting.matrix"), SettingsText.text("setting.matrix.tooltip"), () -> draft.resourcePackMatrixEditing, value -> draft.resourcePackMatrixEditing = value);
                toggle(rows, "color", SettingsText.text("setting.color"), SettingsText.text("setting.color.tooltip"), () -> draft.resourcePackColorChanging, value -> draft.resourcePackColorChanging = value);
            }
            case EXCLUSIONS -> action(rows, "excluded:items", SettingsText.text("exclusions.count", draft.excludedBlocks.size()), SettingsText.text("exclusions.tooltip"), this::openExclusions);
            case MATERIALS -> materialRows(rows);
            case PACKS -> packRows(rows);
        }
        return rows;
    }
    private void materialRows(List<Row> rows) {
        if (editing == null) {
            action(rows, "new", SettingsText.text("material.add"), SettingsText.text("material.add.tooltip"), () -> {
                section = Section.MATERIALS; query = ""; editing = new InteractionMaterial(SettingsText.text("material.new")); draft.materials.add(editing); offset = 0; init();
            });
            for (InteractionMaterial material : draft.materials) {
                String key = "material:" + draft.materials.indexOf(material);
                rows.add(new Row(key, material.name, SettingsText.text("material.summary", material.items.size(), SoundPicker.name(material.sound)), y -> {
                    iconButton(SettingsIcon.EDIT, SettingsText.text("action.edit_named", material.name), contentX + contentWidth - 46, y, () -> editMaterial(draft.materials.indexOf(material)));
                    iconButton(SettingsIcon.REMOVE, SettingsText.text("action.remove_named", material.name), contentX + contentWidth - 20, y, () -> {
                        int index = System.identityHashCode(material);
                        draft.materials.remove(material);
                        // Material indices change; rebuild editors without retaining text for a different material.
                        invalid.keySet().removeIf(entry -> entry.startsWith("mat:" + index + ":"));
                        pendingText.keySet().removeIf(entry -> entry.startsWith("mat:"));
                        refreshContent();
                    });
                }, material.items.isEmpty() ? null : material.items.get(0)));
            }
        } else {
            InteractionMaterial material = editing;
            String materialKey = "mat:" + System.identityHashCode(material) + ":";
            action(rows, "back", SettingsText.text("material.back"), SettingsText.text("material.back.tooltip"), () -> { editing = null; offset = 0; init(); });
            text(rows, materialKey + "name", SettingsText.text("material.name"), SettingsText.text("material.name.tooltip"), () -> material.name, value -> material.name = value, 160);
            action(rows, materialKey + "items", SettingsText.text("material.items_count", material.items.size()), SettingsText.text("material.items.tooltip"), this::openMaterialItems);
            action(rows, materialKey + "sound", SettingsText.text("material.sound", SoundPicker.name(material.sound)), SettingsText.text("material.sound.tooltip"), this::openSounds);
            number(rows, materialKey + "volume", SettingsText.text("material.volume"), SettingsText.text("material.volume.tooltip"), 0, 4, 0.01, () -> (double) material.volume, value -> material.volume = value.floatValue());
            number(rows, materialKey + "pitch", SettingsText.text("material.pitch"), SettingsText.text("material.pitch.tooltip"), 0.1, 4, 0.01, () -> (double) material.pitch, value -> material.pitch = value.floatValue());
            toggle(rows, materialKey + "random", SettingsText.text("material.random"), SettingsText.text("material.random.tooltip"), () -> material.randomPitch, value -> material.randomPitch = value);
            toggle(rows, materialKey + "block", SettingsText.text("material.block"), SettingsText.text("material.block.tooltip"), () -> material.requiresBlockHit, value -> material.requiresBlockHit = value);
            action(rows, materialKey + "pitches", SettingsText.text("material.pitches_count", material.customPitches.size()), SettingsText.text("material.pitches.tooltip"), this::openItemPitches);
            action(rows, "preview", SettingsText.text("material.preview"), SettingsText.text("material.preview.tooltip"), () -> SettingsAccess.preview(material));
        }
    }
    private String pitchKey(Item item) { return "mat:" + System.identityHashCode(editing) + ":pitch:" + BuiltInRegistries.ITEM.getKey(item); }
    private void pitchRows(List<Row> rows) {
        Item item = pitchItem;
        String key = pitchKey(item);
        action(rows, "back", SettingsText.text("pitch.back"), SettingsText.text("pitch.back.tooltip"), () -> { pitchItem = null; offset = 0; init(); });
        number(rows, key, SettingsText.text("pitch.label"), SettingsText.text("pitch.help", picker.name(item)), Math.min(0.01, picker.pitch(item)), Math.max(4, picker.pitch(item)), 0.01,
                () -> (double) picker.pitch(item), value -> picker.pitch(item, value.floatValue()));
        Row control = rows.get(rows.size() - 1);
        rows.set(rows.size() - 1, new Row(control.key, control.label, control.help, control.build, item, () -> {
            picker.pitch(item, editing.pitch); pendingText.remove(key); invalid.remove(key);
        }));
        action(rows, "preview", SettingsText.text("pitch.preview"), SettingsText.text("pitch.preview.tooltip"), () -> previewSound(editing.sound, picker.pitch(item)));
    }
    private void soundRows(List<Row> rows) {
        for (SoundPicker.Entry sound : soundPicker.matches()) {
            rows.add(new Row("sound:" + sound.id(), sound.name(), SettingsText.text("sound.help", sound.id()), y -> {
                iconButton(SettingsIcon.PREVIEW, SettingsText.text("sound.preview", sound.name()), contentX + contentWidth - 46, y, () -> previewSound(sound.id(), editing.pitch));
                var choose = iconButton(SettingsIcon.CHECK, SettingsText.text("sound.choose", sound.name()), contentX + contentWidth - 20, y, () -> chooseSound(sound.id()));
                choose.active = !sound.id().equals(editing.sound);
                if (!choose.active) choose.setTooltip(Tooltip.create(Component.literal(SettingsText.text("sound.current", sound.name()))));
            }, null));
        }
    }
    private void previewSound(String sound, float pitch) {
        InteractionMaterial preview = new InteractionMaterial(editing.name);
        preview.sound = sound; preview.pitch = pitch; preview.volume = editing.volume;
        SettingsAccess.preview(preview);
    }
    private String packName(String id) {
        if (id.equals("interactivestuff:interactive_resourcepack")) return SettingsText.text("title");
        var pack = minecraft.getResourcePackRepository().getPack(id);
        return pack == null ? id : pack.getTitle().getString();
    }
    private void packRows(List<Row> rows) {
        String group = "";
        for (ConfigOption option : PackOptions.options().stream().sorted(Comparator.comparing((ConfigOption option) -> option.owner.getPackId()).thenComparing(option -> option.category)).toList()) {
            String nextGroup = packName(option.owner.getPackId()) + " / " + SettingsText.resolve(option.category);
            if (!nextGroup.equals(group)) {
                group = nextGroup;
                rows.add(new Row("group:" + option.owner.getPackId() + ":" + option.category, group, option.owner.getPackId() + " / " + SettingsText.resolve(option.category), ignored -> {}, null));
            }
            String key = "pack:" + option.owner.getPackId() + ":" + option.id;
            String label = SettingsText.resolve(option.label);
            String help = packName(option.owner.getPackId()) + " / " + SettingsText.resolve(option.category) + "\n" + SettingsText.resolve(option.tooltip);
            Supplier<Object> value = () -> PackOptions.value(option, draft.packOptionValues);
            Consumer<Object> set = next -> PackOptions.put(draft.packOptionValues, option, next);
            int first = rows.size();
            switch (option.type) {
                case "toggle" -> toggle(rows, key, label, help, () -> (Boolean) value.get(), set::accept);
                case "number" -> number(rows, key, label, help, option.minimum, option.maximum, option.step, () -> ((Number) value.get()).doubleValue(), set::accept);
                case "dropdown" -> rows.add(new Row(key, label, help, y -> {
                    Button[] control = new Button[1];
                    control[0] = button(SettingsText.resolve((String) value.get()), controlX(), y, controlWidth(), 20, () -> {
                        int next = (option.choices.indexOf(value.get()) + 1) % option.choices.size();
                        set.accept(option.choices.get(next)); control[0].setMessage(Component.literal(SettingsText.resolve((String) value.get())));
                    });
                }, null));
                case "text" -> text(rows, key, label, help, () -> (String) value.get(), set::accept, option.maxLength);
                case "color" -> {
                    colorControls.put(key, option);
                    text(rows, key, label, help + SettingsText.text("color.hint"), () -> (String) value.get(), next -> { set.accept(next); syncColor(key, key); }, 7);
                    String[] channels = {SettingsText.text("color.red"), SettingsText.text("color.green"), SettingsText.text("color.blue")};
                    for (int channel = 0; channel < 3; channel++) {
                        int index = channel;
                        number(rows, key + ":" + channel, label + " · " + channels[channel], help, 0, 255, 1,
                                () -> (double) Integer.parseInt(((String) value.get()).substring(1 + index * 2, 3 + index * 2), 16), next -> {
                                    String hex = (String) value.get();
                                    set.accept(hex.substring(0, 1 + index * 2) + String.format(Locale.ROOT, "%02X", next.intValue()) + hex.substring(3 + index * 2));
                                    syncColor(key, key + ":" + index);
                                });
                    }
                }
            }
            for (int index = first; index < rows.size(); index++) {
                Row row = rows.get(index);
                rows.set(index, new Row(row.key, row.label, row.help, row.build, row.icon, () -> {
                    PackOptions.put(draft.packOptionValues, option, option.defaultValue);
                    invalid.keySet().removeIf(entry -> entry.equals(key) || entry.startsWith(key + ":"));
                    pendingText.keySet().removeIf(entry -> entry.equals(key) || entry.startsWith(key + ":"));
                }));
            }
        }
    }
    private void toggle(List<Row> rows, String key, String label, String help, Supplier<Boolean> get, Consumer<Boolean> set) {
        rows.add(new Row(key, label, help, y -> {
            Supplier<String> description = () -> SettingsText.text("toggle.description", label, SettingsText.text(get.get() ? "value.on" : "value.off"), help);
            Button[] control = new Button[1];
            control[0] = iconButton(() -> get.get() ? SettingsIcon.CHECK : SettingsIcon.UNCHECKED, description, controlX() + controlWidth() - 20, y, () -> {
                set.accept(!get.get()); control[0].setTooltip(Tooltip.create(Component.literal(description.get())));
            }, () -> get.get() ? SettingsTheme.BOOLEAN_TRUE : SettingsTheme.BOOLEAN_FALSE);
        }, null));
    }
    private void action(List<Row> rows, String key, String label, String help, Runnable action) {
        SettingsIcon icon = key.equals("new") ? SettingsIcon.ADD : key.equals("back") ? SettingsIcon.BACK : key.equals("reload") ? SettingsIcon.RESET : key.endsWith(":items") || key.endsWith(":pitches") ? SettingsIcon.ITEMS : key.endsWith(":sound") ? SettingsIcon.SOUND : SettingsIcon.PREVIEW;
        rows.add(new Row(key, label, help, y -> iconButton(icon, label + "\n" + help, controlX() + controlWidth() - 20, y, action), null));
    }
    private int labelWidth() { return Math.max(84, contentWidth / 2); }
    private int controlWidth() { return Math.min(140, contentWidth - labelWidth() - (building != null && building.reset != null ? 24 : 0)); }
    private int controlX() { return contentX + contentWidth - controlWidth() - (building != null && building.reset != null ? 24 : 0); }
    private void text(List<Row> rows, String key, String label, String help, Supplier<String> get, Consumer<String> set, int maxLength) {
        rows.add(new Row(key, label, help, y -> {
            EditBox field = new EditBox(font, controlX(), y, controlWidth(), 20, Component.literal(label));
            field.setMaxLength(maxLength); field.setValue(pendingText.getOrDefault(key, get.get()));
            field.setTextColor(invalid.containsKey(key) ? SettingsTheme.INVALID : SettingsTheme.FIELD);
            field.setResponder(next -> {
                if (synchronizing) return;
                pendingText.put(key, next);
                try { set.accept(next); invalid.remove(key); field.setTextColor(SettingsTheme.FIELD); }
                catch (Exception failure) { invalid.put(key, SettingsText.text("error.field", label, colorControls.containsKey(key) ? SettingsText.text("error.color") : SettingsText.text("error.text_length", maxLength))); field.setTextColor(SettingsTheme.INVALID); }
                field.setTooltip(Tooltip.create(Component.literal(invalid.containsKey(key) ? invalid.get(key) + SettingsText.text("error.correct_field") : label + "\n" + help)));
                updateValidation(); error = "";
            });
            refreshValues.put(key, () -> field.setValue(get.get()));
            addRenderableWidget(field);
        }, null));
    }
    private void number(List<Row> rows, String key, String label, String help, double min, double max, double step, Supplier<Double> get, Consumer<Double> set) {
        rows.add(new Row(key, label, help, y -> {
            int w = controlWidth(); int input = Math.min(55, w / 2);
            EditBox field = new EditBox(font, controlX() + w - input, y, input, 20, Component.literal(label));
            field.setMaxLength(32); field.setValue(pendingText.getOrDefault(key, format(get.get())));
            field.setTextColor(invalid.containsKey(key) ? SettingsTheme.INVALID : SettingsTheme.FIELD);
            class NumberSlider extends AbstractSliderButton {
                NumberSlider() { super(controlX(), y, w - input - 4, 20, Component.literal(format(get.get())), Math.max(0, Math.min(1, (get.get() - min) / (max - min)))); }
                void sync(double number) { value = Math.max(0, Math.min(1, (number - min) / (max - min))); updateMessage(); }
                @Override protected void updateMessage() { setMessage(Component.literal(format(min + value * (max - min)))); }
                @Override protected void applyValue() {
                    double next = Math.max(min, Math.min(max, min + Math.round(value * (max - min) / step) * step));
                    set.accept(next); pendingText.remove(key); invalid.remove(key); field.setValue(format(next));
                    setMessage(Component.literal(format(next))); updateValidation();
                }
            }
            NumberSlider slider = new NumberSlider();
            field.setResponder(next -> {
                if (synchronizing) return;
                pendingText.put(key, next);
                try {
                    double parsed = Double.parseDouble(next);
                    if (!Double.isFinite(parsed) || parsed < min || parsed > max) throw new IllegalArgumentException(SettingsText.text("error.range", format(min), format(max)));
                    double normalized = min + Math.round((parsed - min) / step) * step;
                    set.accept(Math.max(min, Math.min(max, normalized))); slider.sync(get.get()); invalid.remove(key); field.setTextColor(SettingsTheme.FIELD);
                } catch (Exception failure) { invalid.put(key, SettingsText.text("error.field", label, SettingsText.text("error.range", format(min), format(max)))); field.setTextColor(SettingsTheme.INVALID); }
                field.setTooltip(Tooltip.create(Component.literal(invalid.containsKey(key) ? invalid.get(key) + SettingsText.text("error.correct_field") : label + "\n" + help)));
                updateValidation(); error = "";
            });
            refreshValues.put(key, () -> { field.setValue(format(get.get())); slider.sync(get.get()); });
            addRenderableWidget(slider); addRenderableWidget(field);
        }, null));
    }
    /** Update sibling fields without rebuilding widgets or replacing the active text/cursor. */
    private void syncColor(String colorKey, String origin) {
        synchronizing = true;
        try {
            for (var entry : refreshValues.entrySet()) {
                String key = entry.getKey();
                if ((key.equals(colorKey) || key.startsWith(colorKey + ":")) && !key.equals(origin) && !invalid.containsKey(key)) {
                    pendingText.remove(key);
                    entry.getValue().run();
                }
            }
        } finally { synchronizing = false; }
    }
    private void updateValidation() {
        apply.active = invalid.isEmpty();
        apply.setTooltip(Tooltip.create(Component.literal(invalid.isEmpty() ? SettingsText.text("action.apply_all") : SettingsText.text("action.apply_invalid", invalid.values().iterator().next()))));
    }
    private static String format(double value) { return String.format(Locale.ROOT, "%.3f", value).replaceAll("0+$", "").replaceAll("\\.$", ""); }
    private void resetPage() {
        Set<String> keys = rows().stream().map(Row::key).collect(Collectors.toSet());
        if (section == Section.MATERIALS) { invalid.keySet().removeIf(key -> key.startsWith("mat:")); pendingText.keySet().removeIf(key -> key.startsWith("mat:")); }
        switch (section) {
            case GENERAL -> {
                draft.enableInteractiveHits = defaults.enableInteractiveHits;
                draft.HitCooldownTicks = defaults.HitCooldownTicks; draft.menuAnimations = defaults.menuAnimations;
            }
            case PARTICLES -> {
                draft.enableParticles = defaults.enableParticles; draft.particleDensity = defaults.particleDensity; draft.particleLimit = defaults.particleLimit;
                draft.particleDebugCounters = defaults.particleDebugCounters; draft.particleEmitterMarkers = defaults.particleEmitterMarkers;
            }
            case SCRIPTING -> { draft.resourcePackDebugMode = defaults.resourcePackDebugMode; draft.resourcePackMatrixEditing = defaults.resourcePackMatrixEditing; draft.resourcePackColorChanging = defaults.resourcePackColorChanging; }
            case EXCLUSIONS -> { draft.excludedBlocks = new ArrayList<>(defaults.excludedBlocks); picker = null; }
            case MATERIALS -> { draft.materials = defaults.copy().materials; editing = null; picker = null; }
            case PACKS -> { for (ConfigOption option : PackOptions.options()) PackOptions.put(draft.packOptionValues, option, option.defaultValue); }
        }
        invalid.keySet().removeIf(keys::contains); pendingText.keySet().removeIf(keys::contains);
        pitchItem = null; soundPicker = null;
        error = ""; offset = 0; init();
    }
    public interface Painter {
        void fill(int left, int top, int right, int bottom, int color);
        void text(String text, int x, int y, int color);
        void item(ItemStack stack, int x, int y);
        void foreground(Runnable draw);
        void tooltip(String text, int x, int y);
        void texture(Identifier texture, int x, int y, int width, int height);
    }
    /** Use the same wrapping/newline handling as native widget tooltips, never a single visual-order line. */
    protected List<net.minecraft.util.FormattedCharSequence> tooltipLines(String text) {
        return Tooltip.splitTooltip(minecraft, Component.literal(text.replace("\r\n", "\n").replace('\r', '\n')));
    }
    /** Draw above native button backgrounds; center each icon/label pair together. */
    protected void drawNavigation(Painter graphics) {
        for (Navigation entry : navigation) {
            Button button = entry.button;
            String label = font.plainSubstrByWidth(switch (entry.section) {
                case EXCLUSIONS -> SettingsText.text("category.exclusions.short"); case PACKS -> SettingsText.text("category.packs.short"); default -> name(entry.section);
            }, button.getWidth() - 25);
            int x = button.getX() + (button.getWidth() - 13 - font.width(label)) / 2;
            int color = button.active && button.isHovered() ? SettingsTheme.ACCENT : SettingsTheme.ICON;
            SettingsIcon.valueOf(entry.section.name()).draw(graphics, x, button.getY() + 6, color);
            graphics.text(label, x + 13, button.getY() + (button.getHeight() - font.lineHeight) / 2, color);
            if (entry.section == section) {
                int left = button.getX(), top = button.getY(), right = left + button.getWidth(), bottom = top + button.getHeight();
                graphics.fill(left, top, right, top + 1, SettingsTheme.SLOT_LIGHT);
                graphics.fill(left, bottom - 1, right, bottom, SettingsTheme.SLOT_LIGHT);
                graphics.fill(left, top, left + 1, bottom, SettingsTheme.SLOT_LIGHT);
                graphics.fill(right - 1, top, right, bottom, SettingsTheme.SLOT_LIGHT);
            }
        }
        for (IconControl control : icons) {
            Button button = control.button;
            if (control.stateColor != null) {
                // A native-sized dark inset keeps both state colors legible on the button texture.
                graphics.fill(button.getX() + 4, button.getY() + 4, button.getX() + 16, button.getY() + 16, SettingsTheme.GROUND);
            }
            control.icon.get().draw(graphics, button.getX() + (button.getWidth() - 8) / 2, button.getY() + (button.getHeight() - 8) / 2,
                    !button.active ? SettingsTheme.ICON_DISABLED : control.stateColor != null ? control.stateColor.getAsInt()
                            : button.isHovered() ? SettingsTheme.ACCENT : SettingsTheme.ICON);
        }
        if (picker != null) for (SlotControl slot : slots) {
            int x = slot.button.getX(), y = slot.button.getY();
            graphics.fill(x, y, x + 18, y + 18, SettingsTheme.SLOT_SHADOW);
            graphics.fill(x + 1, y + 1, x + 18, y + 18, SettingsTheme.SLOT_LIGHT);
            graphics.fill(x + 1, y + 1, x + 17, y + 17, SettingsTheme.SLOT_FILL);
            if (slot.entry != null) graphics.item(slot.entry.stack(), x + 1, y + 1);
        }
        // Item rendering has its own depth/order. Paint every slot overlay in one later layer.
        if (picker != null) graphics.foreground(() -> {
            for (SlotControl slot : slots) {
                int x = slot.button.getX(), y = slot.button.getY();
                boolean marked = slot.entry != null && picker.marked(slot.entry.item(), slot.available);
                if (marked) {
                    graphics.fill(x, y, x + 18, y + 1, SettingsTheme.ACCENT);
                    graphics.fill(x, y + 17, x + 18, y + 18, SettingsTheme.ACCENT);
                    graphics.fill(x, y, x + 1, y + 18, SettingsTheme.ACCENT);
                    graphics.fill(x + 17, y, x + 18, y + 18, SettingsTheme.ACCENT);
                }
                if (slot.button.isHoveredOrFocused()) graphics.fill(x + 1, y + 1, x + 17, y + 17, SettingsTheme.SLOT_HOVER);
                if (marked || slot.available && slot.entry != null && picker.contains(slot.entry.item()))
                    SettingsIcon.CHECK.draw(graphics, x + 9, y + 9, marked ? SettingsTheme.ACCENT : SettingsTheme.TEXT);
            }
        });
    }
    protected void drawSurface(Painter graphics, int mouseX, int mouseY) {
        int side = contentX - 14;
        graphics.fill(0, 0, width, height, SettingsTheme.GROUND);
        graphics.fill(0, 0, side, height, SettingsTheme.SIDEBAR);
        graphics.fill(0, 0, width, 32, SettingsTheme.BARS);
        graphics.fill(0, height - 35, width, height, SettingsTheme.BARS);
        graphics.texture(LOGO, 8, 7, 18, 18);
        graphics.text(font.plainSubstrByWidth(side < 126 ? SettingsText.text("title.compact") : SettingsText.text("title"), side - 34), 30, 12, SettingsTheme.TEXT);
        String title = pitchItem != null ? SettingsText.text("title.pitch", picker.name(pitchItem)) : soundPicker != null ? SettingsText.text("title.sounds", editing.name)
                : picker != null ? picker.purpose == MaterialItemPicker.Purpose.EXCLUSIONS ? SettingsText.text("category.exclusions") : SettingsText.text(picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES ? "title.pitches" : "title.items", editing.name)
                : editing == null ? name(section) : SettingsText.text("title.material", editing.name);
        graphics.text(font.plainSubstrByWidth(title, width - contentX - 40), contentX, 12, SettingsTheme.ACCENT);
        double elapsed = (System.nanoTime() - transition) / 1_000_000_000.0;
        double progress = picker != null || !draft.menuAnimations || elapsed >= 0.2 ? 1 : 1 - Math.pow(2, -10 * elapsed / 0.2);
        int shift = (int) Math.round(10 * (1 - progress));
        for (var entry : animated.entrySet()) { entry.getKey().setX(entry.getValue() + shift); entry.getKey().setAlpha((float) (0.75 + 0.25 * progress)); }
        int y = rowTop;
        for (Row row : displayed) {
            building = row;
            int x = contentX + shift;
            graphics.fill(side, y - 2, width, y + 23, (y - rowTop) % 56 == 0 ? SettingsTheme.ROW_LIGHT : SettingsTheme.ROW_DARK);
            if (row.icon != null) { graphics.item(MenuItems.stack(row.icon), x, y + 1); x += 20; }
            ConfigOption colorOption = colorControls.get(row.key);
            int reserved = colorOption == null ? 5 : 23;
            graphics.text(font.plainSubstrByWidth(row.label, row.key.startsWith("group:") ? contentWidth - 5 : row.key.startsWith("sound:") ? contentWidth - 54 : controlX() - x - reserved), x, y + 6, row.key.startsWith("group:") ? SettingsTheme.ACCENT : SettingsTheme.TEXT);
            if (colorOption != null) {
                int color = 0xFF000000 | Integer.parseInt(((String) PackOptions.value(colorOption, draft.packOptionValues)).substring(1), 16);
                graphics.fill(controlX() - 20, y + 2, controlX() - 4, y + 18, 0xFF000000);
                graphics.fill(controlX() - 19, y + 3, controlX() - 5, y + 17, color);
            }
            if (mouseX >= contentX && mouseX < controlX() && mouseY >= y && mouseY < y + 22)
                graphics.tooltip(row.label + "\n" + row.help, mouseX, mouseY);
            y += 28;
        }
        building = null;
        if (picker != null && pitchItem == null) { drawInventoryHeaders(graphics); }
        else if (count == 0) {
            graphics.text(soundPicker != null ? SettingsText.text("empty.sounds") : query.isBlank() && section == Section.PACKS ? SettingsText.text("empty.packs") : SettingsText.text("empty.settings"), contentX, rowTop + 8, SettingsTheme.MUTED);
        }
        String problem = !error.isEmpty() ? error : invalid.isEmpty() ? "" : invalid.values().iterator().next();
        if (!problem.isEmpty()) {
            graphics.text(font.plainSubstrByWidth(problem, contentWidth), contentX, height - 72, SettingsTheme.ERROR);
            if (mouseX >= contentX && mouseX < width - 12 && mouseY >= height - 74 && mouseY < height - 60)
                graphics.tooltip(problem + (error.isEmpty() ? SettingsText.text("error.correct_page") : ""), mouseX, mouseY);
        }
        if ((picker == null || pitchItem != null) && count > visibleCount) {
            String status = SettingsText.text("page.range", offset + 1, Math.min(count, offset + visibleCount), count);
            graphics.text(font.plainSubstrByWidth(status, contentWidth - 56), contentX + 52, height - 49, SettingsTheme.MUTED);
        }
    }
    private void drawInventoryHeaders(Painter graphics) {
        for (InventoryPane pane : List.of(availablePane, selectedPane)) {
            var entries = pane.available ? picker.available() : picker.selected();
            String title = SettingsText.text("inventory.heading", SettingsText.text(pane.available ? picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES ? "inventory.material_items" : "inventory.available" : picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES ? "inventory.pitch_overrides" : picker.purpose == MaterialItemPicker.Purpose.EXCLUSIONS ? "category.exclusions.short" : "inventory.selected"), entries.size());
            graphics.text(font.plainSubstrByWidth(title, pane.columns * 18), pane.x, pane.y - 12, SettingsTheme.TEXT);
            int page = pane.available ? picker.availablePage : picker.selectedPage;
            int pages = Math.max(1, (entries.size() + pane.size() - 1) / pane.size());
            int toolbarY = pane.available || availablePane.y == selectedPane.y ? pane.bottom() + 4 : height - 57;
            graphics.text(font.plainSubstrByWidth(SettingsText.text("page.number", page + 1, pages), pane.columns * 18 - (picker.purpose == MaterialItemPicker.Purpose.ITEM_PITCHES && !pane.available ? 104 : 78)), pane.x + 52, toolbarY + 6, SettingsTheme.MUTED);
        }
    }
}
