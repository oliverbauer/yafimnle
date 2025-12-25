package io.github.yafimnle.image.transformation;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.ffmpeg.VideoTransformation;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.utils.CLI;
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

        // TODO Reuse ZoomPan from outlinev2?
        String scaleFlags = Config.instance().ffmpeg().scaleFlags();
        var filterComplex = "[0:v]scale=" + scale + ":-1"+scaleFlags+",zoompan=z='zoom+"+speed+"':s=" + dimension + ":x=iw/2-(iw/zoom/2):y=ih/2-(ih/zoom/2):d=" + frames + "[v];[1:a]atrim=0:5[a]";
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
