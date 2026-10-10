package omar.projects.interactivestuff.mixin.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.variables.Position;
import omar.projects.interactivestuff.scripts.variables.Sound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(SoundEngine.class)
public abstract class SoundListenerMixin {

    @Unique
    private final Minecraft client = Minecraft.getInstance();

    @Inject(method = "play", at = @At("TAIL"))
    private void interactivestuff$play(final SoundInstance sound, CallbackInfo ci) {
        if (client.player == null || client.level == null) {
            return;
        }
        if (sound.getSound() == null || "UI".equals(sound.getSource().name()) || sound.getSource() == SoundSource.AMBIENT) {
            return;
        }
        ScriptInterpreter.onPlaySound(new Sound(
                sound.getLocation().toString(),
                sound.getVolume(),
                sound.getPitch(),
                new Position((int) sound.getX(), (int) sound.getY(), (int) sound.getZ())));
    }
}
