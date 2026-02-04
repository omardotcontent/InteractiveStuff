package omar.projects.interactivestuff.scripts.Utilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

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
            id = Identifier.of(componentId);
        } catch (final Exception e) {
            return stack;
        }

        final Registry<ComponentType<?>> registry = Registries.DATA_COMPONENT_TYPE;
        final ComponentType<?> type = registry.get(id);
        if (type == null) {
            return stack;
        }

        final NbtElement nbt = coerce(rawValue);
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

        final ComponentMap components = stack.getComponents();
        final Registry<ComponentType<?>> registry = Registries.DATA_COMPONENT_TYPE;

        for (final Component<?> component : components) {
            final ComponentType<?> type = component.type();
            final Identifier id = registry.getId(type);
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

        final ComponentMap components = stack.getComponents();
        final Registry<ComponentType<?>> registry = Registries.DATA_COMPONENT_TYPE;

        for (final Component<?> component : components) {
            final Identifier id = registry.getId(component.type());
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
            id = Identifier.of(componentId);
        } catch (final Exception e) {
            return null;
        }

        final Registry<ComponentType<?>> registry = Registries.DATA_COMPONENT_TYPE;
        final ComponentType<?> type = registry.get(id);
        if (type == null) {
            return null;
        }

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
            id = Identifier.of(componentId);
        } catch (final Exception e) {
            return false;
        }

        final ComponentType<?> type = Registries.DATA_COMPONENT_TYPE.get(id);
        return type != null && stack.contains(type);
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
            id = Identifier.of(componentId);
        } catch (final Exception e) {
            return;
        }

        final ComponentType<?> type = Registries.DATA_COMPONENT_TYPE.get(id);
        if (type == null) {
            return;
        }

        stack.remove(type);
    }

    @SuppressWarnings("unchecked")
    private static <T> String encodeComponentValue(final Component<T> component) {
        return encodeValue(component.type(), component.value());
    }

    @SuppressWarnings("unchecked")
    private static <T> String encodeValue(final ComponentType<T> type, final Object value) {
        try {
            final Codec<T> codec = type.getCodecOrThrow();
            final DataResult<NbtElement> result = codec.encodeStart(NbtOps.INSTANCE, (T) value);
            return result.result()
                    .map(NbtElement::toString)
                    .orElse(value.toString());
        } catch (final Exception e) {
            return value.toString();
        }
    }

    private static <T> void applyDecoded(final ItemStack stack, final ComponentType<T> type, final NbtElement nbt) {
        final Codec<T> codec = type.getCodecOrThrow();
        final DataResult<T> result = codec.parse(NbtOps.INSTANCE, nbt);
        result.result().ifPresent(value -> stack.set(type, value));
        result.error().ifPresent(err -> System.err.println("Error parsing component " + type + ": " + err.message()));
    }

    private static NbtElement coerce(final String rawInput) {
        final String input = rawInput.trim();
        if (input.equals("null")) {
            return null;
        }
        if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
            return NbtByte.of(Boolean.parseBoolean(input));
        }
        if (input.matches("-?\\d+")) {
            return NbtInt.of(Integer.parseInt(input));
        }
        if (input.matches("-?\\d+\\.\\d+")) {
            return NbtDouble.of(Double.parseDouble(input));
        }
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return NbtString.of(input.substring(1, input.length() - 1));
        }
        if (input.startsWith("[") && input.endsWith("]")) {
            return parseList(input);
        }
        if (input.startsWith("{") && input.endsWith("}")) {
            return parseCompound(input);
        }
        return NbtString.of(input);
    }

    private static NbtElement parseList(final String input) {
        final String body = input.substring(1, input.length() - 1).trim();
        final NbtList list = new NbtList();
        if (body.isEmpty()) {
            return list;
        }
        for (final String part : body.split(",")) {
            final NbtElement element = coerce(part.trim());
            if (element != null) {
                list.add(element);
            }
        }
        return list;
    }

    private static NbtCompound parseCompound(final String input) {
        final String body = input.substring(1, input.length() - 1).trim();
        final NbtCompound compound = new NbtCompound();
        if (body.isEmpty()) {
            return compound;
        }
        for (final String pair : body.split(",")) {
            final String[] kv = pair.split(":", 2);
            if (kv.length != 2) {
                continue;
            }
            final NbtElement value = coerce(kv[1].trim());
            if (value != null) {
                compound.put(kv[0].trim(), value);
            }
        }
        return compound;
    }
}