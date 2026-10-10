package omar.projects.interactivestuff.render;

import net.minecraft.client.resources.model.geometry.BakedQuad;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Bounded immutable geometry cache; tint slot zero makes previously untinted quads colorable. */
final class ColoredQuadCache {
    private static final int MAX_MODELS = 128;
    private final Map<Key, Geometry> entries = new LinkedHashMap<>(32, 0.75f, true);

    Geometry get(List<BakedQuad> quads) {
        Key lookup = new Key(quads);
        Geometry cached = entries.get(lookup);
        if (cached != null) return cached;
        List<BakedQuad> recolored = new ArrayList<>(quads.size());
        int slots = 1;
        for (BakedQuad quad : quads) {
            BakedQuad.MaterialInfo material = quad.materialInfo();
            int slot = Math.max(0, material.tintIndex() + 1);
            slots = Math.max(slots, slot + 1);
            var tinted = new BakedQuad.MaterialInfo(material.sprite(), material.layer(), material.itemRenderType(),
                    slot, material.shade(), material.lightEmission());
            recolored.add(new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                    quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(), quad.direction(), tinted));
        }
        Geometry geometry = new Geometry(List.copyOf(recolored), slots);
        entries.put(new Key(List.copyOf(quads)), geometry);
        if (entries.size() > MAX_MODELS) entries.remove(entries.keySet().iterator().next());
        return geometry;
    }

    void clear() { entries.clear(); }

    record Geometry(List<BakedQuad> quads, int slots) {}

    private static final class Key {
        private final List<BakedQuad> quads;
        private final int hash;

        Key(List<BakedQuad> quads) {
            this.quads = quads;
            int value = 1;
            for (BakedQuad quad : quads) value = 31 * value + System.identityHashCode(quad);
            hash = value;
        }

        @Override public int hashCode() { return hash; }

        @Override public boolean equals(Object other) {
            if (!(other instanceof Key key) || key.quads.size() != quads.size()) return false;
            for (int i = 0; i < quads.size(); i++) if (quads.get(i) != key.quads.get(i)) return false;
            return true;
        }
    }
}
