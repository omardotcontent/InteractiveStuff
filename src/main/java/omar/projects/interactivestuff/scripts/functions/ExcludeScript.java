package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import omar.projects.interactivestuff.scripts.ScriptRuntime;

public final class ExcludeScript extends NativeFunction {
    public ExcludeScript(ScriptRuntime scripts) {
        super((environment, args) -> {
            if (args.size() != 2) throw new IllegalArgumentException("excludeScript requires pack ID and script name");
            scripts.requireScript(args.get(0).toString(), args.get(1).toString()).exclude();
            return null;
        });
    }
}
