package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.config.PackOptions;
import omar.projects.interactivestuff.scripts.ScriptExecutionContext;

@VynType(name = "InteractiveStuffConfig")
public final class InteractiveStuffConfig {
    private static boolean packToggle(String id) {
        var owner = ScriptExecutionContext.current();
        return owner == null || PackOptions.toggleValue(owner.getPackId(), id, true);
    }
    private static boolean packToggle(String id, String legacyId) {
        var owner = ScriptExecutionContext.current();
        return owner == null || PackOptions.toggleValue(owner.getPackId(), id,
                PackOptions.toggleValue(owner.getPackId(), legacyId, true));
    }
    @VynFunc public boolean isParticlesEnabled() { return ConfigHandler.INSTANCE.enableParticles; }
    @VynFunc public int getParticleLimit() { return ConfigHandler.INSTANCE.particleLimit; }
    @VynFunc public int getParticleDensity() { return ConfigHandler.INSTANCE.particleDensity; }
    @VynFunc public boolean isParticleDebugCountersEnabled() { return omar.projects.interactivestuff.config.PackDebugOptions.currentCounters(); }

    @VynFunc
    public boolean isResourcePackDebugModeEnabled () {return omar.projects.interactivestuff.config.PackDebugOptions.currentMode();}

    @VynFunc
    public boolean isInteractiveHitsEnabled() {
        return ConfigHandler.INSTANCE.enableInteractiveHits;
    }

    /** @deprecated Declare and read a pack-owned ConfigOption instead. */
    @Deprecated @VynFunc
    public boolean isSculkSensorFeatureEnabled() {
        return packToggle("animate_sculk_sensor", "sculk_sensors");
    }

    @VynFunc
    public int getHitCooldownTicks() {
        return ConfigHandler.INSTANCE.HitCooldownTicks;
    }

    /** @deprecated Declare and read a pack-owned ConfigOption instead. */
    @Deprecated @VynFunc
    public boolean isTextureChangesEnabled() {
        return packToggle("texture_changes");
    }

    /** @deprecated Declare and read a pack-owned ConfigOption instead. */
    @Deprecated @VynFunc
    public boolean isNoteBlockCrouchFeatureEnabled() {
        return packToggle("note_block_sounds", "note_block_crouch");
    }

    @VynFunc
    public int getCooldownTicks() {
        return ConfigHandler.INSTANCE.HitCooldownTicks;
    }

    /** @deprecated Declare and read a pack-owned ConfigOption instead. */
    @Deprecated @VynFunc
    public boolean isSpecializedNoteblockHitsEnabled() {
        return packToggle("note_block_sounds", "note_block_hits");
    }

    @VynFunc
    public boolean isResourcePackMatrixEditingEnabled() {
        return ConfigHandler.INSTANCE.resourcePackMatrixEditing;
    }

    @VynFunc
    public boolean isResourcePackColorChangingEnabled() {
        return ConfigHandler.INSTANCE.resourcePackColorChanging;
    }
}
