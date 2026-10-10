package omar.projects.interactivestuff.scripts.objects;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/** Full relative paths prevent scripts in different subfolders from overwriting each other. */
public final class PackScripts {
    private final Map<String, Script> scripts = new TreeMap<>();

    public void addScript(String name, Script script) { scripts.put(name, script); }
    public Collection<Script> scripts() { return scripts.values(); }

    public Script getScript(String name) {
        Script exact = scripts.get(name);
        if (exact != null) return exact;
        Script match = null;
        for (Map.Entry<String, Script> entry : scripts.entrySet()) {
            if (entry.getKey().endsWith("/" + name)) {
                if (match != null) throw new IllegalArgumentException("Ambiguous script name: " + name + "; use its relative path");
                match = entry.getValue();
            }
        }
        return match;
    }
}
