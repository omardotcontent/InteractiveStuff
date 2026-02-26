package omar.projects.interactivestuff;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import omar.projects.interactivestuff.scripts.functions.GetDelta;
import omar.projects.interactivestuff.scripts.variables.ISP;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import studio.meraki.vynapi.model.VynAddon;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class ISClient extends VynAddon implements ClientModInitializer {

    private static final NativeFunction GET_DELTA_FUNCTION = new GetDelta();
    private static final ISP PLAYER_VAR;

    static {
        PLAYER_VAR = new ISP(MinecraftClient.getInstance().player, MinecraftClient.getInstance());
        ClientPlayConnectionEvents.JOIN
                .register((handler, sender, client) -> PLAYER_VAR.setPlayer(client.player));
    }

    public ISClient() {
        super("onItemUpdate");
    }

    @Override
    public void onInitializeClient() {
    }

    @Override
    public Consumer<me.abdelaziz.runtime.Environment> onEnable() {
        return env -> {
            NativeBinder.defineConstant(env, "isp", PLAYER_VAR);
            env.defineFunction("getDelta", GET_DELTA_FUNCTION);
            NativeBinder.bind(env, ItemModel.class);
        };
    }

}