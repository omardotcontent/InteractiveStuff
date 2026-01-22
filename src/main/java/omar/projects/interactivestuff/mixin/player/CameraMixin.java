package omar.projects.interactivestuff.mixin.player;

import net.minecraft.client.render.Camera;
import omar.projects.interactivestuff.handlers.CameraVelocityAccessor;
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
    
    
    @Inject(method = "setRotation", at = @At("HEAD"))
    private void capturePreviousRotation(final float yaw, final float pitch, final CallbackInfo ci) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    @Override
    public float interactivestuff$getYawVelocity() {
        return this.yaw - this.lastYaw;
    }

    @Override
    public float interactivestuff$getPitchVelocity() {
        return this.pitch - this.lastPitch;
    }
}