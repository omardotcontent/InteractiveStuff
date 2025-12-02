package omar.projects.interactivestuff.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public final class ClientPlayerEntityMixin {

    @Unique
    private final MinecraftClient client = MinecraftClient.getInstance();


    @Inject(method = "swingHand", at = @At("HEAD"))
    private void onSwingHand(final Hand hand, final CallbackInfo ci) {
        if (client.player.getStackInHand(hand).isOf(Items.BELL)) {
            client.player.playSound(SoundEvents.BLOCK_BELL_USE, 1.0f, 1.0f);
        }
    }
}