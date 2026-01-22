package omar.projects.interactivestuff.handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.objects.InteractionMaterial;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;

import java.util.Random;

public final class InteractionHandler {

    private static final InteractionHandler INSTANCE = new InteractionHandler();
    private static final String COOLDOWN_ID = "SwingSoundCooldown";
    private static final Random RANDOM = new Random();

    private InteractionHandler() {}

    public static InteractionHandler getInstance() {
        return INSTANCE;
    }

    public void handleBlockInteraction(final MinecraftClient client, final Hand hand) {
        if (BackgroundLoopHandler.getInstance().isLoopRunning(COOLDOWN_ID)) {
            return;
        }

        final ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        final StatusEffectInstance haste = player.getStatusEffect(StatusEffects.HASTE);
        final StatusEffectInstance conduit = player.getStatusEffect(StatusEffects.CONDUIT_POWER);
        final int amplifier = (haste != null ? haste.getAmplifier() + 1 : 0) + (conduit != null ? conduit.getAmplifier() + 1 : 0);

        BackgroundLoopHandler.getInstance().waitTicks(COOLDOWN_ID, Math.max(0, (ConfigHandler.INSTANCE.HitCooldownTicks) - amplifier), () -> {});

        final ConfigHandler config = ConfigHandler.INSTANCE;
        ScriptInterpreter.onSwingHand();

        if (!config.enableInteractiveHits) {
            return;
        }

        if (player.isSpectator() || player.getGameMode() == GameMode.ADVENTURE) {
            return;
        }

        final ItemStack stack = player.getStackInHand(hand);
        final InteractionMaterial material = config.getMaterial(stack.getItem());
        if (material == null) {
            return;
        }

        if (client.world == null) {
            return;
        }

        try {
            final BlockHitResult blockHit = (BlockHitResult) client.crosshairTarget;
            if (blockHit == null) {
                return;
            }
            if (config.isExcluded(client.world.getBlockState(blockHit.getBlockPos()).getBlock())) {
                return;
            }
        } catch (final Exception ignored) {
            return;
        }

        if (material.requiresBlockHit) {
            if (client.crosshairTarget == null || client.crosshairTarget.getType() == HitResult.Type.MISS) {
                return;
            }
        }

        final float basePitch = material.getPitch(stack);
        final float pitch = material.randomPitch ? basePitch + (RANDOM.nextFloat() * 1.5F) : basePitch;

        client.world.playSoundClient(material.getSoundEvent(), SoundCategory.BLOCKS, material.volume, pitch);
    }

}