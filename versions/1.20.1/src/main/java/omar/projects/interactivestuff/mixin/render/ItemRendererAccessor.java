package omar.projects.interactivestuff.mixin.render;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
 @Accessor("itemColors") ItemColors interactivestuff$itemColors();
}
