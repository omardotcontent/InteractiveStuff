package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

@VynType(name = "World")
public final class World {

    private net.minecraft.world.World sourceWorld;

    public World(final net.minecraft.world.World sourceWorld) {
        this.sourceWorld = sourceWorld;
    }

    @VynFunc
    public Block getBlock(final int x, final int y, final int z) {
        return new Block(new net.minecraft.util.math.BlockPos(x, y, z), sourceWorld);
    }

    @VynFunc
    public String getDimension() {
        return sourceWorld.getRegistryKey().getValue().toString();
    }
    @VynFunc
    public boolean isDay() {
        return sourceWorld.isDay();
    }

    @VynFunc
    public long getTimeOfDay() {
        return sourceWorld.getTimeOfDay();
    }

    @VynFunc
    public long getTime() {
        return sourceWorld.getTime();
    }

    @VynFunc
    public long calculateDistanceBetweenPositions(final Position pos1, final Position pos2) {
        return pos1.getDistanceTo(pos2);
    }

    @VynFunc
    public String getBiomeAt(int x, int y, int z) {
        return sourceWorld.getBiome(new net.minecraft.util.math.BlockPos(x, y, z)).getIdAsString();
    }

    @VynFunc
    public String toString() {
        return "World{dimension=" + getDimension() + ", isDay=" + isDay() + ", timeOfDay=" + getTimeOfDay() + ", time=" + getTime() + "}";
    }

}