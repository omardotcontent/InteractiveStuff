package omar.projects.interactivestuff.scripts.variables;


import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.util.math.MatrixStack;

@VynType(name = "Matrices")
public final class Matrices {

    private final MatrixStack matrices;

    public Matrices(final MatrixStack matrices) {
        this.matrices = matrices;
    }

    @VynFunc
    public void translate(final double x, final double y, final double z) {
        matrices.translate(
                x,
                y,
                z
        );
    }

    @VynFunc
    public void scale(final float x, final float y, final float z) {
        matrices.scale(
                x,
                y,
                z
        );
    }
}