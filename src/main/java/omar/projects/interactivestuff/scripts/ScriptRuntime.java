package omar.projects.interactivestuff.scripts;

import me.abdelaziz.runtime.Environment;
import omar.projects.interactivestuff.scripts.objects.PackScripts;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.objects.Script.FunctionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Resource-order loading and callback indexes. No Minecraft API is needed by this core. */
public final class ScriptRuntime {
    private final Map<String, PackScripts> packs = new LinkedHashMap<>();
    private final EnumMap<FunctionType, List<Script>> callbacks = new EnumMap<>(FunctionType.class);
    private final Map<String, List<Script>> itemCallbacks = new HashMap<>();
    private final Supplier<Environment> environments;
    private final Consumer<String> errors;

    public ScriptRuntime(Supplier<Environment> environments, Consumer<String> errors) {
        this.environments = environments;
        this.errors = errors;
        for (FunctionType type : FunctionType.values()) callbacks.put(type, List.of());
    }

    public void addScript(String pack, String name, String code) {
        packs.computeIfAbsent(pack, ignored -> new PackScripts())
                .addScript(name, new Script(code, pack, name, environments, errors));
    }

    public void load() {
        omar.projects.interactivestuff.config.PackOptions.beginRegistration();
        itemCallbacks.clear();
        for (PackScripts pack : packs.values()) for (Script script : pack.scripts()) script.reset();
        for (PackScripts pack : packs.values()) for (Script script : pack.scripts()) script.load();
        omar.projects.interactivestuff.config.PackOptions.finishRegistration();
        for (FunctionType type : FunctionType.values()) {
            List<Script> indexed = new ArrayList<>();
            for (PackScripts pack : packs.values()) {
                for (Script script : pack.scripts()) if (script.hasCallback(type)) indexed.add(script);
            }
            callbacks.put(type, List.copyOf(indexed));
        }
    }

    public List<Script> callbacks(FunctionType type) { return callbacks.get(type); }

    public List<Script> itemCallbacks(String itemId) {
        return itemCallbacks.computeIfAbsent(itemId, id ->
                callbacks.get(FunctionType.ON_ITEM_UPDATE).stream().filter(script -> script.acceptsItem(id)).toList());
    }

    public Script requireScript(String packId, String name) {
        PackScripts pack = packs.get(packId);
        Script script = pack == null ? null : pack.getScript(name);
        if (script == null) throw new IllegalArgumentException("No script " + name + " in pack " + packId);
        return script;
    }

    public Map<String, PackScripts> packs() { return Collections.unmodifiableMap(packs); }

    public void clear() {
        omar.projects.interactivestuff.config.PackOptions.beginRegistration();
        packs.clear();
        itemCallbacks.clear();
        for (FunctionType type : FunctionType.values()) callbacks.put(type, List.of());
    }
}
