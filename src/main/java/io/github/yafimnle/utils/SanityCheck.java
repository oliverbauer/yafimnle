package io.github.yafimnle.utils;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.exception.H264Exception;
import io.github.yafimnle.ffmpeg.FFProbe;

import java.io.File;

public class SanityCheck {
    private SanityCheck() {

    }

    public static void checkDimension(File file) {
        var config = Config.instance();
        if (!file.exists()) {
            throw new H264Exception("File does not exist: "+file);
        }
        if (!(FFProbe.instance().resolution(file)).equals(config.resolution())) {
            throw new H264Exception("Expected output to be "+ config.resolution().dimension()+" but was "+FFProbe.instance().resolution(file));
        }
    }
}
