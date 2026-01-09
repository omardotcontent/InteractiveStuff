package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import omar.projects.interactivestuff.scripts.objects.PackScripts;
import omar.projects.interactivestuff.scripts.objects.Script;

import java.util.Map;

public final class ExcludeScript extends NativeFunction {
    public ExcludeScript(final Map<String, PackScripts> scripts) {
        super((env, args) -> {
            if (args.size() != 2) {
                throw new RuntimeException("excludeScript requires 2 arguments: packid (String), name (String)");
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
            packScripts.excludeScript(script);
            return null;
        });
    }
}
