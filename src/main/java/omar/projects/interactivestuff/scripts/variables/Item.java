package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;

@VynType(name = "Item")
public final class Item {

    private final ItemStack originalStack;
    private ItemStack workingStack;
    private boolean modified = false;

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
        // Copy on first modification
        if (!modified) {
            workingStack = originalStack.copy();
            modified = true;
        }
        ScriptItemHandler.apply(workingStack, key, value.toString());
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

    public ItemStack getFinalItemStack() {
        return workingStack;
    }

    /**
     * Returns whether this item was modified by scripts
     */
    public boolean wasModified() {
        return modified;
    }

    @Override
    public String toString() {
        return workingStack.toString();
    }
}