package omar.projects.interactivestuff.particles;

import me.abdelaziz.main.VynMain;
import me.abdelaziz.runtime.VynInstance;
import me.abdelaziz.util.NativeBinder;
import omar.projects.interactivestuff.scripts.ScriptRuntime;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.variables.Particle;
import omar.projects.interactivestuff.scripts.variables.ParticleEmitter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/** Deterministic simulation and real Vyn callback tests; no game/world particle registry. */
public final class ParticleRegressionChecks {
    private static final ParticleSystem SYSTEM = ParticleSystem.INSTANCE;
    private static BiConsumer<Boolean, String> check;
    private ParticleRegressionChecks() {}

    public static void run(BiConsumer<Boolean, String> assertions) {
        check = assertions;
        curves();
        coordinates();
        emission();
        callbacks();
        SYSTEM.reset();
    }

    private static void curves() {
        for (String family : List.of("Quad", "Cubic", "Quart", "Quint", "Sine", "Expo", "Circ", "Back", "Bounce", "Elastic")) {
            for (String direction : List.of("In", "Out", "InOut")) {
                var curve = ParticleCurve.between(2, 4, "ease" + direction + family);
                check.accept(close(curve.sample(0), 2) && close(curve.sample(1), 4)
                        && Double.isFinite(curve.sample(0.37)), "easing endpoints and finite interpolation: " + direction + family);
            }
        }
        var original = ParticleCurve.between(0, 0, "linear");
        var keyed = original.key(0.5, 1, "easeInQuad");
        check.accept(close(keyed.sample(0.25), 0.25) && close(keyed.sample(0.75), 0.5), "key easing applies to its arriving interval");
        check.accept(original.sample(0.5) == 0, "adding keys does not mutate existing particle curves");
        rejects(() -> ParticleCurve.between(0, 1, "unknown"), "unknown easing rejected");
        rejects(() -> original.key(1.01, 1, "linear"), "out-of-lifetime keys rejected");
        rejects(() -> ParticleCurve.constant(Double.NaN), "nonfinite particle values rejected");
        rejects(() -> ParticleCurve.constant(Double.MAX_VALUE), "values overflowing rendering coordinates rejected");
    }

    private static void coordinates() {
        var style = new ParticleStyle();
        style.type = "minecraft:flame";
        style.x = 0.5; style.y = 0.75; style.z = 0.5;
        var birth = new Matrix4f().translation(1, 2, -3);
        var moved = new Matrix4f().translation(4, 5, -6);
        var detached = new Particle(style.copy(), new Random(1), birth, true);
        style.attached = true;
        var attached = new Particle(style.copy(), new Random(1), birth, false);
        check.accept(attached.progress(0.9) == 0, "newborn curves do not jump backward on their first simulation tick");
        check.accept(detached.position(moved, 1).distance(new Vector3f(1.5f, 2.75f, -2.5f)) < 0.0001,
                "detached particle retains its birth view-space anchor");
        check.accept(attached.position(moved, 1).distance(new Vector3f(4.5f, 5.75f, -5.5f)) < 0.0001,
                "attached particle follows current item anchor");
        attached.setVelocity(1, 2, 0); attached.style.gravity = 0.5; attached.style.drag = 0.5;
        attached.tick();
        check.accept(close(attached.velocityX, 0.5) && close(attached.velocityY, 0.75), "gravity precedes drag and integration");
        check.accept(close(attached.position(new Matrix4f(), 0.5).x, 0.75), "positions interpolate between client ticks");
        check.accept(attached.isOffHand() && detached.isMainHand(), "particle callback retains logical hand identity");
        attached.finishAt(birth);
        check.accept(close(attached.position(moved, 1).x, 2), "finishing attached particles keep their last anchor after rebinding another item");
    }

    private static void bind(ParticleEmitter emitter, boolean main) {
        SYSTEM.bind(emitter, main, "minecraft:torch", new Matrix4f().translation(main ? 1 : -1, 0, -2), 0xF000F0);
    }

    private static int simulateFrames(int framesPerTick, boolean lowFps) {
        SYSTEM.reset();
        var emitter = new ParticleEmitter("minecraft:flame");
        emitter.setRate(0.5); emitter.setLifetime(1000);
        bind(emitter, true);
        for (int tick = 0; tick < 20; tick++) {
            if (!lowFps || tick % 2 == 0) for (int frame = 0; frame < framesPerTick; frame++) {
                SYSTEM.beginFrame(1.0 / framesPerTick);
                bind(emitter, true);
            }
            SYSTEM.tick(true, 1, 512, true);
        }
        return SYSTEM.getCount();
    }

