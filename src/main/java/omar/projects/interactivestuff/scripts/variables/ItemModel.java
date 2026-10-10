package omar.projects.interactivestuff.scripts.variables;

import com.mojang.blaze3d.vertex.PoseStack;
import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.objects.PivotRendererBlock;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import omar.projects.interactivestuff.render.RenderTransforms;
import omar.projects.interactivestuff.render.ColorMath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@VynType(name = "ItemModel")
public class ItemModel {

    private int glint = -1;
    private int light = -1;
    private int quadEnd = 0;
    private int quadStart = 0;
    private int seed;
    private int selectionColor = 0xFFFFFFFF;

    private final ItemStack originalStack;
    private ItemStack workingStack;

    public boolean modified = false;
    private boolean exclusive = false;
    private boolean useSelectionColor = false;

    private ItemModel parent = null;

    private ItemDisplayContext displayContext = ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

    private Map<Integer, Integer> indexTints;

    private double x, y, z;
    private double sx = 1, sy = 1, sz = 1;
    private double px = 0.5, py = 0.5, pz = 0.5;

    private float shXY = 0, shXZ = 0;
    private float shYX = 0, shYZ = 0;
    private float shZX = 0, shZY = 0;

    private Quaternionf rotation;
    private Matrix4f shearMatrix;
    private boolean mainHand = true;

    private float red = 1.0f;
    private float alpha = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;

    @VynConstructor
    public ItemModel(final String itemId) {
        this(createStackFromId(itemId));
        ItemModelRenderRegistry.enqueue(this);
    }

