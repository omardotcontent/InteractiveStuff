package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import org.joml.Quaternionf;

@VynType(name = "ItemModel")
public final class ItemModel {

    private static int NEXT_SEED = 1;
    private final int seed = NEXT_SEED++;

    private final Quaternionf rotation = new Quaternionf();
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
     * Rotates the item around its OWN axes (Local Space).
     * If the item is upside down, rotateLocal(0, 10, 0) will spin it around its upside-down Y axis.
     */
    @VynFunc
    public void rotateLocal(double x, double y, double z) {
        if (x != 0) this.rotation.rotateX((float) Math.toRadians(x));
        if (y != 0) this.rotation.rotateY((float) Math.toRadians(y));
        if (z != 0) this.rotation.rotateZ((float) Math.toRadians(z));
    }

    /**
     * Rotates the item around the WORLD axes (Global/Camera Space).
     * No matter how the item is rotated, rotateGlobal(0, 10, 0) will always spin it horizontally relative to the ground.
     */
    @VynFunc
    public void rotateGlobal(double x, double y, double z) {
        final Quaternionf globalRot = new Quaternionf()
                .rotateX((float) Math.toRadians(x))
                .rotateY((float) Math.toRadians(y))
                .rotateZ((float) Math.toRadians(z));

        this.rotation.premul(globalRot);
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
        matrices.multiply(rotation, (float) px, (float) py, (float) pz);


        // Apply the accumulated Quaternion rotation around the Pivot Point
        matrices.multiply(rotation, (float) px, (float) py, (float) pz);
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    public ItemStack getFinalStack() {
        return item.getFinalItemStack();
    }
}