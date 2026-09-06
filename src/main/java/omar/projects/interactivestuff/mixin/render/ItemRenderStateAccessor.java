package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemRenderStateAccessor {

    @Accessor("activeLayerCount")
    int getLayerCount();

    @Accessor("layers")
    ItemStackRenderState.LayerRenderState[] getLayers();

}