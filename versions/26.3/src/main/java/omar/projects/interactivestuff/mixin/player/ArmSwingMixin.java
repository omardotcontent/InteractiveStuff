package omar.projects.interactivestuff.mixin.player;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import omar.projects.interactivestuff.handlers.InteractionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ArmSwingMixin {
    @Inject(method = "swing", at = @At("RETURN"))
    private void interactivestuff$swingHand(InteractionHand hand, SwingAnimation animation, boolean force,
                                          CallbackInfoReturnable<Boolean> ci) {
        Minecraft client = Minecraft.getInstance();
        if ((Object) this == client.player && ci.getReturnValueZ())
            InteractionHandler.getInstance().handleBlockInteraction(client, hand);
    }
}
