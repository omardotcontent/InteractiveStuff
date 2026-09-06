package omar.projects.interactivestuff.objects;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.variables.ItemModel;

public class PivotRendererBlock extends ItemModel {

    public PivotRendererBlock(ItemModel parent) {
        super(new ItemStack(Items.BARRIER));
        setLight(15);
        setParent(parent);
        translate(parent.getPivotX(), parent.getPivotY(), parent.getPivotZ());
        scale(0.3D, 0.3D, 0.3D);
        setItemModel("interactivestuff:pivotrendererblock");
        ItemModelRenderRegistry.ACTIVE.add(this);
    }

}