package io.github.yafimnle.image.filter;

import io.github.yafimnle.image.filter.imagefilter.*;

public class ImageFilters {
    private ImageFilters() {
        // Singleton
    }

    /**
     * Nikon NEF RAW Format
     * @return
     */
    public static ExtractJpgWithExiftool fromNEF() {
        return new ExtractJpgWithExiftool();
    }

    // TODO Add transform for Sony RAW Format?
    // TODO Add transform for HEIF-Format?

    public static PartialBlur partialBlur(int x, int y, int with, int height, int blur) {
        // 50x75+965+875
        // xsize:ysize+xpos+xpos
        return new PartialBlur(x, y, with, height, blur);
    }

    public static Implode implode(int x, int y, int with, int height, double factor) {
        // 50x75+965+875
        // xsize:ysize+xpos+xpos
        return new Implode(x, y, with, height, factor);
    }

    public static Paint paint(int x, int y, int with, int height, int factor) {
        // 50x75+965+875
        // xsize:ysize+xpos+xpos
        return new Paint(x, y, with, height, factor);
    }

    /**
     * transform the image to black and white
     *
     * @return
     */
    public static Monocrome monocrome() {
        return new Monocrome();
    }

    public static Colorspace colorspace() {
        return new Colorspace();
    }
}
