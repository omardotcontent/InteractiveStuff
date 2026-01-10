package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;

@VynType(name = "Item")
public final class Item {

    private final ItemStack originalStack;
    private ItemStack workingStack;
    private boolean modified = false;

    private double x, y, z;
    private double rx, ry, rz;
    private double sx = 1, sy = 1, sz = 1;

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

    @VynFunc
    public void translate(double x, double y, double z) {
        modificationCheck();
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @VynFunc
    public void rotate(double x, double y, double z) {
        modificationCheck();
        this.rx += x;
        this.ry += y;
        this.rz += z;
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
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) rx));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) ry));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rz));
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    @Override
    public String toString() {
        return workingStack.toString();
    }
}