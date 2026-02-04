package omar.projects.interactivestuff.mixin.player;

import net.minecraft.client.render.Camera;
import omar.projects.interactivestuff.handlers.CameraVelocityAccessor;
import omar.projects.interactivestuff.scripts.Utilities.RenderTickHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraVelocityAccessor {
    @Shadow private float pitch;
    @Shadow private float yaw;

    @Unique
    private float lastPitch;

    @Unique
    private float lastYaw;

    @Unique
    private float smoothedPitchVelocity;

    @Unique
    private float smoothedYawVelocity;

    @Unique
    private static final float SMOOTHING_SPEED = 0.3f;

    
    @Inject(method = "setRotation", at = @At("HEAD"))
    private void capturePreviousRotation(final float yaw, final float pitch, final CallbackInfo ci) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    @Inject(method = "setRotation", at = @At("TAIL"))
    private void calculateSmoothedVelocity(final float yaw, final float pitch, final CallbackInfo ci) {
        float rawYawVelocity = this.yaw - this.lastYaw;
        float rawPitchVelocity = this.pitch - this.lastPitch;

        final float dt = RenderTickHandler.normalizedDelta;
        if (dt > 0) {
            rawYawVelocity /= dt;
            rawPitchVelocity /= dt;
        }

        final float smoothFactor = (float) (1.0 - Math.pow(1.0 - SMOOTHING_SPEED, dt));
        this.smoothedYawVelocity = this.smoothedYawVelocity + (rawYawVelocity - this.smoothedYawVelocity) * smoothFactor;
        this.smoothedPitchVelocity = this.smoothedPitchVelocity + (rawPitchVelocity - this.smoothedPitchVelocity) * smoothFactor;

        if (Math.abs(this.smoothedYawVelocity) < 0.01f) {
            this.smoothedYawVelocity = 0.0f;
        }
        if (Math.abs(this.smoothedPitchVelocity) < 0.01f) {
            this.smoothedPitchVelocity = 0.0f;
        }
    }

    @Override
    public float interactivestuff$getYawVelocity() {
        return this.smoothedYawVelocity;
    }

    @Override
    public float interactivestuff$getPitchVelocity() {
        return this.smoothedPitchVelocity;
    }
}