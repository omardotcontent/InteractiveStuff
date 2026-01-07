package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

@VynType(name = "Block")
public final class Block {

    private final BlockState blockState;
    private final BlockPos sourceBlock;
    private final World world;
    private final Position position;

    public Block(final BlockPos sourceBlock, final World world) {
        this.sourceBlock = sourceBlock;
        this.world = world;
        blockState = world.getBlockState(sourceBlock);
        this.position = new Position(sourceBlock.getX(), sourceBlock.getY(), sourceBlock.getZ());
    }

    @VynFunc
    public String getName() {
        return blockState.getBlock().asItem().toString();
    }

    @VynFunc
    public boolean hasBlockTag(final String tagID) {
        return blockState.getBlock()
                .getRegistryEntry()
                .streamTags()
                .map(tag -> tag.id().toString())
                .anyMatch(id -> id.toLowerCase(Locale.ROOT).contains(tagID));
    }

    @VynFunc
    public Position getPosition() {
        return position;
    }


    @VynFunc
    public String getInstrument() {
        return blockState.getInstrument().getSound().getIdAsString();
    }

    @VynFunc
    public boolean isSolid() {
        return blockState.isSolidBlock(world, sourceBlock);
    }

    @VynFunc
    public boolean isAir() {
        return blockState.isAir();
    }

    @VynFunc
    public boolean isBurnable() {
        return blockState.isBurnable();
    }

    @VynFunc
    public boolean isTransparent() {
        return blockState.isTransparent();
    }

    @VynFunc
    public boolean isOpaque() {
        return blockState.isOpaque();
    }

    @VynFunc
    public boolean isOpaqueFullCube() {
        return blockState.isOpaqueFullCube();
    }

    @VynFunc
    public String toString() {
        return blockState.toString();
    }

}