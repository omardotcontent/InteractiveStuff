package omar.projects.interactivestuff.render;

/** Half-open range; an end of zero means the full layer. */
public record QuadSelection(int start, int end) {
    public static QuadSelection resolve(int start, int end, int size) {
        int boundedEnd = end == 0 ? size : Math.max(0, Math.min(size, end));
        return new QuadSelection(Math.min(Math.max(0, Math.min(size, start)), boundedEnd), boundedEnd);
    }
}
