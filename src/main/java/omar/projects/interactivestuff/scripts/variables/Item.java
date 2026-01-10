package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;

@VynType(name = "Item")
public final class Item {

    private final ItemStack stack;

    public Item(final ItemStack stack) {
        this.stack = stack;
    }

    @VynFunc
    public String getName() {
        return stack.getItem().toString();
    }

    @VynFunc
    public void setDataComponent(final String key, final Object value) {
        ScriptItemHandler.apply(stack, key, value.toString());
    }

    @VynFunc
    public boolean isDamaged() {
        return stack.isDamaged();
    }

    @VynFunc
    public boolean isDamageable() {
        return stack.isDamageable();
    }

    @VynFunc
    public boolean isEnchantable() {
        return stack.isEnchantable();
    }

    @VynFunc
    public boolean isStackable() {
        return stack.isStackable();
    }

    @VynFunc
    public boolean isUsedOnRelease() {
        return stack.isUsedOnRelease();
    }

    public ItemStack getFinalItemStack() {
        return stack; // always same object
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}

