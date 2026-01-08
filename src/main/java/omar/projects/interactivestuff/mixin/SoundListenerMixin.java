package omar.projects.interactivestuff.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.variables.Position;
import omar.projects.interactivestuff.scripts.variables.Sound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SoundSystem.class)
public abstract class SoundListenerMixin {

    @Unique
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("TAIL"))
    private void onPlaySound(final SoundInstance sound, final CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        if (client.player == null || client.world == null) {
            return;
        }
        if(sound.getSound() != null && sound.getCategory() != SoundCategory.UI && sound.getCategory() != SoundCategory.AMBIENT) ScriptInterpreter.onPlaySound(new Sound(
                sound.getId().toString(),
                sound.getVolume(),
                sound.getPitch(),
                new Position((int) sound.getX(), (int) sound.getY(), (int) sound.getZ())));
    }
}