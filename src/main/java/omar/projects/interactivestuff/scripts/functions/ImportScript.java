package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.Importer;
import omar.projects.interactivestuff.scripts.exceptions.DelayedScriptException;
import omar.projects.interactivestuff.scripts.objects.PackScripts;
import omar.projects.interactivestuff.scripts.objects.Script;

import java.util.Map;

public final class ImportScript extends NativeFunction {
    public ImportScript(final Map<String, PackScripts> scripts) {
        super((env, args) -> {
            if (args.size() != 2) {
                throw new RuntimeException("importScript requires 2 arguments: packid (String), name (String)");
            }
            final String packId = args.get(0).toString();
            final String name = args.get(1).toString();
            final PackScripts packScripts = scripts.get(packId);
            if (packScripts == null) {
                throw new RuntimeException("No scripts found for pack ID: " + packId);
            }
            final Script script = packScripts.getScript(name);
            if (script == null) {
                throw new RuntimeException("No script named '" + name + "' found in pack ID: " + packId);
            }

            if (script.getState() != Script.State.IMPORTABLE) {
                throw new DelayedScriptException("Script '" + name + "' in pack ID: " + packId + " is not Importable.");
            }
            Importer.loadFromLines(script.getCode(), env);
            return null;
        });
    }
}

