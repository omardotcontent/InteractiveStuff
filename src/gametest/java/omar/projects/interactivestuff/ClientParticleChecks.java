package omar.projects.interactivestuff;

import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.CameraType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.particles.ParticleSystem;
import omar.projects.interactivestuff.render.ParticleTextures;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.objects.Script;
import omar.projects.interactivestuff.scripts.variables.Position;
import omar.projects.interactivestuff.scripts.variables.Sound;

/** Runs inside the existing disposable world; all geometry is drawn by the real native render pass. */
public final class ClientParticleChecks {
    private ClientParticleChecks() {}
    public static void run(ClientGameTestContext context) {
        var config = ConfigHandler.INSTANCE;
        boolean enabled = config.enableParticles, markers = config.particleEmitterMarkers, counters = config.particleDebugCounters;
        int density = config.particleDensity, limit = config.particleLimit;
        context.runOnClient(client -> {
            config.enableParticles = true; config.particleDensity = 100; config.particleLimit = 512;
            config.particleEmitterMarkers = true; config.particleDebugCounters = true;
            client.options.setCameraType(CameraType.FIRST_PERSON);
            client.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TORCH));
            client.player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SOUL_TORCH));
        });
        try {
            context.waitTicks(40);
            context.takeScreenshot("interactivestuff-particles-torches-left");
            context.runOnClient(client -> {
                healthyScripts();
                var draws = ParticleSystem.INSTANCE.snapshot(false);
                check(draws.stream().anyMatch(p -> p.type().equals("minecraft:flame")), "ordinary flame emitted");
                check(draws.stream().anyMatch(p -> p.type().equals("minecraft:soul_fire_flame")), "off-hand soul flame emitted independently");
                check(draws.stream().anyMatch(p -> p.type().equals("minecraft:smoke")), "detached torch smoke emitted");
                for (var particle : draws) check(particle.position().isFinite() && particle.position().z < 0, "particle anchor is in front of the view camera");
            });
            context.runOnClient(client -> {
                client.options.mainHand().set(HumanoidArm.RIGHT);
                client.options.broadcastOptions();
            });
            context.waitFor(client -> client.player.getMainArm() == HumanoidArm.RIGHT);
            context.waitTicks(12);
            context.takeScreenshot("interactivestuff-particles-torches-right");
            context.runOnClient(client -> client.player.setYRot(client.player.getYRot() + 90));
            context.waitTicks(12);
            context.runOnClient(client -> check(ParticleSystem.INSTANCE.snapshot(false).stream()
                    .allMatch(p -> p.position().z < 0), "particles stay in view coordinates after rotating the camera"));
            context.runOnClient(client -> {
                client.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.ENCHANTING_TABLE));
                client.player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            });
            context.waitTicks(40);
            context.runOnClient(client -> {
                healthyScripts();
                check(ParticleSystem.INSTANCE.snapshot(false).stream().anyMatch(p -> p.type().equals("minecraft:enchant")), "animated enchanting glyphs emitted");
            });
            context.takeScreenshot("interactivestuff-particles-enchanting");
            for (var sensor : new net.minecraft.world.item.Item[]{Items.SCULK_SENSOR, Items.CALIBRATED_SCULK_SENSOR}) {
                context.runOnClient(client -> client.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(sensor)));
                context.waitTicks(8);
                context.runOnClient(client -> ScriptInterpreter.onPlaySound(new Sound("minecraft:entity.experience_orb.pickup", 1, 1,
                        new Position(client.player.blockPosition().getX(), client.player.blockPosition().getY(), client.player.blockPosition().getZ()))));
                context.waitTicks(5);
                context.runOnClient(client -> {
                    healthyScripts();
                    check(ParticleSystem.INSTANCE.snapshot(false).stream().anyMatch(p -> p.type().equals("minecraft:sculk_charge_pop")), "active sculk sensor emits charge particles: " + sensor);
                });
                context.takeScreenshot("interactivestuff-particles-" + sensor.toString().replace(':', '-'));
                context.waitTicks(45);
            }
            context.runOnClient(client -> {
                ScriptInterpreter.addScript("interactivestuff_test", "particles", """
                        lock interactiveItems = ["minecraft:stone"]
                        make sparks = new ParticleEmitter("interactivestuff_test:spark")
                        sparks.setPosition(0.5, 0.85, 0.5)
                        sparks.setSpread(0.2, 0.05, 0.2)
                        sparks.setRate(1)
                        sparks.setLifetime(12)
                        sparks.setSizeOverLife(0.08, 0.02, "easeOutQuad")
                        sparks.setOpacityOverLife(1, 0, "easeInSine")
                        sparks.setAnimation("loop", 2)
                        sparks.setDepthTest(false)
                        sparks.setLight(15)
                        task onItemUpdate takes item do sparks.attach(item) end
                        """);
                ScriptInterpreter.loadScripts();
                client.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));
            });
            context.waitTicks(20);
            context.runOnClient(client -> {
                healthyScripts();
                var draws = ParticleSystem.INSTANCE.snapshot(false);
                check(draws.size() >= 6 && draws.stream().allMatch(p -> !p.depth()), "custom overlay texture particles render through the native adapter");
                check(draws.stream().map(ParticleTextures::texture).allMatch(id -> id.getNamespace().equals("interactivestuff_test")), "custom particle JSON resolves its own PNG frames");
                client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            });
            context.waitTicks(2);
            context.runOnClient(client -> check(ParticleSystem.INSTANCE.snapshot(true).isEmpty(), "particles are hidden outside first-person view"));
            context.runOnClient(client -> client.options.setCameraType(CameraType.FIRST_PERSON));
            context.waitTicks(8);
            checkVisibleTexture(context.takeScreenshot("interactivestuff-particles-custom"));
            context.runOnClient(client -> {
                config.enableParticles = false;
                client.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TORCH));
            });
            context.waitTicks(2);
            context.runOnClient(client -> check(ParticleSystem.INSTANCE.getCount() == 0, "global setting clears live particles"));
        } finally {
            context.runOnClient(client -> {
                config.enableParticles = enabled; config.particleEmitterMarkers = markers; config.particleDebugCounters = counters;
                config.particleDensity = density; config.particleLimit = limit;
                client.options.setCameraType(CameraType.FIRST_PERSON);
            });
        }
        IS.LOGGER.info("InteractiveStuff native first-person particle checks passed.");
    }

    private static void healthyScripts() {
        for (var pack : ScriptInterpreter.getPackScripts().values()) for (var script : pack.scripts())
            check(script.getState() != Script.State.ERROR, "healthy particle script: " + script.getFilename());
    }
    private static void checkVisibleTexture(java.nio.file.Path screenshot) {
        try {
            var image = javax.imageio.ImageIO.read(screenshot.toFile());
            int colored = 0;
            for (int y = image.getHeight() * 2 / 5; y < image.getHeight() * 94 / 100; y++) {
                for (int x = image.getWidth() * 55 / 100; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    int red = pixel >> 16 & 255, green = pixel >> 8 & 255, blue = pixel & 255;
                    if (red > 200 && green > 60 && green < 160 && blue < 80
                            || red < 90 && green > 100 && blue > 180) colored++;
                }
            }
            check(colored >= 12, "custom PNG particles are visible beside the right-hand item after camera rotation");
        } catch (java.io.IOException error) { throw new AssertionError("Cannot read particle screenshot", error); }
    }
    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
