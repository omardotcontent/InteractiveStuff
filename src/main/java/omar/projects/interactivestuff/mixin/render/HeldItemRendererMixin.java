package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
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

import java.util.List;


@Mixin(value = ItemInHandRenderer.class, priority = 2000)
public abstract class HeldItemRendererMixin {



    @Final
    @Shadow
    private ItemModelResolver itemModelResolver;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void interactivestuff$renderItem(
            final LivingEntity entity,
            final ItemStack stack,
            final ItemDisplayContext renderMode,
            final PoseStack matrices,
            final SubmitNodeCollector queue,
            int light,
            CallbackInfo ci) {
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
            final PoseStack matrices,
            final SubmitNodeCollector queue,
            final int light) {

        final ItemStack stack = item.getFinalStack();

        if (stack.isEmpty()) {
            return;
        }

        final ItemStackRenderState state = new ItemStackRenderState();
        this.itemModelResolver.updateForTopItem(state, stack, mode, entity.level(), entity,
                entity.getId() + mode.ordinal() + item.getUniqueSeed());

        switch (item.getGlint()) {
            case 2 -> this.applyGlint(state, ItemStackRenderState.FoilType.SPECIAL);
            case 1 -> this.applyGlint(state, ItemStackRenderState.FoilType.STANDARD);
            case 0 -> this.applyGlint(state, ItemStackRenderState.FoilType.NONE);
        }

        final ItemRenderStateAccessor accessor = (ItemRenderStateAccessor) state;

        for (int j = 0; j < accessor.getLayerCount(); j++) {
            final ItemStackRenderState.LayerRenderState layer = accessor.getLayers()[j];
            final ItemRenderStateLayerAccessor layerAccessor = (ItemRenderStateLayerAccessor) layer;
            final List<BakedQuad> allQuads = layer.prepareQuadList();

            if (allQuads.isEmpty())
                continue;

            final int listSize = allQuads.size();

            if (!item.isPartialSelection()) {
                this.renderQuadRange(allQuads, layerAccessor, mode, matrices, queue, light, item, true);
                continue;
            }

            int start = Mth.clamp(item.getQuadStart(), 0, listSize);
            int end = (item.getQuadEnd() == 0) ? listSize : Mth.clamp(item.getQuadEnd(), 0, listSize);

            if (start > end)
                start = end;

            if (item.isExclusive()) {
                if (end > start) {
                    this.renderQuadRange(allQuads.subList(start, end), layerAccessor, mode, matrices, queue, light, item, true);
                }
            } else {
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
    }

    @Unique
    private void renderQuadRange(
            final List<BakedQuad> quads,
            final ItemRenderStateLayerAccessor layerAccessor,
            final ItemDisplayContext mode,
            final PoseStack matrices,
            final SubmitNodeCollector queue,
            final int light,
            final ItemModel item,
            final boolean isSelectedRange) {

        matrices.pushPose();

        if (isSelectedRange) {
            item.applyParentChain(matrices);
            item.applySelf(matrices);
        }

        layerAccessor.getItemTransform().apply(mode.leftHand(), matrices.last());

        final int[] tints = new int[quads.size()];

        for (int i = 0; i < quads.size(); i++) {
            final BakedQuad quad = quads.get(i);
            final int quadTintIndex = quad.materialInfo().tintIndex();

            if (!isSelectedRange || !ConfigHandler.INSTANCE.resourcePackColorChanging) {
                tints[i] = 0xFFFFFFFF;
                continue;
            }

            if (item.getUseSelectionColor()) {
                tints[i] = item.getSelectionColor();
                continue;
            }

            tints[i] = item.getFinalColorForIndex(quadTintIndex);
        }

        queue.submitItem(
                matrices,
                mode,
                item.getLight() == -1 ? light : item.getLight(),
                OverlayTexture.NO_OVERLAY,
                0,
                tints,
                quads,
                layerAccessor.getGlint());

        matrices.popPose();
    }

    @Unique
    private void applyGlint(final ItemStackRenderState state, final ItemStackRenderState.FoilType glintType) {
        final ItemRenderStateAccessor stateAcc = (ItemRenderStateAccessor) state;
        final int count = stateAcc.getLayerCount();

        for (int i = 0; i < count; i++) {
            ((ItemRenderStateLayerAccessor) stateAcc.getLayers()[i]).setInteractiveGlint(glintType);
        }
    }
}