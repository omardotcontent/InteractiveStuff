package omar.projects.interactivestuff.handlers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BackgroundLoopHandler {

    private static final BackgroundLoopHandler INSTANCE = new BackgroundLoopHandler();

    private final Map<String, BackgroundLoop> loops = new ConcurrentHashMap<>();

    private BackgroundLoopHandler() {
    }

    public static BackgroundLoopHandler getInstance() {
        return INSTANCE;
    }

    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) {
                return;
            }
            if (client.isPaused()) {
                return;
            }
            loops.values().forEach(BackgroundLoop::tick);
        });
    }

    public void startLoop(final String name, final Runnable task, final int tickDelay) {
        if (loops.containsKey(name)) {
            endLoop(name);
        }
        loops.put(name, new BackgroundLoop(task, tickDelay));
    }

    public void waitTicks(final String id, final int ticks, final Runnable task) {
        if (isLoopRunning(id)) {
            return;
        }
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
        if (loop == null) {
            return;
        }
        loop.pause();
    }

    public void resumeLoop(final String name) {
        final BackgroundLoop loop = loops.get(name);
        if (loop == null) {
            return;
        }
        loop.resume();
    }

    public void endLoop(final String name) {
        final BackgroundLoop loop = loops.remove(name);
        if (loop == null) {
            return;
        }
        loop.stop();
    }

    public boolean isLoopRunning(final String name) {
        return loops.containsKey(name);
    }

    public void clearAll() {
        loops.values().forEach(BackgroundLoop::stop);
        loops.clear();
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
            if (tickCounter < tickDelay) {
                return;
            }
            tickCounter = 0;
            try {
                task.run();
            } catch (final Throwable t) {
                t.printStackTrace();
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