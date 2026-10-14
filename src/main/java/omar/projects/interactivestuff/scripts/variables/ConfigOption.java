package omar.projects.interactivestuff.scripts.variables;

import me.abdelaziz.api.annotation.VynType;
import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import omar.projects.interactivestuff.config.PackOptions;
import omar.projects.interactivestuff.scripts.ScriptExecutionContext;
import omar.projects.interactivestuff.scripts.objects.Script;
import java.util.List;
import java.util.Locale;

/** Pack-owned schema. Values belong to the player, never to the declaration object. */
@VynType(name = "ConfigOption")
public final class ConfigOption {
    public final String id, type;
    public final Object defaultValue;
    public final Script owner;
    public final long generation;
    public String label, tooltip = "", category = "interactive_stuff.config.menu.category.general", onChange = "";
    public double minimum = 0, maximum = 1, step = 0.01;
    public int maxLength = 256;
    public List<String> choices = List.of();

    @VynConstructor public ConfigOption(String id, String type, Object defaultValue) {
        owner = ScriptExecutionContext.current();
        if (owner == null || owner.getState() != Script.State.LOADING)
            throw new IllegalStateException("Declare ConfigOption at the top level of a Vyn script");
        if (!id.matches("[a-zA-Z0-9_.-]{1,96}")) throw new IllegalArgumentException("Option ID requires letters, digits, dots, hyphens or underscores (1–96)");
        this.id = id;
        this.type = type.toLowerCase(Locale.ROOT);
        if (!List.of("toggle", "number", "dropdown", "color", "text").contains(this.type))
            throw new IllegalArgumentException("Unknown option type: " + type);
        this.defaultValue = defaultValue;
        label = id;
        generation = owner.getGeneration();
        PackOptions.register(this);
    }

    private void declaring() {
        if (owner.getGeneration() != generation || owner.getState() != Script.State.LOADING)
            throw new IllegalStateException("ConfigOption setters are only available during script loading");
    }
    private static String bounded(String value, int size) {
        if (value == null || value.length() > size) throw new IllegalArgumentException("Option text exceeds " + size + " characters");
        return value;
    }
    @VynFunc public void setLabel(String value) { declaring(); label = bounded(value, 160); }
    @VynFunc public void setTooltip(String value) { declaring(); tooltip = bounded(value, 1024); }
    @VynFunc public void setCategory(String value) { declaring(); category = bounded(value, 96); }
    @VynFunc public void setOnChange(String task) { declaring(); onChange = bounded(task, 160); }
    @VynFunc public void setRange(double min, double max, double increment) {
        declaring();
        if (!Double.isFinite(min) || !Double.isFinite(max) || !Double.isFinite(increment) || !Double.isFinite(max - min) || min >= max || increment <= 0 || increment > max - min)
            throw new IllegalArgumentException("Range requires finite min < max and 0 < step <= max - min");
        minimum = min; maximum = max; step = increment;
    }
    @VynFunc public void setChoices(List<String> values) {
        declaring();
        if (values.isEmpty() || values.size() > 64) throw new IllegalArgumentException("Dropdown requires 1–64 choices");
        choices = values.stream().map(value -> bounded(value, 160)).distinct().toList();
    }
    @VynFunc public void setMaxLength(int length) {
        declaring(); if (length < 1 || length > 4096) throw new IllegalArgumentException("Text length requires 1–4096"); maxLength = length;
    }
    @VynFunc public Object getValue() { return PackOptions.value(this); }
    @VynFunc public boolean getBoolean() { return (Boolean) getValue(); }
    @VynFunc public double getNumber() { return ((Number) getValue()).doubleValue(); }
    @VynFunc public String getText() { return (String) getValue(); }
    @VynFunc public String getColor() { return (String) getValue(); }
    @VynFunc public int getRed() { return Integer.parseInt(getColor().substring(1, 3), 16); }
    @VynFunc public int getGreen() { return Integer.parseInt(getColor().substring(3, 5), 16); }
    @VynFunc public int getBlue() { return Integer.parseInt(getColor().substring(5, 7), 16); }
    public boolean active() {
        return owner.getGeneration() == generation && (owner.getState() == Script.State.LOADED || owner.getState() == Script.State.IMPORTABLE);
    }
    public Object normalize(Object value) {
        return switch (type) {
            case "toggle" -> { if (!(value instanceof Boolean)) throw new IllegalArgumentException("Toggle requires true or false"); yield value; }
            case "number" -> {
                if (!(value instanceof Number number) || !Double.isFinite(number.doubleValue())) throw new IllegalArgumentException("Number requires a finite value");
                double snapped = minimum + Math.round((number.doubleValue() - minimum) / step) * step;
                yield Math.max(minimum, Math.min(maximum, snapped));
            }
            case "dropdown" -> {
                if (!(value instanceof String text) || !choices.contains(text)) throw new IllegalArgumentException("Choose one of " + choices);
                yield text;
            }
            case "color" -> {
                if (!(value instanceof String text) || !text.matches("#[a-fA-F0-9]{6}")) throw new IllegalArgumentException("Color requires #RRGGBB");
                yield text.toUpperCase(Locale.ROOT);
            }
            case "text" -> {
                if (!(value instanceof String text) || text.length() > maxLength) throw new IllegalArgumentException("Text exceeds " + maxLength + " characters");
                yield text;
            }
            default -> throw new IllegalArgumentException(type);
        };
    }
    public List<Object> schema() { return List.of(type, normalize(defaultValue), label, tooltip, category, minimum, maximum, step, maxLength, choices); }
}
