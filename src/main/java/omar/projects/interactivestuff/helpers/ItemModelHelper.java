package omar.projects.interactivestuff.helpers;

import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import studio.meraki.vynapi.handler.script.ScriptHandler;

public final class ItemModelHelper {

    public static ItemModel itemUpdate(final ItemStack itemStack, final ItemDisplayContext displayContext) {
        final ItemModel item = new ItemModel(itemStack);
        item.setDisplayContext(displayContext);

        ScriptHandler.fireEvent("onItemUpdate", item);

        if (!item.modified) {
            return null;
        }

        return item;
    }

}
