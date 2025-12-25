package io.github.yafimnle.image.transformation;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.ffmpeg.VideoTransformation;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import io.github.yafimnle.utils.Logs;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.time.Instant;

/**
 * Difference to None:
 *
 * No scale:
 *
 * ffmpeg -threads 1 \
 * 	 -loop 1 -framerate 25 -t 5 -i /tmp/input_2.jpg -f lavfi -i anullsrc  \
 * 	 -acodec aac -vcodec h264 -t 5 \
 * 	 -y -crf 23 /tmp/16_13_56.nef-1080p.mp4
 */
@Log4j2
public class FastNone implements Transformation {
    @Override
    public File fromImageToVideo(File input, File output, int seconds, String destinationDir) {
        var command = new VideoTransformation().transformImage(input, output, null, seconds);

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
