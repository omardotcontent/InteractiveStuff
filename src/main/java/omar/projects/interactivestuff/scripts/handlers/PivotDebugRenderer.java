package omar.projects.interactivestuff.scripts.handlers;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PivotDebugRenderer {

    public static final PivotDebugRenderer INSTANCE = new PivotDebugRenderer();

    private static final float MARKER_SIZE = 0.02f;

    private static final int[] PIVOT_COLORS = {
            0xFFFF0000,
            0xFF00FF00,
            0xFF0000FF,
            0xFFFFFF00,
            0xFFFF00FF,
            0xFF00FFFF,
            0xFFFF8000,
            0xFF8000FF,
    };

    private static final RenderLayer DEBUG_SPHERE_LAYER = RenderLayer.of(
            "pivot_debug_sphere",
            1536,
            false,
            true,
            RenderPipelines.DEBUG_QUADS,
            RenderLayer.MultiPhaseParameters.builder()
                    .build(false)
    );

    private final List<PivotPoint> pivotPoints = new CopyOnWriteArrayList<>();
    private int colorIndex = 0;

    private PivotDebugRenderer() {}

    public void addPivotPoint(final double x, final double y, final double z, final double transX, final double transY, final double transZ) {
        final int color = PIVOT_COLORS[colorIndex % PIVOT_COLORS.length];
        colorIndex++;
        pivotPoints.add(new PivotPoint(
                (float) (x + transX),
                (float) (y + transY),
                (float) (z + transZ),
                color
        ));
    }

    public void render(final MatrixStack matrices) {
        if (pivotPoints.isEmpty()) {
            return;
        }

        final List<PivotPoint> pointsToRender = new ArrayList<>(pivotPoints);
        pivotPoints.clear();
        colorIndex = 0;

        for (final PivotPoint point : pointsToRender) {
            matrices.push();
            matrices.translate(point.x, point.y, point.z);

            renderSphere(matrices, point.color);

            matrices.pop();
        }
    }

    private void renderSphere(final MatrixStack matrices, final int color) {
        final float red = ((color >> 16) & 0xFF) / 255f;
        final float green = ((color >> 8) & 0xFF) / 255f;
        final float blue = (color & 0xFF) / 255f;
        final float alpha = 0.8f;

        final Matrix4f matrix = matrices.peek().getPositionMatrix();
        final float radius = MARKER_SIZE;

        final int latitudes = 8;
        final int longitudes = 8;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (int lat = 0; lat < latitudes; lat++) {
            final float theta1 = (float) (lat * Math.PI / latitudes);
            final float theta2 = (float) ((lat + 1) * Math.PI / latitudes);

            for (int lon = 0; lon < longitudes; lon++) {
                final float phi1 = (float) (lon * 2 * Math.PI / longitudes);
                final float phi2 = (float) ((lon + 1) * 2 * Math.PI / longitudes);

                final float x1 = radius * MathHelper.sin(theta1) * MathHelper.cos(phi1);
                final float y1 = radius * MathHelper.cos(theta1);
                final float z1 = radius * MathHelper.sin(theta1) * MathHelper.sin(phi1);

                final float x2 = radius * MathHelper.sin(theta1) * MathHelper.cos(phi2);
                final float y2 = radius * MathHelper.cos(theta1);
                final float z2 = radius * MathHelper.sin(theta1) * MathHelper.sin(phi2);

                final float x3 = radius * MathHelper.sin(theta2) * MathHelper.cos(phi2);
                final float y3 = radius * MathHelper.cos(theta2);
                final float z3 = radius * MathHelper.sin(theta2) * MathHelper.sin(phi2);

                final float x4 = radius * MathHelper.sin(theta2) * MathHelper.cos(phi1);
                final float y4 = radius * MathHelper.cos(theta2);
                final float z4 = radius * MathHelper.sin(theta2) * MathHelper.sin(phi1);

                buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha);
                buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha);
                buffer.vertex(matrix, x3, y3, z3).color(red, green, blue, alpha);
                buffer.vertex(matrix, x4, y4, z4).color(red, green, blue, alpha);
            }
        }

        DEBUG_SPHERE_LAYER.draw(buffer.end());
    }

    private record PivotPoint(float x, float y, float z, int color) {}
}