package io.github.yafimnle.image.ar;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.image.enums.Gravity;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Crop implements AbstractAROptions {
    private final Gravity gravity;
    private final boolean enforceCorrectResolution;

    public Crop(Gravity gravity) {
        this(gravity, false);
    }

    public Crop(Gravity gravity, boolean enforceCorrectResolution) {
        this.gravity = gravity;
        this.enforceCorrectResolution = enforceCorrectResolution;
    }

    @Override
    public String command(File i, File o) {
        String input = FileUtils.escapeWhitespaces(i);
        String output = FileUtils.escapeWhitespaces(o);

        Config config = Config.instance();

        String cmd = config.magick().command();
        String threads = config.magick().threads();
        String ar = config.resolution().ar();
        String dim = config.resolution().dimension();
        String grav = gravity.toString();
        if (!enforceCorrectResolution)
            return String.format("%s %s %s -gravity %s -crop %s -quality 100 %s", cmd, threads, input, grav, ar, output);
        else
            return String.format("%s %s %s -gravity %s -crop %s -resize %s -quality 100 %s", cmd, threads, input, grav, ar, dim, output);
    }
}
