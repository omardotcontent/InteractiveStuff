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
import omar.projects.interactivestuff.scripts.variables.Item;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = HeldItemRenderer.class, priority = 2000)
public class HeldItemRendererMixin {

    @Final
    @Shadow private ItemModelManager itemModelManager;



    @Inject(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void interactivestuff$renderItem(
            final LivingEntity entity,
            final ItemStack stack,
            final ItemDisplayContext renderMode,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue orderedRenderCommandQueue,
            final int light,
            final CallbackInfo ci
    ) {

        final Item scriptItem = ScriptInterpreter.itemUpdate(stack);

        if (scriptItem == null) {
            return;
        }

        final ItemStack modifiedStack = scriptItem.getFinalItemStack();

        if (!modifiedStack.isEmpty()) {
            final ItemRenderState itemRenderState = new ItemRenderState();

            matrices.push();

            // 🔥 APPLY SCRIPT TRANSFORMS
            scriptItem.apply(matrices);

            this.itemModelManager.clearAndUpdate(
                    itemRenderState,
                    modifiedStack,
                    renderMode,
                    entity.getEntityWorld(),
                    entity,
                    entity.getId() + renderMode.ordinal()
            );

            itemRenderState.render(matrices, orderedRenderCommandQueue, light, OverlayTexture.DEFAULT_UV, 0);

            matrices.pop();
        }


        for (ItemModel model : ItemModelRenderRegistry.ACTIVE) {

            ItemStack modelFinalStack = model.getFinalStack();
            if (stack.isEmpty()) continue;

            matrices.push();
            model.apply(matrices);

            final ItemRenderState state = new ItemRenderState();

            this.itemModelManager.clearAndUpdate(
                    state,
                    modelFinalStack,
                    renderMode,
                    entity.getEntityWorld(),
                    entity,
                    entity.getId() + renderMode.ordinal() + model.getUniqueSeed()
            );

            state.render(matrices, orderedRenderCommandQueue, light, OverlayTexture.DEFAULT_UV, 0);

            matrices.pop();
        }

        ItemModelRenderRegistry.clear();

        ci.cancel();
    }

}