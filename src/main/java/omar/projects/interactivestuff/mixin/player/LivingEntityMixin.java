package omar.projects.interactivestuff.mixin.player;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 2000)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("TAIL"))
    private void interactivestuff$damage(
            final ServerWorld world,
            final DamageSource source,
            final float amount,
            final CallbackInfoReturnable<Boolean> cir) {
        ScriptInterpreter.onDamage(source, amount, cir.getReturnValue());
    }
}
