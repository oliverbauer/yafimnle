package io.github.yafimnle.image.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtendFlipFlopEqual implements AbstractAROptions {
    int blur;

    public ExtendFlipFlopEqual(int blur) {
        this.blur = blur;
    }

    /* Suppose:
     *
     *       iw-missing
     *            |
     *    ----------
     * ih |IMAGE | |
     *    ----------
     *       iw
     *
     * Image with:
     * iw/ih = 4/3 or 16/9 or 3/2 does not matter
     *
     * There may be some missing width to get a 16/9 image.
     * What is missing?
     *   estimaged_width_for_16_to_9 * ih = 16 / 9 -> estimaged_width_for_16_to_9 = 16 * ih / 9
     * So missing is
     *   iw - estimaged_width_for_16_to_9
     *
     * This appends iw/2 to the left and to the right of the image.
     */
    @Override
    public String command(File i, File o) {
        /*
        5184x3888 = 4/3
        Xx3888 = 16/9 -> 3888 = 16/9x -> x = 16*3888 / 9 = 6912        so what is missing: 6912 - 5184 = 1728 also 864 wenn beide Seiten

        convert input2.jpg \( -clone 0 -crop 1728x3888+0+0 -blur 0x20 +flop +repage \) -reverse +append +repage -geometry x1080 result-2.jpg
        convert input2.jpg \( -clone 0 -crop 864x3888+0+0 -blur 0x20 +flop +repage \) -reverse +append +repage \( -clone 0 +flop -crop 864x3888+0+0 -blur 0x20 +repage \) +append +repage -geometry x1080 result-2.jpg
         */
        Resolution dimension = FFProbe.instance().resolution(i);
        int iw = dimension.width();
        int ih = dimension.height();

        int missingWidth = (16*ih/9) - iw;

        String crop2 = missingWidth/2+"x"+ih+"+0+0";

        StringBuilder stringBuilder = new StringBuilder(Config.instance().magick().command())
                .append(" ");
        stringBuilder.append(Config.instance().magick().threads());
        stringBuilder.append(" ");
        stringBuilder.append(FileUtils.escapeWhitespaces(i));
        stringBuilder.append(" ");
        if (missingWidth != 0) {
            stringBuilder.append("\\( -clone 0 -crop ").append(crop2).append(" -blur 0x").append(blur).append(" -flop +repage \\) -reverse +append +repage ");
            stringBuilder.append("\\( -clone 0 -flop -crop ").append(crop2).append(" -blur 0x").append(blur).append(" +repage \\) +append +repage ");
        }
        stringBuilder.append(" -quality 100% ");
        stringBuilder.append(FileUtils.escapeWhitespaces(o));

        return stringBuilder.toString();
    }
}
