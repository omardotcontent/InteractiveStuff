package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import omar.projects.interactivestuff.particles.ParticleCurve;
import omar.projects.interactivestuff.particles.ParticleCamera;
import omar.projects.interactivestuff.particles.ParticleStyle;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3d;

@VynType(name = "Particle")
public final class Particle {
    public final ParticleStyle style;
    public final double lifetime;
    public final int variant;
    public final boolean mainHand;
    private final double spawnX, spawnY, spawnZ;
    public double x, y, z, previousX, previousY, previousZ, velocityX, velocityY, velocityZ;
    public double age;
    private boolean alive = true;
    private Matrix4f finishedAnchor;

    public Particle(ParticleStyle style, java.util.Random random, Matrix4f anchor, boolean mainHand) {
        this(style, random, anchor, mainHand, ParticleCamera.IDENTITY);
    }

    public Particle(ParticleStyle style, java.util.Random random, Matrix4f anchor, boolean mainHand, ParticleCamera camera) {
        this.style = style;
        this.mainHand = mainHand;
        lifetime = Math.max(1, style.lifetime + jitter(random, style.lifetimeSpread));
        variant = random.nextInt(Integer.MAX_VALUE);
        x = style.x + jitter(random, style.spreadX); y = style.y + jitter(random, style.spreadY); z = style.z + jitter(random, style.spreadZ);
        velocityX = style.velocityX + jitter(random, style.velocitySpreadX);
        velocityY = style.velocityY + jitter(random, style.velocitySpreadY);
        velocityZ = style.velocityZ + jitter(random, style.velocitySpreadZ);
        if (!style.attached) {
            var point = new Vector3d(x, y, z).mulPosition(anchor);
            var velocity = new Vector3d(velocityX, velocityY, velocityZ).mulDirection(anchor);
            if (style.worldSpace) {
                point = camera.toWorld(point.x, point.y, point.z);
                velocity = camera.directionToWorld(velocity.x, velocity.y, velocity.z);
            }
            x = point.x; y = point.y; z = point.z;
            velocityX = velocity.x; velocityY = velocity.y; velocityZ = velocity.z;
        }
        previousX = x; previousY = y; previousZ = z;
        spawnX = x; spawnY = y; spawnZ = z;
    }

    private static double jitter(java.util.Random random, double amount) { return (random.nextDouble() * 2 - 1) * amount; }

    public void tick() {
        previousX = x; previousY = y; previousZ = z;
        velocityY -= style.gravity;
        double damping = 1 - style.drag;
        velocityX *= damping; velocityY *= damping; velocityZ *= damping;
        x += velocityX; y += velocityY; z += velocityZ;
        age++;
        if (age >= lifetime || !Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                || Math.abs(x) > Float.MAX_VALUE || Math.abs(y) > Float.MAX_VALUE || Math.abs(z) > Float.MAX_VALUE) alive = false;
    }

    public Vector3f position(Matrix4f anchor, double interpolation) {
        return position(anchor, interpolation, ParticleCamera.IDENTITY);
    }

    public Vector3f position(Matrix4f anchor, double interpolation, ParticleCamera camera) {
        double px = previousX + (x - previousX) * interpolation;
        double py = previousY + (y - previousY) * interpolation;
        double pz = previousZ + (z - previousZ) * interpolation;
        if (style.worldSpace) {
            var view = camera.toView(px, py, pz);
            return new Vector3f((float) view.x, (float) view.y, (float) view.z);
        }
        var position = new Vector3f((float) px, (float) py, (float) pz);
        return style.attached ? (finishedAnchor == null ? anchor : finishedAnchor).transformPosition(position) : position;
    }

    public void finishAt(Matrix4f anchor) {
        if (style.attached && finishedAnchor == null) finishedAnchor = new Matrix4f(anchor);
    }

    public double progress(double interpolation) { return Math.max(0, Math.min(1, (age - 1 + interpolation) / lifetime)); }
    @VynFunc public double getAge() { return age; }
    @VynFunc public double getLifetime() { return lifetime; }
    @VynFunc public double getProgress() { return Math.min(1, age / lifetime); }
    @VynFunc public double getX() { return x; }
    @VynFunc public double getY() { return y; }
    @VynFunc public double getZ() { return z; }
    @VynFunc public double getSpawnX() { return spawnX; }
    @VynFunc public double getSpawnY() { return spawnY; }
    @VynFunc public double getSpawnZ() { return spawnZ; }
    @VynFunc public boolean isAlive() { return alive; }
    @VynFunc public boolean isMainHand() { return mainHand; }
    @VynFunc public boolean isOffHand() { return !mainHand; }
    @VynFunc public void kill() { alive = false; }
    @VynFunc public void setPosition(double x, double y, double z) { this.x = ParticleCurve.finite(x); this.y = ParticleCurve.finite(y); this.z = ParticleCurve.finite(z); }
    @VynFunc public void setVelocity(double x, double y, double z) { velocityX = ParticleCurve.finite(x); velocityY = ParticleCurve.finite(y); velocityZ = ParticleCurve.finite(z); }
    @VynFunc public void setOpacity(double opacity) { style.opacity = ParticleCurve.constant(Math.max(0, Math.min(1, ParticleCurve.finite(opacity)))); }
    @VynFunc public void setSize(double size) { style.size = ParticleCurve.constant(Math.max(0, ParticleCurve.finite(size))); }
    @VynFunc public void setRotation(double degrees) { style.rotation = ParticleCurve.constant(degrees); }
    @VynFunc public void setColor(int red, int green, int blue) {
        style.red = ParticleCurve.constant(Math.max(0, Math.min(255, red)) / 255.0);
        style.green = ParticleCurve.constant(Math.max(0, Math.min(255, green)) / 255.0);
        style.blue = ParticleCurve.constant(Math.max(0, Math.min(255, blue)) / 255.0);
    }
}
