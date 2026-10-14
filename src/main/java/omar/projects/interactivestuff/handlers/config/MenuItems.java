package omar.projects.interactivestuff.handlers.config;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Display-only stacks before connection binds Minecraft's item components; saved choices use real items. */
public final class MenuItems {
    private MenuItems() {}
    public static ItemStack stack(Item item) {
        if (Minecraft.getInstance().level != null) return new ItemStack(item);
        var components = DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS)
                .set(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(item))
                .set(DataComponents.ITEM_NAME, Component.translatable(item.getDescriptionId()))
                .set(DataComponents.MAX_STACK_SIZE, 64).build();
        return new ItemStack(Holder.direct(item, components), 1);
    }
}
