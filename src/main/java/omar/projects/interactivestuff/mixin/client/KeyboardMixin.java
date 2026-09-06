package omar.projects.interactivestuff.mixin.client;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = KeyboardHandler.class)
public abstract class KeyboardMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void interactivestuff$damage(
            long handle, int action, KeyEvent event, CallbackInfo ci) {
        ScriptInterpreter.onKeyPress(event.input());
    }
}
