package omar.projects.interactivestuff.objects.special;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public final class PistonAnimation {

	private static float progress = 0f;
	private static float prevProgress = 0f;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(PistonAnimation::tick);
	}

	private static void tick(final MinecraftClient client) {
		if (client.player == null) return;

		prevProgress = progress;

		final boolean falling =
				client.player.getVelocity().y < -0.08 &&
						!client.player.isOnGround();

		progress += falling ? 0.08f : -0.08f;
		progress = MathHelper.clamp(progress, 0f, 1f);
	}

	public static float getProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, prevProgress, progress);
	}
}
