package omar.projects.interactivestuff.mixin.client;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = KeyboardHandler.class)
public abstract class KeyboardMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void interactivestuff$keyPress(
            long handle, int action, KeyEvent event, CallbackInfo ci) {
        if (action == 1) ScriptInterpreter.onKeyPress(event.input());
    }
}
