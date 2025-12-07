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
public class LeftToRight implements Transformation {
    @Override
    public File fromImageToVideo(File input, File output, int seconds, String destinationDir) {
        var codec = Config.instance().ffmpeg().codec();
        // TODO hard coded crf and dimension!
        // TODO check ",gblur=sigma=0.5,minterpolate='mi_mode=mci:mc_mode=aobmc:vsbmc=1:fps=50" after y=0 but this reduces encoding time significantly!
        var command = "ffmpeg -loop 1 -i "+ FileUtils.escapeWhitespaces(input)+" -t "+seconds+" -vf \"crop=3840:2160: x='(iw - 3840) * t / "+seconds+"':y=0\" -c:v "+codec+" -crf 23 -preset medium -pix_fmt yuv420p "+FileUtils.escapeWhitespaces(output);

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
