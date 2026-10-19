package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import omar.projects.interactivestuff.config.PackDebugOptions;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;

public final class DebugText extends NativeFunction {

    private static final DebugTextHandler DEBUG_TEXT_HANDLER = new DebugTextHandler();

    public DebugText() {
        super((env, args) -> {
            if (args.size() != 1 && args.size() != 2) {
                throw new IllegalArgumentException("debugText requires text (String) and an optional visible (Boolean)");
            }
            boolean visible = args.size() == 2 ? args.get(1).asBoolean() : PackDebugOptions.currentMode();
            if (visible) DEBUG_TEXT_HANDLER.addText(args.get(0).toString());
            return null;
        });
    }
}
