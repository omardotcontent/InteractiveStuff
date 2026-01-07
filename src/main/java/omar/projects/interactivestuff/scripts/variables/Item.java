package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import omar.projects.interactivestuff.scripts.Utilities.ScriptComponentApplier;

@VynType(name = "Item")
public final class Item {

    private ItemStack item;

    public Item(final ItemStack item) {
        this.item = item.copy();
    }

    @VynFunc
    public String getName() {
        return item.getItem().toString();
    }

    @VynFunc
    public void setItem(String item) {
        this.item = new ItemStack(Registries.ITEM.get(Identifier.of(item)));
    }

    @VynFunc
    public void setDataComponent(final String key, final Object value) {
        item = ScriptComponentApplier.apply(item, key, value.toString());
    }

    @VynFunc
    public boolean isDamaged() {
        return item.isDamaged();
    }

    @VynFunc
    public boolean isDamageable() {
        return item.isDamageable();
    }

    @VynFunc
    public boolean isEnchantable() {
        return item.isEnchantable();
    }

    @VynFunc
    public boolean isStackable() {
        return item.isStackable();
    }

    @VynFunc
    public boolean isUsedOnRelease() {
        return item.isUsedOnRelease();
    }

    public ItemStack getFinalItemStack() {
        return item;
    }

    @VynFunc
    public String toString() {
        return item.toString();
    }

}
