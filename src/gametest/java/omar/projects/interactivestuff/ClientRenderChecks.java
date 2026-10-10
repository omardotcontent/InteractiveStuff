package omar.projects.interactivestuff;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omar.projects.interactivestuff.handlers.BackgroundLoopHandler;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.render.MinecraftItemRenderer;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.variables.ItemModel;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Real resolver, real mixins, real frames; only this test mod supplies the fixture. */
public final class ClientRenderChecks implements FabricClientGameTest {
    @Override public void runTest(ClientGameTestContext context) {
        try (var world = context.worldBuilder().adjustSettings(settings -> {
            settings.setName("InteractiveStuff render checks");
            settings.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
        }).create()) {
            world.getConnection().waitForChunksRender();
            context.runOnClient(client -> {
                checkScripts();
                components();
                renderAdapter(client);
                configPersistence();
                client.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.NOTE_BLOCK));
                var hit = client.hitResult;
                client.hitResult = null;
                try { ScriptInterpreter.onSwingHand(); }
                finally { client.hitResult = hit; }
            });
            context.runOnClient(client -> ConfigHandler.INSTANCE.resourcePackDebugMode = true);
            context.waitTicks(2);
            context.runOnClient(client -> {
                checkScripts();
                ConfigHandler.INSTANCE.resourcePackDebugMode = false;
            });

