package omar.projects.interactivestuff.render;

import org.joml.Matrix4d;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import java.util.LinkedHashMap;
import java.util.Map;

/** Aim the local +Z axis using the actual hand/parent pose, including mirrored left hands. */
public final class ModelTargeting {
    private static final Map<Object, AimState> STATES = new LinkedHashMap<>(16, .75f, true);
    private static final class AimState {
        final Quaternionf rotation = new Quaternionf();
        long frame = Long.MIN_VALUE;
        double lift;
    }
    public record Clearance(double surface, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        public Clearance {
            for (double value : new double[]{surface, minX, minY, minZ, maxX, maxY, maxZ})
                if (!Double.isFinite(value)) throw new IllegalArgumentException("Clearance bounds must be finite");
            if (minX > maxX || minY > maxY || minZ > maxZ) throw new IllegalArgumentException("Invalid clearance bounds");
        }
    }
    private ModelTargeting() {}

    /** Share one shortest-path, time-normalized update between all pieces in a hand's group. */
    public static Quaternionf smooth(Object key, Quaternionf target, long frame, double delta, double speed, double weight) {
        var state = STATES.computeIfAbsent(key, ignored -> new AimState());
        if (state.frame != frame) {
            if (state.frame == Long.MIN_VALUE || frame < state.frame || frame - state.frame > 1) state.rotation.identity();
            double alpha = 1 - Math.pow(1 - speed, Math.max(0, Math.min(15, delta)));
            var desired = new Quaternionf().slerp(target, (float) weight);
            state.rotation.slerp(desired, (float) alpha).normalize();
            state.frame = frame;
        }
        if (STATES.size() > 256) STATES.remove(STATES.keySet().iterator().next());
        return new Quaternionf(state.rotation);
    }
    public static double previousLift(Object key) { var state = STATES.get(key); return state == null ? 0 : state.lift; }
    public static void rememberLift(Object key, double lift) { var state = STATES.get(key); if (state != null) state.lift = lift; }
    public static void clear() { STATES.clear(); }

    /** Bounds encompass all folded parts before aiming; both halves receive the same lift. */
    public static double clearance(Quaternionf aim, double px, double py, double pz, double translateY, Clearance bounds) {
        double lowest = Double.POSITIVE_INFINITY;
        for (double x : new double[]{bounds.minX, bounds.maxX})
            for (double y : new double[]{bounds.minY, bounds.maxY})
                for (double z : new double[]{bounds.minZ, bounds.maxZ}) {
                    var point = aim.transform(new Vector3f((float)(x - px), (float)(y - py), (float)(z - pz)));
                    lowest = Math.min(lowest, point.y + py + translateY);
                }
        return Math.max(0, bounds.surface - lowest);
    }
    public static Quaternionf rotation(Matrix4fc pose, Vector3d viewTarget) {
        var transform = new Matrix4d(pose);
        double determinant = transform.determinant();
        if (!Double.isFinite(determinant) || Math.abs(determinant) < 1e-12)
            return new Quaternionf();
        var direction = transform.invert().transformPosition(new Vector3d(viewTarget));
        if (!direction.isFinite() || direction.lengthSquared() < 1e-12) return new Quaternionf();
        // A shortest-arc rotation has an undefined roll at -Z. Keep a fixed local up axis instead.
        float yaw = (float) Math.atan2(direction.x, direction.z);
        float pitch = (float) -Math.atan2(direction.y, Math.hypot(direction.x, direction.z));
        return new Quaternionf().rotationY(yaw).rotateX(pitch);
    }
}
