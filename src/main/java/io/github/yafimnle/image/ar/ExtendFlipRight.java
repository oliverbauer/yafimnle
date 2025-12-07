package io.github.yafimnle.image.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtendFlipRight implements AbstractAROptions {
    int blur;

    public ExtendFlipRight(int blur) {
        this.blur = blur;
    }

    @Override
    public String command(File i, File o) {
        Resolution dimension = FFProbe.instance().resolution(i);
        int iw = dimension.width();
        int ih = dimension.height();

        int missingWidth = (16*ih/9) - iw;

        String crop2 = missingWidth+"x"+ih+"+0+0";

        StringBuilder stringBuilder = new StringBuilder(Config.instance().magick().command())
                .append(" ");
        stringBuilder.append(Config.instance().magick().threads());
        stringBuilder.append(" ");
        stringBuilder.append(FileUtils.escapeWhitespaces(i));
        stringBuilder.append(" ");
        if (missingWidth != 0) {
            stringBuilder.append("\\( -clone 0 -flop -crop ").append(crop2).append(" -blur 0x").append(blur).append(" \\) +append +repage ");
        }
        stringBuilder.append("-geometry x").append(Config.instance().resolution().height()).append(" -quality 100% ");
        stringBuilder.append(FileUtils.escapeWhitespaces(o));

        return stringBuilder.toString();
    }
}
