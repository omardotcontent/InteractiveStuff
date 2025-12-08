package omar.projects.interactivestuff.handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.objects.InteractionMaterial;

import java.util.Random;

public final class InteractionHandler {

    private static final InteractionHandler INSTANCE = new InteractionHandler();
    private static final String COOLDOWN_ID = "SwingSoundCooldown";
    private static final Random random = new Random();
    private InteractionHandler() {
    }

    public static InteractionHandler getInstance() {
        return INSTANCE;
    }



    public void handleBlockInteraction(final MinecraftClient client, final BlockPos pos, final Hand hand) {
        final ConfigHandler config = ConfigHandler.INSTANCE;
        if (!config.enableInteractiveHits) {
            return;
        }

        if (BackgroundLoopHandler.getInstance().isLoopRunning(COOLDOWN_ID)) {
            return;
        }

        final ClientPlayerEntity player = client.player;

        if (player == null || player.isSpectator() || player.getGameMode() == GameMode.ADVENTURE) {
            return;
        }

        final ItemStack stack = player.getStackInHand(hand);

        final InteractionMaterial material = config.getMaterial(stack.getItem());

        if (material == null) {
            return;
        }

        if (client.world != null) {
            if (config.isExcluded(client.world.getBlockState(pos).getBlock())) {
                return;
            }
        }

        if (material.requiresBlockHit) {
            if (client.crosshairTarget == null || client.crosshairTarget.getType() == HitResult.Type.MISS) {
                return;
            }
        }

        final float basePitch = material.getPitch(stack);
        // Fix: Random does not have nextFloat(min, max). Used (min + random * range)
        final float pitch = material.randomPitch ? basePitch + (random.nextFloat() * 1.5F) : basePitch;

        // Fix: Use client.world.playSound to play sound client-side with a category
        client.player.playSoundToPlayer(
                material.getSoundEvent(),
                SoundCategory.BLOCKS,
                material.volume,
                pitch
        );

        final StatusEffectInstance haste = player.getStatusEffect(StatusEffects.HASTE);
        final StatusEffectInstance conduit = player.getStatusEffect(StatusEffects.CONDUIT_POWER);
        final int amplifier = (haste != null ? haste.getAmplifier() + 1 : 0) + (conduit != null ? conduit.getAmplifier() + 1 : 0);

        BackgroundLoopHandler.getInstance().waitTicks(COOLDOWN_ID, Math.max(0, (ConfigHandler.INSTANCE.HitCooldownTicks) - amplifier), () -> {
        });
    }
}