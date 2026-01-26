package omar.projects.interactivestuff.scripts.Utilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import omar.projects.interactivestuff.scripts.variables.ItemModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemModelCache {

    private static final Map<CacheKey, CachedResult> CACHE = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 64;

    private ItemModelCache() {}

    public static void put(final Item item, final ItemDisplayContext context, final ItemModel model, final java.util.List<ItemModel> extras) {
        if (CACHE.size() >= MAX_CACHE_SIZE) {
            CACHE.clear();
        }
        CACHE.put(new CacheKey(item, context), new CachedResult(model, new java.util.ArrayList<>(extras)));
    }

    public static CachedResult get(final Item item, final ItemDisplayContext context) {
        return CACHE.get(new CacheKey(item, context));
    }


    public static void clear() {
        CACHE.clear();
    }

    private record CacheKey(Item item, ItemDisplayContext context) {}
    public record CachedResult(ItemModel mainModel, java.util.List<ItemModel> extras) {}
}
