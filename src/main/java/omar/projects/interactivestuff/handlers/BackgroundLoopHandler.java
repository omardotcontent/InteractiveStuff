package omar.projects.interactivestuff.handlers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import omar.projects.interactivestuff.IS;
import omar.projects.interactivestuff.runtime.TickScheduler;

/** Fabric lifecycle adapter for the version-independent tick scheduler. */
public final class BackgroundLoopHandler {
    private static final BackgroundLoopHandler INSTANCE = new BackgroundLoopHandler();
    private final TickScheduler scheduler = new TickScheduler(error -> IS.LOGGER.error("Scheduled task failed", error));

    private BackgroundLoopHandler() {}
    public static BackgroundLoopHandler getInstance() { return INSTANCE; }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null && !client.isPaused()) scheduler.tick();
        });
    }

    public void startLoop(String name, Runnable task, int tickDelay) { scheduler.repeat(name, tickDelay, task); }
    public void waitTicks(String id, int ticks, Runnable task) { scheduler.once(id, ticks, task); }
    public void waitTicks(int ticks, Runnable task) { scheduler.once(ticks, task); }
    public void pauseLoop(String name) { scheduler.pause(name); }
    public void resumeLoop(String name) { scheduler.resume(name); }
    public void endLoop(String name) { scheduler.cancel(name); }
    public boolean isLoopRunning(String name) { return scheduler.contains(name); }
    public void clearAll() { scheduler.clear(); }
}
