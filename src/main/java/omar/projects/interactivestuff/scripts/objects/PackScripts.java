package omar.projects.interactivestuff.scripts.objects;


import omar.projects.interactivestuff.scripts.variables.Player;

import java.util.*;

public final class PackScripts {
    private final Map<String, Script> scripts;

    public PackScripts() {
        scripts = new HashMap<>();
    }

    public void addScript(final String code, final String fileName) {
       scripts.put(fileName, new Script(code, fileName));
    }

    public void loadScripts(final Player playerVar, final Set<Script> globalScripts, final Deque<Script> delayedScripts) {
        for (final Script script: scripts.values()) {
            script.load(playerVar, globalScripts, delayedScripts);
        }
    }

    public void excludeScript(final Script script) {
        script.setState(Script.State.EXCLUDED);
    }


    public Map<String, Script> getScripts() {
        return Collections.unmodifiableMap(scripts);
    }

    public int getScriptCount() {
        return scripts.size();
    }

    public Script getScript(final String fileName) {
        return scripts.get(fileName);
    }
}
