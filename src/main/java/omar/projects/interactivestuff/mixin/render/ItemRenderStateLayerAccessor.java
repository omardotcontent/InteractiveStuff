package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.LayerRenderState.class)
public interface ItemRenderStateLayerAccessor {

    @Accessor("renderLayer")
    RenderLayer getInteractiveLayer();

    @Accessor("renderLayer")
    void setInteractiveLayer(RenderLayer layer);
}