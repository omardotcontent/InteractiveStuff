package omar.projects.interactivestuff.particles;

import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;

/** Immutable camera frame. Subtract world coordinates before narrowing them to render floats. */
public final class ParticleCamera {
    public static final ParticleCamera IDENTITY = new ParticleCamera(0, 0, 0, new Quaternionf());
    private final double x, y, z;
    private final Quaterniond toWorld, toView;

    public ParticleCamera(double x, double y, double z, Quaternionfc rotation) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                || !Float.isFinite(rotation.x()) || !Float.isFinite(rotation.y())
                || !Float.isFinite(rotation.z()) || !Float.isFinite(rotation.w())
                || rotation.lengthSquared() < 1e-12) throw new IllegalArgumentException("Invalid particle camera");
        this.x = x; this.y = y; this.z = z;
        toWorld = new Quaterniond(rotation).normalize();
        toView = new Quaterniond(toWorld).conjugate();
    }

    public Vector3d toWorld(double x, double y, double z) {
        return toWorld.transform(new Vector3d(x, y, z)).add(this.x, this.y, this.z);
    }

    public Vector3d directionToWorld(double x, double y, double z) {
        return toWorld.transform(new Vector3d(x, y, z));
    }

    public Vector3d toView(double x, double y, double z) {
        return toView.transform(new Vector3d(x - this.x, y - this.y, z - this.z));
    }
}
