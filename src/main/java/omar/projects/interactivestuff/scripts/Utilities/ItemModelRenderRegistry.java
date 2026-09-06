package omar.projects.interactivestuff.scripts.Utilities;

import omar.projects.interactivestuff.scripts.variables.ItemModel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ItemModelRenderRegistry {

    public static final List<ItemModel> ACTIVE = new CopyOnWriteArrayList<>();

    private ItemModelRenderRegistry() {}

    public static void clear() {
        ACTIVE.clear();
    }
}
