package io.github.yafimnle.image.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtendBlur implements AbstractAROptions {
    int blurFactor = 20;

    public ExtendBlur blur(int blue) {
        this.blurFactor = blue;
        return this;
    }

    @Override
    public String command(File i, File o) {
        String input = FileUtils.escapeWhitespaces(i);

        Config config = Config.instance();

        String cmd = config.magick().command();
        String threads = config.magick().threads();

        Resolution resolution = FFProbe.instance().resolution(i);
        int newWidth = (resolution.height()*16)/9;
        String newWxH = newWidth+"x"+resolution.height();
        return new StringBuilder(cmd)
                .append(" ")
                .append(threads)
                .append(" ")
                .append(input)
                .append(" ").append("\\( +clone -scale 10% -blur 0x").append(blurFactor).append(" -resize ").append(newWxH).append("! \\) ")
                .append("+swap -gravity center -composite -quality 100% ")
                .append(FileUtils.escapeWhitespaces(o))
                .toString();
    }
}
