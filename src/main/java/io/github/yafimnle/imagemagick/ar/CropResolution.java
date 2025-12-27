package io.github.yafimnle.imagemagick.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.imagemagick.enums.Gravity;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class CropResolution implements AbstractAROptions {
    private final Gravity gravity;

    public CropResolution(Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public String command(File i, File o) {
        return Config.instance().magick().command()+" "+FileUtils.escapeWhitespaces(i)+" -gravity " + gravity + " -crop "+Config.instance().resolution().width()+"x" + Config.instance().resolution().height() + "+0+0 " + " " + FileUtils.escapeWhitespaces(o);
    }
}
