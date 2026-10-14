package omar.projects.interactivestuff.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;

/** Version-dependent client access kept outside the shared script runtime. */
public final class MinecraftAccess {
    private MinecraftAccess() {}
    public static void openDirectory(java.nio.file.Path path) { net.minecraft.util.Util.getPlatform().openFile(path.toFile()); }
    public static void chat(LocalPlayer player, net.minecraft.network.chat.Component message) { player.sendSystemMessage(message); }
    public static long dayTime(net.minecraft.world.level.Level level) { return level.getDefaultClockTime(); }
    public static int foliageTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return level.getClientLeafTintColor(pos); }
    public static int grassTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return net.minecraft.client.renderer.BiomeColors.getAverageGrassColor((net.minecraft.client.renderer.block.BlockAndTintGetter) level, pos); }
    public static int dryFoliageTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return net.minecraft.client.renderer.BiomeColors.getAverageDryFoliageColor((net.minecraft.client.renderer.block.BlockAndTintGetter) level, pos); }
    public static int waterTint(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return net.minecraft.client.renderer.BiomeColors.getAverageWaterColor((net.minecraft.client.renderer.block.BlockAndTintGetter) level, pos); }
    public static boolean isDay(net.minecraft.world.level.Level level) { return level.isBrightOutside(); }
    public static net.minecraft.world.item.ItemStack activeItem(LocalPlayer player) { return player.getActiveItem(); }
    public static void openScreen(Minecraft client, Screen screen) { client.setScreenAndShow(screen); }
    public static boolean isFlyingVehicle(LocalPlayer player) { return player.isFlyingVehicle(); }
    public static boolean isJumping(LocalPlayer player) { return player.isJumping(); }
    public static boolean isInClouds(LocalPlayer player) { return player.isInClouds(); }
    public static Screen screen(Minecraft client) { return client.gui.screen(); }
    public static Camera camera(Minecraft client) { return client.gameRenderer.mainCamera(); }
    public static net.minecraft.world.phys.Vec3 cameraPosition(Camera camera) { return camera.position(); }
    public static int permissionLevel(LocalPlayer player) {
        return player == null ? 0 : player.getPermissionContext().permissionLevel().id();
    }
}
