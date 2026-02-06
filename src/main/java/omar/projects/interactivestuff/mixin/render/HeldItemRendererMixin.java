package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.handlers.PivotDebugRenderer;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(value = HeldItemRenderer.class, priority = 2000)
public abstract class HeldItemRendererMixin {

    @Final
    @Shadow private ItemModelManager itemModelManager;

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
            at = @At("HEAD"),
            cancellable = true)
    private void interactivestuff$renderItem(
            final LivingEntity entity,
            final ItemStack stack,
            final ItemDisplayContext renderMode,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue queue,
            final int light,
            final CallbackInfo ci) {

        final ItemModel mainItem = ScriptInterpreter.itemUpdate(stack, renderMode);

        if (mainItem == null && ItemModelRenderRegistry.ACTIVE.isEmpty()) {
            return;
        }

        ci.cancel();

        final boolean debugMode = ConfigHandler.INSTANCE != null && ConfigHandler.INSTANCE.resourcePackDebugMode;
        final PivotDebugRenderer pivotDebugRenderer = PivotDebugRenderer.INSTANCE;

        if (mainItem != null) {
            renderScriptItem(mainItem, entity, renderMode, matrices, queue, light);
            if (debugMode) {
                pivotDebugRenderer.addPivotPoint(
                        mainItem.getPivotX(), mainItem.getPivotY(), mainItem.getPivotZ(),
                        mainItem.getTranslateX(), mainItem.getTranslateY(), mainItem.getTranslateZ()
                );
            }
        }

        for (final ItemModel extraModel : ItemModelRenderRegistry.ACTIVE) {
            renderScriptItem(extraModel, entity, renderMode, matrices, queue, light);
            if (debugMode) {
                pivotDebugRenderer.addPivotPoint(
                        extraModel.getPivotX(), extraModel.getPivotY(), extraModel.getPivotZ(),
                        extraModel.getTranslateX(), extraModel.getTranslateY(), extraModel.getTranslateZ()
                );
            }
        }

        ItemModelRenderRegistry.clear();

        if (debugMode) {
            pivotDebugRenderer.render(matrices);
        }
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

        if (stack.isEmpty()) {
            return;
        }

        matrices.push();
        item.apply(matrices);

        final ItemRenderState state = new ItemRenderState();

        this.itemModelManager.clearAndUpdate(
                state, stack, mode, entity.getEntityWorld(),
                entity, entity.getId() + mode.ordinal() + item.getUniqueSeed()
        );

        if (ConfigHandler.INSTANCE.resourcePackColorChanging) {
            applyColorToState(item, state);
        }

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

        final ItemRenderStateAccessor accessor = (ItemRenderStateAccessor) state;

        for (int j = 0; j < accessor.getLayerCount(); j++) {

            final ItemRenderState.LayerRenderState layer = accessor.getLayers()[j];
            final ItemRenderStateLayerAccessor layerAccessor = (ItemRenderStateLayerAccessor) layer;
            final RenderLayer renderLayer = layerAccessor.getRenderLayer();

            if (renderLayer != null) {
                matrices.push();
                layerAccessor.getTransform().apply(mode.isLeftHand(), matrices.peek());

                queue.submitItem(
                        matrices,
                        accessor.getDisplayContext(),
                        item.getLight() == -1 ? light : item.getLight(),
                        OverlayTexture.DEFAULT_UV,
                        0,
                        layer.initTints(layer.getQuads().size()),
                        layer.getQuads(),
                        renderLayer,
                        layerAccessor.getGlint()
                );

                matrices.pop();
            }
        }

        matrices.pop();
    }

    @Unique
    private void applyColorToState(final ItemModel item, final ItemRenderState state) {
        final ItemRenderStateAccessor accessor = (ItemRenderStateAccessor) state; //
        final ItemRenderState.LayerRenderState[] layers = accessor.getLayers(); //

        for (int i = 0; i < accessor.getLayerCount(); i++) { //
            final ItemRenderState.LayerRenderState layer = layers[i]; //
            final List<BakedQuad> quads = layer.getQuads(); //

            if (quads.isEmpty()) { //
                continue; //
            }

            final int[] tints = layer.initTints(quads.size()); //

            for (int j = 0; j < quads.size(); j++) { //
                int tintIndex = quads.get(j).tintIndex(); //

                if (tintIndex != -1) {

                    tints[j] = item.getFinalColorForIndex(tintIndex); //
                } else {
                    tints[j] = item.getFinalColorForIndex(-1); //
                }
            }
        }
    }


    @Unique
    private void applyGlint(final ItemRenderState state, final ItemRenderState.Glint glintType) {
        final ItemRenderStateAccessor stateAcc = (ItemRenderStateAccessor) state;
        final ItemRenderState.LayerRenderState[] layers = stateAcc.getLayers();
        final int count = stateAcc.getLayerCount();

        for (int i = 0; i < count; i++) {
            final ItemRenderStateLayerAccessor layerAcc = (ItemRenderStateLayerAccessor) layers[i];
            layerAcc.setInteractiveGlint(glintType);
        }
    }

}