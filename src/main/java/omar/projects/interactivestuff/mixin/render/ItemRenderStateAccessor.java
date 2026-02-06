package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.class)
public interface ItemRenderStateAccessor {
    @Accessor("layerCount")
    int getLayerCount();

    @Accessor("layers")
    ItemRenderState.LayerRenderState[] getLayers();

    @Accessor("displayContext")
    ItemDisplayContext getDisplayContext();
}