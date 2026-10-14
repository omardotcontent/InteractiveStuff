package omar.projects.interactivestuff.particles;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import omar.projects.interactivestuff.scripts.variables.Particle;
import omar.projects.interactivestuff.scripts.variables.ParticleEmitter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;

/** Client-only item/view/world coordinate simulation. Never adds particles to Minecraft's world engine. */
@VynType(name = "Particles")
public final class ParticleSystem {
    public static final ParticleSystem INSTANCE = new ParticleSystem();
    private final List<ParticleEmitter> emitters = new ArrayList<>();
    private long tick, frame, generation, dropped;
    private double interpolation;
    private boolean visible = true;
    private ParticleCamera camera = ParticleCamera.IDENTITY;

    public void setCamera(ParticleCamera camera) { this.camera = camera; }

    public void register(ParticleEmitter emitter) {
        if (emitters.size() >= 256) throw new IllegalArgumentException("Maximum 256 particle emitters per session");
        emitters.add(emitter);
    }

    public void bind(ParticleEmitter emitter, boolean mainHand, String item, Matrix4f anchor, int light) {
        var channel = emitter.channels[mainHand ? 0 : 1];
        if (!channel.item.equals(item)) {
            if (emitter.clearOnItemChange) channel.particles.clear();
            else for (var particle : channel.particles) particle.finishAt(channel.anchor);
        }
        channel.item = item; channel.anchor = new Matrix4f(anchor); channel.light = light; channel.boundTick = tick;
        channel.boundFrame = frame;
        channel.style = emitter.style.copy(); channel.rate = emitter.rate;
        channel.camera = camera;
    }

    public void beginFrame(double deltaTicks) { frame++; interpolation = Math.min(1, interpolation + Math.max(0, deltaTicks)); }

    public void tick(boolean enabled, double density, int limit, boolean firstPerson) {
        tick(enabled, density, limit, firstPerson, (mainHand, item) -> true);
    }

    public void tick(boolean enabled, double density, int limit, boolean firstPerson, BiPredicate<Boolean, String> heldItem) {
        tick++;
        interpolation = 0;
        visible = firstPerson;
        if (!enabled) { for (var emitter : emitters) emitter.clear(); return; }
        int maximum = Math.max(0, Math.min(4096, limit));
        double multiplier = Math.max(0, Math.min(2, density));
        long currentGeneration = generation;
        emitters.removeIf(emitter -> !emitter.valid());
        int live = getCount();
        for (var emitter : List.copyOf(emitters)) for (var channel : emitter.channels) {
            if (currentGeneration != generation) return;
            // Binding stays live between frames even below 20 FPS. Inventory changes stop it immediately.
            boolean active = firstPerson && channel.boundFrame != Long.MIN_VALUE && frame - channel.boundFrame <= 1
                    && heldItem.test(channel.mainHand, channel.item);
            if (!active) {
                channel.fraction = 0; channel.pending = 0;
                if (emitter.clearOnItemChange) { live -= channel.particles.size(); channel.particles.clear(); }
                else for (var particle : channel.particles) particle.finishAt(channel.anchor);
            }
            for (var particle : List.copyOf(channel.particles)) {
                if (!channel.particles.contains(particle)) continue;
                particle.tick();
                if (particle.isAlive()) emitter.callback(particle.style.onUpdate, particle);
                if (!particle.isAlive() || !emitter.valid() || live > maximum) {
                    if (channel.particles.remove(particle)) live--;
                    emitter.callback(particle.style.onDeath, particle);
                }
                if (currentGeneration != generation) return;
            }
            live = getCount(); // Callbacks may clear an emitter or request a burst.
            if (!active || !emitter.enabled || !emitter.valid()) continue;
            channel.fraction += channel.rate * multiplier + channel.pending * multiplier;
            channel.pending = 0;
            int births = Math.min(128, (int) channel.fraction);
            channel.fraction -= (int) channel.fraction;
            for (int i = 0; i < births; i++) {
                if (live >= maximum) { dropped += births - i; break; }
                var particle = new Particle(channel.style.copy(), emitter.random, channel.anchor, channel.mainHand, channel.camera);
                channel.particles.add(particle);
                live++;
                emitter.callback(particle.style.onSpawn, particle);
                if (currentGeneration != generation) return;
                live = getCount();
                if (!emitter.valid()) { emitter.clear(); break; }
            }
        }
    }

    public List<DrawParticle> snapshot(boolean markers) {
        var result = new ArrayList<DrawParticle>();
        if (!visible) return result;
        for (var emitter : emitters) {
            if (!emitter.valid()) continue;
            for (var channel : emitter.channels) {
                for (var particle : channel.particles) {
                    if (!particle.isAlive()) continue;
                    double progress = particle.progress(interpolation);
                    double size = Math.max(0, Math.min(100, particle.style.size.sample(progress)));
                    var position = particle.position(channel.anchor, interpolation, camera);
                    int alpha = color(particle.style.opacity.sample(progress));
                    if (size == 0 || alpha == 0 || !position.isFinite()) continue;
                    int rgba = alpha << 24 | color(particle.style.red.sample(progress)) << 16
                            | color(particle.style.green.sample(progress)) << 8 | color(particle.style.blue.sample(progress));
                    result.add(new DrawParticle(position, (float) size,
                            (float) (particle.style.rotation.sample(progress) % 360), rgba, particle.style.light == -1 ? channel.light : particle.style.light,
                            particle.style.type, particle.style.texture, particle.style.animation, particle.style.frameTicks,
                            Math.max(0, particle.age - 1 + interpolation), progress, particle.variant, particle.style.depth, false));
                }
                if (markers && channel.boundTick == tick) result.add(DrawParticle.markerAt(
                        channel.anchor.transformPosition(new Vector3f((float) channel.style.x, (float) channel.style.y, (float) channel.style.z)), channel.mainHand));
            }
        }
        result.sort(Comparator.comparingDouble(particle -> particle.position.z));
        return result;
    }

    private static int color(double value) { return (int) Math.round(Math.max(0, Math.min(1, value)) * 255); }
    @VynFunc public int getCount() { return emitters.stream().mapToInt(ParticleEmitter::getCount).sum(); }
    @VynFunc public int getEmitterCount() { return emitters.size(); }
    @VynFunc public long getDroppedCount() { return dropped; }
    @VynFunc public void clear() { generation++; for (var emitter : emitters) emitter.clear(); }
    public void reset() { clear(); emitters.clear(); interpolation = 0; dropped = 0; camera = ParticleCamera.IDENTITY; }
    public record DrawParticle(Vector3f position, float size, float rotation, int color, int light, String type, String texture,
                               String animation, double frameTicks, double age, double progress, int variant, boolean depth, boolean marker) {
        public static DrawParticle markerAt(Vector3f position, boolean mainHand) {
            return new DrawParticle(new Vector3f(position), 0.025f, 0, mainHand ? 0xFF40FF70 : 0xFFFFA040,
                    0xF000F0, "", "", "first", 1, 0, 0, 0, false, true);
        }
    }
}
