package omar.projects.interactivestuff.particles;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** Immutable lifetime keys. Each key's easing controls the interval arriving at that key. */
public final class ParticleCurve {
    private record Key(double value, String easing) {}
    private final TreeMap<Double, Key> keys;

    private ParticleCurve(TreeMap<Double, Key> keys) { this.keys = keys; }

    public static ParticleCurve constant(double value) { return between(value, value, "linear"); }

    public static ParticleCurve between(double start, double end, String easing) {
        var keys = new TreeMap<Double, Key>();
        keys.put(0.0, new Key(finite(start), "linear"));
        keys.put(1.0, new Key(finite(end), normalize(easing)));
        return new ParticleCurve(keys);
    }

    public ParticleCurve key(double progress, double value, String easing) {
        if (!Double.isFinite(progress) || progress < 0 || progress > 1)
            throw new IllegalArgumentException("Particle keyframe progress must be between 0 and 1");
        var updated = new TreeMap<>(keys);
        updated.put(progress, new Key(finite(value), normalize(easing)));
        return new ParticleCurve(updated);
    }

    public double sample(double progress) {
        double t = Math.max(0, Math.min(1, progress));
        Map.Entry<Double, Key> left = keys.floorEntry(t);
        Map.Entry<Double, Key> right = keys.ceilingEntry(t);
        if (left == null) return keys.firstEntry().getValue().value;
        if (right == null || left.getKey().equals(right.getKey())) return left.getValue().value;
        double amount = ease(right.getValue().easing, (t - left.getKey()) / (right.getKey() - left.getKey()));
        return left.getValue().value + (right.getValue().value - left.getValue().value) * amount;
    }

    public static double finite(double value) {
        if (!Double.isFinite(value) || !Float.isFinite((float) value))
            throw new IllegalArgumentException("Particle values must fit in a finite rendering coordinate");
        return value;
    }

    private static String normalize(String name) {
        if (name == null) throw new IllegalArgumentException("Particle easing cannot be null");
        String result = name.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "");
        if (result.startsWith("ease")) result = result.substring(4);
        String family = result.replaceFirst("^(inout|out|in)", "");
        if (!java.util.Set.of("linear", "step", "quad", "cubic", "quart", "quint", "sine", "expo", "circ", "back", "bounce", "elastic").contains(family))
            throw new IllegalArgumentException("Unknown particle easing: " + name);
        return result;
    }

    private static double ease(String name, double t) {
        if (t <= 0 || t >= 1) return t;
        if (name.startsWith("inout")) {
            String family = name.substring(5);
            return t < 0.5 ? in(family, t * 2) / 2 : 1 - in(family, 2 - t * 2) / 2;
        }
        if (name.startsWith("out")) return 1 - in(name.substring(3), 1 - t);
        return in(name.startsWith("in") ? name.substring(2) : name, t);
    }

    private static double in(String family, double t) {
        return switch (family) {
            case "step" -> t < 1 ? 0 : 1;
            case "quad" -> t * t;
            case "cubic" -> t * t * t;
            case "quart" -> Math.pow(t, 4);
            case "quint" -> Math.pow(t, 5);
            case "sine" -> 1 - Math.cos(t * Math.PI / 2);
            case "expo" -> t == 0 ? 0 : Math.pow(2, 10 * t - 10);
            case "circ" -> 1 - Math.sqrt(Math.max(0, 1 - t * t));
            case "back" -> 2.70158 * t * t * t - 1.70158 * t * t;
            case "elastic" -> t == 0 || t == 1 ? t : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * 2 * Math.PI / 3);
            case "bounce" -> 1 - bounce(1 - t);
            default -> t;
        };
    }

    private static double bounce(double t) {
        if (t < 1 / 2.75) return 7.5625 * t * t;
        if (t < 2 / 2.75) { t -= 1.5 / 2.75; return 7.5625 * t * t + 0.75; }
        if (t < 2.5 / 2.75) { t -= 2.25 / 2.75; return 7.5625 * t * t + 0.9375; }
        t -= 2.625 / 2.75;
        return 7.5625 * t * t + 0.984375;
    }
}
