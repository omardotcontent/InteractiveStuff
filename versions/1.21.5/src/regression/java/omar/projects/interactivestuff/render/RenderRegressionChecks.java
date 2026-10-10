package omar.projects.interactivestuff.render;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class RenderRegressionChecks {
    public static void run(BiConsumer<Boolean, String> check) {
        ItemModel model = new ItemModel((ItemStack) null);
        check.accept(!model.hasColorChanges(), "default model preserves native colors");
        model.translate(1, 2, 3);
        check.accept(model.modified && model.getFinalStack() == null, "visual transforms do not copy or mutate a stack");
        model.setTint(0x112233);
        check.accept(model.getFinalColorForIndex(0, 0xFFABCDEF) == 0xFF112233, "global tint overrides native tint index zero");
        model.setTint(0, 0x445566);
        check.accept(model.getFinalColorForIndex(0, 0xFFABCDEF) == 0xFF445566, "indexed tint overrides global tint");
        model.setDisplayContext(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, true);
        check.accept(model.isMainHand() && !model.isOffHand(), "left-handed main hand is identified correctly");
        check.accept(model.isLeftHand() && !model.isRightHand(), "physical left side is independent of main-hand identity");
        model.setLight(15);
        check.accept(model.getLight() == 0x00F000F0, "packed light uses both correct sky and block offsets");
        ItemModel copy = model.copy();
        copy.setTint(0, 0x778899);
        check.accept(model.getFinalColorForIndex(0) == 0xFF445566 && copy.getFinalColorForIndex(0) == 0xFF778899,
                "visual copies own their tint maps");

        try (var outer = ItemModelRenderRegistry.begin(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, false)) {
            ItemModelRenderRegistry.enqueue(model);
            check.accept(model.isOffHand() && model.getUniqueSeed() == 1, "extra model inherits actual hand and stable slot seed");
            try (var inner = ItemModelRenderRegistry.begin(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, true)) {
                ItemModelRenderRegistry.enqueue(copy);
                check.accept(inner.models().size() == 1 && outer.models().size() == 1, "nested render scopes retain separate extra models");
            }
            check.accept(outer.models().size() == 1, "closing nested scope preserves outer models");
        }
        try (var fresh = ItemModelRenderRegistry.begin(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, true)) {
            check.accept(fresh.models().isEmpty(), "render scopes do not leak models into the next hand");
        }

        ColoredQuadCache cache = new ColoredQuadCache();
        BakedQuad untinted = quad(-1), tinted = quad(2);
        List<BakedQuad> source = new ArrayList<>(List.of(untinted, tinted));
        var geometry = cache.get(source);
        check.accept(geometry.slots() == 4 && geometry.quads().getFirst().tintIndex() == 0
                && geometry.quads().getLast().tintIndex() == 3, "quad tint indexes map to valid color slots");
        check.accept(untinted.tintIndex() == -1 && tinted.tintIndex() == 2, "recoloring leaves native quads unchanged");
        source.clear();
        check.accept(cache.get(List.of(untinted, tinted)) == geometry, "cache keys survive source layer reuse");
        for (int i = 0; i < 130; i++) cache.get(List.of(quad(-1)));
        check.accept(cache.get(List.of(untinted, tinted)) != geometry, "geometry cache evicts old models at its bound");
        var latest = cache.get(List.of(untinted));
        cache.clear();
        check.accept(cache.get(List.of(untinted)) != latest, "resource reload clears colored geometry");
    }

    private static BakedQuad quad(int tint) {
        return new BakedQuad(new int[32], tint, null, null, true, 0);
    }
}
