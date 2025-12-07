package io.github.yafimnle.image.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtendSuperzoom implements AbstractAROptions {
    @Override
    public String command(File i, File o) {
        // TODO only works for 1080p
        Resolution resolution = FFProbe.instance().resolution(i);
        int newWidth = (resolution.height()*16)/9;
        String newWxH = newWidth+"x"+resolution.height();

        return Config.instance().magick().command() +
                " " +
                Config.instance().magick().threads() +
                " " +
                FileUtils.escapeWhitespaces(i) +
                " " +
                "-crop 2x1@ -distort srt %[fx:t?0:180] " +
                "-append +repage -crop 8x1@ " +
                "+distort srt \"0,0 %[fx:1+(t*0.095)],1 0 0,0\" -shave 1 +append +repage " +
                //"-crop 1x2@ -distort srt %[fx:t?0:180] +append -resize 1920x1080! -quality 100% " +
                "-crop 1x2@ -distort srt %[fx:t?0:180] +append -resize "+newWxH+"! -quality 100% " +
                FileUtils.escapeWhitespaces(o);
    }
}
