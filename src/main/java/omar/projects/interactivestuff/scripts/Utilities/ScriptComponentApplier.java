package omar.projects.interactivestuff.scripts.Utilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ScriptComponentApplier {

    private ScriptComponentApplier() {}

    public static ItemStack apply(ItemStack stack, String componentId, String rawValue) {
        if (stack == null || componentId == null || rawValue == null) {
            return stack;
        }

        Identifier id;
        try {
            id = Identifier.of(componentId);
        } catch (Exception e) {
            return stack;
        }

        Registry<ComponentType<?>> registry = Registries.DATA_COMPONENT_TYPE;
        ComponentType<?> type = registry.get(id);
        if (type == null) return stack;

        NbtElement nbt = coerce(rawValue);
        if (nbt == null) return stack;

        applyDecoded(stack, type, nbt);
        return stack;
    }

    /* ========================= CORE ========================= */

    private static <T> void applyDecoded(
            ItemStack stack,
            ComponentType<T> type,
            NbtElement nbt
    ) {
        Codec<T> codec = type.getCodecOrThrow();
        DataResult<T> result = codec.parse(NbtOps.INSTANCE, nbt);

        result.result().ifPresent(value -> stack.set(type, value));
    }

    /* ===================== STRING → NBT ===================== */

    private static NbtElement coerce(String input) {
        input = input.trim();

        if (input.equals("null")) return null;

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

        if (body.isEmpty()) return list;

        for (String part : body.split(",")) {
            final NbtElement element = coerce(part.trim());
            if (element != null) list.add(element);
        }

        return list;
    }

    private static NbtCompound parseCompound(final String input) {
        final String body = input.substring(1, input.length() - 1).trim();
        final NbtCompound compound = new NbtCompound();

        if (body.isEmpty()) return compound;

        for (final String pair : body.split(",")) {
            final String[] kv = pair.split(":", 2);
            if (kv.length != 2) continue;

            final NbtElement value = coerce(kv[1].trim());
            if (value != null) compound.put(kv[0].trim(), value);
        }

        return compound;
    }
}
