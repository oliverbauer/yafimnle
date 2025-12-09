package io.github.yafimnle.transformation.image;

import io.github.yafimnle.config.Config;
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
        var framerate = Config.instance().ffmpeg().framerate();
        var configThreads = Config.instance().ffmpeg().threads();
        var loglevel = Config.instance().ffmpeg().loggingConfig();
        var codec = Config.instance().ffmpeg().codec();

        if (!output.getName().endsWith("mp4")) {
            String current = output.toString();
            current = current.substring(0, current.length() - 4) + ".mp4";
            output = new File(current);
        }

        var formatOutput = " \\\n\t "; // results in a command better readable
        //formatOutput = " "; // results in a command with one line
        var command = new StringBuilder()
                .append(Config.instance().ffmpeg().command())
                .append(" ").append(loglevel).append(" ").append(configThreads)                                       // ffmpeg
                .append(formatOutput).append("-loop 1 -framerate ").append(framerate).append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(input)) // input image
                .append(" -f lavfi -i anullsrc ")
                .append(formatOutput).append("-acodec aac -vcodec ").append(codec).append(" -t ").append(seconds)                // audio and video definition
                .append(formatOutput).append("-y ")                                                                                                // do not override if "output" already exists
                .append(" -pix_fmt yuv420p ")
                .append(Config.instance().ffmpeg().encoderOptions())                                                  // video definition
                .append(" ").append(FileUtils.escapeWhitespaces(output)).toString();                                                                        // result

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
