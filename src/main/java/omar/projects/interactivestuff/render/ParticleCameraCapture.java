package omar.projects.interactivestuff.render;

import net.minecraft.client.Minecraft;
import omar.projects.interactivestuff.compat.MinecraftAccess;
import omar.projects.interactivestuff.particles.ParticleCamera;
import omar.projects.interactivestuff.particles.ParticleSystem;

/** Capture the interpolated native camera at both hand binding and particle submission. */
public final class ParticleCameraCapture {
    private ParticleCameraCapture() {}

    public static ParticleCamera capture() {
        var camera = MinecraftAccess.camera(Minecraft.getInstance());
        var position = MinecraftAccess.cameraPosition(camera);
        return new ParticleCamera(position.x, position.y, position.z, camera.rotation());
    }

    public static void update() { ParticleSystem.INSTANCE.setCamera(capture()); }
}
