package omar.projects.interactivestuff.mixin.render;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.item.ItemStackRenderState$LayerRenderState")
public interface ItemRenderStateLayerAccessor {

    @Mutable
    @Accessor("foilType")
    void setInteractiveGlint(ItemStackRenderState.FoilType glint);

    @Accessor("foilType")
    ItemStackRenderState.FoilType getGlint();

    @Accessor("itemTransform")
    ItemTransform getItemTransform();
}