package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.json.Transformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.render.item.ItemRenderState$LayerRenderState")
public interface ItemRenderStateLayerAccessor {

    @Mutable
    @Accessor("glint")
    void setInteractiveGlint(ItemRenderState.Glint glint);

    @Accessor("glint")
    ItemRenderState.Glint getGlint();

    @Accessor("renderLayer")
    RenderLayer getRenderLayer();

    @Accessor("transform")
    Transformation getTransform();
}