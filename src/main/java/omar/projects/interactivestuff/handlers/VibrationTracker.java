package omar.projects.interactivestuff.handlers;

public final class VibrationTracker {

    private static boolean vibrating;

    public static boolean isVibrating() {
        return vibrating;
    }

    public static void setVibrating(final boolean active) {
        vibrating = active;
    }
}