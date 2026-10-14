package omar.projects.interactivestuff.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.objects.InteractionMaterial;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;

import java.util.Random;

public final class InteractionHandler {

    private static final InteractionHandler INSTANCE = new InteractionHandler();
    private static final String COOLDOWN_ID = "SwingSoundCooldown";
    private static final Random RANDOM = new Random();

    private InteractionHandler() {
    }

    public static InteractionHandler getInstance() {
        return INSTANCE;
    }

    public void handleBlockInteraction(final Minecraft client, final InteractionHand hand) {
        ScriptInterpreter.onSwingHand();
        if (BackgroundLoopHandler.getInstance().isLoopRunning(COOLDOWN_ID)) {
            return;
        }

        final LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        final int haste = omar.projects.interactivestuff.compat.MinecraftBindings.haste(player);
        final MobEffectInstance conduit = player.getEffect(MobEffects.CONDUIT_POWER);
        final int amplifier = haste + (conduit != null ? conduit.getAmplifier() + 1 : 0);

        BackgroundLoopHandler.getInstance().waitTicks(COOLDOWN_ID, Math.max(0, (ConfigHandler.INSTANCE.HitCooldownTicks) - amplifier), () -> {});

        final ConfigHandler config = ConfigHandler.INSTANCE;
        if (!config.enableInteractiveHits) {
            return;
        }

        if (player.isSpectator() || omar.projects.interactivestuff.compat.MinecraftBindings.gameMode(player) == GameType.ADVENTURE) {
            return;
        }

        final ItemStack stack = player.getItemInHand(hand);
        final InteractionMaterial material = config.getMaterial(stack.getItem());
        if (material == null) {
            return;
        }

        if (client.level == null) {
            return;
        }

        boolean blockHit = client.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK;
        if (material.requiresBlockHit && !blockHit) return;
        if (blockHit && config.isExcluded(client.level.getBlockState(((BlockHitResult) client.hitResult).getBlockPos()).getBlock())) return;

        final float basePitch = material.getPitch(stack);
        final float pitch = material.randomPitch ? basePitch + (RANDOM.nextFloat() * 1.5F) : basePitch;

        omar.projects.interactivestuff.compat.MinecraftBindings.playInteractionSound(client, material.getSoundEvent(), material.volume, pitch);
    }

}