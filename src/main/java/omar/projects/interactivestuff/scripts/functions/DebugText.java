package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import omar.projects.interactivestuff.scripts.handlers.DebugTextHandler;

public class DebugText extends NativeFunction {

    private static final DebugTextHandler debugTextHandler = new DebugTextHandler();

    public DebugText() {
        super((env, args) -> {
            if (args.size() != 1) {
                throw new RuntimeException("excludeScript requires 1 arguments: text (String)");
            }
            final String text = args.getFirst().toString();
            debugTextHandler.addText(text);
            return null;
        });
    }
}