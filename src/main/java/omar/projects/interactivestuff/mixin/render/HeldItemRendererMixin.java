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

import java.util.Arrays;


@Mixin(value = HeldItemRenderer.class, priority = 2000)
public final class HeldItemRendererMixin {

    @Final
    @Shadow private ItemModelManager itemModelManager;

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At("HEAD"), cancellable = true)
    private void interactivestuff$renderItem(
            final LivingEntity entity,
            final ItemStack stack,
            final ItemDisplayContext renderMode,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue queue,
            final int light,
            final CallbackInfo ci) {


        final ItemModel mainItem = ScriptInterpreter.itemUpdate(stack);


        if (mainItem == null && ItemModelRenderRegistry.ACTIVE.isEmpty()) {
            return;
        }

        ci.cancel();

        if (mainItem != null) {
            renderScriptItem(mainItem, entity, renderMode, matrices, queue, light);
        }

        for (final ItemModel extraModel : ItemModelRenderRegistry.ACTIVE) {
            renderScriptItem(extraModel, entity, renderMode, matrices, queue, light);
        }

        ItemModelRenderRegistry.clear();
    }

    @Unique
    private void renderScriptItem(
            final ItemModel item,
            final LivingEntity entity,
            final ItemDisplayContext mode,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue queue,
            final int light) {
        final ItemStack stack = item.getFinalStack();
        if (stack.isEmpty()) return;

        matrices.push();
        item.apply(matrices);

        final ItemRenderState state = new ItemRenderState();
        this.itemModelManager.clearAndUpdate(
                state, stack, mode, entity.getEntityWorld(),
                entity, entity.getId() + mode.ordinal() + item.getUniqueSeed()
        );


        final int renderColor = item.getFinalColor();
        applyColorToState(state, renderColor);
        switch (item.getGlint()) {
            case 2:
                applyGlint(state, ItemRenderState.Glint.SPECIAL);
                break;
            case 1:
                applyGlint(state, ItemRenderState.Glint.STANDARD);
                break;
            case 0:
                applyGlint(state, ItemRenderState.Glint.NONE);
        }
        state.render(matrices, queue, item.getLight() == -1 ? light : item.getLight(), OverlayTexture.DEFAULT_UV, 0);

        matrices.pop();
    }

    @Unique
    private void applyColorToState(final ItemRenderState state, final int argbColor) {
        final ItemRenderStateAccessor accessor = (ItemRenderStateAccessor) state;
        final int layerCount = accessor.getLayerCount();
        final ItemRenderState.LayerRenderState[] layers = accessor.getLayers();


        for (int i = 0; i < layerCount; i++) {
            final ItemRenderState.LayerRenderState layer = layers[i];

            int quadCount = layer.getQuads().size();

            if (quadCount > 0) {
                final int[] tints = layer.initTints(quadCount);

                Arrays.fill(tints, argbColor);
            }
        }
    }


    @Unique
    private void applyGlint(final ItemRenderState state, ItemRenderState.Glint glintType) {
        // 1. Cast the state to your main accessor to get the layers
        final ItemRenderStateAccessor stateAcc = (ItemRenderStateAccessor) state;
        final ItemRenderState.LayerRenderState[] layers = stateAcc.getLayers();
        final int count = stateAcc.getLayerCount();

        // 2. Iterate through each layer and cast the layer itself to the LayerAccessor
        for (int i = 0; i < count; i++) {
            final ItemRenderStateLayerAccessor layerAcc = (ItemRenderStateLayerAccessor) layers[i];
            layerAcc.setInteractiveGlint(glintType);
        }
    }

}