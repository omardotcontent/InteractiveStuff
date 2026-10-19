package omar.projects.interactivestuff.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.particles.ParticleSystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/** Small native boundary; submitted geometry retains immutable per-frame particle snapshots. */
public final class MinecraftParticleRenderer {
    private MinecraftParticleRenderer() {}
    public static void clearCaches() { ParticleTextures.clear(); ModelDebugMarkers.clear(); }
    public static void submit(SubmitNodeCollector queue) {
        var client = Minecraft.getInstance();
        var config = ConfigHandler.INSTANCE;
        if (client.player == null || !client.options.getCameraType().isFirstPerson()) return;
        ParticleCameraCapture.update();
        var pose = ParticleView.pose();
        var batches = new LinkedHashMap<RenderType, List<ParticleSystem.DrawParticle>>();
        var draws = new java.util.ArrayList<ParticleSystem.DrawParticle>();
        if (config.enableParticles) draws.addAll(ParticleSystem.INSTANCE.snapshot(config.particleEmitterMarkers));
        if (config.pivotMarkers) draws.addAll(ModelDebugMarkers.snapshot());
        for (var particle : draws) {
            var texture = particle.marker() ? ParticleTextures.MARKER : ParticleTextures.texture(particle);
            RenderType type = particle.depthTest(config.debugMarkersThroughItems)
                    ? RenderTypes.text(texture) : RenderTypes.textSeeThrough(texture);
            batches.computeIfAbsent(type, ignored -> new ArrayList<>()).add(particle);
        }
        // Custom geometry in the item bucket can be consumed before its solid models.
        var effects = queue.order(1);
        batches.forEach((type, particles) -> effects.submitCustomGeometry(pose, type, (matrix, vertices) -> {
            for (var particle : particles) ParticleGeometry.draw(particle, matrix, vertices);
        }));
    }
}
