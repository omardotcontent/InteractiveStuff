package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;
import org.joml.Quaternionf;

@VynType(name = "Item")
public final class Item {

    private final ItemStack originalStack;
    private ItemStack workingStack;
    private boolean modified = false;

    private final Quaternionf rotation = new Quaternionf();
    private double x, y, z;
    private double sx = 1, sy = 1, sz = 1;
    private double px = 0.5, py = 0.5, pz = 0.5;


    public Item(final ItemStack stack) {
        this.originalStack = stack;
        this.workingStack = stack; // Start with original reference
    }

    @VynFunc
    public String getName() {
        return workingStack.getItem().toString();
    }

    @VynFunc
    public void setDataComponent(final String key, final Object value) {
        modificationCheck();
        ScriptItemHandler.apply(workingStack, key, value.toString());
    }

    @VynFunc
    public void setBobbingAnimationTime(final int ticks) {
        modificationCheck();
        workingStack.setBobbingAnimationTime(ticks);
    }

    @VynFunc
    public int getBobbingAnimationTime() {
        return workingStack.getBobbingAnimationTime();
    }

    @VynFunc
    public boolean isDamaged() {
        return workingStack.isDamaged();
    }

    @VynFunc
    public boolean isDamageable() {
        return workingStack.isDamageable();
    }

    @VynFunc
    public boolean isEnchantable() {
        return workingStack.isEnchantable();
    }

    @VynFunc
    public boolean isStackable() {
        return workingStack.isStackable();
    }

    @VynFunc
    public boolean isUsedOnRelease() {
        return workingStack.isUsedOnRelease();
    }

    // ---------- Transform API ----------

    @VynFunc
    public void translate(double x, double y, double z) {
        modificationCheck();
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @VynFunc
    public void setPivot(double x, double y, double z) {
        modificationCheck();
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
        modificationCheck();
        // Post-multiplication: this = this * rot
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
        modificationCheck();
        // Create the new rotation in world space
        final Quaternionf globalRot = new Quaternionf()
                .rotateX((float) Math.toRadians(x))
                .rotateY((float) Math.toRadians(y))
                .rotateZ((float) Math.toRadians(z));

        // Pre-multiplication: this = rot * this
        this.rotation.premul(globalRot);
    }

    /**
     * Legacy rotation method for backwards compatibility.
     * Equivalent to rotateGlobal.
     */
    @VynFunc
    public void rotate(double x, double y, double z) {
        rotateGlobal(x, y, z);
    }

    @VynFunc
    public void scale(double x, double y, double z) {
        modificationCheck();
        this.sx *= x;
        this.sy *= y;
        this.sz *= z;
    }

    public ItemStack getFinalItemStack() {
        return workingStack;
    }

    public void modificationCheck() {
        if (!modified) {
            workingStack = originalStack.copy();
            modified = true;
        }
    }

    /**
     * Returns whether this item was modified by scripts
     */
    public boolean wasModified() {
        return modified;
    }

    public void apply(MatrixStack matrices) {
        matrices.translate(x, y, z);
        matrices.multiply(rotation, (float) px, (float) py, (float) pz);
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    @Override
    public String toString() {
        return workingStack.toString();
    }
}