package omar.projects.interactivestuff.handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import omar.projects.interactivestuff.objects.InteractionMaterial;

import java.util.Objects;
import java.util.Random;

public final class InteractionHandler {

    private static final InteractionHandler INSTANCE = new InteractionHandler();
    private static final String COOLDOWN_ID = "SwingSoundCooldown";
    private static final int COOLDOWN_TICKS = 4;
    private static final Random random = new Random();

    private InteractionHandler() {
    }

    public static InteractionHandler getInstance() {
        return INSTANCE;
    }

    public void handleBlockInteraction(final MinecraftClient client, final BlockPos pos, final Hand hand) {

        if (BackgroundLoopHandler.getInstance().isLoopRunning(COOLDOWN_ID)) {
            return;
        }

        final ClientPlayerEntity player = client.player;

        if (player == null || player.isSpectator() || Objects.equals(player.getGameMode(), GameMode.ADVENTURE)) {
            return;
        }

        final ItemStack stack = player.getStackInHand(hand);
        final InteractionMaterial material = InteractionMaterial.getMaterial(stack);

        if (material == InteractionMaterial.NONE) {
            return;
        }

        if (client.world != null) {
            if (material.isExcluded(client.world.getBlockState(pos).getBlock())) {
                return;
            }
        }

        if (material.requiresBlockHit()) {
            if (client.crosshairTarget == null || (client.crosshairTarget.getType() == HitResult.Type.MISS)) {
                return;
            }
        }

        // 4. Play Sound
        final float pitch = material.getPitch(stack) == -1.0f ? random.nextFloat(0.5f, 2f) : material.getPitch(stack);
        player.playSoundToPlayer(
                material.getSound(),
                SoundCategory.BLOCKS,
                material.getVolume(),
                pitch
        );

        // 5. Start Cooldown
        BackgroundLoopHandler.getInstance().waitTicks(COOLDOWN_ID, COOLDOWN_TICKS, () -> {
        });
    }
}
