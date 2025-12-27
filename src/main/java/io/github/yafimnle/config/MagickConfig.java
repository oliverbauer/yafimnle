package io.github.yafimnle.config;

import io.github.yafimnle.imagemagick.ar.AR;
import io.github.yafimnle.imagemagick.ar.AbstractAROptions;
import io.github.yafimnle.imagemagick.enums.Gravity;
import io.github.yafimnle.imagemagick.filter.imagefilter.ImageFilter;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// Note: Do not use lombok here in this class unless change from instance to "this"
@Builder
@Getter
public class MagickConfig {
    // Source: https://imagemagick.org/archive/binaries/magick On my Ubuntu it is "convert" by default installation, but it is an old version
    @Builder.Default
    private String command = "/home/oliver/imagemagick-source/ImageMagick/utilities/magick";
    @Builder.Default
    private String threads = "-limit thread 1";
    @Builder.Default
    private AbstractAROptions defaultImageAspectRatio = AR.crop(Gravity.CENTER, false);
    /**
     * For example: You could append ImagesFilters.waifu2xUpscale
     */
    @Builder.Default
    private List<ImageFilter> preprocessFilters = new ArrayList<>();
}
