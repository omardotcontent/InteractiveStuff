package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import omar.projects.interactivestuff.handlers.InteractionHandler;
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
        final HitResult target = client.crosshairTarget;
        BlockPos pos = null;

        if (target instanceof BlockHitResult blockHit) {
            pos = blockHit.getBlockPos();
        } else if (target != null) {
            pos = BlockPos.ofFloored(target.getPos());
        } else if (client.player != null) {
            pos = client.player.getBlockPos();
        }

        if (pos != null) {
            InteractionHandler.getInstance().handleBlockInteraction(client, hand);
        }
    }
}