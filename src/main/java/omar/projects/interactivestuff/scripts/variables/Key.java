package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;
import net.minecraft.network.chat.Component;

@VynType(name = "Key")
public final class Key {

    public Key() {}

    @VynFunc
    public String getTranslatedKey(final String key) {
        return Component.translatable(key).toString();
    }

}
