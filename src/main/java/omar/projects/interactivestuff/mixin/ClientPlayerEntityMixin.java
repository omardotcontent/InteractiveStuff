package omar.projects.interactivestuff.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public final class ClientPlayerEntityMixin {

    @Inject(method = "swingHand", at = @At("HEAD"))
    private void onSwingHand(final Hand hand, final CallbackInfo ci) {
        final ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.MISS) {
            if (player.getStackInHand(hand).isOf(Items.BELL)) {
                player.playSound(SoundEvents.BLOCK_BELL_USE, 1.0f, 1.0f);
            }
        }
    }
}