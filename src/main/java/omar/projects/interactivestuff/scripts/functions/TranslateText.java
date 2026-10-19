package omar.projects.interactivestuff.scripts.functions;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import net.minecraft.network.chat.Component;

/** Pack-owned language keys use Minecraft's normal selected-language and English fallback rules. */
public final class TranslateText extends NativeFunction {
    public TranslateText() {
        super((environment, args) -> {
            if (args.isEmpty()) throw new IllegalArgumentException("translateText requires a translation key and optional format arguments");
            Object[] values = args.subList(1, args.size()).stream().map(Value::toString).toArray();
            return new Value(Component.translatable(args.get(0).toString(), values).getString());
        });
    }
}
