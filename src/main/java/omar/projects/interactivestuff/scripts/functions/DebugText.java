package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;

public final class DebugText extends NativeFunction {

    private static final DebugTextHandler DEBUG_TEXT_HANDLER = new DebugTextHandler();

    public DebugText() {
        super((env, args) -> {
            if (!ConfigHandler.INSTANCE.resourcePackDebugMode) {
                return null;
            }
            if (args.size() != 1) {
                throw new RuntimeException("excludeScript requires 1 arguments: text (String)");
            }
            final String text = args.getFirst().toString();
            DEBUG_TEXT_HANDLER.addText(text);
            return null;
        });
    }
}