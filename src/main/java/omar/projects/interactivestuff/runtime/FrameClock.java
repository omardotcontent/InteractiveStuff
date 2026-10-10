package omar.projects.interactivestuff.runtime;

/** Monotonic timing independent of GLFW/SDL and Minecraft version changes. */
public final class FrameClock {
    private long previous = Long.MIN_VALUE;
    private long frame;

    public float advance(long nowNanos, boolean paused) {
        long elapsed = previous == Long.MIN_VALUE ? 16_666_667L : Math.max(0L, nowNanos - previous);
        previous = nowNanos;
        frame++;
        return paused ? 0f : (float) (Math.min(50_000_000L, elapsed) * 60.0 / 1_000_000_000.0);
    }

    public long frame() { return frame; }
    public void reset() { previous = Long.MIN_VALUE; }
}
