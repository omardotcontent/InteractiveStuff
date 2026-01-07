package omar.projects.interactivestuff.mixin.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public final class HeldItemRendererMixin {

    @Redirect(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"
            )
    )
    private void interactivestuff$redirectRenderItem(
            HeldItemRenderer instance, LivingEntity entity, ItemStack stack, net.minecraft.item.ItemDisplayContext renderMode, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light
    ) {

        matrices.push();

        ItemStack processedStack = stack;

        if (stack != null && !stack.isEmpty()) {
            processedStack = ScriptInterpreter.itemUpdate(stack, matrices);
        }


        instance.renderItem(
                entity,
                processedStack,
                renderMode,
                matrices,
                orderedRenderCommandQueue,
                light
        );


        matrices.pop();
    }
}