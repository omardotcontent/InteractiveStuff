package omar.projects.interactivestuff.scripts.handlers;

import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public final class PivotDebugRenderer {

    public static final PivotDebugRenderer INSTANCE = new PivotDebugRenderer();
    private static final float MARKER_SIZE = 0.02f;

    private int colorIndex = 0;
    private long lastFrameTime = 0; // Internal tracker

    private static final int[] PIVOT_COLORS = {
            0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00,
            0xFFFF00FF, 0xFF00FFFF, 0xFFFF8000, 0xFF8000FF,
    };

    private PivotDebugRenderer() {}

    /**
     * Call this in the Mixin. It automatically resets the color cycle
     * if it's being called in a new frame.
     */
    public void submit(MatrixStack matrices, OrderedRenderCommandQueue queue) {
        // AUTOMATIC RESET LOGIC
        // Using System.currentTimeMillis() or Util.getMeasuringTimeMs()
        // If more than 10ms passed, we assume it's a new frame/render pass
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > 10) {
            this.colorIndex = 0;
        }
        lastFrameTime = currentTime;

        final int color = PIVOT_COLORS[colorIndex % PIVOT_COLORS.length];
        colorIndex++;

        // Capture Matrix State
        MatrixStack copyStack = new MatrixStack();
        copyStack.peek().getPositionMatrix().set(matrices.peek().getPositionMatrix());

        queue.submitCustom(copyStack, RenderLayer.getLines(), (entry, buffer) -> {
            this.renderSphereDirect(entry.getPositionMatrix(), buffer, color);
        });
    }

    public void renderSphereDirect(Matrix4f matrix, VertexConsumer buffer, int color) {
        final float r = ((color >> 16) & 0xFF) / 255f;
        final float g = ((color >> 8) & 0xFF) / 255f;
        final float b = (color & 0xFF) / 255f;

        final int segments = 6;
        for (int lat = 0; lat < segments; lat++) {
            float theta1 = (float) (lat * Math.PI / segments);
            float theta2 = (float) ((lat + 1) * Math.PI / segments);

            for (int lon = 0; lon < segments; lon++) {
                float phi1 = (float) (lon * 2 * Math.PI / segments);
                float phi2 = (float) ((lon + 1) * 2 * Math.PI / segments);

                addVertex(buffer, matrix, theta1, phi1, r, g, b);
                addVertex(buffer, matrix, theta1, phi2, r, g, b);
                addVertex(buffer, matrix, theta1, phi1, r, g, b);
                addVertex(buffer, matrix, theta2, phi1, r, g, b);
            }
        }
    }

    private void addVertex(VertexConsumer buffer, Matrix4f matrix, float theta, float phi, float r, float g, float b) {
        float x = MARKER_SIZE * (float)(Math.sin(theta) * Math.cos(phi));
        float y = MARKER_SIZE * (float)Math.cos(theta);
        float z = MARKER_SIZE * (float)(Math.sin(theta) * Math.sin(phi));

        // .normal(0, 1, 0) fixes the "Missing elements in vertex: Normal" crash
        buffer.vertex(matrix, x, y, z)
                .color(r, g, b, 1.0f)
                .normal(0, 1, 0);
    }
}