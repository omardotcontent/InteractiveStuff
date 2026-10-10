package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import omar.projects.interactivestuff.particles.ParticleSystem.DrawParticle;

/** Camera-facing quads in first-person view coordinates, independent of item/model orientation. */
public final class ParticleGeometry {
    private ParticleGeometry() {}

    public static void draw(DrawParticle particle, PoseStack.Pose pose, VertexConsumer vertices) {
        if (particle.marker()) {
            quad(particle, pose, vertices, particle.size(), particle.size() / 6);
            quad(particle, pose, vertices, particle.size() / 6, particle.size());
        } else quad(particle, pose, vertices, particle.size(), particle.size());
    }

    private static void quad(DrawParticle particle, PoseStack.Pose pose, VertexConsumer vertices, float width, float height) {
        double angle = Math.toRadians(particle.rotation());
        float cosine = (float) Math.cos(angle), sine = (float) Math.sin(angle);
        vertex(particle, pose, vertices, -width / 2, -height / 2, cosine, sine, 0, 1);
        vertex(particle, pose, vertices, width / 2, -height / 2, cosine, sine, 1, 1);
        vertex(particle, pose, vertices, width / 2, height / 2, cosine, sine, 1, 0);
        vertex(particle, pose, vertices, -width / 2, height / 2, cosine, sine, 0, 0);
    }

    private static void vertex(DrawParticle particle, PoseStack.Pose pose, VertexConsumer vertices,
                               float x, float y, float cosine, float sine, float u, float v) {
        var position = particle.position();
        vertices.addVertex(pose, position.x + x * cosine - y * sine, position.y + x * sine + y * cosine, position.z)
                .setColor(particle.color()).setUv(u, v).setLight(particle.light());
    }
}
