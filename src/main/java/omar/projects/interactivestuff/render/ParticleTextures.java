package omar.projects.interactivestuff.render;

import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import omar.projects.interactivestuff.IS;
import omar.projects.interactivestuff.particles.ParticleSystem.DrawParticle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/** Uses pack PNGs directly, so custom particles do not need a world-particle registration. */
public final class ParticleTextures {
    private static final LinkedHashMap<String, List<Identifier>> TYPES = new LinkedHashMap<>();
    public static final Identifier MARKER = Identifier.parse("interactivestuff:textures/debug/marker.png");
    private static final Identifier FALLBACK = Identifier.parse("minecraft:textures/particle/generic_0.png");
    private ParticleTextures() {}
    public static void clear() { TYPES.clear(); }

    public static Identifier texture(DrawParticle particle) {
        if (!particle.texture().isEmpty()) return Identifier.parse(particle.texture());
        List<Identifier> frames = TYPES.get(particle.type());
        if (frames == null) {
            frames = load(particle.type());
            if (TYPES.size() >= 256) TYPES.remove(TYPES.keySet().iterator().next());
            TYPES.put(particle.type(), frames);
        }
        int index = switch (particle.animation()) {
            case "random" -> particle.variant() % frames.size();
            case "loop" -> (int) (particle.age() / particle.frameTicks()) % frames.size();
            case "first" -> 0;
            default -> Math.min(frames.size() - 1, (int) (particle.progress() * frames.size()));
        };
        return frames.get(index);
    }

    private static List<Identifier> load(String name) {
        try {
            var id = Identifier.parse(name);
            var manager = Minecraft.getInstance().getResourceManager();
            var resource = manager.getResource(Identifier.fromNamespaceAndPath(id.getNamespace(), "particles/" + id.getPath() + ".json"));
            if (resource.isEmpty()) throw new IllegalArgumentException("Missing particles/" + id.getPath() + ".json");
            var frames = new ArrayList<Identifier>();
            try (var reader = resource.get().openAsReader()) {
                for (var entry : JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("textures")) {
                    if (frames.size() >= 256) throw new IllegalArgumentException("Maximum 256 animation textures per particle type");
                    var texture = Identifier.parse(entry.getAsString());
                    var path = Identifier.fromNamespaceAndPath(texture.getNamespace(), "textures/particle/" + texture.getPath() + ".png");
                    if (manager.getResource(path).isEmpty()) throw new IllegalArgumentException("Missing particle texture " + path);
                    frames.add(path);
                }
            }
            if (frames.isEmpty()) throw new IllegalArgumentException("Particle texture list is empty");
            return List.copyOf(frames);
        } catch (Exception error) {
            IS.LOGGER.warn("Cannot load first-person particle {}: {}; using fallback texture", name, error.getMessage());
            return List.of(FALLBACK);
        }
    }
}
