package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

import java.util.Locale;

@VynType(name = "Block")
public final class Block {

    private final BlockState blockState;
    private final BlockPos sourceBlock;
    private final Level world;
    private Position position;

    public Block(final BlockPos sourceBlock, final Level world) {
        this.sourceBlock = sourceBlock;
        this.world = world;
        this.blockState = world.getBlockState(sourceBlock);
    }

    @VynFunc
    public String getName() {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
    }

    @VynFunc
    public boolean hasBlockTag(final String tagID) {
        return blockState.getBlock().builtInRegistryHolder()
                .tags()
                .map(tag -> tag.location().toString())
                .anyMatch(id -> id.toLowerCase(Locale.ROOT).contains(tagID.toLowerCase(Locale.ROOT)));
    }

    @VynFunc
    public Position getPosition() {
        if (position != null) {
            return position;
        }
        position = new Position(sourceBlock.getX(), sourceBlock.getY(), sourceBlock.getZ());
        return position;
    }

    @VynFunc
    public int getBlockLightLevel() {
        return world.getBrightness(LightLayer.BLOCK, sourceBlock);
    }

    @VynFunc
    public int getSkyLightLevel() {
        return world.getBrightness(LightLayer.SKY, sourceBlock);
    }

    @VynFunc
    public String getInstrument() {
        return blockState.instrument().getSoundEvent().getRegisteredName();
    }

    @VynFunc
    public boolean isSolid() {
        return blockState.isRedstoneConductor(world, sourceBlock);
    }

    @VynFunc
    public boolean isAir() {
        return blockState.isAir();
    }

    @VynFunc
    public boolean isBurnable() {
        return blockState.ignitedByLava();
    }

    @VynFunc
    public boolean isTransparent() {
        return blockState.propagatesSkylightDown();
    }

    @VynFunc
    public boolean isOpaque() {
        return blockState.canOcclude();
    }

    @VynFunc
    public boolean isOpaqueFullCube() {
        return blockState.isSolidRender();
    }

    @VynFunc
    public String toString() {
        return blockState.toString();
    }

}