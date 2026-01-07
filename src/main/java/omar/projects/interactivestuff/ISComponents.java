package omar.projects.interactivestuff;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import java.util.function.UnaryOperator;

public final class ISComponents {

    public static final ComponentType<Boolean> VIBRATING = register("vibrating");

    public static final ComponentType<Boolean> CALIBRATED_VIBRATING = register("calibrated_vibrating");

    public static final ComponentType<Boolean> WATERLOGGED = register("waterlogged");

    private static ComponentType<Boolean> register(final String name) {
        final UnaryOperator<ComponentType.Builder<Boolean>> builderOperator = builder ->
                builder.codec(com.mojang.serialization.Codec.BOOL);
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(IS.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    public static void init() {
    }
}