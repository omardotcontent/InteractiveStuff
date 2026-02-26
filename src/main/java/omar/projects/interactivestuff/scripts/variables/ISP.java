package omar.projects.interactivestuff.scripts.variables;


import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import omar.projects.interactivestuff.acessor.CameraVelocityAccessor;

@VynType(name = "ISP")
@SuppressWarnings("unused")
public class ISP {

    private ClientPlayerEntity player;
    private final MinecraftClient client;

    public ISP(final ClientPlayerEntity player, final MinecraftClient client) {
        this.player = player;
        this.client = client;
    }

    @VynFunc
    public boolean isMainHand(final ItemModel item) {
        return player != null && item != null && player.getMainHandStack().equals(item.getFinalStack());
    }

    @VynFunc
    public boolean isOffHand(final ItemModel item) {
        return player != null && item != null && player.getOffHandStack().equals(item.getFinalStack());
    }

    @VynFunc
    public boolean isHoldingItem(final String itemId) {
        final ItemModel mainHandItem = getMainHandItem();
        final ItemModel offHandItem = getOffHandItem();
        final String mainItemName = mainHandItem == null ? "AIR" : mainHandItem.getName();
        final String offItemName = offHandItem == null ? "AIR" : offHandItem.getName();
        return mainItemName.equals(itemId) || offItemName.equals(itemId);
    }

    @VynFunc
    public float getCameraPitchVelocity() {
        if (client.gameRenderer == null) {
            return 0.0f;
        }
        final Camera camera = client.gameRenderer.getCamera();
        if (camera instanceof CameraVelocityAccessor acc) {
            return acc.interactivestuff$getPitchVelocity();
        }
        return 0.0f;
    }

    @VynFunc
    public float getCameraYawVelocity() {
        if (client.gameRenderer == null) {
            return 0.0f;
        }
        final Camera camera = client.gameRenderer.getCamera();
        if (camera instanceof CameraVelocityAccessor acc) {
            return acc.interactivestuff$getYawVelocity();
        }
        return 0.0f;
    }

    @VynFunc
    public ItemModel getMainHandItem() {
        return (player != null) ? new ItemModel(player.getMainHandStack()) : null;
    }

    @VynFunc
    public ItemModel getOffHandItem() {
        return (player != null) ? new ItemModel(player.getOffHandStack()) : null;
    }



    public void setPlayer(final ClientPlayerEntity player) {
        this.player = player;
    }


}
