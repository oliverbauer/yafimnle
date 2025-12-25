package io.github.yafimnle.image.transformation;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.ffmpeg.VideoTransformation;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.Logs;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.time.Instant;

/**
 * Transforms an image to a video in correct resolution. The video will contain "silent" audio which is necessary
 * for joining many final videos.
 *
 * If your image has already correct size use FastNone which is faster.
 */
@Log4j2
public class None implements Transformation {
    @Override
    public File fromImageToVideo(File input, File output, int seconds, String destinationDir) {
        var dimension = Config.instance().resolution().dimension();

        var filterComplex = "[0:v]scale=" + dimension + "[v];[1:a]atrim=0:5[a]";
        var command = new VideoTransformation().transformImage(input, output, filterComplex, seconds);

        var start = Instant.now();
        CLI.exec(command, this);
        if (log.isInfoEnabled()) {
            log.info("{} {} (created) (length {}s, enc-time {},  directory {})",
                    Logs.yellow("finished builder"),
                    output.getName(),
                    seconds,
                    Logs.time(start),
                    output.getParent()
            );
        }

        return output;
    }

    @Override
    public File fromVideoToVideo(File input, File output, int seconds, String destinationDir) {
        throw new UnsupportedOperationException();
    }
}
