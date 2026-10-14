package omar.projects.interactivestuff.mixin.player;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 1.21.5 has the native flag but no public getter yet. */
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("jumping") boolean interactivestuff$isJumping();
    @Invoker("isAffectedByFluids") boolean interactivestuff$affectedByFluids();
}
