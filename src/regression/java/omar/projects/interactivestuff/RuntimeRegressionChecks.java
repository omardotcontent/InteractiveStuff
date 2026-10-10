package omar.projects.interactivestuff;

import me.abdelaziz.main.VynMain;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;
import omar.projects.interactivestuff.particles.ParticleSystem;
import omar.projects.interactivestuff.particles.ParticleRegressionChecks;
import omar.projects.interactivestuff.scripts.variables.ParticleEmitter;
import omar.projects.interactivestuff.scripts.variables.Particle;
import omar.projects.interactivestuff.render.ColorMath;
import omar.projects.interactivestuff.render.QuadSelection;
import omar.projects.interactivestuff.render.RenderRegressionChecks;
import omar.projects.interactivestuff.runtime.FrameClock;
import omar.projects.interactivestuff.runtime.TickScheduler;
import omar.projects.interactivestuff.scripts.ScriptExecutionContext;
import omar.projects.interactivestuff.scripts.ScriptRuntime;
import omar.projects.interactivestuff.scripts.functions.ExcludeScript;
import omar.projects.interactivestuff.scripts.functions.ImportScript;
import omar.projects.interactivestuff.scripts.handlers.WaitHandler;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.objects.Script.FunctionType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** No server, display, downloaded test framework, or user world is needed. */
public final class RuntimeRegressionChecks {
    private static int checks;

    public static void main(String[] args) throws Exception {
        scheduler();
        timingAndSelection();
        VynMain.init(false);
        Parser.register("wait", new WaitHandler());
        scripts();
        ParticleRegressionChecks.run(RuntimeRegressionChecks::check);
        bundledScripts();
        RenderRegressionChecks.run(RuntimeRegressionChecks::check);
        System.out.println("Passed " + checks + " runtime/render regression checks.");
    }

    public static void check(boolean condition, String description) {
        checks++;
        if (!condition) throw new AssertionError(description);
    }

    private static void scheduler() {
        List<Throwable> errors = new ArrayList<>();
        TickScheduler queue = new TickScheduler(errors::add);
        List<String> events = new ArrayList<>();
        queue.once(2, () -> events.add("a"));
        queue.once(2, () -> events.add("b"));
        queue.tick();
        check(events.isEmpty(), "waits do not execute early");
        queue.tick();
        check(events.equals(List.of("a", "b")), "same-duration waits are independent and stable");
        queue.once("single", 1, () -> {
            events.add("first");
            queue.once("single", 1, () -> events.add("second"));
        });
        queue.once("single", 1, () -> events.add("duplicate"));
        queue.tick();
        queue.tick();
        check(events.equals(List.of("a", "b", "first", "second")), "named waits deduplicate and can reschedule themselves");
        queue.repeat("loop", 3, () -> events.add("loop"));
        queue.tick();
        queue.pause("loop");
        for (int i = 0; i < 5; i++) queue.tick();
        check(!events.contains("loop"), "paused tasks stay paused past their original due tick");
        queue.resume("loop");
        queue.tick();
        check(!events.contains("loop"), "resume preserves remaining delay");
        queue.tick();
        check(events.getLast().equals("loop"), "resume creates a valid immutable queue entry");
        queue.cancel("loop");
        int count = events.size();
        for (int i = 0; i < 4; i++) queue.tick();
        check(events.size() == count, "cancelled repeating tasks stay cancelled");
        queue.repeat("replace", 1, () -> queue.repeat("replace", 2, () -> events.add("replacement")));
        queue.tick();
        queue.tick();
        check(events.size() == count, "reentrant replacement does not requeue its predecessor");
        queue.tick();
        check(events.getLast().equals("replacement"), "reentrant replacement runs at its own interval");
        queue.clear();
        queue.repeat("clear", 1, queue::clear);
        queue.once(1, () -> events.add("stale"));
        queue.tick();
        check(!queue.contains("clear") && !events.contains("stale"), "clear inside a callback invalidates the current batch");
        queue.once(0, () -> events.add("next"));
        check(!events.getLast().equals("next"), "zero-delay work never executes inline");
        queue.tick();
        check(events.getLast().equals("next"), "zero delay executes next tick");
        queue.repeat("failure", 1, () -> { throw new IllegalStateException("expected"); });
        queue.tick();
        queue.tick();
        check(errors.size() == 1 && !queue.contains("failure"), "failing loops stop once without breaking later ticks");
    }

