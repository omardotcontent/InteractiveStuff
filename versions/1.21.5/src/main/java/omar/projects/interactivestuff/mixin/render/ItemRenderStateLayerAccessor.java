package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** The small, version-specific boundary to Minecraft's native layer submission. */
@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemRenderStateLayerAccessor {
    @Accessor("foilType")
    ItemStackRenderState.FoilType interactivestuff$getFoil();

    @Accessor("tintLayers")
    int[] interactivestuff$getTints();

    @Accessor("specialRenderer")
    SpecialModelRenderer<Object> interactivestuff$getSpecialRenderer();

    @Invoker("render")
    void interactivestuff$render(PoseStack matrices, MultiBufferSource queue, int light, int overlay);

    @Accessor("transform")
    net.minecraft.client.renderer.block.model.ItemTransform interactivestuff$getTransform();

    @Accessor("renderType")
    net.minecraft.client.renderer.RenderType interactivestuff$getRenderType();
}
