package omar.projects.interactivestuff.render;

public final class ColorMath {
    private ColorMath() {}

    public static int multiply(int first, int second) {
        int a = ((first >>> 24) & 255) * ((second >>> 24) & 255) / 255;
        int r = ((first >>> 16) & 255) * ((second >>> 16) & 255) / 255;
        int g = ((first >>> 8) & 255) * ((second >>> 8) & 255) / 255;
        int b = (first & 255) * (second & 255) / 255;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
