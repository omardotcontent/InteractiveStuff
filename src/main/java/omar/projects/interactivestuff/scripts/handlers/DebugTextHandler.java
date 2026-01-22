package omar.projects.interactivestuff.scripts.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DebugTextHandler {

    public static volatile DebugTextHandler INSTANCE;

    private volatile List<String> renderedTexts = Collections.emptyList();
    private final List<String> textBuffer = new CopyOnWriteArrayList<>();

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
        this.renderedTexts = new ArrayList<>(textBuffer);
        textBuffer.clear();
    }
}
