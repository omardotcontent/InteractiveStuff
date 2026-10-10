package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import me.abdelaziz.util.NativeBinder;
import omar.projects.interactivestuff.particles.ParticleCurve;
import omar.projects.interactivestuff.particles.ParticleStyle;
import omar.projects.interactivestuff.particles.ParticleSystem;
import omar.projects.interactivestuff.scripts.ScriptExecutionContext;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.objects.Script;
import org.joml.Matrix4f;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

/** A persistent script object with separate channels for the two inventory hands. */
@VynType(name = "ParticleEmitter")
public final class ParticleEmitter {
    public final ParticleStyle style = new ParticleStyle();
    public final Channel[] channels = {new Channel(true), new Channel(false)};
    public final Random random = new Random();
    private final Script owner = ScriptExecutionContext.current();
    private final long generation = owner == null ? 0 : owner.getGeneration();
    public double rate;
    public boolean enabled = true, clearOnItemChange;

    @VynConstructor public ParticleEmitter(String type) {
        setType(type);
        ParticleSystem.INSTANCE.register(this);
    }

    @VynFunc public void attach(ItemModel item) { ItemModelRenderRegistry.bind(this, item); }
    @VynFunc public void emit() { burst(1); }
    @VynFunc public void burst(int count) { for (var channel : channels) channel.pending = Math.min(128, channel.pending + Math.max(0, Math.min(128, count))); }
    @VynFunc public void burstForHand(boolean mainHand, int count) {
        var channel = channels[mainHand ? 0 : 1];
        channel.pending = Math.min(128, channel.pending + Math.max(0, Math.min(128, count)));
    }
    @VynFunc public void start() { enabled = true; }
    @VynFunc public void stop() { enabled = false; for (var channel : channels) channel.pending = 0; }
    @VynFunc public void clear() { for (var channel : channels) { channel.particles.clear(); channel.pending = 0; channel.fraction = 0; } }
    @VynFunc public int getCount() { return channels[0].particles.size() + channels[1].particles.size(); }
    @VynFunc public void setSeed(long seed) { random.setSeed(seed); }
    @VynFunc public void setType(String type) {
        if (type == null || !type.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) throw new IllegalArgumentException("Particle type requires a namespaced ID");
        style.type = type;
        style.texture = "";
    }
    @VynFunc public void setTexture(String texture) {
        if (texture == null || !texture.matches("[a-z0-9_.-]+:textures/[a-z0-9_./-]+\\.png")) throw new IllegalArgumentException("Use a namespaced PNG path, e.g. pack:textures/particle/spark.png");
        style.texture = texture;
    }
    @VynFunc public void setRate(double particlesPerTick) { rate = range(particlesPerTick, 0, 64, "Emission rate"); }
    @VynFunc public void setLifetime(double ticks) { style.lifetime = range(ticks, 1, 12000, "Lifetime"); }
    @VynFunc public void setLifetimeSpread(double ticks) { style.lifetimeSpread = range(ticks, 0, 12000, "Lifetime spread"); }
    @VynFunc public void setPosition(double x, double y, double z) { style.x = ParticleCurve.finite(x); style.y = ParticleCurve.finite(y); style.z = ParticleCurve.finite(z); }
    @VynFunc public void setSpread(double x, double y, double z) { style.spreadX = range(x, 0, 100, "Spread"); style.spreadY = range(y, 0, 100, "Spread"); style.spreadZ = range(z, 0, 100, "Spread"); }
    @VynFunc public void setVelocity(double x, double y, double z) { style.velocityX = ParticleCurve.finite(x); style.velocityY = ParticleCurve.finite(y); style.velocityZ = ParticleCurve.finite(z); }
    @VynFunc public void setVelocitySpread(double x, double y, double z) { style.velocitySpreadX = range(x, 0, 100, "Velocity spread"); style.velocitySpreadY = range(y, 0, 100, "Velocity spread"); style.velocitySpreadZ = range(z, 0, 100, "Velocity spread"); }
    @VynFunc public void setGravity(double gravity) { style.gravity = ParticleCurve.finite(gravity); }
    @VynFunc public void setDrag(double drag) { style.drag = range(drag, 0, 1, "Drag"); }
    @VynFunc public void setAttached(boolean attached) { style.attached = attached; }
    @VynFunc public void setDepthTest(boolean depth) { style.depth = depth; }
    @VynFunc public void setClearOnItemChange(boolean clear) { clearOnItemChange = clear; }
    @VynFunc public void setLight(int light) {
        if (light < -1 || light > 15) throw new IllegalArgumentException("Particle light must be -1 (item light) or 0..15");
        style.light = light == -1 ? -1 : light << 20 | light << 4;
    }
    @VynFunc public void setAnimation(String mode, double frameTicks) {
        if (!List.of("lifetime", "loop", "random", "first").contains(mode)) throw new IllegalArgumentException("Animation must be lifetime, loop, random or first");
        style.animation = mode; style.frameTicks = range(frameTicks, 0.05, 12000, "Animation frame duration");
    }
    @VynFunc public void setSize(double size) { style.size = ParticleCurve.constant(range(size, 0, 100, "Size")); }
    @VynFunc public void setOpacity(double opacity) { style.opacity = ParticleCurve.constant(range(opacity, 0, 1, "Opacity")); }
    @VynFunc public void setRotation(double degrees) { style.rotation = ParticleCurve.constant(degrees); }
    @VynFunc public void setSizeOverLife(double start, double end, String easing) { style.size = ParticleCurve.between(range(start, 0, 100, "Size"), range(end, 0, 100, "Size"), easing); }
    @VynFunc public void setOpacityOverLife(double start, double end, String easing) { style.opacity = ParticleCurve.between(range(start, 0, 1, "Opacity"), range(end, 0, 1, "Opacity"), easing); }
    @VynFunc public void setRotationOverLife(double start, double end, String easing) { style.rotation = ParticleCurve.between(start, end, easing); }
    @VynFunc public void addSizeKeyframe(double progress, double size, String easing) { style.size = style.size.key(progress, range(size, 0, 100, "Size"), easing); }
    @VynFunc public void addOpacityKeyframe(double progress, double opacity, String easing) { style.opacity = style.opacity.key(progress, range(opacity, 0, 1, "Opacity"), easing); }
    @VynFunc public void addRotationKeyframe(double progress, double degrees, String easing) { style.rotation = style.rotation.key(progress, degrees, easing); }
    @VynFunc public void setColor(int red, int green, int blue) { setColorOverLife(red, green, blue, red, green, blue, "linear"); }
    @VynFunc public void setColorOverLife(int r1, int g1, int b1, int r2, int g2, int b2, String easing) {
        style.red = ParticleCurve.between(color(r1), color(r2), easing);
        style.green = ParticleCurve.between(color(g1), color(g2), easing);
        style.blue = ParticleCurve.between(color(b1), color(b2), easing);
    }
    @VynFunc public void addColorKeyframe(double progress, int red, int green, int blue, String easing) {
        style.red = style.red.key(progress, color(red), easing); style.green = style.green.key(progress, color(green), easing); style.blue = style.blue.key(progress, color(blue), easing);
    }
    @VynFunc public void setOnSpawn(String task) { style.onSpawn = task; }
    @VynFunc public void setOnUpdate(String task) { style.onUpdate = task; }
    @VynFunc public void setOnDeath(String task) { style.onDeath = task; }

