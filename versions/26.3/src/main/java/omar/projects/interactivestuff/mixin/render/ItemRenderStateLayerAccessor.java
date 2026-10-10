package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** The small, version-specific boundary to Minecraft's native layer submission. */
@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemRenderStateLayerAccessor {
    @Accessor("quads")
    net.minecraft.client.resources.model.geometry.ItemQuads interactivestuff$getQuads();

    @Accessor("foilType")
    ItemStackRenderState.FoilType interactivestuff$getFoil();

    @Accessor("tintLayers")
    IntList interactivestuff$getTints();

    @Accessor("specialRenderer")
    SpecialModelRenderer<Object> interactivestuff$getSpecialRenderer();

    @Invoker("submit")
    void interactivestuff$submit(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, int outline);

    @Invoker("applyTransform")
    void interactivestuff$applyTransform(PoseStack.Pose pose);
}
