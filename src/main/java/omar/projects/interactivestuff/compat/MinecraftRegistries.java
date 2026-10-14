package omar.projects.interactivestuff.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.sounds.SoundEvent;

/** Registry API differences must not leak into script bindings or config persistence. */
public final class MinecraftRegistries {
    private MinecraftRegistries() {}
    public static Item item(Identifier id) { return BuiltInRegistries.ITEM.get(id).map(Holder.Reference::value).orElse(null); }
    public static Block block(Identifier id) { return BuiltInRegistries.BLOCK.get(id).map(Holder.Reference::value).orElse(null); }
    public static SoundEvent sound(Identifier id) { return BuiltInRegistries.SOUND_EVENT.get(id).map(Holder.Reference::value).orElse(null); }
    public static String holderName(Holder<?> holder) { return holder.unwrapKey().map(key -> key.identifier().toString()).orElse(""); }
}
