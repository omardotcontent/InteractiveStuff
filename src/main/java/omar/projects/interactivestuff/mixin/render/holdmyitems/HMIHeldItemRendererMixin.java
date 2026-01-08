package omar.projects.interactivestuff.mixin.render.holdmyitems;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.holdmylua.source.mixin.render.HeldItemRendererMixin", priority = 1500)
public class HMIHeldItemRendererMixin {

    @TargetHandler(
            mixin = "com.holdmylua.source.mixin.render.HeldItemRendererMixin",
            name = "renderOverhaul"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/holdmylua/source/mixin/render/HeldItemRendererMixin;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"
            )
    )
    private void interactivestuff$redirectRenderItem(
            net.minecraft.client.render.item.HeldItemRenderer instance,
            LivingEntity entity,
            ItemStack stack,
            ItemDisplayContext context,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light
    ) {
        ItemStack processedStack = stack;

        if (!stack.isEmpty()) {
            ItemStack modified = ScriptInterpreter.itemUpdate(stack, matrices);
            if (modified != null) {
                processedStack = modified;
            }
        }

        instance.renderItem(entity, processedStack, context, matrices, queue, light);
    }
}
