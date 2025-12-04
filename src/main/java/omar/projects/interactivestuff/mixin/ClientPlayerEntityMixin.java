package omar.projects.interactivestuff.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
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
        InteractionHandler.getInstance().handleBlockInteraction(client, BlockPos.ofFloored(client.crosshairTarget.getPos()), hand);
    }
}