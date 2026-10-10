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
    public static void clearCaches() { ParticleTextures.clear(); }
    public static void submit(MultiBufferSource.BufferSource queue) {
        var client = Minecraft.getInstance();
        var config = ConfigHandler.INSTANCE;
        if (!config.enableParticles || client.player == null || !client.options.getCameraType().isFirstPerson()) return;
        var pose = new PoseStack();
        RenderTransforms.multiply(pose, new org.joml.Matrix4f(com.mojang.blaze3d.systems.RenderSystem.getModelViewStack()).invert());
        for (var particle : ParticleSystem.INSTANCE.snapshot(config.particleEmitterMarkers)) {
            RenderType type = particle.marker() ? RenderType.debugQuads() : particle.depth()
                    ? RenderType.text(ParticleTextures.texture(particle)) : RenderType.textSeeThrough(ParticleTextures.texture(particle));
            ParticleGeometry.draw(particle, pose.last(), queue.getBuffer(type));
        }
        // Flush while the hand pass still owns its camera matrix.
        queue.endBatch();
    }
}
