package omar.projects.interactivestuff.scripts.handlers;

import java.util.ArrayList;
import java.util.List;

public final class DebugTextHandler {
    public static DebugTextHandler INSTANCE;
    private static List<String> renderedTexts = List.of();
    private final List<String> textBuffer = new ArrayList<>();

    public DebugTextHandler() { INSTANCE = this; }
    public static List<String> getRenderedTexts() {
        var config = omar.projects.interactivestuff.handlers.config.ConfigHandler.INSTANCE;
        if (config == null) return renderedTexts;
        if (!config.particleDebugCounters) return config.resourcePackDebugMode ? renderedTexts : List.of();
        var result = new ArrayList<String>();
        if (config.resourcePackDebugMode) result.addAll(renderedTexts);
        var particles = omar.projects.interactivestuff.particles.ParticleSystem.INSTANCE;
        result.add("IS particles: " + particles.getCount() + "/" + config.particleLimit
                + "  emitters: " + particles.getEmitterCount() + "  dropped: " + particles.getDroppedCount());
        return result;
    }
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
