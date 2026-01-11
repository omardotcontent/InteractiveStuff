package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;

@VynType(name = "InteractiveStuffConfig")
public final class InteractiveStuffConfig {

    @VynFunc
    public boolean isResourcePackDebugModeEnabled () {return ConfigHandler.INSTANCE.resourcePackDebugMode;}

    @VynFunc
    public boolean isInteractiveHitsEnabled() {
        return ConfigHandler.INSTANCE.enableInteractiveHits;
    }

    @VynFunc
    public boolean isSculkSensorFeatureEnabled() {
        return ConfigHandler.INSTANCE.enableSculkSensorFeature;
    }

    @VynFunc
    public int getHitCooldownTicks() {
        return ConfigHandler.INSTANCE.HitCooldownTicks;
    }

    @VynFunc
    public boolean isTextureChangesEnabled() {
        return ConfigHandler.INSTANCE.enableTextureChanges;
    }

    @VynFunc
    public boolean isNoteBlockCrouchFeatureEnabled() {
        return ConfigHandler.INSTANCE.enableNoteBlockCrouchFeature;
    }

    @VynFunc
    public int getCooldownTicks() {
        return ConfigHandler.INSTANCE.HitCooldownTicks;
    }

    @VynFunc
    public boolean isSpecializedNoteblockHitsEnabled() {
        return ConfigHandler.INSTANCE.specializedNoteblockHits;
    }
}