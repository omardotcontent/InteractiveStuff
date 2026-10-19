package omar.projects.interactivestuff.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;

/** Version-dependent client access kept outside the shared script runtime. */
public final class MinecraftAccess {
    private MinecraftAccess() {}
    public static String gameVersion() { return net.minecraft.SharedConstants.getCurrentVersion().getName(); }
    public static void openDirectory(java.nio.file.Path path) { net.minecraft.Util.getPlatform().openFile(path.toFile()); }
    public static void chat(LocalPlayer player, net.minecraft.network.chat.Component message) { player.displayClientMessage(message, false); }
    public static long dayTime(net.minecraft.world.level.Level level) { return level.getDayTime(); }
    public static int foliageTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor(level, pos); }
    public static int grassTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return net.minecraft.client.renderer.BiomeColors.getAverageGrassColor((net.minecraft.world.level.BlockAndTintGetter) level, pos); }
    public static int dryFoliageTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor((net.minecraft.world.level.BlockAndTintGetter) level, pos); }
    public static int waterTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return net.minecraft.client.renderer.BiomeColors.getAverageWaterColor((net.minecraft.world.level.BlockAndTintGetter) level, pos); }
    public static boolean isDay(net.minecraft.world.level.Level level) { return level.isDay(); }
    public static net.minecraft.world.item.ItemStack activeItem(LocalPlayer player) { return player.getUseItem(); }
    public static void openScreen(Minecraft client, Screen screen) { client.setScreen(screen); }
    public static boolean isFlyingVehicle(LocalPlayer player) { return false; }
    public static boolean isJumping(LocalPlayer player) { return ((omar.projects.interactivestuff.mixin.player.LivingEntityAccessor) player).interactivestuff$isJumping(); }
    public static boolean isInClouds(LocalPlayer player) {
        if (!(player.level() instanceof net.minecraft.client.multiplayer.ClientLevel level)) return false;
        float height = level.effects().getCloudHeight();
        return !Float.isNaN(height) && player.getY() + player.getBbHeight() >= height && player.getY() <= height + 4;
    }
    public static Screen screen(Minecraft client) { return client.screen; }
    public static Camera camera(Minecraft client) { return client.gameRenderer.getMainCamera(); }
    public static net.minecraft.world.phys.Vec3 cameraPosition(Camera camera) { return camera.getPosition(); }
    public static int permissionLevel(LocalPlayer player) {
        if (player == null) return 0;
        for (int level=4; level>0; level--) if (player.hasPermissions(level)) return level;
        return 0;
    }
}
