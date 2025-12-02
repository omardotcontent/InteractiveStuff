package omar.projects.interactivestuff;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import java.util.function.UnaryOperator;

public final class ISComponents {

    public static final ComponentType<Boolean> VIBRATING = register("vibrating", builder ->
            builder.codec(com.mojang.serialization.Codec.BOOL)
    );

    public static final ComponentType<Boolean> WATERLOGGED = register("waterlogged", builder ->
            builder.codec(com.mojang.serialization.Codec.BOOL)
    );

    private static <T> ComponentType<T> register(final String name, final UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of("interactivestuff", name),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    public static void init() {}
}