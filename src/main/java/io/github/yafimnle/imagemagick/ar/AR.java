package io.github.yafimnle.imagemagick.ar;

import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.imagemagick.enums.Gravity;

public class AR {
    private AR() {
        // Singleton
    }

    // CROP -> Rempve pixels, result will match aspect ratio (16:9)

    public static Crop crop(Gravity gravity, boolean enforceCorrectResultion) {
        return new Crop(gravity, enforceCorrectResultion);
    }

    public static Crop crop(Gravity gravity) {
        return new Crop(gravity);
    }

    public static CropResolution cropResolution(Gravity gravity) {
        return new CropResolution(gravity);
    }


    // EXTRACT -> Remove pixels, this time it can be fully configured "where" to extract part. Result is not only in
    //            aspect radio, it has although size defined by Config.instance().resolution.

    /**
     * Extracts a part of an image where width and height is defined by Resolution (LOW_QUALITY, FULL_HD or ULTRA_HD).
     *
     * @param startx number of pixels cropped from left
     * @param starty number of pixels cropped from top
     * @return command for imagemagick
     */
    public static ExtractResolution extractResolution(int startx, int starty) {
        return new ExtractResolution(startx, starty);
    }
    public static ExtractResolution extractResolution(Resolution resolution, int startx, int starty) {
        return extractResolution(resolution.width(), resolution.height(), startx, starty);
    }
    public static ExtractResolution extractResolution(int width, int height, int startx, int starty) {
        return new ExtractResolution(width, height, startx, starty);
    }

    // EXTEND Result will be larger (or equal) to given input. Result will have 16:9 aspect ratio. Note: Those are slower operations.

    public static ExtendByColor enlargeByColor() {
        return new ExtendByColor();
    }
    public static ExtendFlipFlopEqual appendFlipped(int blur) {
        return new ExtendFlipFlopEqual(blur);
    }
    public static ExtendFlipLeft appendFlippedLeft(int blur) { return new ExtendFlipLeft(blur); }
    public static ExtendFlipRight appendFlippedRight(int blur) { return new ExtendFlipRight(blur); }
    public static ExtendBlur blur(int factor) {
        return new ExtendBlur().blur(factor);
    }
    public static ExtendSuperzoom superzoom() {
        return new ExtendSuperzoom();
    }

}
