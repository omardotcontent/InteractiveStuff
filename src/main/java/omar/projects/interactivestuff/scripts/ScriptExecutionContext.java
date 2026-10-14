package omar.projects.interactivestuff.scripts;

import omar.projects.interactivestuff.scripts.objects.Script;

/** Tracks ownership for deferred callbacks without retaining a global render model. */
public final class ScriptExecutionContext {
    private static final ThreadLocal<Script> CURRENT = new ThreadLocal<>();

    private ScriptExecutionContext() {}

    public static Script current() { return CURRENT.get(); }

    public static void run(Script script, Runnable action) {
        Script previous = CURRENT.get();
        CURRENT.set(script);
        try {
            action.run();
        } finally {
            if (previous == null) CURRENT.remove(); else CURRENT.set(previous);
        }
    }

    public static Runnable deferred(Runnable action) {
        Script owner = CURRENT.get();
        if (owner == null) return action;
        long generation = owner.getGeneration();
        return () -> {
            // Config-only scripts remain importable but can own committed change callbacks.
            if ((owner.getState() != Script.State.LOADED && owner.getState() != Script.State.IMPORTABLE)
                    || generation != owner.getGeneration()) return;
            try {
                run(owner, action);
            } catch (Exception error) {
                owner.fail("wait", error);
            }
        };
    }
}
