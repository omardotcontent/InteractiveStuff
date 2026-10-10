package omar.projects.interactivestuff.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Consumer;

/** Client-thread scheduler with immutable queue entries and safe reentrant callbacks. */
public final class TickScheduler {
    private final Map<String, Task> named = new HashMap<>();
    private final PriorityQueue<Entry> queue = new PriorityQueue<>();
    private final Consumer<Throwable> errors;
    private long tick;
    private long sequence;
    private long epoch;

    public TickScheduler(Consumer<Throwable> errors) {
        this.errors = Objects.requireNonNull(errors);
    }

    public void repeat(String name, int delay, Runnable action) {
        Objects.requireNonNull(name);
        cancel(name);
        add(name, delay, true, action);
    }

    public void once(String name, int delay, Runnable action) {
        Objects.requireNonNull(name);
        if (!named.containsKey(name)) add(name, delay, false, action);
    }

    public void once(int delay, Runnable action) {
        add(null, delay, false, action);
    }

    private void add(String name, int delay, boolean repeating, Runnable action) {
        Task task = new Task(name, Math.max(1, delay), repeating, Objects.requireNonNull(action), epoch);
        if (name != null) named.put(name, task);
        enqueue(task, tick + task.delay);
    }

    private void enqueue(Task task, long due) {
        task.due = due;
        queue.add(new Entry(due, sequence++, task, task.generation));
    }

    public void tick() {
        tick++;
        while (!queue.isEmpty() && queue.peek().due <= tick) {
            Entry entry = queue.remove();
            Task task = entry.task;
            if (task.cancelled || task.paused || task.epoch != epoch || entry.generation != task.generation) continue;
            if (!task.repeating && task.name != null) named.remove(task.name, task);
            long generation = task.generation;
            try {
                task.action.run();
            } catch (Exception error) {
                task.cancelled = true;
                if (task.name != null) named.remove(task.name, task);
                errors.accept(error);
            }
            if (task.repeating && !task.cancelled && !task.paused && task.epoch == epoch
                    && task.generation == generation && named.get(task.name) == task) {
                enqueue(task, tick + task.delay);
            }
        }
    }

    public boolean contains(String name) { return named.containsKey(name); }

    public void cancel(String name) {
        Task task = named.remove(name);
        if (task != null) {
            task.cancelled = true;
            task.generation++;
        }
    }

    public void pause(String name) {
        Task task = named.get(name);
        if (task == null || task.paused) return;
        task.remaining = Math.max(1, task.due - tick);
        task.paused = true;
        task.generation++;
    }

    public void resume(String name) {
        Task task = named.get(name);
        if (task == null || !task.paused) return;
        task.paused = false;
        task.generation++;
        enqueue(task, tick + task.remaining);
    }

    public void clear() {
        epoch++;
        named.clear();
        queue.clear();
    }

    private static final class Task {
        final String name;
        final int delay;
        final boolean repeating;
        final Runnable action;
        final long epoch;
        long due;
        long generation;
        long remaining;
        boolean paused;
        boolean cancelled;

        Task(String name, int delay, boolean repeating, Runnable action, long epoch) {
            this.name = name;
            this.delay = delay;
            this.repeating = repeating;
            this.action = action;
            this.epoch = epoch;
        }
    }

    private record Entry(long due, long sequence, Task task, long generation) implements Comparable<Entry> {
        @Override public int compareTo(Entry other) {
            int byTime = Long.compare(due, other.due);
            return byTime != 0 ? byTime : Long.compare(sequence, other.sequence);
        }
    }
}
