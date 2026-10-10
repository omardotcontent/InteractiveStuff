package omar.projects.interactivestuff.scripts.handlers;

import java.util.ArrayList;
import java.util.List;

public final class DebugTextHandler {
    public static DebugTextHandler INSTANCE;
    private static List<String> renderedTexts = List.of();
    private final List<String> textBuffer = new ArrayList<>();

    public DebugTextHandler() { INSTANCE = this; }
    public static List<String> getRenderedTexts() { return renderedTexts; }
    public void addText(String text) { textBuffer.add(text); }
    public void onTickEnd() {
        renderedTexts = List.copyOf(textBuffer);
        textBuffer.clear();
    }
    public void clear() {
        textBuffer.clear();
        renderedTexts = List.of();
    }
}
