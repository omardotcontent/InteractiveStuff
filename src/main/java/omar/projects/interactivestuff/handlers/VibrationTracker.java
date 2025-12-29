package omar.projects.interactivestuff.handlers;

public final class VibrationTracker {

    private static float intensity = 0.0f;

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

    public static float getIntensity() {
        return intensity;
    }

    public static void pulse(final int ticksDuration) {
        intensity = 0.02f; // Adjust this value for Shake Strength

        BackgroundLoopHandler.getInstance().waitTicks(
                "vibration_stop_task",
                ticksDuration,
                () -> intensity = 0.0f // Reset to 0 after time passes
        );
    }


}