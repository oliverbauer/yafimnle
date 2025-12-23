package io.github.yafimnle.image.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtendByColor implements AbstractAROptions {
    private String color = "Black";
    private boolean enforceCorrectResolution = false;

    public ExtendByColor color(String color) {
        this.color = color;
        return this;
    }

    @Override
    public String command(File i, File o) {
        // TODO Improve no resize!
        // /home/oliver/ffmpeg-video-gen/yafimnle-binaries/magick-7.1.1-43-Q16-HDRI-x86_64 496.JPG -gravity Center -background none -extent '16:9#' 496.JPG-extend-169.jpg

        String input = FileUtils.escapeWhitespaces(i);
        String output = FileUtils.escapeWhitespaces(o);

        Config config = Config.instance();

        String cmd = config.magick().command();
        String threads = config.magick().threads();
        String ar = config.resolution().ar();
        String dim = config.resolution().dimension();

        return cmd +
                " " +
                threads +
                " " +
                input +
                " " +
                "-resize x" + // no space!
                Config.instance().resolution().height() +
                " " +
                " -quality 100% -background " +
                color +
                " " +
                "-compose Copy -gravity Center -extent " +
                Config.instance().resolution().dimension() +
                "+0+0 " +
                output;
    }
}
