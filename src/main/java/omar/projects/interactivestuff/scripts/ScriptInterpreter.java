package omar.projects.interactivestuff.scripts;


import me.abdelaziz.main.VynMain;

import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import omar.projects.interactivestuff.scripts.functions.ImportScript;
import omar.projects.interactivestuff.scripts.functions.ExcludeScript;
import omar.projects.interactivestuff.scripts.handlers.WaitHandler;
import omar.projects.interactivestuff.scripts.objects.PackScripts;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.variables.*;

import java.util.*;

public final class ScriptInterpreter {

    private static final Player playerVar;
    private static final InteractiveStuffConfig interactiveStuffConfig = new InteractiveStuffConfig();
    private static final Key key = new Key();

    private static final Set<Script> globalScripts = new HashSet<>();
    private static final Map<String, PackScripts> scripts = new HashMap<>();

    private static final NativeFunction importScriptFunction = new ImportScript(scripts);

    private static final NativeFunction excludeScriptFunction = new ExcludeScript(scripts);

    static {
        playerVar = new Player(MinecraftClient.getInstance().player, MinecraftClient.getInstance());
        ClientPlayConnectionEvents.JOIN
                .register((handler, sender, client) -> playerVar.setPlayer(client.player));
    }

    public static void register() {
        VynMain.init(true);
        Parser.register("wait", new WaitHandler());
        VynMain.setStdListener(listener -> {
            NativeBinder.defineConstant(listener, "player", playerVar);
            NativeBinder.defineConstant(listener, "world", playerVar.getWorld());
            NativeBinder.defineConstant(listener, "config", interactiveStuffConfig);
            NativeBinder.defineConstant(listener, "key", key);
            listener.defineFunction("importScript", importScriptFunction);
            listener.defineFunction("excludeScript", excludeScriptFunction);
        });
        ClientTickEvents.END_CLIENT_TICK.register(ScriptInterpreter::tick);
    }

    private static void tick(final MinecraftClient client) {
        if (playerVar.getPlayer() != null && !client.isPaused() && client.world != null) {
            for (final Script entry : globalScripts)
                entry.call(playerVar);
        }
    }

    public static ItemStack itemUpdate(final ItemStack itemStack, final MatrixStack matrixStack) {
        if (playerVar.getPlayer() == null) return null;
        final Item item = new Item(itemStack);
        final Matrices matrices = matrixStack == null ? null : new Matrices(matrixStack);

        for (final Script entry : globalScripts) {
            entry.call(playerVar, Script.FunctionType.ON_ITEM_UPDATE, List.of(NativeBinder.toValue(entry.getEnvironment(), item), NativeBinder.toValue(entry.getEnvironment(), matrices)));
        }

        // Only return modified stack if actually changed
        return item.wasModified() ? item.getFinalItemStack() : null;
    }

    public static void onSwingHand() {
        for (final Script entry : globalScripts)
            entry.call(playerVar, Script.FunctionType.ON_SWING_HAND);
    }

    public static void onPlaySound(final Sound sound) {
        for (final Script entry : globalScripts)
            entry.call(playerVar, Script.FunctionType.ON_PLAY_SOUND, List.of(NativeBinder.toValue(entry.getEnvironment(), sound)));
    }

    public static void addScript(final String packId, final String name, final String content) {
        scripts.computeIfAbsent(packId, k -> new PackScripts())
                .addScript(content, name);
    }

    public static void loadScripts() {
        for(final PackScripts packScripts: scripts.values()) {
            packScripts.loadScripts(playerVar, globalScripts);
        }
    }

    public static void clearScripts() {
        globalScripts.clear();
        scripts.clear();
    }

}