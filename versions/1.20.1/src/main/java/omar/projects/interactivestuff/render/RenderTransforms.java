package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Matrix4f;

/** Version-specific pose operation, kept outside the script-facing model. */
public final class RenderTransforms {
    private RenderTransforms() {}

    public static void multiply(PoseStack matrices, Matrix4f transform) { matrices.mulPoseMatrix(transform); }
    public static void rotate(PoseStack matrices, Quaternionf rotation) { matrices.mulPose(rotation); }
}
