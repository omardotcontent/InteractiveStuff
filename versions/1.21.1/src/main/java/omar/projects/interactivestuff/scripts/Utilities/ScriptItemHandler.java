package omar.projects.interactivestuff.scripts.Utilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.RegistryOps;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ScriptItemHandler {

    private static final String MODEL_KEY = "InteractiveStuffModel";

    /** 1.21.1 has components but predates item_model; only IS's renderer consumes this private marker. */
    public static String modelOverride(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? "" : data.copyTag().getCompound(MODEL_KEY).getString("id");
    }

    private static CompoundTag customData(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    private static void writeModel(ItemStack stack, String model) {
        ResourceLocation.parse(model);
        boolean authored = customDataPresent(stack);
        CompoundTag data = customData(stack);
        CompoundTag marker = new CompoundTag();
        marker.putString("id", model);
        marker.putBoolean("authored", authored);
        data.put(MODEL_KEY, marker);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }

    private static boolean customDataPresent(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) return false;
        CompoundTag data = customData(stack);
        boolean authored = !data.contains(MODEL_KEY, Tag.TAG_COMPOUND) || data.getCompound(MODEL_KEY).getBoolean("authored");
        data.remove(MODEL_KEY);
        return authored || !data.isEmpty();
    }

    private static boolean isModel(String id) { return "item_model".equals(id) || "minecraft:item_model".equals(id); }

    private static DataComponentType<?> componentType(ResourceLocation id) {
        var type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(id);
        if (type == null) throw new IllegalArgumentException("Component " + id + " is unavailable on 1.21.1.");
        return type;
    }

    private ScriptItemHandler() {}

    public static ItemStack apply(final ItemStack stack, String componentId, final String rawValue) {
        if (stack == null || componentId == null || rawValue == null) {
            return stack;
        }

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final ResourceLocation id;
        try {
            id = ResourceLocation.parse(componentId);
        } catch (final Exception e) {
            return stack;
        }

        final Tag nbt = coerce(rawValue);
        if (nbt == null) {
            return stack;
        }

        if (isModel(componentId)) {
            if (!(nbt instanceof StringTag)) throw new IllegalArgumentException("item_model requires a model identifier string.");
            writeModel(stack, nbt.getAsString());
        } else {
            final DataComponentType<?> type = componentType(id);
            String model = modelOverride(stack);
            applyDecoded(stack, type, nbt);
            if (type == DataComponents.CUSTOM_DATA && !model.isEmpty()) writeModel(stack, model);
        }
        return stack;
    }

    public static List<Map<String, String>> getComponents(final ItemStack stack) {
        final List<Map<String, String>> result = new ArrayList<>();
        if (stack == null || stack.isEmpty()) {
            return result;
        }

        for (String id : getComponentIds(stack)) result.add(Map.of("id", id, "value", getComponent(stack, id)));

        return result;
    }

    public static List<String> getComponentIds(final ItemStack stack) {
        final List<String> result = new ArrayList<>();
        if (stack == null || stack.isEmpty()) {
            return result;
        }

        final DataComponentMap components = stack.getComponents();
        final Registry<DataComponentType<?>> registry = BuiltInRegistries.DATA_COMPONENT_TYPE;

        for (final TypedDataComponent<?> component : components) {
            final ResourceLocation id = registry.getKey(component.type());
            if (id != null && (component.type() != DataComponents.CUSTOM_DATA || hasComponent(stack, "minecraft:custom_data"))) {
                result.add(id.toString());
            }
        }

        if (!modelOverride(stack).isEmpty()) result.add("minecraft:item_model");

        return result;
    }

    public static String getComponent(final ItemStack stack, String componentId) {
        if (stack == null || stack.isEmpty() || componentId == null) {
            return null;
        }

        if (isModel(componentId)) {
            String model = modelOverride(stack);
            return model.isEmpty() ? null : StringTag.valueOf(model).toString();
        }
        if ("custom_data".equals(componentId) || "minecraft:custom_data".equals(componentId)) {
            if (!customDataPresent(stack)) return null;
            CompoundTag data = customData(stack); data.remove(MODEL_KEY);
            return data.toString();
        }

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final ResourceLocation id;
        try {
            id = ResourceLocation.parse(componentId);
        } catch (final Exception e) {
            return null;
        }

        final Registry<DataComponentType<?>> registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
        final DataComponentType<?> type = componentType(id);

        final Object value = stack.get(type);
        if (value == null) {
            return null;
        }

        return encodeValue(type, value);
    }

    public static boolean hasComponent(final ItemStack stack, String componentId) {
        if (stack == null || stack.isEmpty() || componentId == null) {
            return false;
        }

        if (isModel(componentId)) return !modelOverride(stack).isEmpty();
        if ("custom_data".equals(componentId) || "minecraft:custom_data".equals(componentId)) return getComponent(stack, componentId) != null;

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final ResourceLocation id;
        try {
            id = ResourceLocation.parse(componentId);
        } catch (final Exception e) {
            return false;
        }

        final DataComponentType<?> type = componentType(id);
        return stack.has(type);
    }

    public static void remove(final ItemStack stack, String componentId) {
        if (stack == null || stack.isEmpty() || componentId == null) {
            return;
        }

        if (isModel(componentId)) {
            boolean authored = customDataPresent(stack);
            CompoundTag data = customData(stack); data.remove(MODEL_KEY);
            if (data.isEmpty() && !authored) stack.remove(DataComponents.CUSTOM_DATA);
            else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
            return;
        }
        if ("custom_data".equals(componentId) || "minecraft:custom_data".equals(componentId)) {
            String model = modelOverride(stack); stack.remove(DataComponents.CUSTOM_DATA);
            if (!model.isEmpty()) writeModel(stack, model);
            return;
        }

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final ResourceLocation id;
        try {
            id = ResourceLocation.parse(componentId);
        } catch (final Exception e) {
            return;
        }

        final DataComponentType<?> type = componentType(id);

        stack.remove(type);
    }

    @SuppressWarnings("unchecked")
    private static <T> String encodeValue(final DataComponentType<T> type, final Object value) {
        try {
            final Codec<T> codec = type.codecOrThrow();
            final DataResult<Tag> result = codec.encodeStart(componentOps(), (T) value);
            return result.result()
                    .map(Tag::toString)
                    .orElse(value.toString());
        } catch (final Exception e) {
            return value.toString();
        }
    }

    private static <T> void applyDecoded(final ItemStack stack, final DataComponentType<T> type, final Tag nbt) {
        final Codec<T> codec = type.codecOrThrow();
        final DataResult<T> result = codec.parse(componentOps(), nbt);
        T value = result.getOrThrow(error -> new IllegalArgumentException("Invalid component " + type + ": " + error));
        stack.set(type, value);
    }

    private static DynamicOps<Tag> componentOps() {
        Minecraft client = Minecraft.getInstance();
        return client != null && client.level != null
                ? RegistryOps.create(NbtOps.INSTANCE, client.level.registryAccess()) : NbtOps.INSTANCE;
    }

    private static Tag coerce(final String rawInput) {
        String input = rawInput.trim();
        if (input.equals("null")) return null;
        // Existing scripts pass model IDs as bare strings; preserve that shorthand.
        if (!input.startsWith("{") && !input.startsWith("[") && !input.startsWith("\"")
                && !input.startsWith("'") && input.contains(":")) return StringTag.valueOf(input);
        try {
            return new TagParser(new com.mojang.brigadier.StringReader(input)).readValue();
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException error) {
            throw new IllegalArgumentException("Invalid component SNBT: " + error.getMessage(), error);
        }
    }
}
