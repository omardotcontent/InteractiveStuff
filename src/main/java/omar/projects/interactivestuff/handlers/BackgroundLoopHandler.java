package omar.projects.interactivestuff.handlers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BackgroundLoopHandler {

    private static final BackgroundLoopHandler INSTANCE = new BackgroundLoopHandler();
    // ConcurrentHashMap allows us to safely remove loops while iterating (mostly)
    private final Map<String, BackgroundLoop> loops = new ConcurrentHashMap<>();

    private BackgroundLoopHandler() {}

    public static BackgroundLoopHandler getInstance() {
        return INSTANCE;
    }

    public void register() {
        // END_CLIENT_TICK ensures vanilla logic has finished for the frame
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // 1. Check if the player is in a world (don't run in main menu)
            if (client.world == null) {
                return;
            }

            // 2. Check if the game is paused (Escape menu, etc.)
            // This satisfies your requirement: "when paused it pauses"
            if (client.isPaused()) {
                return;
            }

            // Only tick loops if the game is active and running
            loops.values().forEach(BackgroundLoop::tick);
        });
    }

    /**
     * Starts a repeating task.
     * @param name Unique ID for the loop
     * @param task The code to run
     * @param tickDelay How many ticks to wait between runs (20 ticks = 1 second)
     */
    public void startLoop(final String name, final Runnable task, final int tickDelay) {
        if (loops.containsKey(name)) {
            endLoop(name);
        }
        loops.put(name, new BackgroundLoop(task, tickDelay));
    }

    /**
     * schedules a task to run once after a specific delay.
     */
    public void waitTicks(final String id, final int ticks, final Runnable task) {
        if (isLoopRunning(id)) {
            return;
        }
        // Start a loop that runs the task once, then immediately kills itself
        startLoop(id, () -> {
            try {
                task.run();
            } finally {
                endLoop(id);
            }
        }, ticks);
    }

    public void pauseLoop(final String name) {
        final BackgroundLoop loop = loops.get(name);
        if (loop != null) {
            loop.pause();
        }
    }

    public void resumeLoop(final String name) {
        final BackgroundLoop loop = loops.get(name);
        if (loop != null) {
            loop.resume();
        }
    }

    public void endLoop(final String name) {
        final BackgroundLoop loop = loops.remove(name);
        if (loop != null) {
            loop.stop();
        }
    }

    public boolean isLoopRunning(final String name) {
        return loops.containsKey(name);
    }

    private static final class BackgroundLoop {

        private final Runnable task;
        private final int tickDelay;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final AtomicBoolean paused = new AtomicBoolean(false);
        private int tickCounter = 0;

        private BackgroundLoop(final Runnable task, final int tickDelay) {
            this.task = task;
            this.tickDelay = tickDelay;
        }

        public void tick() {
            if (!running.get() || paused.get()) {
                return;
            }

            tickCounter++;

            // Only run the task when the counter hits the delay
            if (tickCounter >= tickDelay) {
                tickCounter = 0; // Reset counter
                try {
                    task.run();
                } catch (final Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        public void pause() {
            paused.set(true);
        }

        public void resume() {
            paused.set(false);
        }

        public void stop() {
            running.set(false);
        }
    }
}