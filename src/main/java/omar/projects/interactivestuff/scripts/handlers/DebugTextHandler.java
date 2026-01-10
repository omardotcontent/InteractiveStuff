package omar.projects.interactivestuff.scripts.handlers;

import java.util.ArrayList;
import java.util.List;

public class DebugTextHandler {

    public static DebugTextHandler INSTANCE;

    private final List<String> renderedTexts = new ArrayList<>();

    private final List<String> textBuffer = new ArrayList<>();

    public DebugTextHandler() {
        INSTANCE = this;
    }

    public List<String> getRenderedTexts() {
        return renderedTexts;
    }

    public void addText(final String text) {
        textBuffer.add(text);
    }


    public void onTickEnd() {
        renderedTexts.clear();
        renderedTexts.addAll(textBuffer);
        textBuffer.clear();
    }
}
