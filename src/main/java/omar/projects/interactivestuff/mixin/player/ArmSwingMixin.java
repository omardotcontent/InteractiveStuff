package omar.projects.interactivestuff.mixin.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import omar.projects.interactivestuff.handlers.InteractionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LocalPlayer.class)
public abstract class ArmSwingMixin {

    @Unique
    private final Minecraft client = Minecraft.getInstance();


    @Inject(method = "swing", at = @At("HEAD"))
    private void interactivestuff$swingHand(final InteractionHand hand, CallbackInfo ci) {
        InteractionHandler.getInstance().handleBlockInteraction(client, hand);
    }
}