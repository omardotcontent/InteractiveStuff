package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.LayerRenderState.class)
public interface ItemRenderStateLayerAccessor {

    @Mutable
    @Accessor("glint")
    void setInteractiveGlint(ItemRenderState.Glint glint);
}