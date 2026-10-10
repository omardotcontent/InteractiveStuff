package omar.projects.interactivestuff.particles;

/** Settings are copied at birth; changing an emitter never rewrites an existing particle. */
public final class ParticleStyle {
    public String type, texture = "", animation = "lifetime";
    public double lifetime = 20, lifetimeSpread, frameTicks = 1;
    public double x, y, z, spreadX, spreadY, spreadZ;
    public double velocityX, velocityY, velocityZ, velocitySpreadX, velocitySpreadY, velocitySpreadZ;
    public double gravity, drag;
    public boolean attached, depth = true;
    public int light = -1;
    public ParticleCurve size = ParticleCurve.constant(0.06), opacity = ParticleCurve.constant(1), rotation = ParticleCurve.constant(0);
    public ParticleCurve red = ParticleCurve.constant(1), green = ParticleCurve.constant(1), blue = ParticleCurve.constant(1);
    public String onSpawn = "", onUpdate = "", onDeath = "";

    public ParticleStyle copy() {
        var copy = new ParticleStyle();
        copy.type = type; copy.texture = texture; copy.animation = animation;
        copy.lifetime = lifetime; copy.lifetimeSpread = lifetimeSpread; copy.frameTicks = frameTicks;
        copy.x = x; copy.y = y; copy.z = z;
        copy.spreadX = spreadX; copy.spreadY = spreadY; copy.spreadZ = spreadZ;
        copy.velocityX = velocityX; copy.velocityY = velocityY; copy.velocityZ = velocityZ;
        copy.velocitySpreadX = velocitySpreadX; copy.velocitySpreadY = velocitySpreadY; copy.velocitySpreadZ = velocitySpreadZ;
        copy.gravity = gravity; copy.drag = drag; copy.attached = attached; copy.depth = depth; copy.light = light;
        copy.size = size; copy.opacity = opacity; copy.rotation = rotation;
        copy.red = red; copy.green = green; copy.blue = blue;
        copy.onSpawn = onSpawn; copy.onUpdate = onUpdate; copy.onDeath = onDeath;
        return copy;
    }
}
