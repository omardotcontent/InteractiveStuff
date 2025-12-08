package omar.projects.interactivestuff.objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

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
        return Registries.SOUND_EVENT.get(Identifier.tryParse(sound));
    }

    public float getPitch(final ItemStack stack) {
        if (customPitches != null) {
            for (final PitchEntry entry : customPitches) {
                if (entry.item.equals(stack.getItem())) {
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