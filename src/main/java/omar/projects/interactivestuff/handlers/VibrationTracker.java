package omar.projects.interactivestuff.handlers;

public final class VibrationTracker {

    private static boolean vibrating;
    private static boolean calibratedVibrating;

    public static boolean isVibrating() {
        return vibrating;
    }

    public static void setVibrating(final boolean active) {
        vibrating = active;
    }

    public static boolean isCalibratedVibrating() {
        return calibratedVibrating;
    }

    public static void setCalibratedVibrating(final boolean active) {
        calibratedVibrating = active;
    }
}