package io.github.yafimnle.imagemagick.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.exception.IllegalArgsException;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtractResolution implements AbstractAROptions {
    private final int x;
    private final int y;
    private Integer width = null;
    private Integer height = null;

    // TODO Allow some kind of Gravity: NORTHEAST = (0,0),...

    public ExtractResolution(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ExtractResolution(int width, int height, int x, int y) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String command(File i, File o) {
        if (this.width == null) {
            this.width = Config.instance().resolution().width();
        }
        if (this.height == null) {
            this.height = Config.instance().resolution().height();
        }
        Resolution dimension = FFProbe.instance().resolution(i);
        int dimWidth = dimension.width();
        int dimHeight = dimension.height();

        if (y + height > dimHeight) {
            throw new IllegalArgsException("Imageheight is "+dimHeight+" and target dimension dimHeight "+height+". You requested starting from "+y+". Max allowed: "+dimHeight+"-"+height+"="+(dimHeight-height));
        }
        if (x + width > dimWidth) {
            throw new IllegalArgsException("Imagewidth is "+dimWidth+" and target dimension dimWidth "+width+". You requested starting from "+x+". Max allowed: "+dimWidth+"-"+width+"="+(dimWidth-width));
        }

        return Config.instance().magick().command()+" -extract " + width + "x" + height + "+"+x+"+"+y+" " + FileUtils.escapeWhitespaces(i) + " " + FileUtils.escapeWhitespaces(o);
    }
}
