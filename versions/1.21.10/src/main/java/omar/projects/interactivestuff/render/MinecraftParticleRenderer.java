package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.particles.ParticleSystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/** Small native boundary; submitted geometry retains immutable per-frame particle snapshots. */
public final class MinecraftParticleRenderer {
    private MinecraftParticleRenderer() {}
    public static void clearCaches() { ParticleTextures.clear(); }
    public static void submit(SubmitNodeCollector queue) {
        var client = Minecraft.getInstance();
        var config = ConfigHandler.INSTANCE;
        if (!config.enableParticles || client.player == null || !client.options.getCameraType().isFirstPerson()) return;
        var pose = ParticleView.pose();
        var batches = new LinkedHashMap<RenderType, List<ParticleSystem.DrawParticle>>();
        for (var particle : ParticleSystem.INSTANCE.snapshot(config.particleEmitterMarkers)) {
            RenderType type = particle.marker() ? RenderType.debugQuads() : particle.depth()
                    ? RenderType.text(ParticleTextures.texture(particle)) : RenderType.textSeeThrough(ParticleTextures.texture(particle));
            batches.computeIfAbsent(type, ignored -> new ArrayList<>()).add(particle);
        }
        batches.forEach((type, particles) -> queue.submitCustomGeometry(pose, type, (matrix, vertices) -> {
            for (var particle : particles) ParticleGeometry.draw(particle, matrix, vertices);
        }));
    }
}
