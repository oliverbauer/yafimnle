package io.github.yafimnle.imagemagick.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtendFlipLeft implements AbstractAROptions {
    int blur;

    public ExtendFlipLeft(int blur) {
        this.blur = blur;
    }

    @Override
    public String command(File i, File o) {
        Resolution dimension = FFProbe.instance().resolution(i);
        int iw = dimension.width();
        int ih = dimension.height();

        int missingWidth = (16*ih/9) - iw;

        StringBuilder stringBuilder = new StringBuilder(Config.instance().magick().command())
                .append(" ");
        stringBuilder.append(Config.instance().magick().threads());
        stringBuilder.append(" ");
        stringBuilder.append(FileUtils.escapeWhitespaces(i));
        stringBuilder.append(" ");
        if (missingWidth != 0) {
            stringBuilder.append("\\( -clone 0 -flop -blur 0x").append(blur).append(" \\) -reverse +append +repage ");
        }
        stringBuilder.append("-geometry x").append(Config.instance().resolution().height()).append(" +repage -crop ").append(Config.instance().resolution().dimension()).append("+").append(missingWidth / 2).append("+0").append(" -quality 100% ");
        stringBuilder.append(FileUtils.escapeWhitespaces(o));

        return stringBuilder.toString();
    }
}
