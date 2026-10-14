package omar.projects.interactivestuff.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundEvent;

public final class MinecraftRegistries {
    private MinecraftRegistries() {}
    public static Item item(ResourceLocation id) { return BuiltInRegistries.ITEM.containsKey(id) ? BuiltInRegistries.ITEM.get(id) : null; }
    public static Block block(ResourceLocation id) { return BuiltInRegistries.BLOCK.containsKey(id) ? BuiltInRegistries.BLOCK.get(id) : null; }
    public static SoundEvent sound(ResourceLocation id) { return BuiltInRegistries.SOUND_EVENT.containsKey(id) ? BuiltInRegistries.SOUND_EVENT.get(id) : null; }
    public static String holderName(Holder<?> holder) { return holder.unwrapKey().map(key -> key.location().toString()).orElse(""); }
}
