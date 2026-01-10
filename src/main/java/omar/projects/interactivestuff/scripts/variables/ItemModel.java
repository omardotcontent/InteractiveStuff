package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;

@VynType(name = "ItemModel")
public final class ItemModel {

    private static int NEXT_SEED = 1;

    private final int seed = NEXT_SEED++;

    private final Item item = new Item(new ItemStack(Items.STICK));

    private double x, y, z;
    private double rx, ry, rz;
    private double sx = 1, sy = 1, sz = 1;

    @VynConstructor
    public ItemModel() {
        ItemModelRenderRegistry.ACTIVE.add(this);
    }

    // ---------- Item control ----------

    @VynFunc
    public void setItemModel(final String item) {
        this.item.setDataComponent("minecraft:item_model",item);
    }

    @VynFunc
    public Item getItem() {
        return item;
    }

    // ---------- Transform API ----------

    @VynFunc
    public void translate(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @VynFunc
    public void rotate(double x, double y, double z) {
        this.rx += x;
        this.ry += y;
        this.rz += z;
    }

    @VynFunc
    public void scale(double x, double y, double z) {
        this.sx *= x;
        this.sy *= y;
        this.sz *= z;
    }

    // ---------- Renderer ----------

    public int getUniqueSeed() {
        return seed;
    }

    public void apply(MatrixStack matrices) {
        matrices.translate(x, y, z);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) rx));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) ry));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rz));
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    public ItemStack getFinalStack() {
        return item.getFinalItemStack(); // AIR if never set
    }
}

