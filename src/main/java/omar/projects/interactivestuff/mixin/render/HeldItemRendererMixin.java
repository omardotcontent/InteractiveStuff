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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
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
    @Shadow
    private ItemModelManager itemModelManager;

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

        if (mainItem != null) {
            this.renderScriptItem(mainItem, entity, renderMode, matrices, queue, light);
        }

        for (final ItemModel extraModel : ItemModelRenderRegistry.ACTIVE) {
            this.renderScriptItem(extraModel, entity, renderMode, matrices, queue, light);
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

        if (stack.isEmpty()) {
            return;
        }

        final ItemRenderState state = new ItemRenderState();
        this.itemModelManager.clearAndUpdate(state, stack, mode, entity.getEntityWorld(), entity, entity.getId() + mode.ordinal() + item.getUniqueSeed());

        switch (item.getGlint()) {
            case 2 -> this.applyGlint(state, ItemRenderState.Glint.SPECIAL);
            case 1 -> this.applyGlint(state, ItemRenderState.Glint.STANDARD);
            case 0 -> this.applyGlint(state, ItemRenderState.Glint.NONE);
        }

        final ItemRenderStateAccessor accessor = (ItemRenderStateAccessor) state;

        for (int j = 0; j < accessor.getLayerCount(); j++) {
            final ItemRenderState.LayerRenderState layer = accessor.getLayers()[j];
            final ItemRenderStateLayerAccessor layerAccessor = (ItemRenderStateLayerAccessor) layer;
            final List<BakedQuad> allQuads = layer.getQuads();

            if (layerAccessor.getRenderLayer() == null) continue;
            if (allQuads.isEmpty()) continue;

            final int listSize = allQuads.size();

            if (!item.isPartialSelection()) {
                this.renderQuadRange(allQuads, layerAccessor, mode, matrices, queue, light, item, true);
                continue;
            }

            int start = MathHelper.clamp(item.getQuadStart(), 0, listSize);
            final int rawEnd = item.getQuadEnd();
            int end = (rawEnd == 0) ? listSize : MathHelper.clamp(rawEnd, 0, listSize);

            if (start > end) start = end;

            if (start > 0) {
                this.renderQuadRange(allQuads.subList(0, start), layerAccessor, mode, matrices, queue, light, item, false);
            }

            if (end > start) {
                this.renderQuadRange(allQuads.subList(start, end), layerAccessor, mode, matrices, queue, light, item, true);
            }

            if (end < listSize) {
                this.renderQuadRange(allQuads.subList(end, listSize), layerAccessor, mode, matrices, queue, light, item, false);
            }
        }
    }

    @Unique
    private void renderQuadRange(
            final List<BakedQuad> quads,
            final ItemRenderStateLayerAccessor layerAccessor,
            final ItemDisplayContext mode,
            final MatrixStack matrices,
            final OrderedRenderCommandQueue queue,
            final int light,
            final ItemModel item,
            final boolean isSelectedRange) {

        matrices.push();

        if (isSelectedRange) {
            item.apply(matrices);
        }

        layerAccessor.getTransform().apply(mode.isLeftHand(), matrices.peek());

        final int[] tints = new int[quads.size()];

        for (int i = 0; i < quads.size(); i++) {
            final BakedQuad quad = quads.get(i);
            final int quadTintIndex = quad.tintIndex();

            if (!isSelectedRange || !ConfigHandler.INSTANCE.resourcePackColorChanging) {
                tints[i] = 0xFFFFFFFF;
                continue;
            }

            if (item.getUseSelectionColor()) {
                tints[i] = item.getSelectionColor();
                continue;
            }

            // This calculates the color based on the index found in the quad
            tints[i] = item.getFinalColorForIndex(quadTintIndex);
        }

        RenderLayer finalLayer = layerAccessor.getRenderLayer();
        // Check transparent color on index -1 (Base) if no specific selection color is used
        final int checkColor = isSelectedRange && item.getUseSelectionColor() ? item.getSelectionColor() : item.getFinalColorForIndex(-1);

        if (((checkColor >> 24) & 0xFF) < 255) {
            finalLayer = RenderLayer.getEntityTranslucent(Identifier.of("minecraft", "textures/atlas/blocks.png"));
        }

        queue.submitItem(
                matrices,
                mode,
                item.getLight() == -1 ? light : item.getLight(),
                OverlayTexture.DEFAULT_UV,
                0,
                tints,
                quads,
                finalLayer,
                layerAccessor.getGlint()
        );

        if (isSelectedRange && ConfigHandler.INSTANCE != null && ConfigHandler.INSTANCE.resourcePackDebugMode) {
            PivotDebugRenderer.INSTANCE.submit(matrices, queue, item.getPivotX(), item.getPivotY(), item.getPivotZ());
        }

        matrices.pop();
    }

    @Unique
    private void applyGlint(final ItemRenderState state, final ItemRenderState.Glint glintType) {
        final ItemRenderStateAccessor stateAcc = (ItemRenderStateAccessor) state;
        final int count = stateAcc.getLayerCount();

        for (int i = 0; i < count; i++) {
            ((ItemRenderStateLayerAccessor) stateAcc.getLayers()[i]).setInteractiveGlint(glintType);
        }
    }
}