    private static void timingAndSelection() {
        FrameClock clock = new FrameClock();
        check(Math.abs(clock.advance(1_000_000_000L, false) - 1) < 0.00001, "first frame uses a nominal delta");
        check(Math.abs(clock.advance(1_008_333_333L, false) - 0.5f) < 0.00001, "120Hz delta is normalized");
        check(clock.advance(2_000_000_000L, false) == 3, "stalls are capped at 50ms");
        check(clock.advance(3_000_000_000L, true) == 0, "paused frames have zero delta");
        check(Math.abs(clock.advance(3_016_666_667L, false) - 1) < 0.00001, "resume does not integrate paused time");
        long frame = clock.frame();
        clock.reset();
        check(Math.abs(clock.advance(9_000_000_000L, false) - 1) < 0.00001 && clock.frame() > frame,
                "reset preserves monotonic frame IDs and discards elapsed time");
        check(QuadSelection.resolve(-5, 0, 8).equals(new QuadSelection(0, 8)), "zero end selects through final quad");
        check(QuadSelection.resolve(7, 2, 8).equals(new QuadSelection(2, 2)), "reversed selections are empty");
        check(QuadSelection.resolve(40, 90, 8).equals(new QuadSelection(8, 8)), "out-of-range selections clamp");
        check(ColorMath.multiply(0xFFFFFFFF, 0x80123456) == 0x80123456, "neutral color retains native tint and alpha");
        check(ColorMath.multiply(0x80808080, 0x80808080) == 0x40404040, "color multiplication includes alpha");
    }

    private static ScriptRuntime runtime(List<String> errors) {
        ScriptRuntime runtime = new ScriptRuntime(() -> VynMain.loadLines(""), errors::add);
        VynMain.setStdListener(environment -> {
            NativeBinder.bind(environment, ParticleEmitter.class);
            NativeBinder.bind(environment, Particle.class);
            NativeBinder.defineConstant(environment, "particles", ParticleSystem.INSTANCE);
            environment.defineFunction("importScript", new ImportScript(runtime));
            environment.defineFunction("excludeScript", new ExcludeScript(runtime));
        });
        return runtime;
    }

