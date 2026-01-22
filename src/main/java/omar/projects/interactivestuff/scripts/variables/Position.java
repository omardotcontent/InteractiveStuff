package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;

@VynType(name = "Position")
public final class Position {

    private int x;
    private int y;
    private int z;

    @VynConstructor
    public Position(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @VynFunc
    public long getDistanceTo(final Position other) {
        final int dx = other.x - this.x;
        final int dy = other.y - this.y;
        final int dz = other.z - this.z;
        return (long) Math.sqrt((double) dx * dx + (double) dy * dy + (double) dz * dz);
    }

    @VynFunc
    public long getDistanceTo(final int x, final int y, final int z) {
        final int dx = x - this.x;
        final int dy = y - this.y;
        final int dz = z - this.z;
        return (long) Math.sqrt((double) dx * dx + (double) dy * dy + (double) dz * dz);
    }

    @VynFunc
    public int getX() {
        return x;
    }

    @VynFunc
    public int getY() {
        return y;
    }

    @VynFunc
    public int getZ() {
        return z;
    }

    @VynFunc
    public void setX(final int x) {
        this.x = x;
    }

    @VynFunc
    public void setY(final int y) {
        this.y = y;
    }

    @VynFunc
    public void setZ(final int z) {
        this.z = z;
    }

    @VynFunc
    public String toString() {
        return "Position{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

}
