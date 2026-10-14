package omar.projects.interactivestuff.objects;



import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class InteractionMaterial {
    public String name;
    public List<Item> items;
    public String sound;
    public float pitch;
    public float volume;
    public boolean requiresBlockHit;
    public boolean randomPitch;
    public List<PitchEntry> customPitches;

    public InteractionMaterial(final String name) {
        this.name = name;
        this.items = new ArrayList<>();
        this.customPitches = new ArrayList<>();
        this.sound = "minecraft:block.stone.break";
        this.pitch = 1.0f;
        this.volume = 1.0f;
        this.requiresBlockHit = true;
        this.randomPitch = false;
        this.items.add(Items.STONE);
    }

    public SoundEvent getSoundEvent() {
        Identifier id = sound == null ? null : Identifier.tryParse(sound);
        if (id == null) return net.minecraft.sounds.SoundEvents.STONE_BREAK;
        return java.util.Optional.ofNullable(omar.projects.interactivestuff.compat.MinecraftRegistries.sound(id)).orElse(net.minecraft.sounds.SoundEvents.STONE_BREAK);
    }

    public float getPitch(final ItemStack stack) {
        if (customPitches != null) {
            for (final PitchEntry entry : customPitches) {
                if (entry != null && entry.item == stack.getItem()) {
                    return entry.pitch;
                }
            }
        }
        return pitch;
    }

    public static final class PitchEntry {
        public Item item;
        public float pitch;

        public PitchEntry(final Item item, final float pitch) {
            this.item = item;
            this.pitch = pitch;
        }
    }
}