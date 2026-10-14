package omar.projects.interactivestuff.render;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import omar.projects.interactivestuff.particles.ParticleCamera;
import omar.projects.interactivestuff.particles.ParticleSystem;

/** This camera already uses render-view axes; do not inherit 1.20.1's basis conversion. */
public final class ParticleCameraCapture {
    private ParticleCameraCapture() {}

    public static ParticleCamera capture() { return capture(Minecraft.getInstance().gameRenderer.getMainCamera()); }

    static ParticleCamera capture(Camera camera) {
        var position = camera.getPosition();
        return new ParticleCamera(position.x, position.y, position.z, camera.rotation());
    }

    public static void update() { ParticleSystem.INSTANCE.setCamera(capture()); }
}
