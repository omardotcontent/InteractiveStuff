package omar.projects.interactivestuff.handlers.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import omar.projects.interactivestuff.objects.InteractionMaterial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class ModMenuIntegration implements ModMenuApi {

    private static final Random random = new Random();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(final Screen parent) {
        if (ConfigHandler.INSTANCE == null)
            ConfigHandler.INSTANCE = ConfigHandler.load();
        final ConfigHandler instance = ConfigHandler.INSTANCE;
        final ConfigHandler defaults = new ConfigHandler();

        final YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("InteractiveStuff Config §6§b(Beta)"))
                .save(instance::save);

        // --------------------- General Settings ---------------------
        final ConfigCategory generalCategory = createGeneralCategory(instance, defaults).build();
        builder.category(generalCategory);

        // --------------------- Excluded Blocks ---------------------
        final ConfigCategory excludedCategory = createExcludedBlocksCategory(instance, defaults).build();
        builder.category(excludedCategory);

        // --------------------- Manage Materials ---------------------
        final ConfigCategory manageMaterialsCategory = createManageMaterialsCategory(instance, defaults).build();
        builder.category(manageMaterialsCategory);

        // --------------------- Individual Material Categories ---------------------
        for (final InteractionMaterial mat : instance.materials) {
            final InteractionMaterial defaultMat = defaults.materials.stream()
                    .filter(m -> m.name.equalsIgnoreCase(mat.name))
                    .findFirst()
                    .orElse(null);

            final ConfigCategory matCategory = createMaterialCategory(mat, defaultMat).build();

            builder.category(matCategory);
        }

        return builder.build().generateScreen(parent);
    }

    // --------------------- Category builders ---------------------

    private ConfigCategory.Builder createGeneralCategory(final ConfigHandler instance, final ConfigHandler defaults) {
        final ConfigCategory.Builder general = ConfigCategory.createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.general"))
                .tooltip(Text.translatable("interactive_stuff.config.settings.general.tooltip"));

        general.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.general.interactive_hits"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.settings.general.interactive_hits.tooltip")))
                .binding(defaults.enableInteractiveHits, () -> instance.enableInteractiveHits, newVal -> instance.enableInteractiveHits = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build());

        general.option(
                Option.<Integer>createBuilder()
                        .name(Text.translatable("interactive_stuff.config.settings.general.interactive_hits.cooldown"))
                        .description(OptionDescription.of(Text.translatable("interactive_stuff.config.settings.general.interactive_hits.cooldown.tooltip")))
                        .binding(defaults.HitCooldownTicks, () -> instance.HitCooldownTicks, newVal -> instance.HitCooldownTicks = newVal)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1,10).step(1))
                        .build());

        general.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.general.sculk_sensor"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.settings.general.sculk_sensor.tooltip")))
                .binding(defaults.enableSculkSensorFeature, () -> instance.enableSculkSensorFeature, newVal -> instance.enableSculkSensorFeature = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build());

        general.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.general.note_block"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.settings.general.note_block.tooltip")))
                .binding(defaults.enableNoteBlockCrouchFeature, () -> instance.enableNoteBlockCrouchFeature, newVal -> instance.enableNoteBlockCrouchFeature = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build());

        general.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.general.model_changes"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.settings.general.model_changes.tooltip")))
                .binding(defaults.enableTextureChanges, () -> instance.enableTextureChanges, newVal -> instance.enableTextureChanges = newVal)
                .controller(TickBoxControllerBuilder::create)
                .build());

        return general;
    }

    private ConfigCategory.Builder createExcludedBlocksCategory(final ConfigHandler instance, final ConfigHandler defaults) {
        final ConfigCategory.Builder cat = ConfigCategory.createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.general.excluded_blocks"))
                .tooltip(Text.translatable("interactive_stuff.config.settings.general.excluded_blocks.tooltip"));

        cat.option(ListOption.<Item>createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.general.excluded_blocks"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.settings.general.excluded_blocks.tooltip")))
                .binding(defaults.excludedBlocks,
                        () -> instance.excludedBlocks,
                        newVal -> instance.excludedBlocks = newVal)
                .controller(ItemControllerBuilder::create)
                .initial(Items.STONE)
                .build());

        return cat;
    }

    private ConfigCategory.Builder createManageMaterialsCategory(final ConfigHandler instance, final ConfigHandler defaults) {
        final ConfigCategory.Builder manageMaterials = ConfigCategory.createBuilder()
                .name(Text.translatable("interactive_stuff.config.settings.materials"))
                .tooltip(Text.translatable("interactive_stuff.config.settings.materials.tooltip"));

        manageMaterials.option(
                ListOption.<String>createBuilder()
                        .name(Text.translatable("interactive_stuff.config.settings.materials"))
                        .description(OptionDescription.of(Text.translatable("interactive_stuff.config.settings.materials.details1")
                                .append("\n")
                                .append(Text.translatable("interactive_stuff.config.settings.materials.details2"))
                                .append("\n")
                                .append(Text.translatable("interactive_stuff.config.settings.materials.details3"))
                                .append("\n")
                                .append(Text.translatable("interactive_stuff.config.settings.materials.details4")
                        )))
                        .binding(
                                defaults.materials.stream().map(m -> m.name).collect(Collectors.toList()),
                                () -> instance.materials.stream().map(m -> m.name).collect(Collectors.toList()),
                                newList -> {
                                    instance.materials.removeIf(mat -> !newList.contains(mat.name));

                                    for (final String name : newList) {
                                        final boolean exists = instance.materials.stream().anyMatch(mat -> mat.name.equalsIgnoreCase(name));
                                        if (!exists) {
                                            instance.materials.add(new InteractionMaterial(name));
                                        }
                                    }

                                    instance.materials.sort(Comparator.comparingInt(m -> newList.indexOf(m.name)));

                                    for (int i = 0; i < instance.materials.size(); i++) {
                                        instance.materials.get(i).name = newList.get(i);
                                    }
                                }
                        )
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .collapsed(false)
                        .build()
        );

        return manageMaterials;
    }

    private ConfigCategory.Builder createMaterialCategory(final InteractionMaterial mat, final InteractionMaterial defaultMat) {
        final String displayName = mat.name.isEmpty() ? "?" : mat.name.substring(0, 1).toUpperCase();

        final ConfigCategory.Builder matCat = ConfigCategory.createBuilder()
                .name(Text.literal(displayName))
                .tooltip(Text.literal(mat.name));

        matCat.option(Option.<String>createBuilder()
                .name(Text.translatable("interactive_stuff.config.sound_id"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.sound_id.tooltip")))
                .binding(
                        defaultMat != null ? defaultMat.sound : "minecraft:block.stone.break",
                        () -> mat.sound,
                        newVal -> mat.sound = newVal
                )
                .controller(StringControllerBuilder::create)
                .build());

        matCat.option(Option.<Float>createBuilder()
                .name(Text.translatable("interactive_stuff.config.volume"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.volume.tooltip")))
                .binding(
                        defaultMat != null ? defaultMat.volume : 1.0f,
                        () -> mat.volume,
                        newVal -> mat.volume = newVal
                )
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.05f, 2f).step(0.05f))
                .build());

        matCat.option(Option.<Float>createBuilder()
                .name(Text.translatable("interactive_stuff.config.base_pitch"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.base_pitch.tooltip")))
                .binding(
                        defaultMat != null ? defaultMat.pitch : 1.0f,
                        () -> mat.pitch,
                        newVal -> mat.pitch = newVal
                )
                .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.05f, 2f).step(0.05f))
                .build());

        matCat.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("interactive_stuff.config.random_pitch"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.random_pitch.tooltip", mat.pitch)))
                .binding(
                        defaultMat != null && defaultMat.randomPitch,
                        () -> mat.randomPitch,
                        newVal -> mat.randomPitch = newVal
                )
                .controller(TickBoxControllerBuilder::create)
                .build());

        matCat.option(Option.<Boolean>createBuilder()
                .name(Text.translatable("interactive_stuff.config.requires_block_hit"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.requires_block_hit.tooltip")))
                .binding(
                        defaultMat == null || defaultMat.requiresBlockHit,
                        () -> mat.requiresBlockHit,
                        newVal -> mat.requiresBlockHit = newVal
                )
                .controller(TickBoxControllerBuilder::create)
                .build());

        matCat.option(ButtonOption.createBuilder()
                .name(Text.translatable("interactive_stuff.config.preview_sound"))
                        .description(OptionDescription.of(Text.translatable("interactive_stuff.config.preview_sound.tooltip")))
        .action((yaclScreen, thisOption) ->
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(mat.getSoundEvent(),
                        mat.randomPitch ? mat.pitch + (random.nextFloat() * 1.5F) : mat.pitch)))
                .build()
        );

        matCat.option(ListOption.<Item>createBuilder()
                .name(Text.translatable("interactive_stuff.config.included_items"))
                .description(OptionDescription.of(Text.translatable("interactive_stuff.config.included_items.tooltip")))
                .binding(
                        defaultMat != null ? defaultMat.items : new ArrayList<>(),
                        () -> mat.items,
                        newVal -> mat.items = newVal
                )
                .controller(ItemControllerBuilder::create)
                .initial(Items.STONE)
                .build());

        final List<String> defaultCustomPitches = defaultMat != null
                ? defaultMat.customPitches.stream()
                .map(cp -> (cp.item != null ? Registries.ITEM.getId(cp.item).toString() : "minecraft:stone") + "=" + cp.pitch)
                .collect(Collectors.toList())
                : new ArrayList<>();

        matCat.option(
                ListOption.<String>createBuilder()
                        .name(Text.translatable("interactive_stuff.config.custom_pitches"))
                        .description(OptionDescription.of(Text.translatable("interactive_stuff.config.custom_pitches.tooltip")))
                        .binding(
                                defaultCustomPitches,
                                () -> mat.customPitches.stream()
                                        .map(cp -> (cp.item != null ? Registries.ITEM.getId(cp.item).toString() : "minecraft:stone") + "=" + cp.pitch)
                                        .collect(Collectors.toList()),
                                newList -> {
                                    mat.customPitches.clear();
                                    for (final String entry : newList) {
                                        final String[] split = entry.split("=");
                                        if (split.length != 2) {
                                            continue;
                                        }

                                        try {
                                            final Item item = Registries.ITEM.get(Identifier.tryParse(split[0]));
                                            final float pitch = Float.parseFloat(split[1]);
                                            mat.customPitches.add(new InteractionMaterial.PitchEntry(item, pitch));
                                        } catch (final Exception ignored) {
                                        }
                                    }
                                }
                        )
                        .controller(StringControllerBuilder::create)
                        .initial("minecraft:stone=1.0")
                        .collapsed(false)
                        .build()
        );

        return matCat;
    }
}