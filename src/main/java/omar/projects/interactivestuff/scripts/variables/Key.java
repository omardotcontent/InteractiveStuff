package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynType;
import net.minecraft.text.Text;

@VynType(name = "Key")
public final class Key {

    public Key() {}

    public String getTranslatedKey(final String key) {
        return Text.translatable(key).toString();
    }


}
