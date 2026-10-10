package omar.projects.interactivestuff.scripts.Utilities;

import net.minecraft.world.item.ItemDisplayContext;
import omar.projects.interactivestuff.scripts.variables.ItemModel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Extra models belong to one item render; nested renders cannot consume another hand's models. */
public final class ItemModelRenderRegistry {
    private static final ThreadLocal<Deque<Scope>> SCOPES = ThreadLocal.withInitial(ArrayDeque::new);

    private ItemModelRenderRegistry() {}

    public static Scope begin(ItemDisplayContext context, boolean mainHand) {
        Scope scope = new Scope(context, mainHand);
        SCOPES.get().push(scope);
        return scope;
    }

    public static void enqueue(ItemModel model) {
        Scope scope = SCOPES.get().peek();
        if (scope == null) return;
        model.setDisplayContext(scope.context, scope.mainHand);
        model.setUniqueSeed(scope.models.size() + 1);
        scope.models.add(model);
    }

    public static void clear() { SCOPES.remove(); }

    public static final class Scope implements AutoCloseable {
        private final ItemDisplayContext context;
        private final boolean mainHand;
        private final List<ItemModel> models = new ArrayList<>();
        private boolean closed;

        private Scope(ItemDisplayContext context, boolean mainHand) {
            this.context = context;
            this.mainHand = mainHand;
        }

        public List<ItemModel> models() { return models; }

        @Override public void close() {
            if (closed) return;
            closed = true;
            Deque<Scope> scopes = SCOPES.get();
            if (scopes.peek() != this) throw new IllegalStateException("Render scopes must close in order");
            scopes.pop();
            models.clear();
            if (scopes.isEmpty()) SCOPES.remove();
        }
    }
}
