package omar.projects.interactivestuff.render;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import omar.projects.interactivestuff.particles.ParticleCamera;
import omar.projects.interactivestuff.particles.ParticleSystem;
import org.joml.Quaternionf;

/** Converts the legacy native camera into the particle renderer's view frame. */
public final class ParticleCameraCapture {
    private ParticleCameraCapture() {}

    public static ParticleCamera capture() { return capture(Minecraft.getInstance().gameRenderer.getMainCamera()); }

    static ParticleCamera capture(Camera camera) {
        var position = camera.getPosition();
        // 1.20.1 rotates local +Z forward and +X left; render view uses -Z forward and +X right.
        // Post-multiply in the camera's local frame, preserving its native yaw, pitch and quaternion.
        var rotation = new Quaternionf(camera.rotation()).rotateY((float) Math.PI);
        return new ParticleCamera(position.x, position.y, position.z, rotation);
    }

    public static void update() { ParticleSystem.INSTANCE.setCamera(capture()); }
}
