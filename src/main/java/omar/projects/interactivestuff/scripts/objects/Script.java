package omar.projects.interactivestuff.scripts.objects;

import me.abdelaziz.main.VynMain;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.VynCallable;
import omar.projects.interactivestuff.scripts.variables.Player;

import java.util.*;

public final class Script {

    private State state;
    private final String filename, code;
    private Environment environment;
    private final Map<FunctionType, VynCallable> callables;

    public Script(final String code, final String fileName) {
        this.code = code;
        this.filename = fileName;
        this.callables = new HashMap<>();
    }

    public void load(final Player playerVar, final Set<Script> globalScripts) {
        try {
            environment = VynMain.loadLines(code);

            for (final FunctionType type : FunctionType.values()) {
                try {
                    this.callables.put(type, environment.getFunction(type.getName()));
                } catch (final Exception ignored) {

                }
            }

            if(callables.isEmpty()) {
                this.state = State.IMPORTABLE;
                return;
            }

            this.state = State.LOADED;
            globalScripts.add(this);
        } catch (final Exception e) {
            this.state = State.ERROR;

            playerVar.sendMessage("§6(" + filename + ") §cError in script during loading: " + e.getMessage());
        }
        System.out.println(
                "Loaded .vyn script: " + filename +
                        " (state=" + state + ")"
        );
        call(playerVar, Script.FunctionType.ON_LOAD);
    }

    public void call(final Player playerVar) {
        call(playerVar, FunctionType.ON_TICK);
    }

    public void call(final Player playerVar, final FunctionType functionType) {
        call(playerVar, functionType, Collections.emptyList());
    }

    public void call(final Player playerVar, final FunctionType functionType, final List<Value> values) {
        if (state != State.LOADED) return;
        try {
            final VynCallable callable = callables.get(functionType);
            if (callable != null)
                callable.call(environment, values);
        } catch (final Exception e) {
            state = State.ERROR;
            if(playerVar != null) playerVar.sendMessage("§6(" + filename + ") §cError in script during "+ functionType.getName() + ": " + e.getMessage());
            else System.out.println("[InteractiveStuff] (" + filename + ") Error in script during "+ functionType.getName() + ": " + e.getMessage());
        }
    }

    public State getState() {
        return state;
    }

    public String getCode() {
        return code;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public enum State {
        LOADED,
        IMPORTABLE,
        ERROR
    }

    public enum FunctionType {
        ON_TICK("onTick"),
        ON_ITEM_UPDATE("onItemUpdate"),
        ON_SWING_HAND("onSwingHand"),
        ON_PLAY_SOUND("onPlaySound"),
        ON_LOAD("onLoad");

        private final String name;
        
        FunctionType(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}