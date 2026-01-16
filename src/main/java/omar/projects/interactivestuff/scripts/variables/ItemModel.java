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
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
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
    private int tintColor = 0xFFFFFFFF; // white = no tint
    private int light = -1;
    private int glint = -1;



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

    @VynFunc
    public int getLight() {
        return light;
    }

    @VynFunc
    public int getGlint() {
        return glint;
    }

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
    public void setColor(final int r, final int g, final int b) {
        modificationCheck(); // Add this
        this.red = MathHelper.clamp(r, 0, 255) / 255.0f;
        this.green = MathHelper.clamp(g, 0, 255) / 255.0f;
        this.blue = MathHelper.clamp(b, 0, 255) / 255.0f;
    }

    @VynFunc
    public void setLight(int light) {
        modificationCheck();

        if (light < -1) light = -1;
        if (light > 15) light = 15;

        if (light == -1) {
            this.light = -1;
        } else {
            int blockPart = light << 4;
            int skyPart = light << 16;

            this.light = skyPart | blockPart;
        }
    }

    @VynFunc
    public void setGlint(int glint) {
        modificationCheck();

        if (glint < -1) glint = -1;
        if (glint > 2) glint = 2;

        this.glint = glint;
    }

    @VynFunc
    public void setTint(int color) {
        modificationCheck(); // Add this
        if ((color & 0xFF000000) == 0) {
            color |= 0xFF000000;
        }
        this.tintColor = color;
    }


    @VynFunc
    public void setOpacity(final double alpha) {
        this.opacity = (float) MathHelper.clamp(alpha, 0.0, 1.0);
    }

    @VynFunc
    public void translate(double dx, double dy, double dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    @VynFunc
    public void rotate(double dx, double dy, double dz) {
        this.rotX += (float) dx;
        this.rotY += (float) dy;
        this.rotZ += (float) dz;
    }

    @VynFunc
    public void scale(double sx, double sy, double sz) {
        this.sx *= sx;
        this.sy *= sy;
        this.sz *= sz;
    }

    @VynFunc
    public double smooth(double current, double target, double speed) {
        float dt = RenderTickHandler.normalizedDelta;
        return current + (target - current) * (1.0 - Math.pow(1.0 - speed, dt));
    }

    @VynFunc
    public void setPivot(final double x, final double y, final double z) {
        this.px = x; this.py = y; this.pz = z;
    }

    // ---------- Internal Helpers ----------

    public int getUniqueSeed() { return seed; }

    public ItemStack getFinalStack() { return workingStack; }

    public void apply(final MatrixStack matrices) {
        matrices.translate(x, y, z);
        matrices.translate(px, py, pz);
        if (rotX != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        if (rotY != 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        if (rotZ != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        matrices.translate(-px, -py, -pz);
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    private int getRenderColor() {
        final int a = (int) (MathHelper.clamp(opacity, 0.0f, 1.0f) * 255.0f);
        final int r = (int) (MathHelper.clamp(red, 0.0f, 1.0f) * 255.0f);
        final int g = (int) (MathHelper.clamp(green, 0.0f, 1.0f) * 255.0f);
        final int b = (int) (MathHelper.clamp(blue, 0.0f, 1.0f) * 255.0f);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int getTintColor() {
        return tintColor;
    }

    public int getFinalColor() {
        return multiplyColor(getRenderColor(), getTintColor());
    }


    private static int multiplyColor(final int c1, final int c2) {
        final int a1 = (c1 >>> 24) & 0xFF;
        final int r1 = (c1 >>> 16) & 0xFF;
        final int g1 = (c1 >>> 8)  & 0xFF;
        final int b1 =  c1         & 0xFF;

        final int a2 = (c2 >>> 24) & 0xFF;
        final int r2 = (c2 >>> 16) & 0xFF;
        final int g2 = (c2 >>> 8)  & 0xFF;
        final int b2 =  c2         & 0xFF;

        final int a = (a1 * a2) / 255;
        final int r = (r1 * r2) / 255;
        final int g = (g1 * g2) / 255;
        final int b = (b1 * b2) / 255;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

}