package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

@VynType(name = "ItemModel")
public final class ItemModel {

    private static int NEXT_SEED = 1;
    private final int seed = NEXT_SEED++;

    private final ItemStack originalStack;
    private ItemStack workingStack;
    public boolean modified = false;

    private ItemDisplayContext displayContext = ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

    private double x, y, z;
    private double sx = 1, sy = 1, sz = 1;
    private double px = 0.5, py = 0.5, pz = 0.5;
    private Quaternionf rotation = new Quaternionf();

    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private int tintColor = 0xFFFFFFFF;
    private int light = -1;
    private int glint = -1;

    @VynConstructor
    public ItemModel(final String itemId) {
        this(createStackFromId(itemId));
        ItemModelRenderRegistry.ACTIVE.add(this);
    }

    private static ItemStack createStackFromId(final String itemId) {
        try {
            return new ItemStack(Registries.ITEM.get(Identifier.of(itemId)));
        } catch (final Exception e) {
            return new ItemStack(Items.AIR);
        }
    }

    public ItemModel(final ItemStack stack) {
        this.originalStack = stack;
        this.workingStack = stack;
    }

    @VynFunc
    public String getName() {
        return Registries.ITEM.getId(workingStack.getItem()).toString();
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
    public int getCount() {
        return workingStack.getCount();
    }

    @VynFunc
    public int getMaxCount() {
        return workingStack.getMaxCount();
    }

    @VynFunc
    public int getBobbingAnimationTime() {
        return workingStack.getBobbingAnimationTime();
    }

    @VynFunc
    public int getLight() {
        return light;
    }

    @VynFunc
    public int getGlint() {
        return glint;
    }


    public void setDisplayContext(final ItemDisplayContext context) {
        this.displayContext = context;
    }

    public ItemDisplayContext getDisplayContext() {
        return displayContext;
    }

    @VynFunc
    public boolean isMainHand() {
        return displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    @VynFunc
    public boolean isOffHand() {
        return displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    @VynFunc
    public boolean isFirstPerson() {
        return displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
    }

    @VynFunc
    public String getDisplayContextName() {
        return displayContext.name();
    }


    @VynFunc
    public void setItemModel(final String model) {
        modificationCheck();
        ScriptItemHandler.apply(workingStack, "minecraft:item_model", model);
    }

    @VynFunc
    public void setBobbingTime(final int bobbingTime) {
        modificationCheck();
        workingStack.setBobbingAnimationTime(bobbingTime);
    }

    @VynFunc
    public void setDataComponent(final String key, final Object value) {
        modificationCheck();
        ScriptItemHandler.apply(workingStack, key, value.toString());
    }

    @VynFunc
    public String getDataComponent(final String key) {
        return ScriptItemHandler.getComponent(workingStack, key);
    }

    @VynFunc
    public boolean hasDataComponent(final String key) {
        return ScriptItemHandler.hasComponent(workingStack, key);
    }

    @VynFunc
    public List<String> getDataComponentIds() {
        return ScriptItemHandler.getComponentIds(workingStack);
    }

    @VynFunc
    public List<Map<String, String>> getDataComponents() {
        return ScriptItemHandler.getComponents(workingStack);
    }

    @VynFunc
    public void removeDataComponent(final String key) {
        modificationCheck();
        ScriptItemHandler.remove(workingStack, key);
    }


    @VynFunc
    public void setColor(final int r, final int g, final int b) {
        modificationCheck();
        this.red = MathHelper.clamp(r, 0, 255) / 255.0f;
        this.green = MathHelper.clamp(g, 0, 255) / 255.0f;
        this.blue = MathHelper.clamp(b, 0, 255) / 255.0f;
    }

    @VynFunc
    public void setLight(final int light) {
        modificationCheck();

        final int clampedLight = MathHelper.clamp(light, -1, 15);

        if (clampedLight == -1) {
            this.light = -1;
            return;
        }

        final int blockPart = clampedLight << 4;
        final int skyPart = clampedLight << 16;
        this.light = skyPart | blockPart;
    }

    @VynFunc
    public void setGlint(final int glint) {
        modificationCheck();
        this.glint = MathHelper.clamp(glint, -1, 2);
    }

    @VynFunc
    public void setTint(final int color) {
        modificationCheck();
        this.tintColor = (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    @VynFunc
    public void translate(final double dx, final double dy, final double dz) {
        modificationCheck();
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    @VynFunc
    public void translateX(final double dx) {
        modificationCheck();
        this.x += dx;
    }

    @VynFunc
    public void translateY(final double dy) {
        modificationCheck();
        this.y += dy;
    }

    @VynFunc
    public void translateZ(final double dz) {
        modificationCheck();
        this.z += dz;
    }

    @VynFunc
    public void rotateAxis(final double angle, final double axisX, final double axisY, final double axisZ) {
        modificationCheck();
        Vector3f axis = new Vector3f((float) axisX, (float) axisY, (float) axisZ).normalize();
        Quaternionf delta = new Quaternionf().rotateAxis(
                (float) Math.toRadians(angle),
                axis
        );
        rotation.mul(delta);
    }

    @VynFunc
    public void rotate(final double dx, final double dy, final double dz) {
        modificationCheck();
        Quaternionf delta = new Quaternionf().rotateXYZ(
                (float) Math.toRadians(dx),
                (float) Math.toRadians(dy),
                (float) Math.toRadians(dz)
        );
        rotation.mul(delta);
    }


    @VynFunc
    public void rotateX(final double angle) {
        modificationCheck();
        rotation.rotateX((float) Math.toRadians(angle));
    }

    @VynFunc
    public void rotateY(final double angle) {
        modificationCheck();
        rotation.rotateY((float) Math.toRadians(angle));
    }

    @VynFunc
    public void rotateZ(final double angle) {
        modificationCheck();
        rotation.rotateZ((float) Math.toRadians(angle));
    }

    @VynFunc
    public void scale(final double sx, final double sy, final double sz) {
        modificationCheck();
        this.sx *= sx;
        this.sy *= sy;
        this.sz *= sz;
    }

    @VynFunc
    public void scaleX(final double sx) {
        modificationCheck();
        this.sx *= sx;
    }

    @VynFunc
    public void scaleY(final double sy) {
        modificationCheck();
        this.sy *= sy;
    }

    @VynFunc
    public void scaleZ(final double sz) {
        modificationCheck();
        this.sz *= sz;
    }

    @VynFunc
    public double smooth(final double current, final double target, final double speed) {
        modificationCheck();
        final float dt = RenderTickHandler.normalizedDelta;
        return current + (target - current) * (1.0 - Math.pow(1.0 - speed, dt));
    }

    @VynFunc
    public void setPivot(final double x, final double y, final double z) {
        modificationCheck();
        this.px = x;
        this.py = y;
        this.pz = z;
    }

    public double getPivotX() {
        return px;
    }

    public double getPivotY() {
        return py;
    }

    public double getPivotZ() {
        return pz;
    }

    public double getTranslateX() {
        return x;
    }

    public double getTranslateY() {
        return y;
    }

    public double getTranslateZ() {
        return z;
    }

    private void modificationCheck() {
        if (modified) {
            return;
        }
        workingStack = originalStack.copy();
        modified = true;
    }

    public int getUniqueSeed() {
        return seed;
    }

    public ItemStack getFinalStack() {
        return workingStack;
    }

    public void apply(final MatrixStack matrices) {
        if (!ConfigHandler.INSTANCE.resourcePackMatrixEditing) {
            return;
        }
        matrices.translate(x, y, z);
        matrices.translate(px, py, pz);
        matrices.multiply(rotation);
        matrices.translate(-px, -py, -pz);
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    private int getRenderColor() {
        final int r = (int) (MathHelper.clamp(red, 0.0f, 1.0f) * 255.0f);
        final int g = (int) (MathHelper.clamp(green, 0.0f, 1.0f) * 255.0f);
        final int b = (int) (MathHelper.clamp(blue, 0.0f, 1.0f) * 255.0f);

        return (255 << 24) | (r << 16) | (g << 8) | b;
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