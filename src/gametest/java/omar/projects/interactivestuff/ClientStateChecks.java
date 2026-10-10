package omar.projects.interactivestuff;

import net.minecraft.client.Minecraft;
import omar.projects.interactivestuff.scripts.variables.Player;

/** Native state setters test the shared binding against each selected Minecraft API. */
final class ClientStateChecks {
    static void run(Minecraft client) {
        Player binding = new Player(client.player, client);
        client.player.setJumping(true);
        try {
            if (!binding.isJumping()) throw new AssertionError("jump binding observes native state");
        } finally { client.player.setJumping(false); }
        if (binding.isJumping()) throw new AssertionError("jump binding observes native state reset");
        if (binding.isFlyingVehicle()) throw new AssertionError("the local player is not a flying vehicle");
        if (binding.isAtCloudHeight()) throw new AssertionError("the ground-level fixture is outside clouds");
    }
}
