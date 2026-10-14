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

public final class MinecraftBindings {
    private MinecraftBindings() {}
    public static void registerModels() { omar.projects.interactivestuff.render.LegacyModels.register(); }
    public static boolean leftHand(ItemDisplayContext mode) { return mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND; }
    public static GameType gameMode(LocalPlayer player) { var mode=Minecraft.getInstance().gameMode;return mode==null?null:mode.getPlayerMode(); }
    public static ItemStack blockingItem(LivingEntity entity) { return entity.isBlocking()?entity.getUseItem():null; }
    public static ItemStack weaponItem(LivingEntity entity) { return entity.getMainHandItem(); }
    public static float nausea(LocalPlayer player) { return player.spinningEffectIntensity; }
    public static boolean inLiquid(LocalPlayer player) { return player.isInWaterOrBubble() || player.isInLava(); }
    public static boolean limitedCrafting(LocalPlayer player) { return player.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_LIMITED_CRAFTING); }
    public static boolean landedInLiquid(LivingEntity entity) { return entity.isInWaterOrBubble() || entity.isInLava(); }
    public static float ageScale(LivingEntity entity) { return entity.getAgeScale(); }
    public static boolean affectedByFluids(LivingEntity entity) { return ((omar.projects.interactivestuff.mixin.player.LivingEntityAccessor) entity).interactivestuff$affectedByFluids(); }
    public static float luck(LivingEntity entity) { var attribute=entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.LUCK);return attribute==null?0:(float)attribute.getValue(); }
    public static float maxAbsorption(LivingEntity entity) { return entity.getMaxAbsorption(); }
    public static int haste(LocalPlayer player) { var effect=player.getEffect(MobEffects.DIG_SPEED);return effect==null?0:effect.getAmplifier()+1; }
    public static void playInteractionSound(Minecraft client, net.minecraft.sounds.SoundEvent sound, float volume, float pitch) { var p=client.player;client.level.playLocalSound(p.getX(),p.getY(),p.getZ(),sound,net.minecraft.sounds.SoundSource.BLOCKS,volume,pitch,false); }
    public static boolean skylight(BlockState state, Level level, BlockPos pos) { return state.propagatesSkylightDown(level,pos); }
    public static boolean solidRender(BlockState state, Level level, BlockPos pos) { return state.isSolidRender(level,pos); }
}
