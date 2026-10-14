package omar.projects.interactivestuff.scripts.Utilities;

import net.minecraft.world.item.ItemDisplayContext;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import omar.projects.interactivestuff.scripts.variables.ParticleEmitter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.function.Supplier;
import org.joml.Matrix4f;

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

    public static void bind(ParticleEmitter emitter, ItemModel item) {
        Scope scope = SCOPES.get().peek();
        if (scope == null) throw new IllegalStateException("Attach particle emitters inside onItemUpdate");
        scope.particles.add(new ParticleBinding(emitter, item));
    }

    /** Keep only the final pivot for each model in this item-render scope. */
    public static void pivot(ItemModel model) {
        Scope scope = SCOPES.get().peek();
        if (scope != null) scope.pivots.add(model);
    }

    public record ParticleBinding(ParticleEmitter emitter, ItemModel item) {}

    public static final class Scope implements AutoCloseable {
        private final ItemDisplayContext context;
        private final boolean mainHand;
        private final List<ItemModel> models = new ArrayList<>();
        private final List<ParticleBinding> particles = new ArrayList<>();
        private final Set<ItemModel> pivots = Collections.newSetFromMap(new IdentityHashMap<>());
        private final Map<ItemModel, Matrix4f> anchors = new IdentityHashMap<>();
        private boolean closed;

        private Scope(ItemDisplayContext context, boolean mainHand) {
            this.context = context;
            this.mainHand = mainHand;
        }

        public List<ItemModel> models() { return models; }
        public List<ParticleBinding> particles() { return particles; }
        public Set<ItemModel> pivots() { return pivots; }
        public Matrix4f anchor(ItemModel model, Supplier<Matrix4f> resolve) {
            return anchors.computeIfAbsent(model, ignored -> resolve.get());
        }

        @Override public void close() {
            if (closed) return;
            closed = true;
            Deque<Scope> scopes = SCOPES.get();
            if (scopes.peek() != this) throw new IllegalStateException("Render scopes must close in order");
            scopes.pop();
            models.clear();
            particles.clear();
            pivots.clear();
            anchors.clear();
            if (scopes.isEmpty()) SCOPES.remove();
        }
    }
}