    private static void emission() {
        check.accept(simulateFrames(1, false) == 10 && simulateFrames(6, false) == 10 && simulateFrames(1, true) == 10,
                "20, 120 and 10 FPS produce identical tick-based emission");
        SYSTEM.reset();
        var emitter = new ParticleEmitter("minecraft:flame");
        emitter.setRate(1); emitter.setLifetime(20);
        bind(emitter, true);
        emitter.setType("minecraft:soul_fire_flame"); emitter.setRate(2);
        bind(emitter, false);
        SYSTEM.tick(true, 1, 512, true);
        check.accept(emitter.channels[0].particles.size() == 1 && emitter.channels[1].particles.size() == 2,
                "per-hand settings and emission rates remain independent");
        check.accept(emitter.channels[0].particles.iterator().next().style.type.equals("minecraft:flame")
                && emitter.channels[1].particles.iterator().next().style.type.equals("minecraft:soul_fire_flame"), "per-hand texture snapshots remain independent");
        emitter.stop(); SYSTEM.tick(true, 1, 512, true);
        check.accept(emitter.getCount() == 3, "stop lets existing particles finish");
        emitter.clear(); check.accept(emitter.getCount() == 0, "clear removes both hands immediately");
        emitter.start(); emitter.setRate(0); bind(emitter, true); bind(emitter, false);
        emitter.burstForHand(true, 3); SYSTEM.tick(true, 1, 512, true);
        check.accept(emitter.channels[0].particles.size() == 3 && emitter.channels[1].particles.isEmpty(), "hand-selected burst only spawns in the selected hand");
        emitter.clear(); emitter.burst(1000); SYSTEM.tick(true, 1, 5, true);
        check.accept(SYSTEM.getCount() == 5 && SYSTEM.getDroppedCount() > 0, "global cap bounds bursts and tracks discarded particles");
        SYSTEM.tick(true, 1, 2, true);
        check.accept(SYSTEM.getCount() == 2, "reducing cap also trims existing particles");
        SYSTEM.tick(false, 1, 512, true);
        check.accept(SYSTEM.getCount() == 0, "global disable clears particles");
        emitter.setRate(1); bind(emitter, true);
        SYSTEM.tick(true, 0, 512, true);
        check.accept(SYSTEM.getCount() == 0, "zero density suppresses emission");
        SYSTEM.tick(true, 1, 512, true, (main, item) -> false);
        check.accept(SYSTEM.getCount() == 0, "inventory change stops emission without waiting for a render frame");
        emitter.burstForHand(true, 1); SYSTEM.tick(true, 1, 512, true);
        SYSTEM.tick(true, 1, 512, true, (main, item) -> false);
        check.accept(SYSTEM.getCount() == 2, "item switch preserves existing particles by default");
        emitter.setClearOnItemChange(true);
        SYSTEM.tick(true, 1, 512, true, (main, item) -> false);
        check.accept(SYSTEM.getCount() == 0, "clear-on-item-change clears immediately");
        bind(emitter, true); SYSTEM.tick(true, 1, 512, false);
        check.accept(SYSTEM.snapshot(true).isEmpty() && SYSTEM.getCount() == 0, "third-person mode suppresses emission and drawing");
        rejects(() -> emitter.setLifetime(-1), "negative lifetime rejected");
        rejects(() -> emitter.setType("flame"), "particle type requires a namespace");
        rejects(() -> emitter.setTexture("pack:spark"), "direct texture requires a PNG resource path");
    }

    private static ScriptRuntime runtime(List<String> errors) {
        VynMain.setStdListener(environment -> {
            NativeBinder.bind(environment, ParticleEmitter.class);
            NativeBinder.bind(environment, Particle.class);
            NativeBinder.defineConstant(environment, "particles", SYSTEM);
        });
        return new ScriptRuntime(() -> VynMain.loadLines(""), errors::add);
    }

    private static ParticleEmitter emitter(Script script) {
        return (ParticleEmitter) ((VynInstance) script.getEnvironment().get("effect").asJavaObject()).get("__host__").asJavaObject();
    }

    private static void callbacks() {
        SYSTEM.reset();
        var errors = new ArrayList<String>();
        var runtime = runtime(errors);
        runtime.addScript("test", "callbacks", """
                make effect = new ParticleEmitter("minecraft:flame")
                effect.setRate(1)
                effect.setLifetime(2)
                effect.setOnSpawn("born")
                effect.setOnUpdate("moving")
                effect.setOnDeath("died")
                make births = 0
                make updates = 0
                make deaths = 0
                task born takes particle do
                    make births = births + 1
                    particle.setVelocity(0.1, 0, 0)
                end
                task moving takes particle do make updates = updates + 1 end
                task died takes particle do make deaths = deaths + 1 end
                task onTick do reply nothing end
                """);
        runtime.load();
        var script = runtime.requireScript("test", "callbacks");
        check.accept(errors.isEmpty() && script.getState() == Script.State.LOADED, "real Vyn emitter setters and callbacks initialize");
        var effect = emitter(script); bind(effect, true);
        SYSTEM.tick(true, 1, 512, true);
        effect.stop(); SYSTEM.tick(true, 1, 512, true); SYSTEM.tick(true, 1, 512, true);
        check.accept(script.getEnvironment().get("births").asInt() == 1 && script.getEnvironment().get("updates").asInt() == 1
                && script.getEnvironment().get("deaths").asInt() == 1, "Vyn spawn/update/death callbacks fire exactly once at the expected ages");
        runtime.load();
        check.accept(!effect.valid(), "an emitter cannot survive its owner's reload generation");
        SYSTEM.tick(true, 1, 512, true);
        check.accept(SYSTEM.getEmitterCount() == 1, "reload releases the previous generation's emitter");
        effect = emitter(script); effect.setOnSpawn("missingTask"); bind(effect, true);
        var healthy = new ParticleEmitter("minecraft:flame"); healthy.setRate(1); bind(healthy, true);
        SYSTEM.tick(true, 1, 512, true);
        check.accept(errors.size() == 1 && script.getState() == Script.State.ERROR, "bad callbacks fail their owner once");
        check.accept(healthy.getCount() == 1, "a failing callback does not interrupt another emitter");
        SYSTEM.tick(true, 1, 512, true);
        check.accept(errors.size() == 1, "failed callbacks do not repeatedly report");
        SYSTEM.clear();
        check.accept(SYSTEM.getCount() == 0, "script-visible global clear clears all particles");
    }

    private static void rejects(Runnable action, String message) {
        try { action.run(); check.accept(false, message); }
        catch (IllegalArgumentException expected) { check.accept(true, message); }
    }
    private static boolean close(double a, double b) { return Math.abs(a - b) < 0.00001; }
}
