package omar.projects.interactivestuff.objects;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public enum InteractionMaterial {

    BELL(
            Set.of(Items.BELL),
            SoundEvents.BLOCK_BELL_USE,
            1.0f,
            1.0f,
            false
    ),

    AMETHYST(
            Set.of(
                    Items.AMETHYST_BLOCK, Items.BUDDING_AMETHYST, Items.AMETHYST_CLUSTER,
                    Items.LARGE_AMETHYST_BUD, Items.MEDIUM_AMETHYST_BUD,
                    Items.SMALL_AMETHYST_BUD, Items.AMETHYST_SHARD
            ),
            SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP,
            1.0f,
            0.5f,
            true,
            Map.of(
                    Items.AMETHYST_BLOCK, 0.6f,
                    Items.BUDDING_AMETHYST, 0.6f,
                    Items.AMETHYST_CLUSTER, 0.8f,
                    Items.LARGE_AMETHYST_BUD, 1.0f,
                    Items.MEDIUM_AMETHYST_BUD, 1.2f,
                    Items.SMALL_AMETHYST_BUD, 1.4f,
                    Items.AMETHYST_SHARD, 1.6f
            )
    ),

    SCAFFOLD(
            Set.of(
                    Items.SCAFFOLDING
            ),
            SoundEvents.BLOCK_SCAFFOLDING_BREAK,
            1.6f,
            0.25f,
            true
    ),

    COPPER(
            Set.of(
                    Items.COPPER_BLOCK, Items.WAXED_COPPER_BLOCK, Items.VAULT,

                    Items.CHISELED_COPPER,
                    Items.WAXED_CHISELED_COPPER, Items.EXPOSED_CHISELED_COPPER, Items.WEATHERED_CHISELED_COPPER, Items.OXIDIZED_CHISELED_COPPER,
                    Items.WAXED_EXPOSED_CHISELED_COPPER, Items.WAXED_WEATHERED_CHISELED_COPPER, Items.WAXED_OXIDIZED_CHISELED_COPPER,
                    Items.CUT_COPPER, Items.WAXED_CUT_COPPER, Items.EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_CUT_COPPER,
                    Items.WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_CUT_COPPER, Items.OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER,

                    Items.COPPER_GRATE, Items.EXPOSED_COPPER_GRATE, Items.WEATHERED_COPPER_GRATE, Items.OXIDIZED_COPPER_GRATE,
                    Items.WAXED_COPPER_GRATE, Items.WAXED_EXPOSED_COPPER_GRATE, Items.WAXED_WEATHERED_COPPER_GRATE, Items.WAXED_OXIDIZED_COPPER_GRATE,

                    Items.COPPER_DOOR, Items.EXPOSED_COPPER_DOOR, Items.WEATHERED_COPPER_DOOR, Items.OXIDIZED_COPPER_DOOR,
                    Items.WAXED_COPPER_DOOR, Items.WAXED_EXPOSED_COPPER_DOOR, Items.WAXED_WEATHERED_COPPER_DOOR, Items.WAXED_OXIDIZED_COPPER_DOOR,

                    Items.COPPER_TRAPDOOR, Items.EXPOSED_COPPER_TRAPDOOR, Items.WEATHERED_COPPER_TRAPDOOR, Items.OXIDIZED_COPPER_TRAPDOOR,
                    Items.WAXED_COPPER_TRAPDOOR, Items.WAXED_EXPOSED_COPPER_TRAPDOOR, Items.WAXED_WEATHERED_COPPER_TRAPDOOR, Items.WAXED_OXIDIZED_COPPER_TRAPDOOR,

                    Items.LIGHTNING_ROD, Items.EXPOSED_LIGHTNING_ROD, Items.WEATHERED_LIGHTNING_ROD, Items.OXIDIZED_LIGHTNING_ROD,
                    Items.WAXED_LIGHTNING_ROD, Items.WAXED_EXPOSED_LIGHTNING_ROD, Items.WAXED_WEATHERED_LIGHTNING_ROD, Items.WAXED_OXIDIZED_LIGHTNING_ROD,

                    Items.COPPER_BARS.exposed(), Items.COPPER_BARS.weathered(), Items.COPPER_BARS.oxidized(),
                    Items.COPPER_BARS.waxedExposed(), Items.COPPER_BARS.waxedWeathered(), Items.COPPER_BARS.waxedOxidized(), Items.COPPER_BARS.unaffected(), Items.COPPER_BARS.waxed(),

                    Items.COPPER_CHAINS.oxidized(), Items.COPPER_CHAINS.waxedOxidized(), Items.COPPER_CHAINS.weathered(), Items.COPPER_CHAINS.waxedWeathered(), Items.COPPER_CHAINS.exposed(), Items.COPPER_CHAINS.waxedExposed()
                    , Items.COPPER_CHAINS.unaffected(), Items.COPPER_CHAINS.waxed()
            ),
            SoundEvents.BLOCK_COPPER_BREAK,
            1.5f,
            0.25f,
            true
    ),

    LANTERN(
            Set.of(
                    Items.LANTERN, Items.SOUL_LANTERN,
                    Items.COPPER_LANTERNS.unaffected(), Items.COPPER_LANTERNS.exposed(),
                    Items.COPPER_LANTERNS.weathered(), Items.COPPER_LANTERNS.oxidized(),

                    Items.COPPER_LANTERNS.waxed(), Items.COPPER_LANTERNS.waxedExposed(),
                    Items.COPPER_LANTERNS.waxedWeathered(), Items.COPPER_LANTERNS.waxedOxidized()
            ),
            SoundEvents.BLOCK_LANTERN_BREAK,
            1.0f,
            0.2f,
            true
    ),

    DIRT(
            Set.of(
                    Items.DIRT, Items.COARSE_DIRT, Items.PODZOL, Items.ROOTED_DIRT,
                    Items.CLAY_BALL, Items.CLAY
            ),
            SoundEvents.BLOCK_GRAVEL_BREAK,
            1.0f,
            0.4f,
            true
    ),

    GRASS(
            Set.of(
                    Items.OAK_LEAVES, Items.SPRUCE_LEAVES, Items.BIRCH_LEAVES, Items.JUNGLE_LEAVES,
                    Items.ACACIA_LEAVES, Items.DARK_OAK_LEAVES, Items.MANGROVE_LEAVES, Items.CHERRY_LEAVES,
                    Items.AZALEA_LEAVES, Items.FLOWERING_AZALEA_LEAVES, Items.PALE_OAK_LEAVES,

                    Items.GRASS_BLOCK, Items.DIRT_PATH,
                    Items.DRIED_KELP_BLOCK, Items.HAY_BLOCK,
                    Items.MOSS_BLOCK, Items.MOSS_CARPET, Items.PALE_MOSS_BLOCK, Items.PALE_MOSS_CARPET,

                    Items.SPRUCE_SAPLING, Items.BIRCH_SAPLING, Items.JUNGLE_SAPLING, Items.ACACIA_SAPLING,
                    Items.DARK_OAK_SAPLING, Items.MANGROVE_PROPAGULE, Items.CHERRY_SAPLING, Items.OAK_SAPLING,
                    Items.PALE_OAK_SAPLING, Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.DANDELION,
                    Items.POPPY, Items.BLUE_ORCHID, Items.ALLIUM, Items.AZURE_BLUET, Items.RED_TULIP, Items.ORANGE_TULIP,
                    Items.WHITE_TULIP, Items.PINK_TULIP, Items.OXEYE_DAISY, Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY,
                    Items.WITHER_ROSE, Items.SUNFLOWER, Items.LILAC, Items.PEONY, Items.TORCHFLOWER, Items.PITCHER_PLANT,
                    Items.CACTUS_FLOWER, Items.SPORE_BLOSSOM, Items.CLOSED_EYEBLOSSOM, Items.OPEN_EYEBLOSSOM, Items.PINK_PETALS,
                    Items.WILDFLOWERS, Items.BIG_DRIPLEAF, Items.SMALL_DRIPLEAF, Items.LEAF_LITTER, Items.TALL_DRY_GRASS,
                    Items.VINE, Items.GLOW_LICHEN, Items.SEAGRASS, Items.KELP, Items.TALL_GRASS, Items.FERN, Items.LARGE_FERN,
                    Items.BUSH, Items.ROSE_BUSH, Items.FIREFLY_BUSH, Items.HANGING_ROOTS, Items.PITCHER_POD, Items.PALE_HANGING_MOSS,

                    Items.MANGROVE_ROOTS, Items.MUDDY_MANGROVE_ROOTS,



                    Items.TARGET, Items.TNT
            ),
            SoundEvents.BLOCK_GRASS_BREAK,
            2f,
            0.4f,
            true
    ),

    WOOD(
            Set.of(
                    Items.COMPOSTER, Items.END_ROD, Items.LADDER,

                    Items.BLACK_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER,
                    Items.CYAN_BANNER, Items.GRAY_BANNER, Items.GREEN_BANNER, Items.LIGHT_BLUE_BANNER, Items.LIGHT_GRAY_BANNER,
                    Items.LIME_BANNER, Items.MAGENTA_BANNER, Items.ORANGE_BANNER, Items.PINK_BANNER, Items.PURPLE_BANNER,
                    Items.RED_BANNER, Items.WHITE_BANNER, Items.YELLOW_BANNER,
                    Items.BOOKSHELF, Items.CHEST, Items.TRAPPED_CHEST,
                    Items.OAK_DOOR, Items.SPRUCE_DOOR, Items.BIRCH_DOOR,
                    Items.JUNGLE_DOOR, Items.ACACIA_DOOR, Items.DARK_OAK_DOOR, Items.MANGROVE_DOOR, Items.CHERRY_DOOR,
                    Items.CRIMSON_DOOR, Items.WARPED_DOOR,

                    Items.OAK_TRAPDOOR, Items.SPRUCE_TRAPDOOR, Items.BIRCH_TRAPDOOR,
                    Items.JUNGLE_TRAPDOOR, Items.ACACIA_TRAPDOOR, Items.DARK_OAK_TRAPDOOR, Items.MANGROVE_TRAPDOOR, Items.CHERRY_TRAPDOOR,
                    Items.CRIMSON_TRAPDOOR, Items.WARPED_TRAPDOOR,

                    Items.OAK_FENCE, Items.SPRUCE_FENCE, Items.BIRCH_FENCE,
                    Items.JUNGLE_FENCE, Items.ACACIA_FENCE, Items.DARK_OAK_FENCE, Items.MANGROVE_FENCE, Items.CHERRY_FENCE,
                    Items.CRIMSON_FENCE, Items.WARPED_FENCE,

                    Items.OAK_FENCE_GATE, Items.SPRUCE_FENCE_GATE, Items.BIRCH_FENCE_GATE,
                    Items.JUNGLE_FENCE_GATE, Items.ACACIA_FENCE_GATE, Items.DARK_OAK_FENCE_GATE, Items.MANGROVE_FENCE_GATE, Items.CHERRY_FENCE_GATE,
                    Items.CRIMSON_FENCE_GATE, Items.WARPED_FENCE_GATE,

                    Items.OAK_SIGN, Items.SPRUCE_SIGN, Items.BIRCH_SIGN,
                    Items.JUNGLE_SIGN, Items.ACACIA_SIGN, Items.DARK_OAK_SIGN, Items.MANGROVE_SIGN, Items.CHERRY_SIGN,
                    Items.CRIMSON_SIGN, Items.WARPED_SIGN,

                    Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT,
                    Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT, Items.MANGROVE_BOAT, Items.CHERRY_BOAT,

                    Items.ACACIA_HANGING_SIGN, Items.BIRCH_HANGING_SIGN, Items.DARK_OAK_HANGING_SIGN,
                    Items.JUNGLE_HANGING_SIGN, Items.MANGROVE_HANGING_SIGN, Items.OAK_HANGING_SIGN,
                    Items.SPRUCE_HANGING_SIGN, Items.CRIMSON_HANGING_SIGN, Items.WARPED_HANGING_SIGN
            ),
            SoundEvents.BLOCK_WOOD_BREAK,
            1.7f,
            0.4f,
            true
    ),

    HONEY(
            Set.of(
                    Items.HONEYCOMB, Items.HONEY_BLOCK
            ),
            SoundEvents.BLOCK_HONEY_BLOCK_BREAK,
            2f,
            0.3f,
            true,
            Map.of(
                    Items.HONEYCOMB, 1.8f,
                    Items.HONEY_BLOCK, 1.4f
            )
    ),


    NOTEBLOCK(
            Set.of(
                    Items.NOTE_BLOCK
            ),
            SoundEvents.BLOCK_NOTE_BLOCK_HARP.value(),
            -1f,
            1.0f,
            true
    ),

    SLIME(
            Set.of(
                    Items.SLIME_BALL, Items.SLIME_BLOCK, Items.SEA_PICKLE
            ),
            SoundEvents.BLOCK_SLIME_BLOCK_BREAK,
            2f,
            0.3f,
            true,
            Map.of(
                    Items.SEA_PICKLE, 2f,
                    Items.SLIME_BALL, 1.9f,
                    Items.SLIME_BLOCK, 1.6f
            )
    ),

    GLASS(
            Set.of(
                    Items.REDSTONE_LAMP, Items.GLOWSTONE, Items.END_PORTAL_FRAME, Items.GLASS_BOTTLE,
                    Items.BEACON, Items.EXPERIENCE_BOTTLE, Items.HONEY_BOTTLE, Items.OMINOUS_BOTTLE,
                    Items.POTION, Items.LINGERING_POTION, Items.SPLASH_POTION,

                    Items.GLASS, Items.TINTED_GLASS, Items.SEA_LANTERN, Items.GLASS_PANE,
                    Items.WHITE_STAINED_GLASS, Items.ORANGE_STAINED_GLASS, Items.MAGENTA_STAINED_GLASS,
                    Items.LIGHT_BLUE_STAINED_GLASS, Items.YELLOW_STAINED_GLASS, Items.LIME_STAINED_GLASS,
                    Items.PINK_STAINED_GLASS, Items.GRAY_STAINED_GLASS, Items.LIGHT_GRAY_STAINED_GLASS,
                    Items.CYAN_STAINED_GLASS, Items.PURPLE_STAINED_GLASS, Items.BLUE_STAINED_GLASS,
                    Items.BROWN_STAINED_GLASS, Items.GREEN_STAINED_GLASS, Items.RED_STAINED_GLASS,
                    Items.BLACK_STAINED_GLASS,
                    Items.WHITE_STAINED_GLASS_PANE, Items.ORANGE_STAINED_GLASS_PANE, Items.MAGENTA_STAINED_GLASS_PANE,
                    Items.LIGHT_BLUE_STAINED_GLASS_PANE, Items.YELLOW_STAINED_GLASS_PANE, Items.LIME_STAINED_GLASS_PANE,
                    Items.PINK_STAINED_GLASS_PANE, Items.GRAY_STAINED_GLASS_PANE, Items.LIGHT_GRAY_STAINED_GLASS_PANE,
                    Items.CYAN_STAINED_GLASS_PANE, Items.PURPLE_STAINED_GLASS_PANE, Items.BLUE_STAINED_GLASS_PANE,
                    Items.BROWN_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.RED_STAINED_GLASS_PANE,
                    Items.BLACK_STAINED_GLASS_PANE
            ),
            SoundEvents.BLOCK_GLASS_BREAK,
            2.0f,
            0.15f,
            true
    ),

    NONE(Set.of(), null, 0f, 0f, false);

    private static final Set<Block> MANUAL_SOFT_BLOCKS = Set.of(
            Blocks.SLIME_BLOCK, Blocks.HONEY_BLOCK,
            Blocks.HAY_BLOCK, Blocks.MOSS_BLOCK,
            Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS,
            Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW,

            Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL,
            Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL,
            Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL,
            Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL,

            Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET, Blocks.LIGHT_BLUE_CARPET,
            Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET,
            Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.PURPLE_CARPET, Blocks.BLUE_CARPET,
            Blocks.BROWN_CARPET, Blocks.GREEN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET,

            Blocks.VINE, Blocks.GLOW_LICHEN, Blocks.COBWEB, Blocks.LARGE_FERN, Blocks.FERN
            , Blocks.TALL_GRASS, Blocks.SEAGRASS, Blocks.KELP, Blocks.KELP_PLANT
    );

    private final Set<Item> matchingItems;
    private final SoundEvent sound;
    private final float basePitch;
    private final float volume;
    private final boolean requiresBlockHit;
    private final Map<Item, Float> customPitches;


    InteractionMaterial(final Set<Item> items, final SoundEvent sound, final float pitch, final float volume, final boolean requiresBlockHit) {
        this(items, sound, pitch, volume, requiresBlockHit, Collections.emptyMap());
    }

    InteractionMaterial(final Set<Item> items, final SoundEvent sound, final float pitch, final float volume, final boolean requiresBlockHit, final Map<Item, Float> customPitches) {
        this.matchingItems = items;
        this.sound = sound;
        this.basePitch = pitch;
        this.volume = volume;
        this.requiresBlockHit = requiresBlockHit;
        this.customPitches = customPitches;
    }

    public boolean matches(final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        final Item item = stack.getItem();
        return matchingItems.contains(item);
    }

    public SoundEvent getSound() {
        return sound;
    }

    public float getPitch(final ItemStack stack) {
        return customPitches.getOrDefault(stack.getItem(), basePitch);
    }

    public float getVolume() {
        return volume;
    }

    public boolean requiresBlockHit() {
        return requiresBlockHit;
    }

    public boolean isExcluded(final Block block) {
        return MANUAL_SOFT_BLOCKS.contains(block);
    }

    public static InteractionMaterial getMaterial(final ItemStack stack) {
        for (final InteractionMaterial material : values()) {
            if (material.matches(stack)) {
                return material;
            }
        }
        return NONE;
    }

}