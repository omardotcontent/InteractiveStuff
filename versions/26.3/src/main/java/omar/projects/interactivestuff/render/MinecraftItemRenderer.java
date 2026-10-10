package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.mixin.render.ItemRenderStateAccessor;
import omar.projects.interactivestuff.mixin.render.ItemRenderStateLayerAccessor;
import omar.projects.interactivestuff.scripts.variables.ItemModel;

import java.util.List;

/** Minecraft 26.2 rendering adapter. Port this boundary rather than the script/runtime systems. */
public final class MinecraftItemRenderer {
    private static final ColoredQuadCache COLORS = new ColoredQuadCache();
    private static final int[] NO_TINTS = new int[0];

    private MinecraftItemRenderer() {}
    public static void clearCaches() { COLORS.clear(); }

    public static void submit(ItemModel item, ItemModelResolver resolver, LivingEntity entity,
                              ItemDisplayContext mode, PoseStack matrices, SubmitNodeCollector queue, int light) {
        var stack = item.getFinalStack();
        if (stack == null || stack.isEmpty()) return;

        // Submission retains quad lists until the render queue consumes them. Never recycle this state mid-frame.
        ItemStackRenderState state = new ItemStackRenderState();
        resolver.updateForTopItem(state, stack, mode, entity.level(), entity,
                entity.getId() + mode.ordinal() + item.getUniqueSeed());
        ItemRenderStateAccessor layers = (ItemRenderStateAccessor) state;
        int foil = item.getGlint();
        if (foil >= 0) {
            var type = switch (foil) {
                case 2 -> ItemStackRenderState.FoilType.SPECIAL;
                case 1 -> ItemStackRenderState.FoilType.STANDARD;
                default -> ItemStackRenderState.FoilType.NONE;
            };
            for (int i = 0; i < layers.getLayerCount(); i++) layers.getLayers()[i].setFoilType(type);
        }

        int effectiveLight = item.getLight() == -1 ? light : item.getLight();
        boolean recolor = ConfigHandler.INSTANCE.resourcePackColorChanging && item.hasColorChanges();
        if (!item.isPartialSelection() && !recolor) {
            matrices.pushPose();
            try {
                item.apply(matrices);
                state.submit(matrices, queue, effectiveLight, OverlayTexture.NO_OVERLAY, 0);
            } finally {
                matrices.popPose();
            }
            return;
        }

        for (int i = 0; i < layers.getLayerCount(); i++) {
            var layer = layers.getLayers()[i];
            var access = (ItemRenderStateLayerAccessor) layer;
            if (access.interactivestuff$getSpecialRenderer() != null) {
                matrices.pushPose();
                try {
                    item.apply(matrices);
                    access.interactivestuff$submit(matrices, queue, effectiveLight, OverlayTexture.NO_OVERLAY, 0);
                } finally {
                    matrices.popPose();
                }
                continue;
            }
            List<BakedQuad> quads = access.interactivestuff$getQuads().all();
            if (quads.isEmpty()) continue;
            int[] nativeTints = access.interactivestuff$getTints() == null ? NO_TINTS
                    : access.interactivestuff$getTints().toIntArray();
            QuadSelection selection = item.isPartialSelection()
                    ? QuadSelection.resolve(item.getQuadStart(), item.getQuadEnd(), quads.size())
                    : new QuadSelection(0, quads.size());
            if (!item.isExclusive()) {
                submitRange(item, mode, matrices, queue, access, effectiveLight, quads, nativeTints,
                        0, selection.start(), false, false);
            }
            submitRange(item, mode, matrices, queue, access, effectiveLight, quads, nativeTints,
                    selection.start(), selection.end(), true, recolor);
            if (!item.isExclusive()) {
                submitRange(item, mode, matrices, queue, access, effectiveLight, quads, nativeTints,
                        selection.end(), quads.size(), false, false);
            }
        }
    }


    public static org.joml.Matrix4f anchor(ItemModel item, ItemModelResolver resolver, LivingEntity entity,
                                          ItemDisplayContext mode, PoseStack matrices) {
        ItemStackRenderState state = new ItemStackRenderState();
        resolver.updateForTopItem(state, item.getFinalStack(), mode, entity.level(), entity,
                entity.getId() + mode.ordinal() + item.getUniqueSeed());
        var layers = (ItemRenderStateAccessor) state;
        matrices.pushPose();
        try {
            item.apply(matrices);
            if (layers.getLayerCount() > 0) ((ItemRenderStateLayerAccessor) layers.getLayers()[0]).interactivestuff$applyTransform(matrices.last());
            return ParticleView.capture(matrices.last().pose());
        } finally { matrices.popPose(); }
    }

    private static void submitRange(ItemModel item, ItemDisplayContext mode, PoseStack matrices,
                                    SubmitNodeCollector queue, ItemRenderStateLayerAccessor layer, int light,
                                    List<BakedQuad> quads, int[] nativeTints, int start, int end,
                                    boolean selected, boolean recolor) {
        if (start >= end) return;
        int[] tints = nativeTints;
        List<BakedQuad> geometry = quads;
        if (recolor) {
            var colored = COLORS.get(quads);
            geometry = colored.quads();
            tints = new int[colored.slots()];
            for (int slot = 0; slot < tints.length; slot++) {
                int index = slot - 1;
                int original = index >= 0 && index < nativeTints.length ? nativeTints[index] : 0xFFFFFFFF;
                tints[slot] = item.getUseSelectionColor() ? item.getSelectionColor()
                        : item.getFinalColorForIndex(index, original);
            }
        }
        matrices.pushPose();
        try {
            if (selected) item.apply(matrices);
            layer.interactivestuff$applyTransform(matrices.last());
            queue.submitItem(matrices, mode, light, OverlayTexture.NO_OVERLAY, 0, tints,
                    net.minecraft.client.resources.model.geometry.ItemQuads.split(start == 0 && end == geometry.size() ? geometry : geometry.subList(start, end)),
                    layer.interactivestuff$getFoil());
        } finally {
            matrices.popPose();
        }
    }
}
