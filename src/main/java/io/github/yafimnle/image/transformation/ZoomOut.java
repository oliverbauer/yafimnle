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

@Log4j2
public class ZoomOut implements Transformation {
    @Override
    public File fromImageToVideo(File input, File output, int seconds, String destinationDir) {
        var scale = 8000;
        var framerate = Config.instance().ffmpeg().framerate();
        var frames = seconds * framerate;
        var dimension = Config.instance().resolution().dimension();

        // TODO Reuse ZoomPan from outlinev2?
        String scaleFlags = Config.instance().ffmpeg().scaleFlags();
        var filterComplex = "[0:v]scale=" + scale + ":-1"+scaleFlags+",zoompan=z='if(lte(zoom,1.0),1.2,max(1.001,zoom-0.001))':x=iw/2-(iw/zoom/2):y=ih/2-(ih/zoom/2):d=" + frames + ":s=" + dimension + ":fps=25[v];[1:a]atrim=0:5[a]";

        if (!output.getName().endsWith("mp4")) {
            String current = output.toString();
            current = current.substring(0, current.length() - 4) + ".mp4";
            output = new File(current);
        }

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
