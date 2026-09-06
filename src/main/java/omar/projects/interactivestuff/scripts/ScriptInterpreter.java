package omar.projects.interactivestuff.scripts;

import me.abdelaziz.main.VynMain;

import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omar.projects.interactivestuff.handlers.BackgroundLoopHandler;
import omar.projects.interactivestuff.scripts.functions.DebugText;
import omar.projects.interactivestuff.scripts.functions.ImportScript;
import omar.projects.interactivestuff.scripts.functions.ExcludeScript;
import omar.projects.interactivestuff.scripts.functions.GetDelta;
import omar.projects.interactivestuff.scripts.handlers.WaitHandler;
import omar.projects.interactivestuff.scripts.objects.PackScripts;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.variables.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public final class ScriptInterpreter {
    private static final InteractiveStuffConfig INTERACTIVE_STUFF_CONFIG = new InteractiveStuffConfig();
    private static final ModLoader MOD_LOADER = new ModLoader(FabricLoader.getInstance());
    private static final Player PLAYER_VAR;
    private static final Key KEY = new Key();

    private static final Set<Script> globalScripts = new CopyOnWriteArraySet<>();
    private static final Map<String, PackScripts> packScripts = new ConcurrentHashMap<>();

    private static final NativeFunction IMPORT_SCRIPT_FUNCTION = new ImportScript(packScripts);
    private static final NativeFunction EXCLUDE_SCRIPT_FUNCTION = new ExcludeScript(packScripts);
    private static final NativeFunction DEBUG_TEXT_FUNCTION = new DebugText();
    private static final NativeFunction GET_DELTA_FUNCTION = new GetDelta();

    static {
        PLAYER_VAR = new Player(Minecraft.getInstance().player, Minecraft.getInstance());
        ClientPlayConnectionEvents.JOIN
                .register((_, _, client) -> PLAYER_VAR.setPlayer(client.player));
    }

    private ScriptInterpreter() {
    }

    public static void register() {
        VynMain.init(true);
        Parser.register("wait", new WaitHandler());
        VynMain.setStdListener(listener -> {
            NativeBinder.defineConstant(listener, "player", PLAYER_VAR);
            NativeBinder.defineConstant(listener, "world", PLAYER_VAR.getWorld());
            NativeBinder.defineConstant(listener, "config", INTERACTIVE_STUFF_CONFIG);
            NativeBinder.defineConstant(listener, "key", KEY);
            NativeBinder.defineConstant(listener, "modLoader", MOD_LOADER);
            NativeBinder.bind(listener, ItemModel.class);
            NativeBinder.bind(listener, Sound.class);
            NativeBinder.bind(listener, Position.class);
            listener.defineFunction("importScript", IMPORT_SCRIPT_FUNCTION);
            listener.defineFunction("excludeScript", EXCLUDE_SCRIPT_FUNCTION);
            listener.defineFunction("debugText", DEBUG_TEXT_FUNCTION);
            listener.defineFunction("getDelta", GET_DELTA_FUNCTION);
        });
        ClientTickEvents.START_CLIENT_TICK.register(ScriptInterpreter::tick);
    }

    private static void tick(final Minecraft client) {
        if (client.player != null && client.player != PLAYER_VAR.getPlayer()) {
            PLAYER_VAR.setPlayer(client.player);
        }

        if (PLAYER_VAR.getPlayer() == null || client.isPaused() || client.level == null) {
            return;
        }

        if (PLAYER_VAR.getLivingEntity() == null) {
            PLAYER_VAR.setLivingEntity(client.player.getLivingEntity());
        }

        for (final Script entry : globalScripts) {
            entry.call(PLAYER_VAR);
        }
    }

    public static ItemModel itemUpdate(final ItemStack itemStack, final ItemDisplayContext displayContext) {
        if (PLAYER_VAR.getPlayer() == null) {
            return null;
        }
        final ItemModel item = new ItemModel(itemStack);
        item.setDisplayContext(displayContext);

        for (final Script entry : globalScripts) {
            entry.call(PLAYER_VAR, Script.FunctionType.ON_ITEM_UPDATE,
                    List.of(NativeBinder.toValue(entry.getEnvironment(), item)));
        }

        if (!item.modified) {
            return null;
        }

        return item;
    }

    public static void onSwingHand() {
        for (final Script entry : globalScripts) {
            entry.call(PLAYER_VAR, Script.FunctionType.ON_SWING_HAND);
        }
    }

    public static void onPlaySound(Sound sound) {
        for (Script entry : globalScripts)
            entry.call(PLAYER_VAR, Script.FunctionType.ON_PLAY_SOUND,
                    List.of(NativeBinder.toValue(entry.getEnvironment(), sound)));
    }


    public static void onKeyPress(int keyInput) {
        for (Script entry : globalScripts)
            entry.call(PLAYER_VAR, Script.FunctionType.ON_KEY_PRESS,
                    List.of(NativeBinder.toValue(entry.getEnvironment(), keyInput)));
    }

    public static void addScript(String packId, String name, String content) {
        packScripts.computeIfAbsent(packId, _ -> new PackScripts())
                .addScript(content, name);
    }

    public static void loadScripts() {
        Deque<Script> delayedScripts = new ArrayDeque<>();
        for (PackScripts packScripts : ScriptInterpreter.packScripts.values())
            packScripts.loadScripts(PLAYER_VAR, globalScripts, delayedScripts);
        while (!delayedScripts.isEmpty())
            delayedScripts.pop().load(PLAYER_VAR, globalScripts, delayedScripts);
    }

    public static void clearScripts() {
        globalScripts.clear();
        packScripts.clear();
        BackgroundLoopHandler.getInstance().clearAll();
    }

    public static Set<Script> getGlobalScripts() {
        return globalScripts;
    }

    public static Map<String, PackScripts> getPackScripts() {
        return packScripts;
    }
}