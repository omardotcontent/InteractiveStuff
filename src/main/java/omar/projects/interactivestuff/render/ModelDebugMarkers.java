package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.particles.ParticleSystem.DrawParticle;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

/** Frame-local purple diamond markers distinguish model pivots from emitter crosses. */
public final class ModelDebugMarkers {
    private static final List<DrawParticle> PIVOTS = new ArrayList<>();
    private ModelDebugMarkers() {}
    public static void clear() { PIVOTS.clear(); }
    public static void capture(ItemModel model, PoseStack matrices) {
        if (!ConfigHandler.INSTANCE.pivotMarkers) return;
        boolean editing = ConfigHandler.INSTANCE.resourcePackMatrixEditing;
        matrices.pushPose();
        try {
            if (editing) model.applyParentChain(matrices);
            capture(model, ParticleView.capture(matrices.last().pose()), editing);
        } finally { matrices.popPose(); }
    }
    /** The parent anchor excludes this model's own transforms and Minecraft's item display transform. */
    public static void capture(ItemModel model, Matrix4f parentAnchor) {
        capture(model, parentAnchor, true);
    }
    private static void capture(ItemModel model, Matrix4f parentAnchor, boolean editing) {
        Vector3f point = new Vector3f((float) model.getPivotX(),
                (float) model.getPivotY(), (float) model.getPivotZ());
        // Self rotation/shear operate around this point; self scale follows the pivot translations.
        if (editing) point.add((float) model.getTranslateX(), (float) model.getTranslateY(),
                (float) model.getTranslateZ());
        parentAnchor.transformPosition(point);
        if (point.isFinite()) PIVOTS.add(DrawParticle.pivotAt(point, model.isMainHand()));
    }
    public static List<DrawParticle> snapshot() { return List.copyOf(PIVOTS); }
}
