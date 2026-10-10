package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/** Hand poses can include inverse camera rotation. Store actual view coordinates and cancel the draw-pass view. */
public final class ParticleView {
    private ParticleView() {}
    public static Matrix4f capture(Matrix4fc model) {
        return new Matrix4f(RenderSystem.getModelViewStack()).mul(model);
    }
    public static PoseStack pose() {
        var matrices = new PoseStack();
        RenderTransforms.multiply(matrices, new Matrix4f(RenderSystem.getModelViewStack()).invert());
        return matrices;
    }
}
