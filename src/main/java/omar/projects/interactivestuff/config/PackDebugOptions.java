package omar.projects.interactivestuff.config;

import omar.projects.interactivestuff.scripts.ScriptExecutionContext;

/** Vyn text visibility belongs to the calling resource pack; marker controls belong to the mod. */
public final class PackDebugOptions {
    public static final String MODE = "debug_mode", COUNTERS = "debug_counters";
    private PackDebugOptions() {}
    private static String currentPack() {
        var script = ScriptExecutionContext.current();
        return script == null ? null : script.getPackId();
    }
    public static boolean currentMode() { return mode(currentPack()); }
    public static boolean currentCounters() { return counters(currentPack()); }
    public static boolean mode(String pack) { return pack != null && PackOptions.toggleValue(pack, MODE, false); }
    public static boolean counters(String pack) { return pack != null && PackOptions.toggleValue(pack, COUNTERS, false); }
}
