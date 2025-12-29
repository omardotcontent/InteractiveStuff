package omar.projects.interactivestuff.mixin.render.locomotion;

import com.trainguy9512.locomotion.render.FirstPersonPlayerRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.handlers.ItemVisualHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FirstPersonPlayerRenderer.class)
public abstract class LocomotionFirstPersonItemMixin {

    @ModifyVariable(
            method = "renderItem",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0,
            require = 0
    )
    private ItemStack modifyLocomotionRenderedStack(final ItemStack stack) {
        return ItemVisualHelper.applyVisuals(
                stack,
                MinecraftClient.getInstance().player
        );
    }
}
