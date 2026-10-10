package omar.projects.interactivestuff.scripts.Utilities;

import omar.projects.interactivestuff.runtime.FrameClock;

public final class RenderTickHandler {
    private static final FrameClock CLOCK = new FrameClock();
    public static float normalizedDelta = 1.0f;

    private RenderTickHandler() {}

    public static void beginFrame(boolean paused) {
        normalizedDelta = CLOCK.advance(System.nanoTime(), paused);
    }

    public static long getFrameId() { return CLOCK.frame(); }
    public static void reset() { CLOCK.reset(); normalizedDelta = 1.0f; }
}
