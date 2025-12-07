package io.github.yafimnle.config;

import io.github.yafimnle.image.ar.AR;
import io.github.yafimnle.image.ar.AbstractAROptions;
import io.github.yafimnle.image.enums.Gravity;

// Note: Do not use lombok here in this class unless change from instance to "this"
public class MagickConfig {
    private static MagickConfig instance;

    // Source: https://imagemagick.org/archive/binaries/magick On my Ubuntu it is "convert" by default installtion, but it is an old version
    // TODO this shouldn't be default!
    private String command = "/home/oliver/ffmpeg-video-gen/yafimnle-binaries/magick-7.1.1-43-Q16-HDRI-x86_64";
    private String threads = "-limit thread 1";
    private AbstractAROptions defaultImageAspectRatio = AR.crop(Gravity.CENTER);

    // Konstructor
    private MagickConfig() {
        // private
    }

    public static MagickConfig magick() {
        if (instance == null) {
            instance = new MagickConfig();
        }
        return instance;
    }

    public String command() {
        return instance.command;
    }

    public MagickConfig command(String command) {
        instance.command = command;
        return instance;
    }


    public String threads() {
        return instance.threads;
    }

    public MagickConfig threads(int num) {
        instance.threads = "-limit thread "+num;
        return instance;
    }

    public AbstractAROptions defaultImageAspectRatio() {
        return instance.defaultImageAspectRatio;
    }
}
