package omar.projects.interactivestuff.scripts.Utilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.RegistryOps;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScriptItemHandler {

    private ScriptItemHandler() {}

    public static ItemStack apply(final ItemStack stack, String componentId, final String rawValue) {
        if (stack == null || componentId == null || rawValue == null) {
            return stack;
        }

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final Identifier id;
        try {
            id = Identifier.parse(componentId);
        } catch (final Exception e) {
            return stack;
        }

        final Registry<DataComponentType<?>> registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
        final DataComponentType<?> type = registry.get(id).orElseThrow().value();

        final Tag nbt = coerce(rawValue);
        if (nbt == null) {
            return stack;
        }

        applyDecoded(stack, type, nbt);
        return stack;
    }

    public static List<Map<String, String>> getComponents(final ItemStack stack) {
        final List<Map<String, String>> result = new ArrayList<>();
        if (stack == null || stack.isEmpty()) {
            return result;
        }

        final DataComponentMap components = stack.getComponents();
        final Registry<DataComponentType<?>> registry = BuiltInRegistries.DATA_COMPONENT_TYPE;

        for (final TypedDataComponent<?> component : components) {
            final DataComponentType<?> type = component.type();
            final Identifier id = registry.getKey(type);
            if (id == null) {
                continue;
            }

            final Map<String, String> entry = new HashMap<>();
            entry.put("id", id.toString());
            entry.put("value", encodeComponentValue(component));
            result.add(entry);
        }

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
            final Identifier id = registry.getKey(component.type());
            if (id != null) {
                result.add(id.toString());
            }
        }

        return result;
    }

    public static String getComponent(final ItemStack stack, String componentId) {
        if (stack == null || stack.isEmpty() || componentId == null) {
            return null;
        }

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final Identifier id;
        try {
            id = Identifier.parse(componentId);
        } catch (final Exception e) {
            return null;
        }

        final Registry<DataComponentType<?>> registry = BuiltInRegistries.DATA_COMPONENT_TYPE;
        final DataComponentType<?> type = registry.get(id).orElseThrow().value();

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

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final Identifier id;
        try {
            id = Identifier.parse(componentId);
        } catch (final Exception e) {
            return false;
        }

        final DataComponentType<?> type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(id).orElseThrow().value();
        return stack.has(type);
    }

    public static void remove(final ItemStack stack, String componentId) {
        if (stack == null || stack.isEmpty() || componentId == null) {
            return;
        }

        if (!componentId.contains(":")) {
            componentId = "minecraft:" + componentId;
        }

        final Identifier id;
        try {
            id = Identifier.parse(componentId);
        } catch (final Exception e) {
            return;
        }

        final DataComponentType<?> type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(id).orElseThrow().value();

        stack.remove(type);
    }

    private static <T> String encodeComponentValue(final TypedDataComponent<T> component) {
        return encodeValue(component.type(), component.value());
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
            return TagParser.create(NbtOps.INSTANCE).parseFully(input);
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException error) {
            throw new IllegalArgumentException("Invalid component SNBT: " + error.getMessage(), error);
        }
    }
}
