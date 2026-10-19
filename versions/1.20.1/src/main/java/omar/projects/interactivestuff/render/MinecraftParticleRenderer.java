package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.particles.ParticleSystem;

/** Small native boundary; submitted geometry retains immutable per-frame particle snapshots. */
public final class MinecraftParticleRenderer {
    private MinecraftParticleRenderer() {}
    public static void clearCaches() { ParticleTextures.clear(); ModelDebugMarkers.clear(); }
    public static void submit(MultiBufferSource.BufferSource queue) {
        var client = Minecraft.getInstance();
        var config = ConfigHandler.INSTANCE;
        if (client.player == null || !client.options.getCameraType().isFirstPerson()) return;
        ParticleCameraCapture.update();
        var pose = new PoseStack();
        RenderTransforms.multiply(pose, new org.joml.Matrix4f(com.mojang.blaze3d.systems.RenderSystem.getModelViewStack().last().pose()).invert());
        var draws = new java.util.ArrayList<ParticleSystem.DrawParticle>();
        if (config.enableParticles) draws.addAll(ParticleSystem.INSTANCE.snapshot(config.particleEmitterMarkers));
        if (config.pivotMarkers) draws.addAll(ModelDebugMarkers.snapshot());
        if (draws.isEmpty()) return;
        // Finish solid hand buffers before drawing overlay markers/effects.
        queue.endBatch();
        for (var particle : draws) {
            var texture = particle.marker() ? ParticleTextures.MARKER : ParticleTextures.texture(particle);
            RenderType type = particle.depthTest(config.debugMarkersThroughItems)
                    ? RenderType.text(texture) : RenderType.textSeeThrough(texture);
            ParticleGeometry.draw(particle, pose.last(), queue.getBuffer(type));
        }
        // Flush while the hand pass still owns its camera matrix.
        queue.endBatch();
    }
}
