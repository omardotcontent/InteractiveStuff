package omar.projects.interactivestuff.scripts.Utilities;

import omar.projects.interactivestuff.scripts.variables.ItemModel;

import java.util.ArrayList;
import java.util.List;

public final class ItemModelRenderRegistry {
    public static final List<ItemModel> ACTIVE = new ArrayList<>();

    public static void clear() {
        ACTIVE.clear();
    }
}
