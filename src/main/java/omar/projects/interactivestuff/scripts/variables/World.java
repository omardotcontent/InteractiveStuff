package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;

@VynType(name = "World")
public final class World {

    private final net.minecraft.world.World sourceWorld;

    public World(final net.minecraft.world.World sourceWorld) {
        this.sourceWorld = sourceWorld;
    }

    @VynFunc
    public Block getBlock(final int x, final int y, final int z) {
        return new Block(new BlockPos(x, y, z), sourceWorld);
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
    public String getBiomeAt(final int x, final int y, final int z) {
        return sourceWorld.getBiome(new BlockPos(x, y, z)).getIdAsString();
    }

    @VynFunc
    public int getBiomeColorAt(final int x, final int y, final int z) {
        return sourceWorld.getBlockColor(new BlockPos(x, y, z));
    }

    @VynFunc
    public String getBiomeAt(final Position position) {
        return sourceWorld.getBiome(new BlockPos(position.getX(), position.getY(), position.getZ())).getIdAsString();
    }

    @VynFunc
    public int getGrassColor(final Position position) {
        return BiomeColors.getGrassColor(sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getDryFoliageColor(final Position position) {
        return BiomeColors.getDryFoliageColor(sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getFoliageColor(final Position position) {
        return BiomeColors.getFoliageColor(sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getWaterColor(final Position position) {
        return BiomeColors.getWaterColor(sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getBiomeColorAt(final Position position) {
        return sourceWorld.getBlockColor(new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public String toString() {
        return "World{dimension=" + getDimension() + ", isDay=" + isDay() + ", timeOfDay=" + getTimeOfDay() + ", time=" + getTime() + "}";
    }

}