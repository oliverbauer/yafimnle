package io.github.yafimnle.image;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.image.ar.AbstractAROptions;
import io.github.yafimnle.utils.CLI;
import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
public class ImageCropper {
    public void crop(File input, File output, AbstractAROptions ar) {
        // scale down or crop or super zoom (go pro style) or enlarge blur... based on arOptions, if not set fallback to default
        var usedAR = ar;
        if (usedAR == null) {
            usedAR = Config.instance().magick().defaultImageAspectRatio();
        }

        CLI.exec(usedAR.command(input, output), this);
    }
}
