package omar.projects.interactivestuff.handlers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;

public final class CampAndTorchHandler {

    private static boolean wasSubmerged = false;

    private CampAndTorchHandler() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(CampAndTorchHandler::tick);
    }

    static void tick(final MinecraftClient client) {
        final PlayerEntity player = client.player;
        if (player == null || client.world == null || player.isSpectator()) {
            return;
        }

        final boolean isSubmerged = player.isSubmergedInWater();
        if (isSubmerged == wasSubmerged) {
            return;
        }

        wasSubmerged = isSubmerged;

        if (!isHoldingCampfireOrTorch(player)) {
            return;
        }

        if (isSubmerged) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.6f, 1.0f);
            return;
        }

        player.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 0.6f, 1.0f);
    }

    private static boolean isHoldingCampfireOrTorch(final PlayerEntity player) {
        return isCampfireOrTorch(player.getMainHandStack()) || isCampfireOrTorch(player.getOffHandStack());
    }

    private static boolean isCampfireOrTorch(final ItemStack stack) {
        return stack.isOf(Items.CAMPFIRE) || stack.isOf(Items.SOUL_CAMPFIRE) ||
               stack.isOf(Items.TORCH) || stack.isOf(Items.SOUL_TORCH) ||
               stack.isOf(Items.COPPER_TORCH) || stack.isOf(Items.REDSTONE_TORCH);
    }
}