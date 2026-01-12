package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis; // Ensure this is imported
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;

@VynType(name = "ItemModel")
public final class ItemModel {

    private static int NEXT_SEED = 1;
    private final int seed = NEXT_SEED++;

    // Replaced Quaternionf with explicit Euler angles (in degrees)
    private float rotX = 0;
    private float rotY = 0;
    private float rotZ = 0;

    private final Item item = new Item(new ItemStack(Items.STICK));

    private double x, y, z;
    private double sx = 1, sy = 1, sz = 1;
    private double px = 0.5, py = 0.5, pz = 0.5;


    @VynConstructor
    public ItemModel() {
        ItemModelRenderRegistry.ACTIVE.add(this);
    }

    // ---------- Item control ----------
    @VynFunc
    public void setItemModel(final String item) {
        this.item.setDataComponent("minecraft:item_model", item);
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
    public void setPivot(double x, double y, double z) {
        this.px = x;
        this.py = y;
        this.pz = z;
    }

    /**
     * Accumulates rotation degrees for the X, Y, and Z axes.
     */
    @VynFunc
    public void rotate(double x, double y, double z) {
        this.rotX += (float) x;
        this.rotY += (float) y;
        this.rotZ += (float) z;
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


        matrices.translate(px, py, pz);
        if (rotX != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        if (rotY != 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        if (rotZ != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        matrices.translate(-px, -py, -pz);

        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    public ItemStack getFinalStack() {
        return item.getFinalItemStack();
    }
}