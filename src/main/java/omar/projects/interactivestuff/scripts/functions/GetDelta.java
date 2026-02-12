package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.runtime.Value;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;

public final class GetDelta extends NativeFunction {

    public GetDelta() {
        super((env, args) -> new Value(RenderTickHandler.normalizedDelta));
    }
}
