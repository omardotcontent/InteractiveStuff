package omar.projects.interactivestuff.scripts;

import me.abdelaziz.main.VynMain;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import omar.projects.interactivestuff.IS;
import omar.projects.interactivestuff.handlers.BackgroundLoopHandler;
import omar.projects.interactivestuff.render.MinecraftItemRenderer;
import omar.projects.interactivestuff.render.MinecraftParticleRenderer;
import omar.projects.interactivestuff.particles.ParticleSystem;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
import omar.projects.interactivestuff.scripts.functions.*;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;
import omar.projects.interactivestuff.scripts.handlers.WaitHandler;
import omar.projects.interactivestuff.scripts.objects.PackScripts;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.objects.Script.FunctionType;
import omar.projects.interactivestuff.scripts.variables.*;

import java.util.Map;

/** Minecraft lifecycle and bindings around the independently testable script runtime. */
public final class ScriptInterpreter {
    private static final Player PLAYER = new Player(null, Minecraft.getInstance());
    private static final World WORLD = new World(null);
    private static final ScriptRuntime RUNTIME = new ScriptRuntime(() -> VynMain.loadLines(""), message -> {
        IS.LOGGER.error(message);
        PLAYER.sendMessage(message);
    });
    private static Level sessionWorld;
    private static volatile long sessionGeneration;
    private static boolean ready;

    private ScriptInterpreter() {}

    public static void register() {
        VynMain.init(false);
        Parser.register("wait", new WaitHandler());
        VynMain.setStdListener(environment -> {
            NativeBinder.defineConstant(environment, "player", PLAYER);
            NativeBinder.defineConstant(environment, "world", WORLD);
            NativeBinder.defineConstant(environment, "config", new InteractiveStuffConfig());
            NativeBinder.defineConstant(environment, "key", new Key());
            NativeBinder.defineConstant(environment, "modLoader", new ModLoader(FabricLoader.getInstance()));
            NativeBinder.bind(environment, ItemModel.class);
            NativeBinder.bind(environment, Sound.class);
            NativeBinder.bind(environment, Position.class);
            NativeBinder.bind(environment, ParticleEmitter.class);
            NativeBinder.bind(environment, ConfigOption.class);
            NativeBinder.bind(environment, Particle.class);
            NativeBinder.defineConstant(environment, "particles", ParticleSystem.INSTANCE);
            environment.defineFunction("importScript", new ImportScript(RUNTIME));
            environment.defineFunction("excludeScript", new ExcludeScript(RUNTIME));
            environment.defineFunction("debugText", new DebugText());
            environment.defineFunction("translateText", new TranslateText());
            environment.defineFunction("getDelta", new GetDelta());
            environment.defineFunction("getFrameId", new NativeFunction((ignoredFirst, ignoredSecond) -> new Value(RenderTickHandler.getFrameId())));
        });
        ClientPlayConnectionEvents.JOIN.register((ignoredHandler, ignoredSender, client) -> updateSession(client));
        ClientPlayConnectionEvents.DISCONNECT.register((ignoredHandler, client) -> {
            ready = false;
            sessionWorld = null;
            PLAYER.setPlayer(null);
            WORLD.setWorld(null);
            clearSessionWork();
        });
        ClientTickEvents.START_CLIENT_TICK.register(ScriptInterpreter::tick);
    }

    private static void updateSession(Minecraft client) {
        boolean changedWorld = sessionWorld != client.level;
        PLAYER.setPlayer(client.player);
        WORLD.setWorld(client.level);
        if (changedWorld) {
            sessionWorld = client.level;
            clearSessionWork();
            RUNTIME.load();
        }
        ready = client.player != null && client.level != null;
    }

    private static void clearSessionWork() {
        sessionGeneration++;
        BackgroundLoopHandler.getInstance().clearAll();
        ItemModelRenderRegistry.clear();
        omar.projects.interactivestuff.render.ModelTargeting.clear();
        RenderTickHandler.reset();
        ParticleSystem.INSTANCE.reset();
        if (DebugTextHandler.INSTANCE != null) DebugTextHandler.INSTANCE.clear();
    }

    private static void tick(Minecraft client) {
        if (client.level != sessionWorld || client.player != PLAYER.getPlayer()) updateSession(client);
        if (!ready || client.isPaused()) return;
        for (Script script : RUNTIME.callbacks(FunctionType.ON_TICK)) script.call(FunctionType.ON_TICK);
        var config = ConfigHandler.INSTANCE;
        ParticleSystem.INSTANCE.tick(config.enableParticles, config.particleDensity / 100.0, config.particleLimit,
                client.options.getCameraType().isFirstPerson(), (mainHand, id) -> {
                    var stack = mainHand ? client.player.getMainHandItem() : client.player.getOffhandItem();
                    return !stack.isEmpty() && BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id);
                });
    }

    public static boolean hasItemCallbacks(ItemStack stack) {
        return ready && !stack.isEmpty()
                && !RUNTIME.itemCallbacks(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()).isEmpty();
    }

    public static ItemModel itemUpdate(ItemStack stack, ItemDisplayContext context, boolean mainHand) {
        if (!ready) return null;
        ItemModel model = new ItemModel(stack);
        model.setDisplayContext(context, mainHand);
        for (Script script : RUNTIME.itemCallbacks(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()))
            script.call(FunctionType.ON_ITEM_UPDATE, model);
        return model.modified ? model : null;
    }

    public static void onSwingHand() {
        if (!ready) return;
        for (Script script : RUNTIME.callbacks(FunctionType.ON_SWING_HAND)) script.call(FunctionType.ON_SWING_HAND);
    }

    public static void onPlaySound(Sound sound) {
        Minecraft client = Minecraft.getInstance();
        long generation = sessionGeneration;
        Runnable dispatch = () -> {
            if (!ready || generation != sessionGeneration) return;
            for (Script script : RUNTIME.callbacks(FunctionType.ON_PLAY_SOUND)) script.call(FunctionType.ON_PLAY_SOUND, sound);
        };
        if (client.isSameThread()) dispatch.run();
        else client.execute(dispatch);
    }

    public static void onKeyPress(int key) {
        if (!ready || omar.projects.interactivestuff.compat.MinecraftAccess.screen(Minecraft.getInstance()) != null) return;
        for (Script script : RUNTIME.callbacks(FunctionType.ON_KEY_PRESS)) script.call(FunctionType.ON_KEY_PRESS, key);
    }

    public static void addScript(String pack, String name, String code) { RUNTIME.addScript(pack, name, code); }

    public static void loadScripts() {
        clearSessionWork();
        Minecraft client = Minecraft.getInstance();
        PLAYER.setPlayer(client.player);
        WORLD.setWorld(client.level);
        sessionWorld = client.level;
        RUNTIME.load();
        ready = client.player != null && client.level != null;
    }

    public static void clearScripts() {
        ready = false;
        clearSessionWork();
        MinecraftItemRenderer.clearCaches();
        MinecraftParticleRenderer.clearCaches();
        omar.projects.interactivestuff.handlers.config.SoundPicker.clearCache();
        RUNTIME.clear();
    }

    public static Map<String, PackScripts> getPackScripts() { return RUNTIME.packs(); }
}