    private static ItemStack createStackFromId(final String itemId) {
        try {
            return new ItemStack(BuiltInRegistries.ITEM.get(Identifier.parse(itemId)).get());
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
        if (workingStack == null) return Items.AIR.toString();
        return BuiltInRegistries.ITEM.getKey(workingStack.getItem()).toString();
    }

    @VynFunc
    public boolean isDamaged() {
        return workingStack.isDamaged();
    }

    @VynFunc
    public boolean isDamageable() {
        return workingStack.isDamageableItem();
    }

    @VynFunc
    public boolean isEnchantable() {
        return workingStack.isEnchantable();
    }

    @VynFunc
    public boolean isExclusive() {
        return exclusive;
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
        return workingStack.getMaxStackSize();
    }

    @VynFunc
    public int getBobbingAnimationTime() {
        return workingStack.getPopTime();
    }

    @VynFunc
    public int getLight() {
        if (light != -1) {
            return light;
        }
        if (parent != null) {
            return parent.getLight();
        }
        return -1;
    }

    @VynFunc
    public int getGlint() {
        if (glint != -1) {
            return glint;
        }
        if (parent != null) {
            return parent.getGlint();
        }
        return -1;
    }

    @VynFunc
    public double getRotationX() {
        return rotation == null ? 0 : rotation.x;
    }

    @VynFunc
    public double getRotationY() {
        return rotation == null ? 0 : rotation.y;
    }

    @VynFunc
    public double getRotationZ() {
        return rotation == null ? 0 : rotation.z;
    }

    public void setDisplayContext(final ItemDisplayContext context) {
        setDisplayContext(context, !context.leftHand());
    }

    public void setDisplayContext(final ItemDisplayContext context, boolean mainHand) {
        this.displayContext = context;
        this.mainHand = mainHand;
    }

    public ItemDisplayContext getDisplayContext() {
        return displayContext;
    }

    @VynFunc
    public boolean isMainHand() {
        return isHandContext() && mainHand;
    }

    @VynFunc
    public boolean isOffHand() {
        return isHandContext() && !mainHand;
    }

    /** Physical arm used by rendering, independent of the player's dominant hand. */
    @VynFunc
    public boolean isLeftHand() {
        return isHandContext() && displayContext.leftHand();
    }

    @VynFunc
    public boolean isRightHand() {
        return isHandContext() && !displayContext.leftHand();
    }

    private boolean isHandContext() {
        return displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
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
        ensureWorkingCopy();
        workingStack.set(DataComponents.ITEM_MODEL, Identifier.parse(model));
    }

    @VynFunc
    public void setBobbingTime(final int bobbingTime) {
        ensureWorkingCopy();
        workingStack.setPopTime(bobbingTime);
    }

    @VynFunc
    public void setDataComponent(final String key, final Object value) {
        ensureWorkingCopy();
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
        ensureWorkingCopy();
        ScriptItemHandler.remove(workingStack, key);
    }

    @VynFunc
    public void setQuads(int start, int end) {
        markModified();
        this.quadStart = Math.max(0, start);
        this.quadEnd = Math.max(0, end);
    }

    @VynFunc
    public void setColor(final int r, final int g, final int b) {
        markModified();
        this.red = Mth.clamp(r, 0, 255) / 255.0f;
        this.green = Mth.clamp(g, 0, 255) / 255.0f;
        this.blue = Mth.clamp(b, 0, 255) / 255.0f;
    }

    @VynFunc
    public void setOpacity(final double opacity) {
        markModified();
        this.alpha = (float) Mth.clamp(opacity, 0.0, 1.0);
    }

    @VynFunc
    public void setLight(final int light) {
        markModified();

        final int clampedLight = Mth.clamp(light, -1, 15);

        if (clampedLight == -1) {
            this.light = -1;
            return;
        }

        final int blockPart = clampedLight << 4;
        final int skyPart = clampedLight << 20;
        this.light = skyPart | blockPart;
    }

    @VynFunc
    public void setGlint(final int glint) {
        markModified();
        this.glint = Mth.clamp(glint, -1, 2);
    }

    @VynFunc
    public void setTint(final int index, final int color) {
        markModified();
        if (this.indexTints == null) this.indexTints = new HashMap<>();
        this.indexTints.put(index, (color & 0xFF000000) == 0 ? color | 0xFF000000 : color);
    }

    @VynFunc
    public void setTint(final int color) {
        setTint(-1, color);
    }

    @VynFunc
    public void setQuadColor(final int color) {
        markModified();
        this.selectionColor = (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
        this.useSelectionColor = true;
    }

    @VynFunc
    public void translate(final double dx, final double dy, final double dz) {
        markModified();
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    @VynFunc
    public void translateX(final double dx) {
        markModified();
        this.x += dx;
    }

    @VynFunc
    public void translateY(final double dy) {
        markModified();
        this.y += dy;
    }

    @VynFunc
    public void translateZ(final double dz) {
        markModified();
        this.z += dz;
    }

    @VynFunc
    public void rotateAxis(final double angle, final double axisX, final double axisY, final double axisZ) {
        markModified();
        double length = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        if (length == 0 || !Double.isFinite(length)) return;
        rotation().rotateAxis((float) Math.toRadians(angle), (float) (axisX / length),
                (float) (axisY / length), (float) (axisZ / length));
    }

    @VynFunc
    public void rotate(final double dx, final double dy, final double dz) {
        markModified();
        rotation().rotateXYZ(
                (float) Math.toRadians(dx),
                (float) Math.toRadians(dy),
                (float) Math.toRadians(dz));
    }

    @VynFunc
    public void rotateX(final double angle) {
        markModified();
        rotation().rotateX((float) Math.toRadians(angle));
    }

    @VynFunc
    public void rotateY(final double angle) {
        markModified();
        rotation().rotateY((float) Math.toRadians(angle));
    }

    @VynFunc
    public void rotateZ(final double angle) {
        markModified();
        rotation().rotateZ((float) Math.toRadians(angle));
    }

    @VynFunc
    public void scale(final double sx, final double sy, final double sz) {
        markModified();
        this.sx *= sx;
        this.sy *= sy;
        this.sz *= sz;
    }

    @VynFunc
    public void scaleX(final double sx) {
        markModified();
        this.sx *= sx;
    }

    @VynFunc
    public void scaleY(final double sy) {
        markModified();
        this.sy *= sy;
    }

    @VynFunc
    public void scaleZ(final double sz) {
        markModified();
        this.sz *= sz;
    }

    @VynFunc
    public void shear(final double xy, final double xz, final double yx, final double yz, final double zx,
            final double zy) {
        markModified();
        this.shXY = (float) xy;
        this.shXZ = (float) xz;
        this.shYX = (float) yx;
        this.shYZ = (float) yz;
        this.shZX = (float) zx;
        this.shZY = (float) zy;
        this.shearMatrix = null;
    }

    @VynFunc
    public double smooth(final double current, final double target, final double speed) {
        return current + (target - current) * (1.0 - Math.pow(1.0 - speed, RenderTickHandler.normalizedDelta));
    }

    @VynFunc
    public void setPivot(final double x, final double y, final double z) {
        markModified();
        this.px = x;
        this.py = y;
        this.pz = z;
        if (ConfigHandler.INSTANCE.resourcePackDebugMode)
            new PivotRendererBlock(this);
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

    @VynFunc
    public void setParent(final ItemModel parent) {
        if (parent == this) {
            System.err.println("InteractiveStuff Error: Cannot set item as its own parent.");
            return;
        }

        ItemModel currentChecker = parent;
        while (currentChecker != null) {
            if (currentChecker == this) {
                System.err.println("InteractiveStuff Error: Circular dependency detected in parent hierarchy.");
                return;
            }
            currentChecker = currentChecker.getParent();
        }

        markModified();
        this.parent = parent;
    }

    @VynFunc
    public void select(final int quadStart, final int quadEnd) {
        this.setQuads(quadStart, quadEnd);
        this.exclusive = true;
    }

    @VynFunc
    public ItemModel copy() {
        final ItemModel copy = new ItemModel(this.workingStack);
        copy.displayContext = displayContext;
        copy.mainHand = mainHand;
        copy.modified = modified;
        copy.glint = glint;
        copy.light = light;
        copy.quadStart = quadStart;
        copy.quadEnd = quadEnd;
        copy.exclusive = exclusive;
        copy.selectionColor = selectionColor;
        copy.useSelectionColor = useSelectionColor;
        copy.parent = parent;
        copy.x = x; copy.y = y; copy.z = z;
        copy.sx = sx; copy.sy = sy; copy.sz = sz;
        copy.px = px; copy.py = py; copy.pz = pz;
        copy.shXY = shXY; copy.shXZ = shXZ;
        copy.shYX = shYX; copy.shYZ = shYZ;
        copy.shZX = shZX; copy.shZY = shZY;
        copy.rotation = rotation == null ? null : new Quaternionf(rotation);
        copy.indexTints = indexTints == null ? null : new HashMap<>(indexTints);
        copy.red = red; copy.green = green; copy.blue = blue; copy.alpha = alpha;
        ItemModelRenderRegistry.enqueue(copy);
        return copy;
    }

    @VynFunc
    public void detach() {
        markModified();
        this.parent = null;
    }

    @VynFunc
    public boolean hasParent() {
        return this.parent != null;
    }

    public ItemModel getParent() {
        return parent;
    }

    private void markModified() { modified = true; }

    private void ensureWorkingCopy() {
        markModified();
        if (workingStack == originalStack) workingStack = originalStack.copy();
    }

    private Quaternionf rotation() {
        if (rotation == null) rotation = new Quaternionf();
        return rotation;
    }

    public void setUniqueSeed(int seed) { this.seed = seed; }

    public int getUniqueSeed() {
        return seed;
    }

    public ItemStack getSourceStack() { return originalStack; }

    public ItemStack getFinalStack() {
        return workingStack;
    }

    public void apply(final PoseStack matrices) {
        if (!ConfigHandler.INSTANCE.resourcePackMatrixEditing) {
            return;
        }
        applyParentChain(matrices);
        applySelf(matrices);
    }

    public void applyParentChain(final PoseStack matrices) {
        if (parent != null) {
            parent.apply(matrices);
        }
    }

    public void applySelf(final PoseStack matrices) {
        matrices.translate(x, y, z);
        matrices.translate(px, py, pz);

        if (shXY != 0 || shXZ != 0 || shYX != 0 || shYZ != 0 || shZX != 0 || shZY != 0) {
            if (shearMatrix == null) {
                shearMatrix = new Matrix4f();

                shearMatrix.m01(shXY);
                shearMatrix.m02(shXZ);
                shearMatrix.m10(shYX);
                shearMatrix.m12(shYZ);
                shearMatrix.m20(shZX);
                shearMatrix.m21(shZY);
            }

            RenderTransforms.multiply(matrices, shearMatrix);
        }

        if (rotation != null) RenderTransforms.rotate(matrices, rotation);
        matrices.translate(-px, -py, -pz);
        matrices.scale((float) sx, (float) sy, (float) sz);
    }

    private int getRenderColor() {
        final int a = (int) (this.alpha * 255.0f);
        final int r = (int) (Mth.clamp(red, 0.0f, 1.0f) * 255.0f);
        final int g = (int) (Mth.clamp(green, 0.0f, 1.0f) * 255.0f);
        final int b = (int) (Mth.clamp(blue, 0.0f, 1.0f) * 255.0f);

        int color = (a << 24) | (r << 16) | (g << 8) | b;

        // Combine with parent color if parent exists
        if (parent != null) {
            color = ColorMath.multiply(color, parent.getRenderColor());
        }

        return color;
    }

    public boolean hasColorChanges() {
        return red != 1 || green != 1 || blue != 1 || alpha != 1 || useSelectionColor
                || (indexTints != null && !indexTints.isEmpty()) || (parent != null && parent.hasColorChanges());
    }

    public int getFinalColorForIndex(final int tintIndex) {
        return getFinalColorForIndex(tintIndex, 0xFFFFFFFF);
    }

    public int getFinalColorForIndex(final int tintIndex, int originalColor) {
        return ColorMath.multiply(getRenderColor(), resolveTint(tintIndex, originalColor));
    }

    private int resolveTint(int index, int fallback) {
        if (indexTints != null) {
            Integer tint = indexTints.get(index);
            if (tint != null) return tint;
            tint = indexTints.get(-1);
            if (tint != null) return tint;
        }
        return parent == null ? fallback : parent.resolveTint(index, fallback);
    }

    public boolean getUseSelectionColor() {
        if (useSelectionColor) {
            return true;
        }
        if (parent != null) {
            return parent.getUseSelectionColor();
        }
        return false;
    }

    public int getSelectionColor() {
        if (useSelectionColor) {
            return selectionColor;
        }
        if (parent != null) {
            return parent.getSelectionColor();
        }
        return 0xFFFFFFFF;
    }

    public boolean isPartialSelection() {
        boolean hasSelection = quadStart > 0 || quadEnd > 0;
        if (!hasSelection && parent != null) {
            return parent.isPartialSelection();
        }
        return hasSelection;
    }

    public int getQuadStart() {
        if (quadStart > 0 || quadEnd > 0) {
            return quadStart;
        }
        if (parent != null) {
            return parent.getQuadStart();
        }
        return 0;
    }

    public int getQuadEnd() {
        if (quadStart > 0 || quadEnd > 0) {
            return quadEnd;
        }
        if (parent != null) {
            return parent.getQuadEnd();
        }
        return 0;
    }

}