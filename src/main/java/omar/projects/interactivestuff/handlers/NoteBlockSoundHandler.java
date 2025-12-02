package omar.projects.interactivestuff.handlers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;

public class NoteBlockSoundHandler {

    private static boolean wasSneaking = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(NoteBlockSoundHandler::tick);
    }

    private static void tick(final MinecraftClient client) {
        final PlayerEntity player = client.player;

        if (player == null || client.world == null) {
            return;
        }


        final boolean isSneaking = player.isSneaking();
        final boolean isStandingOnRedstoneBlock = client.world.getBlockState(player.getSteppingPos()).isOf(Blocks.REDSTONE_BLOCK);

        if (isStandingOnRedstoneBlock && isHoldingNoteBlock(player)) {

            if (isSneaking && !wasSneaking) {
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(), 0.5f, 1.0f);
            }
        }


        wasSneaking = isSneaking;
    }

    private static boolean isHoldingNoteBlock(final PlayerEntity player) {
        return isNoteBlock(player.getMainHandStack()) || isNoteBlock(player.getOffHandStack());
    }

    private static boolean isNoteBlock(final ItemStack stack) {
        return stack.isOf(Items.NOTE_BLOCK);
    }
}