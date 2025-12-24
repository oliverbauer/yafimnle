package io.github.yafimnle.transformation.image;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import io.github.yafimnle.utils.Logs;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.time.Instant;

@Log4j2
public class ZoomIn implements Transformation {
    private double speed = 0.001;

    public ZoomIn speed(double speed) {
        this.speed = speed;
        return this;
    }

    @Override
    public File fromImageToVideo(File input, File output, int seconds, String destinationDir) {
        var scale = 8000;
        var framerate = Config.instance().ffmpeg().framerate();
        var frames = seconds * framerate;
        var dimension = Config.instance().resolution().dimension();
        var configThreads = Config.instance().ffmpeg().threads();
        var loggingConfig = Config.instance().ffmpeg().loggingConfig();
        var codec = Config.instance().ffmpeg().codec();

        // TODO Reuse ZoomPan from outlinev2?
        String scaleFlags = Config.instance().ffmpeg().scaleFlags();
        var filterComplex = "\"[0:v]scale=" + scale + ":-1"+scaleFlags+",zoompan=z='zoom+"+speed+"':s=" + dimension + ":x=iw/2-(iw/zoom/2):y=ih/2-(ih/zoom/2):d=" + frames + "[v];[1:a]atrim=0:5[a]\"";

        if (!output.getName().endsWith("mp4")) {
            String current = output.toString();
            current = current.substring(0, current.length() - 4) + ".mp4";
            output = new File(current);
        }

        var formatOutput = " \\\n\t "; // results in a command better readable
        //formatOutput = " "; // results in a command with one line
        var command = new StringBuilder()
                .append(Config.instance().ffmpeg().command())
                .append(" ").append(loggingConfig).append(" ").append(configThreads)                                       // ffmpeg
                .append(formatOutput).append("-loop 1 -framerate ").append(framerate).append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(input)) // input image

                // https://video.stackexchange.com/questions/34800/encode-still-image-video-effectively-in-ffmpeg
                //.append(formatOutput).append("-loop 1 -framerate 1 ").append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(input)) // input image

                .append(" -f lavfi -i anullsrc -c:a aac ")// input audio
                .append(formatOutput).append("-filter_complex ").append(filterComplex)                                                             // see above: zoom-in
                .append(formatOutput).append("-b:a 192k -ac 2 -ar 44100 -vcodec ").append(codec).append(" -map [v] -map [a] -t ").append(seconds)                                  // audio and video definition
                .append(formatOutput).append("-y ")                                                                                                // do not override if "output" already exists
                .append(formatOutput).append(configThreads)
                .append(formatOutput).append("-r ").append(framerate)
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