            Item[] features = {Items.CHEST, Items.ENCHANTING_TABLE, Items.ITEM_FRAME, Items.OAK_LEAVES,
                    Items.MACE, Items.PISTON, Items.REDSTONE_TORCH, Items.OAK_SAPLING,
                    Items.SCULK_SENSOR, Items.TORCH, Items.TOTEM_OF_UNDYING, Items.VAULT, Items.WATER_BUCKET};
            for (Item item : features) {
                context.runOnClient(client -> {
                    client.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(item));
                    client.player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.ITEM_FRAME));
                });
                context.waitTicks(8);
                context.runOnClient(client -> {
                    check(ScriptInterpreter.hasItemCallbacks(new ItemStack(item)), "feature filter: " + item);
                    checkScripts();
                });
                if (item == Items.CHEST || item == Items.OAK_LEAVES || item == Items.WATER_BUCKET)
                    context.takeScreenshot("interactivestuff-" + item.toString().replace(':', '-'));
            }
            context.runOnClient(client -> {
                client.options.mainHand().set(HumanoidArm.LEFT);
                client.options.broadcastOptions();
            });
            context.waitFor(client -> client.player.getMainArm() == HumanoidArm.LEFT);
            context.waitTicks(8);
            context.runOnClient(client -> {
                checkScripts();
                check(!ScriptInterpreter.hasItemCallbacks(new ItemStack(Items.STONE)), "vanilla stone fast path");
            });
            context.takeScreenshot("interactivestuff-left-handed");

            AtomicInteger waits = new AtomicInteger();
            context.runOnClient(client -> {
                BackgroundLoopHandler.getInstance().waitTicks(2, waits::incrementAndGet);
                BackgroundLoopHandler.getInstance().waitTicks(2, waits::incrementAndGet);
            });
            context.waitTicks(4);
            check(waits.get() == 2, "both real client waits fired");

            context.runOnClient(client -> client.reloadResourcePacks());
            context.waitFor(client -> client.gui.overlay() == null && ScriptInterpreter.hasItemCallbacks(new ItemStack(Items.CHEST)), 600);
            context.waitTicks(4);
            context.runOnClient(client -> {
                checkScripts();
                renderAdapter(client);
            });
            context.runOnClient(client -> BackgroundLoopHandler.getInstance().waitTicks("disconnect-check", 100, waits::incrementAndGet));
        }
        context.runOnClient(client -> {
            check(!BackgroundLoopHandler.getInstance().isLoopRunning("disconnect-check"), "disconnect clears scheduled work");
            check(!ScriptInterpreter.hasItemCallbacks(new ItemStack(Items.CHEST)), "disconnect clears render readiness");
        });
        IS.LOGGER.info("InteractiveStuff client render/lifecycle checks passed.");
    }

    private static void checkScripts() {
        int count = 0;
        for (var pack : ScriptInterpreter.getPackScripts().values()) {
            for (Script script : pack.scripts()) {
                check(script.getState() != Script.State.ERROR, "healthy script: " + script.getFilename());
                count++;
            }
        }
        check(count >= 16, "bundled resource pack loaded");
    }

    private static void components() {
        ItemStack source = new ItemStack(Items.STONE);
        ItemModel model = new ItemModel(source);
        model.setDataComponent("minecraft:custom_data", "{outer:{text:\"a,b:c\",list:[1,2]},flag:true}");
        check(model.hasDataComponent("custom_data"), "nested SNBT decoded");
        check(model.getDataComponent("custom_data").contains("a,b:c"), "quoted commas and colons survive");
        check(!source.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA), "component edits do not mutate inventory");
        ItemModel copy = model.copy();
        copy.removeDataComponent("custom_data");
        check(model.hasDataComponent("custom_data") && !copy.hasDataComponent("custom_data"), "copied stacks mutate independently");
        model.setItemModel("minecraft:stone");
        check(model.getDataComponent("item_model").contains("minecraft:stone"), "bare model ID shorthand decodes");
    }

    private static void configPersistence() {
        ConfigHandler config = new ConfigHandler();
        config.specializedNoteblockHits = false;
        config.materials.add(new omar.projects.interactivestuff.objects.InteractionMaterial("test"));
        config.save();
        check(config.getMaterial(Items.STONE) != null, "save refreshes material lookup");
        config.specializedNoteblockHits = true;
        config.reload();
        check(!config.specializedNoteblockHits, "reload copies specialized noteblock setting");
        config.materials.clear();
        config.save();
        check(config.getMaterial(Items.STONE) == null, "removed material leaves no stale cache entry");
        ConfigHandler.INSTANCE.save();
    }

    private static void renderAdapter(Minecraft client) {
        List<Object[]> itemSubmits = new ArrayList<>();
        AtomicInteger specialSubmits = new AtomicInteger();
        SubmitNodeCollector queue = (SubmitNodeCollector) Proxy.newProxyInstance(
                SubmitNodeCollector.class.getClassLoader(), new Class[]{SubmitNodeCollector.class}, (proxy, method, args) -> {
                    if (method.getName().equals("order")) return proxy;
                    if (method.getName().equals("submitItem")) itemSubmits.add(args.clone());
                    else if (method.getName().startsWith("submit")) specialSubmits.incrementAndGet();
                    return null;
                });
        PoseStack matrices = new PoseStack();
        var originalPose = matrices.last();
        ItemModel leaves = new ItemModel(new ItemStack(Items.OAK_LEAVES));
        leaves.setColor(80, 160, 240);
        MinecraftItemRenderer.submit(leaves, client.getItemModelResolver(), client.player,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, matrices, queue, 0x00F000F0);
        check(!itemSubmits.isEmpty(), "colored baked model submitted");
        for (Object[] args : itemSubmits) {
            int[] colors = (int[]) args[5];
            @SuppressWarnings("unchecked") List<BakedQuad> quads = (List<BakedQuad>) args[6];
            for (BakedQuad quad : quads)
                check(quad.materialInfo().tintIndex() >= 0 && quad.materialInfo().tintIndex() < colors.length,
                        "color lookup covers every quad tint index");
        }
        ItemModel shield = new ItemModel(new ItemStack(Items.SHIELD));
        shield.translate(0.1, 0.2, 0.3);
        shield.setColor(128, 255, 255);
        MinecraftItemRenderer.submit(shield, client.getItemModelResolver(), client.player,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, matrices, queue, 0x00F000F0);
        check(specialSubmits.get() > 0, "special shield renderer preserved in custom path");
        itemSubmits.clear();
        ItemModel selected = new ItemModel(new ItemStack(Items.STONE));
        selected.select(0, 1);
        selected.setColor(128, 255, 255);
        selected.setGlint(1);
        selected.setLight(15);
        MinecraftItemRenderer.submit(selected, client.getItemModelResolver(), client.player,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, matrices, queue, 0);
        int submitted = itemSubmits.stream().mapToInt(args -> ((List<?>) args[6]).size()).sum();
        check(submitted == 1, "exclusive partial selection submits only one quad");
        check((int) itemSubmits.getFirst()[2] == 0x00F000F0, "light override reaches submission");
        check(itemSubmits.getFirst()[7] == net.minecraft.client.renderer.item.ItemStackRenderState.FoilType.STANDARD,
                "foil override reaches submission");
        itemSubmits.clear();
        ItemModel partial = new ItemModel(new ItemStack(Items.STONE));
        partial.setQuads(1, 2);
        partial.translate(0.1, 0, 0);
        MinecraftItemRenderer.submit(partial, client.getItemModelResolver(), client.player,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, matrices, queue, 0);
        check(itemSubmits.size() == 3 && itemSubmits.stream().mapToInt(args -> ((List<?>) args[6]).size()).sum() == 6,
                "nonexclusive selection preserves both complements");
        check(matrices.last() == originalPose, "all adapter pose pushes are balanced");
        ItemModel stone = new ItemModel(new ItemStack(Items.STONE));
        stone.translate(1, 2, 3);
        boolean enabled = ConfigHandler.INSTANCE.resourcePackMatrixEditing;
        ConfigHandler.INSTANCE.resourcePackMatrixEditing = false;
        try {
            PoseStack noTransform = new PoseStack();
            var before = new org.joml.Matrix4f(noTransform.last().pose());
            stone.apply(noTransform);
            check(noTransform.last().pose().equals(before), "matrix editing toggle is honored");
        } finally {
            ConfigHandler.INSTANCE.resourcePackMatrixEditing = enabled;
            ItemModelRenderRegistry.clear();
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
