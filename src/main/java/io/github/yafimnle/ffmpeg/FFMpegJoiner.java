package io.github.yafimnle.ffmpeg;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.exception.IllegalArgsException;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class FFMpegJoiner {
    public File join(String outputscript, File... files) {
        return join(outputscript, Arrays.stream(files).toList());
    }

    public File join(String outputscript, List<File> files) {
        var destinationDir = Config.instance().destinationDir();
        var apprev = Config.instance().resolution().apprev();

        var sb = new StringBuilder();
        for (File file : files) {
            sb.append("file '").append(file.getAbsolutePath()).append("'\n");
        }
        // ffmpeg -f concat -i mylist.txt -c copy output.mp4

        var filename = destinationDir + File.separator + outputscript + "-files-" + apprev + ".txt";
        try {
            new File(filename).createNewFile();
        } catch (IOException e) {
            throw new IllegalArgsException(e);
        }
        FileUtils.writeStringBuilderToFile(sb, filename);

        var command = Config.instance().ffmpeg().command();
        if (new File(destinationDir + File.separator + outputscript+"-"+apprev+"-full.mp4").exists()) {
            log.warn("Output {} exists, skipping", destinationDir + "/" + outputscript + "-" + apprev + "-full.mp4");
        } else {
            CLI.exec(command + " -n -f concat -safe 0 -i " + filename + " -c:v copy -c:a libmp3lame " + destinationDir + "/" + outputscript + "-" + apprev + "-full.mp4", this);
        }

        return new File(destinationDir + File.separator + outputscript + "-" + apprev + "-full.mp4");
    }
}
