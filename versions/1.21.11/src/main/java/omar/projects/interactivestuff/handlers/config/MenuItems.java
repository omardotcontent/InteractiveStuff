package omar.projects.interactivestuff.handlers.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** These releases bind the default item data before the title menu. */
public final class MenuItems {
    private MenuItems() {}
    public static ItemStack stack(Item item) { return new ItemStack(item); }
}