    private static void scripts() {
        List<String> errors = new ArrayList<>();
        ScriptRuntime runtime = runtime(errors);
        runtime.addScript("base", "z/library", "make counter = 10\ntask addOne do make counter = counter + 1 end");
        runtime.addScript("base", "a/client", """
                importScript("base", "z/library")
                lock interactiveItems = ["minecraft:chest"]
                task onTick do addOne() end
                task onItemUpdate takes item do make counter = counter + 2 end
                """);
        runtime.addScript("base", "b/wildcard", "task onItemUpdate takes item do reply nothing end");
        runtime.load();
        check(errors.isEmpty(), "dependencies load before their importer regardless of lexical order");
        check(runtime.requireScript("base", "z/library").getState() == Script.State.IMPORTABLE, "callback-free libraries remain importable");
        check(runtime.callbacks(FunctionType.ON_TICK).size() == 1, "event index contains only matching callbacks");
        check(runtime.itemCallbacks("minecraft:chest").size() == 2, "filtered and legacy wildcard scripts both match");
        check(runtime.itemCallbacks("minecraft:stone").size() == 1, "unrelated items skip filtered callbacks");
        check(runtime.itemCallbacks("minecraft:stone") == runtime.itemCallbacks("minecraft:stone"), "item callback lists are memoized");
        Script client = runtime.requireScript("base", "a/client");
        client.call(FunctionType.ON_TICK);
        check(client.getEnvironment().get("counter").asInt() == 11, "imported tasks mutate their caller's environment");
        runtime.load();
        check(client.getEnvironment().get("counter").asInt() == 10, "reload resets globals while reusing parsed source");
        AtomicInteger fired = new AtomicInteger();
        Runnable[] deferred = new Runnable[1];
        ScriptExecutionContext.run(client, () -> deferred[0] = ScriptExecutionContext.deferred(fired::incrementAndGet));
        deferred[0].run();
        check(fired.get() == 1, "deferred work executes for a loaded owner");
        runtime.load();
        deferred[0].run();
        check(fired.get() == 1, "deferred work cannot cross an owner reload");
        ScriptExecutionContext.run(client, () -> deferred[0] = ScriptExecutionContext.deferred(() -> { throw new IllegalStateException("expected"); }));
        deferred[0].run();
        deferred[0].run();
        check(client.getState() == Script.State.ERROR && errors.size() == 1, "deferred errors disable and report their owner once");

        errors.clear();
        runtime = runtime(errors);
        runtime.addScript("base", "a/controller", "excludeScript(\"base\", \"z/excluded\")\ntask onTick do reply nothing end");
        runtime.addScript("base", "z/excluded", "task onTick do reply nothing end");
        runtime.load();
        check(runtime.callbacks(FunctionType.ON_TICK).size() == 1
                && runtime.requireScript("base", "z/excluded").getState() == Script.State.EXCLUDED, "exclusion survives the load order");
        runtime.addScript("base", "left/library", "make x = 1");
        runtime.addScript("base", "right/library", "make x = 2");
        try {
            runtime.requireScript("base", "library");
            check(false, "ambiguous aliases must fail");
        } catch (IllegalArgumentException expected) { check(true, "ambiguous short names fail explicitly"); }
        check(runtime.requireScript("base", "left/library") != runtime.requireScript("base", "right/library"), "relative paths retain distinct scripts");

        errors.clear();
        runtime = runtime(errors);
        runtime.addScript("cycle", "a", "importScript(\"cycle\", \"b\")");
        runtime.addScript("cycle", "b", "importScript(\"cycle\", \"a\")");
        runtime.load();
        check(runtime.requireScript("cycle", "a").getState() == Script.State.ERROR
                && runtime.requireScript("cycle", "b").getState() == Script.State.ERROR, "circular imports terminate with errors");
        check(errors.size() == 2, "each circular dependency reports once");
        errors.clear();
        runtime = runtime(errors);
        runtime.addScript("bad", "missing", "importScript(\"absent\", \"library\")");
        runtime.addScript("bad", "filter", "lock interactiveItems = [42]\ntask onItemUpdate takes item do reply nothing end");
        runtime.addScript("bad", "good", "task onTick do reply nothing end");
        runtime.load();
        check(errors.size() == 2 && runtime.callbacks(FunctionType.ON_TICK).size() == 1,
                "missing imports and bad filters fail without blocking healthy scripts");
        runtime.clear();
        check(runtime.packs().isEmpty() && runtime.callbacks(FunctionType.ON_TICK).isEmpty(), "clear releases scripts and callback indexes");
    }

    private static void bundledScripts() throws Exception {
        List<String> errors = new ArrayList<>();
        ScriptRuntime runtime = runtime(errors);
        Path directory = Path.of("src/main/resources/resourcepacks/interactive_resourcepack/assets/interactivestuff/scripts");
        try (var paths = Files.list(directory)) {
            for (Path path : paths.sorted().toList()) {
                runtime.addScript("interactivestuff:interactive_resourcepack", path.getFileName().toString().replace(".vyn", ""), Files.readString(path));
            }
        }
        runtime.load();
        check(errors.isEmpty(), "all bundled scripts parse and initialize: " + errors);
        check(runtime.packs().get("interactivestuff:interactive_resourcepack").scripts().size() == 16, "all bundled scripts are discovered");
        check(runtime.callbacks(FunctionType.ON_ITEM_UPDATE).size() == 13, "all 13 item features register");
        int wildcard = (int) runtime.callbacks(FunctionType.ON_ITEM_UPDATE).stream()
                .filter(script -> !script.getEnvironment().has("interactiveItems")).count();
        check(runtime.itemCallbacks("minecraft:stone").size() == wildcard, "stone only binds deliberately unfiltered scripts");
        check(runtime.itemCallbacks("minecraft:chest").size() == wildcard + 1, "chest skips unrelated filtered features");
        check(runtime.itemCallbacks("minecraft:redstone_torch").size() == wildcard + 2, "overlapping redstone and torch features both run");
        check(runtime.itemCallbacks("minecraft:waxed_oxidized_copper_chest").size() == wildcard + 1, "waxed chest variants are covered");
        long activeScripts = runtime.packs().get("interactivestuff:interactive_resourcepack").scripts().stream()
                .filter(script -> script.getState() == Script.State.LOADED).count();
        check(activeScripts == 15, "baseline all-active-script binding count includes tick-only scripts");
        System.out.println("Bundled dispatch: " + wildcard + " unfiltered callbacks; chest +1, redstone torch +2; " + activeScripts + " active scripts.");
    }
}
