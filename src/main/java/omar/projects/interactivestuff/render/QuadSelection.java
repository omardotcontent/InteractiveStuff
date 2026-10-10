package omar.projects.interactivestuff.render;

/** Half-open range; an end of zero means the full layer. */
public record QuadSelection(int start, int end) {
    public static QuadSelection resolve(int start, int end, int size) {
        int boundedEnd = end == 0 ? size : Math.clamp(end, 0, size);
        return new QuadSelection(Math.min(Math.clamp(start, 0, size), boundedEnd), boundedEnd);
    }
}
