package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;

@VynType(name = "ItemModel")
public final class ItemModel {

    private static int NEXT_SEED = 1;
    private final int seed = NEXT_SEED++;

    private final ItemStack originalStack;
    private ItemStack workingStack;
    public boolean modified = false;

    // Transform State
    private float rotX, rotY, rotZ;
    private double x, y, z;
    private double sx = 1, sy = 1, sz = 1;
    private double px = 0.5, py = 0.5, pz = 0.5;

    // Color State (White / 100% Opacity)
    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private float opacity = 1.0f;

    /**
     * Constructor for scripts creating NEW models: `var m = new Item()`
     */
    @VynConstructor
    public ItemModel(final String itemId) {
        this(createStackFromId(itemId));
        ItemModelRenderRegistry.ACTIVE.add(this);
    }

    private static ItemStack createStackFromId(final String itemId) {
        try {
            return new ItemStack(Registries.ITEM.get(Identifier.of(itemId)));
        } catch (Exception e) {
            return new ItemStack(Items.AIR);
        }
    }

    /**
     * Internal constructor for wrapping existing items (like the hand item)
     */
    public ItemModel(final ItemStack stack) {
        this.originalStack = stack;
        this.workingStack = stack;
    }

    // ---------- Item Utility Getters (Restored) ----------

    @VynFunc
    public String getName() {
        // Returns the registry ID (e.g., "minecraft:diamond_sword")
        return Registries.ITEM.getId(workingStack.getItem()).toString();
    }

    @VynFunc
    public boolean isDamaged() { return workingStack.isDamaged(); }

    @VynFunc
    public boolean isDamageable() { return workingStack.isDamageable(); }

    @VynFunc
    public boolean isEnchantable() { return workingStack.isEnchantable(); }

    @VynFunc
    public boolean isStackable() { return workingStack.isStackable(); }

    @VynFunc
    public int getCount() { return workingStack.getCount(); }

    @VynFunc
    public int getMaxCount() { return workingStack.getMaxCount(); }

    @VynFunc
    public int getBobbingAnimationTime() { return workingStack.getBobbingAnimationTime(); }

    // ---------- Item Logic ----------

    @VynFunc
    public void setItemModel(final String model) {
        modificationCheck();
        ScriptItemHandler.apply(workingStack, "minecraft:item_model", model);
    }

    @VynFunc
    public void setBobbingTime(final int bobbingTime) {
        workingStack.setBobbingAnimationTime(bobbingTime);
    }

    @VynFunc
    public void setDataComponent(final String key, final Object value) {
        modificationCheck();
        ScriptItemHandler.apply(workingStack, key, value.toString());
    }

    private void modificationCheck() {
        if (!modified) {
            workingStack = originalStack.copy();
            modified = true;
        }
    }

    // ---------- Color API --------------

    @VynFunc
    public void setColor(int r, int g, int b) {
        this.red = MathHelper.clamp(r, 0, 255) / 255.0f;
        this.green = MathHelper.clamp(g, 0, 255) / 255.0f;
        this.blue = MathHelper.clamp(b, 0, 255) / 255.0f;
    }

    @VynFunc
    public void setOpacity(double alpha) {
        this.opacity = (float) MathHelper.clamp(alpha, 0.0, 1.0);
    }

    // ---------- Transform API ----------

    @VynFunc
    public void translate(double x, double y, double z) {
        this.x += x; this.y += y; this.z += z;
    }

    @VynFunc
    public void rotate(double x, double y, double z) {
        this.rotX += (float) x;
        this.rotY += (float) y;
        this.rotZ += (float) z;
    }

    @VynFunc
    public void scale(double x, double y, double z) {
        this.sx *= x; this.sy *= y; this.sz *= z;
    }

    @VynFunc
    public void setPivot(double x, double y, double z) {
        this.px = x; this.py = y; this.pz = z;
    }

    // ---------- Internal Helpers ----------

    public int getUniqueSeed() { return seed; }

    public ItemStack getFinalStack() { return workingStack; }

    public void apply(MatrixStack matrices) {
        matrices.translate(x, y, z);
        matrices.translate(px, py, pz);
        if (rotX != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        if (rotY != 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        if (rotZ != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        matrices.translate(-px, -py, -pz);
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    public int getRenderColor() {
        // Clamp all values to valid range
        int a = (int) (MathHelper.clamp(opacity, 0.0f, 1.0f) * 255.0f);
        int r = (int) (MathHelper.clamp(red, 0.0f, 1.0f) * 255.0f);
        int g = (int) (MathHelper.clamp(green, 0.0f, 1.0f) * 255.0f);
        int b = (int) (MathHelper.clamp(blue, 0.0f, 1.0f) * 255.0f);

        // Pack into ARGB format (0xAARRGGBB)
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}