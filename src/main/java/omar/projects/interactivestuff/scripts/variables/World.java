package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@VynType(name = "World")
public final class World {

    private Level sourceWorld;

    public World(final Level sourceWorld) {
        this.sourceWorld = sourceWorld;
    }

    public void setWorld(Level world) { sourceWorld = world; }

    @VynFunc
    public Block getBlock(final int x, final int y, final int z) {
        return sourceWorld == null ? null : new Block(new BlockPos(x, y, z), sourceWorld);
    }

    @VynFunc
    public String getDimension() {
        return sourceWorld == null ? "" : sourceWorld.dimension().identifier().toString();
    }

    @VynFunc
    public boolean isDay() {
        return sourceWorld != null && sourceWorld.isBrightOutside();
    }

    @VynFunc
    public long getTimeOfDay() {
        return sourceWorld == null ? 0 : sourceWorld.getDefaultClockTime();
    }

    @VynFunc
    public long getTime() {
        return sourceWorld == null ? 0 : sourceWorld.getGameTime();
    }

    @VynFunc
    public long calculateDistanceBetweenPositions(final Position pos1, final Position pos2) {
        return pos1.getDistanceTo(pos2);
    }

    @VynFunc
    public String getBiomeAt(final int x, final int y, final int z) {
        return sourceWorld == null ? "" : sourceWorld.getBiome(new BlockPos(x, y, z)).getRegisteredName();
    }

    @VynFunc
    public int getBiomeColorAt(final int x, final int y, final int z) {
        return sourceWorld == null ? 0xFFFFFF : sourceWorld.getClientLeafTintColor(new BlockPos(x, y, z));
    }

    @VynFunc
    public String getBiomeAt(final Position position) {
        return sourceWorld == null ? "" : sourceWorld.getBiome(new BlockPos(position.getX(), position.getY(), position.getZ())).getRegisteredName();
    }

    @VynFunc
    public int getGrassColor(final Position position) {
        return sourceWorld == null ? 0xFFFFFF : BiomeColors.getAverageGrassColor((BlockAndTintGetter) sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getDryFoliageColor(final Position position) {
        return sourceWorld == null ? 0xFFFFFF : BiomeColors.getAverageDryFoliageColor((BlockAndTintGetter) sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getFoliageColor(final Position position) {
        return sourceWorld == null ? 0xFFFFFF : BiomeColors.getAverageFoliageColor((BlockAndTintGetter) sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getWaterColor(final Position position) {
        return sourceWorld == null ? 0xFFFFFF : BiomeColors.getAverageWaterColor((BlockAndTintGetter) sourceWorld, new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public int getBiomeColorAt(final Position position) {
        return sourceWorld == null ? 0xFFFFFF : sourceWorld.getClientLeafTintColor(new BlockPos(position.getX(), position.getY(), position.getZ()));
    }

    @VynFunc
    public String toString() {
        return "World{dimension=" + getDimension() + ", isDay=" + isDay() + ", timeOfDay=" + getTimeOfDay() + ", time=" + getTime() + "}";
    }

}