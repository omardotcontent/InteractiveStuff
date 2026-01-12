package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = HeldItemRenderer.class, priority = 2000)
public class HeldItemRendererMixin {

    @Final
    @Shadow private ItemModelManager itemModelManager;

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At("HEAD"), cancellable = true)
    private void interactivestuff$renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, CallbackInfo ci) {

        // 1. Handle the main item (the one actually in the hand)
        final ItemModel mainItem = ScriptInterpreter.itemUpdate(stack);
        if (mainItem != null) {
            renderScriptItem(mainItem, entity, renderMode, matrices, queue, light);
        }

        // 2. Handle any extra models created via `new Item()`
        for (ItemModel extraModel : ItemModelRenderRegistry.ACTIVE) {
            renderScriptItem(extraModel, entity, renderMode, matrices, queue, light);
        }

        ItemModelRenderRegistry.clear();
        ci.cancel();
    }

    // Helper method to avoid duplicating the ItemRenderState logic
    @Unique
    private void renderScriptItem(ItemModel item, LivingEntity entity, ItemDisplayContext mode, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        ItemStack stack = item.getFinalStack();
        if (stack.isEmpty()) return;

        matrices.push();
        item.apply(matrices);

        ItemRenderState state = new ItemRenderState();
        this.itemModelManager.clearAndUpdate(
                state, stack, mode, entity.getEntityWorld(),
                entity, entity.getId() + mode.ordinal() + item.getUniqueSeed()
        );

        state.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, item.getRenderColor());

        matrices.pop();
    }

}