    public boolean valid() { return owner == null || owner.getGeneration() == generation && owner.getState() == Script.State.LOADED; }
    public void callback(String name, Particle particle) {
        if (name == null || name.isEmpty() || owner == null || !valid()) return;
        try {
            ScriptExecutionContext.run(owner, () -> {
                var environment = owner.getEnvironment();
                if (!environment.hasFunction(name)) throw new IllegalArgumentException("Missing particle callback task: " + name);
                environment.getFunction(name).call(environment, List.of(NativeBinder.toValue(environment, particle)));
            });
        } catch (Exception error) { owner.fail("particle " + name, error); }
    }

    private static double color(int value) { return range(value, 0, 255, "Color") / 255; }
    private static double range(double value, double minimum, double maximum, String name) {
        if (!Double.isFinite(value) || value < minimum || value > maximum) throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum);
        return value;
    }

    public static final class Channel {
        public final boolean mainHand;
        public final Set<Particle> particles = new LinkedHashSet<>();
        public Matrix4f anchor = new Matrix4f();
        public long boundTick = Long.MIN_VALUE;
        public long boundFrame = Long.MIN_VALUE;
        public String item = "";
        public int light, pending;
        public double fraction, rate;
        public ParticleStyle style;
        private Channel(boolean mainHand) { this.mainHand = mainHand; }
    }
}
