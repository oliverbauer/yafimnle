package io.github.yafimnle.image.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtendByColor implements AbstractAROptions {
    private String color = "Black";

    public ExtendByColor color(String color) {
        this.color = color;
        return this;
    }

    @Override
    public String command(File i, File o) {
        // TODO Improve no resize!
        // /home/oliver/ffmpeg-video-gen/yafimnle-binaries/magick-7.1.1-43-Q16-HDRI-x86_64 496.JPG -gravity Center -background none -extent '16:9#' 496.JPG-extend-169.jpg

        return Config.instance().magick().command() +
                " " +
                Config.instance().magick().threads() +
                " " +
                FileUtils.escapeWhitespaces(i) +
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
                FileUtils.escapeWhitespaces(o);
    }
}
