package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import omar.projects.interactivestuff.particles.ParticleSystem.DrawParticle;
import omar.projects.interactivestuff.particles.ParticleSystem.Marker;

/** Camera-facing quads in first-person view coordinates, independent of item/model orientation. */
public final class ParticleGeometry {
    private static final float[] DIAMOND_X = {0, 1, 0, -1}, DIAMOND_Y = {-1, 0, 1, 0};
    private ParticleGeometry() {}

    public static void draw(DrawParticle particle, PoseStack.Pose pose, VertexConsumer vertices) {
        if (particle.markerKind() == Marker.PIVOT) {
            diamond(particle, pose, vertices);
            quad(particle, pose, vertices, particle.size() / 6, particle.size() / 6);
        } else if (particle.marker()) {
            quad(particle, pose, vertices, particle.size(), particle.size() / 6);
            quad(particle, pose, vertices, particle.size() / 6, particle.size());
        } else quad(particle, pose, vertices, particle.size(), particle.size());
    }

    private static void diamond(DrawParticle particle, PoseStack.Pose pose, VertexConsumer vertices) {
        float radius = particle.size() / 2;
        for (int edge = 0; edge < 4; edge++) {
            int next = (edge + 1) % 4;
            float x = DIAMOND_X[edge] * radius, y = DIAMOND_Y[edge] * radius;
            float nextX = DIAMOND_X[next] * radius, nextY = DIAMOND_Y[next] * radius;
            vertex(particle, pose, vertices, x, y, 1, 0, 0, 1);
            vertex(particle, pose, vertices, nextX, nextY, 1, 0, 1, 1);
            vertex(particle, pose, vertices, nextX * 0.55f, nextY * 0.55f, 1, 0, 1, 0);
            vertex(particle, pose, vertices, x * 0.55f, y * 0.55f, 1, 0, 0, 0);
        }
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
        ParticleVertex.write(vertices, pose, position.x + x * cosine - y * sine,
                position.y + x * sine + y * cosine, position.z, particle.color(), u, v, particle.light());
    }
}
