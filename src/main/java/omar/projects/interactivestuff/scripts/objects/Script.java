package omar.projects.interactivestuff.scripts.objects;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.lexer.Lexer;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.VynCallable;
import me.abdelaziz.util.NativeBinder;
import omar.projects.interactivestuff.scripts.ScriptExecutionContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Parsed code is reusable; runtime state belongs to one resource-pack script and one session. */
public final class Script {
    private final String filename;
    private final String packId;
    private final String scriptName;
    private final String code;
    private final Supplier<Environment> environmentFactory;
    private final Consumer<String> errors;
    private final VynCallable[] callables = new VynCallable[FunctionType.values().length];
    private List<Statement> statements;
    private Environment environment;
    private State state = State.UNLOADED;
    private Set<String> itemFilter;
    private Exception failure;
    private long generation;

    public Script(String code, String filename, Supplier<Environment> environmentFactory, Consumer<String> errors) {
        this(code, "standalone", filename, environmentFactory, errors);
    }

    public Script(String code, String packId, String scriptName, Supplier<Environment> environmentFactory, Consumer<String> errors) {
        this.code = code;
        this.packId = packId;
        this.scriptName = scriptName;
        this.filename = packId + "/" + scriptName;
        this.environmentFactory = environmentFactory;
        this.errors = errors;
    }

    public void load() {
        if (state == State.LOADING) throw new IllegalStateException("Circular script import involving " + filename);
        if (state != State.UNLOADED) return;
        state = State.LOADING;
        try {
            if (statements == null) statements = List.copyOf(new Parser(new Lexer(code).tokenize()).parse());
            environment = environmentFactory.get();
            ScriptExecutionContext.run(this, () -> execute(environment));
            if (state == State.EXCLUDED) return;
            boolean hasCallbacks = false;
            for (FunctionType type : FunctionType.values()) {
                if (environment.hasFunction(type.getName())) {
                    callables[type.ordinal()] = environment.getFunction(type.getName());
                    hasCallbacks = true;
                }
            }
            itemFilter = readItemFilter();
            state = hasCallbacks ? State.LOADED : State.IMPORTABLE;
        } catch (Exception error) {
            fail("loading", error);
        }
    }

    private Set<String> readItemFilter() {
        if (!environment.has("interactiveItems")) return null;
        Object value = environment.get("interactiveItems").asJavaObject();
        if (!(value instanceof List<?> entries)) {
            throw new IllegalArgumentException("interactiveItems must be an array of item ID strings");
        }
        Set<String> items = new HashSet<>();
        for (Object entry : entries) {
            Object item = entry instanceof Value wrapped ? wrapped.asJavaObject() : entry;
            if (!(item instanceof String name) || !name.contains(":")) {
                throw new IllegalArgumentException("interactiveItems requires namespaced item IDs");
            }
            items.add(name);
        }
        return Set.copyOf(items);
    }

    public void importInto(Environment target) {
        load();
        if (state != State.IMPORTABLE) {
            throw new IllegalStateException("Cannot import " + filename + " (state=" + state + ")"
                    + (failure == null ? "" : ": " + failure.getMessage()));
        }
        execute(target);
    }

    private void execute(Environment target) {
        for (Statement statement : statements) statement.execute(target);
    }

    public boolean hasCallback(FunctionType type) {
        return state == State.LOADED && callables[type.ordinal()] != null;
    }

    public boolean acceptsItem(String id) {
        return itemFilter == null || itemFilter.contains(id);
    }

    public void call(FunctionType type) { invoke(type, null, false); }
    public void call(FunctionType type, Object argument) { invoke(type, argument, true); }

    private void invoke(FunctionType type, Object argument, boolean hasArgument) {
        if (!hasCallback(type)) return;
        try {
            ScriptExecutionContext.run(this, () -> callables[type.ordinal()].call(environment,
                    hasArgument ? List.of(NativeBinder.toValue(environment, argument)) : List.of()));
        } catch (Exception error) {
            fail(type.getName(), error);
        }
    }

    public void fail(String phase, Exception error) {
        if (state == State.ERROR) return;
        failure = error;
        state = State.ERROR;
        errors.accept("(" + filename + ") Error during " + phase + ": " + error.getMessage());
    }

    public void exclude() { state = State.EXCLUDED; }

    public void reset() {
        generation++;
        state = State.UNLOADED;
        environment = null;
        itemFilter = null;
        failure = null;
        java.util.Arrays.fill(callables, null);
    }

    /** Callbacks are isolated to the same script generation as their declaration. */
    public void callTask(String name, Object... arguments) {
        if (state != State.LOADED && state != State.IMPORTABLE) return;
        try {
            ScriptExecutionContext.run(this, () -> {
                if (!environment.hasFunction(name)) throw new IllegalArgumentException("Missing config callback task: " + name);
                environment.getFunction(name).call(environment, java.util.Arrays.stream(arguments)
                        .map(argument -> NativeBinder.toValue(environment, argument)).toList());
            });
        } catch (Exception error) { fail("config " + name, error); }
    }

    public String getPackId() { return packId; }
    public String getScriptName() { return scriptName; }
    public long getGeneration() { return generation; }

    public State getState() { return state; }
    public String getCode() { return code; }
    public Environment getEnvironment() { return environment; }
    public String getFilename() { return filename; }

    public enum State { UNLOADED, LOADING, LOADED, IMPORTABLE, ERROR, EXCLUDED }

    public enum FunctionType {
        ON_TICK("onTick"), ON_ITEM_UPDATE("onItemUpdate"), ON_SWING_HAND("onSwingHand"),
        ON_PLAY_SOUND("onPlaySound"), ON_KEY_PRESS("onKeyPress");

        private final String name;
        FunctionType(String name) { this.name = name; }
        public String getName() { return name; }
    }
}
