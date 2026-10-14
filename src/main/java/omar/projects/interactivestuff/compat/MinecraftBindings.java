package omar.projects.interactivestuff.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;

/** Native facts used by otherwise shared script/player and interaction code. */
public final class MinecraftBindings {
    private MinecraftBindings() {}
    public static void registerModels() {}
    public static boolean leftHand(ItemDisplayContext mode) { return mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND; }
    public static GameType gameMode(LocalPlayer player) { return player.gameMode(); }
    public static ItemStack blockingItem(LivingEntity entity) { return entity.getItemBlockingWith(); }
    public static ItemStack weaponItem(LivingEntity entity) { return entity.getWeaponItem(); }
    public static float nausea(LocalPlayer player) { return player.portalEffectIntensity; }
    public static boolean inLiquid(LocalPlayer player) { return player.isInLiquid(); }
    public static boolean limitedCrafting(LocalPlayer player) { return player.getDoLimitedCrafting(); }
    public static boolean landedInLiquid(LivingEntity entity) { return entity.hasLandedInLiquid(); }
    public static float ageScale(LivingEntity entity) { return entity.getAgeScale(); }
    public static boolean affectedByFluids(LivingEntity entity) { return entity.isAffectedByFluids(); }
    public static float luck(LivingEntity entity) { return entity.getLuck(); }
    public static float maxAbsorption(LivingEntity entity) { return entity.getMaxAbsorption(); }
    public static int haste(LocalPlayer player) { var effect=player.getEffect(MobEffects.HASTE);return effect==null?0:effect.getAmplifier()+1; }
    public static void playInteractionSound(Minecraft client, net.minecraft.sounds.SoundEvent sound, float volume, float pitch) { client.level.playPlayerSound(sound, net.minecraft.sounds.SoundSource.BLOCKS, volume, pitch); }
    public static boolean skylight(BlockState state, Level level, BlockPos pos) { return state.propagatesSkylightDown(); }
    public static boolean solidRender(BlockState state, Level level, BlockPos pos) { return state.isSolidRender(); }
}
