package omar.projects.interactivestuff.handlers.config;

import net.minecraft.locale.Language;
import net.minecraft.client.Minecraft;
import java.util.Locale;
import net.minecraft.network.chat.Component;

/** Resolve UI copy through Minecraft's current language; author/user text stays literal. */
public final class SettingsText {
    public static final String PREFIX = "interactive_stuff.config.menu.";
    private static String searchLanguage = "";
    private static Locale searchLocale = Locale.ROOT;
    private SettingsText() {}
    public static Component component(String key, Object... args) { return Component.translatable(PREFIX + key, args); }
    public static String text(String key, Object... args) { return component(key, args).getString(); }
    /** Locale casing for names, with ROOT casing retained for ASCII registry identifiers. */
    public static boolean matches(String text, String query) {
        String needle = query.strip();
        if (text.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT))) return true;
        String language = Minecraft.getInstance().getLanguageManager().getSelected();
        if (!language.equals(searchLanguage)) {
            searchLanguage = language;
            searchLocale = Locale.forLanguageTag(language.replace('_', '-'));
        }
        return text.toLowerCase(searchLocale).contains(needle.toLowerCase(searchLocale));
    }
    public static String resolve(String text) {
        return Language.getInstance().has(text) ? Component.translatable(text).getString() : text;
    }
}
